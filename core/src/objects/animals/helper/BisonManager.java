package objects.animals.helper;
import objects.animals.bison.Bison;

import java.util.HashMap;
import java.util.Map;

public class BisonManager {
    private static final Map<Integer, Bison> bisonMap = new HashMap<>();

    public static void addBison(Bison bison) {
        bisonMap.put(bison.getId(), bison);
    }

    public static Bison getBisonById(int id) {
        return bisonMap.get(id);
    }
}