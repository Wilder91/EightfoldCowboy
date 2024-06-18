package com.mygdx.eightfold.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
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
import com.mygdx.eightfold.Boot;
import com.mygdx.eightfold.GameAssets;

public class StartScreen extends ScreenAdapter {
    private OrthographicCamera camera;
    private Viewport viewport;
    private Stage stage;
    private SpriteBatch batch;
    private Skin skin;
    private Sound song;
    private Texture backgroundTexture;
    private BitmapFont font;
    private GameAssets gameAssets;
    private Music backgroundMusic;
    public StartScreen(OrthographicCamera camera, GameAssets gameAssets) {
        this.camera = camera;
        this.viewport = new FitViewport(camera.viewportWidth, camera.viewportHeight, camera);
        this.stage = new Stage(viewport, new SpriteBatch());
        this.gameAssets = gameAssets;
        this.camera = camera;

        Gdx.input.setInputProcessor(stage);

        initUI();
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("music/start_music.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.2f);
        backgroundMusic.play();

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
        titleLabel.setPosition(viewport.getScreenWidth() - titleLabel.getWidth() / 2, 300);
        stage.addActor(titleLabel);

        // Start button
        TextButton startButton = new TextButton("Start Game", skin);
        startButton.setPosition(viewport.getScreenWidth() - startButton.getWidth() / 2, 200);
        startButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                // Transition to the game screen
               // ((Game) Gdx.app.getApplicationListener())
                Boot.INSTANCE.changeScreen(new GameScreen(camera, gameAssets));
            }
        });
        stage.addActor(startButton);
    }

    private void checkInput(){
        if(Gdx.input.isKeyPressed(Input.Keys.ENTER)){
            Boot.INSTANCE.changeScreen(new GameScreen(camera, gameAssets));
        }


    }

    @Override
    public void render(float delta) {
        stage.act(delta);
        stage.draw();
        checkInput();
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
