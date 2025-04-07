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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.GameEntity;
import conversations.DialogueLine;
import helper.ContactType;
import helper.tiledmap.TiledMapHelper;
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
//import box2dLight.AmbientLight;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static helper.Constants.PPM;

public class GameScreen extends ScreenAdapter implements ScreenInterface {
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
    private final TiledMapHelper tiledMapHelper;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private Player player;
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
    public OrthographicCamera getCamera() {
        return camera;
    }

    // TextBox
    private TextBox textBox;
    private DecisionTextBox decisionTextBox;
    private InfoBox infoBox;
    private String origin;

    public GameScreen(OrthographicCamera camera, ScreenInterface screenInterface, GameAssets gameAssets, Game game, String origin) {


        this.screenInterface = screenInterface;
        this.timeOfDayHelper = new TimeOfDayHelper();
        this.buildingList = new ArrayList<>();
        this.camera = camera;
        this.pondList = new ArrayList<>();

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
        this.debugRendering = false;
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

            }
        };

        this.infoBox = new InfoBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")));

        rayHandler = new RayHandler(world);
        RayHandler.useDiffuseLight(true);
        rayHandler.setCulling(true);  // Ensure lights are not culled
        rayHandler.setBlurNum(1);      // Low blur for better performance during testing
        rayHandler.setShadows(true);
        //rayHandler.setAmbientLight(0.1f, 0.1f, 0.1f, 1f);  // Very dark to make lights more visible
        //new PointLight(rayHandler, 128, new Color(1, 0, 0, 1), 20f, 60f, 20f);
        //new PointLight(rayHandler, 128, new Color(1, 1, 0.5f, 1), 10f, 50f, 10f);
        String timeOfDay = "day";
        float camX = camera.position.x * PPM; // Convert to Box2D coordinates
        float camY = camera.position.y * PPM;        //rayHandler.setCombinedMatrix(camera.combined);
        //em.out.println("Test light position: " + camX + ", " + camY);// darker environment // night
        float[] ambientColor = timeOfDayHelper.returnTime(timeOfDay);
        rayHandler.setAmbientLight(ambientColor[0], ambientColor[1], ambientColor[2], ambientColor[3]);// neutral light
        // Optional: soften shadows, tweak performance
        rayHandler.setBlur(true);
        rayHandler.setShadows(true);
        setPlayer(player);
        //enablePlayerLight();
        Gdx.input.setInputProcessor(textBox.getStage());
        Gdx.input.setInputProcessor(infoBox.getStage());
        adjustLocation();
    }

    private void adjustLocation() {

        if(origin == "saloon"){
            Door door = doorList.get(0);
            //System.out.println("fromSaloon true");
            player.getBody().setTransform(door.getBody().getPosition().x, door.getBody().getPosition().y - 2, 0);
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
        // hideTextBox();
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
        //System.out.println("CLOSE!");
        this.saloonTime = time;
    }

    @Override
    public void setGameTime(boolean saloonTime) {

    }

    public Player getPlayer() {
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

        //System.out.println("Camera position: " + camera.position.x + ", " + camera.position.y);


        if (player != null && playerLight != null) {
            Vector2 pos = player.getBody().getPosition();
//            System.out.println("playerLight x: " + pos.x * PPM);
//            System.out.println("playerLight y: " + pos.y * PPM);
            playerLight.setPosition(pos.x + .1f , pos.y);

        }

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);

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



        if (player != null) {
            player.update(delta);
        }








        for (Bird bird : birdList) {
            bird.update(delta);
        }




        for (Pond pond : pondList) {
            pond.update(delta);
        }




        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            enterPauseScreen();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameAssets, this));
        }
        if (saloonTime) {
            World newWorld = new World(new Vector2(0, 0), false); // Create a new World instance for the new screen
            // Create a new GameContactListener instance
            SaloonScreen saloonScreen = new SaloonScreen(camera, gameAssets,   this, newWorld, this, player, game);
            // Use new instances
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
            Vector2 target = player.getBody().getPosition().scl(PPM);
            position.x += (target.x - position.x) * 0.1f; // smoothing factor
            position.y += (target.y - position.y) * 0.1f;
            camera.position.set(position);
            camera.update();

            // 👇 Set player light position (if not attached to body)
//            if (playerLight != null) {
//                //System.out.println("player light exists");
//                Vector2 lightPos = player.getBody().getPosition();
//                System.out.println("lightpos = " + lightPos);
//                playerLight.setPosition(lightPos);
//            }
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
        Door door = doorList.get(0);
        player.getBody().setTransform(door.getBody().getPosition().x, door.getBody().getPosition().y +200, 0);
    }

    @Override
    public void addUpperRock(Rock rock) {

    }

    public void removePlayerBody() {
        //System.out.println("player body before : " + player.getBody());
        if (player != null && player.getBody() != null) {
            world.destroyBody(player.getBody());
            player.setBody(null);
            //System.out.println("player body after: " + player.getBody());// Clear the reference to the old body
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

        // Clear the screen
        Gdx.gl.glClearColor(168f / 255f, 178f / 255f, 113f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Set up the tiled map renderer to follow the camera
        orthogonalTiledMapRenderer.setView(camera);
        orthogonalTiledMapRenderer.render();

        // Create a list for all entities that need Y-sorting
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
        // Add any other entities that should be Y-sorted

        // Sort by Y position
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



        // Render all Y-sorted entities
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

        // Rest of your render method (lights, UI, etc.)
        rayHandler.setCombinedMatrix(camera.combined.cpy().scl(PPM));
        rayHandler.updateAndRender();

        // Render UI elements
        textBox.getStage().act(delta);
        textBox.getStage().draw();
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


    public void setPlayer(Player player) {
        this.player = player;
        enablePlayerLight();
    }




    public World getWorld() {
        return world;
    }

    public void flipDebugRendering() {
        debugRendering = !debugRendering;

    }
}