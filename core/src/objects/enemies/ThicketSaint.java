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
import helper.*;
import helper.animation.AnimationHelper;
import helper.combat.EnemyMeleeCombatHelper;
import helper.combat.PlayerMeleeCombatHelper;
import helper.movement.SimpleCombatWalkingHelper;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteWalkingHelper;
import helper.state.EnemyStateManager;
import objects.GameEntity;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;
import static objects.GameEntity.State.*;

public class ThicketSaint extends GameEntity {

    private Sprite sprite;
    private SimpleIdleHelper idleHelper;
    private EnemyStateManager stateManager;
    private SimpleSpriteWalkingHelper walkingHelper;
    private SimpleCombatWalkingHelper combatWalkingHelper;
    private EnemyMeleeCombatHelper meleeCombatHelper;
    private SimpleAnimator animator;
    private SensorHelper sensorHelper;
    private AnimationHelper animationHelper;
    private String lastDirection = "idleDown";
    private float attackCounter;
    private long lastAttackTime = 0;
    private static final long ATTACK_COOLDOWN = 2000;
    
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
    private float largeSensorRadius = 5f;
    private float smallSensorRadius = 2f;
    private GameEntity.State currentState = GameEntity.State.IDLE;
    private float hp;
    private boolean beginPursuit;


    public ThicketSaint(float width, float height, Body body, ScreenInterface screenInterface,
                        GameAssets gameAssets, String entityType, String entityName, float hp) {
        super(width, height, body, screenInterface, gameAssets, hp);
        this.sprite = new Sprite();
        this.sprite.setSize(width, height);
        this.entityName = entityName;
        this.entityType = entityType;
        this.screenInterface = screenInterface;
        this.hp = hp;


        // Initialize the helpers with correct parameters
        int idleFrameCounts = 4;
        int[] walkingFrameCounts = {8, 8, 8};
        int[] combatFrameCounts = {4, 4, 4, 0, 0};

        // Create a custom subclass of SpriteWalkingHelper to override the problematic method
        this.walkingHelper = new SimpleSpriteWalkingHelper(gameAssets, this, entityType, this.entityName, walkingFrameCounts, false, FRAME_DURATION) {
            @Override
            public void setRestingFrame(String texturePath) {
                // Do nothing - avoid loading the hardcoded texture
            }
        };

        this.idleHelper = new SimpleIdleHelper(gameAssets, this, "enemies", this.entityName, idleFrameCounts, 1.5f);
        this.meleeCombatHelper = new EnemyMeleeCombatHelper(gameAssets, entityType, entityName, "sword",  5, screenInterface.getWorld(),
                .07f, ContactType.ENEMY, ContactType.PLAYER, screenInterface, .5f, .5f);
        this.combatWalkingHelper = new SimpleCombatWalkingHelper(gameAssets,  entityType, entityName, combatFrameCounts, false,FRAME_DURATION);
        this.movement = new EntityMovement(this);
        this.sensorHelper = new SensorHelper();
        // Initialize renderer directly without animator
        this.renderer = new EntityRenderer(this, meleeCombatHelper);
        this.animator = new SimpleAnimator(this, walkingHelper, idleHelper);
        // Try to get initial sprite from idle helper
        Sprite idleSprite = idleHelper.getSprite();
        if (idleSprite != null) {
            this.sprite = idleSprite;
        }

        this.renderer.setMainSprite(sprite);

        // Initialize state manager and set initial state
        this.stateManager = new EnemyStateManager();
        this.stateManager.setAttackDuration(5f); // Set your attack duration
        this.stateManager.changeState(this, State.IDLE);

        // Setup sensor
        sensorHelper.setupAttackSensor(largeSensorRadius, this);
        sensorHelper.setupAttackSensor(smallSensorRadius, this);
    }



    // Getters and setters needed by the EnemyStateManager
    public SimpleIdleHelper getSimpleIdleHelper() {
        return this.idleHelper;
    }

    public SimpleSpriteWalkingHelper getSimpleWalkingHelper() {
        return this.walkingHelper;
    }


    public EnemyMeleeCombatHelper getMeleeHelper() {
        return this.meleeCombatHelper;
    }

    public void setBeginPursuit(boolean pursuit){
        beginPursuit = pursuit;
    }

    public void pursuePlayer() {
        // Get player position
        Vector2 playerPosition = screenInterface.getPlayer().getBody().getPosition();
        Vector2 myPosition = this.body.getPosition();

        // Calculate direction vector to player
        Vector2 direction = new Vector2(playerPosition.x - myPosition.x, playerPosition.y - myPosition.y);

        // Calculate distance to player
        float distanceToPlayer = direction.len();

        // Define minimum distance to stop (in Box2D units)
        float stopDistance = 1f; // Adjust this value as needed

        // Determine the appropriate state based on distance
        GameEntity.State newState = getCurrentState(); // Default to current state

        // Check if player is within the sensor radius
        if (distanceToPlayer > largeSensorRadius) {
            // Player is outside sensor radius, gradually stop movement
            Vector2 currentVelocity = this.body.getLinearVelocity();
            this.body.setLinearVelocity(currentVelocity.x * 0.8f, currentVelocity.y * 0.8f);
            newState = IDLE;
        }
        // Only normalize and move if not too close
        else if (distanceToPlayer > stopDistance) {
            // Normalize the direction
            direction.nor();

            // Set movement speed
            float movementSpeed = 1.1f; // Adjust this value as needed

            float halfwayDistance = stopDistance * 2; // Adjust this multiplier as needed

            // Choose state based on distance
            if (distanceToPlayer > halfwayDistance) {
                // Far away - use regular walking/running
                newState = RUNNING;
            } else {
                // Closer - use combat walking
                sprite.flip(true, true);
                newState = PURSUING;
            }

            // Apply velocity toward player
            this.body.setLinearVelocity(direction.x * movementSpeed, direction.y * movementSpeed);
        } else {
            // We're close enough, stop moving but don't reset velocity to zero immediately
            // This creates a more natural slowing down effect
            Vector2 currentVelocity = this.body.getLinearVelocity();
            this.body.setLinearVelocity(currentVelocity.x * 0.9f, currentVelocity.y * 0.9f);

            if (distanceToPlayer < stopDistance * 1f && currentVelocity.len() < 0.2f) {
                if (getCurrentState() != ATTACKING) {
                    triggerAttack();
                }
            }
        }

        // Only change state if needed
        if (newState != getCurrentState()) {
            stateManager.changeState(this, newState);
        }
    }

    public String getLastDirection() {
        return lastDirection;
    }

    public void triggerAttack(){
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastAttackTime < ATTACK_COOLDOWN) {
            // Still on cooldown, don't attack yet
            return;
        }

        // Update the last attack time
        lastAttackTime = currentTime;

        stateManager.changeState(this, ATTACKING);
    }


    public void setLastDirection(String direction) {
        this.lastDirection = direction;
    }

    public void setSprite(Sprite newSprite) {
        this.sprite = newSprite;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public GameEntity.State getCurrentState() {
        return this.currentState;
    }

    @Override
    public GameEntity.State getState() {
        return this.currentState;
    }

    @Override
    public void setState(GameEntity.State state) {
        // Map GameEntity.State to your local state
       this.currentState = state;
    }

    public String getEntityName() {
        return this.entityName;
    }




    @Override
    public void takeDamage() {
        //stateManager.changeState(this, ATTACKING);

        Sound sound = screenInterface.getGameAssets().getSound("sounds/bison-sound.mp3");
        sound.play(0.05f);
        this.hp -= 5;
        //stateManager.changeState(this, ATTACKING);
        System.out.println("thicketsaint hp: " + hp);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        resetDepthToY();

        if (beginPursuit){
            pursuePlayer();
        }
        // Update movement
        movement.update(delta);

        // Get current position and direction for combat helper
        Vector2 position = new Vector2(x, y);
        Vector2 facingDirection = getFacingDirection();

        // Update state manager BEFORE combat helper
        stateManager.update(this, delta);

        // Update combat helper AFTER the state manager
        meleeCombatHelper.update(delta, position, facingDirection, isFacingRight, lastDirection);

        // Set the main sprite appropriately based on combat state
        if (meleeCombatHelper.isAttacking()) {
            // If attacking, use the attack sprite from the helper
            renderer.setMainSprite(meleeCombatHelper.getSprite());
        } else {
            // Otherwise use the normal sprite
            renderer.setMainSprite(sprite);
        }

        // Update renderer last
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

    public SimpleCombatWalkingHelper getCombatWalkingHelper() {
        return this.combatWalkingHelper;
    }

    public boolean hasValidTarget() {
        return true;
    }
}