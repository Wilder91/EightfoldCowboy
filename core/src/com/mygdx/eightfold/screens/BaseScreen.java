package com.mygdx.eightfold.screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import text.infobox.InfoBox;
import text.textbox.BisonTextBox;

public abstract class BaseScreen extends ScreenAdapter implements Screen {
    protected OrthographicCamera camera;
    protected SpriteBatch batch;
    protected World world;
    protected Box2DDebugRenderer box2DDebugRenderer;
    protected BisonTextBox textBox;
    protected InfoBox infoBox;
    protected Game game;

    public BaseScreen(OrthographicCamera camera, Game game) {
        this.camera = camera;
        this.game = game;
        this.batch = new SpriteBatch();
        this.world = new World(new Vector2(0, 0), false);
        this.box2DDebugRenderer = new Box2DDebugRenderer();
        Skin skin = new Skin(Gdx.files.internal("commodore64/skin/uiskin.json"));
        this.textBox = new BisonTextBox(skin, "animals/bison/bison-single.png");
        this.infoBox = new InfoBox(skin);
        Gdx.input.setInputProcessor(textBox.getStage());
        Gdx.input.setInputProcessor(infoBox.getStage());
    }

    @Override
    public void show() {}

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(162f / 255f, 188f / 255f, 104f / 255f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        world.dispose();
        //box2DDebugRenderer.dispose();
        textBox.getStage().dispose();
        textBox.getSkin().dispose();
        infoBox.getStage().dispose();
    }
}
