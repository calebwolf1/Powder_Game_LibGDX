package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.mygdx.game.component.drawer.ElementDrawer;
import com.mygdx.game.component.drawer.FluidDrawer;
import com.mygdx.game.component.drawer.PenDrawer;
import com.mygdx.game.component.manager.ElementManager;
import com.mygdx.game.component.manager.FluidManager;
import com.mygdx.game.component.manager.PenManager;
import com.mygdx.game.utils.Projector;
import com.mygdx.game.utils.RectDrawer;
import com.mygdx.game.utils.ViewportManager;

public class GameManager extends ApplicationAdapter {
	// constants
	public static final int X_RES = 400, Y_RES = 300;  // dimensions of game area

	// components
	private ViewportManager viewportManager;

	private FluidManager fluidManager;
	private ElementManager elementManager;
	private PenManager penManager;

	private RectDrawer rectDrawer;

	private FluidDrawer fluidDrawer;
	private ElementDrawer elementDrawer;
	private PenDrawer penDrawer;

	private ButtonTable buttonTable;
	private Stage controlStage;

	// game state
	private boolean paused;

	@Override
	public void create() {
		rectDrawer = new RectDrawer();
		viewportManager = new ViewportManager(X_RES, Y_RES);

		fluidManager = new FluidManager(X_RES, Y_RES);
		elementManager = new ElementManager(X_RES, Y_RES);
		penManager = new PenManager(new Projector(viewportManager.getTop()));

		fluidDrawer = new FluidDrawer(rectDrawer);
		elementDrawer = new ElementDrawer(rectDrawer);
		penDrawer = new PenDrawer(rectDrawer);

		buttonTable = new ButtonTable(X_RES, viewportManager.getBottom().getWorldHeight());
		controlStage = new Stage(viewportManager.getBottom());

		addButtons();
		controlStage.addActor(buttonTable.makeTable());
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(controlStage);
		multiplexer.addProcessor(penManager);
		Gdx.input.setInputProcessor(multiplexer);

		Gdx.gl.glClearColor(0.25f,0.25f,0.25f, 1);
	}

	private void updateSim() {
//		fluidManager.step(1);

		// move particles
		elementManager.update(fluidManager);
	}

	// updates graphics every frame
	private void draw() {
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		// draw game area
		viewportManager.getTop().apply();
		rectDrawer.drawRect(0, 0, X_RES, Y_RES, Color.BLACK);

		// draw elements, pen, and fluid
		fluidDrawer.draw(fluidManager);
		elementDrawer.draw(elementManager);
		penDrawer.draw(penManager);

		// call after drawing every rect that needs to be drawn this frame
		rectDrawer.flush();

		// draw control area
		controlStage.getViewport().apply();
		controlStage.draw();
	}

	@Override
	public void render() {
		penManager.act();
		if(!paused) {
			updateSim();
		}
		draw();
	}

	@Override
	public void resize(int width, int height) {
		viewportManager.getTop().update(width, height, true);
		rectDrawer.setProjectionMatrix(viewportManager.getTop().getCamera().combined);
		controlStage.getViewport().update(width, height);
	}

	private void addButtons() {
		// pen buttons
		penManager.addPenButtons(buttonTable, elementManager::placeElement, elementManager::clearElement,
				elementManager::eraseElement);

		// reset
		buttonTable.addTextButton("Reset", b -> elementManager.reset());

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
				updateSim();
			}
		});
	}

	public static boolean boundsCheck(int x, int y) {
		return x >= 0 && x < X_RES && y >= 0 && y < Y_RES;
	}

	public static boolean boundsCheck(Vector2 pos) {
		return pos.x >= 0 && pos.x < X_RES && pos.y >= 0 && pos.y < Y_RES;
	}
}
