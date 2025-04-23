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
import static objects.GameEntity.State.ATTACKING;

public class ThicketSaint extends GameEntity {

    private Sprite sprite;
    private SimpleIdleHelper idleHelper;
    private EnemyStateManager stateManager;
    private SimpleSpriteWalkingHelper walkingHelper;
    private MeleeCombatHelper meleeCombatHelper;
    private SimpleAnimator animator;
    private String lastDirection = "idleDown";

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
    private float largeSensorRadius = .5f;
    private float smallSensorRadius = .5f;
    private GameEntity.State currentState = GameEntity.State.IDLE;
    private float hp;

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
        this.walkingHelper = new SimpleSpriteWalkingHelper(gameAssets, entityType, this.entityName, walkingFrameCounts, false, .2f) {
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
        this.stateManager.changeState(this, GameEntity.State.IDLE);

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

    // Getters and setters needed by the EnemyStateManager
    public SimpleIdleHelper getSimpleIdleHelper() {
        return this.idleHelper;
    }

    public SimpleSpriteWalkingHelper getSimpleWalkingHelper() {
        return this.walkingHelper;
    }

    public MeleeCombatHelper getMeleeCombatHelper() {
        return this.meleeCombatHelper;
    }

    public MeleeCombatHelper getMeleeHelper() {
        return this.meleeCombatHelper;
    }

    public void beginAttack() {
        System.out.println("okay");
        //stateManager.changeState(this, ATTACKING);
       //stateManager.changeState(this, GameEntity.State.ATTACKING);
    }

    public String getLastDirection() {
        return lastDirection;
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
        Sound sound = screenInterface.getGameAssets().getSound("sounds/bison-sound.mp3");
        sound.play(0.05f);
        this.hp -= 5;
        stateManager.changeState(this, ATTACKING);
        System.out.println("thicketsaint hp: " + hp);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        resetDepthToY();

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


    private Vector2 getFacingDirection() {
        Vector2 direction = new Vector2(0, 0);

        switch (lastDirection) {
            case "idleUp":
                direction.set(0, 1);
                break;
            case "idleDown":
                direction.set(0, -1);
                break;
            case "idleSide":
                direction.set(isFacingRight ? 1 : -1, 0);
                break;
            case "idleDiagonalUp":
                direction.set(isFacingRight ? 1 : -1, 1);
                break;
            case "idleDiagonalDown":
                direction.set(isFacingRight ? 1 : -1, -1);
                break;
            default:
                direction.set(isFacingRight ? 1 : -1, 0);
                break;
        }

        return direction.nor();
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