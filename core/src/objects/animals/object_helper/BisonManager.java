package objects.animals.object_helper;
import objects.animals.bison.Bison;

import java.util.HashMap;
import java.util.Map;

public class BisonManager {
    private static final Map<Integer, Bison> bisonMap = new HashMap<>();

    public static void addBison(Bison bison) {
        //System.out.println("bison manager called" + bison.getSprite());
        bisonMap.put(bison.getId(), bison);
        //System.out.println("bison map after addition: " + bisonMap);
    }

    public static Map<Integer, Bison> getBisonMap() {
        return bisonMap;
    }

    public static Bison getBisonById(int id) {
        return bisonMap.get(id);
    }
}
