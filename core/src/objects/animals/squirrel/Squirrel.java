package objects.animals.squirrel;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import helper.EntityRenderer;
import objects.GameEntity;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.movement.HorizontalSpriteHelper;
import helper.movement.SimpleIdleHelper;

import static helper.Constants.PPM;

public class Squirrel extends GameEntity {
    private HorizontalSpriteHelper squirrelWalkingHelper;
    private SimpleIdleHelper squirrelIdleHelper;
    private EntityRenderer squirrelRenderer;
    private SquirrelAnimator squirrelAnimator;
    private SquirrelMovement squirrelMovement;
    private Sprite sprite;
    private float stateTime;
    // Track original position and movement count
    private Vector2 originalPosition = new Vector2();
    private float hp;

    public Squirrel(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets, float hp) {
        super(width, height, body, screenInterface, gameAssets, hp);
        int idleFrameCount = 20;
        this.squirrelWalkingHelper = new HorizontalSpriteHelper(gameAssets, "wild-animal", "Squirrel", 4, true);
        this.squirrelIdleHelper = new SimpleIdleHelper(gameAssets, this, "wild-animal", "Squirrel", idleFrameCount, 0.3f);
        this.squirrelRenderer = new EntityRenderer(this);
        this.squirrelAnimator = new SquirrelAnimator(this, squirrelWalkingHelper, squirrelIdleHelper);
        this.squirrelMovement = new SquirrelMovement(this);
        this.sprite = squirrelIdleHelper.getSprite();
        this.hp = hp;
        if (this.sprite == null) {
            this.sprite = new Sprite();
            System.err.println("Warning: Could not initialize squirrel sprite from idle helper");
        }
        this.sprite.setSize(width, height);

        // Store the original position
        originalPosition.set(body.getPosition().x, body.getPosition().y);
        setDepth(body.getPosition().y);

    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        // Update movement
        squirrelMovement.update(delta);
        // Update animation using the component
        Vector2 velocity = body.getLinearVelocity();
        sprite = squirrelAnimator.updateAnimation(delta);

        // Update renderer
        squirrelRenderer.setMainSprite(sprite);
        squirrelRenderer.update(delta);
    }


    @Override
    public void render(SpriteBatch batch) {
        squirrelRenderer.render(batch);
    }

    public void setSquirrelSpeed(float speed) {
        squirrelMovement.setSquirrelSpeed(speed);
    }

    public void setMovementDuration(float duration) {
        squirrelMovement.setMovementDuration(duration);
    }

    public void setRestDuration(float duration) {
        squirrelMovement.setRestDuration(duration);
    }

    public int getMoveCount() {
        return squirrelMovement.getMoveCount();
    }

    public boolean isReturningToOrigin() {
        return squirrelMovement.isReturningToOrigin();
    }

}