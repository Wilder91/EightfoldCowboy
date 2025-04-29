package helper.combat;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import objects.GameEntity;

/**
 * A helper class to handle death animations for game entities
 */
public class SpriteDeathHelper {
    private Animation<TextureRegion> deathAnimation;
    private Sprite sprite;
    private float stateTime;
    private boolean isAnimationComplete;
    private float frameDuration;
    private GameAssets gameAssets;
    private String entityName;
    private String entityType;

    /**
     * Creates a new death animation helper
     *
     * @param gameAssets The game assets for loading textures
     * @param entityType The type of entity (e.g., "enemies")
     * @param entityName The name of the entity (e.g., "saint_small")
     * @param frameDuration The duration of each frame in the animation
     */
    public SpriteDeathHelper(GameAssets gameAssets, String entityType, String entityName, float frameDuration) {
        this.gameAssets = gameAssets;
        this.entityType = entityType;
        this.entityName = entityName;
        this.frameDuration = frameDuration;
        this.stateTime = 0;
        this.isAnimationComplete = false;

        // Load the death animation
        loadDeathAnimation();

        // Initialize the sprite with the first frame
        if (deathAnimation != null) {
            this.sprite = new Sprite(deathAnimation.getKeyFrame(0));
            this.sprite.setOriginCenter();
        } else {
            this.sprite = new Sprite();
        }
    }

    /**
     * Loads the death animation from the atlas
     */
    private void loadDeathAnimation() {
        try {
            String atlasPath = "atlases/eightfold/" + entityType + "-movement.atlas";
            TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

            if (atlas != null) {
                Array<TextureRegion> frames = new Array<>();
                int i = 1;
                TextureRegion region;

                // Load all frames of the death animation
                String regionName = entityName + "_death";
                while ((region = atlas.findRegion(regionName, i)) != null) {
                    frames.add(region);
                    i++;
                }

                if (frames.size > 0) {
                    // Create the animation with the NORMAL play mode (plays once and stops)
                    deathAnimation = new Animation<>(frameDuration, frames, Animation.PlayMode.NORMAL);
                    //System.out.println("Loaded death animation for " + entityName + " with " + frames.size + " frames");
                } else {
                    System.err.println("No death animation frames found for " + entityName);
                }
            } else {
                System.err.println("Atlas not found: " + atlasPath);
            }
        } catch (Exception e) {
            System.err.println("Error loading death animation: " + e.getMessage());
        }
    }

    /**
     * Updates the death animation
     *
     * @param delta The time elapsed since the last update
     * @return true if the animation is still playing, false if complete
     */
    public boolean update(float delta) {
        if (deathAnimation == null || isAnimationComplete) {
            return false;
        }

        // Update the state time
        stateTime += delta;

        // Check if the animation is complete
        isAnimationComplete = deathAnimation.isAnimationFinished(stateTime);

        // Update the sprite with the current frame
        TextureRegion frame = deathAnimation.getKeyFrame(stateTime, false);
        if (frame != null && sprite != null) {
            sprite.setRegion(frame);
            sprite.setSize(frame.getRegionWidth(), frame.getRegionHeight());
            sprite.setOriginCenter();
        }

        return !isAnimationComplete;
    }

    /**
     * Gets the current animation sprite
     *
     * @return The current sprite
     */
    public Sprite getSprite() {
        return sprite;
    }

    /**
     * Checks if the death animation is complete
     *
     * @return true if the animation is complete, false otherwise
     */
    public boolean isAnimationComplete() {
        return isAnimationComplete;
    }

    /**
     * Resets the death animation to start from the beginning
     */
    public void reset() {
        stateTime = 0;
        isAnimationComplete = false;

        // Reset sprite to first frame
        if (deathAnimation != null) {
            TextureRegion frame = deathAnimation.getKeyFrame(0);
            if (frame != null && sprite != null) {
                sprite.setRegion(frame);
                sprite.setAlpha(1.0f); // Reset alpha
            }
        }
    }

    /**
     * Applies a fade-out effect to the sprite
     *
     * @param alpha The alpha value (0-1)
     */
    public void setAlpha(float alpha) {
        if (sprite != null) {
            sprite.setAlpha(alpha);
        }
    }
}