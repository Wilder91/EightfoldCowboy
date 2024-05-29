package objects.animals.helper;

import objects.animals.Bird;

import java.util.HashMap;
import java.util.Map;

public class BirdManager {
    private static final Map<Integer, Bird> birdMap = new HashMap<>();

    public static void addBird(Bird bird) {
        birdMap.put(bird.getId(), bird);
    }

    public static Bird getBirdById(int id) {
        return birdMap.get(id);
    }
}
