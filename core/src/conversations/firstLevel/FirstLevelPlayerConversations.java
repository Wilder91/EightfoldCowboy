package conversations.firstLevel;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.Conversation;
import objects.animals.bison.Bison;

public class FirstLevelPlayerConversations extends Conversation {
    private final ScreenInterface screenInterface;
    private final Player player;


    public FirstLevelPlayerConversations(ScreenInterface screenInterface, Player player, String filepath, String imagePath) {
        super(getPlayerConversationTexts(player), filepath, imagePath);
        this.screenInterface = screenInterface;
        this.player = player;
    }

    private static String[] getPlayerConversationTexts(Player player) {
        //System.out.println(bison.getId());
                return new String[]{"Howdy"};

    }

    @Override
    protected void showTextBox(String text) {
        screenInterface.showTextBox(text);
    }

    @Override
    protected void hideTextBox() {
        screenInterface.hideTextBox();
    }

    @Override
    protected void hideInfoBox() {
        screenInterface.hideInfoBox();
    }

    public boolean isConversationFinished() {
        return conversationIndex > conversationTexts.length;

    }


    public void resetConversation() {
        conversationIndex = 0;
        screenInterface.hideTextBox();
    }
}
