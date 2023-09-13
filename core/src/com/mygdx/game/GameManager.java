package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;
import com.badlogic.gdx.utils.viewport.Viewport;

import com.mygdx.game.element.Block;
import com.mygdx.game.element.Element;
import com.mygdx.game.element.Particle;
import com.mygdx.game.element.Powder;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;

public class GameManager extends ApplicationAdapter {
	// TODO: make Vector2 and int parameters consistent
	// constants
	private static final float T_PROP = 0.75f; // proportion of game screen taken up by top layer
	public static final int X_RES = 300, Y_RES = 200;  // dimensions of game area
	private static final float CONTROL_WIDTH = 300;
	private static final float CONTROL_HEIGHT = Y_RES / T_PROP - Y_RES;
	public static final Vector2 G = new Vector2(0, 0.7f);
	private static final int MAX_PARTICLES = 20_000;
	private static final Array<Class<? extends Element>> P_TYPES;
	private static final int BORDER_WIDTH = 2;
	private static final int PARTS_PER_FLUID = 4;  // size of a fluid cell, in Particles
	private static final float MAX_PRESSURE = 2f;

	// event-related variables
	private boolean placing;
	private int mouseX, mouseY;  // game coordinates
	private int mousePrevX, mousePrevY;  // mouse coords from previous interrupt
	private int lineStartX, lineStartY;
	private Class<? extends Element> activeElement = Powder.class;
	private int penSize = 2;
	private PenType penType = PenType.FREE;
	private Consumer<Vector2> penAction = this::placeElement;

	public enum PenType {FREE, LINE}

	// simulation data structures
	private ArrayMap<Element> elementMap;  // map of on-screen Elements in each game position
	private ArrayMap<Vector2> velocityMap;  // map of velocity vectors in each game position
	private ObjectSet<Element> elements; // set of Elements in the game
	private FluidManager2 fluidManager;
	// possible optimization: store Element positions as longs composed of two ints

	// rendering stuff
	private Viewport gameViewport;
	private Stage controlStage;
	private RectDrawer shape;

	static {
		// intialize P_TYPES with every concrete descendant of Element
		Reflections ref = new Reflections("com.mygdx.game");
		Set<Class<? extends Element>> set = ref.getSubTypesOf(Element.class);
		set.removeIf(c -> Modifier.isAbstract(c.getModifiers()));
		P_TYPES = new Array<>();
		for(Class<? extends Element> c : set) {
			P_TYPES.add(c);
		}
	}

	@Override
	public void create() {
		elementMap = new ArrayMap<>(X_RES, Y_RES);
		velocityMap = new ArrayMap<>(X_RES, Y_RES);
		elements = new ObjectSet<>(MAX_PARTICLES);
		fluidManager = new FluidManager2(X_RES / PARTS_PER_FLUID, Y_RES / PARTS_PER_FLUID);
		makeBorder();
//		Block block = new Block(new Vector2(50, 50));
//		elementMap.set(50, 50, block);
//		elements.add(block);

		gameViewport = new LayeredFitViewport(X_RES, Y_RES, T_PROP, true, true);
		controlStage = new Stage(new LayeredFitViewport(CONTROL_WIDTH, CONTROL_HEIGHT,
				1 - T_PROP, false));
		controlStage.addActor(makeButtonTable());
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(controlStage);
		multiplexer.addProcessor(new EventHandler(this));
		Gdx.input.setInputProcessor(multiplexer);

		shape = new RectDrawer();
		Gdx.gl.glClearColor(0.25f,0.25f,0.25f, 1);
	}

	// computes updates to game state not directly caused by events every frame
	private void update() {
		fluidManager.step(1);

		// place elements
		if(penType == PenType.FREE) {
			if(placing && (boundsCheck(mouseX, mouseY) || boundsCheck(mousePrevX, mousePrevY))) {
				Coords.line(mousePrevX, mousePrevY, mouseX, mouseY, penSize, penAction);
			}
		}

		// move particles
		Iterator<Element> it = elements.iterator();
		while(it.hasNext()) {
			Element e = it.next();
			if(e instanceof Particle) {
				Particle p = (Particle) e;
				if(!p.move(velocityMap, elementMap)) {
					it.remove();
				}
			}
		}
	}

	private void placeElement(Vector2 pos) {
		if(elements.size < MAX_PARTICLES) {
			if (boundsCheck(pos) && elementMap.get(pos) == null) {
				Element e = null;
				try {
					e = activeElement.getConstructor(Vector2.class).newInstance(pos);
				} catch (InstantiationException | NoSuchMethodException |
						IllegalAccessException | InvocationTargetException ex) {
					ex.printStackTrace();
					System.exit(-1);
				}
				elementMap.set(Coords.intX(pos), Coords.intY(pos), e);
				elements.add(e);
			}
		}
	}

	// remove the Element at the given position if it is within bounds and is not a Block
	private void clearElement(Vector2 pos) {
		if(boundsCheck(pos)) {
			Element e = elementMap.get(pos);
			if(e != null && !(e instanceof Block)) {
				elementMap.set(pos, null);
				elements.remove(e);
			}
		}
	}

	private void eraseElement(Vector2 pos) {
		if(boundsCheck(pos)) {
			Element e = elementMap.get(pos);
			if(e instanceof Block) {
				elementMap.set(pos, null);
				elements.remove(e);
			}
		}
	}

	// updates graphics every frame
	private void draw() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// draw game area
		gameViewport.apply();
		Consumer<Vector2> drawRedRect = p -> shape.drawRect(Math.round(p.x), Math.round(p.y), 1,
				1, Color.RED);
		shape.drawRect(0, 0, X_RES, Y_RES, Color.BLACK);
		// draw pressure and velocity of each cell of fluid manager first
		fluidManager.applyPressureFn(((xPos, yPos, p) ->
				shape.drawRect(xPos * PARTS_PER_FLUID, yPos * PARTS_PER_FLUID, PARTS_PER_FLUID,
						PARTS_PER_FLUID, getPressureColor(p))));
		fluidManager.applyVelocityFn(((xPos, yPos, vx, vy) ->
				Coords.line(xPos * PARTS_PER_FLUID, yPos * PARTS_PER_FLUID,
						xPos * PARTS_PER_FLUID + Math.round(vx) * PARTS_PER_FLUID,
						yPos * PARTS_PER_FLUID + Math.round(vy) * PARTS_PER_FLUID, 0,
				drawRedRect)));

		// draw elements
		for(Element e : elements) {
			e.draw(shape);
		}

		if(penType == PenType.LINE && placing) {
			Coords.line(lineStartX, lineStartY, mouseX, mouseY, 0, drawRedRect);
		}
		Coords.circle(new Vector2(mouseX, mouseY), penSize, false, drawRedRect);

		// call after drawing every rect that needs to be drawn this frame
		shape.flush();

		// draw control area
		controlStage.getViewport().apply();
		controlStage.draw();
	}

	private Color getPressureColor(float p) {
		if(p >= 0) {
			return new Color(0f, p / MAX_PRESSURE, 0f, 0f);
		}
		return null;
	}

	@Override
	public void render() {
		update();
		draw();
	}

	@Override
	public void resize(int width, int height) {
		gameViewport.update(width, height, true);
		shape.setProjectionMatrix(gameViewport.getCamera().combined);
		controlStage.getViewport().update(width, height);
	}

	private void makeBorder() {
		Consumer<Vector2> lineFn = p -> {
			Block block = new Block(p);
			elementMap.set(p, block);
			elements.add(block);
		};
		int i = 0;
		while(i < GameManager.BORDER_WIDTH) {
			int yLim = Y_RES - 1 - i;
			int xLim = X_RES - 1 - i;
			Coords.line(i, i, i, yLim, 0, lineFn);  // left
			Coords.line(xLim, i, xLim, yLim, 0, lineFn);  // right
			Coords.line(i, i, xLim, i, 0, lineFn);  // top
			Coords.line(i, yLim, xLim, yLim, 0, lineFn);  // bottom
			i++;
		}
	}

	private Table makeButtonTable() {
		final float FONT_SCALE = 0.75f;
		// set up skin for buttons
		Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
		skin.getAtlas().getTextures().iterator().next().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
		skin.getFont("default-font").getData().setScale(FONT_SCALE);
		skin.getFont("default-font").setUseIntegerPositions(false);
		Array<TextButton> buttons = createButtons(skin);

		// get the max width and height of the text in each button
		GlyphLayout glyphLayout = new GlyphLayout(skin.getFont("default-font"),
				buttons.get(0).getText());
		float height = glyphLayout.height;
		float maxWidth = 0;

		for(int i = 1; i < buttons.size; i++) {
			glyphLayout.setText(skin.getFont("default-font"), buttons.get(i).getText());
			if(glyphLayout.width > maxWidth) {
				maxWidth = glyphLayout.width;
			}
		}

		// table-related values
		final float textPad = 0.08f * maxWidth;
		final float buttonPad = 0.08f * (maxWidth + textPad);
		final float totalPad = 2 * (textPad + buttonPad);
		float buttonWidth = maxWidth + totalPad;
		float buttonHeight = height + totalPad;
		int numCols = (int) Math.floor(CONTROL_WIDTH / buttonWidth);
		int numRows = (int) Math.ceil((float) buttons.size / numCols);
		int maxNumRows = (int) Math.floor(CONTROL_HEIGHT / buttonHeight);
		if(numRows > maxNumRows) {
			throw new IllegalStateException("Too many buttons! Decrease the font scale.");
		}

		Table table = new Table();
		table.setFillParent(true);
		buttonWidth -= 2 * buttonPad;
		buttonHeight -= 2 * buttonPad;
		table.defaults()
				.width(buttonWidth)
				.height(buttonHeight)
				.pad(buttonPad);

		for(int i = 0; i < buttons.size; i++) {
			if(i % numCols == 0) table.row();
			table.add(buttons.get(i));
		}

		return table;
	}

	private Array<TextButton> createButtons(Skin skin) {
		Array<TextButton> buttons = new Array<>();

		// Element buttons
		for(int i = 0; i < P_TYPES.size; i++) {
			String elemName = P_TYPES.get(i).getSimpleName();
			final int finalI = i;
			addTextButton(elemName, skin, buttons, b -> {
				activeElement = P_TYPES.get(finalI);
				penAction = this::placeElement;
			});
		}

		// pen size
		addTextButton("psize: " + penSize, skin, buttons, b -> {
			penSize = penSize == 9 ? 0 : penSize + 1;
			b.setText("psize: " + penSize);
		}, b -> {
			penSize = penSize == 0 ? 9 : penSize - 1;
			b.setText("psize: " + penSize);
		});

		// pen type
		addTextButton("pen: " + penType.toString().toLowerCase(), skin, buttons, b -> {
			penType = penType == PenType.FREE ? PenType.LINE : PenType.FREE;
			b.setText("pen: " + penType.toString().toLowerCase());
		});

		// clear
		addTextButton("Clear", skin, buttons, b -> penAction = this::clearElement);

		// erase
		addTextButton("Erase", skin, buttons, b -> penAction = this::eraseElement);

		// reset
		addTextButton("Reset", skin, buttons, b -> {
			// relies on each element storing its position, will probably have to change
			for(Element e : elements) {
				elementMap.set(e.getPos(), null);
			}
			elements.clear();
			makeBorder();
		});

		return buttons;
	}

	private void addListener(TextButton button, int clickType, Consumer<TextButton> clickFn) {
		button.addListener(new ClickListener(clickType) {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				clickFn.accept(button);
			}
		});
	}

	private void addTextButton(String text, Skin skin, Array<TextButton> buttons,
							   Consumer<TextButton> leftClickFn) {
		TextButton button = new TextButton(text, skin);
		addListener(button, Input.Buttons.LEFT, leftClickFn);
		buttons.add(button);
	}

	private void addTextButton(String text, Skin skin, Array<TextButton> buttons,
							   Consumer<TextButton> leftClickFn,
							   Consumer<TextButton> rightClickFn) {
		TextButton button = new TextButton(text, skin);
		addListener(button, Input.Buttons.LEFT, leftClickFn);
		addListener(button, Input.Buttons.RIGHT, rightClickFn);
		buttons.add(button);
	}

	public void setPlacing(boolean newVal) {
		placing = newVal;
	}

	public void setMouse(int newX, int newY) {
		mouseX = newX;
		mouseY = newY;
	}

	public int getMouseX() {
		return mouseX;
	}

	public int getMouseY() {
		return mouseY;
	}

	public void setMousePrev(int newX, int newY) {
		mousePrevX = newX;
		mousePrevY = newY;
	}

	public void setLineStart(int newX, int newY) {
		lineStartX = newX;
		lineStartY = newY;
	}

	public int getLineStartX() {
		return lineStartX;
	}

	public int getLineStartY() {
		return lineStartY;
	}

	public PenType getPenType() {
		return penType;
	}

	public int getPenSize() {
		return penSize;
	}

	public Consumer<Vector2> getPenAction() {
		return penAction;
	}

	public Viewport getGameViewport() {
		return gameViewport;
	}

	public static boolean boundsCheck(int x, int y) {
		return x >= 0 && x < X_RES && y >= 0 && y < Y_RES;
	}

	public static boolean boundsCheck(Vector2 pos) {
		return pos.x >= 0 && pos.x < X_RES && pos.y >= 0 && pos.y < Y_RES;
	}
}
