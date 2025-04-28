package helper.animation;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import objects.GameEntity;

import java.util.HashMap;

public class AnimationHelper {
    private String atlasPath;
    private String entityName;
    private String entityType;
    private GameAssets gameAssets;
    private HashMap<String, Animation<TextureRegion>> animations;

    public AnimationHelper(GameAssets gameAssets, GameEntity entity) {
        this.gameAssets = gameAssets;
        this.animations = new HashMap<>();
    }

    public void loadAnimations(String entityType, String entityName, float frameDuration, String action) {
        this.entityType = entityType;
        this.entityName = entityName;
        this.atlasPath = "atlases/eightfold/" + entityType + "-movement.atlas";

        // Only populate with up, down and horizontal animations
        animations.put("Up", createAnimation(entityName + "_up_" + action, atlasPath, frameDuration));
        animations.put("Down", createAnimation(entityName + "_down_" + action, atlasPath, frameDuration));
        animations.put("Horizontal", createAnimation(entityName + "_horizontal_" + action, atlasPath, frameDuration));
        animations.put("DiagonalUp", createAnimation(entityName + "_diagUP_" + action, atlasPath, frameDuration));
        animations.put("DiagonalDown", createAnimation(entityName + "_diagDOWN_" + action, atlasPath, frameDuration));

    }

    public void loadSimpleAnimations(String entityType, String entityName, float frameDuration, String action) {
        this.entityType = entityType;
        this.entityName = entityName;
        this.atlasPath = "atlases/eightfold/" + entityType + "-movement.atlas";

        // Only load animations that exist in the atlas
        animations.put("Up", createAnimation(entityName + "_up_" + action, atlasPath, frameDuration));
        animations.put("Down", createAnimation(entityName + "_down_" + action, atlasPath, frameDuration));
        animations.put("Horizontal", createAnimation(entityName + "_horizontal_" + action, atlasPath, frameDuration));

        // Don't try to load diagonal animations if they don't exist in the atlas
        // animations.put("DiagonalUp", createAnimation(entityName + "_diagUP_" + action, atlasPath, frameDuration));
        // animations.put("DiagonalDown", createAnimation(entityName + "_diagDOWN_" + action, atlasPath, frameDuration));
    }

    private Animation<TextureRegion> createAnimation(String regionNamePrefix, String atlasPath, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

        //System.out.println("Creating animation for: " + regionNamePrefix + " with frameDuration: " + frameDuration);

        int i = 1;
        TextureRegion region;
        while ((region = atlas.findRegion(regionNamePrefix, i)) != null) {
            frames.add(region);
            //System.out.println("Added frame " + i + " for " + regionNamePrefix);
            i++;
        }

        if (frames.size == 0) {
            System.err.println("No regions found with prefix: " + regionNamePrefix);

//             List all regions in atlas for debugging
//            Array<TextureAtlas.AtlasRegion> allRegions = atlas.getRegions();
//            System.out.println("All regions in atlas (" + atlasPath + "):");
//            for (TextureAtlas.AtlasRegion reg : allRegions) {
//                System.out.println("Region: " + reg.name + (reg.index != -1 ? " index: " + reg.index : ""));
//            }

            // Return a default animation with at least one frame to avoid division by zero
            frames.add(new TextureRegion()); // empty region as fallback
        }

        //out.println("Created animation with " + frames.size + " frames");

        // Ensure frameDuration is not zero to avoid division by zero
        if (frameDuration <= 0) {
            System.err.println("Warning: frameDuration was <= 0, setting to 0.1f");
            frameDuration = 0.1f;
        }

        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }

    // Method to get a specific animation
    public Animation<TextureRegion> getAnimation(String animationKey) {
        return animations.get(animationKey);
    }

    // Method to get all animations
    public HashMap<String, Animation<TextureRegion>> getAllAnimations() {
        return animations;
    }
}