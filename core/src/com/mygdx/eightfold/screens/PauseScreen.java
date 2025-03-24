package com.mygdx.eightfold.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.mygdx.eightfold.GameAssets;

import static helper.Constants.PPM;

public class PauseScreen implements Screen {
    private OrthographicCamera camera;
    private GameAssets gameAssets;
    private GameScreen gameScreen;
    private Stage stage;
    private Skin skin;
    private int selectedIndex; // To keep track of the selected button
    private TextButton[] buttons; // Array to hold buttons
    private InventoryScreen inventoryScreen;

    public PauseScreen(OrthographicCamera camera, GameAssets gameAssets, GameScreen gameScreen) {
        this.camera = camera;
        this.gameAssets = gameAssets;
        this.gameScreen = gameScreen;
        this.skin = new Skin(Gdx.files.internal("vhs/skin/vhs-ui.json"));
        this.inventoryScreen = new InventoryScreen(camera, gameScreen, gameAssets, skin);
        // Initialize Scene2D Stage and Skin
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Set input processor to the stage
        // Create UI elements
        Label pauseLabel = new Label("Paused", skin);
        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton inventoryButton = new TextButton("Inventory", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Store buttons in an array
        buttons = new TextButton[]{resumeButton, inventoryButton, exitButton};

        // Set up layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(pauseLabel).padBottom(20).row();
        table.add(resumeButton).padBottom(20).row();
        table.add(inventoryButton).padBottom(20).row();
        table.add(exitButton).padBottom(20).row();
        // Add table to the stage
        stage.addActor(table);

        // Initialize selectedIndex
        selectedIndex = 0;

        // Add listeners to buttons
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resumeGame();
            }
        });

        inventoryButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                ((Game) Gdx.app.getApplicationListener()).setScreen(inventoryScreen);
            }
        });

        exitButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show() {
        // Initialization code, if needed
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();

        // Handle input
        handleInput();

        // Render the Stage
        stage.act(delta);
        stage.draw();

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            // Resume the game
            resumeGame();
        }
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedIndex = (selectedIndex - 1 + buttons.length) % buttons.length;
            // updateButtonStyles();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % buttons.length;
            // updateButtonStyles();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            buttons[selectedIndex].toggle();
            buttons[selectedIndex].getClickListener().clicked(null, 0, 0);
        }
    }

    private void resumeGame() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(gameScreen);
    }

    @Override
    public void resize(int width, int height) {
        // Update the viewport
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Handle the pause event
    }

    @Override
    public void resume() {
        // Handle the resume event
    }

    @Override
    public void hide() {
        // Handle when this screen is hidden
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
