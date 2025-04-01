package conversations;

import com.mygdx.eightfold.player.GameEntity;
import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.firstLevel.FirstLevelConversations;
import objects.animals.bison.Bison;
import objects.humans.NPC;

public class ConversationManager {
    private FirstLevelConversations firstLevelConversations;
    private ScreenInterface screenInterface;
    private int level;
    private GameEntity firstGameEntity;
    private GameEntity secondGameEntity;

    public ConversationManager(int level, GameEntity firstGameEntity, GameEntity secondGameEntity, ScreenInterface screenInterface) {
        this.level = level;
        this.firstGameEntity = firstGameEntity;
        this.secondGameEntity = secondGameEntity;
        this.screenInterface = screenInterface;
    }

    public void startFirstLevelConversation() {
        if (level == 1) {
            if (secondGameEntity instanceof Player) {
                Player player = (Player) secondGameEntity;
                if (firstGameEntity instanceof NPC) {
                    NPC npc = (NPC) firstGameEntity;
                    //System.out.println("here I am!");
                    firstLevelConversations = new FirstLevelConversations(screenInterface, npc, player, "commodore64/skin/uiskin.json", "animals/bison/bison-single.png", npc.getConversationPhase());
                    firstLevelConversations.startNPCConversation();
                }
            }
        }
    }

    public void nextLine() {
        if (level == 1 && firstLevelConversations != null) {
            //firstLevelConversations.showCurrentLine();
            //firstLevelConversations.showCurrentLine();
            firstLevelConversations.advanceConversation();

        }
    }
}
