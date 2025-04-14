package helper.movement;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;

import java.util.HashMap;
import java.util.Map;



public class SpriteIdleHelper {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String characterType;
    private String characterName;
    private boolean isFacingRight = true;
    private int[] frameCounts;

    public SpriteIdleHelper(GameAssets gameAssets, String characterType, String characterName, int[] frameCounts, float stateTime) {
        this.gameAssets = gameAssets;
        this.characterType = characterType;
        this.characterName = characterName;
        this.stateTime = stateTime;
        this.animations = new HashMap<>();
        this.frameCounts = frameCounts;

        loadAnimations();

        this.currentAnimation = animations.get("idleDown");
        this.sprite = new Sprite(currentAnimation.getKeyFrame(0));
        this.sprite.setOriginCenter();
    }

    private void loadAnimations() {
       // System.out.println("character TYPE: " + characterType);
        String atlasPath = "atlases/eightfold/" + characterType + "-movement.atlas";
        animations.put("idleDown", createAnimation(characterName + "_down_idle", frameCounts[0], atlasPath, .3f));
        animations.put("idleUp", createAnimation(characterName + "_up_idle", frameCounts[1], atlasPath, .1f));
        //animations.put("idleDiagonalUp", createAnimation(characterName + "_diagUP_idle", frameCounts[2], atlasPath, .1f));
        //animations.put("idleDiagonalDown", createAnimation(characterName + "_diagDOWN_idle", frameCounts[3], atlasPath, .3f));
        animations.put("idleSide", createAnimation(characterName + "_horizontal_idle", frameCounts[4], atlasPath, .8f));
    }

    private Animation<TextureRegion> createAnimation(String regionPrefix, int frameCount, String atlasPath, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

        if (atlas == null) {
            System.err.println("ERROR: Atlas not found: " + atlasPath);
            return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
        }

        //System.out.println("Loading idle animation: " + regionPrefix + " with " + frameCount + " frames");

        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(regionPrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.err.println("Missing frame: " + regionPrefix + "_" + i);
            }
        }

        //System.out.println("Loaded " + frames.size + " frames for " + regionPrefix);



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


