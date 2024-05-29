package objects.animals;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.physics.box2d.Body;
import com.waldergames.eightfoldtwo.GameScreen;
import helper.movement.AnimalMovementHelper;
import objects.animals.helper.BirdManager;
import objects.player.GameEntity;

import java.util.Random;

import static helper.Constants.PPM;

public class Bird extends GameEntity {

    private static final float MOVEMENT_DURATION = 2.0f; // 1 second
    private boolean isMoving; // Removed static keyword
    private float moveTimer; // Removed static keyword
    private boolean isActive;
    private Sprite sprite;
    private boolean isFacingRight;
    private int id;


    public Bird(float width, float height, float x, float y, Body body, boolean isFacingRight, GameScreen gameScreen, int birdId) {
        super(0, 0, body, gameScreen);
        float spriteX = x - width / 2;
        float spriteY = y - height / 2;
        this.speed = 15f;
        Texture birdTexture = new Texture("bird.png");
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
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

        AnimalMovementHelper.checkLinearVelocity(body, sprite, isFacingRight);

    }

    public void setBody(Body body) {
        this.body = body;
    }

    public Body getBody() {
        return body;
    }



    public Sprite getSprite() {
        return sprite;
    }

    private void checkBounds(){
        if(this.x <= 0){
            this.x = 1;
        } else if (this.x >= Gdx.graphics.getWidth()) {
            this.x = Gdx.graphics.getWidth();

        }
    }

    @Override
    public void render(SpriteBatch batch) {

        sprite.draw(batch);
    }




    public static void playerContact(Body body, int id){
        // Apply linear damping to gradually reduce velocity
        body.setLinearDamping(.8f);
        // Generate a random direction and set the initial velocity
        Random random = new Random();
        float angle = random.nextFloat() * MathUtils.PI2 * 40; // Random angle between 0 and 2π
        float speed = 15.0f; // Set a desired speed

        float velocityX = speed * MathUtils.cos(angle);
        float velocityY = speed * MathUtils.sin(angle);
        System.out.println(velocityX);
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
