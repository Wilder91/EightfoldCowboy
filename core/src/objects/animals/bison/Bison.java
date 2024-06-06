package objects.animals.bison;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.screens.GameScreen;
import helper.movement.AnimalMovementHelper;
import objects.GameAssets;
import objects.animals.object_helper.BisonManager;
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
    private GameAssets gameAssets;

    public Bison(float width, float height, float x, float y, Body body, boolean isFacingRight, GameScreen gameScreen, int bisonId, GameAssets gameAssets) {
        super(width, height, body, gameScreen, gameAssets);
        this.stateTime = 0f;
        this.id = bisonId;
        this.isFacingRight = isFacingRight;
        this.body = body;
        this.gameAssets = gameAssets;

        // Load animations
        loadAnimations();

        // Initialize the sprite with the first frame of the animation
        this.sprite = new Sprite(currentAnimation.getKeyFrame(0));
        this.sprite.setSize(175, 175);

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
        System.out.println(body.getLinearVelocity());
        updateAnimation();

        // Check and update the sprite's direction

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

    public void playerContact(Body body, int bisonId, Vector2 linearVelocity) {
        body.setLinearDamping(1.5f);

        body.setLinearVelocity(linearVelocity); // Adjust the linear velocity
    }

    private void loadAnimations() {
        stationaryAnimation = createGrazingAnimationFromAtlas();


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


    private Animation<TextureRegion> loadUpWalkingAnimation() {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas("animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas");
        for (int i = 1; i <= 7; i++) {
            TextureRegion region = atlas.findRegion("Bison_Up_Walk", i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region Bison_Up_Walk_" + i + " not found!");
            }
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    private Animation<TextureRegion> loadDownWalkingAnimation() {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas("animals/bison/walking/atlases/eightfold/bison-up-and-down.atlas");
        for (int i = 1; i <= 8; i++) {
            TextureRegion region = atlas.findRegion("Bison_Down_Walk", i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region Bison_Down_Walk_" + i + " not found!");
            }
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    private Animation<TextureRegion> loadDiagonalDownWalkingAnimation() {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas("animals/bison/walking/atlases/eightfold/bison-diagonal.atlas");
        for (int i = 1; i <= 5; i++) {
            TextureRegion region = atlas.findRegion("Bison_DiagDOWN_Walk", i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region Bison_Down_Walk_" + i + " not found!");
            }
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }


    private Animation<TextureRegion> loadDiagonalUpWalkingAnimation() {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas("animals/bison/walking/atlases/eightfold/bison-diagonal.atlas");
        for (int i = 1; i <= 5; i++) {
            TextureRegion region = atlas.findRegion("Bison_DiagUP_Walk", i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region Bison_Down_Walk_" + i + " not found!");
            }
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    private Animation<TextureRegion> loadHorizontalWalkingAnimation() {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas("animals/bison/walking/atlases/eightfold/bison-horizontal.atlas");
        for (int i = 1; i <= 5; i++) {
            TextureRegion region = atlas.findRegion("Bison_Horizontal_Walk", i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Region bison-horizontal-" + i + " not found!");
            }
        }
        return new Animation<>(FRAME_DURATION, frames, Animation.PlayMode.LOOP);
    }

    private void updateAnimation() {
        boolean isMoving = Math.abs(body.getLinearVelocity().x) > 0 || Math.abs(body.getLinearVelocity().y) > 0;
        boolean isMovingUp = body.getLinearVelocity().y > 0;
        boolean isMovingDown = body.getLinearVelocity().y < 0;
        boolean isMovingRight = body.getLinearVelocity().x > 0;
        boolean isMovingLeft = body.getLinearVelocity().x < 0;

        if (isMoving) {
            if (isMovingUp) {
                if (isMovingRight) {
                    currentAnimation = loadDiagonalUpWalkingAnimation();
                    isFacingRight= true;

                } else if (isMovingLeft) {
                    currentAnimation = loadDiagonalUpWalkingAnimation();
                    isFacingRight =false;
                } else {
                    currentAnimation = loadUpWalkingAnimation();
                }
            } else if (isMovingDown) {
                if (isMovingRight) {
                    currentAnimation = loadDiagonalDownWalkingAnimation();
                    isFacingRight = true;
                } else if (isMovingLeft) {
                    currentAnimation = loadDiagonalDownWalkingAnimation();
                    isFacingRight = false;
                    sprite.flip(true,false);
                } else {
                    currentAnimation = loadDownWalkingAnimation();
                }
            } else if (isMovingRight || isMovingLeft) {
                currentAnimation = loadHorizontalWalkingAnimation();
                isFacingRight = isMovingRight;
            }
        } else {
            currentAnimation = stationaryAnimation;
        }

        TextureRegion frame = currentAnimation.getKeyFrame(stateTime, true);



        sprite.setRegion(frame);
        sprite.setSize(width, height);
    }
}

