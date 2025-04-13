package com.mygdx.eightfold.player;

import box2dLight.PointLight;
import box2dLight.RayHandler;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.ContactType;
import com.mygdx.eightfold.GameAssets;
import helper.combat.MeleeCombatHelper;
import helper.movement.*;

import objects.GameEntity;
import objects.inanimate.Door;

import static helper.Constants.PPM;

public class Player extends GameEntity {
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


    public Player(float x, float y, float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets) {
        super(width, height, body, screenInterface, gameAssets);
        this.x = x;
        this.y = y;
        setDepth(y);
        this.width = width;
        this.height = height;
        this.speed = 2.5f;
        this.originalSpeed = speed;
        this.isFacingRight = true;
        this.body = body;
        this.gameAssets = gameAssets;
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
        playerLight = new PointLight(rayHandler, 128, new Color(.5f, .4f, .5f, .8f), .4f, 0, 0);
        playerLight.setSoftnessLength(1f);
        playerLight.setContactFilter(ContactType.LIGHT.getCategoryBits(),
                ContactType.LIGHT.getMaskBits(),
                (short) 0);


//        System.out.println("playerLight x: " + x * PPM);
//        System.out.println("playerLight y: " + y * PPM);
        playerLight.setPosition(x + .1f, y);
        //resizeBody(width, height);

    }

    @Override
    public void update(float delta) {

        stateTime += delta; // Update the state time
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        checkUserInput();
        updateAnimation(delta);

        // Update melee combat helper
        Vector2 position = new Vector2(x, y);
        Vector2 facingDirection = getFacingDirection();
        meleeHelper.update(delta, position, facingDirection, isFacingRight, lastDirection);

        setDepth(y);
        //if (justSwitchedHelpers) {
        // idleHelper.resetStateTime(); // <— Add a method like this if needed
        //}
    }

    public void createBody(World world, Door door) {
        this.body = BodyHelperService.createBody(
                x , y, width * 4, height, false, world, ContactType.PLAYER, 1);
    }

    public void resizeBody(float newWidth, float newHeight) {
        World world = body.getWorld();
        Vector2 position = body.getPosition();
        float angle = body.getAngle();

        // Destroy old body
        world.destroyBody(body);

        // Create new body with new dimensions
        this.body = BodyHelperService.createBody(
                position.x, position.y,
                newWidth / PPM, newHeight / PPM,
                false, world, ContactType.PLAYER, 1);

        // Update visual dimensions
        this.width = newWidth;
        this.height = newHeight;
    }

    public void screenChange(World world, Door door) {
        this.body = BodyHelperService.createBody(
                door.getBody().getPosition().x, door.getBody().getPosition().y, width, height, false, world, ContactType.PLAYER, 1);
    }

    public void setSpeed(Float speed){
        this.speed = speed;
    }

    public void setPosition(float x, float y) {
        if (body != null) {
            body.setTransform(x / PPM, y / PPM, body.getAngle()); // Set initial position
        }
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

        // Check for attack input (spacebar)
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            Vector2 playerPosition = new Vector2(x, y);
            meleeHelper.startAttack(lastDirection, playerPosition);
        }

        // Combine input direction and normalize to avoid diagonal speed boost
        Vector2 direction = new Vector2(velX, velY);
        if (direction.len() > 0) {
            direction.nor();
        }

        // Optional sprint logic
        float currentSpeed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? speed * 1.5f : speed;

        // Apply movement
        body.setLinearVelocity(direction.x * currentSpeed, direction.y * currentSpeed);
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

        switch(lastDirection) {
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