package objects.animals.bison;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameScreen;
import helper.movement.AnimalMovementHelper;
import objects.animals.helper.BisonManager;
import objects.player.GameEntity;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class Bison extends GameEntity {
    private Animation<TextureRegion> stationaryAnimation;
    private Animation<TextureRegion> walkingAnimation;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private boolean isFacingRight;
    private int id;
    private Sprite sprite;
    private TextureAtlas textureAtlas;
    private AssetManager assetManager;

    public Bison(float width, float height, float x, float y, Body body, boolean isFacingRight, GameScreen gameScreen, int bisonId) {
        super(0, 0, body, gameScreen);
        this.stateTime = 0f;
        this.id = bisonId;
        this.isFacingRight = isFacingRight;
        this.body = body;

        // Load animations
        loadAnimations();

        // Initialize the sprite with the first frame of the animation
        this.sprite = new Sprite(currentAnimation.getKeyFrame(0));
        this.sprite.setSize(width, height);

        BisonManager.addBison(this);
    }

    @Override
    public void update(float delta) {
        stateTime += delta; // Update the state time

        // Update sprite position
        float x = body.getPosition().x * PPM;
        float y = body.getPosition().y * PPM;
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);

        // Determine the current animation frame
        updateAnimation();

        // Check and update the sprite's direction
        boolean newFacingRight = AnimalMovementHelper.checkLinearVelocity(body, sprite, isFacingRight);
        if (newFacingRight != isFacingRight) {
            isFacingRight = newFacingRight;
        }
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

    public void playerContact(Body body, int bisonId, float linearVelocity) {
        body.setLinearDamping(1.5f);
        body.setLinearVelocity(linearVelocity, 0); // Adjust the linear velocity
    }

    private void loadAnimations() {
        stationaryAnimation = loadAnimation("animals/bison/grazing/Bison_Grazing_", 40);
        walkingAnimation = loadWalkingAnimation();
        currentAnimation = stationaryAnimation; // Default to stationary animation
    }

    private Animation<TextureRegion> loadAnimation(String basePath, int frameCount) {
        Array<TextureRegion> frames = new Array<>();
        for (int i = 0; i < frameCount; i++) {
            String filename = basePath + i + ".png";
            Texture texture = new Texture(filename);
            frames.add(new TextureRegion(texture));
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }
    private Animation<TextureRegion> loadGrazingAtlas() {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = new TextureAtlas("animals/bison/grazing/atlas/bison-grazing.atlas");
        for (int i = 1; i <= 40; i++) {
            TextureRegion region = atlas.findRegion("Bison_Grazing-" + i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region bison-horizontal-" + i + " not found!");
            }
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    private Animation<TextureRegion> loadWalkingAnimation() {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = new TextureAtlas("animals/bison/walking/atlases/horizontal.atlas");
        for (int i = 1; i <= 5; i++) {
            TextureRegion region = atlas.findRegion("bison-horizontal-" + i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region bison-horizontal-" + i + " not found!");
            }
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    private void updateAnimation() {
        boolean isMovingRight = body.getLinearVelocity().x > 0;
        float movementThreshold = 1;

        if (Math.abs(body.getLinearVelocity().x) < movementThreshold) {
            // If velocity is below the threshold, use stationary animation
            sprite.setRegion(stationaryAnimation.getKeyFrame(stateTime, true));
            if (!isFacingRight) {
                sprite.flip(true, false);
            }
        } else if (isMovingRight) {
            // If moving right, use walking animation facing right
            sprite.setRegion(walkingAnimation.getKeyFrame(stateTime, true));
        } else {
            // If moving left, use walking animation flipped horizontally
            TextureRegion flippedWalkingRegion = new TextureRegion(walkingAnimation.getKeyFrame(stateTime, true));
            flippedWalkingRegion.flip(true, false);
            sprite.setRegion(flippedWalkingRegion);
        }
    }
}
