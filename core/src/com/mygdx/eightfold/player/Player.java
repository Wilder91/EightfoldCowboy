package com.mygdx.eightfold.player;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.ContactType;
import com.mygdx.eightfold.GameAssets;
import helper.combat.PlayerMeleeCombatHelper;
import helper.movement.*;

import helper.state.PlayerMovementStateManager;
import helper.ui.HealthBar;
import objects.GameEntity;
import objects.inanimate.Door;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class Player extends GameEntity {

    public enum State {
        IDLE,
        RUNNING,
        ATTACKING

    }

    private final float originalSpeed;
    private GameAssets gameAssets;
    private Sprite sprite;
    private boolean isFacingRight;
    private float stateTime;
    private SpriteMovementHelper movementHelper;
    private String action;
    private PlayerMeleeCombatHelper meleeHelper;
    //private SpriteWalkingHelper walkingHelper;
    //private SpriteIdleHelper idleHelper;
    private String lastDirection = "idleDown";
    private boolean justSwitchedHelpers;
    private PointLight playerLight;
    private RayHandler rayHandler;
    private String equippedWeapon;
    private Sound swordSound;
    private CircleShape sensorShape;
    private FixtureDef sensorFixtureDef;
    private Fixture sensorFixture;
    private boolean isSensorSmall = false;
    private float largeSensorRadius = 3.0f;
    private float smallSensorRadius = 1.5f;
    private State currentState;
    private PlayerInputHelper inputHandler;
    private PlayerRenderer renderer;
    private PlayerMovementStateManager stateManager;
    private ScreenInterface screenInterface;
    private float hp;
    private HealthBar healthBar;



    public Player(float x, float y, float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets, Float hp) {

        super(width, height, body, screenInterface, gameAssets, hp);
        this.x = x;
        this.y = y;
        setDepth(y);
        this.width = width;
        this.height = height;
        this.stateManager = new PlayerMovementStateManager();
        this.screenInterface = screenInterface;
        this.currentState = State.IDLE;
        this.speed = 2.5f;
        this.originalSpeed = speed;
        this.isFacingRight = true;
        this.body = body;
        this.gameAssets = gameAssets;
        this.hp = hp;
        MassData massData = new MassData();
        massData.mass = 2.5f;  // Very high mass
        massData.center.set(0, 0);  // Center of mass at body center
        massData.I = 1000.0f;  // High moment of inertia too
        body.setMassData(massData);
        this.justSwitchedHelpers = false;
        this.rayHandler = new RayHandler(screenInterface.getWorld());
        int[] runningFrameCounts = {8, 8, 8, 8, 8}; // Ensure frame counts are non-zero
        int[] idleFrameCounts = {18, 1, 8, 18, 4};
        this.equippedWeapon = "sword";
        this.healthBar = new HealthBar(20, 1.5f, hp, hp, false);
        healthBar.setOffsetY(20);
        healthBar.setVisible(false);
        //this.runningHelper = new SpriteWalkingHelper(gameAssets, this, "character", "character", runningFrameCounts, false);
//        this.idleHelper = new SpriteIdleHelper(gameAssets, this, "character",
//                "character", idleFrameCounts, 0f);
        this.action = "idle";
        this.movementHelper = new SpriteMovementHelper(gameAssets, this, "character", "character",
                false, FRAME_DURATION, action, false);
        this.meleeHelper = new PlayerMeleeCombatHelper(gameAssets, "character", "character",
                equippedWeapon, 10f, screenInterface.getWorld(),
                .03f, ContactType.ATTACK, ContactType.ENEMY, screenInterface, 1f, 1f);
        this.inputHandler = new PlayerInputHelper(this);
        this.sprite = movementHelper.getSprite();
        this.sprite.setSize(width, height);
        this.swordSound = gameAssets.getSound("sounds/whoosh.mp3");
        playerLight = new PointLight(rayHandler, 128, new Color(.5f, .4f, .5f, .8f), .4f, 0, 0);
        playerLight.setSoftnessLength(1f);
        playerLight.setContactFilter(ContactType.LIGHT.getCategoryBits(),
                ContactType.LIGHT.getMaskBits(),
                (short) 0);
        playerLight.setPosition(x + .1f, y);
        this.renderer = new PlayerRenderer(this, meleeHelper);
        this.sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.isSensor = true;
        sensorShape = new CircleShape();
        sensorShape.setRadius(largeSensorRadius);
        sensorShape.setPosition(new Vector2(0.0f, 0)); // position relative to body center
        sensorFixtureDef.shape = sensorShape;
// Add the fixture to your body
        this.sensorFixture = body.createFixture(sensorFixtureDef);
        sensorFixture.setUserData("playerSensor");


 //Don't forget to dispose the shape when done
        sensorShape.dispose();
    }



    public SpriteIdleHelper getIdleHelper() {
        return idleHelper;
    }

    public SpriteMovementHelper getMovementHelper(){
        return movementHelper;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public  void setAction(String action){this.action = action; }

    public String getLastDirection() {
        return lastDirection;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }


    public Sprite getSprite() {
        return  sprite;
    }


    public void triggerAttack() {
        swordSound.play(.2f);
        stateManager.changeState(this, State.ATTACKING);
    }

    public void toggleSensor() {
        // Remove the old sensor fixture
        for (com.badlogic.gdx.physics.box2d.Fixture fixture : body.getFixtureList()) {
            if ("playerSensor".equals(fixture.getUserData())) {
                body.destroyFixture(fixture); // This line was missing - need to actually destroy the fixture
                System.out.println("Removed old sensor fixture");
                break;
            }
        }
        isSensorSmall = !isSensorSmall;
        // Create a new sensor with the updated radius
        float newRadius = isSensorSmall ? smallSensorRadius : largeSensorRadius;
        // Update and create new sensor
        sensorShape = new CircleShape();
        sensorShape.setRadius(newRadius);
        sensorShape.setPosition(new Vector2(0.0f, 0));

        sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.isSensor = true;
        sensorFixtureDef.shape = sensorShape;

        // Add the new fixture
        sensorFixture = body.createFixture(sensorFixtureDef);
        sensorFixture.setUserData("playerSensor"); // This line was commented out - need to set userData
        // Now we can dispose the shape
        sensorShape.dispose();

        System.out.println("Sensor radius changed to: " + newRadius);
    }

    public void move(Vector2 direction, float speedMultiplier) {
        float currentSpeed = speed * speedMultiplier;
        body.setLinearVelocity(direction.x * currentSpeed, direction.y * currentSpeed);
    }

    public void stopMovement() {
        body.setLinearVelocity(0, 0);
    }

    public State getCurrentPlayerState() {
        return currentState;
    }

    public void setCurrentState(State state) {
        this.currentState = state;
    }

    public PlayerMeleeCombatHelper getMeleeHelper() {
        return meleeHelper;
    }
    public void setFacingRight(boolean b) {
        isFacingRight = b;
    }

    public void setLastDirection(String newDirection) {
        lastDirection = newDirection;
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        //System.out.println(movementHelper.getFrameIndex());
        inputHandler.processInput();
        updateSensor();

        // Let the state manager handle states instead of your switch statement
        stateManager.updateState(this, delta);

        // Update melee combat helper
        Vector2 position = new Vector2(x, y);
        Vector2 facingDirection = getFacingDirection();
        meleeHelper.update(delta, position, facingDirection, isFacingRight, lastDirection);

        resetDepthToY();
    }

    public void createBody(World world, Door door) {
        this.body = BodyHelperService.createBody(
                x, y, width , height, false, world, ContactType.PLAYER, 1);
    }

    public void screenChange(World world, Door door) {
        this.body = BodyHelperService.createBody(
                door.getBody().getPosition().x, door.getBody().getPosition().y, width, height, false, world, ContactType.PLAYER, 1);
    }



    public void setPosition(float x, float y) {
        if (body != null) {
            body.setTransform(x / PPM, y / PPM, body.getAngle()); // Set initial position
        }
    }


    @Override
    public void render(SpriteBatch batch) {
        renderer.render(batch);
        batch.end();

        // Make sure we're passing the correct coordinates
        // Use PPM to ensure coordinates match what's expected
        healthBar.render(batch.getProjectionMatrix(), x, y);

        // Start the batch again
        batch.begin();
    }

    public void parry(){
        Sound parrySound = gameAssets.getSound("sounds/sword-clash.wav");
        parrySound.play();

    }

    private void updateSensor() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            // Toggle between large and small sensor
        }
    }

    public void takeDamage(){
        Sound sound = screenInterface.getGameAssets().getSound("sounds/bison-sound.mp3");
        sound.play(0.05f);
        this.hp -= 5;
        healthBar.updateHealth(hp);
        healthBar.setVisible(true);
        System.out.println("player hp: " + hp);
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public int getId() {
        return 1;
    }
}