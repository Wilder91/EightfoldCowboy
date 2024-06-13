package com.mygdx.eightfold.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.eightfold.GameContactListener;
import objects.GameAssets;
import helper.tiledmap.TiledMapHelper;
import objects.animals.bird.Bird;
import objects.animals.bison.Bison;
import objects.inanimate.Boulder;
import objects.inanimate.Building;
import objects.inanimate.Door;
import objects.inanimate.Tree;
import objects.player.Player;
import java.util.ArrayList;

import static helper.Constants.PPM;

public class GameScreen extends ScreenAdapter {
    private final ArrayList<Bison> bisonList;
    private final ArrayList<Bird> birdList;
    private final ArrayList<Building> buildingList;
    private final ArrayList<Boulder> boulderList;
    private final ArrayList<Tree> treeList;
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final ArrayList<Door> doorList;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private final TiledMapHelper tiledMapHelper;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private Player player;
    private Label textLabel;
    private Label doorLabel;
    private final GameContactListener gameContactListener;
    private final GameAssets gameAssets;

    // Scene2D
    private Stage stage;
    private Skin skin;

    public GameScreen(OrthographicCamera camera, GameAssets gameAssets) {

        this.buildingList = new ArrayList<>();
        this.camera = camera;
        camera.zoom = 80f;
        this.bisonList = new ArrayList<>();
        this.birdList = new ArrayList<>();
        this.boulderList = new ArrayList<>();
        this.treeList = new ArrayList<>();
        this.doorList = new ArrayList<>();
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, 0), false);
        this.gameAssets = gameAssets;

        this.gameContactListener = new GameContactListener(this);
        this.world.setContactListener(this.gameContactListener);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.tiledMapHelper = new TiledMapHelper(this, gameAssets);
        this.orthogonalTiledMapRenderer = tiledMapHelper.setupMap();

        // Initialize Scene2D Stage
        this.skin = new Skin(Gdx.files.internal("vhs/skin/vhs-ui.json"));
        this.stage = new Stage(new ScreenViewport());
        this.textLabel = new Label("", skin);
        this.textLabel.setVisible(false);
        this.doorLabel = new Label("", skin);
        this.doorLabel.setVisible(false);

        Gdx.input.setInputProcessor(stage); // Set input processor to the stage

        // Initialize Skin (you should load an actual skin file here)
        this.skin = new Skin(Gdx.files.internal("commodore64/skin/uiskin.json"));

        // Create UI elements
        //Table table = new Table();
        //table.setFillParent(true);
        //table.add(textLabel).center().expand();
        stage.addActor(textLabel);

        // Create a button

    }

    public void showTextBox(String text, float x, float y) {
       // System.out.println("textbox");
        textLabel.setText(text);
        textLabel.setPosition(x, y);
        textLabel.setVisible(true);
    }

    public void showDoorBox(String text, float x, float y) {
        System.out.println("doorbox");
        doorLabel.setText(text);
        doorLabel.setPosition(x, y);
        doorLabel.setVisible(true);
    }

    public void hideTextBox() {
        textLabel.setVisible(false);
    }

    public void hideDoorBox() {
        doorLabel.setVisible(false);
    }


    private void update(float delta) {
        world.step(1 / 60f, 6, 2);
        cameraUpdate();

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);
        for (Tree tree : treeList) {
            tree.update(delta);
        }

        if (player != null) {
            player.update(delta);
        }

        for (Bison bison : bisonList) {
            bison.update(delta);
        }
        for (Bird bird : birdList) {
            bird.update(delta);
        }
        for (Boulder boulder : boulderList) {
            boulder.update(delta);
        }

        for (Building building : buildingList) {
            building.update(delta);
        }
        for (Door door : doorList){
            door.update(delta);

        }



        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameAssets, this));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            // Pause the game
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameAssets, this));
        }
    }

    public void resetCamera() {
        camera.zoom = 80f; // Or whatever your initial zoom level is
        camera.update();
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

    public void addBison(Bison bison) {
        if (bisonList != null) {
            //System.out.println(bison);
            bisonList.add(bison);
        } else {
            System.err.println("bisonList is null. Cannot add bison.");
        }
    }

    public void addDoor(Door door) {
        if (doorList != null) {
            //System.out.println(bison);
            System.out.println(door.getId());
            doorList.add(door);
        } else {
            System.err.println("bisonList is null. Cannot add bison.");
        }
    }


    public void addBird(Bird bird) {
        if (birdList != null) {
            birdList.add(bird);
        } else {
            System.err.println("birdList is null. Cannot add bird.");
        }
    }

    public void addBoulder(Boulder boulder) {
        if (boulderList != null) {
            boulderList.add(boulder);
        }
    }
    public void addBuilding(Building building) {
        if (buildingList != null) {
            buildingList.add(building);
        }
    }

    public void addTree( Tree tree) {
        if (treeList != null) {
            treeList.add(tree);
        }
    }


    // Other existing methods...

    @Override
    public void render(float delta) {
        update(delta); // Pass delta time to update method

        Gdx.gl.glClearColor(162f / 255f, 188f / 255f, 104f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthogonalTiledMapRenderer.setView(camera);
        orthogonalTiledMapRenderer.render();
        batch.begin();

        // Render game objects
        for (Building building : buildingList) {
            building.render(batch);
        }
        if (player != null) {
            player.render(batch);
        }
        for (Bird bird : birdList) {
            bird.render(batch);
        }
        for (Bison bison : bisonList){
            bison.render(batch);
        }
        for (Boulder boulder : boulderList) {
            boulder.render(batch);
        }
        for (Tree tree : treeList) {
            tree.render(batch);
        }
        for (Door door : doorList){
            door.render(batch);
        }
        batch.end();

        // Render the Stage
        stage.act(delta);
        stage.draw();

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
        stage.dispose();
        skin.dispose();
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public World getWorld() {
        return world;
    }

    public void conversationScreen(int id) {
        System.out.println("E PRESSED AGAIn");

        ((Game) Gdx.app.getApplicationListener()).setScreen(new BisonConversationScreen(camera, gameAssets, this,  id));
    }
}



