package com.mygdx.eightfold;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.mygdx.eightfold.GameScreen;

public class Boot extends Game {

    public static Boot INSTANCE;
    private int widthScreen, heightScreen;
    private OrthographicCamera orthographicCamera;

    public Boot() {
        INSTANCE = this;
    }

    @Override
    public void create() {

        this.widthScreen = Gdx.graphics.getWidth();
        this.heightScreen = Gdx.graphics.getHeight();
        this.orthographicCamera = new OrthographicCamera();
        this.orthographicCamera.setToOrtho(false, widthScreen, heightScreen);
        System.out.println("Before GameScreen instantiation");
        setScreen(new GameScreen(orthographicCamera));
        //System.out.println("After GameScreen instantiation");
    }
}
