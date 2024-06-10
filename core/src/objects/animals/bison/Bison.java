package objects.animals.bison;

import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.screens.GameScreen;
import helper.movement.SpriteMovementHelper;
import objects.GameAssets;
import objects.animals.object_helper.BisonManager;
import objects.player.GameEntity;
import helper.movement.Facing;

import java.util.Map;
import java.util.Random;

import static helper.Constants.PPM;

public class Bison extends GameEntity {
    private Map<String, Animation<TextureRegion>> animations;
    private Animation<TextureRegion> currentAnimation;
    private SpriteMovementHelper movementHelper;
    private float stateTime;
    private float randomStateTime;
    private Facing facingDirection;
    private int id;
    private Sprite sprite;
    private GameAssets gameAssets;
    private float movementThreshold;
    private boolean isResting;
    private Random random;
    private Array frameCounts;
    private boolean isPaused;
    private float restingTime;
    private float pauseDuration;

    public Bison(float width, float height, float x, float y, Body body, Facing initialDirection, GameScreen gameScreen, int bisonId, GameAssets gameAssets) {
        super(width, height, body, gameScreen, gameAssets);
        this.random = new Random();
        this.stateTime = 0f;
        this.randomStateTime = random.nextFloat();
        this.id = bisonId;
        this.facingDirection = initialDirection;
        this.body = body;
        this.gameAssets = gameAssets;
        this.movementThreshold = 1f;
        this.restingTime = 0f;
        this.pauseDuration = 3f; // 3 seconds pause duration
        this.isPaused = true;
        int[] frameCounts = {5, 7, 8, 5, 5, 5, 7, 7, 7, 7, 5, 5, 5, 5, 5};
        this.movementHelper = new SpriteMovementHelper(gameAssets, "bison", frameCounts);
        movementHelper.loadAnimations();



        // Load animations


        // Initialize the sprite with the first frame of the animation
        this.sprite = new Sprite(movementHelper.getCurrentAnimation().getKeyFrame(0));
        BisonManager.addBison(this);
    }

    @Override
    public void update(float delta) {
        stateTime += delta; // Update the state time
        randomStateTime += delta / 2;

        // Update sprite position
        float x = body.getPosition().x * PPM;
        float y = body.getPosition().y * PPM;
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

        // Set the origin of the sprite to its center
        sprite.setOriginCenter();

        // Use the helper to update the animation based on body velocity
        movementHelper.updateAnimation(body.getLinearVelocity(), delta);

        // The sprite's flip state should be managed within the helper
        sprite = movementHelper.getSprite();
    }




    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public int getId() {
        return id;
    }

    public void setBody(Body body) {
        this.body = body;
    }






    public void playerContact(Body body, int bisonId, Vector2 linearVelocity) {
        body.setLinearDamping(1.5f);
        body.setLinearVelocity(linearVelocity); // Adjust the linear velocity
    }








}