package conversations.firstLevel;

import com.mygdx.eightfold.player.Player;
import com.mygdx.eightfold.screens.ScreenInterface;
import conversations.Conversation;

public class FirstLevelPlayerConversations extends Conversation {
    private final ScreenInterface screenInterface;
    private final Player player;


    public FirstLevelPlayerConversations(ScreenInterface screenInterface, Player player, String filepath) {
        super(getPlayerConversationTexts(player), getPlayerConversationTexts(player), filepath);
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
        return conversationIndex > bisonConversationTexts.length;

    }


    public void resetConversation() {
        conversationIndex = 0;
        screenInterface.hideTextBox();
    }
}