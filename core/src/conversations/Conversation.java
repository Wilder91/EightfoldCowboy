package conversations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import objects.animals.bison.Bison;
import text.textbox.BisonTextBox;

public abstract class Conversation {
    protected int conversationIndex;
    protected String[] conversationTexts;
    protected boolean isTextBoxVisible;
    private BisonTextBox textBox;
    private Skin skin;

    public Conversation(String[] conversationTexts, String filepath, String imagePath) {
        this.conversationIndex = 0;
        this.conversationTexts = conversationTexts;
        this.isTextBoxVisible = false;
        this.skin  = new Skin(Gdx.files.internal(filepath));
        this.textBox = new BisonTextBox(skin, imagePath);
    }

    public void bisonNextLine(Bison bison) {
        if (conversationIndex < conversationTexts.length) {
            showTextBox(conversationTexts[conversationIndex]);
            conversationIndex++;
            isTextBoxVisible = true;
        } else {
            hideTextBox();
            bison.setInConversation(false);
            conversationIndex = 0;
            isTextBoxVisible = false;
        }
    }

    public void startBisonConversations(Bison bison) {
        if (!isTextBoxVisible) {
            hideInfoBox();
            bisonNextLine(bison);
        }
    }

    public void endConversations() {
        hideTextBox();
        //isTextBoxVisible = false;
    }

    protected abstract void showTextBox(String text);
    protected abstract void hideTextBox();
    protected abstract void hideInfoBox();
}
