package helper.tiledmap;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.eightfold.screens.GameScreen;
import helper.BodyHelperService;
import helper.ContactType;
import helper.tiledmap.factories.animals.BirdFactory;
import helper.tiledmap.factories.inanimate.BoulderFactory;
import helper.tiledmap.factories.inanimate.BuildingFactory;
import helper.tiledmap.factories.inanimate.TreeFactory;
import helper.tiledmap.factories.animals.BisonFactory;
import objects.GameAssets;
import objects.inanimate.Tree;
import objects.player.Player;

import static helper.Constants.PPM;

public class TiledMapHelper {

    private TiledMap tiledMap;
    private GameScreen gameScreen;
    private ShapeRenderer shapeRenderer;
    private GameAssets gameAssets;

    public TiledMapHelper(GameScreen gameScreen, GameAssets gameAssets) {
        this.gameScreen = gameScreen;
        this.shapeRenderer = new ShapeRenderer();
        this.gameAssets = gameAssets;
    }

    public OrthogonalTiledMapRenderer setupMap() {
        tiledMap = new TmxMapLoader().load("maps/EightfoldMap.tmx");
        parseMapObjects(tiledMap.getLayers().get("objects").getObjects());
        parseWallObjects(tiledMap.getLayers().get("wall").getObjects());
        return new OrthogonalTiledMapRenderer(tiledMap);
    }

    private void parseMapObjects(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof PolygonMapObject) {
                PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
                String polygonName = mapObject.getName();
                if (polygonName != null) {
                    switch (polygonName) {
                        case "bison":
                            createBison(polygonMapObject);
                            break;
                        case "bird":
                            createBird(polygonMapObject);
                            break;
                        case "boulder":
                            createBoulder(polygonMapObject);
                            break;
                        case "saloon":
                            createSaloon(polygonMapObject);
                            break;
                        case "large_oak":
                            createTree(polygonMapObject, Tree.LARGE_OAK);
                            break;
                        case "medium_oak_one":
                            createTree(polygonMapObject, Tree.MEDIUM_1);
                            break;
                        case "medium_oak_two":
                            createTree(polygonMapObject, Tree.MEDIUM_2);
                            break;
                        case "small_oak":
                            createTree(polygonMapObject, Tree.SMALL);
                            break;
                        case "juvenile":
                            createTree(polygonMapObject, Tree.JUVENILE);
                            break;
                        case "seedling":
                            createTree(polygonMapObject, Tree.SEEDLING);
                            break;
                        default:
                            createStaticBody(polygonMapObject);
                            break;
                    }
                } else {
                    createStaticBody(polygonMapObject);
                }
            }

            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                String rectangleName = mapObject.getName();

                if (rectangleName != null && rectangleName.equals("player")) {
                    int playerId = 1;
                    Body body = BodyHelperService.createBody(
                            rectangle.x + rectangle.width / 2,
                            rectangle.y + rectangle.height / 2,
                            rectangle.width,
                            rectangle.height,
                            false,
                            gameScreen.getWorld(),
                            ContactType.PLAYER,
                            playerId
                    );
                    //System.out.println(rectangle.width + " " + rectangle.height);
                    gameScreen.setPlayer(new Player(rectangle.width , rectangle.height, body, gameScreen, gameAssets));
                }
            }
        }
    }

    private void parseWallObjects(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof PolygonMapObject) {
                PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
                String polygonName = mapObject.getName();
                if (polygonName != null) {
                    switch (polygonName) {
                        case "roots":
                            createStaticBody(polygonMapObject);
                            break;
                        case "wall":
                            createStaticBody(polygonMapObject);
                        default:
                            createStaticBody(polygonMapObject);
                            break;
                    }
                } else {
                    createStaticBody(polygonMapObject);
                }
            }

            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                String rectangleName = mapObject.getName();

                if (rectangleName != null && rectangleName.equals("player")) {
                    int playerId = 1;
                    Body body = BodyHelperService.createBody(
                            rectangle.x + rectangle.width / 2,
                            rectangle.y + rectangle.height / 2,
                            rectangle.width,
                            rectangle.height,
                            false,
                            gameScreen.getWorld(),
                            ContactType.PLAYER,
                            playerId
                    );
                    //System.out.println(rectangle.width + " " + rectangle.height);
                    gameScreen.setPlayer(new Player(rectangle.width , rectangle.height, body, gameScreen, gameAssets));
                }
            }
        }
    }

    private void createBird(PolygonMapObject polygonMapObject) {
        BirdFactory birdFactory = new BirdFactory(gameScreen, gameAssets);
        birdFactory.createBird(polygonMapObject);
    }
    private void createBison(PolygonMapObject polygonMapObject) {
        BisonFactory bisonFactory = new BisonFactory(gameScreen, gameAssets); // Instantiate the BisonFactory
        bisonFactory.createBison(polygonMapObject); // Call createBison method from BisonFactory
    }

    private void createTree(PolygonMapObject polygonMapObject, int treeType) {
        TreeFactory treeFactory = new TreeFactory(gameScreen, gameAssets);
        treeFactory.createTree(polygonMapObject, treeType);

    }
    private void createBoulder(PolygonMapObject polygonMapObject) {
        BoulderFactory boulderFactory = new BoulderFactory(gameScreen, gameAssets);
        boulderFactory.createBoulder(polygonMapObject);
    }

    private void createSaloon(PolygonMapObject polygonMapObject) {
        BuildingFactory buildingFactory = new BuildingFactory(gameScreen, gameAssets);
        buildingFactory.createBuilding(polygonMapObject);

    }

    private void createStaticBody(PolygonMapObject polygonMapObject) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(polygonMapObject);
        body.setUserData("static_body"); // Set userData to some meaningful data
        body.createFixture(shape, 1000);
        shape.dispose();
    }

    private Shape createPolygonShape(PolygonMapObject polygonMapObject) {
        float[] vertices = polygonMapObject.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; i++) {
            Vector2 current = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1] / PPM);
            worldVertices[i] = current;
        }

        PolygonShape shape = new PolygonShape();
        shape.set(worldVertices);
        return shape;
    }
}
