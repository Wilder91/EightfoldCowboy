package helper.movement;

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


public class SpriteIdleHelper {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private AnimationHelper animationHelper;
    private float stateTime;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String characterType;
    private String characterName;
    private boolean isFacingRight = true;
    private int[] frameCounts;
    private float frameDuration;

    public SpriteIdleHelper(GameAssets gameAssets, GameEntity entity, String characterType, String characterName, int[] frameCounts, float stateTime) {
        this.gameAssets = gameAssets;
        this.characterType = characterType;
        this.characterName = characterName;
        this.stateTime = stateTime;
        this.animations = new HashMap<>();
        this.frameCounts = frameCounts;
        this.animationHelper = new AnimationHelper(gameAssets, entity);
        animationHelper.loadAnimations(characterType, characterName, FRAME_DURATION, "idle");
        animations = animationHelper.getAllAnimations();

       //loadAnimations();

        this.currentAnimation = animations.get("Down");
        this.sprite = new Sprite(currentAnimation.getKeyFrame(0));
        this.sprite.setOriginCenter();
    }

    private void loadAnimations() {
       // System.out.println("character TYPE: " + characterType);
        String atlasPath = "atlases/eightfold/" + characterType + "-movement.atlas";
        animations.put("Down", createAnimation(characterName + "_down_idle", atlasPath, .3f));
        animations.put("Up", createAnimation(characterName + "_up_idle", atlasPath, .1f));
        //animations.put("idleDiagonalUp", createAnimation(characterName + "_diagUP_idle", frameCounts[2], atlasPath, .1f));
        //animations.put("idleDiagonalDown", createAnimation(characterName + "_diagDOWN_idle", frameCounts[3], atlasPath, .3f));
        animations.put("Side", createAnimation(characterName + "_horizontal_idle", atlasPath, .8f));
    }

    private Animation<TextureRegion> createAnimation(String regionNamePrefix, String atlasPath, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

        int i = 1;
        TextureRegion region;
        while ((region = atlas.findRegion(regionNamePrefix, i)) != null) {
            frames.add(region);
            i++;
        }

        if (frames.size == 0) {
           // System.err.println("No regions found with prefix: " + regionNamePrefix);
        }

        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    public void update(float delta) {
        stateTime += delta;
        TextureRegion region = currentAnimation.getKeyFrame(stateTime, true);
        sprite.setRegion(region);
        sprite.setSize(region.getRegionWidth(), region.getRegionHeight());
        sprite.setOriginCenter();

        // Apply flipping based on direction
        sprite.setFlip(!isFacingRight, false); // Flip when NOT facing right
    }
    public void setFacingRight(boolean right) {
        isFacingRight = right;

    }



    public void setDirection(String direction) {
        Animation<TextureRegion> newAnimation = animations.get(direction);
        if (newAnimation != null && newAnimation != currentAnimation) {
            currentAnimation = newAnimation;
            stateTime = 0f; // Optional: reset to start of idle loop on direction change
        }
    }

    public void resetStateTime() {
        this.stateTime = 0f;
    }

    private void flipSprite(boolean shouldFaceRight) {
        if (isFacingRight != shouldFaceRight) {
            sprite.flip(true, false);
            isFacingRight = shouldFaceRight;
        }
    }

    public Sprite getSprite() {
        return sprite;
    }

    public float getStateTime() {
        return stateTime;
    }

    public void getFacingDirection(Vector2 velocity, Vector2 absVelocity) {
    }
}


