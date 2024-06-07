package objects.animals.bison;


import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.screens.GameScreen;
import objects.GameAssets;
import objects.animals.object_helper.BisonManager;
import objects.player.GameEntity;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class Bison extends GameEntity {
    private Animation<TextureRegion> stationaryAnimation;
    private Animation<TextureRegion> walkingUpAnimation;
    private Animation<TextureRegion> walkingDownAnimation;
    private Animation<TextureRegion> walkingDiagonalUpAnimation;
    private Animation<TextureRegion> walkingDiagonalDownAnimation;
    private Animation<TextureRegion> walkingHorizontalAnimation;
    private Animation<TextureRegion> currentAnimation;
    private float stateTime;
    private boolean isFacingRight;
    private int id;
    private Sprite sprite;
    private GameAssets gameAssets;
    private float movementThreshold;

    public Bison(float width, float height, float x, float y, Body body, boolean isFacingRight, GameScreen gameScreen, int bisonId, GameAssets gameAssets) {
        super(width, height, body, gameScreen, gameAssets);
        this.stateTime = 0f;
        this.id = bisonId;
        this.isFacingRight = isFacingRight;
        this.body = body;
        this.gameAssets = gameAssets;
        this.movementThreshold = 1f;

        // Load animations
        loadAnimations();

        // Initialize the sprite with the first frame of the animation
        this.sprite = new Sprite(currentAnimation.getKeyFrame(0));
        BisonManager.addBison(this);
    }

    @Override
    public void update(float delta) {
        stateTime += delta; // Update the state time

        // Update sprite position
        float x = body.getPosition().x * PPM;
        float y = body.getPosition().y * PPM;
        sprite.setPosition(x - sprite.getWidth() / 2, y - sprite.getHeight() / 2);
        if (Math.abs(body.getLinearVelocity().x) < 1 && Math.abs(body.getLinearVelocity().y) < 1) {
            body.setLinearVelocity(0, 0);
        }
        // Determine the current animation frame and update sprite size if needed
        updateAnimation();
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

    private void loadAnimations() {
        stationaryAnimation = createGrazingAnimationFromAtlas();
        walkingUpAnimation = loadUpWalkingAnimation();
        walkingDownAnimation = loadDownWalkingAnimation();
        walkingDiagonalUpAnimation = loadDiagonalUpWalkingAnimation();
        walkingDiagonalDownAnimation = loadDiagonalDownWalkingAnimation();
        walkingHorizontalAnimation = loadHorizontalWalkingAnimation();
        currentAnimation = stationaryAnimation; // Default to stationary animation
    }

    private Animation<TextureRegion> createGrazingAnimationFromAtlas() {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas grazingAtlas = gameAssets.getAtlas("animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas");
        for (int i = 1; i <= 5; i++) {
            TextureRegion region = grazingAtlas.findRegion("Bison_Down_Rest", i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region Bison_Grazing_" + i + " not found!");
            }
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    private Animation<TextureRegion> loadUpWalkingAnimation() {
        return loadAnimation("Bison_Up_Walk", 7, "animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas");
    }

    private Animation<TextureRegion> loadDownWalkingAnimation() {
        return loadAnimation("Bison_Down_Walk", 8, "animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas");
    }

    private Animation<TextureRegion> loadDiagonalUpWalkingAnimation() {

        return loadAnimation("Bison_DiagUP_Walk", 5, "animals/bison/walking/atlases/eightfold/bison-diagonal.atlas");
    }

    private Animation<TextureRegion> loadDiagonalDownWalkingAnimation() {
        return loadAnimation("Bison_DiagDOWN_Walk", 5, "animals/bison/walking/atlases/eightfold/bison-diagonal.atlas");
    }

    private Animation<TextureRegion> loadHorizontalWalkingAnimation() {
        return loadAnimation("Bison_Horizontal_Walk", 5, "animals/bison/walking/atlases/eightfold/bison-horizontal.atlas");
    }

    private Animation<TextureRegion> loadAnimation(String regionNamePrefix, int frameCount, String atlasPath) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);
        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(regionNamePrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region " + regionNamePrefix + "_" + i + " not found!");
            }
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    private void updateAnimation() {
        boolean isMoving = Math.abs(body.getLinearVelocity().x) > movementThreshold || Math.abs(body.getLinearVelocity().y) > movementThreshold;
        boolean isMovingUp = body.getLinearVelocity().y > movementThreshold;
        boolean isMovingDown = body.getLinearVelocity().y < -movementThreshold;
        boolean isMovingRight = body.getLinearVelocity().x > movementThreshold;
        boolean isMovingLeft = body.getLinearVelocity().x < -movementThreshold;

        if (isMoving) {
            if (isMovingUp) {
                if (isMovingRight) {
                    currentAnimation = walkingDiagonalUpAnimation;

                    isFacingRight = false; //png is inverted so i'm rolling with it for now
                    System.out.println("up and to the right!");
                } else if (isMovingLeft) {
                    currentAnimation = walkingDiagonalUpAnimation;

                    isFacingRight = true; //png is inverted so i'm rolling with it for now
                } else {
                    currentAnimation = walkingUpAnimation;
                }
            } else if (isMovingDown) {
                if (isMovingRight) {
                    currentAnimation = walkingDiagonalDownAnimation;
                    isFacingRight = true;
                } else if (isMovingLeft) {
                    currentAnimation = walkingDiagonalDownAnimation;
                    isFacingRight = false;
                } else {
                    currentAnimation = walkingDownAnimation;
                }
            } else if (isMovingRight) {
                currentAnimation = walkingHorizontalAnimation;
                isFacingRight = true;
            } else if (isMovingLeft) {
                currentAnimation = walkingHorizontalAnimation;
                isFacingRight = false;
            }
        } else {
            currentAnimation = stationaryAnimation;
        }

        TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);
        sprite.setRegion(frame);

        // Adjust sprite size and flip based on direction
        sprite.setSize(frame.getRegionWidth(), frame.getRegionHeight());
        if ((isFacingRight && sprite.isFlipX()) || (!isFacingRight && !sprite.isFlipX())) {
            sprite.flip(true, false);
        }
    }
    public void playerContact(Body body, int bisonId, Vector2 linearVelocity) {
        body.setLinearDamping(1.5f);

        body.setLinearVelocity(linearVelocity); // Adjust the linear velocity
    }


}
