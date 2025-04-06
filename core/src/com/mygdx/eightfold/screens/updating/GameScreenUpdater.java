//package com.mygdx.eightfold.screens.updating;

import box2dLight.PointLight;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.screens.PauseScreen;
import com.mygdx.eightfold.screens.SaloonScreen;
import objects.animals.bird.Bird;

import objects.animals.bugs.Butterfly;
import objects.animals.bugs.Dragonfly;
import objects.humans.NPC;
import objects.inanimate.*;

import java.util.ArrayList;

import static helper.Constants.PPM;

//public class GameScreenUpdater {
//    private final World world;
//    private final OrthographicCamera camera;
//    private final Player player;
//    private final PointLight playerLight;
//    private final GameScreen gameScreen;

//    // Lists of game objects to update
//    private final ArrayList<Bison> bisons;
//    private final ArrayList<Bird> birds;
//    private final ArrayList<Building> buildings;
//    private final ArrayList<Boulder> boulders;
//    private final ArrayList<Tree> trees;
//    private final ArrayList<Bush> bushes;
//    private final ArrayList<Rock> rocks;
//    private final ArrayList<Rock> rockTops;
//    private final ArrayList<Pond> ponds;
//    private final ArrayList<Butterfly> butterflies;
//    private final ArrayList<Dragonfly> dragonflies;
//    private final ArrayList<NPC> npcs;
//    private final ArrayList<Door> doors;

//    public GameScreenUpdater(World world, OrthographicCamera camera,
//                             Player player, PointLight playerLight,
//                             GameScreen gameScreen) {
//        this.world = world;
//        this.camera = camera;
//        this.player = player;
//        this.playerLight = playerLight;
//        this.gameScreen = gameScreen;
//
//        // Get references to game objects
//        this.bisons = gameScreen.getBisonList();
//        this.birds = gameScreen.getBirdList();
//        this.buildings = gameScreen.getBuildingList();
//        this.boulders = gameScreen.getBoulderList();
//        this.trees = gameScreen.getTreeList();
//        this.bushes = gameScreen.getBushList();
//        this.rocks = gameScreen.getRockList();
//        this.rockTops = gameScreen.getRockTopList();
//        this.ponds = gameScreen.getPondList();
//        this.butterflies = gameScreen.getButterflyList();
//        this.dragonflies = gameScreen.getDragonflyList();
//        this.npcs = gameScreen.getNPCList();
//        this.doors = gameScreen.getDoorList();
//    }
//
//    public void update(float delta) {
//        // Update physics
//        world.step(1 / 60f, 6, 2);
//
//        // Update camera
//        cameraUpdate();
//
//        // Update player light position
//        updatePlayerLight();
//
//        // Update game objects
//        updateGameObjects(delta);
//
//        // Check for input
//        checkForInput();
//
//        // Check if it's time to transition to saloon
//        checkSaloonTransition();
//
//        // Update UI components
//        updateUI(delta);
//    }
//
//    private void cameraUpdate() {
//        if (player != null) {
//            Vector3 position = camera.position;
//            Vector2 target = player.getBody().getPosition().scl(PPM);
//            position.x += (target.x - position.x) * 0.1f; // smoothing factor
//            position.y += (target.y - position.y) * 0.1f;
//            camera.position.set(position);
//            camera.update();
//        }
//    }
//
//    private void updatePlayerLight() {
//        if (player != null && playerLight != null) {
//            Vector2 pos = player.getBody().getPosition();
//            playerLight.setPosition(pos.x + .1f, pos.y);
//        }
//    }
//
//    private void updateGameObjects(float delta) {
//        // Update all game objects
//        if (player != null) {
//            player.update(delta);
//        }
//
//        // Update animals
//        for (Bison bison : bisons) {
//            bison.update(delta);
//        }
//
//        for (Bird bird : birds) {
//            bird.update(delta);
//        }
//
//        for (Butterfly butterfly : butterflies) {
//            butterfly.update(delta);
//        }
//
//        for (Dragonfly dragonfly : dragonflies) {
//            dragonfly.update(delta);
//        }
//
//        // Update NPCs
//        for (NPC npc : npcs) {
//            npc.update(delta);
//        }
//
//        // Update environment objects
//        for (Tree tree : trees) {
//            tree.update(delta);
//        }
//
//        for (Boulder boulder : boulders) {
//            boulder.update(delta);
//        }
//
//        for (Pond pond : ponds) {
//            pond.update(delta);
//        }
//
//        for (Building building : buildings) {
//            building.update(delta);
//        }
//
//        for (Door door : doors) {
//            door.update(delta);
//        }
//
//
//    }
//
//    private void checkForInput() {
//        // Check for escape key to pause
//        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)) {
//            gameScreen.enterPauseScreen();
//        }
//
//        // Check for P key to pause (alternative)
//        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
//            ((Game) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(camera, gameScreen.getGameAssets(), gameScreen));
//        }
//    }
//
//    private void checkSaloonTransition() {
//        if (gameScreen.isSaloonTime()) {
//            World newWorld = new World(new Vector2(0, 0), false); // Create a new World instance for the new screen
//            SaloonScreen saloonScreen = new SaloonScreen(camera, gameScreen.getGameAssets(), gameScreen, newWorld, gameScreen, player, gameScreen.getGame());
//            // Use new instances
//            ((Game) Gdx.app.getApplicationListener()).setScreen(saloonScreen);
//            gameScreen.updateDoorScreenReferences(saloonScreen);
//        }
//    }
//
//    private void updateUI(float delta) {
//        gameScreen.getTextBox().resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
//    }
//}