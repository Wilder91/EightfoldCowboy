package conversations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import objects.humans.NPC;
import text.textbox.BisonTextBox;

public abstract class Conversation {
    protected int conversationIndex;
    protected String[] bisonConversationTexts;
    protected String[] npcConversationTexts;
    protected String[] playerConversationTexts;
    protected boolean isTextBoxVisible;
    private BisonTextBox textBox;
    private Skin skin;

    public Conversation(String[] firstConversationTexts, String[] secondConversationTexts, String filepath, String imagePath) {
        this.conversationIndex = 0;
        this.npcConversationTexts = firstConversationTexts;
        this.playerConversationTexts = secondConversationTexts;
        this.isTextBoxVisible = false;
        this.skin  = new Skin(Gdx.files.internal(filepath));
        //this.textBox = new BisonTextBox(skin, imagePath);
    }


    private void npcNextLine(NPC npc) {

            if (conversationIndex < npcConversationTexts.length) {
                showTextBox(npcConversationTexts[conversationIndex]);
                conversationIndex++;
                isTextBoxVisible = true;
            } else {
                hideTextBox();
                npc.setInConversation(false);
                conversationIndex = 0;
                isTextBoxVisible = false;

            }
    }


    public void startNPCConversations(NPC npc) {
        if(!isTextBoxVisible){
            hideInfoBox();
            npcNextLine(npc);
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