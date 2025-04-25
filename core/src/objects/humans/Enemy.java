package objects.humans;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.ContactType;
import helper.EntityAnimator;
import helper.EntityRenderer;
import helper.combat.MeleeCombatHelper;
import helper.movement.SimpleIdleHelper;
import helper.movement.SimpleSpriteWalkingHelper;
import helper.movement.SpriteIdleHelper;

import helper.state.EnemyStateManager;
import objects.GameEntity;

import static helper.Constants.PPM;

public class Enemy extends GameEntity {

    private EntityAnimator animator;
    private EntityRenderer renderer;
    private EnemyStateManager stateManager;
    private String entityType;
    private String entityName;

    public Enemy(float width, float height, Body body, ScreenInterface screenInterface,
                 GameAssets gameAssets, String enemyType, String enemyName, float hp) {
        super(width, height, body, screenInterface, gameAssets, hp);

        this.entityType = enemyType;
        this.entityName = enemyName;

        // Initialize the sprite
        this.sprite = new Sprite();
        this.sprite.setSize(width, height);

        // Initialize helpers with correct parameters
        int idleFrameCounts = 4; // Adjust as needed
        int[] walkingFrameCounts = {8, 8, 8}; // Adjust as needed
        int[] combatFrameCounts = {4, 4, 4, 0, 0}; // Adjust as needed

        // Initialize walking helper
        this.simpleWalkingHelper = new SimpleSpriteWalkingHelper(gameAssets, this, entityType, entityName, walkingFrameCounts, true, 0.5f);

        // Initialize idle helper
        this.simpleIdleHelper = new SimpleIdleHelper(gameAssets, "enemies-movement", entityName, idleFrameCounts, 1.5f);

        // Initialize melee helper
        this.meleeHelper = new MeleeCombatHelper(gameAssets, entityType, entityName, "sword", combatFrameCounts, 5,
                screenInterface.getWorld(), 0.09f, ContactType.ENEMY, ContactType.PLAYER, screenInterface, .5f, .5f);

        // Initialize renderer
        this.renderer = new EntityRenderer(this);

        // Initialize sprite from idle helper
        Sprite idleSprite = simpleIdleHelper.getSprite();
        if (idleSprite != null) {
            this.sprite = idleSprite;
        }

        this.renderer.setMainSprite(sprite);

        // Initialize state manager
        this.stateManager = new EnemyStateManager();
        this.stateManager.setAttackDuration(5f); // Set attack duration
        this.stateManager.changeState(this, State.IDLE);
    }

    /**
     * Start an attack
     */
    public void startAttack() {
        stateManager.changeState(this, State.ATTACKING);
    }

    @Override
    public void update(float delta) {
        // Update position from physics body
        x = body.getPosition().x * PPM;
        y = body.getPosition().y * PPM;
        resetDepthToY();

        // Update state using the state manager
        stateManager.update(this, delta);

        // Update the renderer
        renderer.setMainSprite(sprite);
        renderer.update(delta);
    }

    @Override
    public void render(SpriteBatch batch) {
        renderer.render(batch);
    }

    public String getEntityName() {
        return entityName;
    }
}