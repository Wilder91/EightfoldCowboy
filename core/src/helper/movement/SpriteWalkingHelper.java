package helper.movement;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;

import java.util.HashMap;
import java.util.Map;

import static helper.Constants.FRAME_DURATION;

public class SpriteWalkingHelper {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private TextureRegion restingFrame;
    private float stateTime;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String animalType;
    private boolean isFacingRight = true; // Track the facing direction
    private int[] frameCounts;
    private boolean startFlipped;

    public SpriteWalkingHelper(GameAssets gameAssets, String animalType, int[] frameCounts, boolean startFlipped) {
        this.gameAssets = gameAssets;
        this.animalType = animalType;
        this.stateTime = 0f;
        this.animations = new HashMap<>();
        this.frameCounts = frameCounts;
        this.startFlipped = startFlipped;
        loadAnimations();

        this.currentAnimation = animations.get("walkingUp");
        setRestingFrame("player/Character_Down_Run_1.png");
        this.sprite = new Sprite(restingFrame);
        this.sprite.setOriginCenter();

        if (startFlipped) {
            sprite.flip(true, false);
            isFacingRight = false;
        }
    }

    public void loadAnimations() {

        // Populate the animations map with all available running animations
        System.out.println("Trying to load: " + animalType + " from " + "atlases/eightfold/" + animalType + "-movement.atlas" );
        animations.put("walkingUp", createAnimation(animalType + "_Up_Walk", frameCounts[0], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingDown", createAnimation(animalType + "_Down_Walk", frameCounts[1], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingHorizontal", createAnimation(animalType + "_Horizontal_Walk", frameCounts[2], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingDiagonalUp", createAnimation(animalType + "_DiagUP_Walk", frameCounts[3], "atlases/eightfold/" + animalType + "-movement.atlas"));
        animations.put("walkingDiagonalDown", createAnimation(animalType + "_DiagDOWN_Walk", frameCounts[4], "atlases/eightfold/" + animalType + "-movement.atlas"));
    }

    private Animation<TextureRegion> createAnimation(String regionNamePrefix, int frameCount, String atlasPath) {
        System.out.println("Trying to load: " + regionNamePrefix + " from " + atlasPath);
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);
        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(regionNamePrefix, i);
            if (region != null) {
                frames.add(region);
                //System.out.println("Added frame: " + regionNamePrefix + "_" + i); // Debug print
            } else {
                System.out.println("Region " + regionNamePrefix + "_" + i + " not found!"); // Debug print
            }
        }
        System.out.println("Total frames for " + regionNamePrefix + ": " + frames.size); // Debug print
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    public void updateAnimation(Vector2 linearVelocity, float delta) {
        float vx = linearVelocity.x;
        float vy = linearVelocity.y;
        boolean isMoving = Math.abs(vx) > 0.1f || Math.abs(vy) > 0.1f;
        if (isMoving) {
            setWalkingAnimation(vx, vy);
            stateTime += delta;
            TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
            sprite.setRegion(frame);
        } else {
            if (restingFrame != null) {
                sprite.setRegion(currentAnimation.getKeyFrame(stateTime));
            } else {
                stateTime += delta;
                TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
                sprite.setRegion(frame);
            }
        }

        sprite.setSize(sprite.getRegionWidth(), sprite.getRegionHeight());
        sprite.setOriginCenter();

        // Adjust the sprite facing direction
        if (vx != 0) {
            flipSprite(vx > 0);
        }
    }

    private void setWalkingAnimation(float vx, float vy) {
        if (vy > 0.1f) {
            if (vx > 0) {
                currentAnimation = animations.get("walkingDiagonalUp");
                flipSprite(true);
            } else if (vx < 0) {
                currentAnimation = animations.get("walkingDiagonalUp");
                flipSprite(false);
            } else {
                currentAnimation = animations.get("walkingUp");
            }
        } else if (vy < -0.1f) {
            if (vx > 0) {
                currentAnimation = animations.get("walkingDiagonalDown");
            } else if (vx < 0) {
                currentAnimation = animations.get("walkingDiagonalDown");
            } else {
                currentAnimation = animations.get("walkingDown");
            }
        } else if (vx > 0.1f) {
            currentAnimation = animations.get("walkingHorizontal");
            flipSprite(true);
        } else if (vx < -0.1f) {
            currentAnimation = animations.get("walkingHorizontal");
            flipSprite(false);
        }
    }

    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }

    private void flipSprite(boolean shouldFaceRight) {
        if (isFacingRight != shouldFaceRight) {
            sprite.flip(true, false); // Flip horizontally
            isFacingRight = shouldFaceRight; // Set the new facing direction
        }
    }

    public void setRestingFrame(String texturePath) {
        Texture texture = new Texture(texturePath);
        this.restingFrame = new TextureRegion(texture);
    }

    public Sprite getSprite() {
        return sprite;
    }

    public float getStateTime() {
        return stateTime;
    }
}
