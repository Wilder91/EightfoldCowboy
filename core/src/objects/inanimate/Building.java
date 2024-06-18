package objects.inanimate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.GameEntity;

import java.util.Random;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class Building extends GameEntity {

    private static final float MOVEMENT_DURATION = 2.0f; // 1 second
    // Duration of each frame in the animation
    private boolean isMoving;
    private float moveTimer;
    private Animation<TextureRegion> animation;
    private float stateTime; // Time elapsed since the animation started
    private int id;

    public Building(float width, float height, float x, float y, Body body, boolean isFacingRight, GameScreen gameScreen, int buildingId, GameAssets gameAssets) {
        super(0, 0, body, gameScreen, gameAssets);

        this.speed = 15f;
        this.moveTimer = 0;
        this.body = body;
        this.stateTime = 0f;
        this.id = buildingId;

        // Load the animation frames
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < 40; i++) { // Assuming there are 40 frames
            Texture frameTexture = new Texture(Gdx.files.internal("Saloon/Saloon" + i + ".png"));
            TextureRegion frame = new TextureRegion(frameTexture);
            frames.add(frame);
        }
        this.animation = new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
        // Randomly select a starting frame
        Random random = new Random();
        int randomFrame = random.nextInt(40); // Random number between 0 and 39
        this.stateTime = randomFrame * FRAME_DURATION;
    }

    @Override
    public void update(float delta) {
        stateTime += delta; // Update the state time

        if (isMoving) {
            startMoving(body);
            moveTimer += delta;
            if (moveTimer >= MOVEMENT_DURATION) {
                stopMoving();
            }
        }
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
    }

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
        float spriteX = x - currentFrame.getRegionWidth() ;
        float spriteY = y - currentFrame.getRegionHeight() ;

        // Draw the current frame
        batch.draw(currentFrame, spriteX, spriteY, currentFrame.getRegionWidth() * 2, currentFrame.getRegionHeight() * 2);
    }





    public static void startMoving(Body body) {
        // Apply linear damping to gradually reduce velocity
        body.setLinearDamping(1.5f);
    }

    public void stopMoving() {
        isMoving = false;
        moveTimer = 0;
        body.setLinearVelocity(0, 0);
    }

    public boolean isMoving() {
        return isMoving;
    }

    public void toggleMoving() {
        isMoving = !isMoving;
    }

    public int getId() {
        return id;
    }
}
