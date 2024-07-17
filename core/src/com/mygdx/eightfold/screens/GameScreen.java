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
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.GameAssets;
import helper.tiledmap.TiledMapHelper;
import objects.animals.bird.Bird;
import objects.animals.bison.Bison;
import objects.inanimate.Boulder;
import objects.inanimate.Building;
import objects.inanimate.Door;
import objects.inanimate.Tree;
import com.mygdx.eightfold.player.Player;
import text.infobox.InfoBox;
import text.textbox.BisonTextBox;
import text.textbox.TextBox;

import java.util.ArrayList;

import static helper.Constants.PPM;

public class GameScreen extends ScreenAdapter implements ScreenInterface {
    private final ArrayList<Bison> bisonList;
    private final ArrayList<Bird> birdList;
    private final ArrayList<Building> buildingList;
    private final ArrayList<Boulder> boulderList;
    private final ArrayList<Tree> treeList;
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
    private final GameAssets gameAssets;
    private boolean saloonTime = false;
    private Game game;
    public OrthographicCamera getCamera() {
        return camera;
    }



    // TextBox
    private TextBox textBox;
    private InfoBox infoBox;

    public GameScreen(OrthographicCamera camera, ScreenInterface screenInterface, GameAssets gameAssets, Game game) {
        this.screenInterface = screenInterface;
        this.buildingList = new ArrayList<>();
        this.camera = camera;
        this.bisonList = new ArrayList<>();
        this.birdList = new ArrayList<>();
        this.boulderList = new ArrayList<>();
        this.treeList = new ArrayList<>();
        this.doorList = new ArrayList<>();
        this.batch = new SpriteBatch();
        this.game = game;
        this.music = gameAssets.getMusic("lost & found.mp3");
        this.world = new World(new Vector2(0, 0), false);
        this.gameAssets = gameAssets;
        this.gameContactListener = new GameContactListener(this);
        this.world.setContactListener(this.gameContactListener);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.tiledMapHelper = new TiledMapHelper(this, gameAssets, gameContactListener);
        this.orthogonalTiledMapRenderer = tiledMapHelper.setupMap("maps/EightfoldMap.tmx");
        // Initialize TextBox
        this.textBox = new BisonTextBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")), "animals/bison/bison-single.png");
        this.infoBox = new InfoBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")));
        Gdx.input.setInputProcessor(textBox.getStage());
        Gdx.input.setInputProcessor(infoBox.getStage());
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

    public void hideTextBox() {
        textBox.hideTextBox();
    }

    public void showInfoBox(String text) {
        infoBox.showInfoBox(text);
    }

    public void hideInfoBox() {
        infoBox.hideInfoBox();
    }

    public void setSaloonTime(boolean time) {
        music.stop();
        System.out.println("CLOSE!");
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
        for (Door door : doorList) {
            door.update(delta);
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
            position.x = Math.round(player.getBody().getPosition().x * PPM * 10) / 10f;
            position.y = Math.round(player.getBody().getPosition().y * PPM * 10) / 10f;
            camera.position.set(position);
            camera.update();
        }
    }

    @Override
    public void addBison(Bison bison) {
        if (bisonList != null) {
            bisonList.add(bison);
        } else {
            System.err.println("bisonList is null. Cannot add bison.");
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

    public void removePlayerBody() {
        System.out.println("player body before : " + player.getBody());
        if (player != null && player.getBody() != null) {
            world.destroyBody(player.getBody());
            player.setBody(null);
            System.out.println("player body after: " + player.getBody());// Clear the reference to the old body
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
    public void render(float delta) {
        update(delta); // Pass delta time to update method

        Gdx.gl.glClearColor(162f / 255f, 188f / 255f, 104f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthogonalTiledMapRenderer.setView(camera);
        orthogonalTiledMapRenderer.render();
        music.setVolume(.1f);
        //music.play();
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
        for (Bison bison : bisonList) {
            bison.render(batch);
        }
        for (Boulder boulder : boulderList) {
            boulder.render(batch);
        }
        for (Tree tree : treeList) {
            tree.render(batch);
        }
        for (Door door : doorList) {
            door.render(batch);
        }
        batch.end();

        // Render the Stage
        textBox.getStage().act(delta);
        textBox.getStage().draw();
        infoBox.getStage().act(delta);
        infoBox.getStage().draw();

        // Uncomment for debugging physics bodies
       //box2DDebugRenderer.render(world, camera.combined.scl(PPM));
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
    }

    public void resetPlayer(Player player){
        player.createBody(world);
    }

    public void setPlayer(Player player) {
        this.player = player;
        if(player.getBody() == null) {

        }
    }



    public World getWorld() {
        return world;
    }

    public Game getGame() {
        return game;
    }
}
