package com.mygdx.eightfold.screens;

public interface ScreenInterface {
    void showInfoBox(String message);
    void hideInfoBox();
    void setSaloonTime(boolean saloonTime);
    boolean isSaloonTime();

    void toggle();

    boolean isActive();
}

