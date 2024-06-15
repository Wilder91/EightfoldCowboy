package conversations.firstLevel;

import com.mygdx.eightfold.screens.GameScreen;

public abstract class Conversation {
    protected final GameScreen gameScreen;
    protected int id;
    protected String[] conversationTexts;
    protected int conversationIndex;

    public Conversation(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.conversationIndex = 0;
    }

    public void nextLine() {
        System.out.println("Next line");
        conversationIndex += 1;
        startConversations(id);
    }

    public void startConversations(int id) {
        gameScreen.hideInfoBox();
        System.out.println(id);

        // Display current conversation text based on conversationIndex
        if (conversationIndex < conversationTexts.length) {
            gameScreen.showTextBox(conversationTexts[conversationIndex]);
        } else {
            gameScreen.hideTextBox();
            conversationIndex = 0;
        }

        // Increment conversation index for next conversation
    }

    public void endConversations() {
        gameScreen.hideTextBox();
    }

    protected abstract void initializeConversationTexts();
}
