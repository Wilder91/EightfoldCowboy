package conversations;

import objects.GameEntity;
import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.firstLevel.FirstLevelConversations;
import objects.humans.NPC;

public class ConversationManager {
    private FirstLevelConversations conversation;
    private final ScreenInterface screenInterface;
    private final int level;
    private final GameEntity firstGameEntity;
    private final GameEntity secondGameEntity;
    private final ConversationStateManager stateManager;

    // Conversation state
    private boolean isNPCTurn = true;
    private int npcLineIndex = 0;
    private int playerLineIndex = 0;

    public ConversationManager(int level, GameEntity firstGameEntity, GameEntity secondGameEntity, ScreenInterface screenInterface) {
        this.level = level;
        this.firstGameEntity = firstGameEntity;
        this.secondGameEntity = secondGameEntity;
        this.screenInterface = screenInterface;
        this.stateManager = ConversationStateManager.getInstance();
    }

    public void startFirstLevelConversation() {
        if (level == 1) {
            if (secondGameEntity instanceof Player && firstGameEntity instanceof NPC) {
                Player player = (Player) secondGameEntity;
                NPC npc = (NPC) firstGameEntity;

                System.out.println("Starting conversation with NPC ID: " + npc.getId());
                // Create the conversation object which will provide content
                conversation = new FirstLevelConversations(screenInterface, npc, player);

                // Reset conversation state
                isNPCTurn = true;
                npcLineIndex = 0;
                playerLineIndex = 0;

                // Show the first line
                showCurrentLine();
            }
        }
    }

    public void nextLine() {
        System.out.println("NEXT LINE CALLED"); // Debug log
        advanceConversation();
    }

    private void advanceConversation() {
        NPC npc = (NPC) firstGameEntity;
        int currentPhase = stateManager.getConversationPhase(npc.getId());

        System.out.println("Advance conversation - NPC Turn: " + isNPCTurn);

        if (isNPCTurn) {
            // NPC is currently speaking
            npcLineIndex++;

            // Check if NPC has more lines to say
            if (npcLineIndex < conversation.getNPCLines(currentPhase).length) {
                // Show the next NPC line
                showCurrentLine();
            } else {
                // NPC has no more lines, switch to player
                isNPCTurn = false;
                playerLineIndex = 0;

                // Check if player has any responses at all
                if (conversation.getPlayerLines(currentPhase).length == 0) {
                    // No player responses, go directly to next phase
                    checkNextPhase();
                } else {
                    showCurrentLine(); // Show first player line
                }
            }
        } else {
            // Player is currently speaking
            playerLineIndex++;

            // Check if player has more lines to say
            if (playerLineIndex < conversation.getPlayerLines(currentPhase).length) {
                // Show the next player line
                showCurrentLine();
            } else {
                // Player has no more lines, check if we should go to next phase
                checkNextPhase();
            }
        }
    }

    private void showCurrentLine() {
        NPC npc = (NPC) firstGameEntity;
        Player player = (Player) secondGameEntity;
        int currentPhase = stateManager.getConversationPhase(npc.getId());

        System.out.println("Showing: Phase=" + currentPhase +
                ", NPC Turn=" + isNPCTurn +
                ", NPC Index=" + npcLineIndex +
                ", Player Index=" + playerLineIndex);

        if (isNPCTurn) {
            String[] npcLines = conversation.getNPCLines(currentPhase);
            if (npcLineIndex < npcLines.length) {
                conversation.setPortrait(npc);
                String text = npcLines[npcLineIndex];
                System.out.println("Showing NPC text: " + text);
                screenInterface.hideTextBox();
                screenInterface.showTextBox(text);
            }
        } else {
            String[] playerLines = conversation.getPlayerLines(currentPhase);
            if (playerLineIndex < playerLines.length) {
                conversation.setPortrait(player);
                String text = playerLines[playerLineIndex];
                System.out.println("Showing player text: " + text);
                screenInterface.hideTextBox();
                screenInterface.showTextBox(text);
            }
        }
    }

    private void checkNextPhase() {
        NPC npc = (NPC) firstGameEntity;

        if (stateManager.hasMorePhases(npc.getId())) {
            nextConversationPhase();
        } else {
            endConversation();
        }
    }

    private void nextConversationPhase() {
        NPC npc = (NPC) firstGameEntity;
        int currentPhase = stateManager.getConversationPhase(npc.getId());

        System.out.println("Moving to next phase from " + currentPhase);

        // Advance the phase in the state manager
        stateManager.advanceConversationPhase(npc.getId());

        // Reset for next phase
        npcLineIndex = 0;
        playerLineIndex = 0;
        isNPCTurn = true;

        // Start the new phase
        showCurrentLine();
    }

    private void endConversation() {
        NPC npc = (NPC) firstGameEntity;

        System.out.println("Ending conversation");
        screenInterface.hideTextBox();
        screenInterface.hideDecisionTextBox();
        npc.setInConversation(false);

        // Reset the state in the central manager
        stateManager.resetConversationPhase(npc.getId());

        // Reset local variables
        npcLineIndex = 0;
        playerLineIndex = 0;
        isNPCTurn = true;

        System.out.println("Conversation reset: Phase=0" +
                ", NPC Index=" + npcLineIndex +
                ", Player Index=" + playerLineIndex);
    }
}