package objects.inanimate;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class Pond extends InanimateEntity {
    public static final int POND_1 = 0;
    public static final int POND_2 = 1;
    public static final int POND_3 = 2;
    public static final int POND_4 = 3;

    private final int pondType;
    private float stateTime;
    private Animation<TextureRegion>[] pondAnimations;
    private GameAssets gameAssets;

    @SuppressWarnings("unchecked")
    public Pond(float width, float height, Body body, ScreenInterface screenInterface, int pondType, int id, GameAssets gameAssets, GameContactListener gameContactListener) {
        super(width, height, body, screenInterface, id, gameAssets, gameContactListener);
        this.stateTime = 0f;
        this.pondType = pondType;
        this.gameAssets = gameAssets;
        pondAnimations = new Animation[4];
        initAnimations();
        setDepth(y);
    }

    @Override
    public void update(float delta) {
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        stateTime += delta;

    }

    private void initAnimations() {
        TextureAtlas atlas = gameAssets.getAtlas("atlases/eightfold/pond.atlas");
        loadAnimation(POND_1, atlas, 1, 4, "Pond");
        loadAnimation(POND_2, atlas, 1, 1, "Pond_2");
        loadAnimation(POND_3, atlas, 1, 1, "Pond_3");
        loadAnimation(POND_4, atlas, 1, 1, "Pond_4");
    }

    private void loadAnimation(int type, TextureAtlas atlas, int startFrame, int endFrame, String prefix) {
        Array<TextureRegion> frames = new Array<>();

        if (endFrame == 1) {
            TextureRegion region = atlas.findRegion(prefix);
            if (region == null) {
                //System.out.println("Region " + prefix + " not found!");
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

        pondAnimations[type] = new Animation<>(.5f, frames, Animation.PlayMode.LOOP);
    }

    @Override
    public void render(SpriteBatch batch) {
        Animation<TextureRegion> currentAnimation = pondAnimations[pondType];

        try {
            TextureRegion currentFrame = currentAnimation.getKeyFrame(stateTime, true);
            float x = body.getPosition().x * PPM - width / 2;
            float y = body.getPosition().y * PPM - height / 2;
            batch.draw(currentFrame, x, y, width, height);
        } catch (Exception e) {
            System.err.println("Error rendering pond frame: " + e.getMessage());
        }
    }
}
