package objects.player;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import objects.GameAssets;

import static helper.Constants.FRAME_DURATION;

public class PlayerAnimations {
    private GameAssets gameAssets;


    public PlayerAnimations(GameAssets gameAssets){
        this.gameAssets = gameAssets;


    }

    Animation<TextureRegion> createWalkingAnimationFromAtlas() {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas("kath_walk/atlas/kath-walk.atlas");

        // Use the region names and bounds specified in the atlas file
        String[] regionNames = {"kath_walk"};
        int[] regionIndices = {0, 1, 2, 3, 4, 5, 6, 7};
        for (int index : regionIndices) {
            TextureRegion region = atlas.findRegion(regionNames[0], index);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region " + regionNames[0] + " with index " + index + " not found!");
            }
        }

        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }
}
