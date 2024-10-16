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
import helper.movement.SpriteRunningHelper;

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

    public Player(float x, float y, float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets) {
        super(width, height, body, screenInterface, gameAssets);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.speed = 3.5f;
        this.originalSpeed = speed;
        this.isFacingRight = true;
        this.body = body;
        this.gameAssets = gameAssets;
        System.out.println("x: " + Gdx.graphics.getWidth() / 2);
        this.initialPosition = new Vector2(300, 100);
        int[] frameCounts = {8, 8, 8, 8, 8}; // Ensure frame counts are non-zero
        this.runningHelper = new SpriteRunningHelper(gameAssets, "Character", frameCounts, false);
        this.sprite = new Sprite(runningHelper.getCurrentAnimation().getKeyFrame(0));
        this.sprite.setSize(width, height);
    }

    public void setPosition(int setX, int setY) {
        x = setX;
        y = setY;
    }

    public Sprite getSprite() {
        return sprite;
    }

    @Override
    public void update(float delta) {
        stateTime += delta; // Update the state time
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        checkUserInput();
        updateAnimation(delta);
    }

    public void createBody(World world) {
        this.body = BodyHelperService.createBody(
                x, y, width, height, false, world, ContactType.PLAYER, 1);
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
        runningHelper.updateAnimation(velocity, delta);
        sprite = runningHelper.getSprite();

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
