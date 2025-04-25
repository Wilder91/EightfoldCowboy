package helper;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import helper.combat.MeleeCombatHelper;
import objects.GameEntity;

public class EntityRenderer {
    protected GameEntity entity;
    protected Sprite sprite;
    protected MeleeCombatHelper meleeCombatHelper;

    public EntityRenderer(GameEntity entity) {
        this.entity = entity;
        // This constructor is missing meleeCombatHelper initialization
    }

    public EntityRenderer(GameEntity entity, MeleeCombatHelper meleeCombatHelper) {
        this.entity = entity;
        this.meleeCombatHelper = meleeCombatHelper;
        // The mainSprite might be null initially and set later
    }

    public void setMainSprite(Sprite sprite) {
        this.sprite = sprite;
    }

    public void update(float delta) {
        // Basic update logic
        // Override in specialized renderers if needed
    }

    public void render(SpriteBatch batch) {
        if (sprite == null) {
            System.err.println("Warning: Player sprite is null in rendering component");
            return; // Skip rendering if sprite is null
        }

        if (meleeCombatHelper != null && meleeCombatHelper.isAttacking()) {
            // Render attack animation
            Sprite attackSprite = meleeCombatHelper.getSprite();
            if (attackSprite != null) {
                sprite.flip(true,false);
                attackSprite.draw(batch);
            }
        } else {
            // Render normal sprite
            sprite.setPosition(
                    entity.getX() - sprite.getWidth() / 2,
                    entity.getY() - sprite.getHeight() / 2
            );
            sprite.draw(batch);
        }
    }
}