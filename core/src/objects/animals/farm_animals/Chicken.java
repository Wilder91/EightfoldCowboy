package objects.animals.farm_animals;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import helper.EntityRenderer;
import objects.GameEntity;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteRunningHelper;

import static helper.Constants.PPM;

public class Chicken extends GameEntity {
    private SimpleSpriteRunningHelper chickenWalkingHelper;
    private SimpleIdleHelper chickenIdleHelper;
    private EntityRenderer chickenRenderer;
    private ChickenMovement chickenMovement;
    private ChickenAnimator chickenAnimator;
    private Sprite sprite;
    private float stateTime;
    private Vector2 originalPosition = new Vector2();

    public Chicken(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets, String chickenName) {
        super(width, height, body, screenInterface, gameAssets);
        int[] frameCounts = {4, 4, 4};  // [up, down, horizontal]
        int idleFrameCount = 25;
        this.chickenWalkingHelper = new SimpleSpriteRunningHelper(gameAssets, "farm_animal", "Chicken", frameCounts, false);
        this.chickenIdleHelper = new SimpleIdleHelper(gameAssets, "farm_animal", "Chicken", idleFrameCount, 0.4f);
        this.chickenRenderer = new EntityRenderer(this);
        this.chickenMovement = new ChickenMovement(this);
        this.chickenAnimator = new ChickenAnimator(this, chickenWalkingHelper, chickenIdleHelper);
        // Initialize sprite
        this.sprite = chickenIdleHelper.getSprite();
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
        chickenMovement.update(delta);

        // Then update animation - this sets the current sprite

        sprite = chickenAnimator.updateAnimation(delta);
        // Finally set the updated sprite on the renderer
        chickenRenderer.setMainSprite(sprite);

        // Let the renderer do any additional updates it needs
        chickenRenderer.update(delta);

    }


    @Override
    public void render(SpriteBatch batch) {

        chickenRenderer.render(batch);
    }


    public void setChickenSpeed(float speed) {
        chickenMovement.setChickenSpeed(speed);
    }
    void setMovementDuration(float duration) {
        chickenMovement.setMovementDuration(duration);
    }

    public void setRestDuration(float duration) {
        chickenMovement.setRestDuration(duration);
    }

    public int getMoveCount() {
        return chickenMovement.getMoveCount();
    }

    public boolean isReturningToOrigin() {
        return chickenMovement.isReturningToOrigin();
    }

}