package helper.movement;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;

import static helper.Constants.FRAME_DURATION;

public class HorizontalSpriteHelper {
    private Animation<TextureRegion> walkAnimation;
    private float stateTime;
    private Sprite sprite;
    private boolean isFacingRight = true;
    private boolean startFlipped;

    /**
     * Creates a helper for sprites that only move horizontally
     *
     * @param gameAssets Game assets manager
     * @param atlasType Type of atlas (e.g., "farm_animal", "wild-animal")
     * @param animalName Name of the animal (e.g., "Squirrel", "Rabbit")
     * @param frameCount Number of frames in the walking animation
     * @param startFlipped Whether the sprite should start facing left
     */
    public HorizontalSpriteHelper(GameAssets gameAssets, String atlasType, String animalName,
                                  int frameCount, boolean startFlipped) {
        this.stateTime = 0f;
        this.startFlipped = startFlipped;

        // Load the walking animation
        String atlasPath = "atlases/eightfold/" + atlasType + ".atlas";
        walkAnimation = createAnimation(animalName + "_Walk_Side", frameCount, atlasPath, gameAssets);

        // Initialize the sprite
        TextureRegion firstFrame = walkAnimation.getKeyFrame(0);
        this.sprite = new Sprite(firstFrame);
        this.sprite.setOriginCenter();

        // Set initial direction
        if (startFlipped) {
            sprite.flip(true, false);
            isFacingRight = false;
        }
    }

    /**
     * Creates an animation from atlas regions
     */
    private Animation<TextureRegion> createAnimation(String regionNamePrefix, int frameCount,
                                                     String atlasPath, GameAssets gameAssets) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(regionNamePrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.err.println("Region " + regionNamePrefix + "_" + i + " not found!");
            }
        }

        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    /**
     * Updates the animation based on horizontal velocity
     *
     * @param velocity The velocity vector (only x component is used)
     * @param delta Time since last frame
     */
    public void updateAnimation(Vector2 velocity, float delta) {
        float vx = velocity.x;
        boolean isMoving = Math.abs(vx) > 0.1f;

        if (isMoving) {
            // Update animation time
            stateTime += delta;

            // Update sprite region
            TextureRegion frame = walkAnimation.getKeyFrame(stateTime, true);
            sprite.setRegion(frame);

            // Update facing direction
            setFacingRight(vx > 0);
        } else {
            // When not moving, use first frame
            sprite.setRegion(walkAnimation.getKeyFrame(0));
        }

        // Ensure sprite size matches region
        sprite.setSize(sprite.getRegionWidth(), sprite.getRegionHeight());
        sprite.setOriginCenter();
    }

    /**
     * Sets whether the sprite should face right or left
     */
    public void setFacingRight(boolean shouldFaceRight) {
        if (isFacingRight != shouldFaceRight) {
            sprite.flip(true, false);
            isFacingRight = shouldFaceRight;
        }
    }

    /**
     * Gets the current sprite
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Gets the current state time
     */
    public float getStateTime() {
        return stateTime;
    }

    /**
     * Resets the animation state time
     */
    public void resetStateTime() {
        stateTime = 0f;
    }

    /**
     * Checks if the sprite is currently facing right
     */
    public boolean isFacingRight() {
        return isFacingRight;
    }
}