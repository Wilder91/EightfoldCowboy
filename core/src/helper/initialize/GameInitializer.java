package helper.initialize;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.eightfold.GameAssets;
import helper.Constants;
import helper.tiledmap.TiledMapHelper;

import static helper.Constants.PPM;

public class GameInitializer {

    private OrthographicCamera camera;
    private SpriteBatch batch;
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private OrthogonalTiledMapRenderer orthogonalTiledMapRenderer;

    public void initialize(GameAssets gameAssets) {
        // Initialize camera
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 800, 480);

        // Initialize batch
        batch = new SpriteBatch();

        // Initialize world
        world = new World(new Vector2(0, 0), false);

        // Initialize Box2DDebugRenderer (for debugging)
        box2DDebugRenderer = new Box2DDebugRenderer();

        // Initialize TiledMapHelper (assuming it's used for rendering maps)
       // TiledMapHelper tiledMapHelper = new TiledMapHelper(/* pass necessary parameters */);
        //orthogonalTiledMapRenderer = tiledMapHelper.setupMap("maps/EightfoldMap.tmx");

        // Load music and other assets
        // Example:
        // Music music = gameAssets.getMusic("music_file.mp3");
    }

    public OrthographicCamera getCamera() {
        return camera;
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public World getWorld() {
        return world;
    }

    public Box2DDebugRenderer getBox2DDebugRenderer() {
        return box2DDebugRenderer;
    }

    public OrthogonalTiledMapRenderer getOrthogonalTiledMapRenderer() {
        return orthogonalTiledMapRenderer;
    }

    public void dispose() {
        batch.dispose();
        world.dispose();
        //box2DDebugRenderer.dispose();
        orthogonalTiledMapRenderer.dispose();
    }
}