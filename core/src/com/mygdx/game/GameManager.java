package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;

public class GameManager extends ApplicationAdapter {
	// possible optimization: store Element positions as longs composed of two ints
	// constants
	private static final float T_PROP = 0.75f; // proportion of game screen taken up by top layer
	public static final int X_RES = 400, Y_RES = 300;  // dimensions of game area
//	private static final float CONTROL_WIDTH = 400;
	private static final float CONTROL_HEIGHT = Y_RES / T_PROP - Y_RES;
	private static final int PARTS_PER_FLUID = 4;  // size of a fluid cell, in Particles
	private static final float MAX_PRESSURE = 2f;

	private boolean paused;

	// components
	private FluidManager2 fluidManager;
	private ElementManager elementManager;
	private RectDrawer shape;
	private PenManager penManager;
	private ButtonTable buttonTable;
	private Viewport gameViewport;
	private Stage controlStage;

	@Override
	public void create() {
		penManager = new PenManager(this, this::placeElement);
//		fluidManager = new FluidManager2(X_RES / PARTS_PER_FLUID, Y_RES / PARTS_PER_FLUID);
		fluidManager = new FluidManager2(X_RES, Y_RES);
		shape = new RectDrawer();
		elementManager = new ElementManager(X_RES, Y_RES);
		buttonTable = new ButtonTable(X_RES, CONTROL_HEIGHT);
		gameViewport = new LayeredFitViewport(X_RES, Y_RES, T_PROP, true, true);
		controlStage = new Stage(new LayeredFitViewport(X_RES, CONTROL_HEIGHT,
				1 - T_PROP, false));

		addButtons();
		controlStage.addActor(buttonTable.makeTable());
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(controlStage);
		multiplexer.addProcessor(penManager);
		Gdx.input.setInputProcessor(multiplexer);

		Gdx.gl.glClearColor(0.25f,0.25f,0.25f, 1);
	}

	// computes updates to game state not directly caused by events every frame
	private void update() {
		// place elements
		penManager.act();

		if(!paused) {
			fluidManager.step(1);

			// move particles
			elementManager.update(fluidManager.getVelocityMap());
		}
	}

	// updates graphics every frame
	private void draw() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// draw game area
		gameViewport.apply();
		BiIntConsumer drawRedRect = (x, y) -> shape.drawRect(x, y, 1, 1, Color.RED);
		shape.drawRect(0, 0, X_RES, Y_RES, Color.BLACK);
		// draw pressure and velocity of each cell of fluid manager first
//		fluidManager.applyPressureFn(((xPos, yPos, p) ->
//				shape.drawRect(xPos * PARTS_PER_FLUID, yPos * PARTS_PER_FLUID, PARTS_PER_FLUID,
//						PARTS_PER_FLUID, getPressureColor(p))));
//		fluidManager.applyVelocityFn(((xPos, yPos, vx, vy) ->
//				Coords.line(xPos * PARTS_PER_FLUID, yPos * PARTS_PER_FLUID,
//						xPos * PARTS_PER_FLUID + Math.round(vx) * PARTS_PER_FLUID,
//						yPos * PARTS_PER_FLUID + Math.round(vy) * PARTS_PER_FLUID, 0,
//				drawRedRect)));

		// draw elements
		elementManager.forEachElement(e -> e.draw(shape));

		penManager.draw(drawRedRect);

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

	private void placeElement(int x, int y) {
		elementManager.placeElement(x, y, penManager.getActiveElement());
	}


	private void addButtons() {
		// Element buttons
		elementManager.forEachPType(t -> {
			String elemName = t.getSimpleName();
			buttonTable.addTextButton(elemName, b -> {
				penManager.setActiveElement(t);
				penManager.setPenAction(this::placeElement);
			});
		});

		// pen size
		buttonTable.addTextButton("psize: " + penManager.getPenSize(), b -> {
			penManager.penSizeUp();
			b.setText("psize: " + penManager.getPenSize());
		}, b -> {
			penManager.penSizeDown();
			b.setText("psize: " + penManager.getPenSize());
		});

		// pen type
		buttonTable.addTextButton("pen: " + penManager.getPenType(), b -> {
			penManager.penTypeNext();
			b.setText("pen: " + penManager.getPenType());
		});

		// clear
		buttonTable.addTextButton("Clear", b -> penManager.setPenAction(elementManager::clearElement));

		// erase
		buttonTable.addTextButton("Erase", b -> penManager.setPenAction(elementManager::eraseElement));

		// reset
		buttonTable.addTextButton("Reset", b -> {
			// relies on each element storing its position, will probably have to change
			elementManager.reset();
		});

		// pause
		buttonTable.addTextButton(paused ? "Unpause" : "Pause", b -> {
			if(paused) {
				paused = false;
				b.setText("Pause");
			} else {
				paused = true;
				b.setText("Unpause");
			}
		});

		// step
		buttonTable.addTextButton("Step", b -> {
			if(paused) {

				elementManager.update(fluidManager.getVelocityMap());
			}
		});
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
