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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.eightfold.Boot;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.GameAssets;
import conversations.DialogueLine;
import helper.tiledmap.TiledMapHelper;
import com.mygdx.eightfold.player.Player;
import objects.GameEntity;
import objects.animals.squirrel.Squirrel;
import objects.animals.birds.Bird;
import objects.animals.farm_animals.Chicken;
import objects.animals.bugs.Bug;
import objects.animals.bugs.Butterfly;
import objects.animals.bugs.Dragonfly;
import objects.humans.Enemy;
import objects.humans.NPC;
import objects.inanimate.*;
import text.infobox.InfoBox;
import text.textbox.SaloonTextBox;

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

        Door door = null;
        player.createBody(world, door);


        if (player == null) {

            //System.out.println("Player is null!");
        }

        // Initialize TextBox and InfoBox

        this.infoBox = new InfoBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")));
        Gdx.input.setInputProcessor(textBox.getStage());
        Gdx.input.setInputProcessor(infoBox.getStage());
    }

    public void showTextBox(String text) {
        textBox.showTextBox(text);
    }

    @Override
    public void addChicken(Chicken chicken) {

    }

    @Override
    public void addPond(Pond pond) {

    }

    @Override
    public void addButterfly(Butterfly butterfly) {

    }

    @Override
    public void addBug(Bug bug) {

    }

    @Override
    public void addDragonfly(Dragonfly dragonfly) {

    }


    @Override
    public void addNPC(NPC npc) {

    }

    @Override
    public void getNPCById(int id) {

    }

    @Override
    public void hideDecisionTextBox() {

    }

    @Override
    public void setChoices(String... choices) {

    }

    @Override
    public void addSquirrel(Squirrel squirrel) {

    }

    @Override
    public void addFence(Fence fence) {

    }

    @Override
    public GameAssets getGameAssets() {
        return this.gameAssets;
    }

    @Override
    public void addEnemy(Enemy enemy) {

    }

    @Override
    public void addEntity(GameEntity gameEntity) {

    }

    @Override
    public void removeEntity(GameEntity entity) {

    }


    @Override
    public OrthographicCamera getCamera() {
        return camera;
    }

    @Override
    public void setTextBox(String filepath) {

    }

    @Override
    public void setDecisionTextBox(String filepath) {

    }

    @Override
    public void showDecisionTextBox(String text) {

    }

    @Override
    public void showDecisionTextbox(DialogueLine dialogueLine) {

    }


    @Override
    public void showPlayerTextBox(String playerConversationText) {

    }

    @Override
    public void addBush(Bush bush) {

    }

    @Override
    public void addRock(Rock rock) {

    }


    public void hideTextBox() {
        textBox.hideTextBox();
    }

    @Override
    public void showTextBox(DialogueLine line) {

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
            Door door = doorList.get(0);
            if (!playerPositionInitialized) {
                player.getBody().setTransform(door.getBody().getPosition().x, 2, 0); // Set the initial position
                playerPositionInitialized = true; // Set the flag to true to avoid resetting the position
            }

            player.update(delta);  // Update the player
        }


    }

    public void enterPauseScreen() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameAssets, gameScreen));
    }

    public void playerArrives(){
        Door door = doorList.get(0);
       player.getBody().setTransform(door.getBody().getPosition().x, 2, 0);
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
        //System.out.println("HELLLLO");
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
        music.play();

        batch.end();

        // Render the Stage
        textBox.getStage().act(delta);
        textBox.getStage().draw();
        infoBox.getStage().act(delta);
        infoBox.getStage().draw();

        // Uncomment for debugging physics bodies
       // box2DDebugRenderer.render(world, camera.combined.scl(PPM));
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

        Door door = null;
        if (player != null && player.getBody() == null) {
            player.createBody(world, door); // Create a new body in the new world
        }
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
