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

public class TimeOfDayScreen implements Screen {
    private OrthographicCamera camera;
    private GameScreen gameScreen;
    private GameAssets gameAssets;
    private Stage stage;
    private Skin skin;
    private int selectedIndex; // To keep track of the selected button
    private TextButton[] buttons; // Array to hold buttons

    public TimeOfDayScreen(OrthographicCamera camera, GameScreen gameScreen, GameAssets gameAssets, Skin skin) {
        this.camera = camera;
        this.gameScreen = gameScreen;
        this.gameAssets = gameAssets;
        this.skin = skin;

        // Initialize Scene2D Stage
        this.stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Create UI elements
        Label titleLabel = new Label("Time of Day", skin);
        TextButton dayButton = new TextButton("Day", skin);
        TextButton duskButton = new TextButton("Dusk", skin);
        TextButton nightButton = new TextButton("Night", skin);
        TextButton returnButton = new TextButton("Return", skin);

        // Store buttons in an array for keyboard navigation
        buttons = new TextButton[] {dayButton, duskButton, nightButton, returnButton};

        // Set up layout
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(titleLabel).padBottom(30).row();
        table.add(dayButton).width(200).padBottom(20).row();
        table.add(duskButton).width(200).padBottom(20).row();
        table.add(nightButton).width(200).padBottom(20).row();
        table.add(returnButton).width(200).padBottom(20).row();

        // Add table to the stage
        stage.addActor(table);

        // Initialize selectedIndex
        selectedIndex = 0;

        // Add button listeners
        dayButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setTimeOfDay("day");
            }
        });

        duskButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setTimeOfDay("dusk");
            }
        });

        nightButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setTimeOfDay("night");
            }
        });

        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                returnToGame();
            }
        });
    }

    /**
     * Sets the time of day in the game
     * @param timeOfDay The time of day: "day", "dusk", or "night"
     */
    private void setTimeOfDay(String timeOfDay) {
        // Tell the game screen to change the time of day
        if (gameScreen != null) {
            gameScreen.setTimeOfDay(timeOfDay);
            gameScreen.showInfoBox("Time changed to " + timeOfDay);
        }

        // Return to the game
        returnToGame();
    }

    private void returnToGame() {
        ((Game) Gdx.app.getApplicationListener()).setScreen(gameScreen);
    }

    @Override
    public void show() {
        // Reset the input processor when this screen is shown
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        // Clear the screen
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Handle keyboard input
        handleInput();

        // Update and draw the stage
        stage.act(delta);
        stage.draw();
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
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            returnToGame();
        }
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
        // Not used
    }

    @Override
    public void resume() {
        // Not used
    }

    @Override
    public void hide() {
        // Not used
    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}