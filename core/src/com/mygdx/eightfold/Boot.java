package com.mygdx.eightfold;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.screens.StartScreen;
import objects.GameAssets;

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
        this.widthScreen = 1920;
        this.heightScreen = 1080;
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
