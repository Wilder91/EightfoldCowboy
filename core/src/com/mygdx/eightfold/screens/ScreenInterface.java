package com.mygdx.eightfold.screens;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.eightfold.player.Player;
import objects.animals.bird.Bird;
import objects.animals.bison.Bison;
import objects.inanimate.*;
import text.textbox.TextBox;

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

    void addLowerRock(Rock rock);

    void addUpperRock(Rock rock);

    void setPlayer(Player player);
    Player getPlayer();
    void addBison(Bison bison);
    void addTree(Tree tree);

    void addBird(Bird bird);

    void addBoulder(Boulder boulder);



    void addBuilding(Building building);
    void hideTextBox();
    void showTextBox(String text);

    OrthographicCamera getCamera();

    void setTextBox(String filepath);

    void setDecisionTextBox(String filepath);

    void showDecisionTextBox(String text);

    void showPlayerTextBox(String playerConversationText);

    void addBush(Bush bush);

    void addRock(Rock rock);
}
