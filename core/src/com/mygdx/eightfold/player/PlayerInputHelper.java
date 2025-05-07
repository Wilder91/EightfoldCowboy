package com.mygdx.eightfold.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.eightfold.commands.AttackCommand;
import com.mygdx.eightfold.commands.Command;
import com.mygdx.eightfold.commands.MovementCommand;
import com.mygdx.eightfold.commands.ToggleSensorCommand;

import java.util.HashMap;
import java.util.Map;

public class PlayerInputHelper {
    private Player player;
    private Map<Integer, Command> keyJustPressedCommands;
    private MovementCommand movementCommand;

    public PlayerInputHelper(Player player) {
        this.player = player;
        this.movementCommand = new MovementCommand(player);
        initializeCommands();
    }

    private void initializeCommands() {
        keyJustPressedCommands = new HashMap<>();

        // One-time press commands
        keyJustPressedCommands.put(Input.Keys.SPACE, new AttackCommand(player));
        keyJustPressedCommands.put(Input.Keys.TAB, new ToggleSensorCommand(player));
    }

    public void processInput() {
        // Process one-shot commands first
        for (Map.Entry<Integer, Command> entry : keyJustPressedCommands.entrySet()) {
            if (Gdx.input.isKeyJustPressed(entry.getKey())) {
                entry.getValue().execute();
                // If attack was executed, don't process movement
                if (entry.getKey() == Input.Keys.SPACE && player.getCurrentPlayerState() == Player.State.ATTACKING) {
                    return;
                }
            }
        }

        // Process movement if not attacking
        if (player.getCurrentPlayerState() != Player.State.ATTACKING) {
            Vector2 direction = new Vector2(0, 0);
            boolean isSprinting = false;

            // Get movement input
            if (Gdx.input.isKeyPressed(Input.Keys.D)) {
                direction.x += 1;
                if (!player.isFacingRight()) player.setFacingRight(true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.A)) {
                direction.x -= 1;
                if (player.isFacingRight()) player.setFacingRight(false);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.W)) {
                direction.y += 1;
            }
            if (Gdx.input.isKeyPressed(Input.Keys.S)) {
                direction.y -= 1;
            }

            // Check sprint
            if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                isSprinting = true;
            }

            // Execute movement command
            movementCommand.setParameters(direction, isSprinting);
            movementCommand.execute();
        } else {
            player.stopMovement();
        }
    }
}