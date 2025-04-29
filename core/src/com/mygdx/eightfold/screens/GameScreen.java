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
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.DebugContactListener;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.ecs.EntityManager;
import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.save.SaveManager;
import conversations.DialogueLine;
import helper.ContactType;
import helper.EntityAnimator;
import helper.tiledmap.TiledMapHelper;
import helper.world.time.TimeOfDayHelper;
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
import text.textbox.DecisionTextBox;
import text.textbox.TextBox;
import box2dLight.RayHandler;
import box2dLight.PointLight;

import static helper.Constants.PPM;

public class GameScreen extends ScreenAdapter implements ScreenInterface {
    // Core game components
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ScreenInterface screenInterface;
    private World world;
    private EntityAnimator animator;
    private Box2DDebugRenderer box2DDebugRenderer;
    private final TiledMapHelper tiledMapHelper;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private Player player;
    private Music music;
    private final GameContactListener gameContactListener;
    private final DebugContactListener debugContactListener;
    private final TimeOfDayHelper timeOfDayHelper;
    private final GameAssets gameAssets;
    private final EntityManager entityManager;
    private SaveManager saveManager;
    private String currentTimeOfDay = "day";
    private boolean saloonTime = false;
    private Game game;
    private Skin skin;
    private RayHandler rayHandler;
    private PointLight playerLight;
    private Boolean debugRendering;
    private FPSLogger fpsLogger = new FPSLogger();
    private String timeOfDay;
    // UI Components
    private TextBox textBox;
    private DecisionTextBox decisionTextBox;
    private InfoBox infoBox;
    private String origin;
    private final Array<Body> bodiesToRemove = new Array<>();

    public GameScreen(OrthographicCamera camera, ScreenInterface screenInterface, GameAssets gameAssets, Game game, String origin) {

        this.screenInterface = screenInterface;
        this.timeOfDayHelper = new TimeOfDayHelper();
        this.camera = camera;
        this.origin = origin;
        this.batch = new SpriteBatch();
        this.game = game;
        this.music = gameAssets.getMusic("lost & found.mp3");

        this.world = new World(new Vector2(0, 0), false);
        this.gameAssets = gameAssets;
        this.gameContactListener = new GameContactListener(this);
        this.debugContactListener = new DebugContactListener();
        this.world.setContactListener(this.gameContactListener);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.debugRendering = false;

        // Initialize the EntityManager with the world
        this.entityManager = new EntityManager(world);

        this.tiledMapHelper = new TiledMapHelper(this, gameAssets, gameContactListener);
        this.orthogonalTiledMapRenderer = tiledMapHelper.setupMap("maps/new_Maps/Eightfold.tmx");

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
                // Empty implementation
            }
        };

        this.infoBox = new InfoBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")));
        this.saveManager = new SaveManager(this);

        // Initialize lighting
        rayHandler = new RayHandler(world);
        RayHandler.useDiffuseLight(true);
        rayHandler.setCulling(true);
        rayHandler.setBlurNum(1);
        rayHandler.setShadows(true);

        // Set up time of day
        timeOfDay = "day";
        float[] ambientColor = timeOfDayHelper.returnTime(timeOfDay);
        rayHandler.setAmbientLight(ambientColor[0], ambientColor[1], ambientColor[2], ambientColor[3]);
        rayHandler.setBlur(true);
        rayHandler.setShadows(true);

        // Set up input processors
        Gdx.input.setInputProcessor(textBox.getStage());
        Gdx.input.setInputProcessor(infoBox.getStage());

        // Adjust player location based on origin
        adjustLocation();
    }

    private void adjustLocation() {
        if(origin.equals("saloon") && !entityManager.getDoors().isEmpty()) {
            Door door = entityManager.getDoors().get(0);
            player.getBody().setTransform(door.getBody().getPosition().x, door.getBody().getPosition().y - 2, 0);
        }
    }

    @Override
    public void setTextBox(String filepath) {
        try {
            this.textBox = new TextBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")), filepath) {
                @Override
                public void setFontColor(float r, float g, float b, float a) {
                    // Empty implementation
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
        // Implementation needed
    }

    public void setDecisionTextBox(String filepath) {
        try {
            this.decisionTextBox = new DecisionTextBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")), filepath) {
                @Override
                public void setFontColor(float r, float g, float b, float a) {
                    // Empty implementation
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
        // Implementation needed
    }

    public void hideTextBox() {
        textBox.hideTextBox();
    }

    public void hideDecisionTextBox() {
        decisionTextBox.hideTextBox();
    }

    @Override
    public void setChoices(String... choices) {
        // Implementation needed
    }

    @Override
    public void showTextBox(DialogueLine line) {
        // Implementation needed
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
        // Implementation needed
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isSaloonTime() {
        return saloonTime;
    }

    @Override
    public void transitionToScreen(ScreenInterface newScreen) {
        entityManager.removePlayerBody();
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
        for (Door door : entityManager.getDoors()) {
            door.setScreen(newScreen);
        }
    }

    private void update(float delta) {
        world.step(1 / 60f, 6, 2);
        removeScheduledBodies();
        cameraUpdate();

        if (Gdx.input.isKeyJustPressed(Input.Keys.T)){
            System.out.println("t");
            game.setScreen(new GameScreen(camera, screenInterface, gameAssets, game, origin));

        }

        // Update player light position
        if (player != null && playerLight != null) {
            Vector2 pos = player.getBody().getPosition();
            playerLight.setPosition(pos.x + .1f, pos.y);
        }

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);

        // Update all entities using the EntityManager
        entityManager.update(delta);

        // Handle user input
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            enterPauseScreen();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameAssets, this));
        }

        // Transition to Saloon if needed
        if (saloonTime) {
            World newWorld = new World(new Vector2(0, 0), false);
            SaloonScreen saloonScreen = new SaloonScreen(camera, gameAssets, this, newWorld, this, player, game);
            ((Game) Gdx.app.getApplicationListener()).setScreen(saloonScreen);
            updateDoorScreenReferences(saloonScreen);
        }

        textBox.resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public void enterPauseScreen() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameAssets, this));
    }

    public void removeEntity(GameEntity entity){
        entityManager.removeEntity(entity);
    }

    private void removeScheduledBodies() {
        for (Body body : bodiesToRemove) {
            if (body != null) {
                world.destroyBody(body);
            }
        }
        bodiesToRemove.clear();
    }

    public void scheduleBodyForRemoval(Body body) {
        bodiesToRemove.add(body);
    }

    private void cameraUpdate() {
        if (player != null) {
            Vector3 position = camera.position;
            Vector2 target = player.getBody().getPosition().scl(PPM);
            position.x += (target.x - position.x) * 0.1f; // smoothing factor
            position.y += (target.y - position.y) * 0.1f;
            camera.position.set(position);
            camera.update();
        }
    }

    // Use the EntityManager for entity management
    @Override
    public void addDoor(Door door) {
        entityManager.addDoor(door);
    }

    @Override
    public void addChicken(Chicken chicken) {
        entityManager.addChicken(chicken);
    }

    @Override
    public void addSquirrel(Squirrel squirrel) {
        entityManager.addSquirrel(squirrel);
    }

    @Override
    public void addFence(Fence fence) {
        entityManager.addFence(fence);
    }

    @Override
    public GameAssets getGameAssets() {
        return this.gameAssets;
    }

    @Override
    public void addEnemy(Enemy enemy) {
        entityManager.addEnemy(enemy);
    }

    @Override
    public void addEntity(GameEntity gameEntity) {
        entityManager.addEntity(gameEntity);
    }


    @Override
    public void addPond(Pond pond) {
        entityManager.addPond(pond);
    }

    @Override
    public void addButterfly(Butterfly butterfly) {
        entityManager.addButterfly(butterfly);
    }

    @Override
    public void addBug(Bug bug) {
        entityManager.addBug(bug);
    }

    @Override
    public void addDragonfly(Dragonfly dragonfly) {
        entityManager.addDragonfly(dragonfly);
    }

    @Override
    public void addNPC(NPC npc) {
        entityManager.addNPC(npc);
    }

    @Override
    public void getNPCById(int id) {
        entityManager.getNPCById(id);
    }



    public void playerArrives() {
        if (!entityManager.getDoors().isEmpty()) {
            Door door = entityManager.getDoors().get(0);
            player.getBody().setTransform(door.getBody().getPosition().x, door.getBody().getPosition().y + 200, 0);
        }
    }


    public void removePlayerBody() {
        entityManager.removePlayerBody();
    }

    @Override
    public void addBird(Bird bird) {
        entityManager.addBird(bird);
    }

    @Override
    public void addBoulder(Boulder boulder) {
        entityManager.addBoulder(boulder);
    }

    @Override
    public void addBuilding(Building building) {
        entityManager.addBuilding(building);
    }

    @Override
    public void addTree(Tree tree) {
        entityManager.addTree(tree);
    }

    @Override
    public void addBush(Bush bush) {
        entityManager.addBush(bush);
    }

    @Override
    public void addRock(Rock rock) {
        entityManager.addRock(rock);
    }

    @Override
    public void render(float delta) {
        // Update the game state
        update(delta);
        camera.update();
        music.setVolume(.09f);
        //music.play();
        // Clear the screen
        Gdx.gl.glClearColor(168f / 255f, 178f / 255f, 113f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set up the tiled map renderer to follow the camera
        orthogonalTiledMapRenderer.setView(camera);
        orthogonalTiledMapRenderer.render();

        // Begin drawing with the SpriteBatch
        batch.begin();

        // Use the EntityManager to render all entities
        entityManager.render(batch);

        batch.end();

        // Render lighting
        rayHandler.setCombinedMatrix(camera.combined.cpy().scl(PPM));
        rayHandler.updateAndRender();

        // Render UI elements
        textBox.getStage().act(delta);
        textBox.getStage().draw();

        // Debug rendering
        if(debugRendering) {
            box2DDebugRenderer.render(world, camera.combined.scl(PPM));
        }
    }

    @Override
    public void dispose() {
        // Dispose of assets properly
        batch.dispose();
        world.dispose();
        orthogonalTiledMapRenderer.dispose();
        textBox.getStage().dispose();
        textBox.getSkin().dispose();
        rayHandler.dispose();
        entityManager.dispose();
    }

    public void resetPlayer(Player player, Door door) {
        player.screenChange(world, door);
    }

    public void enablePlayerLight() {
        if (rayHandler != null && player != null && player.getBody() != null) {
            if (playerLight != null) {
                playerLight.remove(); // Remove old light if it exists
            }

            // Create a new light
            playerLight = new PointLight(rayHandler, 128, new Color(.5f, .4f, .5f, 1f), .4f, 0, 0);
            playerLight.setSoftnessLength(1f);
            playerLight.setContactFilter(ContactType.LIGHT.getCategoryBits(),
                    ContactType.LIGHT.getMaskBits(),
                    (short) 0);
        }
    }

    public void setPlayer(Player player) {
        this.player = player;
        entityManager.setPlayer(player);
        enablePlayerLight();
    }

    public World getWorld() {
        return world;
    }

    public void flipDebugRendering() {
        debugRendering = !debugRendering;
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public void setTimeOfDay(String timeOfDay) {
        if (rayHandler != null) {
            // Get the appropriate ambient color for the selected time
            this.currentTimeOfDay = timeOfDay;
            float[] ambientColor = timeOfDayHelper.returnTime(timeOfDay);

            // Set the ambient light in the RayHandler
            rayHandler.setAmbientLight(
                    ambientColor[0], ambientColor[1], ambientColor[2], ambientColor[3]
            );
        }
    }

    public String getCurrentTimeOfDay() {
        return currentTimeOfDay;
    }

    public void saveGame(String fileName) {
        saveManager.saveGame(fileName);
        // Optional: show a notification or feedback to the player
        Gdx.app.log("GameScreen", "Game saved to " + fileName);
    }

    public void loadGame(String fileName) {
        saveManager.loadGame(fileName);
        // Optional: show a notification or feedback to the player
        Gdx.app.log("GameScreen", "Game loaded from " + fileName);
    }



}