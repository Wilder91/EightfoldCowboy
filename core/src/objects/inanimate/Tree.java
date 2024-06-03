package objects.inanimate;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameScreen;
import objects.GameAssets;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class Tree extends InanimateEntity {
    // Define constants for tree types
    public static final int LARGE_OAK = 0;
    public static final int MEDIUM_1 = 1;
    public static final int MEDIUM_2 = 2;
    public static final int SMALL = 3;
    public static final int JUVENILE = 4;
    public static final int SEEDLING = 5;
    private final int treeType;
    private float stateTime;
    private Animation<TextureRegion>[] treeAnimations;
    private GameAssets gameAssets;
    @SuppressWarnings("unchecked")
    public Tree(float width, float height, Body body, GameScreen gameScreen, int treeType, int id) {
        super(width, height, body, gameScreen, id);
        this.stateTime = 0f;
        this.treeType = treeType;
        treeAnimations = new Animation[6]; // Array to hold animations for 6 tree types
        initAnimations();
        this.gameAssets = new GameAssets();
        gameAssets.loadAssets();
        gameAssets.finishLoading();
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        stateTime += delta;

    }

    @Override
    public void update() {
        // Optional: Implement additional update logic if needed
    }

    private void initAnimations() {
        loadAnimation(LARGE_OAK, "plants/trees/atlases/oak-trees.atlas", 1, 29);
        loadAnimation(MEDIUM_1, "plants/trees/atlases/oak-trees.atlas", 30, 58);
        loadAnimation(MEDIUM_2, "plants/trees/atlases/oak-trees.atlas", 59, 67);
        loadAnimation(SMALL, "plants/trees/atlases/oak-trees.atlas", 68, 116);
        loadAnimation(JUVENILE, "plants/trees/atlases/oak-trees.atlas", 117, 117);
        loadAnimation(SEEDLING, "plants/trees/atlases/oak-trees.atlas", 118, 118);
    }

    private void loadAnimation(int treeType, String atlasPath, int startFrame, int endFrame) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = new TextureAtlas(atlasPath);
        for (int i = startFrame; i <= endFrame; i++) {

            TextureRegion region = atlas.findRegion("oak-trees-" + treeType + "-" + i);
            if (region == null) {
                //System.out.println("Region oak-trees-" + treeType + "-" + i + " not found!");
            } else {
                frames.add(region);

            }
        }
        treeAnimations[treeType] = new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    @Override
    public void render(SpriteBatch batch) {
        // Select the appropriate animation based on the tree type
        Animation<TextureRegion> currentAnimation = treeAnimations[treeType];

        // Get the current frame of the animation
        TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

        // Draw the current frame at the tree's position
        // Convert the Box2D position from meters to pixels for rendering
        float x = body.getPosition().x * PPM - width / 2;
        float y = body.getPosition().y * PPM - height / 2;

        batch.draw(currentFrame, x, y, width, height);
    }
}
