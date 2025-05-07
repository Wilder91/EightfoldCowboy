package com.mygdx.eightfold.commands;

import com.badlogic.gdx.math.Vector2;
import com.mygdx.eightfold.player.Player;

public class MovementCommand implements Command {
    private Player player;
    private Vector2 direction;
    private boolean isSprinting;

    public MovementCommand(Player player) {
        this.player = player;
        this.direction = new Vector2();
        this.isSprinting = false;
    }

    public void setParameters(Vector2 direction, boolean isSprinting) {
        this.direction.set(direction);
        this.isSprinting = isSprinting;
    }

    @Override
    public void execute() {
        if (direction.len() > 0) {
            direction.nor();
            float speedMultiplier = isSprinting ? 1.5f : 1.0f;
            player.move(direction, speedMultiplier);
        } else {
            player.stopMovement();
        }
    }
}