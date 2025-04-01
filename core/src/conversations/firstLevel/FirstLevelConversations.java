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

    private int npcConversationIndex;
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
    public void startNPCConversation() {
        npcConversationIndex = 0;
        playerConversationIndex = 0;
        isNPCTurn = true;
        isTextBoxVisible = false;
        showCurrentLine(); // Just show the first line
    }

    public void showCurrentLine() {
        hideInfoBox();
        if (isNPCTurn && npcConversationIndex < npcConversationTexts.length) {
            setPortrait();
            screenInterface.showTextBox(npcConversationTexts[npcConversationIndex]);
        } else if (!isNPCTurn && playerConversationIndex < playerConversationTexts.length) {
            screenInterface.setTextBox("Character_Idle_Down_1 copy.png");
            screenInterface.showTextBox(playerConversationTexts[playerConversationIndex]);
        }
    }

    private void setPortrait() {
        if (npc.getId() == 1) {
            screenInterface.setTextBox("Jim_Idle_Down_1 copy.png");
        } else if (npc.getId() == 2) {
            screenInterface.setTextBox("Martha_Idle_Down_1 copy.png");
        }
    }




    public void advanceConversation() {
        if (isNPCTurn) {
            npcConversationIndex++;
            if (npcConversationIndex >= npcConversationTexts.length) {
                // Finished NPC lines, now switch to player
                isNPCTurn = false;
                playerConversationIndex = 0; // Just in case
            }
        } else {
            playerConversationIndex++;

            if (playerConversationIndex >= playerConversationTexts.length) {
                checkNextPhase(conversationPhase); // End or move to next phase
                return;
            }
        }

        showCurrentLine(); // Only call if there are lines to show
    }



    /**
     * Determines whether the conversation should progress to the next phase or end entirely.
     */
    private void checkNextPhase(int conversationPhase) {
        int totalPhases = npcPhases.getOrDefault(npc.getId(), 0);
        if (conversationPhase < totalPhases ) {
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
        isNPCTurn = true; // Always start new phase with NPC
        showCurrentLine(); // ✅ Show first NPC line without advancing
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
        //npc.advanceConversationPhase();
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
        System.out.println("conversation phase: " + conversationPhase);
        System.out.println("get npc texts index: " + npcConversationIndex);
        switch (npc.getId()) {
            case 1:
                if (conversationPhase == 0) {
                    return new String[]{
                            "Hey there, I'm Old Jim."
                    };

                } else if (conversationPhase == 1 ){
                    npcConversationIndex = 0;
                    return new String[]{
                            "Nice day today, huh?",
                            "Feel free to walk around and explore the forest." + System.lineSeparator() + "I'll be here watching" + System.lineSeparator() + "the pond."
                    };
                }else if (conversationPhase == 2) {
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
                            "Oh hello sweetheart, you can call me Martha"
                    };
                } else if (conversationPhase == 1) {
                    return new String[]{
                            "How precious"
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
        System.out.println("get player texts index: " + playerConversationIndex);
        switch (npc.getId()) {
            case 1:
                if (conversationPhase == 0) {
                    return new String[]{
                            "Nice, I'm Kath.",
                            "It's a pleasure to meet you.",
                            "It's beautiful here."
                    };
                } else if(conversationPhase ==1) {
                    return new String[]{"Thank you Jim, I look forward to exploring"};
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
