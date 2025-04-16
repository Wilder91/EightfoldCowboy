package objects;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import helper.EntityAnimator;

public class StationaryObjectAnimator extends EntityAnimator {
    private Animation<TextureRegion> animation;
    private TextureRegion singleFrame; // For single-frame objects
    private String objectName;
    private float stateTime = 0f;
    private GameEntity entity;
    private boolean isAnimated; // Flag to determine if object has animation or just a single frame

    public StationaryObjectAnimator(GameEntity stationaryObject, String atlasName, String objectName, GameAssets gameAssets) {
        super(stationaryObject);
        this.entity = stationaryObject;
        this.objectName = objectName;

        TextureAtlas atlas = gameAssets.getAtlas("atlases/eightfold/" + atlasName + ".atlas");
        Array<TextureRegion> frames = new Array<>();

        // Try to find frames for animation
        for (int i = 1; i <= 4; i++) {
            TextureRegion region = atlas.findRegion(objectName, i);
            if (region != null) {
                frames.add(region);
            } else if (i == 1) {
                // If we can't find frame 1, try without a number
                TextureRegion baseRegion = atlas.findRegion(objectName);
                if (baseRegion != null) {
                    singleFrame = baseRegion;
                    isAnimated = false;
                    return; // Exit early as we found a single frame
                }
            }
        }

        if (frames.size > 1) {
            // We have multiple frames, create animation
            this.animation = new Animation<>(.2f, frames, Animation.PlayMode.LOOP);
            isAnimated = true;
        } else if (frames.size == 1) {
            // Only one frame was found, use it as a static image
            singleFrame = frames.get(0);
            isAnimated = false;
        } else {
            System.err.println("No frames found for: " + objectName);
        }
    }


    public void update(float delta) {
        if (isAnimated) {
            stateTime += delta;
        }
    }

    public TextureRegion getCurrentFrame() {
        if (isAnimated) {
            return animation.getKeyFrame(stateTime, true);
        } else {
            return singleFrame;
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = getCurrentFrame();
        if (currentFrame != null) {

            float x = entity.getX();
            float y = entity.getY();
            float width = entity.getWidth();
            float height = entity.getHeight();
            //System.out.println("location: " + x + "," + y);
            batch.draw(currentFrame, x - width / 2, y - height / 2, width, height);
        }
    }

    public float getTextureWidth() {
        return getCurrentFrame().getRegionWidth();
    }

    public float getTextureHeight() {
        return getCurrentFrame().getRegionHeight();
    }
}