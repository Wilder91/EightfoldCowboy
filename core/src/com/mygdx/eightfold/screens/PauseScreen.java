package com.mygdx.eightfold.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import objects.GameAssets;

public class PauseScreen implements Screen {
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private BitmapFont font;
    private GameAssets gameAssets;
    private GameScreen gameScreen;
    private GlyphLayout layout;
    public PauseScreen(OrthographicCamera camera, GameAssets gameAssets, GameScreen gameScreen) {
        this.camera = camera;
        this.gameAssets = gameAssets;
        this.gameScreen = gameScreen;
        camera.setToOrtho(false, 1920, 1080); // Set your game width and height
        batch = new SpriteBatch();
        font = new BitmapFont();
         layout = new GlyphLayout();
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
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        layout.setText(font, "Paused");
        float pauseTextWidth = layout.width * 2;
        float pauseTextHeight = layout.height * 2;
        font.draw(batch, "Paused", (camera.viewportWidth - pauseTextWidth) / 2, (camera.viewportHeight + pauseTextHeight) / 2);

        // Center the "Press 'P' to Resume" text
        layout.setText(font, "Press 'P' to Resume");
        float resumeTextWidth = layout.width;
        float resumeTextHeight = layout.height;
        font.draw(batch, "Press 'P' to Resume", (camera.viewportWidth - resumeTextWidth) / 2, (camera.viewportHeight - resumeTextHeight) / 2 - 50); // Adjust -50 for spacing

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            // Resume the game
            ((Game) Gdx.app.getApplicationListener()).setScreen(gameScreen);
        }
    }

    @Override
    public void resize(int width, int height) {
        // Handle screen resizing
    }

    @Override
    public void pause() {
        // Handle the pause event
    }

    @Override
    public void resume() {
        // Handle the resume event
    }

    @Override
    public void hide() {
        // Handle when this screen is hidden
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}

