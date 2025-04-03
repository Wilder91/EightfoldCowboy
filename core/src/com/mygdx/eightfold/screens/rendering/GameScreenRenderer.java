//package com.mygdx.eightfold.screens.rendering;
//
//import box2dLight.RayHandler;
//import com.badlogic.gdx.Gdx;
//import com.badlogic.gdx.graphics.GL20;
//import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.g2d.SpriteBatch;
//import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
//import com.mygdx.eightfold.player.Player;
//import com.mygdx.eightfold.screens.GameScreen;
//import objects.animals.bird.Bird;
//import objects.animals.bison.Bison;
//import objects.animals.bugs.Butterfly;
//import objects.animals.bugs.Dragonfly;
//import objects.humans.NPC;
//import objects.inanimate.*;
//
//import java.util.ArrayList;
//
//import static helper.Constants.PPM;
//
//public class GameScreenRenderer {
//    private final SpriteBatch batch;
//    private final OrthographicCamera camera;
//    private final OrthogonalTiledMapRenderer tiledMapRenderer;
//    private final RayHandler rayHandler;
//    private final Player player;
//
//    // Lists of game objects to render
//    private final ArrayList<Bison> bisonList;
//    private final ArrayList<Bird> birdList;
//    private final ArrayList<Building> buildingList;
//    private final ArrayList<Boulder> boulderList;
//    private final ArrayList<Tree> treeList;
//    private final ArrayList<Bush> bushList;
//    private final ArrayList<Rock> rockList;
//    private final ArrayList<Rock> rockTopList;
//    private final ArrayList<Pond> pondList;
//    private final ArrayList<Butterfly> butterflyList;
//    private final ArrayList<Dragonfly> dragonflyList;
//    private final ArrayList<NPC> NPCList;
//    private final ArrayList<Door> doorList;
//
//    public GameScreenRenderer(OrthographicCamera camera, SpriteBatch batch,
//                              OrthogonalTiledMapRenderer tiledMapRenderer,
//                              RayHandler rayHandler,
//                              Player player,
//                              GameScreen gameScreen) {
//        this.camera = camera;
//        this.batch = batch;
//        this.tiledMapRenderer = tiledMapRenderer;
//        this.rayHandler = rayHandler;
//        this.player = player;
//
//        // Get references to game objects
//        this.buildingList = gameScreen.getBuildingList();
//        this.bisonList = gameScreen.getBisonList();
//        this.birdList = gameScreen.getBirdList();
//        this.boulderList = gameScreen.getBoulderList();
//        this.treeList = gameScreen.getTreeList();
//        this.bushList = gameScreen.getBushList();
//        this.rockList = gameScreen.getRockList();
//        this.rockTopList = gameScreen.getRockTopList();
//        this.pondList = gameScreen.getPondList();
//        this.butterflyList = gameScreen.getButterflyList();
//        this.dragonflyList = gameScreen.getDragonflyList();
//        this.NPCList = gameScreen.getNPCList();
//        this.doorList = gameScreen.getDoorList();
//    }
//
//    public void render(float delta) {
//        // Clear the screen
//        Gdx.gl.glClearColor(168f / 255f, 178f / 255f, 113f / 255f, 1);
//        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
//
//        // Set up the tiled map renderer to follow the camera
//        tiledMapRenderer.setView(camera);
//        tiledMapRenderer.render();
//
//        // Begin drawing with the SpriteBatch
//        batch.begin();
//
//        // Render all game elements in the correct order
//        renderBackgroundElements();
//        renderMidLevelElements();
//        renderForegroundElements();
//
//        batch.end();
//
//        // Render lighting
//        rayHandler.setCombinedMatrix(camera.combined.cpy().scl(PPM));
//        rayHandler.updateAndRender();
//    }
//
//    private void renderBackgroundElements() {
//        // 1. Render buildings and ponds (background elements)
//        for (Building building : buildingList) {
//            building.render(batch);
//        }
//
//        for (Boulder boulder : boulderList) {
//            boulder.render(batch);
//        }
//
//        for (Pond pond : pondList) {
//            pond.render(batch);
//        }
//
//        // 2. Render NPCs - should be behind player
//        for (NPC npc : NPCList) {
//            npc.render(batch);
//        }
//
//        // 3. Render the bottom part of trees
//        for (Tree tree : treeList) {
//            tree.renderBottom(batch);
//        }
//
//        // 4. Render the bottom part of rocks
//        for (Rock rock : rockList) {
//            rock.renderBottom(batch);
//        }
//    }
//
//    private void renderMidLevelElements() {
//        // 5. Render the player (mid-level elements)
//        if (player != null) {
//            player.render(batch);
//        }
//
//        // 6. Render dynamic entities like butterflies, dragonflies, and bush
//        for (Butterfly butterfly : butterflyList) {
//            butterfly.render(batch);
//        }
//
//        for (Dragonfly dragonfly : dragonflyList) {
//            dragonfly.render(batch);
//        }
//
//        for (Bush bush : bushList) {
//            bush.render(batch);
//        }
//
//        // 7. Render birds and bison
//        for (Bird bird : birdList) {
//            bird.render(batch);
//        }
//
//        for (Bison bison : bisonList) {
//            bison.render(batch);
//        }
//    }
//
//    private void renderForegroundElements() {
//        // 8. Render the top part of rocks (foreground elements)
//        for (Rock rock : rockList) {
//            rock.renderTop(batch);
//        }
//
//        // 9. Render the top part of trees
//        for (Tree tree : treeList) {
//            tree.renderTop(batch);
//        }
//
//        // 10. Render doors last
//        for (Door door : doorList) {
//            door.render(batch);
//        }
//    }
//
//    // Helper method to render UI elements if needed
//    public void renderUI(float delta, GameScreen gameScreen) {
//        // Render the UI elements (TextBox, DecisionTextBox and InfoBox)
//        gameScreen.getTextBox().getStage().act(delta);
//        gameScreen.getTextBox().getStage().draw();
//
//        gameScreen.getDecisionTextBox().getStage().act(delta);
//        gameScreen.getDecisionTextBox().getStage().draw();
//
//        gameScreen.getInfoBox().getStage().act(delta);
//        gameScreen.getInfoBox().getStage().draw();
//    }
// }