package objects.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameScreen;
import objects.GameAssets;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class Player extends GameEntity {
    private GameAssets gameAssets;
    private Sprite sprite;
    private boolean isFacingRight;
    private Animation<TextureRegion> walkingAnimation;
    private Texture idleTexture;
    private TextureRegion idleRegion;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;

    public Player(float width, float height, Body body, GameScreen gameScreen) {
        super(width, height, body, gameScreen);
        this.speed = 20f;
        this.isFacingRight = false;
        this.body = body;
        gameAssets = new GameAssets();
        gameAssets.loadAssets();
        gameAssets.finishLoading();

        // Load idle texture
        idleTexture = new Texture("kath.gif");
        idleRegion = new TextureRegion(idleTexture);

        // Initialize animations
        walkingAnimation = createWalkingAnimationFromAtlas();
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

    private Animation<TextureRegion> createWalkingAnimationFromAtlas() {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas("kath_walk/atlas/kath-walk.atlas");

        // Use the region names and bounds specified in the atlas file
        String[] regionNames = {"kath_walk"};
        int[] regionIndices = {0, 1, 2, 3, 4, 5, 6, 7};
        for (int index : regionIndices) {
            TextureRegion region = atlas.findRegion(regionNames[0], index);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region " + regionNames[0] + " with index " + index + " not found!");
            }
        }

        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
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
    }

    private void updateAnimation() {
        if (Math.abs(velX) > 0) {
            currentAnimation = walkingAnimation;
            TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
            System.out.println("facing right: " + isFacingRight);

            if (isFacingRight && !frame.isFlipX()) {
                frame.flip(true, false);
            } else if (!isFacingRight && frame.isFlipX()) {
                frame.flip(true, false);
            }
            sprite.setRegion(frame);
        } else {
            currentAnimation = null;
            sprite.setRegion(idleRegion);
            if (isFacingRight) {
                sprite.flip(true, false);
            }
        }
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
