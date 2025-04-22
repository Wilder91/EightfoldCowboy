package objects.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.ContactType;
import helper.EntityRenderer;
import helper.EntityMovement;
import helper.combat.MeleeCombatHelper;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteWalkingHelper;
import helper.state.EnemyStateManager;
import objects.GameEntity;
import helper.SimpleAnimator;

import static helper.Constants.PPM;

public class ThicketSaint extends GameEntity {

    public enum State {
        IDLE,
        RUNNING,
        ATTACKING
    }

    private Sprite sprite;
    private SimpleIdleHelper idleHelper;
    private SimpleSpriteWalkingHelper walkingHelper;
    private MeleeCombatHelper meleeCombatHelper;
    private SimpleAnimator animator;
    private String lastDirection = "idleDown";
    private boolean isFacingRight = true;
    private int id;
    private float stateTime = 0;
    private ScreenInterface screenInterface;
    private EntityRenderer renderer;
    private String entityType;
    private String entityName;
    private EntityMovement movement;
    private CircleShape sensorShape;
    private FixtureDef sensorFixtureDef;
    private Fixture sensorFixture;
    private boolean isSensorSmall = false;
    private float largeSensorRadius = .5f;
    private float smallSensorRadius = .5f;
    private State currentState = State.IDLE;
    private EnemyStateManager stateManager;
    private float attackDuration = 5f; // Set this to match your attack animation length
    private float attackTimer = 0;
    private boolean isAttacking = false;
    private float hp;
    public ThicketSaint(float width, float height, Body body, ScreenInterface screenInterface,
                        GameAssets gameAssets, String entityType, String entityName, float hp) {
        super(width, height, body, screenInterface, gameAssets, hp);
        this.stateManager = new EnemyStateManager();
        this.sprite = new Sprite();
        this.sprite.setSize(width, height);
        this.entityName = "saint_small"; // Hard-code this to match the atlas
        this.entityType = entityType;
        this.screenInterface = screenInterface;
        this.hp = hp;
        // Initialize the helpers with correct parameters
        int idleFrameCounts = 4;
        int[] walkingFrameCounts = {8, 8, 8};
        int[] combatFrameCounts = {4, 4, 4, 0, 0};
        // Create a custom subclass of SpriteWalkingHelper to override the problematic method
        this.walkingHelper = new SimpleSpriteWalkingHelper(gameAssets, "enemies", this.entityName, walkingFrameCounts, true, .2f) {
            @Override
            public void setRestingFrame(String texturePath) {
                // Do nothing - avoid loading the hardcoded texture
            }
        };

        this.idleHelper = new SimpleIdleHelper(gameAssets, "enemies-movement", this.entityName, idleFrameCounts, 1.5f);
        this.meleeCombatHelper = new MeleeCombatHelper(gameAssets, entityType, entityName, "sword", combatFrameCounts, 5, screenInterface.getWorld(),
                .09f, ContactType.ENEMY, ContactType.PLAYER, screenInterface);
        this.movement = new EntityMovement(this);

        // Initialize renderer directly without animator
        this.renderer = new EntityRenderer(this);

        // Try to get initial sprite from idle helper
        Sprite idleSprite = idleHelper.getSprite();
        if (idleSprite != null) {
            this.sprite = idleSprite;
        }

        this.renderer.setMainSprite(sprite);

        // Initialize state manager with default state
        //stateManager.setState("IDLE");

        // Setup sensor
        setupSensor(largeSensorRadius);
    }

    private void setupSensor(float radius) {
        this.sensorFixtureDef = new FixtureDef();

        sensorFixtureDef.isSensor = true;

        sensorShape = new CircleShape();
        sensorShape.setRadius(radius);
        sensorShape.setPosition(new Vector2(0.0f, 0)); // position relative to body center
        sensorFixtureDef.shape = sensorShape;
        // Add the fixture to your body
        this.sensorFixture = body.createFixture(sensorFixtureDef);

        sensorFixture.setUserData(this);
        // Dispose the shape when done
        sensorShape.dispose();
    }

    public SimpleIdleHelper getIdleHelper() {
        return this.idleHelper;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public void setSprite(Sprite newSprite) {
        this.sprite = newSprite;
    }

    public void setFacingRight(boolean newFacingRight) {
        isFacingRight = newFacingRight;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public SimpleSpriteWalkingHelper getWalkingHelper() {
        return this.walkingHelper;
    }

    public String getEntityName(){
        return this.entityName;
    }


    /**
     * Sets the current state of the entity
     * @param state The new state to set
     */
    public void setState(State state) {
        if (this.currentState != state) {
            this.currentState = state;
            //stateManager.setState(state.toString());

            // Reset attack timer when entering attack state
            if (state == State.ATTACKING) {
                isAttacking = true;
                attackTimer = 0;
            }

            // Reset state time when changing states
            stateTime = 0;
        }
    }

    public MeleeCombatHelper getCombatHelper(){
        return meleeCombatHelper;
    }
    @Override
    public void takeDamage(){

        Sound sound = screenInterface.getGameAssets().getSound("sounds/bison-sound.mp3");
        sound.play(0.05f);
        this.hp -= 5;
        System.out.println("thicketsaint hp: " + hp);

    }

    /**
     * Gets the current state of the entity
     * @return The current State
     */

    /**
     * Determines the appropriate state based on entity's current behavior
     */
    private void updateState(float delta) {
        // If currently attacking, don't change state until attack is complete
        if (currentState == State.ATTACKING) {
            attackTimer += delta; // You'll need to pass delta to this method

            if (attackTimer >= attackDuration) {
                isAttacking = false;
                attackTimer = 0;
                // After attack completes, fall back to IDLE or RUNNING
            } else {
                return; // Skip the rest of the method while attacking
            }
        }
        //animator.updateAnimation(delta);
        // Existing code for determining IDLE or RUNNING states
        float vx = body.getLinearVelocity().x;
        float vy = body.getLinearVelocity().y;
        boolean isMoving = Math.abs(vx) > 0.1f || Math.abs(vy) > 0.1f;

        // Update facing direction based on movement
        updateFacingDirection(vx, vy);

        // Set the appropriate state
        if (isMoving) {
            setState(State.RUNNING);
        } else {
            setState(State.IDLE);
        }
    }

    /**
     * Updates the facing direction based on movement velocity
     */
    private void updateFacingDirection(float vx, float vy) {
        try {
            // Update facing direction and lastDirection based on movement
            if (Math.abs(vy) > Math.abs(vx)) {
                // Vertical movement is dominant
                if (vy > 0.1f) {
                    lastDirection = "idleUp";
                } else if (vy < -0.1f) {
                    lastDirection = "idleDown";
                }
            } else {
                // Horizontal movement is dominant
                if (vx > 0.1f) {
                    lastDirection = "idleSide";
                    isFacingRight = true;
                } else if (vx < -0.1f) {
                    lastDirection = "idleSide";
                    isFacingRight = false;
                }
            }
        } catch (Exception e) {
            System.err.println("Error updating facing direction: " + e.getMessage());
        }
    }

    /**
     * Updates the animation based on the current state
     */
    private void updateAnimation(float delta) {
        try {
            switch (currentState) {
                case IDLE:
                    idleHelper.setFacingRight(isFacingRight);
                    idleHelper.update(delta);
                    Sprite idleSprite = idleHelper.getSprite();
                    if (idleSprite != null) {
                        sprite = idleSprite;
                    }
                    break;

                case RUNNING:
                    walkingHelper.setFacingRight(isFacingRight);
                    walkingHelper.updateAnimation(body.getLinearVelocity(), delta);
                    Sprite walkSprite = walkingHelper.getSprite();
                    if (walkSprite != null) {
                        sprite = walkSprite;
                    }
                    break;

                case ATTACKING:

                    meleeCombatHelper.setFacingRight(isFacingRight);

                    // Only start attack if not already attacking
                    if (!meleeCombatHelper.isAttacking()) {
                        Vector2 position = new Vector2(body.getPosition().x * PPM, body.getPosition().y * PPM);


                        boolean attackStarted = meleeCombatHelper.startAttack(lastDirection, position);

                    }

                    // Update the melee combat helper
                    Vector2 pos = new Vector2(body.getPosition().x * PPM, body.getPosition().y * PPM);
                    Vector2 dir = new Vector2(isFacingRight ? 1 : -1, 0);
                    meleeCombatHelper.update(delta, pos, dir, isFacingRight, lastDirection);

                    // Get sprite after update
                    Sprite attackSprite = meleeCombatHelper.getSprite();
                    if (attackSprite != null && attackSprite.getTexture() != null) {
                        // Debug for sprite info
                        //System.out.println("Attack sprite texture: " + attackSprite.getTexture());
                        sprite = attackSprite;
                    } else {
                        System.err.println("Attack sprite or texture is null");
                        // Fallback to idle sprite if attack sprite is null
                        setState(State.IDLE);
                    }
                    // Return to IDLE if attack finished
                    if (!meleeCombatHelper.isAttacking()) {
                        setState(State.IDLE);
                    }
                    break;
            }
        } catch (Exception e) {
            System.err.println("Animation error: " + e.getMessage());
            e.printStackTrace(); // Add stack trace for debugging
        }
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        resetDepthToY();

        // Update movement
        movement.update(delta);

        // Update state based on current behavior
        updateState(delta);

        // Update animations based on current state
        updateAnimation(delta);

        // Update the renderer
        renderer.setMainSprite(sprite);
        renderer.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        renderer.render(batch);
    }

    public int getId() {
        return id;
    }

    public String getEnemyType() {
        return entityType;
    }

}