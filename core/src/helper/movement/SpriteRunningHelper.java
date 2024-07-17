package helper.movement;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static helper.Constants.FRAME_DURATION;

public class SpriteRunningHelper {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String animalType;
    private boolean isFacingRight = true; // Track the facing direction
    private int[] frameCounts;
    private boolean startFlipped;

    public SpriteRunningHelper(GameAssets gameAssets, String animalType, int[] frameCounts, boolean startFlipped) {
        this.gameAssets = gameAssets;
        this.animalType = animalType;
        this.stateTime = 0f;
        this.animations = new HashMap<>();
        this.frameCounts = frameCounts;
        this.startFlipped = startFlipped;

        loadAnimations();

        this.currentAnimation = animations.get("runningHorizontal");
        this.sprite = new Sprite(this.currentAnimation.getKeyFrame(0));
    }

    public static boolean checkLinearVelocity(Body body, Sprite sprite, boolean isFacingRight) {
        float velocityThreshold = 0.5f; // Adjust this threshold as needed
        // Check if the body's velocity exceeds the threshold
        if (Math.abs(body.getLinearVelocity().x) > velocityThreshold) {
            // Determine the direction based on the velocity
            boolean newFacingRight = (body.getLinearVelocity().x > 0);
            // Flip the sprite if the direction has changed
            if (newFacingRight != isFacingRight) {
                sprite.flip(true, false);
                isFacingRight = newFacingRight;
            }
        }
        // Return the current direction if no flipping occurred
        return isFacingRight;
    }

    public void loadAnimations() {
        // Populate the animations map with all available running animations
        animations.put("runningUp", createAnimation(animalType + "_Up_Run", frameCounts[0], "atlases/eightfold/" + animalType + "-running.atlas"));
        animations.put("runningDiagonalUp", createAnimation(animalType + "_DiagUP_Run", frameCounts[1], "atlases/eightfold/" + animalType + "-running.atlas"));
        animations.put("runningDown", createAnimation(animalType + "_Down_Run", frameCounts[2], "atlases/eightfold/" + animalType + "-running.atlas"));
        animations.put("runningDiagonalDown", createAnimation(animalType + "_DiagDOWN_Run", frameCounts[3], "atlases/eightfold/" + animalType + "-running.atlas"));
        animations.put("runningHorizontal", createAnimation(animalType + "_Horizontal_Run", frameCounts[4], "atlases/eightfold/" + animalType + "-running.atlas"));
    }

    private Animation<TextureRegion> createAnimation(String regionNamePrefix, int frameCount, String atlasPath) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);
        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(regionNamePrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region " + regionNamePrefix + "_" + i + " not found!");
            }
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    public void updateAnimation(Vector2 linearVelocity, float delta) {
        float vx = linearVelocity.x;
        float vy = linearVelocity.y;

        boolean isMoving = Math.abs(vx) > 0.1f || Math.abs(vy) > 0.1f;

        if (isMoving) {
            setRunningAnimation(vx, vy);
        }

        stateTime += delta;

        TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
        sprite.setRegion(frame);
        sprite.setSize(frame.getRegionWidth(), frame.getRegionHeight());
        sprite.setOriginCenter();

        if (startFlipped) {
            sprite.flip(true, false);
        }
        if (!isFacingRight) {
            sprite.flip(true, false); // Ensure the origin is centered
        }
    }

    private void setRunningAnimation(float vx, float vy) {
        if (vy > 0.1f) {
            if (vx > 0) {
                currentAnimation = animations.get("runningDiagonalUp");
                flipSprite(false);
            } else if (vx < 0) {
                currentAnimation = animations.get("runningDiagonalUp");
                flipSprite(true);
            } else {
                currentAnimation = animations.get("runningUp");
            }
        } else if (vy < -0.1f) {
            if (vx > 0) {
                currentAnimation = animations.get("runningDiagonalDown");
                flipSprite(false);
            } else if (vx < 0) {
                currentAnimation = animations.get("runningDiagonalDown");
                flipSprite(true);
            } else {
                currentAnimation = animations.get("runningDown");
            }
        } else if (vx > 0.1f) {
            currentAnimation = animations.get("runningHorizontal");
            flipSprite(false);
        } else if (vx < -0.1f) {
            currentAnimation = animations.get("runningHorizontal");
            flipSprite(true);
        }
    }

    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }

    private void flipSprite(boolean shouldFaceLeft) {
        if (isFacingRight == shouldFaceLeft) {
            sprite.flip(true, false); // Flip horizontally
            isFacingRight = !isFacingRight; // Toggle the facing direction
        }
    }

    public Sprite getSprite() {
        return sprite;
    }

    public float getStateTime() {
        return stateTime;
    }
}
