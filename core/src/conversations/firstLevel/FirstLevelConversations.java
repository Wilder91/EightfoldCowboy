package conversations.firstLevel;

import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.Conversation;
import objects.animals.bison.Bison;
import text.textbox.TextBox;

public class FirstLevelConversations extends Conversation {
    private final Bison bison;
    private final Player player;
    private ScreenInterface screenInterface;
    private int bisonConversationIndex = 0;
    private int playerConversationIndex = 0;
    private boolean isTextBoxVisible = false;

    public FirstLevelConversations(ScreenInterface screenInterface, Bison bison, Player player, String filepath, String imagePath) {
        super(getBisonConversationTexts(bison), getPlayerConversationTexts(bison), filepath, imagePath);
        this.bisonConversationTexts = getBisonConversationTexts(bison);
        this.playerConversationTexts = getPlayerConversationTexts(bison);
        this.screenInterface = screenInterface;
        this.bison = bison;
        this.player = player;
    }

    public String returnHello() {
        return "hello";
    }

    public void startBisonConversations(Bison bison) {
        if (!isTextBoxVisible) {
            hideInfoBox();
            showNextBisonLine();
        }
    }

    public void showNextBisonLine() {
        if (bisonConversationIndex < bisonConversationTexts.length) {
            screenInterface.setTextBox("animals/bison/bison-single.png");
            screenInterface.showTextBox(bisonConversationTexts[bisonConversationIndex]);
            bisonConversationIndex++;
            isTextBoxVisible = true;
        } else if (playerConversationIndex < playerConversationTexts.length) {
            screenInterface.setTextBox("player/player-single.png");
            screenInterface.showTextBox(playerConversationTexts[playerConversationIndex]);
            playerConversationIndex++;
            isTextBoxVisible = true;
        } else {
            endBisonConversation();
        }
    }

    private void endBisonConversation() {
        screenInterface.hideTextBox();
        bisonConversationIndex = 0;
        playerConversationIndex = 0; // Reset player conversation index as well
        isTextBoxVisible = false;
    }

    private static String[] getBisonConversationTexts(Bison bison) {
        switch (bison.getId()) {
            case 0:
                return new String[]{"Howdy stranger, welcome\nto the ranch", "Nice day today, huh?", "Feel free to take a walk\naround and talk to the other bison. Just stay out of The Saloon."};
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

    private static String[] getPlayerConversationTexts(Bison bison) {
        System.out.println("PLAYER TALKING");
        switch (bison.getId()) {
            case 0:
                return new String[]{"That's great man, seems like\n a weird place"};
            case 1:
                return new String[]{"Cool.", "I'm Chaotic Good myself"};
            case 2:
                return new String[]{"Not me bro"};
            case 3:
                return new String[]{"Hey there Bison, I'm Kath", "It's a pleasure to meet you", "You smell like fermented hell"};
            default:
                return new String[]{"Fuck off."};
        }
    }

    @Override
    public void showTextBox(String text) {
        // Implementation to show text box with the given text
    }

    @Override
    protected void hideTextBox() {
        // Implementation to hide the text box
    }

    @Override
    protected void hideInfoBox() {
        // Implementation to hide the info box
    }
}
