package helper.movement;

import com.mygdx.eightfold.GameAssets;

public class PlayerMovementHelper extends SpriteMovementHelper{
    public PlayerMovementHelper(GameAssets gameAssets, String animalType, int[] frameCounts, boolean randomFlip, boolean startFlipped) {
        super(gameAssets, animalType, frameCounts, randomFlip, startFlipped);
    }
}
