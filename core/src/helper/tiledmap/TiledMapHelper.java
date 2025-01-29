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
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.screens.ScreenInterface;
import helper.BodyHelperService;
import helper.ContactType;
import helper.tiledmap.factories.animals.BirdFactory;
import helper.tiledmap.factories.inanimate.*;
import helper.tiledmap.factories.animals.BisonFactory;
import com.mygdx.eightfold.GameAssets;
import com.mygdx.eightfold.player.Player;
import objects.inanimate.Tree;

import static helper.Constants.PPM;

public class TiledMapHelper {

    private TiledMap tiledMap;
    private ShapeRenderer shapeRenderer;
    private GameAssets gameAssets;
    private GameContactListener gameContactListener;
    private ScreenInterface screenInterface;

    public TiledMapHelper(ScreenInterface screenInterface, GameAssets gameAssets, GameContactListener gameContactListener) {
        this.shapeRenderer = new ShapeRenderer();
        this.gameAssets = gameAssets;
        this.gameContactListener = gameContactListener;
        this.screenInterface = screenInterface;
    }

    public OrthogonalTiledMapRenderer setupMap(String fileName) {
        // Clear existing bodies
        clearWorldBodies(screenInterface.getWorld());

        // Load the new map
        tiledMap = new TmxMapLoader().load(fileName);

        // Parse the objects in the new map
        parseMapObjects(tiledMap.getLayers().get("objects").getObjects());
        parseWallObjects(tiledMap.getLayers().get("wall").getObjects());

        // Return the new map renderer
        return new OrthogonalTiledMapRenderer(tiledMap);
    }

    private void clearWorldBodies(World world) {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body body : bodies) {
            world.destroyBody(body);
        }
    }

    private void parseMapObjects(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            //System.out.println(mapObject.getProperties().get("type", String.class));
            if (mapObject instanceof PolygonMapObject) {
                PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
                String polygonName = mapObject.getName();
                if (polygonName != null) {
                    switch (polygonName) {
                        case "bison":
                            createBison(polygonMapObject);
                            break;
                        case "talking-bison":
                            createTalkingBison(polygonMapObject);
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
                        case "bush_one":
                            System.out.println("bush!");
                            createBush(polygonMapObject, 1);
                            break;
                        case "bush_two":
                            createBush(polygonMapObject, 2);
                            break;
                        case "bush_three":
                            createBush(polygonMapObject, 3);
                            break;
                        case "bush_four":
                            createBush(polygonMapObject, 4);
                            break;
                        case "bush_five":
                            createBush(polygonMapObject, 5);
                            break;
                        case "small_rock":
                            createRock(polygonMapObject, 0);
                            break;
                        case "medium_rock_one":
                            createRock(polygonMapObject, 3);
                            break;
                        case "medium_rock_two":
                            createRock(polygonMapObject, 2);
                            break;
                        case "large_rock":
                            createRock(polygonMapObject, 1);
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
                    //System.out.println("PLAYER");
                    int playerId = 1;
                    Body body = BodyHelperService.createBody(
                            rectangle.x + rectangle.width / PPM,
                            rectangle.y  + rectangle.height / PPM,
                            rectangle.width,
                            rectangle.height,
                            false,
                            screenInterface.getWorld(),
                            ContactType.PLAYER,
                            playerId
                    );
                    screenInterface.setPlayer(new Player(rectangle.x + rectangle.width / PPM, rectangle.y  + rectangle.height / PPM,rectangle.width, rectangle.height, body, screenInterface, gameAssets));
                }
            }
        }
    }

    private void parseWallObjects(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof PolygonMapObject) {
                PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
                String polygonName = polygonMapObject.getName();
                String polygonClass = mapObject.getProperties().get("type", String.class);

                if (polygonClass != null) {
                    if ("door".equals(polygonClass)) {
                        if (polygonName != null) {
                            switch (polygonName) {
                                case "enter_saloon":
                                    System.out.println("enter saloon!");
                                    createDoor(polygonMapObject, polygonName);
                                    break;
                                case "leave_saloon":
                                    createDoor(polygonMapObject, polygonName);
                                    break;
                                default:
                                    createStaticBody(polygonMapObject);
                                    break;
                            }
                        }
                    } else if ("wall".equals(polygonClass)) {
                        createStaticBody(polygonMapObject);
                    }
                } else if (polygonName != null) {
                    switch (polygonName) {
                        case "roots":
                            createStaticBody(polygonMapObject);
                            break;
                        default:
                            createStaticBody(polygonMapObject);
                            break;
                    }
                } else {
                    createStaticBody(polygonMapObject);
                }
            }

//            if (mapObject instanceof RectangleMapObject) {
//                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
//                String rectangleName = mapObject.getName();
//
//                if (rectangleName != null && rectangleName.equals("player")) {
//                    int playerId = 1;
//                    Body body = BodyHelperService.createBody(
//                            rectangle.x + rectangle.width / 2,
//                            rectangle.y + rectangle.height / 2,
//                            rectangle.width,
//                            rectangle.height,
//                            false,
//                            screenInterface.getWorld(),
//                            ContactType.PLAYER,
//                            playerId
//                    );
//                    screenInterface.setPlayer(new Player(rectangle.width, rectangle.height, body, screenInterface, gameAssets));
////                }
//            }
        }
    }

    private void createBird(PolygonMapObject polygonMapObject) {
        BirdFactory birdFactory = new BirdFactory(screenInterface, gameAssets);
        birdFactory.createBird(polygonMapObject);
    }

    private void createBison(PolygonMapObject polygonMapObject) {
        BisonFactory bisonFactory = new BisonFactory(screenInterface, gameAssets, false); // Instantiate the BisonFactory
        bisonFactory.createBison(polygonMapObject); // Call createBison method from BisonFactory
    }

    private void createTalkingBison(PolygonMapObject polygonMapObject) {
        BisonFactory bisonFactory = new BisonFactory(screenInterface, gameAssets, true); // Instantiate the BisonFactory
        bisonFactory.createBison(polygonMapObject); // Call createBison method from BisonFactory
    }

    private void createDoor(PolygonMapObject polygonMapObject, String polygonName) {
        DoorFactory doorFactory = new DoorFactory(screenInterface, gameAssets); // Instantiate the DoorFactory
        doorFactory.createDoor(polygonMapObject, polygonName); // Call createDoor method from DoorFactory
    }

    private void createTree(PolygonMapObject polygonMapObject, int treeType) {
        TreeFactory treeFactory = new TreeFactory(screenInterface, gameAssets);
        treeFactory.createTree(polygonMapObject, treeType);
    }

    private void createBush(PolygonMapObject polygonMapObject, int bushType) {
        //System.out.println("create bush");
        BushFactory bushFactory = new BushFactory(screenInterface, gameAssets, gameContactListener);
        bushFactory.createBush(polygonMapObject, bushType);
    }
    private void createRock(PolygonMapObject polygonMapObject, int rockType) {
        System.out.println("create rock");
        RockFactory rockFactory = new RockFactory(screenInterface, gameAssets, gameContactListener);
        rockFactory.createRock(polygonMapObject, rockType);
    }


    private void createBoulder(PolygonMapObject polygonMapObject) {
        BoulderFactory boulderFactory = new BoulderFactory(screenInterface, gameAssets, gameContactListener);
        boulderFactory.createBoulder(polygonMapObject);
    }

    private void createSaloon(PolygonMapObject polygonMapObject) {
        BuildingFactory buildingFactory = new BuildingFactory(screenInterface, gameAssets);
        buildingFactory.createBuilding(polygonMapObject);
    }

    private void createStaticBody(PolygonMapObject polygonMapObject) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = screenInterface.getWorld().createBody(bodyDef);
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
