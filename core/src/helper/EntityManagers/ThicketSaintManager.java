package helper.EntityManagers;

import objects.enemies.ThicketSaint;
import objects.humans.Enemy;

import java.util.HashMap;
import java.util.Map;

public class ThicketSaintManager {
    private static final Map<Integer, ThicketSaint> enemyMap = new HashMap<>();

    public static void addEnemy(ThicketSaint enemy) {
        enemyMap.put(enemy.getId(), enemy);
    }

    public static Map getThicketSaint(){
        return enemyMap;
    }
}
