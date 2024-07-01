package objects.animals.object_helper;

import objects.inanimate.Door;
import java.util.HashMap;
import java.util.Map;

public class DoorManager {
    private static final Map<Integer, Door> doorMap = new HashMap<>();

    private DoorManager() {
        // private constructor to prevent instantiation
    }

    public static void addDoor(Door door) {
        doorMap.put(door.getId(), door);
    }

    public static Map<Integer, Door> getDoorMap() {
        return doorMap;
    }

    public static Door getDoorById(int id) {

        return doorMap.get(id);
    }
}
