package com.mygdx.eightfold.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.GameEntity;
import com.mygdx.eightfold.player.IsometricPlayer;
import conversations.DialogueLine;
import helper.ContactType;
import helper.IsometricBodyHelperService;
import helper.tiledmap.IsometricTiledMapHelper;
import helper.world.time.TimeOfDayHelper;
import objects.animals.Squirrel;
import objects.animals.bird.Bird;

import objects.animals.bird.Chicken;
import objects.animals.bugs.Bug;
import objects.animals.bugs.Butterfly;
import objects.animals.bugs.Dragonfly;
import objects.humans.NPC;
import objects.inanimate.*;
import com.mygdx.eightfold.player.Player;
import text.infobox.InfoBox;

import text.textbox.DecisionTextBox;
import text.textbox.TextBox;
import box2dLight.RayHandler;
import box2dLight.PointLight;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static helper.Constants.PPM;

public class IsometricGameScreen extends ScreenAdapter implements ScreenInterface {
    private final ArrayList<Bird> birdList;
    private final ArrayList<Building> buildingList;
    private final ArrayList<Boulder> boulderList;
    private final ArrayList<Tree> treeList;
    private final ArrayList<Bush> bushList;
    private final ArrayList<Rock> rockList;
    private final ArrayList<Rock> rockTopList;
    private final ArrayList<Pond> pondList;
    private final ArrayList<Butterfly> butterflyList;
    private final ArrayList<Dragonfly> dragonflyList;
    private final ArrayList<Chicken> chickenList;
    private final ArrayList<Squirrel> squirrelList;
    private final ArrayList<NPC> NPCList;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ScreenInterface screenInterface;
    private final ArrayList<Door> doorList;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private final IsometricTiledMapHelper tiledMapHelper;
    private IsometricTiledMapRenderer isometricTiledMapRenderer;
    private IsometricPlayer player;  // Changed from Player to IsometricPlayer
    private Music music;
    private final GameContactListener gameContactListener;
    private final TimeOfDayHelper timeOfDayHelper;
    private final GameAssets gameAssets;
    private boolean saloonTime = false;
    private Game game;
    private Skin skin;
    private RayHandler rayHandler;
    private PointLight playerLight;
    private Boolean debugRendering;
    private FPSLogger fpsLogger = new FPSLogger();

    // Isometric-specific fields
    private float tileWidth;
    private float tileHeight;

    public OrthographicCamera getCamera() {
        return camera;
    }

    // TextBox
    private TextBox textBox;
    private DecisionTextBox decisionTextBox;
    private InfoBox infoBox;
    private String origin;

    public IsometricGameScreen(OrthographicCamera camera, ScreenInterface screenInterface, GameAssets gameAssets, Game game, String origin) {
        this.screenInterface = this; // Set this as the screen interface
        this.timeOfDayHelper = new TimeOfDayHelper();
        this.buildingList = new ArrayList<>();
        this.camera = camera;
        this.pondList = new ArrayList<>();
        this.player = player;
        this.birdList = new ArrayList<>();
        this.boulderList = new ArrayList<>();
        this.treeList = new ArrayList<>();
        this.doorList = new ArrayList<>();
        this.bushList = new ArrayList<>();
        this.rockList = new ArrayList<>();
        this.chickenList = new ArrayList<>();
        this.squirrelList = new ArrayList<>();
        this.butterflyList = new ArrayList<>();
        this.dragonflyList = new ArrayList<>();
        this.rockTopList = new ArrayList<>();
        this.NPCList = new ArrayList<>();
        this.origin = origin;
        this.batch = new SpriteBatch();
        this.game = game;
        this.music = gameAssets.getMusic("lost & found.mp3");
        this.world = new World(new Vector2(0, 0), false);
        this.gameAssets = gameAssets;
        this.gameContactListener = new GameContactListener(this);
        this.world.setContactListener(this.gameContactListener);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.debugRendering = true; // Set to true to help debug

        // Use IsometricTiledMapHelper instead of TiledMapHelper
        this.tiledMapHelper = new IsometricTiledMapHelper(this, gameAssets, gameContactListener);

        // Use isometric map file - make sure this file exists and is configured for isometric view in Tiled
        this.isometricTiledMapRenderer = tiledMapHelper.setupMap("maps/new_Maps/Eightfold_Iso.tmx");

        // Store tile dimensions from the map for coordinate conversions
        this.tileWidth = isometricTiledMapRenderer.getMap().getProperties().get("tilewidth", Integer.class);
        this.tileHeight = isometricTiledMapRenderer.getMap().getProperties().get("tileheight", Integer.class);

        // Set the tile dimensions for the IsometricBodyHelperService
        IsometricBodyHelperService.setTileDimensions(tileWidth, tileHeight);
        camera.zoom = .8f;
        // Initialize TextBox
        Skin skin = new Skin(Gdx.files.internal("commodore64/skin/uiskin.json"));
        this.skin = skin;
        this.textBox = new TextBox(skin, "player/player-single.png") {
            @Override
            public void setFontColor(float r, float g, float b, float a) {
                // Empty implementation
            }
        };
        this.decisionTextBox = new DecisionTextBox(skin, "player/player-single.png") {
            @Override
            public void setFontColor(float r, float g, float b, float a) {

            }
        };

        this.infoBox = new InfoBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")));

        rayHandler = new RayHandler(world);
        RayHandler.useDiffuseLight(true);
        rayHandler.setCulling(true);  // Ensure lights are not culled
        rayHandler.setBlurNum(1);      // Low blur for better performance during testing
        rayHandler.setShadows(true);

        String timeOfDay = "day";
        float[] ambientColor = timeOfDayHelper.returnTime(timeOfDay);
        rayHandler.setAmbientLight(ambientColor[0], ambientColor[1], ambientColor[2], ambientColor[3]);

        // Optional: soften shadows, tweak performance
        rayHandler.setBlur(true);
        rayHandler.setShadows(true);

        Gdx.input.setInputProcessor(textBox.getStage());
        Gdx.input.setInputProcessor(infoBox.getStage());

        // Note: player will be set by the map parser when it finds the player object
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

    private void adjustLocation() {
        if(origin == "saloon" && !doorList.isEmpty()){
            Door door = doorList.get(0);
            Vector2 doorPos = door.getBody().getPosition();
            Vector2 offsetPos = new Vector2(doorPos.x, doorPos.y - 2);
            player.getBody().setTransform(offsetPos, 0);
        }
    }

    @Override
    public void setTextBox(String filepath) {
        try {
            this.textBox = new TextBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")), filepath) {
                @Override
                public void setFontColor(float r, float g, float b, float a) {

                }
            };
            Gdx.input.setInputProcessor(textBox.getStage());
        } catch (Exception e) {
            System.err.println("Error reading file: " + filepath);
            e.printStackTrace();
        }
    }

    public void showTextBox(String text) {
        textBox.showTextBox(text);
    }

    public void showDecisionTextBox(String text) {
        decisionTextBox.showTextBox(text);
    }

    @Override
    public void showDecisionTextbox(DialogueLine dialogueLine) {

    }

    public void setDecisionTextBox(String filepath){
        try {
            this.decisionTextBox = new DecisionTextBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")), filepath) {
                @Override
                public void setFontColor(float r, float g, float b, float a) {

                }
            };
            Gdx.input.setInputProcessor(textBox.getStage());
        } catch (Exception e) {
            System.err.println("Error reading file: " + filepath);
            e.printStackTrace();
        }
    }

    @Override
    public void showPlayerTextBox(String playerConversationText) {

    }

    public void hideTextBox() {
        textBox.hideTextBox();
    }

    public void hideDecisionTextBox() {
        decisionTextBox.hideTextBox();
    }

    @Override
    public void setChoices(String... choices) {
        //decisionTextBox.setChoices(choices);
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

    public void setSaloonTime(boolean time) {
        music.stop();
        this.saloonTime = time;
    }

    @Override
    public void setGameTime(boolean saloonTime) {

    }

    public IsometricPlayer getPlayer() {
        return player;
    }

    public boolean isSaloonTime() {
        return saloonTime;
    }

    @Override
    public void transitionToScreen(ScreenInterface newScreen) {
        removePlayerBody();
        ((Game) Gdx.app.getApplicationListener()).setScreen((ScreenAdapter) newScreen);
        updateDoorScreenReferences(newScreen);
    }

    @Override
    public void toggle() {
        // Implement toggle logic if needed
    }

    @Override
    public boolean isActive() {
        return false;
    }

    private void updateDoorScreenReferences(ScreenInterface newScreen) {
        for (Door door : doorList) {
            removePlayerBody();
            door.setScreen(newScreen);
        }
    }

    private void update(float delta) {
        world.step(1 / 60f, 6, 2);
        cameraUpdate();

        if (player != null && playerLight != null) {
            Vector2 pos = player.getBody().getPosition();
            playerLight.setPosition(pos.x + .1f, pos.y);
        }

        batch.setProjectionMatrix(camera.combined);
        isometricTiledMapRenderer.setView(camera);

        // Update all game entities
        for (Butterfly butterfly : butterflyList){
            butterfly.update(delta);
        }

        for (Dragonfly dragonfly : dragonflyList){
            dragonfly.update(delta);
        }

        for (Squirrel squirrel : squirrelList) {
            squirrel.update(delta);
        }

        for (Chicken chicken : chickenList) {
            chicken.update(delta);
        }

        for(NPC npc : NPCList){
            npc.update(delta);
        }

        for (Tree tree : treeList) {
            tree.update(delta);
        }

        for(Bush bush: bushList){
            bush.update(delta);
        }

        // Debug output for player position
        if (player != null) {
            player.update(delta);

            // Print player position for debugging
            if (Gdx.input.isKeyJustPressed(Input.Keys.L)) {
                Vector2 pos = player.getBody().getPosition();
                Vector2 vel = player.getBody().getLinearVelocity();
                System.out.println("Player position: " + pos.x + ", " + pos.y);
                System.out.println("Player velocity: " + vel.x + ", " + vel.y);
            }
        }

        for (Bird bird : birdList) {
            bird.update(delta);
        }

        for (Pond pond : pondList) {
            pond.update(delta);
        }

        // Input handling
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            enterPauseScreen();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameAssets, this));
        }

        if (saloonTime) {
            World newWorld = new World(new Vector2(0, 0), false);
            IsometricSaloonScreen saloonScreen = new IsometricSaloonScreen(camera, gameAssets, this, newWorld, this, player, game);
            ((Game) Gdx.app.getApplicationListener()).setScreen(saloonScreen);
            updateDoorScreenReferences(saloonScreen);
        }

        textBox.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void enterPauseScreen() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameAssets, this));
    }

    private void cameraUpdate() {
        if (player != null) {
            Vector3 position = camera.position;

            // In isometric view, we need to get the screen position, not the physics position
            Vector2 target = player.getBody().getPosition().scl(PPM);

            // Apply smoothing to camera movement
            position.x += (target.x - position.x) * 0.1f;
            position.y += (target.y - position.y) * 0.1f;
            camera.position.set(position);
            camera.update();
        }
    }

    @Override
    public void addDoor(Door door) {
        if (doorList != null) {
            doorList.add(door);
        } else {
            System.err.println("doorList is null. Cannot add door.");
        }
    }

    @Override
    public void addChicken(Chicken chicken) {
        if (chickenList != null) {
            chickenList.add(chicken);
        } else {
            System.err.println("doorList is null. Cannot add door.");
        }
    }

    @Override
    public void addSquirrel(Squirrel squirrel) {
        if (squirrelList != null) {
            squirrelList.add(squirrel);
        } else {
            System.err.println("squirrelList is null. Cannot add squirrel");
        }
    }

    @Override
    public void addPond(Pond pond) {
        if (pondList != null) {
            pondList.add(pond);
        } else {
            System.err.println("pondList is null.");
        }
    }

    @Override
    public void addButterfly(Butterfly butterfly) {
        if (butterflyList != null) {
            butterflyList.add(butterfly);
        } else {
            System.err.println("butterflyList is null.");
        }
    }

    @Override
    public void addBug(Bug bug) {

    }

    @Override
    public void addDragonfly(Dragonfly dragonfly) {
        if (dragonflyList != null) {
            dragonflyList.add(dragonfly);
        } else {
            System.err.println("butterflyList is null.");
        }
    }

    @Override
    public void addNPC(NPC npc) {
        if (NPCList != null){
            NPCList.add(npc);
        }
    }

    @Override
    public void getNPCById(int id) {

    }

    @Override
    public void addLowerRock(Rock rock) {

    }

    public void playerArrives(){
        if (doorList.isEmpty()) {
            System.err.println("No doors available for player arrival point");
            return;
        }

        Door door = doorList.get(0);
        // In isometric view, y-offset might need to be adjusted
        player.getBody().setTransform(door.getBody().getPosition().x, door.getBody().getPosition().y + 2, 0);
    }

    @Override
    public void addUpperRock(Rock rock) {

    }

    public void removePlayerBody() {
        if (player != null && player.getBody() != null) {
            world.destroyBody(player.getBody());
            player.setBody(null);
        }
    }

    @Override
    public void addBird(Bird bird) {
        if (birdList != null) {
            birdList.add(bird);
        } else {
            System.err.println("birdList is null. Cannot add bird.");
        }
    }

    @Override
    public void addBoulder(Boulder boulder) {
        if (boulderList != null) {
            boulderList.add(boulder);
        }
    }

    @Override
    public void addBuilding(Building building) {
        if (buildingList != null) {
            buildingList.add(building);
        }
    }

    @Override
    public void addTree(Tree tree) {
        if (treeList != null) {
            treeList.add(tree);
        }
    }

    @Override
    public void addBush(Bush bush) {
        if (bushList != null) {
            bushList.add(bush);
        }
    }

    @Override
    public void addRock(Rock rock) {
        rockList.add(rock);
    }

    @Override
    public void render(float delta) {
        // Update the game state
        update(delta);
        camera.update();

        // Clear the screen with a suitable background color
        Gdx.gl.glClearColor(168f / 255f, 178f / 255f, 113f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set up the isometric map renderer to follow the camera
        isometricTiledMapRenderer.setView(camera);
        isometricTiledMapRenderer.render();

        // Create a list for all entities that need depth sorting
        List<GameEntity> sortedEntities = new ArrayList<>();

        // Add all entities that should be sorted
        if (player != null) sortedEntities.add(player);
        sortedEntities.addAll(chickenList);
        sortedEntities.addAll(squirrelList);
        sortedEntities.addAll(birdList);
        sortedEntities.addAll(butterflyList);
        sortedEntities.addAll(dragonflyList);
        sortedEntities.addAll(NPCList);
        sortedEntities.addAll(bushList);
        sortedEntities.addAll(treeList);

        // Sort by Y position for isometric depth
        // For isometric view, we might need a custom comparator based on isometric coordinates
        Collections.sort(sortedEntities, GameEntity.Y_COMPARATOR);

        // Begin drawing with the SpriteBatch
        batch.begin();

        // Render background elements that are always behind
        for (Building building : buildingList) {
            building.getBottomSprite().draw(batch);
        }

        for (Pond pond : pondList){
            pond.render(batch);
        }

        // Render rocks bottom
        for (Rock rock : rockList) {
            rock.renderBottom(batch);
        }

        // Render all depth-sorted entities
        for (GameEntity entity : sortedEntities) {
            entity.render(batch);
        }

        // Render elements that are always on top
        for (Rock rock : rockList) {
            rock.renderTop(batch);
        }

        for (Door door : doorList) {
            door.render(batch);
        }

        // Draw all building tops last
        for (Building building : buildingList) {
            building.getTopSprite().draw(batch);
        }
        batch.end();

        // Render lights
        rayHandler.setCombinedMatrix(camera.combined.cpy().scl(PPM));
        rayHandler.updateAndRender();

        // Render UI elements
        textBox.getStage().act(delta);
        textBox.getStage().draw();

        // Debug rendering if enabled
        if(debugRendering) {
            box2DDebugRenderer.render(world, camera.combined.scl(PPM));
        }
    }

    @Override
    public void dispose() {
        // Dispose of assets properly
        batch.dispose();
        world.dispose();
        isometricTiledMapRenderer.dispose();
        textBox.getStage().dispose();
        textBox.getSkin().dispose();
        rayHandler.dispose();
    }

    public void resetPlayer(Player player, Door door){
        player.screenChange(world, door);
    }

    public void enablePlayerLight() {
        if (rayHandler != null && player != null && player.getBody() != null) {
            if (playerLight != null) {
                playerLight.remove(); // Remove old light if it exists
            }

            // Get the current position
            Vector2 pos = player.getBody().getPosition();

            // Create a new light (note: light radius is in world units, not pixels)
            playerLight = new PointLight(rayHandler, 128, new Color(.5f, .4f, .5f, 1f), .4f, 0, 0);
            playerLight.setSoftnessLength(1f);
            playerLight.setContactFilter(ContactType.LIGHT.getCategoryBits(),
                    ContactType.LIGHT.getMaskBits(),
                    (short) 0);
        }
    }

    public void setPlayer(IsometricPlayer player) {
        if (player instanceof IsometricPlayer) {
            this.player = (IsometricPlayer) player;
        } else {
            // Create a new IsometricPlayer with the same properties
            System.out.println("WARNING: Converting Player to IsometricPlayer");
            Vector2 pos = player.getBody().getPosition();
            float width = player.getWidth();
            float height = player.getHeight();

            // Create IsometricPlayer with same position and dimensions
            this.player = new IsometricPlayer(
                    pos.x * PPM,
                    pos.y * PPM,
                    width,
                    height,
                    player.getBody(),
                    this,
                    gameAssets
            );
        }
        enablePlayerLight();
    }

    public World getWorld() {
        return world;
    }

    public void flipDebugRendering() {
        debugRendering = !debugRendering;
        System.out.println("Debug rendering: " + (debugRendering ? "ON" : "OFF"));
    }

    public IsometricPlayer getIsometricPlayer() {
        return player;
    }
}