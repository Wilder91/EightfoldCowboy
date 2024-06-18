package com.mygdx.eightfold;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.eightfold.screens.GameScreen;

public class Boot extends Game {

    public static Boot INSTANCE;
    private int widthScreen, heightScreen;
    private OrthographicCamera orthographicCamera;
    private GameAssets gameAssets;

    public Boot() {
        INSTANCE = this;
    }



    @Override
    public void create() {
        this.widthScreen = 800;
        this.heightScreen = 480;
        this.orthographicCamera = new OrthographicCamera();
        this.orthographicCamera.setToOrtho(false, widthScreen, heightScreen);
        this.gameAssets = new GameAssets();
        gameAssets.loadAssets();
        gameAssets.finishLoading();

        setScreen(new GameScreen(orthographicCamera, gameAssets));

    }

    // Method to change the screen
    public void changeScreen(Screen newScreen) {
        setScreen(newScreen);
    }
}
