package objects.enemies;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.EntityRenderer;
import helper.EntityMovement;
import helper.combat.MeleeCombatHelper;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteWalkingHelper;
import helper.movement.SpriteIdleHelper;
import helper.movement.SpriteWalkingHelper;
import objects.GameEntity;

import static helper.Constants.FRAME_DURATION;
import static helper.Constants.PPM;

public class ThicketSaint extends GameEntity {
    private Sprite sprite;
    private SimpleIdleHelper idleHelper;
    private SimpleSpriteWalkingHelper walkingHelper;
    private MeleeCombatHelper meleeCombatHelper;
    private String lastDirection = "idleDown";
    private boolean isFacingRight = true;
    private int id;
    private float stateTime = 0;
    private ScreenInterface screenInterface;
    private EntityRenderer renderer;
    private String enemyType;
    private String enemyName;
    private EntityMovement movement;

    public ThicketSaint(float width, float height, Body body, ScreenInterface screenInterface,
                        GameAssets gameAssets, String enemyType, String enemyName) {
        super(width, height, body, screenInterface, gameAssets);

        this.sprite = new Sprite();
        this.sprite.setSize(width, height);
        this.enemyName = "saint_small"; // Hard-code this to match the atlas
        this.enemyType = enemyType;
        this.screenInterface = screenInterface;

        // Initialize the helpers with correct parameters
        int[] idleFrameCounts = {4};
        int[] walkingFrameCounts = {8, 8, 8, 0, 0};

        // Create a custom subclass of SpriteWalkingHelper to override the problematic method
        this.walkingHelper = new SimpleSpriteWalkingHelper(gameAssets, "enemies", this.enemyName, walkingFrameCounts, true, .2f) {
            @Override
            public void setRestingFrame(String texturePath) {
                // Do nothing - avoid loading the hardcoded texture
            }
        };

        this.idleHelper = new SimpleIdleHelper(gameAssets, "enemies-movement", this.enemyName, 4, 1.5f);
        this.meleeCombatHelper = new MeleeCombatHelper(gameAssets, enemyType, enemyName, "sword", idleFrameCounts, 5, screenInterface.getWorld());
        this.movement = new EntityMovement(this);

        // Initialize renderer directly without animator
        this.renderer = new EntityRenderer(this);

        // Try to get initial sprite from idle helper
        Sprite idleSprite = idleHelper.getSprite();
        if (idleSprite != null) {
            this.sprite = idleSprite;
        }

        this.renderer.setMainSprite(sprite);
    }

    @Override
    public void update(float delta) {
        stateTime += delta;
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        resetDepthToY();

        // Update movement
        movement.update(delta);

        // Get current velocity
        float vx = body.getLinearVelocity().x;
        float vy = body.getLinearVelocity().y;
        boolean isMoving = Math.abs(vx) > 0.1f || Math.abs(vy) > 0.1f;

        try {
            // Update facing direction and lastDirection based on movement
            if (Math.abs(vy) > Math.abs(vx)) {
                // Vertical movement is dominant
                if (vy > 0.1f) {
                    lastDirection = "idleUp";
                } else if (vy < -0.1f) {
                    lastDirection = "idleDown";
                }
            } else {
                // Horizontal movement is dominant
                if (vx > 0.1f) {
                    lastDirection = "idleSide";
                    isFacingRight = true;
                } else if (vx < -0.1f) {
                    lastDirection = "idleSide";
                    isFacingRight = false;
                }
            }

            // Choose animation based on movement
            if (isMoving) {
                walkingHelper.setFacingRight(isFacingRight);
                walkingHelper.updateAnimation(body.getLinearVelocity(), delta);
                Sprite walkSprite = walkingHelper.getSprite();
                if (walkSprite != null) {
                    sprite = walkSprite;
                }
            } else {
                //idleHelper.setDirection(lastDirection);
                idleHelper.setFacingRight(isFacingRight);
                idleHelper.update(delta);
                Sprite idleSprite = idleHelper.getSprite();
                if (idleSprite != null) {
                    sprite = idleSprite;
                }
            }
        } catch (Exception e) {
            System.err.println("Animation error: " + e.getMessage());
        }

        // Update the renderer
        renderer.setMainSprite(sprite);
        renderer.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        renderer.render(batch);
    }

    public int getId() {
        return id;
    }

    public String getEnemyType() {
        return enemyType;
    }
}