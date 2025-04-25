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


    public AnimationHelper(GameAssets gameAssets, GameEntity entity){
        this.gameAssets = gameAssets;

    }
        public void loadAnimations(String entityType, String entityName, float frameDuration, String action){
            String atlasPath = "atlases/eightfold/" + entityType + ".atlas";
            HashMap animations = new HashMap<>();
            // Only populate with up, down and horizontal animations
            animations.put("runningUp", createAnimation(entityName + "_up_" + action, atlasPath, frameDuration));
            animations.put("runningDown", createAnimation(entityName + "_down_walk", atlasPath, frameDuration));
            animations.put("runningHorizontal", createAnimation(entityName + "_horizontal_walk", atlasPath, frameDuration));
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
            System.err.println("No regions found with prefix: " + regionNamePrefix);
        }

        return new Animation<>(frameDuration, frames, Animation.PlayMode.LOOP);
    }
}
