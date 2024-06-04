package com.mygdx.eightfold;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.eightfold.screens.StartScreen;

public class EightfoldCowboyGame implements ApplicationListener {
    private OrthographicCamera camera;
    private StartScreen startScreen;

    @Override
    public void create() {
        // Initialize the camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Initialize the start screen
        //startScreen = new StartScreen(camera, gameAssets);

        // Set the initial screen to the start screen
        setScreen(startScreen);
    }

    @Override
    public void resize(int width, int height) {
        // Handle resizing
        startScreen.resize(width, height);
    }

    @Override
    public void render() {
        // Clear the screen with a dark blue color
        Gdx.gl.glClearColor(0, 0, 0.2f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Update and render the current screen
        startScreen.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void pause() {
        // Handle pause
    }

    @Override
    public void resume() {
        // Handle resume
    }

    @Override
    public void dispose() {
        // Dispose of resources
        startScreen.dispose();
    }

    public void setScreen(StartScreen screen) {
        // Optionally implement a screen management system
        if (startScreen != null) {
            startScreen.dispose();
        }
        startScreen = screen;
    }
}
