package com.mygdx.eightfold.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class PlayerInputHandler {
    private Player player;

    public PlayerInputHandler(Player player) {
        this.player = player;
    }

    public void processInput() {
        float velX = 0;
        float velY = 0;

        // Movement input
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            velX = 1;
            if (!player.isFacingRight()) player.setFacingRight(true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            velX = -1;
            if (player.isFacingRight()) player.setFacingRight(false);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            velY = 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            velY = -1;
        }

        // Attack input
        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && player.getCurrentState() != Player.State.ATTACKING) {
            player.triggerAttack(); // Create this method in Player that handles sound and state change
            return;
        }

        // Process sensor toggle
        if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
            player.toggleSensor();
        }

        // Combine input direction and normalize
        Vector2 direction = new Vector2(velX, velY);
        if (direction.len() > 0) {
            direction.nor();
        }

        // Handle movement based on player state
        if (player.getCurrentState() != Player.State.ATTACKING) {
            float speedMultiplier = Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) ? 1.5f : 1.0f;
            player.move(direction, speedMultiplier);
        } else {
            player.stopMovement();
        }
    }
}