package objects.animals.object_helper;
import objects.animals.bison.Bison;
import objects.inanimate.Door;

import java.util.HashMap;
import java.util.Map;

public class DoorManager {
    private static final Map<Integer, Door> doorMap = new HashMap<>();

    public static void addDoor(Door door) {
        //System.out.println("bison manager called" + bison.getSprite());
        doorMap.put(door.getId(), door);
        //System.out.println("bison map after addition: " + bisonMap);
    }

    public static Map<Integer, Door> getDoorMap() {
        return doorMap;
    }

    public static Door getDoorById(int id) {
        return doorMap.get(id);
    }
}
