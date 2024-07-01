package conversations.firstLevel;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.Conversation;
import objects.animals.bison.Bison;

public class FirstLevelBisonConversations extends Conversation {
    private final ScreenInterface screenInterface;
    private final Bison bison;

    public FirstLevelBisonConversations(ScreenInterface screenInterface, Bison bison, String filepath, String imagePath) {
        super(getConversationTexts(bison), filepath, imagePath);
        this.screenInterface = screenInterface;
        this.bison = bison;
    }

    private static String[] getConversationTexts(Bison bison) {
        //System.out.println(bison.getId());
        switch (bison.getId()) {
            case 0:
                return new String[]{"Howdy stranger, " + "\n" + "welcome to the ranch", "Nice day today, huh?", "Feel free to take a walk" + "\n" + "around and talk to the other bison. Just stay out of The" + "\n" + "Saloon."};
            case 1:
                return new String[]{"Wassup", "I'm the evil one"};
            case 2:
                return new String[]{"Welcome to Bison Land", "Even the people are bison\nhere"};
            case 3:
                return new String[]{"Stay out of the Saloon," + "\n" + "I've heard it's haunted", "I don't believe in that" + "\n" + "crap but I have heard" + "\n" + "strange noises emanating" + "\n" + "from within"};
            default:
                return new String[]{"Leave my ass alone."};
        }
    }

    @Override
    protected void showTextBox(String text) {
        screenInterface.showTextBox(text);
    }

    @Override
    protected void hideTextBox() {
        screenInterface.hideTextBox();
    }

    @Override
    protected void hideInfoBox() {
        screenInterface.hideInfoBox();
    }

    public boolean isConversationFinished() {
        return conversationIndex > conversationTexts.length;

    }


    public void resetConversation() {
        conversationIndex = 0;
        screenInterface.hideTextBox();
    }
}
