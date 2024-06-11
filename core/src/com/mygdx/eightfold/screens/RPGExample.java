package com.mygdx.eightfold.screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class RPGExample extends ApplicationAdapter {
    private Stage stage;
    private Skin skin;

    @Override
    public void create() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Load the skin
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Set up the menu
        setupMenu();
    }

    private void setupMenu() {
        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        // Create buttons
        TextButton startButton = new TextButton("Start Game", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Add buttons to the table
        table.add(startButton).fillX().uniformX();
        table.row().pad(10, 0, 10, 0);
        table.add(optionsButton).fillX().uniformX();
        table.row();
        table.add(exitButton).fillX().uniformX();

        // Add listeners to buttons
        startButton.addListener(event -> {
            if (startButton.isPressed()) {
                // Start game logic here
                System.out.println("Start Game pressed");
            }
            return false;
        });

        optionsButton.addListener(event -> {
            if (optionsButton.isPressed()) {
                // Options logic here
                System.out.println("Options pressed");
            }
            return false;
        });

        exitButton.addListener(event -> {
            if (exitButton.isPressed()) {
                Gdx.app.exit();
            }
            return false;
        });
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
