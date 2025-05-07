package com.mygdx.eightfold.commands;

import objects.GameEntity;

public class ToggleSensorCommand implements Command {
    private GameEntity gameEntity;

    public ToggleSensorCommand(GameEntity gameEntity) {
        this.gameEntity = gameEntity;
    }

    @Override
    public void execute() {
        gameEntity.toggleSensor();
    }
}
