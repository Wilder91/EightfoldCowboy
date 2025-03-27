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
        animations.put("idleDown", createAnimation(characterType + "_Idle_Down", 18, atlasPath));
        animations.put("idleUp", createAnimation(characterType + "_Idle_Up", 1, atlasPath));
        animations.put("idleDiagonalUp", createAnimation(characterType + "_Idle_DiagUP", 8, atlasPath));
        animations.put("idleDiagonalDown", createAnimation(characterType + "_Idle_DiagDOWN", 18, atlasPath));
        animations.put("idleSide", createAnimation(characterType + "_Idle_Horizontal", 4, atlasPath));
    }

    private Animation<TextureRegion> createAnimation(String regionPrefix, int frameCount, String atlasPath) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(regionPrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.err.println("Missing frame: " + regionPrefix + " " + i);
            }
        }

        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    public void update(float delta) {
        stateTime += delta;
        sprite.setRegion(currentAnimation.getKeyFrame(stateTime, true));
        sprite.setSize(sprite.getRegionWidth(), sprite.getRegionHeight());
        sprite.setOriginCenter();
    }

    public void updateAnimation(Vector2 velocity, float delta) {
        float vx = velocity.x;
        float vy = velocity.y;

        if (vy > 0.1f) {
            if (Math.abs(vx) > 0.1f) {
                setDirection("idleDiagonalUp");
                flipSprite(vx > 0);
            } else {
                setDirection("idleUp");
            }
        } else if (vy < -0.1f) {
            if (Math.abs(vx) > 0.1f) {
                setDirection("idleDiagonalDown");
                flipSprite(vx > 0);
            } else {
                setDirection("idleDown");
            }
        } else if (vx > 0.1f) {
            setDirection("idleSide");
            flipSprite(true);
        } else if (vx < -0.1f) {
            setDirection("idleSide");
            flipSprite(false);
        }

        update(delta);
    }

    public void setDirection(String direction) {
        if (animations.containsKey(direction)) {
            currentAnimation = animations.get(direction);
        }
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
}