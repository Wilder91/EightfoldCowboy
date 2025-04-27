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

public class SpriteWalkingHelper {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private AnimationHelper animationHelper;
    private TextureRegion restingFrame;
    private float stateTime;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String animalType;
    private String animalName;
    private boolean isFacingRight = true; // Track the facing direction
    private int[] frameCounts;
    private boolean startFlipped;

    public SpriteWalkingHelper(GameAssets gameAssets, GameEntity entity, String animalType, String animalName, int[] frameCounts, boolean startFlipped) {
        this.gameAssets = gameAssets;
        this.animalType = animalType;
        this.animalName = animalName;
        this.stateTime = 0f;
        this.animationHelper = new AnimationHelper(gameAssets, entity);
        animationHelper.loadAnimations(animalType, animalName, FRAME_DURATION, "walk");
        animations = animationHelper.getAllAnimations();
        this.frameCounts = frameCounts;
        this.startFlipped = startFlipped;

        //loadAnimations();

        this.currentAnimation = animations.get("Horizontal");
        setRestingFrame("Sprites/Character/Idle/Idle Down (300)/Character_Idle_Down_1.png");
        this.sprite = new Sprite();
        this.sprite.setOriginCenter();

        if (startFlipped) {
            sprite.flip(true, false);
            isFacingRight = false;
        }
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
        if (vy > 0.1f) {
            if (vx > 0) {
                //System.out.println("YO");
                currentAnimation = animations.get("DiagonalUp");
                flipSprite(true);
            } else if (vx < 0) {
                currentAnimation = animations.get("DiagonalUp");
                flipSprite(false);
            } else {
                currentAnimation = animations.get("Up");
            }
        } else if (vy < -0.1f) {
            if (vx > 0) {

                currentAnimation = animations.get("DiagonalDown");
            } else if (vx < 0) {
                currentAnimation = animations.get("DiagonalDown");
            } else {
                currentAnimation = animations.get("Down");
            }
        } else if (vx > 0.1f) {
            currentAnimation = animations.get("Horizontal");
            flipSprite(true);
        } else if (vx < -0.1f) {
            currentAnimation = animations.get("Horizontal");
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
