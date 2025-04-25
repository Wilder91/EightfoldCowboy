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

public class SimpleCombatWalkingHelper {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private TextureRegion restingFrame;
    private float stateTime;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String entityName;
    private boolean isFacingRight = true; // Track the facing direction
    private int[] frameCounts;
    private boolean startFlipped;
    private float frameDuration;

    public SimpleCombatWalkingHelper(GameAssets gameAssets, String entityType, String entityName, int[] frameCounts, boolean startFlipped, float frameDuration) {
        this.gameAssets = gameAssets;
        this.entityName = entityName;
        this.stateTime = 0f;
        this.animations = new HashMap<>();
        this.frameCounts = frameCounts;
        this.startFlipped = startFlipped;
        this.frameDuration = frameDuration;
        loadAnimations();

        this.currentAnimation = animations.get("combatSide");

        this.sprite = new Sprite(this.currentAnimation.getKeyFrame(stateTime));
        this.sprite.setOriginCenter();


    }

    public void loadAnimations() {
        String atlasPath = "atlases/eightfold/enemies-movement.atlas";
        // Populate with up, down and side combat animations
        animations.put("combatUp", createAnimation(entityName + "_combatwalk_up", frameCounts[0], atlasPath));
        animations.put("combatDown", createAnimation(entityName + "_combatwalk_down", frameCounts[1], atlasPath));
        animations.put("combatSide", createAnimation(entityName + "_combatwalk_side", frameCounts[2], atlasPath));
    }

    private Animation<TextureRegion> createAnimation(String regionNamePrefix, int frameCount, String atlasPath) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

        // Looking at your atlas file, regions are indexed simply as 1, 2, 3...
        // not with a formatted frame number like "_01"
        for (int i = 1; i <= frameCount; i++) {
            // Use the index parameter directly without formatting
            TextureRegion region = atlas.findRegion(regionNamePrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.err.println("Region " + regionNamePrefix + " with index " + i + " not found!");
            }
        }

        return new Animation<>(this.frameDuration, frames, Animation.PlayMode.LOOP);
    }

    public void updateAnimation(Vector2 linearVelocity, float delta) {
        float vx = linearVelocity.x;
        float vy = linearVelocity.y;
        boolean isMoving = Math.abs(vx) > 0.1f || Math.abs(vy) > 0.1f;

        if (isMoving) {
            setCombatAnimation(vx, vy);
            stateTime += delta;
            TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
            sprite.setRegion(frame);

            // Handle horizontal facing direction based on velocity
            if (Math.abs(vx) > 0.1f) {
                // Set facing right if velocity is positive, facing left if negative
               // setFacingRight(vx > 0);
            }
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
    }

    public void setFacingRight(boolean shouldFaceRight) {
        if (isFacingRight != shouldFaceRight) {
            sprite.flip(true, false);
            isFacingRight = shouldFaceRight;
        }
    }

    private void setCombatAnimation(float vx, float vy) {
        // Determine the primary direction of movement
        if (Math.abs(vy) > Math.abs(vx)) {
            // Vertical movement is stronger
            if (vy > 0) {
                currentAnimation = animations.get("combatUp");
            } else {
                currentAnimation = animations.get("combatDown");
            }
        } else {
            // Horizontal movement is stronger or equal
            currentAnimation = animations.get("combatSide");
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

    // Set a specific animation directly
    public void setAnimation(String animationKey) {
        if (animations.containsKey(animationKey)) {
            currentAnimation = animations.get(animationKey);
        }
    }

    // Reset the state time (useful when changing animations)
    public void resetStateTime() {
        stateTime = 0f;
    }
}