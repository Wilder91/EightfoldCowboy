package objects.animals.bison;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.waldergames.eightfoldtwo.GameScreen;
import helper.movement.AnimalMovementHelper;
import objects.animals.helper.BisonManager;
import objects.player.GameEntity;

import java.util.Random;

import static helper.Constants.PPM;

public class Bison extends GameEntity {

    private static final float DIRECTION_CHANGE_INTERVAL = 10.0f; // Change direction every 10 seconds
    private static final float MOVEMENT_SPEED = 50f;

    private boolean isMoving;
    private float moveTimer;

    private static final float MOVEMENT_DURATION = 2.0f; // 1 second
    private static final float FRAME_DURATION = 0.1f; // Duration of each frame in the animation
    private boolean isActive;
    private Animation<TextureRegion> animation;
    private float stateTime; // Time elapsed since the animation started
    private boolean isFacingRight;
    private int id;
    private Sprite sprite;

    public Bison(float width, float height, float x, float y, Body body, boolean isFacingRight, GameScreen gameScreen, int bisonId) {
        super(0, 0, body, gameScreen);

        this.speed = 15f;
        this.isActive = false;
        this.moveTimer = 0;
        this.isFacingRight = isFacingRight;
        this.body = body;
        this.stateTime = 0f;
        this.id = bisonId;

        // Load the animation frames
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < 40; i++) { // Assuming there are 40 frames
            Texture frameTexture = new Texture(Gdx.files.internal("animals/bison/grazing/Bison_Grazing_" + i + ".png"));
            TextureRegion frame = new TextureRegion(frameTexture);
            frames.add(frame);
        }
        this.animation = new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);

        // Initialize the sprite with the first frame of the animation
        this.sprite = new Sprite(animation.getKeyFrame(0));

        Random random = new Random();
        int randomFrame = random.nextInt(40); // Random number between 0 and 39
        this.stateTime = randomFrame * FRAME_DURATION;
        BisonManager.addBison(this);
        this.body = body;
    }

    @Override
    public void update(float delta) {
        stateTime += delta; // Update the state time

        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;

        sprite.setRegion(animation.getKeyFrame(stateTime, true));
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        AnimalMovementHelper.checkLinearVelocity(body, sprite, isFacingRight);
    }

    private void checkLinearVelocity() {

    }

    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public static void playerContact(Body body, int bisonId) {
        body.setLinearDamping(1.5f);
        Bison bison = BisonManager.getBisonById(bisonId);
        Sprite sprite = bison.getSprite();

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

    public void setBody(Body body) {
        this.body = body;
    }
}
