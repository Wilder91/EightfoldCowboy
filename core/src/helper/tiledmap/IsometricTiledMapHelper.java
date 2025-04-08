package helper.tiledmap;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.mygdx.eightfold.GameContactListener;
import com.mygdx.eightfold.player.IsometricPlayer;
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

public class IsometricTiledMapHelper {

    private TiledMap tiledMap;
    private ShapeRenderer shapeRenderer;
    private GameAssets gameAssets;
    private GameContactListener gameContactListener;
    private ScreenInterface screenInterface;
    private float tileWidth;
    private float tileHeight;

    public IsometricTiledMapHelper(ScreenInterface screenInterface, GameAssets gameAssets, GameContactListener gameContactListener) {
        this.shapeRenderer = new ShapeRenderer();
        this.gameAssets = gameAssets;
        this.gameContactListener = gameContactListener;
        this.screenInterface = screenInterface;
    }

    public IsometricTiledMapRenderer setupMap(String fileName) {
        // Clear existing bodies
        clearWorldBodies(screenInterface.getWorld());

        // Load the new map
        tiledMap = new TmxMapLoader().load(fileName);

        // Store tile dimensions for coordinate conversions
        tileWidth = tiledMap.getProperties().get("tilewidth", Integer.class);
        tileHeight = tiledMap.getProperties().get("tileheight", Integer.class);

        // Parse the objects in the new map
        parseMapObjects(tiledMap.getLayers().get("objects").getObjects());
        parseWallObjects(tiledMap.getLayers().get("wall").getObjects());

        // Return the new isometric map renderer
        return new IsometricTiledMapRenderer(tiledMap);
    }

    private void clearWorldBodies(World world) {
        Array<Body> bodies = new Array<Body>();
        world.getBodies(bodies);
        for (Body body : bodies) {
            world.destroyBody(body);
        }
    }

    /**
     * Converts isometric coordinates to Cartesian coordinates
     * @param isoX X coordinate in isometric space
     * @param isoY Y coordinate in isometric space
     * @return Vector2 containing Cartesian coordinates
     */
    private Vector2 isoToCartesian(float isoX, float isoY) {
        // Convert from isometric to cartesian
        float cartX = (isoX / tileWidth + isoY / tileHeight) / 2;
        float cartY = (isoY / tileHeight - isoX / tileWidth) / 2;
        return new Vector2(cartX, cartY);
    }

    /**
     * Converts Cartesian coordinates to isometric coordinates
     * @param cartX X coordinate in Cartesian space
     * @param cartY Y coordinate in Cartesian space
     * @return Vector2 containing isometric coordinates
     */
    private Vector2 cartesianToIso(float cartX, float cartY) {
        // Convert from cartesian to isometric
        float isoX = (cartX - cartY) * tileWidth;
        float isoY = (cartX + cartY) * tileHeight;
        return new Vector2(isoX, isoY);
    }

    private void parseMapObjects(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof PolygonMapObject) {
                PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
                String polygonName = mapObject.getName();
                if (polygonName != null) {
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
                        case "aspen_1":
                            createTree(polygonMapObject, Tree.ASPEN_ONE);
                            break;
                        case "aspen_2":
                            createTree(polygonMapObject, Tree.ASPEN_TWO);
                            break;
                        case "aspen_3":
                            createTree(polygonMapObject, Tree.ASPEN_THREE);
                            break;
                        case "aspen_baby":
                            createTree(polygonMapObject, Tree.ASPEN_BABY);
                            break;
                        case "aspen_young":
                            createTree(polygonMapObject, Tree.ASPEN_YOUNG);
                            break;
                        case "aspen_stump":
                            createTree(polygonMapObject, Tree.ASPEN_STUMP);
                            break;
                        case "seedling":
                            createTree(polygonMapObject, Tree.SEEDLING);
                            break;
                        case "bush_one":
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
                        case "small_rock_one":
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
                        case "small_rock_two":
                            createRock(polygonMapObject, 4);
                            break;
                        case "pond":
                            createPond(polygonMapObject, 0);
                            break;
                        case "cliff_one":
                            createRock(polygonMapObject, 5);
                            break;
                        case "cliff_two":
                            createRock(polygonMapObject, 6);
                            break;
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
                    // Convert isometric coordinates to Cartesian for physics world
                    float centerIsoX = rectangle.x + rectangle.width / 2f;
                    float centerIsoY = rectangle.y + rectangle.height / 2f;

                    // Convert to Cartesian for physics
                    Vector2 cartesianPos = isoToCartesian(centerIsoX, centerIsoY);
                    float centerX = cartesianPos.x;
                    float centerY = cartesianPos.y;

                    // Scale dimensions appropriately for isometric view
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

                    // Use isometric coordinates for the sprite position
                    screenInterface.setPlayer(new IsometricPlayer(
                            centerIsoX,
                            centerIsoY,
                            rectangle.width,
                            rectangle.height,
                            body,
                            screenInterface,
                            gameAssets
                    ));
                }
                if (rectangleName != null && rectangleName.equals("Jim")) {

                    RectangleMapObject rectObj = (RectangleMapObject) mapObject;
                    NpcFactory npcFactory = new NpcFactory(screenInterface, gameAssets, gameContactListener, 0);
                    System.out.println(rectangleName);
                    npcFactory.createNPC(rectObj, rectangleName);
                }
                if (rectangleName != null && rectangleName.equals("Martha")) {
                    RectangleMapObject rectObj = (RectangleMapObject) mapObject;
                    NpcFactory npcFactory = new NpcFactory(screenInterface, gameAssets, gameContactListener, 1);
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
        }
    }

    private void createBird(PolygonMapObject polygonMapObject) {
        BirdFactory birdFactory = new BirdFactory(screenInterface, gameAssets);
        birdFactory.createBird(polygonMapObject);
    }

    private void createDoor(PolygonMapObject polygonMapObject, String polygonName) {
        DoorFactory doorFactory = new DoorFactory(screenInterface, gameAssets);
        doorFactory.createDoor(polygonMapObject, polygonName);
    }

    private void createChicken(PolygonMapObject polygonMapObject) {
        ChickenFactory chickenFactory = new ChickenFactory(screenInterface, gameAssets, false);
        chickenFactory.createChicken(polygonMapObject);
    }

    private void createSquirrel(PolygonMapObject polygonMapObject) {
        SquirrelFactory squirrelFactory = new SquirrelFactory(screenInterface, gameAssets, false);
        squirrelFactory.createSquirrel(polygonMapObject);
    }

    private void createBug(PolygonMapObject polygonMapObject, int bugType) {
        BugFactory bugFactory = new BugFactory(screenInterface, gameAssets);
        bugFactory.createBug(polygonMapObject, bugType);
    }

    private void createTree(PolygonMapObject polygonMapObject, int treeType) {
        TreeFactory treeFactory = new TreeFactory(screenInterface, gameAssets);
        treeFactory.createTree(polygonMapObject, treeType);
    }

    private void createPond(PolygonMapObject polygonMapObject, int pondType) {
        PondFactory pondFactory = new PondFactory(screenInterface, gameAssets, gameContactListener);
        pondFactory.createPond(polygonMapObject, 0);
    }

    private void createBush(PolygonMapObject polygonMapObject, int bushType) {
        BushFactory bushFactory = new BushFactory(screenInterface, gameAssets, gameContactListener);
        bushFactory.createBush(polygonMapObject, bushType);
    }

    private void createRock(PolygonMapObject polygonMapObject, int rockType) {
        RockFactory rockFactory = new RockFactory(screenInterface, gameAssets, gameContactListener);
        rockFactory.createRock(polygonMapObject, rockType);
    }

    private void createBoulder(PolygonMapObject polygonMapObject) {
        BoulderFactory boulderFactory = new BoulderFactory(screenInterface, gameAssets, gameContactListener);
        boulderFactory.createBoulder(polygonMapObject);
    }

    private void createBuilding(PolygonMapObject polygonMapObject, int buildingId) {
        BuildingFactory buildingFactory = new BuildingFactory(screenInterface, gameAssets);
        buildingFactory.createBuilding(polygonMapObject, buildingId);
    }

    private void createStaticBody(PolygonMapObject polygonMapObject) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = screenInterface.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(polygonMapObject);
        body.setUserData("static_body");
        body.createFixture(shape, 1000);
        shape.dispose();
    }

    private Shape createPolygonShape(PolygonMapObject polygonMapObject) {
        float[] vertices = polygonMapObject.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; i++) {
            // Convert isometric coordinates to Cartesian for Box2D physics
            float isoX = vertices[i * 2];
            float isoY = vertices[i * 2 + 1];
            Vector2 cartesian = isoToCartesian(isoX, isoY);

            // Scale down for Box2D
            Vector2 current = new Vector2(cartesian.x / PPM, cartesian.y / PPM);
            worldVertices[i] = current;
        }

        PolygonShape shape = new PolygonShape();
        shape.set(worldVertices);
        return shape;
    }
}