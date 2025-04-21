package helper.tiledmap.factories;

import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.BodyUserData;
import helper.ContactType;
import com.mygdx.eightfold.player.Player;

import static helper.Constants.PPM;

public class PlayerFactory {
    private ScreenInterface screenInterface;
    private GameAssets gameAssets;

    public PlayerFactory(ScreenInterface screenInterface, GameAssets gameAssets) {
        this.screenInterface = screenInterface;
        this.gameAssets = gameAssets;
    }

    public Player createPlayer(RectangleMapObject rectangleMapObject) {
        Rectangle rectangle = rectangleMapObject.getRectangle();

        // Calculate position and dimensions
        float centerX = (rectangle.x + rectangle.width / 2f);
        float centerY = (rectangle.y + rectangle.height / 2f);
        float bodyWidth = rectangle.width / 3.5f;
        float bodyHeight = rectangle.height / 1.4f;
        int playerId = 1;

        // Create the body first
        Body body = BodyHelperService.createBody(
                centerX,
                centerY,
                bodyWidth,
                bodyHeight,
                false,
                screenInterface.getWorld(),
                ContactType.PLAYER,
                playerId
        );

        // Create the player with the body already set
        Player player = new Player(
                centerX * PPM,
                centerY * PPM,
                rectangle.width,
                rectangle.height,
                body,  // Pass the already created body
                screenInterface,
                gameAssets,
                100f
        );

        // Now update the fixture user data with the player reference
        for (Fixture fixture : body.getFixtureList()) {
            if (fixture.getUserData() instanceof BodyUserData) {
                BodyUserData userData = (BodyUserData) fixture.getUserData();
                userData.setEntity(player);
            }
        }

        // Add player to screen
        screenInterface.setPlayer(player);

        System.out.println("Created Player with ID: " + playerId);

        return player;
    }
}