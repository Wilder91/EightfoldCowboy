package com.mygdx.eightfold;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class StartScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;
    private Texture backgroundTexture;
    private BitmapFont font;

    public StartScreen(OrthographicCamera camera) {
        this.camera = camera;
        this.viewport = new FitViewport(800, 480, camera);
        this.stage = new Stage(viewport, new SpriteBatch());
        Gdx.input.setInputProcessor(stage);
        initUI();
    }

    private void initUI() {
        // Load assets
        skin = new Skin(Gdx.files.internal("commodore64/skin/uiskin.json"));
        backgroundTexture = new Texture(Gdx.files.internal("background.jpg"));
        font = new BitmapFont();

        // Background image
        Image background = new Image(backgroundTexture);
        background.setFillParent(true);
        stage.addActor(background);

        // Title label
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);
        Label titleLabel = new Label("The Eightfold Cowboy", labelStyle);
        titleLabel.setFontScale(2);
        titleLabel.setPosition(400 - titleLabel.getWidth() / 2, 300);
        stage.addActor(titleLabel);

        // Start button
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.setPosition(400 - startButton.getWidth() / 2, 200);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Transition to the game screen
                // For example: ((Game) Gdx.app.getApplicationListener()).setScreen(new GameScreen(camera));
            }
        });
        stage.addActor(startButton);
    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        backgroundTexture.dispose();
        font.dispose();
    }
}
