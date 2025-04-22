package helper.combat;

import com.badlogic.gdx.audio.Sound;
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
import helper.EntityManagers.ThicketSaintManager;
import objects.enemies.ThicketSaint;

import javax.swing.text.html.parser.Entity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
    private ContactType contactType;
    private ContactType enemyContactType;
    private ScreenInterface screenInterface;
    private int worldStepCounter = 0;
    private int frameCounter = 0;
    private Set<Integer> hitEntitiesForCurrentAttack = new HashSet<>();

    public MeleeCombatHelper(GameAssets gameAssets, String animalType, String animalName,
                             String weaponType, int[] attackFrameCounts, float attackDamage,
                             World world, float frameDuration, ContactType contactType, ContactType enemyContactType, ScreenInterface screenInterface) {  // Added frameDuration parameter
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
        this.contactType = contactType;
        this.enemyContactType = enemyContactType;
        this.screenInterface = screenInterface;
        // Pass frameDuration to the loadAttackAnimations method
        loadAttackAnimations(frameDuration);

        this.currentAttackAnimation = attackAnimations.get("attackHorizontal");
        this.attackSprite = new Sprite();
        this.attackSprite.setOriginCenter();

        // Calculate attack duration based on animation frames * frame duration
        // For example, if you want the attack to last for the length of the animation:
        this.attackDuration = attackFrameCounts[2] * frameDuration;
    }

    public void loadAttackAnimations(float frameDuration) {
        frameCounter += 1;
        String atlasPath = "atlases/eightfold/" + animalType + "-movement.atlas";
        // Populate the animations map with all available attack animations
        attackAnimations.put("attackUp", createAnimation(animalName + "_up_" + weaponType,
                attackFrameCounts[0], atlasPath, frameDuration));
        attackAnimations.put("attackDown", createAnimation(animalName + "_down_" + weaponType,
                attackFrameCounts[1], atlasPath, frameDuration));
        attackAnimations.put("attackHorizontal", createAnimation(animalName + "_horizontal_" + weaponType,
                attackFrameCounts[2], atlasPath, frameDuration));
        attackAnimations.put("attackDiagonalUp", createAnimation(animalName + "_diagUP_" + weaponType,
                attackFrameCounts[3], atlasPath, frameDuration));
        attackAnimations.put("attackDiagonalDown", createAnimation(animalName + "_diagDOWN_" + weaponType,
                attackFrameCounts[4], atlasPath, frameDuration));
    }


    private Animation<TextureRegion> createAnimation(String regionNamePrefix, int frameCount,
                                                     String atlasPath, float frameDuration) {
        Array<TextureRegion> frames = new Array<>();
        TextureAtlas atlas = gameAssets.getAtlas(atlasPath);

        if (atlas == null) {
            System.err.println("ERROR: Attack atlas not found: " + atlasPath);
            return new Animation<>(frameDuration, frames);
        }

        for (int i = 1; i <= frameCount; i++) {
            TextureRegion region = atlas.findRegion(regionNamePrefix, i);
            if (region != null) {
                frames.add(region);
            } else {
                System.out.println("Attack region " + regionNamePrefix + "_" + i + " not found!");
            }
            if (frames.size == 0 && attackFrameCounts[frameCounter] != 0) {
                System.err.println("WARNING: No frames found for animation: " + regionNamePrefix);
            }
        }



        return new Animation<>(frameDuration, frames, Animation.PlayMode.NORMAL);
    }

    public void setAttackDuration(float duration) {
        this.attackDuration = duration;
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
            float[] vertices = new float[8]; // For a box, 4 vertices x 2 coords each

            // Get the size of the attack sensor
            sensorShape.getVertex(0, center); // Get one corner

            // Calculate approximate AABB manually based on the sensor body position
            Vector2 sensorPos = attackSensor.getBody().getPosition();
            float sensorWidth = 0.5f; // Approximate - adjust based on your actual sizes
            float sensorHeight = 0.5f;

            //System.out.println("Attack sensor position: " + sensorPos.x + ", " + sensorPos.y);

            // Create a simple AABB query
            final Array<Fixture> foundFixtures = new Array<>();
            world.QueryAABB(
                    new QueryCallback() {
                        @Override
                        public boolean reportFixture(Fixture fixture) {
                            if (fixture.getUserData() instanceof BodyUserData) {
                                BodyUserData userData = (BodyUserData) fixture.getUserData();
                                if (userData.getType() == enemyContactType) {
                                    foundFixtures.add(fixture);
                                    //System.out.println("Found enemy in query: " + userData.getId());
                                    return true;
                                }
                            }
                            return true; // Keep looking for more fixtures
                        }
                    },
                    sensorPos.x - sensorWidth,
                    sensorPos.y - sensorHeight,
                    sensorPos.x + sensorWidth,
                    sensorPos.y + sensorHeight
            );

            if (foundFixtures.size > 0) {
                for (Fixture woundedFixture : foundFixtures) {
                    BodyUserData userData = (BodyUserData)woundedFixture.getUserData();

                    // Check if this entity ID has already been hit in this attack
                    if (hitEntitiesForCurrentAttack.contains(userData.getId())) {
                        // Skip this entity, it's already been hit
                        //System.out.println("Entity " + userData.getId() + " already hit, skipping");
                        continue;
                    }

                    //System.out.println("Directly applying damage to " + userData.getType() + ": " + userData.getId());

                    // Add this entity ID to the set of hit entities
                    hitEntitiesForCurrentAttack.add(userData.getId());

                    // Apply damage based on entity type
                    if(userData.getEntity() instanceof ThicketSaint){
                        ThicketSaint enemy = (ThicketSaint) userData.getEntity();
                        enemy.takeDamage();
                    } else if (userData.getEntity() instanceof Player){
                        Player player = (Player) userData.getEntity();
                        player.takeDamage();
                    }

                    // Play hit sound

                }
            }


        }
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

    private void createAttackSensor(Vector2 position, Vector2 direction) {
        // Remove any existing sensor
        removeAttackSensor();

        // Create a temporary body for the attack
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
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
            width = 0.5f;      // Narrower width
            height = 0.25f;     // Taller height
            offsetY = 0.3f;    // Position above player
            offsetX = 0f;      // Centered horizontally
        } else if (lastDirection.equalsIgnoreCase("idleDown")) {
            // For downward attacks, make the hitbox taller than wide
            width = 0.5f;      // Narrower width
            height = 0.3f;     // Taller height
            offsetY = -0.2f;   // Position below player
            offsetX = 0f;      // Centered horizontally
        } else if (lastDirection.equalsIgnoreCase("idleSide")) {
            // For side attacks, make the hitbox wider than tall
            width = 0.25f;      // Wider width
            height = 0.35f;     // Shorter height
            offsetX = isFacingRight ? 0.52f : -0.52f;  // Position to the side based on facing
            offsetY = 0f;      // Centered vertically
        } else if (lastDirection.equalsIgnoreCase("idleDiagonalUp")) {
            // For diagonal up attacks
            width = 0.3f;
            height = 0.5f;
            offsetX = isFacingRight ? 0.3f : -0.3f;
            offsetY = 0.3f;
            angle = isFacingRight ? 45 * (float)Math.PI/180 : -45 * (float)Math.PI/180;  // 45 degree angle
        } else if (lastDirection.equalsIgnoreCase("idleDiagonalDown")) {
            // For diagonal down attacks
            width = 0.3f;
            height = 0.5f;
            offsetX = isFacingRight ? 0.3f : -0.3f;
            offsetY = -0.3f;
            angle = isFacingRight ? -45 * (float)Math.PI/180 : 45 * (float)Math.PI/180;  // -45 degree angle
        } else {
            // Default to horizontal attack
            width = 0.4f;
            height = 0.2f;
            offsetX = isFacingRight ? 0.5f : -0.5f;
            offsetY = 0f;
        }

        // Create the sensor shape
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width, height, new Vector2(offsetX, offsetY), angle);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.isSensor = true;
        fixtureDef.filter.categoryBits = contactType.getCategoryBits();
        fixtureDef.filter.maskBits = contactType.getMaskBits();
        sensorBody.setActive(true);
        attackSensor = sensorBody.createFixture(fixtureDef);
        attackSensor.setUserData(new BodyUserData(1, contactType, sensorBody, animalName));

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
            createAttackSensor(playerPosition, attackDirection);


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