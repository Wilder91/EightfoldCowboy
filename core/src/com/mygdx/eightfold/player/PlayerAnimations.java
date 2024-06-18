package com.mygdx.eightfold.player;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.mygdx.eightfold.GameAssets;

public class PlayerAnimations {
    private GameAssets gameAssets;

    public PlayerAnimations(GameAssets gameAssets) {
        this.gameAssets = gameAssets;
    }

    public Animation<TextureRegion> createWalkingAnimationFromAtlas() {
        TextureAtlas atlas = gameAssets.getAtlas("player/atlas/player-horizontal.atlas");
        TextureRegion[] walkFrames = new TextureRegion[8];

        walkFrames[0] = atlas.findRegion("Character_Horizontal_Run", 1);
        walkFrames[1] = atlas.findRegion("Character_Horizontal_Run", 2);
        walkFrames[2] = atlas.findRegion("Character_Horizontal_Run", 3);
        walkFrames[3] = atlas.findRegion("Character_Horizontal_Run", 4);
        walkFrames[4] = atlas.findRegion("Character_Horizontal_Run", 5);
        walkFrames[5] = atlas.findRegion("Character_Horizontal_Run", 6);
        walkFrames[6] = atlas.findRegion("Character_Horizontal_Run", 7);
        walkFrames[7] = atlas.findRegion("Character_Horizontal_Run", 8);

        return new Animation<>(0.1f, walkFrames);
    }
}
