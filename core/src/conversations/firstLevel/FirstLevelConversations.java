package conversations.firstLevel;

import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.Conversation;
import objects.animals.bison.Bison;
import java.util.HashMap;
import java.util.Map;

/**
 * Handles multi-phase conversations between the player and various bison in the first level.
 */
public class FirstLevelConversations extends Conversation {
    private final Bison bison;
    private final Player player;
    private ScreenInterface screenInterface;

    private int bisonConversationIndex = 0;
    private int playerConversationIndex = 0;
    private int conversationPhase = 0;

    private boolean isTextBoxVisible = false;
    private boolean isBisonTurn = true;

    // Static map that defines how many conversation phases each bison has
    private static final Map<Integer, Integer> bisonPhases = new HashMap<>();

    static {
        bisonPhases.put(0, 2); // Ranch bison
        bisonPhases.put(1, 2); // "Evil" bison
        bisonPhases.put(2, 1); // Welcoming bison
        bisonPhases.put(3, 1); // Haunted saloon bison
    }

    public FirstLevelConversations(ScreenInterface screenInterface, Bison bison, Player player, String filepath, String imagePath, int bisonConversationPhase) {
        super(null, null, filepath, imagePath); // Superclass handles audio/image, null for now
        this.screenInterface = screenInterface;
        this.bison = bison;
        this.player = player;
        this.conversationPhase = bisonConversationPhase;

        // Initialize conversation text arrays for both bison and player
        this.bisonConversationTexts = getBisonConversationTexts();
        this.playerConversationTexts = getPlayerConversationTexts();
    }

    /**
     * Starts the conversation with a bison if no text box is currently visible.
     */
    public void startBisonConversations(Bison bison) {
        if (!isTextBoxVisible) {
            hideInfoBox(); // Clear any HUD/instruction info
            showNextLine(); // Begin conversation
        }
    }

    /**
     * Displays the next line in the conversation, alternating between bison and player.
     */
    public void showNextLine() {
        if (isBisonTurn) {
            if (bisonConversationIndex < bisonConversationTexts.length) {
                screenInterface.setTextBox("animals/bison/bison-single.png");
                screenInterface.showTextBox(bisonConversationTexts[bisonConversationIndex]);
                bisonConversationIndex++;
            } else {
                isBisonTurn = false;
                playerConversationIndex = 0; // Reset for player phase
                showNextLine(); // Immediately show player's first response
            }
        } else {
            if (playerConversationIndex < playerConversationTexts.length) {
                screenInterface.setTextBox("player/player-single.png");
                screenInterface.showTextBox(playerConversationTexts[playerConversationIndex]);
                playerConversationIndex++;
                isTextBoxVisible = true;
            } else {
                checkNextPhase(conversationPhase); // Move to next phase or end
            }
        }
    }

    /**
     * Determines whether the conversation should progress to the next phase or end entirely.
     */
    private void checkNextPhase(int conversationPhase) {
        int totalPhases = bisonPhases.getOrDefault(bison.getId(), 1);
        if (conversationPhase < totalPhases - 1) {
            nextConversationPhase();
        } else {
            endConversation();
        }
    }

    /**
     * Progresses the conversation to the next phase and resets indices.
     */
    private void nextConversationPhase() {
        incrementBisonConversationPhase();
        bisonConversationIndex = 0;
        playerConversationIndex = 0;
        isBisonTurn = !isBisonTurn; // Alternate who starts the next phase
        showNextLine();
    }

    /**
     * Resets everything when the conversation is complete.
     */
    private void endConversation() {
        screenInterface.hideTextBox();
        bison.setInConversation(false);
        bisonConversationIndex = 0;
        playerConversationIndex = 0;
        conversationPhase = 0;
        this.bisonConversationTexts = getBisonConversationTexts();
        this.playerConversationTexts = getPlayerConversationTexts();
        isTextBoxVisible = false;
        isBisonTurn = true; // Default to bison starting next time
    }

    /**
     * Increments the conversation phase and reloads the appropriate conversation text.
     */
    private void incrementBisonConversationPhase() {
        conversationPhase++;
        this.bisonConversationTexts = null;
        this.playerConversationTexts = null;
        this.bisonConversationTexts = getBisonConversationTexts();
        this.playerConversationTexts = getPlayerConversationTexts();
    }

    /**
     * Returns the lines the bison should say based on their ID and current phase.
     */
    private String[] getBisonConversationTexts() {
        switch (bison.getId()) {
            case 0:
                if (conversationPhase == 0) {
                    return new String[]{
                            "Howdy stranger, welcome\nto the ranch",
                            "Nice day today, huh?",
                            "Feel free to take a walk\naround and talk to the other bison. Just stay out of The Saloon."
                    };
                } else if (conversationPhase == 1) {
                    return new String[]{
                            "Oh you'll get used to it",
                            "We're good people around here",
                            "The smell is just part of our charm."
                    };
                }
                break;
            case 1:
                if (conversationPhase == 0) {
                    return new String[]{"Wassup", "I'm the evil one"};
                } else if (conversationPhase == 1) {
                    return new String[]{"Good luck with that."};
                }
                break;
            case 2:
                return new String[]{
                        "Welcome to Bison Land",
                        "Even the people are bison\nhere"
                };
            case 3:
                if (conversationPhase == 0) {
                    return new String[]{
                            "Stay out of the Saloon,\nI've heard it's haunted",
                            "I don't believe in that\ncrap but I have heard\nstrange noises emanating\nfrom within"
                    };
                }
                break;
        }
        return new String[]{"Leave my ass alone."}; // Default/fallback
    }

    /**
     * Returns the player's responses based on the bison's ID and current phase.
     */
    private String[] getPlayerConversationTexts() {
        switch (bison.getId()) {
            case 0:
                if (conversationPhase == 0) {
                    return new String[]{
                            "Nice, I'm Kath",
                            "It's a pleasure to meet you",
                            "You smell like fermented hell"
                    };
                } else {
                    return new String[]{"Fair enough"};
                }
            case 1:
                if (conversationPhase == 0) {
                    return new String[]{
                            "Cool.", "I'm Chaotic Good myself"
                    };
                } else {
                    return new String[]{}; // No reply in phase 1
                }
            case 2:
                return new String[]{"Not me bro"};
            case 3:
                return new String[]{"That's great man, seems like\n a weird place"};
            default:
                return new String[]{"Fuck off."}; // Fallback/default
        }
    }

    // These methods would be defined in the base Conversation class and are overridden here

    public void showTextBox(String text) {
        // Custom implementation to show a text box
    }

    @Override
    protected void hideTextBox() {
        // Custom implementation to hide the text box
    }

    @Override
    protected void hideInfoBox() {
        // Custom implementation to hide HUD or info overlay
    }
}
