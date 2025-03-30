package com.mygdx.eightfold;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import box2dLight.RayHandler;
import box2dLight.PointLight;
import com.badlogic.gdx.graphics.Color;

public class LightTestApp extends ApplicationAdapter {
    private OrthographicCamera camera;
    private World world;
    private RayHandler rayHandler;

    @Override
    public void create() {
        world = new World(new Vector2(0, 0), false);
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480); // pixels
        camera.update();

        RayHandler.useDiffuseLight(true);
        rayHandler = new RayHandler(world);
        rayHandler.setAmbientLight(0f, 0f, 0f, 1f); // no ambient, only light
        rayHandler.setShadows(true);
        rayHandler.setCulling(false);
        rayHandler.setBlur(true);

        // Create a red test light in world units (Box2D scale)
        new PointLight(rayHandler, 128, Color.RED, 5f, 25f, 10f); // try moving these values if off screen
    }

    @Override
    public void render() {
        world.step(1/60f, 6, 2);
        camera.update();

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        rayHandler.setCombinedMatrix(camera.combined.cpy().scl(1f / 32f)); // scale to Box2D world
        rayHandler.updateAndRender();
    }

    @Override
    public void dispose() {
        rayHandler.dispose();
        world.dispose();
    }
}

