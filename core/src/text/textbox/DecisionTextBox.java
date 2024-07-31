package text.textbox;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

public class DecisionTextBox extends TextBox {
    private Table buttonTable;
    private BitmapFont buttonFont;

    public DecisionTextBox(Skin skin, String imagePath) {
        super(skin, imagePath);
        this.buttonTable = new Table();
        buttonFont = skin.getFont("commodore-64");
        dialog.getContentTable().row();
        dialog.getContentTable().add(buttonTable).expandX().fillX().padTop(10);
        System.out.println("DecisionTextBox initialized, buttonTable: " + (buttonTable != null));
    }

    public void addChoiceButtons(String[] choices, ChangeListener[] listeners) {
        buttonTable.clear();

        if (choices == null || listeners == null || choices.length != listeners.length) {
            System.err.println("Error: Invalid choices or listeners provided.");
            return;
        }

        for (int i = 0; i < choices.length; i++) {
            TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.font = buttonFont;
            buttonStyle.fontColor = textLabel.getStyle().fontColor;
            TextButton choiceButton = new TextButton(choices[i], buttonStyle);
            choiceButton.addListener(listeners[i]);
            buttonTable.add(choiceButton).expandX().fillX().pad(5);
            buttonTable.row();
        }
    }

    @Override
    public void setFontColor(float r, float g, float b, float a) {
        Color newColor = new Color(r, g, b, a);
        textLabel.getStyle().fontColor = newColor;

        for (Cell<?> cell : buttonTable.getCells()) {
            if (cell.getActor() instanceof TextButton) {
                TextButton button = (TextButton) cell.getActor();
                button.getLabel().setColor(newColor);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        super.resize(width, height);

        System.out.println("Resizing, buttonTable: " + (buttonTable != null));

        if (buttonTable == null) {
            System.err.println("Error: buttonTable is null in resize method.");
            return;
        }

        float scale = height / 720f;
        for (Cell<?> cell : buttonTable.getCells()) {
            if (cell.getActor() instanceof TextButton) {
                TextButton button = (TextButton) cell.getActor();
                button.getLabel().setStyle(new Label.LabelStyle(scaleFont(buttonFont, scale), button.getLabel().getStyle().fontColor));
            }
        }
    }
}
