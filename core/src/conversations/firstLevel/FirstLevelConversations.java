package conversations.firstLevel;

import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.Conversation;
import objects.humans.NPC;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles multi-phase conversations between the player and various NPCs in the first level.
 */
public class FirstLevelConversations extends Conversation {
    private final NPC npc;
    private final Player player;
    private final ScreenInterface screenInterface;

    private int npcConversationIndex = 0;
    private int playerConversationIndex = 0;
    private int conversationPhase = 0;

    private boolean isTextBoxVisible = false;
    private boolean isDecisionTextboxVisible = false;
    private boolean isNPCTurn = true;

    // Static map that defines how many conversation phases each NPC has
    private static final Map<Integer, Integer> npcPhases = new HashMap<>();

    static {
        npcPhases.put(0, 2); // Jim
        npcPhases.put(1, 2); // Martha
        npcPhases.put(2, 1); // Miner
        npcPhases.put(3, 1); // Cowboy
    }

    public FirstLevelConversations(ScreenInterface screenInterface, NPC npc, Player player, String filepath, String imagePath, int npcConversationPhase) {
        super(null, null, filepath, imagePath); // Superclass handles audio/image, null for now
        this.screenInterface = screenInterface;
        this.npc = npc;
        this.player = player;
        this.conversationPhase = npcConversationPhase;
        this.npcConversationTexts = getNPCConversationTexts();
        this.playerConversationTexts = getPlayerConversationTexts();
    }

    /**
     * Starts the conversation with an NPC if no text box is currently visible.
     */
    public void startNPCConversation(NPC npc) {
        hideInfoBox();

        if (!isTextBoxVisible) {
            //hideInfoBox();
            showNextLine();
        }
        if (!isDecisionTextboxVisible) {
            hideTextBox();
        }
    }

    /**
     * Displays the next line in the conversation, alternating between NPC and player.
     */
    public void showNextLine() {
        hideInfoBox();
        if (isNPCTurn) {
            //hideInfoBox();
            if (npcConversationIndex < npcConversationTexts.length) {
                if(npc.getId() == 1) {
                    screenInterface.setTextBox("Jim_Idle_Down_1 copy.png");
                } else if (npc.getId() == 2){
                    screenInterface.setTextBox("Martha_Idle_Down_1 copy.png");
                }
                screenInterface.showTextBox(npcConversationTexts[npcConversationIndex]);
                npcConversationIndex++;
            } else {
                // Flip to player's turn AFTER NPC is done
                isNPCTurn = false;
                showNextLine(); // Immediately call again to start player's dialogue
            }
        } else {
            if (playerConversationIndex < playerConversationTexts.length) {
                screenInterface.setTextBox("Character_Idle_Down_1 copy.png");
                screenInterface.showTextBox(playerConversationTexts[playerConversationIndex]);
                playerConversationIndex++;
                isTextBoxVisible = true;
            } else {
                checkNextPhase(conversationPhase);
            }
        }
    }

    /**
     * Determines whether the conversation should progress to the next phase or end entirely.
     */
    private void checkNextPhase(int conversationPhase) {
        int totalPhases = npcPhases.getOrDefault(npc.getId(), 1);
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
        incrementNPCConversationPhase();
        npcConversationIndex = 0;
        playerConversationIndex = 0;
        isNPCTurn = !isNPCTurn;
        showNextLine();
    }

    /**
     * Resets everything when the conversation is complete.
     */
    private void endConversation() {
        screenInterface.hideTextBox();
        npc.setInConversation(false);
        npcConversationIndex = 0;
        playerConversationIndex = 0;
        conversationPhase = 0;
        this.npcConversationTexts = getNPCConversationTexts();
        this.playerConversationTexts = getPlayerConversationTexts();
        isTextBoxVisible = false;
        isNPCTurn = true;
    }

    /**
     * Increments the conversation phase and reloads the appropriate conversation text.
     */
    private void incrementNPCConversationPhase() {
        conversationPhase++;
        this.npcConversationTexts = null;
        this.playerConversationTexts = null;
        this.npcConversationTexts = getNPCConversationTexts();
        this.playerConversationTexts = getPlayerConversationTexts();
    }

    /**
     * Returns the lines the NPC should say based on their ID and current phase.
     */
    private String[] getNPCConversationTexts() {
        switch (npc.getId()) {
            case 1:
                if (conversationPhase == 0) {
                    return new String[]{
                            "Hey there, I'm Old Jim.",
                            "Nice day today, huh?",
                            "Feel free to walk around and chat with the others." + System.lineSeparator() + "Just stay out of The Saloon."
                    };

                } else if (conversationPhase == 1) {
                    return new String[]{
                            "It gets awful lonely out here",
                            "Just me and Martha",
                            "A man starts to wonder what" + System.lineSeparator() + "it's all for"
                    };
                }
                break;
            case 2:
                if (conversationPhase == 0) {
                    return new String[]{
                            "Oh hello sweetheart, you can call me Martha",
                            "That's my name, Martha",
                            "The smell is just part of our charm."
                    };
                } else if (conversationPhase == 1) {
                    return new String[]{
                            "Good luck with that."
                    };
                }
                break;
            case 3:
                return new String[]{
                        "Welcome to town.",
                        "Even the dogs talk here."
                };

            case 4:
                if (conversationPhase == 0) {
                    return new String[]{
                            "Stay out of the Saloon, it's bad news.",
                            "I don't believe in ghosts, but I've heard some weird stuff coming from there."
                    };
                }
                break;
        }
        return new String[]{"Not now, I'm busy."}; // Fallback/default
    }

    /**
     * Returns the player's responses based on the NPC's ID and current phase.
     */
    private String[] getPlayerConversationTexts() {
        switch (npc.getId()) {
            case 1:
                if (conversationPhase == 0) {
                    return new String[]{
                            "Nice, I'm Kath.",
                            "It's a pleasure to meet you.",
                            "It's beautiful here."
                    };
                } else {
                    return new String[]{"Well, it's a pleasure to meet you"};
                }
            case 2:
                if (conversationPhase == 0) {
                    return new String[]{
                            "It's a pleasure, Martha", "My name is Kath"
                    };
                } else {
                    return new String[]{}; // No reply
                }
            case 3:
                return new String[]{"Not me bro."};
            case 4:
                return new String[]{"That's great man, seems like a weird place."};
            default:
                return new String[]{"Okay then."}; // Fallback/default
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
