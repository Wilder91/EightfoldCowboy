package com.mygdx.eightfold.commands;


import com.mygdx.eightfold.player.Player;

public class AttackCommand implements Command {
    private Player player;

    public AttackCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute() {
        if (player.getCurrentPlayerState() != Player.State.ATTACKING) {
            player.triggerAttack();
        }
    }
}
