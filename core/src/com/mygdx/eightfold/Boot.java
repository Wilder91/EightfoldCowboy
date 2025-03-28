package com.mygdx.eightfold;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.eightfold.player.Player;

import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.screens.SaloonScreen;
import com.mygdx.eightfold.screens.ScreenInterface;
import objects.inanimate.Door;


public class Boot extends Game {

    public static Boot INSTANCE;
    private int widthScreen, heightScreen;
    private OrthographicCamera orthographicCamera;
    private GameAssets gameAssets;
    private ScreenInterface screenInterface;
    private Game game;
    private World world;
    private GameScreen gameScreen;
    private SaloonScreen saloonScreen;
    public Boot() {
        INSTANCE = this;

    }



    @Override
    public void create() {
        this.widthScreen = 800;
        this.heightScreen = 480;
        this.world = new World(new Vector2(0, 0), false);
        this.orthographicCamera = new OrthographicCamera();
        this.orthographicCamera.setToOrtho(false, widthScreen, heightScreen);
        this.orthographicCamera.zoom = .8f;
        this.gameAssets = new GameAssets();
        this.game = this;
        gameAssets.loadAssets();
        gameAssets.finishLoading();
        this.gameScreen = new GameScreen(orthographicCamera, screenInterface, gameAssets, this, "start");
        setScreen(gameScreen);
       // setScreen(new SaloonScreen(orthographicCamera, gameAssets, new GameScreen(orthographicCamera, screenInterface, gameAssets), world, screenInterface));

    }
    public GameScreen getGameScreen() {
        return gameScreen;
    }

    public SaloonScreen getSaloonScreen() {
        return saloonScreen;
    }


    public void switchToSaloonScreen(Player player, Door door) {
        saloonScreen = new SaloonScreen(orthographicCamera, gameAssets, gameScreen,world, screenInterface, player, this);
        saloonScreen.setPlayer(player);
        setScreen(saloonScreen);
        saloonScreen.playerArrives();
    }

    public void switchToGameScreen(Player player, Door door) {
        GameScreen newGameScreen = new GameScreen(orthographicCamera, screenInterface, gameAssets, game, "saloon");
        setScreen(newGameScreen);
        gameScreen.setPlayer(player);
        gameScreen.playerArrives();
    }

    // Method to change the screen
    public void changeScreen(Screen newScreen) {
        setScreen(newScreen);
    }
}
