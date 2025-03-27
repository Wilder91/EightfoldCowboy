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
        System.out.println("x: " + Gdx.graphics.getWidth() / 2);
        this.initialPosition = new Vector2(300, 100);
        int[] runningFrameCounts = {8, 8, 8, 8, 8}; // Ensure frame counts are non-zero
        int[] walkingFrameCounts = {8, 8, 8, 8, 8};
        this.runningHelper = new SpriteRunningHelper(gameAssets, "Character", runningFrameCounts, false);
        //this.walkingHelper = new SpriteWalkingHelper(gameAssets, "Character", walkingFrameCounts, false);
        this.idleHelper = new SpriteIdleHelper(gameAssets,"Character");
        this.sprite = new Sprite();
        this.sprite.setSize(width, height);
    }

    public void setPosition(int setX, int setY) {
        x = setX;
        y = setY;
    }



    @Override
    public void update(float delta) {
        stateTime += delta; // Update the state time
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        checkUserInput();
        updateAnimation(delta);
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
        sprite.draw(batch);
        sprite.setPosition(x - width / 2, y - height / 2);
    }



    private void checkUserInput() {
        velX = 0;
        velY = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velX = 1;
            if (!isFacingRight) {
                isFacingRight = true;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -1;
            if (isFacingRight) {
                isFacingRight = false;
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velY = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velY = -1;
        }
        body.setLinearVelocity(velX * speed, velY * speed);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)) {
            speed = speed * 3 / 2;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            speed = originalSpeed;
        }
    }

    private void updateAnimation(float delta) {
        Vector2 velocity = body.getLinearVelocity();
        Vector2 absVelocity = new Vector2(Math.abs(velocity.x), Math.abs(velocity.y));
        if (velocity.y > 0.1f) {
            lastDirection = "idleUp";
        } else if (velocity.y < -0.1f) {
            lastDirection = "idleDown";
        } else if (Math.abs(velocity.x) > 0.1f) {
            lastDirection = "idleSide";
        }
        if (absVelocity.x > .1 || absVelocity.y > .1){
            runningHelper.updateAnimation(velocity, delta);
            sprite = runningHelper.getSprite();
        } else{
            //System.out.println("WALK");
            idleHelper.setDirection(lastDirection);
            idleHelper.updateAnimation(velocity, delta);
            sprite = idleHelper.getSprite();
            //walkingHelper.updateAnimation(velocity, delta);
            //sprite = walkingHelper.getSprite();
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
