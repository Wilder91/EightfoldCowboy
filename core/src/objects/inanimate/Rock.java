package objects.inanimate;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.debugging.DepthVisualizer;
import objects.StationaryObjectAnimator;

import static helper.Constants.PPM;

public class Rock extends InanimateEntity {
    private final String atlasName;
    private final String textureName;
    private final GameAssets gameAssets;
    private StationaryObjectAnimator animator;
    private static final int DOT_SIZE = 6; // Size of the indicator dots
    private float stateTime;

    public Rock(Body body, ScreenInterface screenInterface, int id,
                String atlasName, String textureName, GameAssets gameAssets,
                GameContactListener gameContactListener) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener);
        this.stateTime = 0;
        this.atlasName = atlasName;
        this.textureName = textureName;
        this.gameAssets = gameAssets;

        // Create the animator using StationaryObjectAnimator
        this.animator = new StationaryObjectAnimator(this, atlasName, textureName, gameAssets);
        if (animator.getCurrentFrame() != null) {
            this.width = animator.getCurrentFrame().getRegionWidth();
            this.height = animator.getCurrentFrame().getRegionHeight();

        } else {
            System.err.println("Failed to get texture for rock: " + textureName);
        }
        // Set depth for proper rendering order
        setDepth(body.getPosition().y);
        setDepthOffset(-1000f);
        DepthVisualizer.toggleVisualization();
    }

    private void calculateAndSetDepthOffset() {
        // Calculate depth offset for proper layering
        float depthOffset = -height / 6;
        setDepthOffset(depthOffset);
    }

    @Override
    public void update(float delta) {
        // Update position based on physics body
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        // Update depth for proper layering
        resetDepthToY();
        calculateAndSetDepthOffset();

        // Update animation
        stateTime += delta;
        animator.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {

        // Use the animator to render the rock
        animator.render(batch);
    }

    public StationaryObjectAnimator getAnimator() {
        return animator;
    }
}