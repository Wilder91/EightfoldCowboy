package helper.movement;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;

import static helper.Constants.FRAME_DURATION;

public class SimpleIdleHelper {
    private Animation<TextureRegion> idleAnimation;
    private float stateTime;
    private Sprite sprite;
    private GameAssets gameAssets;
    private String animalType;
    private String animalName;
    private boolean isFacingRight = true;
    private int frameCount;
    private float frameDuration;

    /**
     * Creates a simple idle animation helper with only one animation
     *
     * @param gameAssets Game assets manager
     * @param animalType Type of animal (folder name in atlas)
     * @param animalName Name of the animal (prefix for animation files)
     * @param frameCount Number of frames in the idle animation
     * @param frameDuration Duration of each frame (or 0 to use default)
     */
    public SimpleIdleHelper(GameAssets gameAssets, String animalType, String animalName, int frameCount, float frameDuration) {
        this.gameAssets = gameAssets;
        this.animalType = animalType;
        this.animalName = animalName;
        this.stateTime = 0f;
        this.frameCount = frameCount;
        this.frameDuration = (frameDuration > 0) ? frameDuration : FRAME_DURATION;

        loadAnimation();

        // Create initial sprite from first frame or create an empty sprite if no frames available
        if (idleAnimation != null && idleAnimation.getKeyFrames().length > 0) {
            this.sprite = new Sprite(idleAnimation.getKeyFrame(0));
            this.sprite.setOriginCenter();
        } else {
            this.sprite = new Sprite();
            System.err.println("Warning: Could not initialize sprite for " + animalName);
        }
    }

    /**
     * Loads the idle animation from the atlas
     */
    private void loadAnimation() {
        String atlasPath = "atlases/eightfold/" + animalType + ".atlas";
        String regionPrefix = animalName + "_Idle_Down";

        // Get the atlas
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

        if (atlas == null) {
            System.err.println("Error: Atlas not found: " + atlasPath);
            // Create an empty animation to avoid null pointer exceptions
            idleAnimation = new Animation<TextureRegion>(frameDuration, new Array<TextureRegion>(TextureRegion.class));
            return;
        }

        // Load frames
        Array<TextureRegion> frames = new Array<TextureRegion>(TextureRegion.class);

        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(regionPrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                // Try without index for the first frame
                if (i == 1) {
                    region = atlas.findRegion(regionPrefix);
                    if (region != null) {
                        frames.add(region);
                        break; // Only use this single frame
                    }
                }
                System.err.println("Missing frame: " + regionPrefix + " " + i);
            }
        }

        if (frames.size == 0) {
            System.err.println("No frames found for animation: " + regionPrefix);
            // Create an empty animation to avoid null pointer exceptions
            idleAnimation = new Animation<TextureRegion>(frameDuration, new Array<TextureRegion>(TextureRegion.class));
        } else {
            System.out.println("Loaded " + frames.size + " frames for " + regionPrefix);
            idleAnimation = new Animation<TextureRegion>(frameDuration, frames, Animation.PlayMode.LOOP);
        }
    }

    /**
     * Updates the animation and sprite
     */
    public void update(float delta) {
        stateTime += delta;

        if (idleAnimation != null && idleAnimation.getKeyFrames().length > 0) {
            TextureRegion region = idleAnimation.getKeyFrame(stateTime, true);
            if (region != null) {
                sprite.setRegion(region);
                sprite.setSize(region.getRegionWidth(), region.getRegionHeight());
                sprite.setOriginCenter();
                sprite.setFlip(!isFacingRight, false);
            }
        }
    }

    /**
     * Sets the facing direction
     */
    public void setFacingRight(boolean right) {
        if (this.isFacingRight != right) {
            this.isFacingRight = right;
            sprite.setFlip(!right, false);
        }
    }

    /**
     * Resets the animation to the beginning
     */
    public void resetStateTime() {
        this.stateTime = 0f;
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
}