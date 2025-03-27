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

public class SpriteIdleHelper {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String characterType;
    private boolean isFacingRight = true;

    public SpriteIdleHelper(GameAssets gameAssets, String characterType) {
        this.gameAssets = gameAssets;
        this.characterType = characterType;
        this.stateTime = 0f;
        this.animations = new HashMap<>();

        loadAnimations();
        this.currentAnimation = animations.get("idleDown");
        this.sprite = new Sprite(currentAnimation.getKeyFrame(0));
        this.sprite.setOriginCenter();
    }

    private void loadAnimations() {
        String atlasPath = "atlases/eightfold/" + characterType + "-movement.atlas";
        animations.put("idleDown", createAnimation(characterType + "_Idle_Down", 18, atlasPath, .3f));
        animations.put("idleUp", createAnimation(characterType + "_Idle_Up", 1, atlasPath, .1f));
        animations.put("idleDiagonalUp", createAnimation(characterType + "_Idle_DiagUP", 8, atlasPath, .1f));
        animations.put("idleDiagonalDown", createAnimation(characterType + "_Idle_DiagDOWN", 18, atlasPath, .3f));
        animations.put("idleSide", createAnimation(characterType + "_Idle_Horizontal", 4, atlasPath, .3f));
    }

    private Animation<TextureRegion> createAnimation(String regionPrefix, int frameCount, String atlasPath, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);
        System.out.println("Loaded " + frames.size + " frames for " + regionPrefix);

        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(regionPrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.err.println("Missing frame: " + regionPrefix + " " + i);
            }
        }

        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    public void update(float delta) {
        stateTime += delta;
        TextureRegion region = currentAnimation.getKeyFrame(stateTime, true);
        sprite.setRegion(region);
        sprite.setSize(region.getRegionWidth(), region.getRegionHeight());
        sprite.setOriginCenter();
        sprite.setFlip(isFacingRight, false); // <- Ensure flip is applied each frame
    }

    public void setFacingRight(boolean right) {
        isFacingRight = !right;
         // Flip X if facing left
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


