package helper.combat;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.ContactType;

import java.util.*;

public abstract class MeleeCombatHelper {
    protected Map<String, Animation<TextureRegion>> attackAnimations;
    protected Animation<TextureRegion> currentAttackAnimation;
    protected float attackStateTime;
    protected Sprite attackSprite;
    protected GameAssets gameAssets;
    protected String animalType;
    protected String animalName;
    protected String weaponType;
    protected boolean isFacingRight;
    protected boolean isAttacking;
    protected float attackCooldown;
    protected float attackDamage;
    protected Rectangle hitbox;
    protected float attackDuration;
    protected float currentAttackTimer;
    protected Fixture attackSensor;
    protected World world;
    protected String lastDirection = "";
    protected ContactType contactType;
    protected ContactType enemyContactType;
    protected ScreenInterface screenInterface;
    protected int worldStepCounter = 0;
    protected float multiplier;
    protected float frameDuration;
    protected Set<Integer> hitEntitiesForCurrentAttack = new HashSet<>();
    private Sprite sprite;
    public MeleeCombatHelper() {
        this.sprite = new Sprite();

    }

    public void setFacingRight(boolean facingRight) {
    }

    public void update(float delta, Vector2 position, Vector2 direction, boolean facingRight, String lastDirection) {
    }



    public Sprite getSprite() {
        return sprite;
    }

    public boolean isAttacking() {
        return isAttacking;
    }

    public boolean startAttack(String lastDirection, Vector2 position) {
        return false;
    }
}