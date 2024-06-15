package conversations.firstLevel;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.eightfold.screens.GameScreen;
import objects.animals.bison.Bison;

public class FirstLevelBisonConversations {
    private final GameScreen gameScreen;
    private int id;
    private String[] conversationTexts;
    private int conversationIndex;

    public FirstLevelBisonConversations(GameScreen gameScreen, Bison bison) {
        this.gameScreen = gameScreen;
        this.conversationTexts = new String[]{"Hello Kath", "How are you?", "Nice weather today"};
        this.conversationIndex = 0; // Start with the first conversation text
    }

    public void nextLine(){
        conversationIndex  += 1;
    }

    public void startConversations(int id) {
        gameScreen.hideInfoBox();
        System.out.println(id);

        // Display current conversation text based on conversationIndex
        if (conversationIndex < conversationTexts.length) {
            gameScreen.showTextBox(conversationTexts[conversationIndex]);
        } else {
            gameScreen.hideTextBox();
        }

        // Increment conversation index for next conversation

    }

    public void endConversations() {
        gameScreen.hideTextBox();
    }
}
