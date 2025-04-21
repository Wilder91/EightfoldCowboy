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
import helper.combat.MeleeCombatHelper;
import helper.movement.*;

import helper.state.PlayerMovementStateManager;
import objects.GameEntity;
import objects.inanimate.Door;

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
    private SpriteWalkingHelper runningHelper;
    private MeleeCombatHelper meleeHelper;
    //private SpriteWalkingHelper walkingHelper;
    private SpriteIdleHelper idleHelper;
    private String lastDirection = "idleDown";
    private boolean justSwitchedHelpers;
    private PointLight playerLight;
    private RayHandler rayHandler;
    private String weaponType;
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



    public Player(float x, float y, float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets) {

        super(width, height, body, screenInterface, gameAssets);
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
        this.justSwitchedHelpers = false;
        this.rayHandler = new RayHandler(screenInterface.getWorld());
        int[] runningFrameCounts = {8, 8, 8, 8, 8}; // Ensure frame counts are non-zero
        int[] idleFrameCounts = {18, 1, 8, 18, 4};
        int[] meleeFrameCounts = {17, 17, 17, 17, 17};
        this.weaponType = "sword";
        this.runningHelper = new SpriteWalkingHelper(gameAssets, "character", "character", runningFrameCounts, false);
        this.idleHelper = new SpriteIdleHelper(gameAssets, "character", "character", idleFrameCounts, 0f);
        this.meleeHelper = new MeleeCombatHelper(gameAssets, "character", "character", weaponType, meleeFrameCounts, 10f, screenInterface.getWorld(), .02f, ContactType.ATTACK, ContactType.ENEMY, screenInterface);
        this.inputHandler = new PlayerInputHelper(this);
        this.sprite = new Sprite();
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


// Don't forget to dispose the shape when done
        sensorShape.dispose();
    }

    public SpriteIdleHelper getIdleHelper() {
        return idleHelper;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public String getLastDirection() {
        return lastDirection;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public SpriteWalkingHelper getRunningHelper() {
        return runningHelper;
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

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State state) {
        this.currentState = state;
    }

    public MeleeCombatHelper getMeleeHelper() {
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

        inputHandler.processInput();
        updateSensor();

        // Let the state manager handle states instead of your switch statement
        stateManager.updateState(this, delta);

        // Update melee combat helper
        Vector2 position = new Vector2(x, y);
        Vector2 facingDirection = getFacingDirection();
        meleeHelper.update(delta, position, facingDirection, isFacingRight, lastDirection);

        setDepth(y);
    }

    public void createBody(World world, Door door) {
        this.body = BodyHelperService.createBody(
                x, y, width, height, false, world, ContactType.PLAYER, 1);
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
    }


    private void updateSensor() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            // Toggle between large and small sensor
        }
    }

    public void takeDamage(){
        Sound sound = screenInterface.getGameAssets().getSound("sounds/bison-sound.mp3");
        sound.play(0.05f);
        System.out.println("five damage!");
    }
    // Helper method to get the facing direction as a Vector2
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

    public void setBody(Body body) {
        this.body = body;
    }

    public int getId() {
        return 1;
    }
}