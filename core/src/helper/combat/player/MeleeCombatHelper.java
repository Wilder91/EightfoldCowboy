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
        attackAnimations.put("attackUp", createAnimation(animalName + "_Up_" + weaponType,  attackFrameCounts[0], atlasPath));
        attackAnimations.put("attackDown", createAnimation(animalName + "_Down_" + weaponType, attackFrameCounts[1], atlasPath));
        attackAnimations.put("attackHorizontal", createAnimation(animalName + "_Horizontal_" + weaponType, attackFrameCounts[2], atlasPath));
        attackAnimations.put("attackDiagonalUp", createAnimation(animalName + "_DiagUP_" + weaponType, attackFrameCounts[3], atlasPath));
        attackAnimations.put("attackDiagonalDown", createAnimation(animalName + "_DiagDOWN_" + weaponType, attackFrameCounts[4], atlasPath));
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
            }
        }
    }

    // Create and destroy the attack sensor when needed
    private void createAttackSensor(Vector2 position, Vector2 direction) {
        // Remove any existing sensor
        if (attackSensor != null && attackSensor.getBody() != null) {
            attackSensor.getBody().destroyFixture(attackSensor);
        }

        // Create a temporary body for the attack
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x / PPM, position.y / PPM);

        Body sensorBody = world.createBody(bodyDef);

        // Create a sensor shape based on attack direction
        PolygonShape shape = new PolygonShape();
        float width = 0.5f; // Adjust based on your weapon size
        float height = 0.3f;
        float offsetX = direction.x * 0.5f;
        float offsetY = direction.y * 0.5f;

        shape.setAsBox(width, height, new Vector2(offsetX, offsetY), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

       // fixtureDef.filter.categoryBits = ContactType.ATTACK.getCategoryBits();
        //fixtureDef.filter.maskBits = ContactType.ENEMY.getCategoryBits();

        //attackSensor = sensorBody.createFixture(fixtureDef);
        attackSensor.setUserData("playerAttack"); // Or any identifier you want

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

        // Calculate the center point of the character
        float characterCenterX = position.x;
        float characterCenterY = position.y;

        // Calculate offsets based on direction
        float offsetX = 0;
        float offsetY = 0;

        // Adjust based on direction
        if (lastDirection.equals("idleUp") || lastDirection.equals("idleDown")) {
            // For vertical attacks, center horizontally
            offsetX = 0;
            // For up attacks, shift slightly up; for down attacks, shift slightly down
            offsetY = lastDirection.equals("idleUp") ? frameHeight * 0.2f : -frameHeight * 0.2f;
        } else if (lastDirection.equals("idleSide")) {
            // For horizontal attacks, shift in facing direction
            offsetX = isFacingRight ? frameWidth * 0.25f : -frameWidth * 0.25f;
            offsetY = 0;
        } else if (lastDirection.equals("idleDiagonalUp")) {
            // For diagonal up attacks
            offsetX = isFacingRight ? frameWidth * 0.2f : -frameWidth * 0.2f;
            offsetY = frameHeight * 0.15f;
        } else if (lastDirection.equals("idleDiagonalDown")) {
            // For diagonal down attacks
            offsetX = isFacingRight ? frameWidth * 0.2f : -frameWidth * 0.2f;
            offsetY = -frameHeight * 0.15f;
        }

        // Set the position, accounting for sprite origin at center
        attackSprite.setPosition(
                characterCenterX - frameWidth/2 + offsetX,
                characterCenterY - frameHeight/2 + offsetY
        );

        // Handle flipping for left-facing attacks
        if (attackSprite.isFlipX() != !isFacingRight) {
            attackSprite.flip(true, false);
        }
    }

    private void updateHitbox() {
        // Create a hitbox slightly smaller than the sprite
        float hitboxScale = 0.8f; // 80% of the sprite size

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

    // Overloaded method that takes a direction string to match player's lastDirection
    public boolean startAttack(String direction) {
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

            // Reset cooldown
            attackCooldown = 0.5f;

            return true;
        }
        return false;
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