package text.textbox;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;

import java.util.function.Consumer;

public class DecisionTextBox extends TextBox {
    private final Table choicesTable;
    private Consumer<String> onChoiceSelected;

    public DecisionTextBox(Skin skin, String imagePath) {
        super(skin, imagePath);

        choicesTable = new Table();
        choicesTable.defaults().pad(5).uniform();

        dialog.getContentTable().row();
        dialog.getContentTable().add(choicesTable).expandX().left().padTop(10);
        resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    /**
     * Sets up 1–3+ dialog choices. Wraps to new row every 3 buttons.
     */
    public void setChoices(String... choices) {
        choicesTable.clear();

        for (int i = 0; i < choices.length; i++) {
            final String choiceText = choices[i];
            TextButton choiceButton = new TextButton(choiceText, skin);

            choiceButton.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    if (onChoiceSelected != null) onChoiceSelected.accept(choiceText);
                    onChoiceSelected(choiceText);
                }
            });

            choicesTable.add(choiceButton).expandX();

            if ((i + 1) % 3 == 0) {
                choicesTable.row();
            }
        }
    }

    public void hideTextBox() {
        dialog.setVisible(false);
    }

    /**
     * Override this to handle what happens when a choice is clicked.
     */
    protected void onChoiceSelected(String choice) {
        System.out.println("Selected: " + choice);
        hideTextBox();
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
}
