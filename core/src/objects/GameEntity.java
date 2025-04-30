package objects;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.combat.MeleeCombatHelper;
import helper.combat.PlayerMeleeCombatHelper;
import helper.movement.*;

import java.util.Comparator;

public abstract class GameEntity {

    public boolean markedForRemoval = false;
    public boolean hasValidTarget() {
        return true;
    }

    public void remove() {
        screenInterface.removeEntity(this);
    }




    public enum State {
        IDLE,
        RUNNING,
        ATTACKING,
        PURSUING,
        WOUNDED,
        DYING
    }

    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected Body body;
    protected ScreenInterface screenInterface;
    protected GameAssets gameAssets;
    protected float hp;
    protected float depth;
    protected float depthOffset = 0f;
    protected float speed = 1.0f; // Default speed value

    // Helpers that can be used by subclasses
    protected SpriteWalkingHelper walkingHelper;
    protected SpriteMovementHelper movementHelper;
    protected SimpleSpriteWalkingHelper simpleWalkingHelper;
    protected SpriteIdleHelper idleHelper;
    protected SimpleIdleHelper simpleIdleHelper;
    protected SimpleCombatWalkingHelper combatWalkingHelper;
    protected PlayerMeleeCombatHelper meleeHelper;
    protected Sprite sprite;
    protected String lastDirection = "idleDown";
    protected boolean isFacingRight = false;
    protected State currentState = State.IDLE;
    protected int entityId;

    public GameEntity(float width, float height, Body body, ScreenInterface screenInterface, GameAssets gameAssets, float hp) {


        this.width = width;
        this.height = height;
        this.body = body;
        this.screenInterface = screenInterface;
        this.gameAssets = gameAssets;
        this.hp = hp;
        this.entityId = 0;

        if (body != null) {
            this.x = body.getPosition().x;
            this.y = body.getPosition().y;
        }
    }
    // Comparator for Y-based depth sorting
    public static final Comparator<GameEntity> Y_COMPARATOR =
            (entity1, entity2) -> Float.compare(entity1.depth, entity2.depth);



    // Common getters for helpers that all entities can use
    public SpriteWalkingHelper getWalkingHelper() {
        return walkingHelper;
    }

    public SimpleSpriteWalkingHelper getSimpleWalkingHelper(){
        return simpleWalkingHelper;
    }



    public SpriteIdleHelper getIdleHelper() {
        return idleHelper;
    }

    public SimpleIdleHelper getSimpleIdleHelper() {
        return this.simpleIdleHelper;
    }

    public int getId(){
        return entityId;
    }

    public void setBody(Body b) {
        this.body = b;
    }

    public MeleeCombatHelper getMeleeHelper() {
        return meleeHelper;
    }

    public SimpleCombatWalkingHelper getCombatWalkingHelper() {
        return combatWalkingHelper;
    }

    public boolean isMarkedForRemoval() {
        return markedForRemoval;
    }

    public SpriteMovementHelper getMovementHelper() {
        return movementHelper;
    }

    public boolean shouldAttack() {
        return false;
    }

    protected Vector2 getFacingDirection() {

        Vector2 direction = new Vector2(0, 0);

        switch (lastDirection) {
            case "idleUp":
                direction.set(0, 1);
                break;
            case "idleDown":
                direction.set(0, -1);
                break;
            case "idleSide":
                direction.set(isFacingRight ? 1 : -1, 0);
                break;
            case "idleDiagonalUp":
                direction.set(isFacingRight ? 1 : -1, 1);
                break;
            case "idleDiagonalDown":
                direction.set(isFacingRight ? 1 : -1, -1);
                break;
            default:
                direction.set(isFacingRight ? 1 : -1, 0);
                break;
        }

        return direction.nor();
    }

    public Sprite getSprite() {
        return sprite;
    }

    public void setSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public String getLastDirection() {
        return lastDirection;
    }

    public void setLastDirection(String direction) {
        this.lastDirection = direction;
    }

    public boolean isFacingRight() {
        return isFacingRight;
    }

    public void setFacingRight(boolean facingRight) {
        this.isFacingRight = facingRight;
    }

    public State getState() {
        return currentState;
    }

    public void setState(State state) {
        this.currentState = state;
    }

    public Body getBody() {
        return body;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getHp() {
        return hp;
    }

    public void setHp(float hp) {
        this.hp = hp;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public void resetDepthToY() {
        this.depth = -this.y;
    }

    public float getDepth() {
        return depth;
    }

    public GameAssets getGameAssets() {
        return gameAssets;
    }

    public ScreenInterface getScreenInterface() {
        return screenInterface;
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        if (this.body != null) {
            this.body.setTransform(x, y, this.body.getAngle());
        }
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public void setDepthOffset(float depthOffset) {
        this.depthOffset = depthOffset;
    }

    public void dispose() {
        // Default implementation - subclasses can override
    }

    public abstract void update(float delta);

    public abstract void render(SpriteBatch batch);

    public void takeDamage() {
        // Default implementation
        this.hp -= 5;
    }
    public void setDepth(float y){
        this.depth = y;
    }
}