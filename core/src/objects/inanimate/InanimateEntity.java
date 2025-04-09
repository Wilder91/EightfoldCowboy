package objects.inanimate;

import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import objects.GameEntity;

public abstract class InanimateEntity extends GameEntity {
    private final int id;
    private final GameContactListener gameContactListener;

    public InanimateEntity(float width, float height, Body body, ScreenInterface screen, int id,
                           GameAssets gameAssets, GameContactListener gameContactListener) {
        // Call the parent constructor with the common parameters
        super(width, height, body, screen, gameAssets);

        // Set inanimate-specific fields
        this.id = id;
        this.gameContactListener = gameContactListener;
        this.speed = 0; // Inanimate objects typically don't move
    }


    public int getId() {
        return id;
    }

    public GameContactListener getGameContactListener() {
        return gameContactListener;
    }
}