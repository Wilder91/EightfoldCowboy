package com.mygdx.eightfold.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.eightfold.Boot;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.GameAssets;
import helper.BodyUserData;
import helper.ContactType;
import helper.tiledmap.TiledMapHelper;
import com.mygdx.eightfold.player.Player;
import objects.animals.bird.Bird;
import objects.animals.bison.Bison;
import objects.inanimate.Boulder;
import objects.inanimate.Building;
import objects.inanimate.Door;
import objects.inanimate.Tree;
import text.infobox.InfoBox;
import text.textbox.SaloonTextBox;
import text.textbox.TextBox;

import java.util.ArrayList;

import static helper.Constants.PPM;

public class SaloonScreen extends ScreenAdapter implements ScreenInterface {
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final GameScreen gameScreen;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private final TiledMapHelper tiledMapHelper;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private Player player;
    private Boolean gameTime = false;
    private boolean playerPositionInitialized = false;
    private final ScreenInterface screenInterface;
    private final GameContactListener gameContactListener;
    private final GameAssets gameAssets;
    private final Music music;
    private final ArrayList<Door> doorList;
    private Game game;
    // TextBox and InfoBox
    private SaloonTextBox textBox;
    private InfoBox infoBox;

    public SaloonScreen(OrthographicCamera camera, GameAssets gameAssets,  GameScreen gameScreen, World world, ScreenInterface screenInterface, Player player, Game game) {
        this.camera = camera;
        //camera.zoom = 3f;
        this.music = gameAssets.getMusic("ethereal.mp3");
        this.screenInterface = screenInterface;
        this.batch = new SpriteBatch();
        this.world = world;
        this.game = game;
        this.gameAssets = gameAssets;
        this.gameScreen = gameScreen;
        this.doorList = new ArrayList<>();
        this.gameContactListener = new GameContactListener(this);
        this.world.setContactListener(this.gameContactListener);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.tiledMapHelper = new TiledMapHelper(this, gameAssets, gameContactListener);
        this.orthogonalTiledMapRenderer = tiledMapHelper.setupMap("maps/InsideMap.tmx");

        this.player = Boot.INSTANCE.getGameScreen().getPlayer();

        player.createBody(world);


        if (player == null) {

            System.out.println("Player is null!");
        }

        // Initialize TextBox and InfoBox
        this.textBox = new SaloonTextBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")), "animals/bison/bison-single.png");
        this.infoBox = new InfoBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")));
        Gdx.input.setInputProcessor(textBox.getStage());
        Gdx.input.setInputProcessor(infoBox.getStage());
    }

    public void showTextBox(String text) {
        textBox.showTextBox(text);
    }

    @Override
    public OrthographicCamera getCamera() {
        return camera;
    }

    @Override
    public void setTextBox(String filepath) {

    }



    public void hideTextBox() {
        textBox.hideTextBox();
    }



    public void showInfoBox(String text) {
        infoBox.showInfoBox(text);
    }

    public void hideInfoBox() {
        infoBox.hideInfoBox();
    }

    @Override
    public void setSaloonTime(boolean saloonTime) {

    }

    @Override
    public void setGameTime(boolean saloonTime) {

        gameScreen.setSaloonTime(!saloonTime);
    }

    @Override
    public boolean isSaloonTime() {
        return false;
    }

    @Override
    public void transitionToScreen(ScreenInterface newScreen) {
        ((Game) Gdx.app.getApplicationListener()).setScreen((ScreenAdapter) newScreen);
        updateDoorScreenReferences(newScreen);
    }



    private void update(float delta) {
        world.step(1 / 60f, 6, 2);  // Step the physics world
        cameraUpdate();  // Update the camera

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            enterPauseScreen();
        }

        if (player != null) {

            player.update(delta);  // Update the player
        }

        for (Door door : doorList) {

            door.update(delta);

        }

        if (player != null) {
            if (!playerPositionInitialized) {
                player.getBody().setTransform(15, 2, 0); // Set the initial position
                playerPositionInitialized = true; // Set the flag to true to avoid resetting the position
            }

            player.update(delta);  // Update the player
        }


    }

    public void enterPauseScreen() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameAssets, gameScreen));
    }

    private void cameraUpdate() {
        if (player != null) {
            Vector3 position = camera.position;
            position.x = Math.round(player.getBody().getPosition().x * PPM * 10) / 10f;
            position.y = Math.round(player.getBody().getPosition().y * PPM * 10) / 10f;
            camera.position.set(position);
            camera.update();
        }
    }

    public void addDoor(Door door) {
        System.out.println("HELLLLO");
        if (doorList != null) {
            doorList.add(door);
            System.out.println(door);
        } else {
            System.err.println("doorList is null. Cannot add door.");
        }
    }



    private void updateDoorScreenReferences(ScreenInterface newScreen) {
        for (Door door : doorList) {
            door.setScreen(newScreen);
        }
    }

    @Override
    public void render(float delta) {
        update(delta); // Pass delta time to update method

        Gdx.gl.glClearColor(78f / 255f, 87f / 255f, 92f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthogonalTiledMapRenderer.setView(camera);
        orthogonalTiledMapRenderer.render();
        batch.begin();

        // Render game objects
        if (player != null) {
            player.render(batch);
            //player.createBody(world);
            //dSystem.out.println("Player Position: " + player.getSprite().getX());
        }
        for (Door door : doorList) {
            door.render(batch);
        }
        music.setVolume(.1f);
        //music.play();

        batch.end();

        // Render the Stage
        textBox.getStage().act(delta);
        textBox.getStage().draw();
        infoBox.getStage().act(delta);
        infoBox.getStage().draw();

        // Uncomment for debugging physics bodies
        box2DDebugRenderer.render(world, camera.combined.scl(PPM));
    }

    @Override
    public void dispose() {
        // Dispose of assets properly
        batch.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
        orthogonalTiledMapRenderer.dispose();
        textBox.getStage().dispose();
        textBox.getSkin().dispose();
        infoBox.getStage().dispose();
        infoBox.getSkin().dispose();
    }

    public void setPlayer(Player player) {
        this.player = player;
        if (player != null && player.getBody() == null) {
            player.createBody(world); // Create a new body in the new world
        }
    }



    @Override
    public void addBison(Bison bison) {

    }

    @Override
    public void addTree(Tree tree) {

    }

    @Override
    public void addBird(Bird bird) {

    }

    @Override
    public void addBoulder(Boulder boulder) {

    }

    @Override
    public void addBuilding(Building building) {

    }

    public World getWorld() {
        return world;
    }

    @Override
    public void toggle() {
        // Implement if needed
    }

    @Override
    public boolean isActive() {
        return false;
    }

    public Game getGame() {
        return game;
    }

    public Player getPlayer() {
        return player;
    }
}
