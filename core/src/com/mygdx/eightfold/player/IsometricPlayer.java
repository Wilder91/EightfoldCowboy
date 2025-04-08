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
import helper.movement.*;

import objects.inanimate.Door;

import static helper.Constants.PPM;

public class IsometricPlayer extends GameEntity {
    private final float originalSpeed;
    private GameAssets gameAssets;
    private Sprite sprite;
    private boolean isFacingRight;
    private float stateTime;
    private ScreenInterface screenInterface;
    private Vector2 initialPosition;
    private SpriteRunningHelper runningHelper;
    private SpriteIdleHelper idleHelper;
    private String lastDirection = "idleDown";
    private boolean justSwitchedHelpers;
    private PointLight playerLight;
    private RayHandler rayHandler;

    // Isometric tile dimensions for coordinate conversion
    private float tileWidth;
    private float tileHeight;

    public IsometricPlayer(float x, float y, float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets) {
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
        this.rayHandler = new RayHandler(screenInterface.getWorld());

        // Set isometric tile dimensions - these should match your map tile dimensions
        this.tileWidth = 64;  // Adjust based on your tile width
        this.tileHeight = 32; // Adjust based on your tile height

        int[] runningFrameCounts = {8, 8, 8, 8, 8}; // Ensure frame counts are non-zero
        int[] idleFrameCounts = {18, 1, 8, 18, 4};
        this.runningHelper = new SpriteRunningHelper(gameAssets, "Character", "Character", runningFrameCounts, false);
        this.idleHelper = new SpriteIdleHelper(gameAssets, "Character", "Character", idleFrameCounts, 0f);
        this.sprite = new Sprite();
        this.sprite.setSize(width, height);

        playerLight = new PointLight(rayHandler, 128, new Color(.5f, .4f, .5f, .8f), .4f, 0, 0);
        playerLight.setSoftnessLength(1f);
        playerLight.setContactFilter(ContactType.LIGHT.getCategoryBits(),
                ContactType.LIGHT.getMaskBits(),
                (short) 0);

        System.out.println("playerLight x: " + x);
        System.out.println("playerLight y: " + y);
        playerLight.setPosition(x/PPM + .1f, y/PPM);
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
                door.getBody().getPosition().x, door.getBody().getPosition().y,
                width, height, false, world, ContactType.PLAYER, 1);
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
        // For isometric, we might need to adjust the sprite position
        // The sprite is positioned relative to the physics body but with visual offset for isometric view
        sprite.setPosition(x - width / 2, y - height / 2);
        sprite.draw(batch);
    }

    /**
     * Converts screen direction to isometric direction
     * @param screenX X component in screen space
     * @param screenY Y component in screen space
     * @return Vector2 containing isometric direction
     */
    private Vector2 screenToIsometricDirection(float screenX, float screenY) {
        // Convert screen space direction to isometric direction
        float isoX = screenX - screenY;
        float isoY = (screenX + screenY) / 2;

        // Normalize if needed
        Vector2 isoDirection = new Vector2(isoX, isoY);
        if (isoDirection.len() > 0) {
            isoDirection.nor();
        }

        return isoDirection;
    }

    private void checkUserInput() {
        float velX = 0;
        float velY = 0;

        // Capture directional input
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

        // Create direction vector and normalize
        Vector2 direction = new Vector2(velX, velY);
        if (direction.len() > 0) {
            direction.nor();
        }

        // Optional sprint logic
        float currentSpeed = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? speed * 1.5f : speed;

        // Convert screen direction to isometric direction
        Vector2 isoDirection = screenToIsometricDirection(direction.x, direction.y);

        // Apply movement with isometric direction
        body.setLinearVelocity(isoDirection.x * currentSpeed, isoDirection.y * currentSpeed);
    }

    private void updateAnimation(float delta) {
        Vector2 velocity = body.getLinearVelocity();
        Vector2 absVelocity = new Vector2(Math.abs(velocity.x), Math.abs(velocity.y));

        // Determine direction for animation
        idleHelper.getFacingDirection(velocity, absVelocity);

        // For isometric view, we need to interpret direction differently
        // This is a simplified approach - you may need to adjust based on your specific needs
        if (velocity.y > 0.1f) {
            if (Math.abs(velocity.x) > 0.1f) {
                if (velocity.x > 0) {
                    lastDirection = "idleDiagonalUp"; // Northeast
                } else {
                    lastDirection = "idleDiagonalUp"; // Northwest
                }
            } else {
                lastDirection = "idleUp"; // North
            }
        } else if (velocity.y < -0.1f) {
            if (Math.abs(velocity.x) > 0.1f) {
                if (velocity.x > 0) {
                    lastDirection = "idleDiagonalDown"; // Southeast
                } else {
                    lastDirection = "idleDiagonalDown"; // Southwest
                }
            } else {
                lastDirection = "idleDown"; // South
            }
        } else if (Math.abs(velocity.x) > 0.1f) {
            if (velocity.x > 0) {
                lastDirection = "idleSide"; // East
                isFacingRight = true;
            } else {
                lastDirection = "idleSide"; // West
                isFacingRight = false;
            }
        }

        // Update animation based on movement
        if (absVelocity.x > .01 || absVelocity.y > .01) {
            runningHelper.updateAnimation(velocity, delta);
            sprite = runningHelper.getSprite();
        } else {
            idleHelper.setDirection(lastDirection);
            idleHelper.setFacingRight(isFacingRight);
            idleHelper.update(delta);
            sprite = idleHelper.getSprite();
        }

        // Flip the sprite if needed
        if (velocity.x < 0 && velocity.y > 0) {
            // Northwest
            sprite.flip(true, false);
            isFacingRight = false;
        } else if (velocity.x > 0 && velocity.y > 0) {
            // Northeast
            sprite.flip(false, false);
            isFacingRight = true;
        } else if (velocity.x < 0 && velocity.y < 0) {
            // Southwest
            sprite.flip(true, false);
            isFacingRight = false;
        } else if (velocity.x > 0 && velocity.y < 0) {
            // Southeast
            sprite.flip(false, false);
            isFacingRight = true;
        } else if (velocity.x < 0) {
            // West
            sprite.flip(true, false);
            isFacingRight = false;
        } else if (velocity.x > 0) {
            // East
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

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}