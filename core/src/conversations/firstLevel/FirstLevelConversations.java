package conversations.firstLevel;

import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.Conversation;
import objects.animals.bison.Bison;
import java.util.HashMap;
import java.util.Map;

public class FirstLevelConversations extends Conversation {
    private final Bison bison;
    private final Player player;
    private ScreenInterface screenInterface;
    private int bisonConversationIndex = 0;
    private int playerConversationIndex = 0;
    private int conversationPhase = 0;
    private boolean isTextBoxVisible = false;
    private boolean isBisonTurn = true;

    // Map to store the number of phases for each bison
    private static final Map<Integer, Integer> bisonPhases = new HashMap<>();

    static {
        // Initialize the number of phases for each bison
        bisonPhases.put(0, 2); // Bison with ID 0 has 2 phases
        bisonPhases.put(1, 2); // Bison with ID 1 has 2 phases
        bisonPhases.put(2, 1); // Bison with ID 2 has 1 phase
        bisonPhases.put(3, 1); // Bison with ID 3 has 1 phase
    }

    public FirstLevelConversations(ScreenInterface screenInterface, Bison bison, Player player, String filepath, String imagePath, int bisonConversationPhase) {
        super(null, null, filepath, imagePath); // Initialize with null, will be set below
        this.screenInterface = screenInterface;
        this.bison = bison;
        this.player = player;
        this.conversationPhase = bisonConversationPhase;
        this.bisonConversationTexts = getBisonConversationTexts();
        this.playerConversationTexts = getPlayerConversationTexts();
    }

    public void startBisonConversations(Bison bison) {
        if (!isTextBoxVisible) {
            hideInfoBox();
            showNextLine();
        }
    }

    public void showNextLine() {
        if (isBisonTurn) {
            if (bisonConversationIndex < bisonConversationTexts.length) {
                screenInterface.setTextBox("animals/bison/bison-single.png");
                screenInterface.showTextBox(bisonConversationTexts[bisonConversationIndex]);
                bisonConversationIndex++;
                //isTextBoxVisible = true;
            } else {
                isBisonTurn = false;
                playerConversationIndex = 0; // Reset player index for the new phase
                showNextLine(); // Show the next line for the player
            }
        } else {
            if (playerConversationIndex < playerConversationTexts.length) {
                screenInterface.setDecisionTextBox("player/player-single.png");
                screenInterface.showDecisionTextBox(playerConversationTexts[playerConversationIndex]);
                playerConversationIndex++;
                isTextBoxVisible = true;
            } else {
                checkNextPhase(conversationPhase);
            }
        }
    }

    private void checkNextPhase(int conversationPhase) {
        int totalPhases = bisonPhases.getOrDefault(bison.getId(), 1);
        if (conversationPhase < totalPhases - 1) {
            nextConversationPhase();
        } else {
            endConversation();
        }
    }

    private void nextConversationPhase() {
        incrementBisonConversationPhase();
        // Ensure indices are reset for the new phase
        bisonConversationIndex = 0;
        playerConversationIndex = 0;
        isBisonTurn = true;
        showNextLine();
    }

    private void endConversation() {
        screenInterface.hideTextBox();
        bison.setInConversation(false);
        bisonConversationIndex = 0;
        playerConversationIndex = 0;
        conversationPhase = 0;
        this.bisonConversationTexts = getBisonConversationTexts();
        this.playerConversationTexts = getPlayerConversationTexts();
        isTextBoxVisible = false;
        isBisonTurn = true; // Ensure bison starts the next conversation
    }

    private void incrementBisonConversationPhase() {
        conversationPhase++;
        this.bisonConversationTexts = null;
        this.playerConversationTexts = null;
        this.bisonConversationTexts = getBisonConversationTexts(); // Update the texts for the new phase
        this.playerConversationTexts = getPlayerConversationTexts(); // Update the texts for the new phase
    }

    private void setConversationPhase(int phase) {
        conversationPhase = phase;
    }

    private String[] getBisonConversationTexts() {
        switch (bison.getId()) {
            case 0:
                if (conversationPhase == 0) {
                    return new String[]{"Howdy stranger, welcome\nto the ranch", "Nice day today, huh?", "Feel free to take a walk\naround and talk to the other bison. Just stay out of The Saloon."};
                } else if (conversationPhase == 1) {
                    return new String[]{"Oh you'll get used to it", "We're good people around here", "The smell is just part of our charm."};
                }
            case 1:
                if (conversationPhase == 0) {
                    return new String[]{"Wassup", "I'm the evil one"};
                } else if (conversationPhase == 1) {
                    return new String[]{"Good luck with that."};
                }
            case 2:
                return new String[]{"Welcome to Bison Land", "Even the people are bison\nhere"};
            case 3:
                if (conversationPhase == 0) {
                    return new String[]{"Stay out of the Saloon," + "\n" + "I've heard it's haunted", "I don't believe in that" + "\n" + "crap but I have heard" + "\n" + "strange noises emanating" + "\n" + "from within"};
                }
            default:
                return new String[]{"Leave my ass alone."};
        }
    }

    private String[] getPlayerConversationTexts() {
        switch (bison.getId()) {
            case 0:
                if (conversationPhase == 0) {
                    return new String[]{"Nice, I'm Kath", "It's a pleasure to meet you", "You smell like fermented hell"};
                } else {
                    return new String[]{"Fair enough"};
                }
            case 1:

                return new String[]{"Cool.", "I'm Chaotic Good myself"};
            case 2:
                return new String[]{"Not me bro"};
            case 3:
                return new String[]{"That's great man, seems like\n a weird place"};
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
