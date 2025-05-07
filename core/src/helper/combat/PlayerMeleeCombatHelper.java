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
import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyUserData;
import helper.ContactType;
import objects.enemies.ThicketSaint;

import java.util.*;

import static helper.Constants.*;

public class PlayerMeleeCombatHelper extends MeleeCombatHelper {
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
    private float attackDuration;
    private float currentAttackTimer;
    private Fixture attackSensor;
    private World world;
    private String lastDirection = "";
    private ContactType contactType;
    private ContactType enemyContactType;
    private ScreenInterface screenInterface;
    private int worldStepCounter = 0;
    private float multiplier;
    private float frameDuration;
    private Set<Integer> hitEntitiesForCurrentAttack = new HashSet<>();


    public PlayerMeleeCombatHelper(GameAssets gameAssets, String animalType, String animalName,
                                   String weaponType, float attackDamage,
                                   World world, float frameDuration, ContactType contactType, ContactType enemyContactType,
                                   ScreenInterface screenInterface, float scaleX, float scaleY) {
        super();  // Added frameDuration parameter
        this.gameAssets = gameAssets;
        this.animalType = animalType;
        this.animalName = animalName;
        this.attackStateTime = 0f;
        this.attackAnimations = new HashMap<>();
        this.isAttacking = false;
        this.attackCooldown = 0.5f; // Half second between attacks
        this.attackDamage = attackDamage;
        this.currentAttackTimer = 0f;
        this.hitbox = new Rectangle();
        this.world = world;
        this.weaponType = weaponType;
        this.contactType = contactType;
        this.enemyContactType = enemyContactType;
        this.screenInterface = screenInterface;
        this.frameDuration = frameDuration;
        // Pass frameDuration to the loadAttackAnimations method
        loadAttackAnimations(frameDuration);
        this.multiplier = scaleX;
        this.currentAttackAnimation = attackAnimations.get("Horizontal");
        this.attackSprite = new Sprite();
        this.attackSprite.setOriginCenter();

        // Calculate attack duration based on animation frames * frame duration
        // For example, if you want the attack to last for the length of the animation:
        this.attackDuration = 80 * frameDuration;
    }

    public void loadAttackAnimations(float frameDuration) {
        //System.out.println(frameCounter);

        String atlasPath = "atlases/eightfold/" + animalType + "-movement.atlas";
        // Populate the animations map with all available attack animations
        attackAnimations.put("attackUp", createAnimation(animalName + "_up_" + weaponType,
                atlasPath));
        attackAnimations.put("attackDown", createAnimation(animalName + "_down_" + weaponType,
                 atlasPath));
        attackAnimations.put("attackHorizontal", createAnimation(animalName + "_horizontal_" + weaponType,
                atlasPath));
        attackAnimations.put("attackDiagonalUp", createAnimation(animalName + "_diagUP_" + weaponType,
                atlasPath));
        attackAnimations.put("attackDiagonalDown", createAnimation(animalName + "_diagDOWN_" + weaponType,
                atlasPath));
    }

    public int getFrameIndex(){
        return currentAttackAnimation.getKeyFrameIndex(attackStateTime);
    }


    private Animation<TextureRegion> createAnimation(String regionNamePrefix, String atlasPath) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

        int i = 1;
        TextureRegion region;
        while ((region = atlas.findRegion(regionNamePrefix, i)) != null) {
            frames.add(region);
            i++;
        }

        if (frames.size == 0) {
            //System.err.println("No regions found with prefix: " + regionNamePrefix);
        }

        return new Animation<>(this.frameDuration, frames, Animation.PlayMode.LOOP);
    }



    public void update(float delta, Vector2 position, Vector2 facingDirection, boolean isFacingRight, String lastDirection) {
        this.isFacingRight = isFacingRight;
        this.lastDirection = lastDirection;
        // Update attack cooldown
        if (attackCooldown > 0) {
            attackCooldown -= delta;
        }
        // Update attack state

        if (isAttacking && attackSensor != null && attackSensor.getBody() != null) {
            // Get the attack sensor bounds
            PolygonShape sensorShape = (PolygonShape) attackSensor.getShape();
            Vector2 center = new Vector2();


            // Get the size of the attack sensor
            sensorShape.getVertex(0, center); // Get one corner



            if (isAttacking) {

                worldStepCounter++;
                //System.out.println("Attack sensor exists for " + worldStepCounter + " world steps");
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
                if (currentAttackTimer >= attackDuration * 1.5f ||
                        (currentAttackAnimation.isAnimationFinished(attackStateTime) && currentAttackTimer >= 0.3f)) {
                    endAttack();
                    removeAttackSensor();
                }
            }
        }
    }

    private void createSwordBladeShape(float length, float baseWidth) {
        // Create a long triangular shape for a sword blade
        PolygonShape shape = new PolygonShape();

        // Define vertices for a sword blade (long triangle with thin base)
        Vector2[] vertices = new Vector2[3];

        // Base of the sword (near the handle)
        vertices[0] = new Vector2(0, -baseWidth/2);  // Bottom left of base
        vertices[1] = new Vector2(0, baseWidth/2);   // Top left of base

        // Tip of the sword
        vertices[2] = new Vector2(length, 0);        // Pointed tip

        // Set the polygon shape with these vertices
        shape.set(vertices);

        // Rest of your fixture creation code...
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        // ... other fixture properties

        // Don't forget to dispose the shape when done
        shape.dispose();
    }







    private void createAttackSensor(Vector2 position, Vector2 direction, float multiplier) {
        // Remove any existing sensor
        removeAttackSensor();

        // Create a temporary body for the attack
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(position.x / PPM, position.y / PPM);
        bodyDef.bullet = true; // Use CCD for fast-moving objects

        Body sensorBody = screenInterface.getWorld().createBody(bodyDef);

        // Default dimensions
        float width = 0.3f;
        float height = 0.2f;
        float offsetX = 0f;
        float offsetY = 0f;
        float angle = 0f;

        // Adjust dimensions and offset based on direction
        if (lastDirection.equalsIgnoreCase("idleUp")) {
            // For upward attacks, make the hitbox taller than wide
            width = 0.5f * multiplier;      // Narrower width
            height = 0.5f * multiplier;    // Taller height
            offsetY = 0.5f;                 // Position above player
            offsetX = 0f;                   // Centered horizontally
        } else if (lastDirection.equalsIgnoreCase("idleDown")) {
            // For downward attacks, make the hitbox taller than wide
            width = 0.5f * multiplier;      // Narrower width
            height = 0.5f * multiplier;     // Taller height
            offsetY = -0.5f;                // Position below player
            offsetX = 0f;                   // Centered horizontally
        } else if (lastDirection.equalsIgnoreCase("idleSide")) {
            // For side attacks, make the hitbox wider than tall
            width = 0.35f * multiplier;     // Wider width
            height = 0.35f * multiplier;    // Shorter height
            offsetX = isFacingRight ? 0.42f : -0.42f;  // Position to the side based on facing
            offsetY = 0f;                   // Centered vertically
        } else if (lastDirection.equalsIgnoreCase("idleDiagonalUp")) {
            // For diagonal up attacks
            width = 0.3f * multiplier;
            height = 0.5f * multiplier;
            offsetX = isFacingRight ? 0.3f : -0.3f;
            offsetY = 0.3f;
            angle = isFacingRight ? 45 * (float)Math.PI/180 : -45 * (float)Math.PI/180;  // 45 degree angle
        } else if (lastDirection.equalsIgnoreCase("idleDiagonalDown")) {
            // For diagonal down attacks
            width = 0.3f * multiplier;
            height = 0.5f * multiplier;
            offsetX = isFacingRight ? 0.3f : -0.3f;
            offsetY = -0.3f;
            angle = isFacingRight ? -45 * (float)Math.PI/180 : 45 * (float)Math.PI/180;  // -45 degree angle
        } else {
            // Default to horizontal attack
            width = 0.4f * multiplier;
            height = 0.2f * multiplier;
            offsetX = isFacingRight ? 0.5f : -0.5f;
            offsetY = 0f;
        }

        // Create the sensor shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height, new Vector2(offsetX, offsetY), angle);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;

        sensorBody.setActive(true);
        attackSensor = sensorBody.createFixture(fixtureDef);
        attackSensor.setUserData(new BodyUserData(0, contactType, sensorBody, "player_sword"));
        //System.out.println("attack sensor user data: " + attackSensor.getUserData());
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



    // Update the startAttack method to create the sensor
    public boolean startAttack(String direction, Vector2 playerPosition) {
        //System.out.println("Starting new attack, cleared hit entities list, size: " + hitEntitiesForCurrentAttack.size());
        hitEntitiesForCurrentAttack.clear();

        if (!isAttacking) {
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
            createAttackSensor(playerPosition, attackDirection, multiplier);


            // Reset cooldown
            attackCooldown = 1f;

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
        worldStepCounter = 0;
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

    public Sprite getSprite() {
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


    public void setFacingRight(boolean isFacingRight) {
        this.isFacingRight = isFacingRight;
    }


}