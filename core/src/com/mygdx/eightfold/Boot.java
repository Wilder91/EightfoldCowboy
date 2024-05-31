package com.mygdx.eightfold;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.mygdx.eightfold.GameScreen;

public class Boot extends Game {

    public static Boot INSTANCE;
    private int widthScreen, heightScreen;
    private OrthographicCamera orthographicCamera;
    public AssetManager gameAssetManager;
    private GameAssets gameAssets;

    public Boot() {
        INSTANCE = this;
    }

    public AssetManager getGameAssetManager() {
        return gameAssetManager;
    }

    @Override
    public void create() {

        this.widthScreen = Gdx.graphics.getWidth();
        this.heightScreen = Gdx.graphics.getHeight();
        this.orthographicCamera = new OrthographicCamera();
        this.gameAssetManager = new AssetManager();
        this.gameAssets = new GameAssets();
        gameAssets.loadAssets();
        gameAssets.finishLoading();
        {

            this.orthographicCamera.setToOrtho(false, widthScreen, heightScreen);
            System.out.println("Before GameScreen instantiation");

            setScreen(new GameScreen(orthographicCamera));
            //System.out.println("After GameScreen instantiation");
        };
    }
}
