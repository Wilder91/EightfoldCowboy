package com.mygdx.eightfold.player;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import helper.combat.MeleeCombatHelper;
public class PlayerRenderer {
    private Player player;
    private MeleeCombatHelper meleeHelper;

    public PlayerRenderer(Player player, MeleeCombatHelper meleeHelper) {
        this.player = player;
        this.meleeHelper = meleeHelper;
    }

    public void render(SpriteBatch batch) {
        float centerX = player.getX();
        float centerY = player.getY();

        Sprite sprite = player.getSprite(); // Get the current sprite from player

        if (sprite == null) {
            System.err.println("Warning: Player sprite is null in rendering component");
            return; // Skip rendering if sprite is null
        }

        if (meleeHelper != null && meleeHelper.isAttacking()) {
            // Render attack animation
            Sprite attackSprite = meleeHelper.getAttackSprite();
            if (attackSprite != null) {
                attackSprite.draw(batch);
            }
        } else {
            // Render normal sprite
            sprite.setPosition(
                    centerX - sprite.getWidth() / 2,
                    centerY - sprite.getHeight() / 2
            );
            sprite.draw(batch);
        }
    }
}
