package conversations.firstLevel;

import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.Conversation;
import objects.humans.NPC;

/**
 * Handles conversation content for first level NPCs.
 * Focuses solely on providing dialogue content and character portraits.
 */
public class FirstLevelConversations extends Conversation {
    private final NPC npc;
    private final Player player;
    private final ScreenInterface screenInterface;

    public FirstLevelConversations(ScreenInterface screenInterface, NPC npc, Player player) {
        super(null, null, "commodore64/skin/uiskin.json");
        this.screenInterface = screenInterface;
        this.npc = npc;
        this.player = player;
    }

    /**
     * Sets the portrait based on which character is speaking
     */
    public void setPortrait(Object character) {
        if(character == npc) {
            if (npc.getId() == 0) {
                screenInterface.setTextBox("Jim_Idle_Down_1 copy.png");
            } else if (npc.getId() == 1) {
                screenInterface.setTextBox("Martha_Idle_Down_1 copy.png");
            }
        } else {
            screenInterface.setTextBox("Character_Idle_Down_1 copy.png");
        }
    }

    /**
     * Returns the lines the NPC should say for a given phase
     */
    public String[] getNPCLines(int phase) {
        System.out.println("Getting NPC texts for phase: " + phase);
        switch (npc.getId()) {
            case 0: // Jim
                if (phase == 0) {
                    return new String[]{
                            "Hey there, I'm Old Jim."
                    };
                } else if (phase == 1) {
                    return new String[]{
                            "Nice day today, huh?",
                            "Feel free to walk around and explore the forest." + System.lineSeparator() + "I'll be here watching" + System.lineSeparator() + "the pond."
                    };
                } else if (phase == 2) {
                    return new String[]{
                            "It gets awful lonely out here",
                            "Just me and Martha",
                            "A man starts to wonder what" + System.lineSeparator() + "it's all for"
                    };
                }
                break;
            case 1: // Martha
                if (phase == 0) {
                    return new String[]{
                            "Oh hello sweetheart, you can call me Martha"
                    };
                } else if (phase == 1) {
                    return new String[]{
                            "How precious"
                    };
                }
                break;
            case 3: // Miner
                return new String[]{
                        "Welcome to town.",
                        "Even the dogs talk here."
                };
            case 4: // Cowboy
                if (phase == 0) {
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
     * Returns the player's responses for a given phase
     */
    public String[] getPlayerLines(int phase) {
        System.out.println("Getting player texts for phase: " + phase);
        switch (npc.getId()) {
            case 0: // Jim
                if (phase == 0) {
                    return new String[]{
                            "Nice, I'm Tully.",
                            "It's a pleasure to meet you.",
                            "It's beautiful here."
                    };
                } else if(phase == 1) {
                    return new String[]{"Thank you Jim, I look forward to exploring"};
                } else if(phase == 2) {
                    return new String[]{"10-4 good buddy"};
                }
                break;
            case 1: // Martha
                if (phase == 0) {
                    return new String[]{
                            "It's a pleasure, Martha", "My name is Tully"
                    };
                } else {
                    return new String[]{}; // No reply
                }

            case 3: // Miner
                return new String[]{"Not me bro."};
            case 4: // Cowboy
                return new String[]{"That's great man, seems like a weird place."};
        }
        return new String[]{"Okay then."}; // Fallback/default
    }

    @Override
    protected void showTextBox(String text) {

    }

    @Override
    protected void hideTextBox() {
        // Implementation could be moved to screenInterface
        screenInterface.hideTextBox();
    }

    @Override
    protected void hideInfoBox() {
        // Implementation could be moved to screenInterface
        screenInterface.hideInfoBox();
    }
}