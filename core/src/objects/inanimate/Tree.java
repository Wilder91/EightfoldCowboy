package objects.inanimate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.debugging.DepthVisualizer;

import static helper.Constants.PPM;

public class Tree extends InanimateEntity {
    public static final int LARGE_OAK = 0;
    public static final int MEDIUM_1 = 1;
    public static final int MEDIUM_2 = 2;
    public static final int SMALL = 3;
    public static final int JUVENILE = 4;
    public static final int SEEDLING = 5;
    public static final int ASPEN_ONE = 6;
    public static final int ASPEN_TWO = 7;
    public static final int ASPEN_THREE = 8;
    public static final int ASPEN_BABY = 9;
    public static final int ASPEN_YOUNG = 10;
    public static final int ASPEN_STUMP = 11;

    private final TextureRegion treeTexture;
    private final int treeType;
    private final GameAssets gameAssets;

    private static final int DOT_SIZE = 6; // Size of the indicator dots

    public Tree(Body body, ScreenInterface screenInterface, int treeType, int id,
                TextureRegion treeTexture, GameAssets gameAssets,
                GameContactListener gameContactListener) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener);
        this.treeType = treeType;
        this.treeTexture = treeTexture;
        this.gameAssets = gameAssets;

        // Use texture size for width/height
        this.width = treeTexture.getRegionWidth();
        this.height = treeTexture.getRegionHeight();
        setDepth(body.getPosition().y);
        setDepthOffset(-1000f);
        DepthVisualizer.toggleVisualization();
    }



    private void calculateAndSetDepthOffset() {
        // We want to move 2/3 of the way from center to bottom
        // That's a distance of (height/2) * (2/3) = height/3
        float depthOffset = -height / 4;
        setDepthOffset(depthOffset);
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        resetDepthToY();
        calculateAndSetDepthOffset();
    }



    @Override
    public void render(SpriteBatch batch) {
        if (treeTexture != null) {
            // Draw the tree
            float drawX = x - width / 2;
            float drawY = y - height / 2;
            batch.draw(treeTexture, drawX, drawY, width, height);


        } else {
            System.err.println("Tree texture is null!");
        }
    }



}