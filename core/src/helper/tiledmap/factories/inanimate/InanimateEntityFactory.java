package helper.tiledmap.factories.inanimate;

import com.badlogic.gdx.maps.objects.PolygonMapObject;

import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import com.mygdx.eightfold.GameAssets;


public abstract class InanimateEntityFactory {
    protected ScreenInterface screenInterface;
    protected GameAssets gameAssets;
    protected GameContactListener gameContactListener;

    public InanimateEntityFactory(ScreenInterface screen, GameAssets gameAssets, GameContactListener gameContactListener) {
        this.screenInterface = screen;
        this.gameAssets = gameAssets;
        this.gameContactListener = gameContactListener;
    }

    public abstract void createEntity(PolygonMapObject polygonMapObject, String polygonName);
}
