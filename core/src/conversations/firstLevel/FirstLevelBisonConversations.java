package conversations.firstLevel;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.eightfold.screens.GameScreen;
import conversations.Conversation;
import objects.animals.bison.Bison;

public class FirstLevelBisonConversations extends Conversation {
    private final GameScreen gameScreen;
    private final Bison bison;

    public FirstLevelBisonConversations(GameScreen gameScreen, Bison bison, String filepath, String imagePath) {
        super(getConversationTexts(bison), filepath, imagePath);
        this.gameScreen = gameScreen;
        this.bison = bison;
    }

    private static String[] getConversationTexts(Bison bison) {
        //System.out.println(bison.getId());
        switch (bison.getId()) {
            case 0:
                return new String[]{"Howdy stranger, " + "\n" + "welcome to the ranch", "Nice day today, huh?", "Feel free to take a walk" + "\n" + "around, there are plenty of other bison to talk to." + "\n" + "Just stay out of The" + "\n" + "Saloon."};
            case 1:
                return new String[]{"Wassup", "I'm the evil one"};
            case 2:
                return new String[]{"Welcome to Bison Land", "Even the people are bison here"};
            case 3:
                return new String[]{"Stay out of the Saloon," + "\n" + "I've heard it's haunted", "I don't believe in that" + "\n" + "crap but I have heard" + "\n" + "strange noises emanating" + "\n" + "from within"};
            default:
                return new String[]{"Leave my ass alone."};
        }
    }

    @Override
    protected void showTextBox(String text) {
        gameScreen.showTextBox(text);
    }

    @Override
    protected void hideTextBox() {
        gameScreen.hideTextBox();
    }

    @Override
    protected void hideInfoBox() {
        gameScreen.hideInfoBox();
    }

    public boolean isConversationFinished() {
        return conversationIndex > conversationTexts.length;

    }


    public void resetConversation() {
        conversationIndex = 0;
        gameScreen.hideTextBox();
    }
}
