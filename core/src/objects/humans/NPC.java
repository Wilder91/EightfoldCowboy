package objects.humans;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.GameEntity;

public class NPC extends GameEntity {
    public NPC(float width, float height, Body body, GameScreen gameScreen, GameAssets gameAssets) {
        super(width, height, body, gameScreen, gameAssets);
    }

    @Override
    public void update(float delta) {

    }

    @Override
    public void render(SpriteBatch batch) {

    }
}
