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

public class EnemyMeleeCombatHelper extends MeleeCombatHelper {
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

    public EnemyMeleeCombatHelper(GameAssets gameAssets, String animalType, String animalName,
                                  String weaponType, float attackDamage,
                                  World world, float frameDuration, ContactType contactType, ContactType enemyContactType,
                                  ScreenInterface screenInterface, float scaleX, float scaleY) {
        super();
        this.gameAssets = gameAssets;
        this.animalType = animalType;
        this.animalName = animalName;
        this.attackStateTime = 0f;
        this.attackAnimations = new HashMap<>();
        this.isAttacking = false;
        this.attackCooldown = 0.5f;
        this.attackDamage = attackDamage;
        this.currentAttackTimer = 0f;
        this.hitbox = new Rectangle();
        this.world = world;
        this.weaponType = weaponType;
        this.contactType = contactType;
        this.enemyContactType = enemyContactType;
        this.screenInterface = screenInterface;
        this.frameDuration = frameDuration;
        loadAttackAnimations(frameDuration);
        this.multiplier = scaleX;
        this.currentAttackAnimation = attackAnimations.get("attackHorizontal");
        this.attackSprite = new Sprite();
        this.attackSprite.setOriginCenter();
        this.attackDuration = 8 * frameDuration;
    }

    public void loadAttackAnimations(float frameDuration) {
        String atlasPath = "atlases/eightfold/" + animalType + "-movement.atlas";
        // Populate the animations map with all available attack animations
        attackAnimations.put("attackUp", createAnimation(animalName + "_up_" + weaponType, atlasPath));
        attackAnimations.put("attackDown", createAnimation(animalName + "_down_" + weaponType, atlasPath));
        attackAnimations.put("attackHorizontal", createAnimation(animalName + "_horizontal_" + weaponType, atlasPath));
        attackAnimations.put("attackDiagonalUp", createAnimation(animalName + "_diagUP_" + weaponType, atlasPath));
        attackAnimations.put("attackDiagonalDown", createAnimation(animalName + "_diagDOWN_" + weaponType, atlasPath));
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

        if (isAttacking && attackSensor != null && attackSensor.getBody() != null) {
            // Get the attack sensor bounds
            PolygonShape sensorShape = (PolygonShape) attackSensor.getShape();
            Vector2 center = new Vector2();
            sensorShape.getVertex(0, center);

            // Calculate approximate AABB for query
            Vector2 sensorPos = attackSensor.getBody().getPosition();
            float sensorWidth = 0.5f;
            float sensorHeight = 0.5f;

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
                Array<Fixture> fixturesToRemove = new Array<>();

                for (Fixture woundedFixture : foundFixtures) {
                    BodyUserData userData = (BodyUserData) woundedFixture.getUserData();

                    if (hitEntitiesForCurrentAttack.contains(userData.getId())) {
                        // Skip this entity, it's already been hit
                    } else {
                        // Apply damage based on entity type
                        if (userData.getEntity() instanceof ThicketSaint) {
                            ThicketSaint enemy = (ThicketSaint) userData.getEntity();
                            //enemy.takeDamage();
                        } else if (userData.getEntity() instanceof Player) {
                            Player player = (Player) userData.getEntity();
                            player.takeDamage();
                        }
                        hitEntitiesForCurrentAttack.add(userData.getId());
                        fixturesToRemove.add(woundedFixture);
                    }
                }
                foundFixtures.removeAll(fixturesToRemove, true);
            }

            if (isAttacking) {
                worldStepCounter++;
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

    private void createAttackSensor(Vector2 position, Vector2 direction, float multiplier) {
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

        // Determine the attack direction based on the player's position
        Vector2 playerPos = screenInterface.getPlayer().getBody().getPosition();
        Vector2 enemyPos = new Vector2(position.x / PPM, position.y / PPM);
        Vector2 attackDir = new Vector2(playerPos.x - enemyPos.x, playerPos.y - enemyPos.y).nor();

        // Set the facing direction based on the attack direction
        isFacingRight = attackDir.x > 0;

        // Determine the attack type based on the angle to the player
        float angle2Player = attackDir.angle();
        String attackType;

        // Convert angle to a direction: up, down, left, right, or diagonal
        if (angle2Player >= 45 && angle2Player < 135) {
            // Up
            attackType = "idleUp";
            width = 0.5f * multiplier;
            height = 0.25f * multiplier;
            offsetY = 0.5f;
            offsetX = 0f;
            currentAttackAnimation = attackAnimations.get("attackUp");
        } else if (angle2Player >= 225 && angle2Player < 315) {
            // Down
            attackType = "idleDown";
            width = 0.5f * multiplier;
            height = 0.3f * multiplier;
            offsetY = -0.2f;
            offsetX = 0f;
            currentAttackAnimation = attackAnimations.get("attackDown");
        } else if ((angle2Player >= 0 && angle2Player < 45) || (angle2Player >= 315 && angle2Player <= 360)) {
            // Right
            attackType = "idleSide";
            width = 0.25f * multiplier;
            height = 0.35f * multiplier;
            offsetX = 0.32f;
            offsetY = 0f;
            currentAttackAnimation = attackAnimations.get("attackHorizontal");
        } else if (angle2Player >= 135 && angle2Player < 225) {
            // Left
            attackType = "idleSide";
            width = 0.25f * multiplier;
            height = 0.35f * multiplier;
            offsetX = -0.52f;
            offsetY = 0f;
            currentAttackAnimation = attackAnimations.get("attackHorizontal");
        } else if ((angle2Player >= 0 && angle2Player < 45) || (angle2Player >= 315 && angle2Player <= 360)) {
            // Up-Right or Down-Right
            if (attackDir.y > 0) {
                attackType = "idleDiagonalUp";
                width = 0.3f * multiplier;
                height = 0.5f * multiplier;
                offsetX = 0.3f;
                offsetY = 0.3f;
                angle = 45 * (float)Math.PI/180;
                currentAttackAnimation = attackAnimations.get("attackDiagonalUp");
            } else {
                attackType = "idleDiagonalDown";
                width = 0.3f * multiplier;
                height = 0.5f * multiplier;
                offsetX = 0.3f;
                offsetY = -0.3f;
                angle = -45 * (float)Math.PI/180;
                currentAttackAnimation = attackAnimations.get("attackDiagonalDown");
            }
        } else {
            // Up-Left or Down-Left
            if (attackDir.y > 0) {
                attackType = "idleDiagonalUp";
                width = 0.3f * multiplier;
                height = 0.5f * multiplier;
                offsetX = -0.3f;
                offsetY = 0.3f;
                angle = -45 * (float)Math.PI/180;
                currentAttackAnimation = attackAnimations.get("attackDiagonalUp");
            } else {
                attackType = "idleDiagonalDown";
                width = 0.3f * multiplier;
                height = 0.5f * multiplier;
                offsetX = -0.3f;
                offsetY = -0.3f;
                angle = 45 * (float)Math.PI/180;
                currentAttackAnimation = attackAnimations.get("attackDiagonalDown");
            }
        }

        // Save the direction for animation purposes
        this.lastDirection = attackType;

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
        attackSensor.setUserData(new BodyUserData(0, contactType, sensorBody, animalName));

        shape.dispose();
    }


//    public void setFacingRight(boolean isFacingRight) {
//        this.isFacingRight = isFacingRight;
//    }

//    @Override
//    public void update(float delta, Vector2 position, Vector2 facingDirection, boolean isFacingRight, String lastDirection) {
//        // Calculate direction to player for real-time tracking
//        if (isAttacking && screenInterface.getPlayer() != null) {
//            Vector2 playerPos = screenInterface.getPlayer().getBody().getPosition();
//            Vector2 enemyPos = new Vector2(position.x / PPM, position.y / PPM);
//            Vector2 dirToPlayer = new Vector2(playerPos.x - enemyPos.x, playerPos.y - enemyPos.y);
//
//            // Update the facing direction based on player position
//            boolean shouldFaceRight = dirToPlayer.x > 0;
//            if (this.isFacingRight != shouldFaceRight) {
//                this.isFacingRight = shouldFaceRight;
//            }
//        }
//
//        // Call the parent update method
//        super.update(delta, position, facingDirection, this.isFacingRight, lastDirection);
//    }

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
                offsetX = frameWidth * 0.05f;
            } else {
                offsetX = -frameWidth * 0.05f;
            }
        } else if (lastDirection.equals("idleUp")) {
            offsetY = frameHeight * 0.05f;
        } else if (lastDirection.equals("idleDown")) {
            offsetY = -frameHeight * 0.05f;
        } else if (lastDirection.equals("idleDiagonalUp")) {
            offsetX = isFacingRight ? frameWidth * 0.05f : -frameWidth * 0.05f;
            offsetY = frameHeight * 0.05f;
        } else if (lastDirection.equals("idleDiagonalDown")) {
            offsetX = isFacingRight ? frameWidth * 0.05f : -frameWidth * 0.05f;
            offsetY = -frameHeight * 0.05f;
        }

        // Set the position of the attack sprite
        attackSprite.setPosition(
                characterCenterX - frameWidth/2 + offsetX,
                characterCenterY - frameHeight/2 + offsetY
        );

        // Handle sprite flipping based on facing direction
        if (!isFacingRight) {
            if (attackSprite.isFlipX()) {
                attackSprite.flip(false, false);
            }
        } else {
            if (!attackSprite.isFlipX()) {
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

    public boolean startAttack(String direction, Vector2 enemyPosition) {
        hitEntitiesForCurrentAttack.clear();

        if (!isAttacking) {
            isAttacking = true;
            attackStateTime = 0f;
            currentAttackTimer = 0f;

            // We'll ignore the direction parameter and always attack toward the player
            Vector2 playerPos = screenInterface.getPlayer().getBody().getPosition();
            Vector2 attackDirection = new Vector2(
                    playerPos.x - enemyPosition.x/PPM,
                    playerPos.y - enemyPosition.y/PPM
            ).nor();

            createAttackSensor(enemyPosition, attackDirection, multiplier);

            // Reset cooldown
            attackCooldown = 1f;

            return true;
        }
        return false;
    }

    private void endAttack() {
        worldStepCounter = 0;
        isAttacking = false;
        attackStateTime = 0f;
        currentAttackTimer = 0f;
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

    public float getAttackDamage() {
        return attackDamage;
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