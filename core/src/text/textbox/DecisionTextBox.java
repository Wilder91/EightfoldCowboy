package text.textbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import java.util.function.Consumer;

public class DecisionTextBox extends TextBox {
    private Consumer<String> onChoiceSelected;
    private TextButton[] choiceButtons;
    private int selectedChoiceIndex = 0;
    private InputAdapter keyboardInputAdapter;
    private InputMultiplexer inputMultiplexer;

    public DecisionTextBox(Skin skin, String imagePath) {
        super(skin, imagePath);

        // Set up keyboard input handling
        setupKeyboardInput();
    }

    /**
     * Sets up keyboard input handling for arrow key navigation
     */
    private void setupKeyboardInput() {
        keyboardInputAdapter = new InputAdapter() {
            @Override
            public boolean keyDown(int keycode) {
                if (!dialog.isVisible() || choiceButtons == null || choiceButtons.length == 0) {
                    return false;
                }

                if (keycode == Input.Keys.UP || keycode == Input.Keys.LEFT) {
                    // Move selection up or left
                    updateSelectedChoice((selectedChoiceIndex - 1 + choiceButtons.length) % choiceButtons.length);
                    return true;
                } else if (keycode == Input.Keys.DOWN || keycode == Input.Keys.RIGHT) {
                    // Move selection down or right
                    updateSelectedChoice((selectedChoiceIndex + 1) % choiceButtons.length);
                    return true;
                } else if (keycode == Input.Keys.ENTER || keycode == Input.Keys.SPACE) {
                    // Select the current choice
                    if (selectedChoiceIndex >= 0 && selectedChoiceIndex < choiceButtons.length) {
                        selectChoice(choiceButtons[selectedChoiceIndex].getText().toString());
                        return true;
                    }
                }
                return false;
            }
        };

        // Create an InputMultiplexer to handle both stage input and our keyboard controls
        inputMultiplexer = new InputMultiplexer(stage, keyboardInputAdapter);
    }

    /**
     * Updates the visual indication of the selected choice
     */
    private void updateSelectedChoice(int newIndex) {
        selectedChoiceIndex = newIndex;

        // Update the visual state of all buttons
        for (int i = 0; i < choiceButtons.length; i++) {
            TextButton button = choiceButtons[i];

            // Create a new style for each button
            TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(button.getStyle());

            if (i == selectedChoiceIndex) {
                // Highlighted style for selected button
                style.up = createBackgroundWithBorder(
                        90f / 255f, 120f / 255f, 94f / 255f, 1f,
                        160f / 255f, 188f / 255f, 114f / 255f, 1f);
                style.fontColor = Color.YELLOW;
            } else {
                // Normal style for unselected buttons
                style.up = createBackgroundWithBorder(
                        80f / 255f, 110f / 255f, 84f / 255f, .9f,
                        140f / 255f, 168f / 255f, 94f / 255f, .9f);
                style.fontColor = Color.WHITE;
            }

            button.setStyle(style);
        }
    }

    /**
     * Select a choice and trigger the callback
     */
    private void selectChoice(String choice) {
        if (onChoiceSelected != null) {
            onChoiceSelected.accept(choice);
        }
        hideTextBox();
    }

    /**
     * Shows decision textbox with text and choices
     * @param text The message to display
     * @param choices The available choices
     */

    public void showDecisionTextBox(String... choices) {
        // First show the text
        textLabel.setText(choices[1]);
        dialog.setVisible(true);

        // Create the choice buttons
        createChoiceButtons(choices);

        // Set input processor to our multiplexer when dialog is shown
        Gdx.input.setInputProcessor(inputMultiplexer);

        // Default to the first choice being selected
        if (choiceButtons != null && choiceButtons.length > 0) {
            updateSelectedChoice(0);
        }
    }

    /**
     * Creates and positions choice buttons in a horizontal grid beneath the dialog
     */
    private void createChoiceButtons(String... choices) {
        // Clear any existing buttons
        if (choiceButtons != null) {
            for (TextButton button : choiceButtons) {
                if (button != null) {
                    button.remove();
                }
            }
        }

        choiceButtons = new TextButton[choices.length];
        selectedChoiceIndex = 0;

        // Create a button style
        TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
        buttonStyle.font = scaleFont(skin.getFont("commodore-64"), 0.5f);
        buttonStyle.fontColor = Color.WHITE;
        buttonStyle.up = createBackgroundWithBorder(
                80f / 255f, 110f / 255f, 84f / 255f, .9f,
                140f / 255f, 168f / 255f, 94f / 255f, .9f);
        buttonStyle.over = createBackgroundWithBorder(
                90f / 255f, 120f / 255f, 94f / 255f, 1f,
                160f / 255f, 188f / 255f, 114f / 255f, 1f);
        buttonStyle.down = createBackgroundWithBorder(
                70f / 255f, 100f / 255f, 74f / 255f, 1f,
                120f / 255f, 148f / 255f, 84f / 255f, 1f);

        // Calculate total width and individual button width
        float dialogWidth = dialog.getWidth();
        float buttonSpacing = 10f;
        float totalButtonWidth = dialogWidth - (buttonSpacing * (choices.length + 1));
        float buttonWidth = totalButtonWidth / choices.length;

        // Calculate Y position (below the dialog)
        float buttonY = dialog.getY() - 50f;

        // Special case for single button - center it
        if (choices.length == 1) {
            String choiceText = choices[0];
            TextButton button = new TextButton(choiceText, buttonStyle);
            button.getLabel().setWrap(true);
            button.getLabel().setAlignment(Align.center);

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    selectChoice(choiceText);
                }
            });

            button.setSize(dialogWidth * 0.5f, 40f);
            button.setPosition(dialog.getX() + (dialogWidth - button.getWidth()) / 2, buttonY);

            choiceButtons[0] = button;
            stage.addActor(button);
            return;
        }

        // Calculate initial X position
        float buttonX = dialog.getX() + buttonSpacing;

        // Create buttons for 2-3 choices
        for (int i = 0; i < choices.length; i++) {
            final String choiceText = choices[i];
            final int choiceIndex = i;

            TextButton button = new TextButton(choiceText, buttonStyle);
            button.getLabel().setWrap(true);
            button.getLabel().setAlignment(Align.center);

            button.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    updateSelectedChoice(choiceIndex);
                    selectChoice(choiceText);
                }

                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    // Highlight on mouse hover
                    if (pointer == -1) { // Only trigger for mouse movements, not touch
                        updateSelectedChoice(choiceIndex);
                    }
                }
            });

            button.setSize(buttonWidth, 40f);
            button.setPosition(buttonX, buttonY);

            choiceButtons[i] = button;
            stage.addActor(button);

            // Update X position for next button
            buttonX += buttonWidth + buttonSpacing;
        }
    }


    public void showDecisionsFromArray(String[] choices) {
        showDecisionTextBox(choices);
    }

    /**
     * Optional: Allow external handler to be set for custom choice logic.
     */
    public void setOnChoiceSelected(Consumer<String> handler) {
        this.onChoiceSelected = handler;
    }

    @Override
    public void setFontColor(float r, float g, float b, float a) {
        textLabel.setColor(r, g, b, a);
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        // Update choice buttons if they exist
        if (choiceButtons != null && choiceButtons.length > 0) {
            // Recalculate button positions after dialog resize
            float dialogWidth = dialog.getWidth();
            float buttonSpacing = 10f;
            float totalButtonWidth = dialogWidth - (buttonSpacing * (choiceButtons.length + 1));
            float buttonWidth = totalButtonWidth / choiceButtons.length;

            float buttonY = dialog.getY() - 50f;

            // Special case for single button
            if (choiceButtons.length == 1) {
                TextButton button = choiceButtons[0];
                button.setSize(dialogWidth * 0.5f, 40f);
                button.setPosition(dialog.getX() + (dialogWidth - button.getWidth()) / 2, buttonY);

                // Update font scale
                float scale = height / 720f;
                TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(button.getStyle());
                style.font = scaleFont(skin.getFont("commodore-64"), scale * 0.5f);
                button.setStyle(style);
                return;
            }

            // Reposition multiple buttons
            float buttonX = dialog.getX() + buttonSpacing;
            float scale = height / 720f;

            for (int i = 0; i < choiceButtons.length; i++) {
                TextButton button = choiceButtons[i];
                if (button != null) {
                    button.setSize(buttonWidth, 40f);
                    button.setPosition(buttonX, buttonY);

                    // Update font scale
                    TextButton.TextButtonStyle style = new TextButton.TextButtonStyle(button.getStyle());
                    style.font = scaleFont(skin.getFont("commodore-64"), scale * 0.5f);
                    button.setStyle(style);

                    // Update X position for next button
                    buttonX += buttonWidth + buttonSpacing;
                }
            }
        }
    }

    @Override
    public void hideTextBox() {
        super.hideTextBox();

        // Also hide/remove all choice buttons
        if (choiceButtons != null) {
            for (TextButton button : choiceButtons) {
                if (button != null) {
                    button.remove();
                }
            }
            choiceButtons = null;
        }

        // Reset to the default input processor when dialog is hidden
        Gdx.input.setInputProcessor(stage);
    }
}