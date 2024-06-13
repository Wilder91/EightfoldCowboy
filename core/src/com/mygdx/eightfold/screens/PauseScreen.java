package com.mygdx.eightfold.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
import objects.GameAssets;

import static helper.Constants.PPM;

public class PauseScreen implements Screen {
    private OrthographicCamera camera;
    private GameAssets gameAssets;
    private GameScreen gameScreen;
    private Stage stage;
    private Skin skin;
    private int selectedIndex; // To keep track of the selected button
    private TextButton[] buttons; // Array to hold buttons

    public PauseScreen(OrthographicCamera camera, GameAssets gameAssets, GameScreen gameScreen) {
        this.camera = camera;
        this.gameAssets = gameAssets;
        this.gameScreen = gameScreen;

        // Initialize Scene2D Stage and Skin
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage); // Set input processor to the stage
        this.skin = new Skin(Gdx.files.internal("vhs/skin/vhs-ui.json"));

        // Set up the camera
        camera.setToOrtho(false, (Gdx.graphics.getWidth() / PPM), (Gdx.graphics.getHeight() / PPM)); // Set your game width and height

        // Create UI elements
        Label pauseLabel = new Label("Paused", skin);
        TextButton resumeButton = new TextButton("Resume", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Store buttons in an array
        buttons = new TextButton[]{resumeButton, exitButton};

        // Set up layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(pauseLabel).padBottom(20).row();
        table.add(resumeButton).padBottom(20).row();
        table.add(exitButton).padBottom(20).row();
        // Add table to the stage
        stage.addActor(table);

        // Initialize selectedIndex
        selectedIndex = 0;
        //updateButtonStyles();

        // Add listeners to buttons
        resumeButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                resumeGame();
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
            //updateButtonStyles();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedIndex = (selectedIndex + 1) % buttons.length;
           // updateButtonStyles();
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            buttons[selectedIndex].toggle();
            buttons[selectedIndex].getClickListener().clicked(null, 0, 0);
        }
    }

//    private void updateButtonStyles() {
//        for (int i = 0; i < buttons.length; i++) {
//            if (i == selectedIndex) {
//                buttons[i].getStyle().up = skin.newDrawable("button-over"); // Add arrow icon drawable if needed
//                buttons[i].getLabel().setColor(Color.YELLOW); // Highlight selected button
//            } else {
//                buttons[i].getStyle().up = skin.newDrawable("button");
//                buttons[i].getLabel().setColor(Color.WHITE); // Default color
//            }
//        }
//    }
private void resumeGame() {
    gameScreen.resetCamera();
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
