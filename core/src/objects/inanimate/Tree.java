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


    private final TextureRegion treeTexture;

    private final GameAssets gameAssets;

    private static final int DOT_SIZE = 6; // Size of the indicator dots

    public Tree(Body body, ScreenInterface screenInterface, int id,
                TextureRegion treeTexture, GameAssets gameAssets,
                GameContactListener gameContactListener) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener);

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