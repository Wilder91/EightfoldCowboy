package conversations.firstLevel;

import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.Conversation;
import objects.animals.bison.Bison;

public class FirstLevelConversations extends Conversation {
    public FirstLevelConversations(ScreenInterface screenInterface, Bison bison, Player player, String filepath, String imagePath) {
        super(getConversationTexts(bison, player), filepath, imagePath);
    }

    public void printHello(){
        System.out.println("hello");
    }

    private static String[] getConversationTexts(Bison bison, Player player) {
        //System.out.println(bison.getId());
        switch (bison.getId()) {
            case 0:
                return new String[]{"Howdy stranger, welcome\nto the ranch", "Nice day today, huh?", "Feel free to take a walk\naround and talk to the other bison. Just stay out of The" + "\n" + "Saloon."};
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
    public void showTextBox(String text) {

    }

    @Override
    protected void hideTextBox() {

    }

    @Override
    protected void hideInfoBox() {

    }
}
