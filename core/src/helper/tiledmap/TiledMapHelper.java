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
import helper.tiledmap.factories.FenceFactory;
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
    private boolean beenCreated;

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
            this.beenCreated = false;

            if (mapObject instanceof PolygonMapObject) {
                PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
                String polygonName = mapObject.getName();
                String objectType = (String) mapObject.getProperties().get("type");
                if (polygonName != null) {
                    if(objectType != null) {
                        switch (objectType) {
                            case "rocks":
                                createRock(polygonMapObject, polygonName);
                                beenCreated = true;
                                break;
                            case "trees":
                                createTree(polygonMapObject, polygonName);
                                beenCreated = true;
                                break;
                            case "farm_animals":
                                createFarmAnimal(polygonMapObject, polygonName);
                                beenCreated = true;
                                break;
                            case "bushes":
                                createBush(polygonMapObject, polygonName);
                                beenCreated = true;
                                break;
                            case "bugs":
                                createBug(polygonMapObject, polygonName);
                                beenCreated = true;
                                break;
                            case "fences":
                                createFence(polygonMapObject, polygonName);
                                beenCreated = true;
                                break;
                        }
                    }
                    if (!beenCreated){
                    switch (polygonName) {
                        case "shop":
                            createBuilding(polygonMapObject, 0);
                            break;
                        case "barn":
                            createBuilding(polygonMapObject, 1);
                            break;
                        case "pond":
                            createPond(polygonMapObject, 0);
                            break;
                        case "squirrel":
                            createSquirrel(polygonMapObject);
                            break;
                        default:
                            createStaticBody(polygonMapObject);
                            break;
                    }
                }
                    }
            }

            if (mapObject instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) mapObject).getRectangle();
                String rectangleName = mapObject.getName();

                if (rectangleName != null && rectangleName.equals("player")) {
                    float centerX = (rectangle.x + rectangle.width / 2f) ;
                    float centerY = (rectangle.y + rectangle.height / 2f);
                    float bodyWidth = rectangle.width /3.5f  ;
                    float bodyHeight = rectangle.height / 2;
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
                    RectangleMapObject rectObj = (RectangleMapObject) mapObject;
                    NpcFactory npcFactory = new NpcFactory(screenInterface, gameAssets, gameContactListener, 0);

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

    private void createDoor(PolygonMapObject polygonMapObject, String polygonName) {
        DoorFactory doorFactory = new DoorFactory(screenInterface, gameAssets); // Instantiate the DoorFactory
        doorFactory.createDoor(polygonMapObject, polygonName); // Call createDoor method from DoorFactory
    }


    private void createFarmAnimal(PolygonMapObject polygonMapObject, String polygonName){
        switch (polygonName){
            case "chicken":
                ChickenFactory chickenFactory = new ChickenFactory(screenInterface, gameAssets, false);
                chickenFactory.createChicken(polygonMapObject, polygonName);
                break;
        }
    }

    private void createSquirrel(PolygonMapObject polygonMapObject) {
        SquirrelFactory squirrelFactory = new SquirrelFactory(screenInterface, gameAssets, false);
        squirrelFactory.createSquirrel(polygonMapObject);

    }

    private void createBug(PolygonMapObject polygonMapObject, String bugName ) {
        BugFactory bugFactory = new BugFactory(screenInterface, gameAssets);
        bugFactory.createBug(polygonMapObject, bugName);

    }

    private void createRock(PolygonMapObject polygonMapObject, String textureName) {
        RockFactory rockFactory = new RockFactory(screenInterface, gameAssets, gameContactListener);
        rockFactory.createRock(polygonMapObject, textureName);
    }


    private void createTree(PolygonMapObject polygonMapObject, String textureName) {
        TreeFactory treeFactory = new TreeFactory(screenInterface, gameAssets);
        treeFactory.createTree(polygonMapObject, textureName);
    }

    private void createFence(PolygonMapObject polygonMapObject, String fenceName) {
        FenceFactory fenceFactory = new FenceFactory(screenInterface, gameAssets, gameContactListener);
        fenceFactory.createFence(polygonMapObject, fenceName);
    }

    private void createPond(PolygonMapObject polygonMapObject, int pondType) {
        PondFactory pondFactory = new PondFactory(screenInterface, gameAssets, gameContactListener);
        pondFactory.createPond(polygonMapObject, pondType);
    }

    private void createBush(PolygonMapObject polygonMapObject, String bushName) {
        //System.out.println("create bush");
        BushFactory bushFactory = new BushFactory(screenInterface, gameAssets, gameContactListener);
        bushFactory.createBush(polygonMapObject, bushName);
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
