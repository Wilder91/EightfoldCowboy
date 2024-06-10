package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.screens.GameScreen;
import objects.GameAssets;

import static helper.Constants.PPM;

public class Player extends GameEntity {
    private final float originalSpeed;
    private GameAssets gameAssets;
    private Sprite sprite;
    private boolean isFacingRight;
    private Animation<TextureRegion> walkingAnimation;
    private Texture idleTexture;
    private TextureRegion idleRegion;
    private Animation<TextureRegion> currentAnimation;
    private PlayerAnimations playerAnimations;
    private float stateTime;


    public Player(float width, float height, Body body, GameScreen gameScreen, GameAssets gameAssets) {
        super(width, height, body, gameScreen, gameAssets);
        this.speed = 7f;
        this.originalSpeed = 7f;
        this.isFacingRight = true;
        this.body = body;
        this.gameAssets = gameAssets;

        this.playerAnimations = new PlayerAnimations(gameAssets);
        // Load idle texture
        idleTexture = gameAssets.getTexture("Character_Horizontal_Run/Character_Horizontal_Run_3.png");
        idleRegion = new TextureRegion(idleTexture);

        // Initialize animations
        walkingAnimation = playerAnimations.createWalkingAnimationFromAtlas();
        currentAnimation = null;

        // Initialize sprite with the idle texture
        this.sprite = new Sprite(idleRegion);
        this.sprite.setSize(width, height);
    }

    @Override
    public void update(float delta) {
        stateTime += delta; // Update the state time

        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        checkUserInput();
        updateAnimation();
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
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT)){
            speed = speed * 2;
        }
        if (!Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)){
            speed = originalSpeed;
        }
    }

    private void updateAnimation() {
        if (Math.abs(velX) > 0) {
            currentAnimation = walkingAnimation;
            TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);

            if (isFacingRight && frame.isFlipX()) {
                frame.flip(true, false);
            } else if (!isFacingRight && !frame.isFlipX()) {
                frame.flip(true, false);
            }
            sprite.setRegion(frame);
        } else {
            currentAnimation = null;
            sprite.setRegion(idleRegion);
            if (!isFacingRight && !sprite.isFlipX()) {
                sprite.flip(true, false);
            } else if (isFacingRight && sprite.isFlipX()) {
                sprite.flip(true, false);
            }
        }
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
