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
import objects.StationaryObjectAnimator;

import static helper.Constants.PPM;

public class Tree extends InanimateEntity {
    private final String textureName;
    private StationaryObjectAnimator animator;

    public Tree(Body body, ScreenInterface screenInterface, int id,
                String atlasName, String textureName, GameAssets gameAssets,
                GameContactListener gameContactListener) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener);
        this.textureName = textureName;

        // Create the animator - simple like the bug version
        this.animator = new StationaryObjectAnimator(this, atlasName, textureName, gameAssets);

        // Update width/height from the loaded texture
        TextureRegion frame = animator.getCurrentFrame();
        if (frame != null) {
            this.width = frame.getRegionWidth();
            this.height = frame.getRegionHeight();
        }
        setDepth(body.getPosition().y);
        setDepthOffset(-1000f);
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
        calculateAndSetDepthOffset();
        animator.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        animator.render(batch);
    }
}