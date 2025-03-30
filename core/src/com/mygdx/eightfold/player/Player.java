package com.mygdx.eightfold.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.ContactType;
import com.mygdx.eightfold.GameAssets;
import helper.movement.*;

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
    //private SpriteWalkingHelper walkingHelper;
    private SpriteIdleHelper idleHelper;
    private String lastDirection = "idleDown";
    private boolean justSwitchedHelpers;

    public Player(float x, float y, float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets) {
        super(width, height, body, screenInterface, gameAssets);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = 2.5f;
        this.originalSpeed = speed;
        this.isFacingRight = true;
        this.body = body;
        this.gameAssets = gameAssets;
        this.justSwitchedHelpers = false;
        //System.out.println("x: " + Gdx.graphics.getWidth() / 2);
        //this.initialPosition = new Vector2(300, 100);
        int[] runningFrameCounts = {8, 8, 8, 8, 8}; // Ensure frame counts are non-zero
        int[] idleFrameCounts = {18, 1, 8, 18, 4};
        this.runningHelper = new SpriteRunningHelper(gameAssets, "Character", "Character", runningFrameCounts, false);
        //this.walkingHelper = new SpriteWalkingHelper(gameAssets, "Character", walkingFrameCounts, false);
        this.idleHelper = new SpriteIdleHelper(gameAssets,"Character", "Character", idleFrameCounts, 0f);
        this.sprite = new Sprite();
        this.sprite.setSize(width, height);



    }





    @Override
    public void update(float delta) {
        stateTime += delta; // Update the state time
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        checkUserInput();
        updateAnimation(delta);
        //if (justSwitchedHelpers) {
           // idleHelper.resetStateTime(); // <— Add a method like this if needed
        //}

    }

    public void createBody(World world, Door door) {
        this.body = BodyHelperService.createBody(
                x, y, width, height, false, world, ContactType.PLAYER, 1);
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
        sprite.setPosition(x - width / 2, y - height / 2);
        sprite.draw(batch);

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

    public void setBody(Body body) {
        this.body = body;

    }

    public int getId() {
        return 1;
    }


}
