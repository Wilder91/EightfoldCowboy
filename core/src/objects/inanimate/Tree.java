package objects.inanimate;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;

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
    public Tree(float width, float height, Body body, ScreenInterface screenInterface, int treeType, int id, GameAssets gameAssets, GameContactListener gameContactListener) {
        super(width, height, body, screenInterface, id, gameAssets, gameContactListener);
        this.stateTime = 0f;
        this.treeType = treeType;
        treeAnimations = new Animation[6]; // Array to hold animations for 6 tree types
        this.gameAssets = gameAssets;
        initAnimations();
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        stateTime += delta;
    }



    private void initAnimations() {
        TextureAtlas atlas = gameAssets.getAtlas("plants/trees/oak-trees.atlas");
        loadAnimation(LARGE_OAK, atlas, 1, 29, "Oak_Large");
        loadAnimation(MEDIUM_1, atlas, 1, 29, "Oak_Medium_1");
        loadAnimation(MEDIUM_2, atlas, 1, 29, "Oak_Medium_2");
        loadAnimation(SMALL, atlas, 1, 29, "Oak_Small");
        loadAnimation(JUVENILE, atlas, 1, 1, "Oak_Sapling");
        loadAnimation(SEEDLING, atlas, 1, 1, "Oak_Seedling");
    }

    private void loadAnimation(int treeType, TextureAtlas atlas, int startFrame, int endFrame, String prefix) {
        Array<TextureRegion> frames = new Array<>();

        // If endFrame is 1, we expect to render a still image
        if (endFrame == 1) {
            TextureRegion region = atlas.findRegion(prefix);
            if (region == null) {
                System.out.println("Region " + prefix + " not found!");
            } else {
                frames.add(region);
            }
        } else {
            for (int i = startFrame; i <= endFrame; i++) {
                TextureRegion region = atlas.findRegion(prefix, i);
                if (region == null) {
                    System.out.println("Region " + prefix + " " + i + " not found!");
                } else {
                    frames.add(region);
                }
            }
        }

        treeAnimations[treeType] = new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }


    @Override
    public void render(SpriteBatch batch) {
        // Select the appropriate animation based on the tree type
        Animation<TextureRegion> currentAnimation = treeAnimations[treeType];
//        System.out.println(currentAnimation);
//        System.out.println("StateTime: " + stateTime);


        // Get the current frame of the animation
        try {
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);

            // Draw the current frame at the tree's position
            // Convert the Box2D position from meters to pixels for rendering
            float x = body.getPosition().x * PPM - width / 2;
            float y = body.getPosition().y * PPM - height / 2;
            //System.out.println(treeType + width + " " + height);
            batch.draw(currentFrame, x, y, width, height);
        } catch (Exception e) {
            System.err.println("Error getting key frame: " + e.getMessage());
        }
    }


}
