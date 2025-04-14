package com.mygdx.eightfold.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.Player;
import conversations.DialogueLine;
import objects.animals.Squirrel;
import objects.animals.birds.Bird;

import objects.animals.farm_animals.Chicken;
import objects.animals.bugs.Bug;
import objects.animals.bugs.Butterfly;
import objects.animals.bugs.Dragonfly;
import objects.humans.NPC;
import objects.inanimate.*;

public interface ScreenInterface {
    void showInfoBox(String message);

    void hideInfoBox();

    void setSaloonTime(boolean saloonTime);

    void setGameTime(boolean saloonTime);

    boolean isSaloonTime();

    void transitionToScreen(ScreenInterface newScreen); // Add this method

    void toggle();

    boolean isActive();

    World getWorld();

    void addDoor(Door door);



    void setPlayer(Player player);

    Player getPlayer();



    void addTree(Tree tree);

    void addBird(Bird bird);

    void addBoulder(Boulder boulder);

    void addBuilding(Building building);

    void hideTextBox();

    void showTextBox(DialogueLine line);

    OrthographicCamera getCamera();

    void setTextBox(String filepath);

    void setDecisionTextBox(String filepath);

    default void showDecisionTextBox(String text) {
        showDecisionTextbox(new DialogueLine(text, false));
    }


    void showDecisionTextbox(DialogueLine dialogueLine);

    void showPlayerTextBox(String playerConversationText);

    void addBush(Bush bush);

    void addRock(Rock rock);

    default void showTextBox(String text) {
        showTextBox(new DialogueLine(text, false));
    }

    void addChicken(Chicken chicken);

    void addPond(Pond pond);

    void addButterfly(Butterfly butterfly);

    void addBug(Bug bug);

    void addDragonfly(Dragonfly dragonfly);

    void addNPC(NPC npc);

    void getNPCById(int id);


    void hideDecisionTextBox();

    void setChoices(String... choices);

    void addSquirrel(Squirrel squirrel);

    void addFence(Fence fence);

    GameAssets getGameAssets();
}
