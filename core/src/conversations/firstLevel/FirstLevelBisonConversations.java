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


        if (bison.getId() == 0) {
            this.conversationTexts = new String[]{"Hey there Cowboy!", "How are you?", "Nice weather today"};
             // Start with the first conversation text
        }else{
            this.conversationTexts = new String[]{"Welcome to Bison Land", "We are bison here", "Even the people are bison here"};
        }
        this.conversationIndex = 0;
    }

    public void nextLine(){

        conversationIndex  += 1;
        startConversations(id);
    }

    public void startConversations(int id) {
        gameScreen.hideInfoBox();
        System.out.println(id);

        // Display current conversation text based on conversationIndex
        if (conversationIndex < conversationTexts.length ) {
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
}
