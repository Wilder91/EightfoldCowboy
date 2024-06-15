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
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.eightfold.GameContactListener;

import objects.GameAssets;
import helper.tiledmap.TiledMapHelper;
import objects.player.Player;
import text.InfoBox;
import text.SaloonTextBox;

import static helper.Constants.PPM;

public class SaloonScreen extends ScreenAdapter {
    private final OrthographicCamera camera;
    private final SpriteBatch batch;
    private final GameScreen gameScreen;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private final TiledMapHelper tiledMapHelper;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;
    private Player player;
    private final GameContactListener gameContactListener;
    private final GameAssets gameAssets;

    // TextBox
    private SaloonTextBox textBox;
    private InfoBox infoBox;

    public SaloonScreen(OrthographicCamera camera, GameAssets gameAssets, GameContactListener gameContactListener, GameScreen gameScreen) {
        this.camera = camera;
        camera.zoom = 40f;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, 0), false);
        this.gameAssets = gameAssets;
        this.gameScreen = gameScreen;
        this.gameContactListener = gameContactListener;
        this.world.setContactListener(this.gameContactListener);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        this.tiledMapHelper = new TiledMapHelper(gameScreen, gameAssets, gameContactListener);
        this.orthogonalTiledMapRenderer = tiledMapHelper.setupMap("maps/InsideMap.tmx");

        // Initialize TextBox
        this.textBox = new SaloonTextBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")), "saloon/Saloon0.png");
        this.infoBox = new InfoBox(new Skin(Gdx.files.internal("commodore64/skin/uiskin.json")));
        Gdx.input.setInputProcessor(textBox.getStage());
        Gdx.input.setInputProcessor(infoBox.getStage());
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

    private void update(float delta) {
        world.step(1 / 60f, 6, 2);
        cameraUpdate();

        batch.setProjectionMatrix(camera.combined);
        orthogonalTiledMapRenderer.setView(camera);

        if (player != null) {
            player.update(delta);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
            enterPauseScreen();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            // Pause the game
            ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameAssets, gameScreen));
        }
    }

    public void enterPauseScreen(){
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

    @Override
    public void render(float delta) {
        update(delta); // Pass delta time to update method

        Gdx.gl.glClearColor(162f / 255f, 188f / 255f, 104f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        orthogonalTiledMapRenderer.setView(camera);
        orthogonalTiledMapRenderer.render();
        batch.begin();

        // Render game objects
        if (player != null) {
            player.render(batch);
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

    public void setPlayer(Player player) {
        this.player = player;
    }

    public World getWorld() {
        return world;
    }


}
