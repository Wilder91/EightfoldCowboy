package helper.movement;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import helper.animation.AnimationHelper;
import objects.GameEntity;

import java.util.HashMap;
import java.util.Map;

import static helper.Constants.FRAME_DURATION;

public class SimpleSpriteWalkingHelper {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private TextureRegion restingFrame;
    private float stateTime;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String animalType;
    private String animalName;
    private boolean isFacingRight = true; // Track the facing direction
    private int[] frameCounts;
    private boolean startFlipped;
    private float frameDuration;
    private AnimationHelper animationHelper;

    public SimpleSpriteWalkingHelper(GameAssets gameAssets, GameEntity entity, String animalType, String animalName, int[] frameCounts, boolean startFlipped, float frameDuration) {
        this.gameAssets = gameAssets;
        this.animalType = animalType;
        this.animalName = animalName;
        this.stateTime = 0f;
        this.animations = new HashMap<>();
        this.frameCounts = frameCounts;
        this.startFlipped = startFlipped;
        this.frameDuration = frameDuration;
        this.animationHelper = new AnimationHelper(gameAssets, entity);
        animationHelper.loadAnimations(animalType, animalName, frameDuration, "walk");
        animations = animationHelper.getAllAnimations();
        //loadAnimations();
        this.currentAnimation = animations.get("runningHorizontal");

        this.sprite = new Sprite(this.currentAnimation.getKeyFrame(stateTime));
        this.sprite.setOriginCenter();

        if (startFlipped) {
            sprite.flip(true, false);
            isFacingRight = false;
        }
    }

    public void loadAnimations() {
        String atlasPath = "atlases/eightfold/" + animalType + ".atlas";
        // Only populate with up, down and horizontal animations
        animations.put("runningUp", createAnimation(animalName + "_up_walk",  atlasPath));
        animations.put("runningDown", createAnimation(animalName + "_down_walk",  atlasPath));
        animations.put("runningHorizontal", createAnimation(animalName + "_horizontal_walk",  atlasPath));
    }

    private Animation<TextureRegion> createAnimation(String regionNamePrefix, String atlasPath) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

        int i = 1;
        TextureRegion region;
        while ((region = atlas.findRegion(regionNamePrefix, i)) != null) {
            frames.add(region);
            i++;
        }

        if (frames.size == 0) {
            System.err.println("No regions found with prefix: " + regionNamePrefix);
        }

        return new Animation<>(this.frameDuration, frames, Animation.PlayMode.LOOP);
    }

    public void updateAnimation(Vector2 linearVelocity, float delta) {
        float vx = linearVelocity.x;
        float vy = linearVelocity.y;
        boolean isMoving = Math.abs(vx) > 0.1f || Math.abs(vy) > 0.1f;

        if (isMoving) {
            setRunningAnimation(vx, vy);
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

    public void setFacingRight(boolean shouldFaceRight) {
        if (isFacingRight != shouldFaceRight) {
            sprite.flip(true, false);
            isFacingRight = shouldFaceRight;
        }
    }

    private void setRunningAnimation(float vx, float vy) {
        // Determine the primary direction of movement
        if (Math.abs(vy) > Math.abs(vx)) {
            // Vertical movement is stronger
            if (vy > 0) {
                currentAnimation = animations.get("runningUp");
            } else {
                currentAnimation = animations.get("runningDown");
            }
        } else {
            // Horizontal movement is stronger or equal
            currentAnimation = animations.get("runningHorizontal");
            flipSprite(vx > 0);
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