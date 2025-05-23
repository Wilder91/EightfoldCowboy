package helper.movement;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.eightfold.GameAssets;
import helper.animation.AnimationHelper;
import objects.GameEntity;

import java.util.HashMap;
import java.util.Map;

public class SpriteMovementHelper{
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private TextureRegion restingFrame;
    private float stateTime;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String animalType;
    private String animalName;
    private boolean isFacingRight = true; // Track the facing direction
    private boolean startFlipped;
    private float frameDuration;
    private AnimationHelper animationHelper;
    private String action;
    private Boolean simple;
    private GameEntity entity;

    public SpriteMovementHelper(GameAssets gameAssets, GameEntity entity, String animalType, String animalName,
                                 boolean startFlipped, float frameDuration, String action, Boolean simple){
        this.gameAssets = gameAssets;
        this.animalType = animalType;
        this.animalName = animalName;
        this.stateTime = 0f;
        this.animations = new HashMap<>();
        this.startFlipped = startFlipped;
        this.action = action;
        this.entity = entity;
        this.frameDuration = frameDuration;
        this.animationHelper = new AnimationHelper(gameAssets, entity);
        this.simple = simple;
        //animationhelper.loadanimations needs to be called again to change the action, at least as
        //currently written
        loadAnimations(simple);
        //animationHelper.loadAnimations(animalType, animalName, frameDuration, action);
        //animations = animationHelper.getAllAnimations();
        //loadAnimations();
        this.currentAnimation = animations.get("Down");

        this.sprite = new Sprite(this.currentAnimation.getKeyFrame(stateTime));
        this.sprite.setOriginCenter();


    }

    public void setAction(String newAction) {
        // Only reload animations if the action has changed
        if (!this.action.equals(newAction)) {

            // Store the current direction before changing action
            String currentDirection = getCurrentAnimationKey();

            this.action = newAction;
            // Reload animations with the new action
            loadAnimations(simple);
            //System.out.println(currentDirection);
            // Restore the same direction with the new action
            if (animations.containsKey(currentDirection)) {
                currentAnimation = animations.get(currentDirection);
                //System.out.println("Restored animation direction: " + currentDirection);
            } else {
                //System.out.println("Could not restore direction: " + currentDirection + ", defaulting to Down");
                currentAnimation = animations.get("Down");
            }

            // Reset the animation state time for smoother transitions
            resetStateTime();
        }
    }
    // Helper method to determine current animation key
    private String getCurrentAnimationKey() {
        for (Map.Entry<String, Animation<TextureRegion>> entry : animations.entrySet()) {
            if (entry.getValue() == currentAnimation) {

                return entry.getKey();
            }
        }
        // Default to down if current animation not found
        return "Down";
    }

    public void loadAnimations(boolean simple){
        //System.out.println(action);
        if(simple) {
            animationHelper.loadSimpleAnimations(animalType, animalName, frameDuration, action);
            animations = animationHelper.getAllAnimations();
        }else {
            System.out.println("not simple");
            animationHelper.loadAnimations(animalType, animalName, frameDuration, action);
            animations = animationHelper.getAllAnimations();
        }


    }

    public int getFrameIndex(){
        return currentAnimation.getKeyFrameIndex(stateTime);
    }

    public void updateAnimation(Vector2 linearVelocity, float delta) {
        float vx = linearVelocity.x;
        float vy = linearVelocity.y;
        boolean isMoving = Math.abs(vx) > 0.1f || Math.abs(vy) > 0.1f;

        if (isMoving) {
            setCurrentAnimation(vx, vy);
            stateTime += delta;
            TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);

            // Check both directions
            if (vx > 0.4f) {
                setFacingRight(true);
            } else if (vx < -0.4f) {
                setFacingRight(false);
            }

            sprite.setRegion(frame);
        } else {
            // FIXED: When stopping, we need to ensure we're using the correct idle animation
            // based on the last direction of movement
            String currentKey = getCurrentAnimationKey();

            // Map movement animations to their corresponding idle animations
            if (currentKey.equals("DiagonalUp")) {
                // Make sure to use "Up" idle animation when stopping from diagonal up movement
                currentAnimation = animations.get("Up");
            } else if (currentKey.equals("DiagonalDown")) {
                currentAnimation = animations.get("Down");
            }
            // For horizontal, we keep the same animation

            stateTime += delta;
            TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
            sprite.setRegion(frame);
        }

        sprite.setSize(sprite.getRegionWidth(), sprite.getRegionHeight());
        sprite.setOriginCenter();
    }

    public void setFacingRight(boolean shouldFaceRight) {
        if (entity.isFacingRight() != shouldFaceRight) {
            entity.setFacingRight(shouldFaceRight);
            sprite.flip(true, false); // Always flip horizontally
        }
    }

    private void setCurrentAnimation(float vx, float vy) {
        if (vy > 0.1f) {
            if (Math.abs(vx) > 0.1f && animations.containsKey("DiagonalUp")) {
                // Use diagonal if it exists and we're moving diagonally
                currentAnimation = animations.get("DiagonalUp");

            } else {
                // Fall back to up if no diagonal exists
                currentAnimation = animations.get("Up");
            }
        } else if (vy < -0.1f) {
            if (Math.abs(vx) > 0.1f && animations.containsKey("DiagonalDown")) {
                // Use diagonal if it exists and we're moving diagonally
                currentAnimation = animations.get("DiagonalDown");

            } else {
                //System.out.println("fall back!");
                // Fall back to down if no diagonal exists
                currentAnimation = animations.get("Down");
            }
        } else if (vx > 0.1f || vx < -0.1f) {
            currentAnimation = animations.get("Horizontal");

        }
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

    public void setFrameDuration(float newDuration){
        frameDuration = newDuration;
    };


    public Animation<TextureRegion> getCurrentAnimation() {
        return currentAnimation;
    }
}