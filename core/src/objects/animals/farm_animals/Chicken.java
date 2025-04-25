package objects.animals.farm_animals;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import helper.EntityAnimator;
import helper.EntityRenderer;
import helper.SimpleAnimator;
import objects.GameEntity;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteWalkingHelper;

import static helper.Constants.PPM;

public class Chicken extends GameEntity {
    private SimpleSpriteWalkingHelper walkingHelper;
    private SimpleIdleHelper idleHelper;
    private EntityRenderer renderer;
    private ChickenMovement movement;
    private EntityAnimator animator;
    private Sprite sprite;
    private float stateTime;
    private Vector2 originalPosition = new Vector2();

    public Chicken(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets, String chickenName, float hp) {
        super(width, height, body, screenInterface, gameAssets, hp);
        int[] frameCounts = {4, 4, 4};  // [up, down, horizontal]
        int idleFrameCount = 25;
        this.walkingHelper = new SimpleSpriteWalkingHelper(gameAssets, this, "farm_animal", "Chicken", frameCounts, false, .4f);
        this.idleHelper = new SimpleIdleHelper(gameAssets, "farm_animal", "Chicken", idleFrameCount, 0.4f);
        this.renderer = new EntityRenderer(this);
        this.movement = new ChickenMovement(this);
        this.animator = new SimpleAnimator(this, walkingHelper, idleHelper);
        // Initialize sprite
        this.sprite = idleHelper.getSprite();
        if (this.sprite == null) {
            this.sprite = new Sprite();
            System.err.println("Warning: Could not initialize chicken sprite from idle helper");
        }
        this.sprite.setSize(width, height);

        // Store the original position
        originalPosition.set(body.getPosition().x, body.getPosition().y);
    }

    public static void playerContact(Body body, int id) {
    }

    public static void chickenContact(Body body, int id) {
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        // Update position from the physics body
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        resetDepthToY();

        // First update the movement
        movement.update(delta);

        // Then update animation - this sets the current sprite

        sprite = animator.updateAnimation(delta);
        // Finally set the updated sprite on the renderer
        renderer.setMainSprite(sprite);

        // Let the renderer do any additional updates it needs
        renderer.update(delta);

    }


    @Override
    public void render(SpriteBatch batch) {

        renderer.render(batch);
    }


    public void setChickenSpeed(float speed) {
        movement.setChickenSpeed(speed);
    }
    void setMovementDuration(float duration) {
        movement.setMovementDuration(duration);
    }

    public void setRestDuration(float duration) {
        movement.setRestDuration(duration);
    }

    public int getMoveCount() {
        return movement.getMoveCount();
    }

    public boolean isReturningToOrigin() {
        return movement.isReturningToOrigin();
    }

}