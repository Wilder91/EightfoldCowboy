package objects.animals.object_helper;

import objects.animals.bird.Bird;

import java.util.HashMap;
import java.util.Map;

public class BirdManager {
    private static final Map<Integer, Bird> birdMap = new HashMap<>();

    public static void addBird(Bird bird) {
        birdMap.put(bird.getId(), bird);
    }


}
