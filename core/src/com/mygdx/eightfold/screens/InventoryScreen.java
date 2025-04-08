package com.mygdx.eightfold.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.eightfold.GameAssets;

public class InventoryScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private GameAssets gameAssets;
    private Stage stage;
    private Skin skin;
    private Game game;

    public InventoryScreen(OrthographicCamera camera, IsometricGameScreen gameScreen, GameAssets gameAssets, Skin skin) {
        this.camera = camera;
        this.gameAssets = gameAssets;
        this.skin = skin;  // Ensure the skin is passed and assigned
        this.game = (Game) Gdx.app.getApplicationListener();

        // Initialize Scene2D Stage
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Set input processor to the stage

        // Create UI elements
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Add inventory items to the table (example)
      Label item1 = new Label("Gun", skin);
      Label item2 = new Label("Apple", skin);
       table.add(item1).padBottom(20).row();
       table.add(item2).padBottom(20).row();

        // Add table to the stage
        stage.addActor(table);
    }

    @Override
    public void show() {
        // Initialization code, if needed
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void hide() {
        // Handle when this screen is hidden
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}

