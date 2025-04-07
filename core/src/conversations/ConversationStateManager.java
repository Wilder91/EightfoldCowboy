package conversations;

import java.util.HashMap;
import java.util.Map;

/**
 * Centralized manager for tracking conversation states across all NPCs.
 * This class maintains the conversation phases for each NPC in the game
 * and provides methods to get, set, advance, and reset these phases.
 */
public class ConversationStateManager {
    // Singleton instance
    private static ConversationStateManager instance;

    // Map of NPC IDs to their current conversation phases
    private final Map<Integer, Integer> npcConversationPhases = new HashMap<>();

    // Map defining how many conversation phases each NPC type has
    private final Map<Integer, Integer> maxNpcPhases = new HashMap<>();

    /**
     * Private constructor for singleton pattern
     */
    private ConversationStateManager() {
        // Initialize the maximum phases for each NPC type
        maxNpcPhases.put(0, 3); // Jim has 3 phases (0, 1, 2)
        maxNpcPhases.put(1, 2); // Martha has 2 phases (0, 1)
        maxNpcPhases.put(3, 1); // Miner has 1 phase
        maxNpcPhases.put(4, 1); // Cowboy has 1 phase
    }

    /**
     * Get the singleton instance
     */
    public static ConversationStateManager getInstance() {
        if (instance == null) {
            instance = new ConversationStateManager();
        }
        return instance;
    }

    /**
     * Get the current conversation phase for an NPC
     * @param npcId The ID of the NPC
     * @return The current conversation phase (defaults to 0 if not set)
     */
    public int getConversationPhase(int npcId) {
        return npcConversationPhases.getOrDefault(npcId, 0);
    }

    /**
     * Set the conversation phase for an NPC
     * @param npcId The ID of the NPC
     * @param phase The phase to set
     */
    public void setConversationPhase(int npcId, int phase) {
        npcConversationPhases.put(npcId, phase);
    }

    /**
     * Advance the conversation phase for an NPC by 1
     * @param npcId The ID of the NPC
     * @return The new phase
     */
    public int advanceConversationPhase(int npcId) {
        int currentPhase = getConversationPhase(npcId);
        int newPhase = currentPhase + 1;
        setConversationPhase(npcId, newPhase);
        return newPhase;
    }

    /**
     * Reset the conversation phase for an NPC to 0
     * @param npcId The ID of the NPC
     */
    public void resetConversationPhase(int npcId) {
        setConversationPhase(npcId, 0);
    }

    /**
     * Check if an NPC has more conversation phases
     * @param npcId The ID of the NPC
     * @return true if the NPC has more phases, false otherwise
     */
    public boolean hasMorePhases(int npcId) {
        int currentPhase = getConversationPhase(npcId);
        int maxPhases = maxNpcPhases.getOrDefault(npcId, 0);
        return currentPhase < maxPhases - 1;
    }

    /**
     * Get the total number of phases for an NPC
     * @param npcId The ID of the NPC
     * @return The total number of phases
     */
    public int getTotalPhases(int npcId) {
        return maxNpcPhases.getOrDefault(npcId, 0);
    }
}