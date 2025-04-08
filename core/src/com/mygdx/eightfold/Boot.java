package com.mygdx.eightfold;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.eightfold.player.IsometricPlayer;
import com.mygdx.eightfold.player.Player;

import com.mygdx.eightfold.screens.IsometricGameScreen;
import com.mygdx.eightfold.screens.IsometricSaloonScreen;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.IsometricCamera;
import helper.IsometricViewport;
import objects.inanimate.Door;


public class Boot extends Game {

    public static Boot INSTANCE;
    private int widthScreen, heightScreen;
    private OrthographicCamera orthographicCamera;
    private IsometricCamera camera;
    private IsometricViewport viewport;
    private GameAssets gameAssets;
    private ScreenInterface screenInterface;
    private Game game;
    private World world;
    private IsometricGameScreen gameScreen;
    private IsometricSaloonScreen saloonScreen;

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
        this.camera = new IsometricCamera(widthScreen, heightScreen);
        this.camera.setIsometricAngle(30f); // Classic isometric angle
        this.camera.setIsometricScale(0.75f); // Adjust as needed

        // Position the camera
        this.camera.position.set(widthScreen / 2f, heightScreen / 2f, 0);

        // Set a smaller zoom for isometric view (zooms out)
        this.camera.zoom = 0.5f;
        // Adjusting zoom for isometric view - a smaller value for zooming out
        this.orthographicCamera.zoom = 0.5f;
        this.viewport = new IsometricViewport(widthScreen, heightScreen, camera);
        this.viewport.update(widthScreen, heightScreen, true);

        this.gameAssets = new GameAssets();
        this.game = this;
        gameAssets.loadAssets();
        gameAssets.finishLoading();

        // Create IsometricGameScreen instead of GameScreen
        this.gameScreen = new IsometricGameScreen(camera, screenInterface, gameAssets, this, "start");
        setScreen(gameScreen);
    }

    public IsometricGameScreen getGameScreen() {
        return gameScreen;
    }

    public IsometricSaloonScreen getSaloonScreen() {
        return saloonScreen;
    }

    public void switchToSaloonScreen(IsometricPlayer player, Door door) {
        World newWorld = new World(new Vector2(0, 0), false);
        saloonScreen = new IsometricSaloonScreen(orthographicCamera, gameAssets, gameScreen, newWorld, screenInterface, player, this);
        saloonScreen.setPlayer(player);
        setScreen(saloonScreen);
        saloonScreen.playerArrives();
    }

    public void switchToGameScreen(IsometricPlayer player, Door door) {
        IsometricGameScreen newGameScreen = new IsometricGameScreen(orthographicCamera, screenInterface, gameAssets, game, "saloon");
        setScreen(newGameScreen);
        newGameScreen.setPlayer(player);
        newGameScreen.playerArrives();
    }

    // Method to change the screen
    public void changeScreen(Screen newScreen) {
        setScreen(newScreen);
    }

    // Helper method to get the current IsometricPlayer (regardless of which screen it's in)
    public IsometricPlayer getIsometricPlayer() {
        Screen currentScreen = getScreen();

        if (currentScreen instanceof IsometricGameScreen) {
            return (IsometricPlayer) ((IsometricGameScreen) currentScreen).getPlayer();
        } else if (currentScreen instanceof IsometricSaloonScreen) {
            return ((IsometricSaloonScreen) currentScreen).getPlayer();
        }

        return null;
    }
}