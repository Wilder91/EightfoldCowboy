package objects.humans;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.EntityAnimator;
import helper.EntityRenderer;
import helper.movement.SpriteIdleHelper;
import helper.movement.SpriteWalkingHelper;
import objects.GameEntity;

public class Enemy extends GameEntity {
    private SpriteWalkingHelper walkingHelper;
    private SpriteIdleHelper idleHelper;
    private EntityAnimator animator;
    private EntityRenderer renderer;
    private float hp;
    public Enemy(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets, String enemyType, String enemyName, float hp) {
        super(width, height, body, screenInterface, gameAssets, hp);


    }


    @Override
    public void update(float delta) {

    }

    @Override
    public void render(SpriteBatch batch) {

    }
}
