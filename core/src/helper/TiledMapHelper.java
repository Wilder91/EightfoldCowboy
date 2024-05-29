package helper;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.mygdx.eightfold.GameScreen;
import objects.animals.Bird;
import objects.animals.bison.Bison;
import objects.inanimate.Boulder;
import objects.inanimate.Building;
import objects.player.Player;

import static helper.Constants.PPM;

public class TiledMapHelper {

    private TiledMap tiledMap;
    private GameScreen gameScreen;
    private ShapeRenderer shapeRenderer;

    private static int bisonCounter = 0;
    private static int birdCounter = 0;
    private static int boulderCounter = 0;
    private static int buildingCounter = 0;

    public TiledMapHelper(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        this.shapeRenderer = new ShapeRenderer();
    }

    public OrthogonalTiledMapRenderer setupMap() {
        tiledMap = new TmxMapLoader().load("maps/map0.tmx");
        parseMapObjects(tiledMap.getLayers().get("objects").getObjects());
        return new OrthogonalTiledMapRenderer(tiledMap);
    }

    public void renderPathRectangles() {

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled); // Begin rendering filled shapes
        shapeRenderer.setColor(Color.BROWN);
        MapObjects objects = tiledMap.getLayers().get("objects").getObjects();
        for (MapObject object : objects) {

            if (object instanceof RectangleMapObject && "path".equals(object.getName())) {
                System.out.println("path requested");
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                createStaticRectangle(rectangle);
                float x = rectangle.getX();
                float y = rectangle.getY();
                float width = rectangle.getWidth();
                float height = rectangle.getHeight();
                shapeRenderer.rect(x, y, width, height); // Render rectangle
            }
        }
        shapeRenderer.end(); // End rendering
    }


    public TiledMap getTiledMap() {
        return tiledMap;
    }

    public int getMapWidth() {
        return tiledMap.getProperties().get("width", Integer.class);
    }

    public int getMapHeight() {
        return tiledMap.getProperties().get("height", Integer.class);
    }

    public int getTileWidth() {
        return tiledMap.getProperties().get("tilewidth", Integer.class);
    }

    public int getTileHeight() {
        return tiledMap.getProperties().get("tileheight", Integer.class);
    }

    private void parseMapObjects(MapObjects mapObjects) {
        for (MapObject mapObject : mapObjects) {
            if (mapObject instanceof PolygonMapObject) {
                PolygonMapObject polygonMapObject = (PolygonMapObject) mapObject;
                String polygonName = mapObject.getName();

                if (polygonName != null) {
                    switch (polygonName) {
                        case "bison":
                            int bisonId = ++bisonCounter;
                            Polygon polygon = polygonMapObject.getPolygon();
                            Rectangle boundingRectangle = polygon.getBoundingRectangle();

                            Body body = BodyHelperService.createBody(
                                    boundingRectangle.x + boundingRectangle.width / 2,
                                    boundingRectangle.y + boundingRectangle.height / 2,
                                    boundingRectangle.width,
                                    boundingRectangle.height,
                                    false,
                                    gameScreen.getWorld(),
                                    ContactType.BISON,
                                    bisonId
                            );

                            Bison bison = new Bison(
                                    boundingRectangle.width,
                                    boundingRectangle.height,
                                    boundingRectangle.x + boundingRectangle.width / 2,
                                    boundingRectangle.y + boundingRectangle.height / 2,
                                    body,
                                    true,
                                    gameScreen,
                                    bisonId
                            );





                            gameScreen.addBison(bison);
                            System.out.println("Created Bison with ID: " + bisonId);
                            break;
                        case "bird":
                            int birdId = ++birdCounter;
                            polygon = polygonMapObject.getPolygon();
                            boundingRectangle = polygon.getBoundingRectangle();

                            body = BodyHelperService.createBody(
                                    boundingRectangle.x + boundingRectangle.width / 2,
                                    boundingRectangle.y + boundingRectangle.height / 2,
                                    boundingRectangle.width / 2,
                                    boundingRectangle.height /2,
                                    false,
                                    gameScreen.getWorld(),
                                    ContactType.BIRD,
                                    birdId
                            );

                            Bird bird = new Bird(
                                    boundingRectangle.width / 2,
                                    boundingRectangle.height / 2,
                                    boundingRectangle.x + boundingRectangle.width / 2,
                                    boundingRectangle.y + boundingRectangle.height / 2,
                                    body,
                                    true,
                                    gameScreen,
                                    birdId
                            );

                            gameScreen.addBird(bird);
                            break;
                        case "boulder":
                            System.out.println("boulder requested");
                            int boulderId = ++boulderCounter;
                            polygon = polygonMapObject.getPolygon();
                            boundingRectangle = polygon.getBoundingRectangle();

                            // Create static body for Boulder
                            BodyDef bodyDef = new BodyDef();
                            bodyDef.type = BodyDef.BodyType.StaticBody;
                            bodyDef.position.set(
                                    (boundingRectangle.x + boundingRectangle.width / 2) / PPM,
                                    (boundingRectangle.y + boundingRectangle.height / 2) / PPM
                            );

                            Body boulderBody = gameScreen.getWorld().createBody(bodyDef);

                            PolygonShape shape = new PolygonShape();
                            shape.setAsBox(
                                    boundingRectangle.width / 2 / PPM,
                                    boundingRectangle.height / 2 / PPM
                            );

                            boulderBody.createFixture(shape, 0.0f);
                            shape.dispose();

                            Boulder boulder = new Boulder(
                                    boundingRectangle.width * 2,
                                    boundingRectangle.height * 2,
                                    boulderBody,
                                    gameScreen,
                                    boulderId
                            );
                            gameScreen.addBoulder(boulder);
                            break;
                        case "saloon":
                            System.out.println("Saloon requested");
                            int buildingId = ++buildingCounter;
                            Polygon buildingPolygon = polygonMapObject.getPolygon();
                            Rectangle buildingBoundingRectangle = buildingPolygon.getBoundingRectangle();

                            // Create static body for Saloon
                            BodyDef buildingBodyDef = new BodyDef();
                            buildingBodyDef.type = BodyDef.BodyType.StaticBody;
                            buildingBodyDef.position.set(
                                    (buildingBoundingRectangle.x + buildingBoundingRectangle.width / 2) / PPM,
                                    (buildingBoundingRectangle.y + buildingBoundingRectangle.height / 2) / PPM
                            );

                            Body saloonBody = gameScreen.getWorld().createBody(buildingBodyDef);

                            PolygonShape buildingShape = new PolygonShape();
                            buildingShape.setAsBox(
                                    buildingBoundingRectangle.width / 2 / PPM,
                                    buildingBoundingRectangle.height / 2 / PPM
                            );

                            saloonBody.createFixture(buildingShape, 0.0f);
                            buildingShape.dispose();

                            // Create a Saloon object and add it to the GameScreen
                            Building building = new Building(
                                    buildingBoundingRectangle.width / 2,
                                    buildingBoundingRectangle.height / 2,
                                    buildingBoundingRectangle.x + buildingBoundingRectangle.width / 2,
                                    buildingBoundingRectangle.y + buildingBoundingRectangle.height / 2,
                                    saloonBody,
                                    true,
                                    gameScreen,
                                    buildingId
                            );

                            gameScreen.addBuilding(building);
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

                    gameScreen.setPlayer(new Player(rectangle.width, rectangle.height, body, gameScreen));
                }
            }
        }
    }

    private void createStaticBody(PolygonMapObject polygonMapObject) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(polygonMapObject);
        body.setUserData(body);
        body.createFixture(shape, 1000);
        shape.dispose();
    }

    private void createStaticRectangle(Rectangle rectangle) {
        // Define the body definition

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        // Set the position of the body's center
        bodyDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / PPM,
                (rectangle.getY() + rectangle.getHeight() / 2) / PPM);

        // Create the body in the Box2D world
        Body body = gameScreen.getWorld().createBody(bodyDef);

        // Define the shape of the rectangle
        PolygonShape shape = new PolygonShape();
        // Set the box shape with half-width and half-height
        shape.setAsBox(rectangle.getWidth() / 2 / PPM, rectangle.getHeight() / 2 / PPM);

        // Create a fixture from the shape with density 0 (static)
        body.createFixture(shape, 0.0f);

        // Dispose the shape to free up resources
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
