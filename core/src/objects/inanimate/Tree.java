package objects.inanimate;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;

import objects.StationaryObjectAnimator;

import static helper.Constants.PPM;

public class Tree extends InanimateEntity {
    private final String textureName;
    private StationaryObjectAnimator animator;

    public Tree(Body body, ScreenInterface screenInterface, int id,
                String atlasName, String textureName, GameAssets gameAssets,
                GameContactListener gameContactListener, float hp) {
        super(0, 0, body, screenInterface, id, gameAssets, gameContactListener, hp);
        this.textureName = textureName;
        this.animator = new StationaryObjectAnimator(this, atlasName, textureName, gameAssets);
        TextureRegion frame = animator.getCurrentFrame();
        if (frame != null) {
            this.width = frame.getRegionWidth();
            this.height = frame.getRegionHeight();
        }

        setDepth(body.getPosition().y);
        setDepthOffset(-1000f);
    }

    private void calculateAndSetDepthOffset() {
        float depthOffset = -height / 4;
        setDepthOffset(depthOffset);
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        calculateAndSetDepthOffset();
        animator.update(delta);
        resetDepthToY();
    }

    @Override
    public void render(SpriteBatch batch) {
        animator.render(batch);
    }
}