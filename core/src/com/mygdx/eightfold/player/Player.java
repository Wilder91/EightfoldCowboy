package com.mygdx.eightfold.player;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.ContactType;
import com.mygdx.eightfold.GameAssets;
import helper.combat.MeleeCombatHelper;
import helper.movement.*;

import helper.state.PlayerStateManager;
import objects.GameEntity;
import objects.inanimate.Door;

import javax.swing.plaf.nimbus.State;

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
    private ScreenInterface screenInterface;
    private Vector2 initialPosition;
    private SpriteRunningHelper runningHelper;
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
    private boolean isSensorSmall = false;
    private float largeSensorRadius = 3.0f;
    private float smallSensorRadius = 0.5f;
    private State currentState;

    private PlayerStateManager stateManager;



    public Player(float x, float y, float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets) {

        super(width, height, body, screenInterface, gameAssets);
        this.x = x;
        this.y = y;
        setDepth(y);
        this.width = width;
        this.height = height;
        this.stateManager = new PlayerStateManager();
        this.currentState = State.IDLE;
        this.speed = 2.5f;
        this.originalSpeed = speed;
        this.isFacingRight = true;
        this.body = body;
        this.gameAssets = gameAssets;
        //this.sensorRadius = 3.0f;
        this.justSwitchedHelpers = false;
        this.rayHandler = new RayHandler(screenInterface.getWorld());
        //System.out.println("x: " + Gdx.graphics.getWidth() / 2);
        //this.initialPosition = new Vector2(300, 100);
        int[] runningFrameCounts = {8, 8, 8, 8, 8}; // Ensure frame counts are non-zero
        int[] idleFrameCounts = {18, 1, 8, 18, 4};
        int[] meleeFrameCounts = {17, 17, 17, 17, 17};
        this.weaponType = "sword";
        this.runningHelper = new SpriteRunningHelper(gameAssets, "character", "character", runningFrameCounts, false);
        //this.walkingHelper = new SpriteWalkingHelper(gameAssets, "Character", walkingFrameCounts, false);
        this.idleHelper = new SpriteIdleHelper(gameAssets, "character", "character", idleFrameCounts, 0f);
        this.meleeHelper = new MeleeCombatHelper(gameAssets, "character", "character", weaponType, meleeFrameCounts, 10f, screenInterface.getWorld());
        this.sprite = new Sprite();
        this.sprite.setSize(width, height);
        this.swordSound = gameAssets.getSound("sounds/whoosh-DrMinky.wav");
        playerLight = new PointLight(rayHandler, 128, new Color(.5f, .4f, .5f, .8f), .4f, 0, 0);
        playerLight.setSoftnessLength(1f);
        playerLight.setContactFilter(ContactType.LIGHT.getCategoryBits(),
                ContactType.LIGHT.getMaskBits(),
                (short) 0);


//        System.out.println("playerLight x: " + x * PPM);
//        System.out.println("playerLight y: " + y * PPM);
        playerLight.setPosition(x + .1f, y);
        FixtureDef sensorFixtureDef = new FixtureDef();
        //sensorFixtureDef = new FixtureDef();
        sensorFixtureDef.isSensor = true;
        sensorShape = new CircleShape();
        // detection radius
        sensorShape.setPosition(new Vector2(0.0f, 0)); // position relative to body center
        sensorFixtureDef.shape = sensorShape;
        //sensorFixtureDef.userData = "playerSensor";


        // sensorFixtureDef.userData = "enemyDetector";

// Add the fixture to your body
        body.createFixture(sensorFixtureDef);

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

    public SpriteRunningHelper getRunningHelper() {
        return runningHelper;
    }

    public Sprite getSprite() {
        return  sprite;
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

        checkUserInput();
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





    // Helper method to update direction variables
    private void updateDirectionVariables(Vector2 velocity) {
        // Update isFacingRight
        if (velocity.x < -0.1f) {
            isFacingRight = false;
        } else if (velocity.x > 0.1f) {
            isFacingRight = true;
        }

        // Update lastDirection
        if (velocity.y > 0.1f) {
            if (Math.abs(velocity.x) > 0.1f) {
                lastDirection = "idleDiagonalUp";
            } else {
                lastDirection = "idleUp";
            }
        } else if (velocity.y < -0.1f) {
            if (Math.abs(velocity.x) > 0.1f) {
                lastDirection = "idleDiagonalDown";
            } else {
                lastDirection = "idleDown";
            }
        } else if (Math.abs(velocity.x) > 0.1f) {
            lastDirection = "idleSide";
        }
    }



    public void screenChange(World world, Door door) {
        this.body = BodyHelperService.createBody(
                door.getBody().getPosition().x, door.getBody().getPosition().y, width, height, false, world, ContactType.PLAYER, 1);
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public void setPosition(float x, float y) {
        if (body != null) {
            body.setTransform(x / PPM, y / PPM, body.getAngle()); // Set initial position
        }
    }

    public void changeState(State newState) {
        // Optional: Handle exit/enter logic
        this.currentState = newState;
    }

    @Override
    public void render(SpriteBatch batch) {
        float centerX = x;  // The physics body center X
        float centerY = y;  // The physics body center Y

        if (meleeHelper.isAttacking()) {
            // The meleeHelper already handles positioning internally
            speed = 0f;
            meleeHelper.getAttackSprite().draw(batch);
        } else {
            // Position the sprite so its center aligns with the body center
            speed = originalSpeed;

            // Adjust the sprite position to center it on the body
            sprite.setPosition(
                    centerX - sprite.getWidth() / 2,
                    centerY - sprite.getHeight() / 2
            );
            sprite.draw(batch);

            // For debugging: draw a small rectangle at the body center
            // shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
            // shapeRenderer.setColor(Color.RED);
            // shapeRenderer.rect(centerX - 2, centerY - 2, 4, 4);
            // shapeRenderer.end();
        }
    }

    private void checkUserInput() {
        float velX = 0;
        float velY = 0;

        // Movement input
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velX = 1;
            if (!isFacingRight) isFacingRight = true;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -1;
            if (isFacingRight) isFacingRight = false;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velY = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velY = -1;
        }

        // Attack input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && currentState != State.ATTACKING) {
            //swordSound.play(.5f);

            stateManager.changeState(this, State.ATTACKING);
            return;

        }

        // Combine input direction and normalize
        Vector2 direction = new Vector2(velX, velY);
        if (direction.len() > 0) {
            direction.nor();
        }

        // Only allow movement if not attacking
        if (currentState != State.ATTACKING) {
            float currentSpeed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? speed * 1.5f : speed;
            body.setLinearVelocity(direction.x * currentSpeed, direction.y * currentSpeed);
        } else {
            body.setLinearVelocity(0, 0); // Stop movement while attacking
        }

    }

    private void updateSensor() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            // Toggle between large and small sensor

            //isSensorSmall = !isSensorSmall;

            // Remove the old sensor fixture
            for (com.badlogic.gdx.physics.box2d.Fixture fixture : body.getFixtureList()) {
                if ("playerSensor".equals(fixture.getUserData())) {
                    body.destroyFixture(fixture);
                    break;
                }
            }

            // Create a new sensor with the updated radius
            float newRadius = isSensorSmall ? smallSensorRadius : largeSensorRadius;

            // Update and create new sensor
            sensorShape = new CircleShape();
            sensorShape.setRadius(newRadius);
            sensorShape.setPosition(new Vector2(0.0f, 0));

            sensorFixtureDef = new FixtureDef();
            sensorFixtureDef.isSensor = true;
            sensorFixtureDef.shape = sensorShape;
            //sensorFixtureDef.userData = "playerSensor";

            // Add the new fixture
            body.createFixture(sensorFixtureDef);

            // Now we can dispose the shape
            sensorShape.dispose();

            System.out.println("Sensor radius changed to: " + newRadius);
        }
    }

    private void updateAnimation(float delta) {
        if (meleeHelper.isAttacking()) {
            return;
        }
        Vector2 velocity = body.getLinearVelocity();
        Vector2 absVelocity = new Vector2(Math.abs(velocity.x), Math.abs(velocity.y));
        idleHelper.getFacingDirection(velocity, absVelocity);
        if (velocity.y > 0.1f) {
            if (Math.abs(velocity.x) > 0.1f) {
                lastDirection = "idleDiagonalUp";
            } else {
                lastDirection = "idleUp";
            }
        } else if (velocity.y < -0.1f) {
            if (Math.abs(velocity.x) > 0.1f) {
                lastDirection = "idleDiagonalDown";
            } else {
                lastDirection = "idleDown";
            }
        } else if (Math.abs(velocity.x) > 0.1f) {
            lastDirection = "idleSide";
        }

        // Update facing direction
        if (velocity.x < -0.1f) {
            isFacingRight = false;
        } else if (velocity.x > 0.1f) {
            isFacingRight = true;
        }
        if (absVelocity.x > .01 || absVelocity.y > .01) {
            runningHelper.updateAnimation(velocity, delta);
            sprite = runningHelper.getSprite();
        } else {
            //System.out.println("Last direction: " + lastDirection);
            idleHelper.setDirection(lastDirection);
            //System.out.println("is facing right: " + isFacingRight);
            idleHelper.setFacingRight(isFacingRight); // Pass flip info here
            idleHelper.update(delta);
            sprite = idleHelper.getSprite();
        }

        //System.out.println("Player Velocity: " + absVelocity);

        // Flip the sprite if needed
        if (velocity.x < 0) {
            sprite.flip(true, false);
            isFacingRight = false;
        } else if (velocity.x > 0) {
            sprite.flip(false, false);
            isFacingRight = true;
        }
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