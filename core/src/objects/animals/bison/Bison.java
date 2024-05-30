package objects.animals.bison;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameScreen;
import helper.movement.AnimalMovementHelper;
import objects.animals.helper.BisonManager;
import objects.player.GameEntity;

import static helper.Constants.PPM;

public class Bison extends GameEntity {
    // 1 second
    private static final float FRAME_DURATION = 0.1f; // Duration of each frame in the animation
    private Animation<TextureRegion> stationaryAnimation;
    private Animation<TextureRegion> walkingAnimation;
    private Animation<TextureRegion> animation;
    private float stateTime; // Time elapsed since the animation started
    private boolean isFacingRight;
    private int id;
    private Sprite sprite;
    private TextureAtlas textureAtlas;

    public Bison(float width, float height, float x, float y, Body body, boolean isFacingRight, GameScreen gameScreen, int bisonId) {
        super(0, 0, body, gameScreen);

        this.stateTime = 0f;
        this.id = bisonId;
        this.isFacingRight = isFacingRight;
        this.body = body;
        System.out.println("BISON CLASS BODY: " + body);
        loadAnimationFrames();

        // Create animations

        // Load the animation frames from the texture atlas


        Array<TextureRegion> frames = new Array<>();
        initAnimations();
        if (Math.abs(body.getLinearVelocity().x) < 0.01f) {
    System.out.println("LINEAR Velocity: " + body.getLinearVelocity());
    System.out.println("BISON BODY: " + body);

    // Load frames for walking animation from atlas
    for (int i = 0; i <= 39; i++) {
        String filename = "Bison_Grazing_" + i + ".png";
        Texture texture = new Texture("animals/bison/grazing/" + filename);
        frames.add(new TextureRegion(texture));
    }
    System.out.println("new linear: " + body.getLinearVelocity());
}
//       if(body.getLinearVelocity().x > 0){
//            System.out.println(body.getLinearVelocity().x);
//            TextureAtlas atlas = new TextureAtlas("animals/bison/walking/atlases/horizontal.atlas");
//            for (int i = 1; i <= 5; i++) {
//                TextureRegion region = atlas.findRegion("bison-horizontal-" + i);
//                if (region == null) {
//                    System.out.println("Region bison-horizontal-" + i + " not found!");
//                } else {
//                    frames.add(region);
//                }
//            }
//
//        }
//
//        if (frames.size == 0) {
//            throw new RuntimeException("No frames found in the atlas. Check the atlas file and region names.");
//        }

        // Create the animation
        this.animation = new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);

        // Initialize the sprite with the first frame of the animation
        this.sprite = new Sprite(animation.getKeyFrame(0));

        // Set the size of the sprite
        this.sprite.setSize(width, height);

        BisonManager.addBison(this);
    }

    @Override
    public void update(float delta) {
        stateTime += delta; // Update the state time

        // Ensure the sprite's position remains constant
        float x = body.getPosition().x * PPM;
        float y = body.getPosition().y * PPM;

        // Determine the direction of movement
        boolean isMovingRight = body.getLinearVelocity().x > 0;

        // Update animation based on the direction of movement
        float movementThreshold = 1;
        if (Math.abs(body.getLinearVelocity().x) < movementThreshold) {
            // If velocity is below the threshold, use stationary animation
            sprite.setRegion(stationaryAnimation.getKeyFrame(stateTime, true));
        } else if (isMovingRight) {
            // If moving right, use walking animation facing right
            sprite.setRegion(walkingAnimation.getKeyFrame(stateTime, true));
        } else {
            // If moving left, flip the walking animation for left-facing movement
            TextureRegion flippedRegion = new TextureRegion(walkingAnimation.getKeyFrame(stateTime, true));
            flippedRegion.flip(true, false); // Flip the region horizontally
            sprite.setRegion(flippedRegion);
        }

        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

        // Check and update the sprite's direction
        boolean newFacingRight = AnimalMovementHelper.checkLinearVelocity(body, sprite, isFacingRight);
        if (newFacingRight != isFacingRight) {
            isFacingRight = newFacingRight;
        }





        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

        // Check and update the sprite's direction

        if (newFacingRight != isFacingRight) {
            isFacingRight = newFacingRight;
        }
    }

    private void loadAnimationFrames() {
        // Load frames for stationary animation
        Array<TextureRegion> stationaryFrames = new Array<>();
        for (int i = 0; i <= 39; i++) {
            String filename = "Bison_Grazing_" + i + ".png";
            Texture texture = new Texture("animals/bison/grazing/" + filename);
            stationaryFrames.add(new TextureRegion(texture));
        }

        // Load frames for walking animation
        Array<TextureRegion> walkingFrames = new Array<>();
        TextureAtlas walkingAtlas = new TextureAtlas("animals/bison/walking/atlases/horizontal.atlas");
        for (int i = 1; i <= 5; i++) {
            TextureRegion region = walkingAtlas.findRegion("bison-horizontal-" + i);
            if (region == null) {
                System.out.println("Region bison-horizontal-" + i + " not found!");
            } else {
                walkingFrames.add(region);
            }
        }
    }

    private void createAnimations() {
        // Create animations using the loaded frames

    }

    private void initAnimations() {
        // Load frames for the stationary animation
        Array<TextureRegion> stationaryFrames = new Array<>();
        for (int i = 0; i <= 39; i++) {
            String filename = "Bison_Grazing_" + i + ".png";
            Texture texture = new Texture("animals/bison/grazing/" + filename);
            stationaryFrames.add(new TextureRegion(texture));
        }
        stationaryAnimation = new Animation<>(FRAME_DURATION, stationaryFrames, Animation.PlayMode.LOOP);

        // Load frames for the walking animation
        Array<TextureRegion> walkingFrames = new Array<>();
        TextureAtlas walkingAtlas = new TextureAtlas("animals/bison/walking/atlases/horizontal.atlas");
        for (int i = 1; i <= 5; i++) {
            TextureRegion region = walkingAtlas.findRegion("bison-horizontal-" + i);
            if (region == null) {
                System.out.println("Region bison-horizontal-" + i + " not found!");
            } else {
                walkingFrames.add(region);
            }
        }
        walkingAnimation = new Animation<>(FRAME_DURATION, walkingFrames, Animation.PlayMode.LOOP);
    }



    @Override
    public void render(SpriteBatch batch) {
        sprite.draw(batch);
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public void playerContact(Body body, int bisonId, float linearVelocity) {
        body.setLinearDamping(1.5f);
        System.out.println(linearVelocity);
        body.setLinearVelocity(linearVelocity, 0); // Adjust the linear velocity

        System.out.println("playercontact bison body: " + body.getLinearVelocity());
        // Ensure this does not conflict with direction logic
    }


    public int getId() {
        return id;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
