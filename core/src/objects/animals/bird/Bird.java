package objects.animals.bird;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.movement.SpriteMovementHelper;

import objects.animals.object_helper.BirdManager;
import com.mygdx.eightfold.player.GameEntity;

import java.util.Random;

import static helper.Constants.PPM;

public class Bird extends GameEntity  {

    private static final float MOVEMENT_DURATION = 2.0f; // 1 second
    private boolean isMoving; // Removed static keyword
    private float moveTimer; // Removed static keyword
    private boolean isActive;
    private Sprite sprite;
    private boolean isFacingRight;
    private int id;
    private AssetManager assetManager;
    private GameAssets gameAssets;
    private ScreenInterface screenInterface;

    public Bird(float width, float height, float x, float y, Body body, boolean isFacingRight, ScreenInterface screenInterface, int birdId, GameAssets gameAssets) {
        super(0, 0, body, screenInterface, gameAssets);
        float spriteX = x - width / 2;
        float spriteY = y - height / 2;
        this.speed = 15f;
       this.gameAssets = gameAssets;
        Texture birdTexture = gameAssets.getTexture("animals/birds/bird.png");
        this.sprite = new Sprite(birdTexture);
        this.sprite.setSize(width, height);
        this.sprite.setPosition(spriteX, spriteY);
        this.isActive = false;
        this.moveTimer = 0;
        this.isFacingRight = isFacingRight;
        this.body = body;
        BirdManager.addBird(this);
    }

    @Override
    public void update(float delta) {


        x = body.getPosition().x * PPM ;
        y = body.getPosition().y * PPM;
        boolean newFacingRight = SpriteMovementHelper.checkLinearVelocity(body, sprite, isFacingRight);
        if (newFacingRight != isFacingRight) {
            isFacingRight = newFacingRight;
        }





        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

        // Check and update the sprite's direction

//        if (newFacingRight != isFacingRight) {
//            isFacingRight = newFacingRight;
//        }

    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }

    @Override
    public void render(SpriteBatch batch) {

        sprite.draw(batch);
    }



    public static void playerContact(Body body, int id){
        // Apply linear damping to gradually reduce velocity
        body.setLinearDamping(3f);
        // Generate a random direction and set the initial velocity
        Random random = new Random();
        float angle = random.nextFloat() * MathUtils.PI2 * 40; // Random angle between 0 and 2π
        float speed = 15.0f; // Set a desired speed

        float velocityX = speed * MathUtils.cos(angle);
        float velocityY = speed * MathUtils.sin(angle);

        body.setLinearVelocity(velocityX, velocityY);

    }

    public static void startMoving(Body body) {

    }

    public void stopMoving() {
        isMoving = false;
        moveTimer = 0;
        body.setLinearVelocity(0, 0);
    }

    public boolean isMoving(){
        return isMoving;
    }


    public void toggleMoving() {
        isMoving = !isMoving;
    }

    public int getId() {
        return id;
    }
}
