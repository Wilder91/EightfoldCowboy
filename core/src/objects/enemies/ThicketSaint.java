package objects.enemies;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.*;
import helper.animation.AnimationHelper;
import helper.combat.EnemyMeleeCombatHelper;
import helper.combat.PlayerMeleeCombatHelper;
import helper.combat.SpriteDeathHelper;
import helper.movement.SimpleCombatWalkingHelper;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteWalkingHelper;
import helper.movement.SpriteMovementHelper;
import helper.state.EnemyStateManager;
import helper.ui.HealthBar;
import objects.GameEntity;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;
import static helper.ContactType.DEAD;
import static objects.GameEntity.State.*;

public class ThicketSaint extends GameEntity {

    private Sprite sprite;
    private SimpleIdleHelper idleHelper;
    private EnemyStateManager stateManager;
    private SpriteMovementHelper movementHelper;
    private SimpleSpriteWalkingHelper walkingHelper;
    private SimpleCombatWalkingHelper combatWalkingHelper;
    private SpriteDeathHelper deathHelper;
    private EnemyMeleeCombatHelper meleeCombatHelper;
    private SimpleAnimator animator;
    private SensorHelper sensorHelper;
    private AnimationHelper animationHelper;
    private String lastDirection = "idleDown";
    private float attackCounter;
    private long lastAttackTime = 0;
    private static final long ATTACK_COOLDOWN = 1200;
    private HealthBar healthBar;
    private int id;
    private float stateTime = 0;
    private ScreenInterface screenInterface;
    private EntityRenderer renderer;
    private String entityType;
    private String entityName;
    private EntityMovement movement;
    private Fixture sensorFixture;
    private float largeSensorRadius = 5f;
    private float smallSensorRadius = 2f;
    private GameEntity.State currentState = GameEntity.State.IDLE;
    private float hp;
    private boolean beginPursuit;
    private boolean dead;




    public ThicketSaint(int id, float width, float height, Body body, ScreenInterface screenInterface,
                        GameAssets gameAssets, String entityType, String entityName, float hp) {
        super(width, height, body, screenInterface, gameAssets, hp);
        this.sprite = new Sprite();
        this.id = id;
        this.sprite.setSize(width, height);
        System.out.println(entityType + ", " + entityName);
        this.entityName = entityName;
        this.entityType = entityType;
        this.screenInterface = screenInterface;
        this.hp = hp;
        ThicketSaintManager.addEnemy(this);
        MassData massData = new MassData();
        massData.mass = 2.5f;  // Very high mass
        massData.center.set(0, 0);  // Center of mass at body center
        massData.I = 1000.0f;  // High moment of inertia too
        body.setMassData(massData);
        this.deathHelper = new SpriteDeathHelper(gameAssets, entityType, entityName, FRAME_DURATION);
        this.healthBar = new HealthBar(20, 1, hp, hp, true);
        healthBar.setOffsetY(20);
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
        this.movementHelper = new SpriteMovementHelper(gameAssets, this, entityType, entityName, true, FRAME_DURATION, "idle", true);
        this.meleeCombatHelper = new EnemyMeleeCombatHelper(gameAssets, entityType, entityName, "sword",  5, screenInterface.getWorld(),
                .07f, ContactType.ENEMY, ContactType.PLAYER, screenInterface, 1f, .5f);
        //this.combatWalkingHelper = new SimpleCombatWalkingHelper(gameAssets,  entityType, entityName, combatFrameCounts, false,FRAME_DURATION);
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
        this.dead = false;
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

    public SpriteMovementHelper getMovementHelper(){
        return movementHelper;
    }

    public void pursuePlayer() {
        // Get player position
        if (body == null) return;
        Vector2 playerPosition = screenInterface.getPlayer().getBody().getPosition();
        Vector2 myPosition = this.body.getPosition();

        // Calculate direction vector to player
        Vector2 direction = new Vector2(playerPosition.x - myPosition.x, playerPosition.y - myPosition.y);

        // Calculate distance to player
        float distanceToPlayer = direction.len();

        // Define minimum distance to stop (in Box2D units)
        float stopDistance = .9f; // Adjust this value as needed

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
            } else if (distanceToPlayer > stopDistance) {
                // Closer - use combat walking
                //sprite.flip(true, true);
                newState = PURSUING;
            }

            // Apply velocity toward player
            this.body.setLinearVelocity(direction.x * movementSpeed, direction.y * movementSpeed);
        } else {
            // We're close enough, stop moving but don't reset velocity to zero immediately
            // This creates a more natural slowing down effect
            Vector2 currentVelocity = this.body.getLinearVelocity();
            this.body.setLinearVelocity(0, 0);

            if (distanceToPlayer < stopDistance && currentVelocity.len() < 0.2f) {
                if (getCurrentState() != ATTACKING && getCurrentState() != DYING) {
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

    public void dispose(){
        screenInterface.removeEntity(this);
    }

    //@Override
    public void hideHealthBar() {
        healthBar.dispose();
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





    @Override
    public void takeDamage() {
        //stateManager.changeState(this, ATTACKING);

        Sound sound = screenInterface.getGameAssets().getSound("sounds/bison-sound.mp3");
        sound.play(0.05f);
        this.hp -= 5;
        healthBar.updateHealth(hp);
        //stateManager.changeState(this, ATTACKING);
        System.out.println("thicketsaint hp: " + hp);
    }

    public int getId() {
        return this.id;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        //System.out.println(body.getUserData());
        // Split body-dependent and body-independent operations
        if (body != null) {
            // Body-dependent physics updates
            x = body.getPosition().x * PPM;
            y = body.getPosition().y * PPM;
            resetDepthToY();

            if (beginPursuit) {
                pursuePlayer();
            }

            // Handle sprite flipping based on velocity
            float vx = body.getLinearVelocity().x;
            if (vx > 0.4f && !isFacingRight) {
                isFacingRight = true;
                //sprite.flip(true, false);
                System.out.println("Flipping sprite to face right");
            } else if (vx < -0.4f && isFacingRight) {
                isFacingRight = false;
                //sprite.flip(true, false);
                System.out.println("Flipping sprite to face left");
            }
        } else {
            // Log the issue while continuing with non-physics updates
            System.out.println("Warning: Entity has null body during update");
        }

        // Body-independent updates that should happen regardless

        // Update movement
        movement.update(delta);

        if (hp < 1) {
            death();
        }

        // Get current position and direction for combat helper
        Vector2 position = new Vector2(x, y);
        Vector2 facingDirection = getFacingDirection();

        // Update state manager
        stateManager.update(this, delta);

        // Update combat helper
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

    public void death() {
        if (!dead) {
            stateManager.changeState(this, DYING);
            healthBar.setVisible(false);
            Array<Fixture> fixtures = body.getFixtureList();
            for (Fixture fixture : fixtures) {
                fixture.setSensor(true);
            }
            //sensorHelper.
            // Mark as dead

        } else {
            // This is called on subsequent updates after death animation completes
            screenInterface.removeEntity(this);
            body.setUserData(new BodyUserData(id, DEAD, body, this));

        }
    }


    @Override
    public void render(SpriteBatch batch) {
        // Draw character as normal


        renderer.render(batch);

        // We need to flush the batch before switching to ShapeRenderer
        batch.flush();

        // End the SpriteBatch to use ShapeRenderer
        batch.end();

        // Make sure we're passing the correct coordinates
        // Use PPM to ensure coordinates match what's expected
        healthBar.render(batch.getProjectionMatrix(), x, y);

        // Start the batch again
        batch.begin();
    }

    public SpriteDeathHelper getDeathHelper() {
        return deathHelper;
    }
}