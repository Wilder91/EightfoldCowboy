package helper.combat;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameAssets;
import helper.ContactType;

import java.util.HashMap;
import java.util.Map;

import static helper.Constants.*;

public class MeleeCombatHelper {
    private Map<String, Animation<TextureRegion>> attackAnimations;
    private Animation<TextureRegion> currentAttackAnimation;
    private float attackStateTime;
    private Sprite attackSprite;
    private GameAssets gameAssets;
    private String animalType;
    private String animalName;
    private String weaponType;
    private boolean isFacingRight;
    private boolean isAttacking;
    private float attackCooldown;
    private float attackDamage;
    private Rectangle hitbox;
    private int[] attackFrameCounts;
    private float attackDuration;
    private float currentAttackTimer;
    private Fixture attackSensor;
    private World world;
    private String lastDirection = "";

    public MeleeCombatHelper(GameAssets gameAssets, String animalType, String animalName, String weaponType, int[] attackFrameCounts, float attackDamage, World world) {
        this.gameAssets = gameAssets;
        this.animalType = animalType;
        this.animalName = animalName;
        this.attackStateTime = 0f;
        this.attackAnimations = new HashMap<>();
        this.attackFrameCounts = attackFrameCounts;
        this.isAttacking = false;
        this.attackCooldown = 0.5f; // Half second between attacks
        this.attackDamage = attackDamage;
        this.currentAttackTimer = 0f;
        this.hitbox = new Rectangle();
        this.world = world;
        this.weaponType = weaponType;
        loadAttackAnimations();

        this.currentAttackAnimation = attackAnimations.get("attackHorizontal");
        this.attackSprite = new Sprite();
        this.attackSprite.setOriginCenter();

        // Calculate attack duration based on animation frames * frame duration
        this.attackDuration = attackFrameCounts[2] * FRAME_DURATION;
    }

    public void loadAttackAnimations() {
        String atlasPath = "atlases/eightfold/" + animalType + "-movement.atlas";

        // Populate the animations map with all available attack animations
        attackAnimations.put("attackUp", createAnimation(animalName + "_up_" + weaponType,  attackFrameCounts[0], atlasPath));
        attackAnimations.put("attackDown", createAnimation(animalName + "_down_" + weaponType, attackFrameCounts[1], atlasPath));
        attackAnimations.put("attackHorizontal", createAnimation(animalName + "_horizontal_" + weaponType, attackFrameCounts[2], atlasPath));
        attackAnimations.put("attackDiagonalUp", createAnimation(animalName + "_diagUP_" + weaponType, attackFrameCounts[3], atlasPath));
        attackAnimations.put("attackDiagonalDown", createAnimation(animalName + "_diagDOWN_" + weaponType, attackFrameCounts[4], atlasPath));
    }

    private Animation<TextureRegion> createAnimation(String regionNamePrefix, int frameCount, String atlasPath) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

        if (atlas == null) {
            System.err.println("ERROR: Attack atlas not found: " + atlasPath);
            return new Animation<>(ATTACK_FRAME_DURATION, frames);
        }

        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(regionNamePrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Attack region " + regionNamePrefix + "_" + i + " not found!");
            }
        }

        if (frames.size == 0) {
            System.err.println("WARNING: No frames found for animation: " + regionNamePrefix);
        }

        return new Animation<>(ATTACK_FRAME_DURATION, frames, Animation.PlayMode.NORMAL);
    }

    public void update(float delta, Vector2 position, Vector2 facingDirection, boolean isFacingRight, String lastDirection) {
        this.isFacingRight = isFacingRight;
        this.lastDirection = lastDirection;
        // Update attack cooldown
        if (attackCooldown > 0) {
            attackCooldown -= delta;
        }

        // Update attack state
        if (isAttacking) {
            attackStateTime += delta;
            currentAttackTimer += delta;

            // Update attack sprite
            TextureRegion frame = currentAttackAnimation.getKeyFrame(attackStateTime, false);
            attackSprite.setRegion(frame);
            attackSprite.setSize(frame.getRegionWidth(), frame.getRegionHeight());
            attackSprite.setOriginCenter();

            // Position the attack sprite based on the character's position and facing direction
            positionAttackSprite(position, facingDirection);

            // Update hitbox position
            updateHitbox();

            // Check if attack animation is complete
            if (currentAttackTimer >= attackDuration || currentAttackAnimation.isAnimationFinished(attackStateTime)) {
                endAttack();
                removeAttackSensor();
            }
        }
    }

    private void createAttackSensor(Vector2 position, Vector2 direction) {
        // Remove any existing sensor
        removeAttackSensor();

        // Create a temporary body for the attack
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x / PPM, position.y / PPM);
        bodyDef.bullet = true; // Use CCD for fast-moving objects

        Body sensorBody = world.createBody(bodyDef);

        // Create a sensor shape based on attack direction
        PolygonShape shape = new PolygonShape();
        float width = 0.3f; // Adjust based on your weapon size
        float height = 0.2f;
        float offsetX = direction.x * 0.5f;
        float offsetY = direction.y * 0.5f;

        shape.setAsBox(width, height, new Vector2(offsetX, offsetY), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = ContactType.ATTACK.getCategoryBits();
        fixtureDef.filter.maskBits = ContactType.ATTACK.getCategoryBits();

        attackSensor = sensorBody.createFixture(fixtureDef);
        attackSensor.setUserData("playerAttack");

        shape.dispose();
    }

    private void removeAttackSensor() {
        if (attackSensor != null && attackSensor.getBody() != null) {
            world.destroyBody(attackSensor.getBody());
            attackSensor = null;
        }
    }

    private void positionAttackSprite(Vector2 position, Vector2 facingDirection) {
        // Get the current frame's dimensions
        float frameWidth = attackSprite.getWidth();
        float frameHeight = attackSprite.getHeight();

        // Calculate the character center
        float characterCenterX = position.x;
        float characterCenterY = position.y;

        // Initialize offsets
        float offsetX = 0;
        float offsetY = 0;

        // Different offset handling for side-facing attacks
        if (lastDirection.equals("idleSide")) {
            if (isFacingRight) {
                // Right-facing horizontal attack - apply minimal offset or none
                offsetX = frameWidth * 0.05f;  // Reduced from 0.16f to 0.05f
            } else {
                // Left-facing horizontal attack - apply minimal offset or none
                offsetX = -frameWidth * 0.05f;  // Add symmetric offset
            }
        } else if (lastDirection.equals("idleUp")) {
            // No horizontal offset needed for up attack
            offsetY = frameHeight * 0.05f;  // Small vertical offset if needed
        } else if (lastDirection.equals("idleDown")) {
            // No horizontal offset needed for down attack
            offsetY = -frameHeight * 0.05f;  // Small vertical offset if needed
        } else if (lastDirection.equals("idleDiagonalUp")) {
            // Handle diagonal up with minimal offsets
            offsetX = isFacingRight ? frameWidth * 0.05f : -frameWidth * 0.05f;
            offsetY = frameHeight * 0.05f;
        } else if (lastDirection.equals("idleDiagonalDown")) {
            // Handle diagonal down with minimal offsets
            offsetX = isFacingRight ? frameWidth * 0.05f : -frameWidth * 0.05f;
            offsetY = -frameHeight * 0.05f;
        }

        // IMPORTANT: When setting the position, make sure the CENTER of the attack sprite
        // aligns with the body position, accounting for any offsets
        attackSprite.setPosition(
                characterCenterX - frameWidth/2 + offsetX,
                characterCenterY - frameHeight/2 + offsetY
        );

        // Handle sprite flipping based on facing direction
        if (!isFacingRight) {
            if (!attackSprite.isFlipX()) {
                attackSprite.flip(true, false);
            }
        } else {
            if (attackSprite.isFlipX()) {
                attackSprite.flip(true, false);
            }
        }
    }

    private void updateHitbox() {
        // Create a hitbox slightly smaller than the sprite
        float hitboxScale = .8f; // 80% of the sprite size

        hitbox.set(
                attackSprite.getX() + attackSprite.getWidth() * (1 - hitboxScale) / 2,
                attackSprite.getY() + attackSprite.getHeight() * (1 - hitboxScale) / 2,
                attackSprite.getWidth() * hitboxScale,
                attackSprite.getHeight() * hitboxScale
        );
    }

    public boolean startAttack(Vector2 facingDirection) {
        if (attackCooldown <= 0 && !isAttacking) {
            isAttacking = true;
            attackStateTime = 0f;
            currentAttackTimer = 0f;

            // Set the correct attack animation based on direction
            setAttackAnimation(facingDirection.x, facingDirection.y);

            // Reset cooldown
            attackCooldown = 0.5f;

            return true;
        }
        return false;
    }


    // Update the startAttack method to create the sensor
    public boolean startAttack(String direction, Vector2 playerPosition) {
        if (attackCooldown <= 0 && !isAttacking) {
            isAttacking = true;
            attackStateTime = 0f;
            currentAttackTimer = 0f;

            // Set the correct attack animation based on direction string
            if (direction.equals("idleUp")) {
                currentAttackAnimation = attackAnimations.get("attackUp");
            } else if (direction.equals("idleDown")) {
                currentAttackAnimation = attackAnimations.get("attackDown");
            } else if (direction.equals("idleSide")) {
                currentAttackAnimation = attackAnimations.get("attackHorizontal");
            } else if (direction.equals("idleDiagonalUp")) {
                currentAttackAnimation = attackAnimations.get("attackDiagonalUp");
            } else if (direction.equals("idleDiagonalDown")) {
                currentAttackAnimation = attackAnimations.get("attackDiagonalDown");
            } else {
                // Default to horizontal attack if direction is unknown
                currentAttackAnimation = attackAnimations.get("attackHorizontal");
            }

            // Create attack sensor with proper direction
            Vector2 attackDirection = getDirectionVector(direction);
            createAttackSensor(playerPosition, attackDirection);

            // Reset cooldown
            attackCooldown = 0.5f;

            return true;
        }
        return false;
    }
    // Helper method to convert direction string to vector
    private Vector2 getDirectionVector(String direction) {
        Vector2 dirVector = new Vector2(0, 0);

        switch(direction) {
            case "idleUp":
                dirVector.set(0, 1);
                break;
            case "idleDown":
                dirVector.set(0, -1);
                break;
            case "idleSide":
                dirVector.set(isFacingRight ? 1 : -1, 0);
                break;
            case "idleDiagonalUp":
                dirVector.set(isFacingRight ? 0.7f : -0.7f, 0.7f);
                break;
            case "idleDiagonalDown":
                dirVector.set(isFacingRight ? 0.7f : -0.7f, -0.7f);
                break;
            default:
                dirVector.set(isFacingRight ? 1 : -1, 0);
                break;
        }

        return dirVector.nor();
    }

    private void setAttackAnimation(float vx, float vy) {
        if (vy > 0.1f) {
            if (Math.abs(vx) > 0.1f) {
                currentAttackAnimation = attackAnimations.get("attackDiagonalUp");
            } else {
                currentAttackAnimation = attackAnimations.get("attackUp");
            }
        } else if (vy < -0.1f) {
            if (Math.abs(vx) > 0.1f) {
                currentAttackAnimation = attackAnimations.get("attackDiagonalDown");
            } else {
                currentAttackAnimation = attackAnimations.get("attackDown");
            }
        } else {
            currentAttackAnimation = attackAnimations.get("attackHorizontal");
        }
    }


    private void endAttack() {
        isAttacking = false;
        attackStateTime = 0f;
        currentAttackTimer = 0f;
    }

    public boolean checkHit(Rectangle targetBounds) {
        if (isAttacking) {
            // Check if the attack hitbox overlaps with the target bounds
            return hitbox.overlaps(targetBounds);
        }
        return false;
    }

    public float getAttackDamage() {
        return attackDamage;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public Sprite getAttackSprite() {
        return attackSprite;
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public float getAttackProgress() {
        if (!isAttacking) return 0;
        return currentAttackTimer / attackDuration;
    }

    public void setAttackDamage(float damage) {
        this.attackDamage = damage;
    }

    public void setCooldownTime(float cooldownTime) {
        this.attackCooldown = cooldownTime;
    }


}