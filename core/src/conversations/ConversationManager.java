package conversations;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.mygdx.eightfold.player.GameEntity;
import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.firstLevel.FirstLevelBisonConversations;
import conversations.firstLevel.FirstLevelConversations;
import objects.animals.bison.Bison;

public class ConversationManager {
    private FirstLevelBisonConversations firstLevelBisonConversations;

    public static void startConversation(int level, GameEntity firstGameEntity, GameEntity secondGameEntity, ScreenInterface screenInterface){
        if(level == 1) {
            if (secondGameEntity instanceof Player) {
                Player player = (Player) secondGameEntity;
                if (firstGameEntity instanceof Bison) {
                    Bison bison = (Bison) firstGameEntity;
                    FirstLevelConversations conversations = new FirstLevelConversations(screenInterface, bison, player, "commodore64/skin/uiskin.json", "animals/bison/bison-single.png");
                    conversations.printHello();
                    screenInterface.showTextBox("yo");


                }

            }
        }



    }
}
