package conversations;

import com.mygdx.eightfold.player.GameEntity;
import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.firstLevel.FirstLevelConversations;
import objects.animals.bison.Bison;

public class ConversationManager {
    //private FirstLevelBisonConversations firstLevelBisonConversations;
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

    public void startConversation() {

        if (level == 1) {
            if (secondGameEntity instanceof Player) {

                Player player = (Player) secondGameEntity;
                if (firstGameEntity instanceof Bison) {

                    Bison bison = (Bison) firstGameEntity;
                    FirstLevelConversations conversations = new FirstLevelConversations(screenInterface, bison, player, "commodore64/skin/uiskin.json", "animals/bison/bison-single.png");
                    conversations.startBisonConversations(bison);
                }
            }
        }
    }
}
