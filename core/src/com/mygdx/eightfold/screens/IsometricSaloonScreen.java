package com.mygdx.eightfold.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.IsometricPlayer;
import conversations.DialogueLine;
import helper.IsometricBodyHelperService;
import helper.tiledmap.IsometricTiledMapHelper;
import com.mygdx.eightfold.player.Player;
import objects.animals.Squirrel;
import objects.animals.bird.Bird;
import objects.animals.bird.Chicken;
import objects.animals.bugs.Bug;
import objects.animals.bugs.Butterfly;
import objects.animals.bugs.Dragonfly;
import objects.humans.NPC;
import objects.inanimate.*;
import text.infobox.InfoBox;
import text.textbox.SaloonTextBox;

import java.util.ArrayList;

import static helper.Constants.PPM;

public class IsometricSaloonScreen extends ScreenAdapter implements ScreenInterface {
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final IsometricGameScreen gameScreen;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private final IsometricTiledMapHelper tiledMapHelper;
    private IsometricTiledMapRenderer isometricTiledMapRenderer;
    private IsometricPlayer player;
    private Boolean gameTime = false;
    private boolean playerPositionInitialized = false;
    private final ScreenInterface screenInterface;
    private final GameContactListener gameContactListener;
    private final GameAssets gameAssets;
    private final Music music;
    private final ArrayList<Door> doorList;
    private Game game;

    // Isometric-specific fields
    private float tileWidth;
    private float tileHeight;

    // TextBox and InfoBox
    private SaloonTextBox textBox;
    private InfoBox infoBox;
    private boolean debugRendering = false;

    public IsometricSaloonScreen(OrthographicCamera camera, GameAssets gameAssets, IsometricGameScreen gameScreen, World world, ScreenInterface screenInterface, IsometricPlayer player, Game game) {
        this.camera = camera;
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

        // Use IsometricTiledMapHelper instead of TiledMapHelper
        this.tiledMapHelper = new IsometricTiledMapHelper(this, gameAssets, gameContactListener);

        // Use isometric map - ensure this file exists and is configured for isometric view in Tiled
        this.isometricTiledMapRenderer = tiledMapHelper.setupMap("maps/InsideMap_Iso.tmx");

        // Store tile dimensions from the map for coordinate conversions
        this.tileWidth = isometricTiledMapRenderer.getMap().getProperties().get("tilewidth", Integer.class);
        this.tileHeight = isometricTiledMapRenderer.getMap().getProperties().get("tileheight", Integer.class);

        // Set the tile dimensions for the IsometricBodyHelperService
        IsometricBodyHelperService.setTileDimensions(tileWidth, tileHeight);

        // Get player from the game screen
        this.player = player;

        Door door = null;
        if (player != null && player.getBody() == null) {
            player.createBody(world, door);
        }

        // Initialize TextBox and InfoBox
        //this.textBox = new SaloonTextBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")));
        this.infoBox = new InfoBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")));
        Gdx.input.setInputProcessor(textBox.getStage());
        Gdx.input.setInputProcessor(infoBox.getStage());
    }

    /**
     * Converts isometric coordinates to Cartesian coordinates
     * @param isoX X coordinate in isometric space
     * @param isoY Y coordinate in isometric space
     * @return Vector2 containing Cartesian coordinates
     */
    private Vector2 isoToCartesian(float isoX, float isoY) {
        return IsometricBodyHelperService.isoToCartesian(isoX, isoY);
    }

    /**
     * Converts Cartesian coordinates to isometric coordinates
     * @param cartX X coordinate in Cartesian space
     * @param cartY Y coordinate in Cartesian space
     * @return Vector2 containing isometric coordinates
     */
    private Vector2 cartesianToIso(float cartX, float cartY) {
        return IsometricBodyHelperService.cartesianToIso(cartX, cartY);
    }

    public void showTextBox(String text) {
        textBox.showTextBox(text);
    }

    @Override
    public void addChicken(Chicken chicken) {
        // Not needed for saloon screen
    }

    @Override
    public void addPond(Pond pond) {
        // Not needed for saloon screen
    }

    @Override
    public void addButterfly(Butterfly butterfly) {
        // Not needed for saloon screen
    }

    @Override
    public void addBug(Bug bug) {
        // Not needed for saloon screen
    }

    @Override
    public void addDragonfly(Dragonfly dragonfly) {
        // Not needed for saloon screen
    }

    @Override
    public void addNPC(NPC npc) {
        // Not needed for saloon screen, but could be implemented for NPCs in the saloon
    }

    @Override
    public void getNPCById(int id) {
        // Not implemented for saloon screen
    }

    @Override
    public void hideDecisionTextBox() {
        // Not implemented for saloon screen
    }

    @Override
    public void setChoices(String... choices) {
        // Not implemented for saloon screen
    }

    @Override
    public void addSquirrel(Squirrel squirrel) {
        // Not needed for saloon screen
    }

    @Override
    public OrthographicCamera getCamera() {
        return camera;
    }

    @Override
    public void setTextBox(String filepath) {
        // Could be implemented to change the textbox appearance
    }

    @Override
    public void setDecisionTextBox(String filepath) {
        // Not implemented for saloon screen
    }

    @Override
    public void showDecisionTextBox(String text) {
        // Not implemented for saloon screen
    }

    @Override
    public void showDecisionTextbox(DialogueLine dialogueLine) {
        // Not implemented for saloon screen
    }

    @Override
    public void showPlayerTextBox(String playerConversationText) {
        // Not implemented for saloon screen
    }

    @Override
    public void addBush(Bush bush) {
        // Not needed for saloon screen
    }

    @Override
    public void addRock(Rock rock) {
        // Not needed for saloon screen
    }

    public void hideTextBox() {
        textBox.hideTextBox();
    }

    @Override
    public void showTextBox(DialogueLine line) {
        // Not implemented for saloon screen
    }

    public void showInfoBox(String text) {
        infoBox.showInfoBox(text);
    }

    public void hideInfoBox() {
        infoBox.hideInfoBox();
    }

    @Override
    public void setSaloonTime(boolean saloonTime) {
        // Not used in saloon screen
    }

    @Override
    public void setGameTime(boolean saloonTime) {
        gameScreen.setSaloonTime(!saloonTime);
    }

    @Override
    public boolean isSaloonTime() {
        return true; // Always true in saloon screen
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
        isometricTiledMapRenderer.setView(camera);

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            enterPauseScreen();
        }

        if (player != null) {
            player.update(delta);  // Update the player
        }

        for (Door door : doorList) {
            door.update(delta);
        }

        if (player != null && !doorList.isEmpty()) {
            Door door = doorList.get(0);
            if (!playerPositionInitialized) {
                // Convert to isometric coordinates for visual positioning but keep physics in Cartesian
                Vector2 doorPos = door.getBody().getPosition();
                player.getBody().setTransform(doorPos.x, 2, 0); // Set the initial position
                playerPositionInitialized = true; // Set the flag to true to avoid resetting the position
            }
        }

        // Debug output for player position
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            if (player != null) {
                Vector2 pos = player.getBody().getPosition();
                Vector2 vel = player.getBody().getLinearVelocity();
                System.out.println("Player position: " + pos.x + ", " + pos.y);
                System.out.println("Player velocity: " + vel.x + ", " + vel.y);
            }
        }

        // Toggle debug rendering
        if (Gdx.input.isKeyJustPressed(Input.Keys.F3)) {
            debugRendering = !debugRendering;
            System.out.println("Debug rendering: " + (debugRendering ? "ON" : "OFF"));
        }
    }

    public void enterPauseScreen() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameAssets, gameScreen));
    }

    public void playerArrives() {
        if (!doorList.isEmpty()) {
            Door door = doorList.get(0);
            player.getBody().setTransform(door.getBody().getPosition().x, 2, 0);
        } else {
            System.err.println("No doors available for player arrival point");
        }
    }

    private void cameraUpdate() {
        if (player != null) {
            Vector3 position = camera.position;

            // For isometric view, get the sprite position
            Vector2 target = player.getBody().getPosition().scl(PPM);

            // Apply smoothing to camera movement
            position.x += (target.x - position.x) * 0.1f;
            position.y += (target.y - position.y) * 0.1f;
            camera.position.set(position);
            camera.update();
        }
    }

    public void addDoor(Door door) {
        if (doorList != null) {
            doorList.add(door);
            System.out.println("Added door: " + door);
        } else {
            System.err.println("doorList is null. Cannot add door.");
        }
    }

    @Override
    public void addLowerRock(Rock rock) {
        // Not needed for saloon screen
    }

    @Override
    public void addUpperRock(Rock rock) {
        // Not needed for saloon screen
    }

    @Override
    public void setPlayer(IsometricPlayer player) {

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

        // Render the isometric tiled map
        isometricTiledMapRenderer.setView(camera);
        isometricTiledMapRenderer.render();

        batch.begin();

        // Render game objects
        if (player != null) {
            player.render(batch);
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

        // Render physics debug if enabled
        if (debugRendering) {
            box2DDebugRenderer.render(world, camera.combined.scl(PPM));
        }
    }

    @Override
    public void dispose() {
        // Dispose of assets properly
        batch.dispose();
        world.dispose();
        box2DDebugRenderer.dispose();
        isometricTiledMapRenderer.dispose();
        textBox.getStage().dispose();
        textBox.getSkin().dispose();
        infoBox.getStage().dispose();
        infoBox.getSkin().dispose();
    }



    @Override
    public void addTree(Tree tree) {
        // Not needed for saloon screen
    }

    @Override
    public void addBird(Bird bird) {
        // Not needed for saloon screen
    }

    @Override
    public void addBoulder(Boulder boulder) {
        // Not needed for saloon screen
    }

    @Override
    public void addBuilding(Building building) {
        // Not needed for saloon screen
    }

    @Override
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

    public IsometricPlayer getPlayer() {
        return player;
    }
}