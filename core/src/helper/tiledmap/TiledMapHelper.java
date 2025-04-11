package helper.tiledmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
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
import helper.tiledmap.factories.animals.ChickenFactory;
import helper.tiledmap.factories.animals.SquirrelFactory;
import helper.tiledmap.factories.animals.bugs.BugFactory;
import helper.tiledmap.factories.animals.BirdFactory;
import helper.tiledmap.factories.inanimate.*;
import helper.tiledmap.factories.animals.NpcFactory;
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

        System.out.println(tiledMap.getLayers().get("objects").getObjects());
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

            if (mapObject instanceof PolygonMapObject) {
                PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
                String polygonName = mapObject.getName();
                String objectClass = (String) mapObject.getProperties().get("class");
                System.out.println(objectClass);
                if (polygonName != null) {
                    if(objectClass != null) {
                        switch (objectClass) {
                            case "rocks":
                                createRock(polygonMapObject, polygonName);
                                break;
                            case "trees":
                                createTree(polygonMapObject, polygonName);

                        }
                    }


                    switch (polygonName) {
                        case "bird":
                            createBird(polygonMapObject);
                            break;
                        case "boulder":
                            createBoulder(polygonMapObject);
                            break;
                        case "shop":
                            createBuilding(polygonMapObject, 0);
                            break;
                        case "barn":
                            createBuilding(polygonMapObject, 1);
                            break;
                        case "bush_one":
                            //System.out.println("bush!");
                            createBush(polygonMapObject, 0);
                            break;
                        case "bush_two":
                            createBush(polygonMapObject, 1);
                            break;
                        case "bush_three":
                            createBush(polygonMapObject, 2);
                            break;
                        case "bush_four":
                            createBush(polygonMapObject, 3);
                            break;
                        case "bush_five":
                            createBush(polygonMapObject, 4);
                            break;
//                        case "large_rock":
//                            createRock(polygonMapObject, 3);
//                            break;
//                        case "small_rock_two":
//                            createRock(polygonMapObject, 4);
//                            break;
                        case "pond":
                            createPond(polygonMapObject, 0);
                            break;
//                        case "cliff_one":
//                            createRock(polygonMapObject, 5);
//                            break;
//                        case "cliff_two":t
//                            createRock(polygonMapObject, 6);
//                            break;
                        case "small_white_butterfly":
                            createBug(polygonMapObject, 0);
                            break;
                        case "dragonfly":
                            createBug(polygonMapObject, 1);
                            break;
                        case "chicken":
                            createChicken(polygonMapObject);
                            break;
                        case "squirrel":
                            createSquirrel(polygonMapObject);
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
//                    System.out.println("PLAYER");
//                    System.out.println("Raw Tiled coords: x=" + rectangle.x + ", y=" + rectangle.y);
//                    System.out.println("Width: " + rectangle.width + ", Height: " + rectangle.height);
//                    System.out.println("PPM = " + PPM);
                    float centerX = (rectangle.x + rectangle.width / 2f) ;
                    float centerY = (rectangle.y + rectangle.height / 2f);
                    float bodyWidth = rectangle.width / PPM;
                    float bodyHeight = rectangle.height / PPM;
                    int playerId = 1;



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
//                    System.out.println("Body pos (meters): " + body.getPosition());
//                    System.out.println("Expected center (meters): " + centerX + ", " + centerY);
//                    System.out.println("Player pos in pixels: " + centerY + ", " + centerY);
//
//                    System.out.println("Tiled map position: " + rectangle.x + ", " + rectangle.y);

                    screenInterface.setPlayer(new Player(
                            centerX * PPM,
                            centerY * PPM,
                            rectangle.width,
                            rectangle.height,
                            body,
                            screenInterface,
                            gameAssets
                    ));


                }
                if (rectangleName != null && rectangleName.equals("Jim")) {
                   // System.out.println("there's jIm!");
                    RectangleMapObject rectObj = (RectangleMapObject) mapObject;
                    NpcFactory npcFactory = new NpcFactory(screenInterface, gameAssets, gameContactListener, 0);
                    System.out.println(rectangleName);
                    npcFactory.createNPC(rectObj, rectangleName);
                }
                if (rectangleName != null && rectangleName.equals("Martha")) {
                    //System.out.println("there it is!");
                    RectangleMapObject rectObj = (RectangleMapObject) mapObject;
                    NpcFactory npcFactory = new NpcFactory(screenInterface, gameAssets, gameContactListener, 1);
                   // System.out.println(npcFactory);
                    npcFactory.createNPC(rectObj, rectangleName);
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

    private void createDoor(PolygonMapObject polygonMapObject, String polygonName) {
        DoorFactory doorFactory = new DoorFactory(screenInterface, gameAssets); // Instantiate the DoorFactory
        doorFactory.createDoor(polygonMapObject, polygonName); // Call createDoor method from DoorFactory
    }

    private void createButterfly(PolygonMapObject polygonMapObject, int butterflyType) {

    }

    private void createChicken(PolygonMapObject polygonMapObject) {
        ChickenFactory chickenFactory = new ChickenFactory(screenInterface, gameAssets, false);
        chickenFactory.createChicken(polygonMapObject);

    }

    private void createSquirrel(PolygonMapObject polygonMapObject) {
        SquirrelFactory squirrelFactory = new SquirrelFactory(screenInterface, gameAssets, false);
        squirrelFactory.createSquirrel(polygonMapObject);

    }

    private void createBug(PolygonMapObject polygonMapObject, int bugType ) {
        BugFactory bugFactory = new BugFactory(screenInterface, gameAssets);
        bugFactory.createBug(polygonMapObject, bugType);
        //System.out.println("bug id: " + bugType);

    }

    private void createRock(PolygonMapObject polygonMapObject, String textureName) {
        RockFactory rockFactory = new RockFactory(screenInterface, gameAssets, gameContactListener);
        rockFactory.createRock(polygonMapObject, textureName);
    }


    private void createTree(PolygonMapObject polygonMapObject, String textureName) {
        System.out.println("create tree");
        TreeFactory treeFactory = new TreeFactory(screenInterface, gameAssets);
        treeFactory.createTree(polygonMapObject, textureName);
    }

    private void createPond(PolygonMapObject polygonMapObject, int pondType) {
        PondFactory pondFactory = new PondFactory(screenInterface, gameAssets, gameContactListener);
        pondFactory.createPond(polygonMapObject, 0);
    }

    private void createBush(PolygonMapObject polygonMapObject, int bushType) {
        //System.out.println("create bush");
        BushFactory bushFactory = new BushFactory(screenInterface, gameAssets, gameContactListener);
        bushFactory.createBush(polygonMapObject, bushType);
    }





    private void createBoulder(PolygonMapObject polygonMapObject) {
        BoulderFactory boulderFactory = new BoulderFactory(screenInterface, gameAssets, gameContactListener);
        boulderFactory.createBoulder(polygonMapObject);
    }

    private void createBuilding(PolygonMapObject polygonMapObject, int buildingId) {
        BuildingFactory buildingFactory = new BuildingFactory(screenInterface, gameAssets);
        //System.out.println("building id" + buildingId);
        buildingFactory.createBuilding(polygonMapObject, buildingId);
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
