package objects.enemies;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import objects.GameEntity;

/**
 * Manages all ThicketSaint enemies in the game.
 * This class provides a central registry for tracking enemies
 * and enables entity lookup by ID for collision handling.
 */
public class ThicketSaintManager {
    // Static singleton instance
    private static ThicketSaintManager instance;

    // Maps entity IDs to ThicketSaint instances
    private static ObjectMap<Integer, ThicketSaint> enemiesById = new ObjectMap<>();

    // Array of all active enemies for iteration
    private static Array<ThicketSaint> activeEnemies = new Array<>();

    // Private constructor for singleton pattern
    private ThicketSaintManager() {
        // Initialize if needed
    }

    /**
     * Gets the singleton instance of ThicketSaintManager.
     * @return The manager instance
     */
    public static ThicketSaintManager getInstance() {
        if (instance == null) {
            instance = new ThicketSaintManager();
        }
        return instance;
    }

    /**
     * Adds a ThicketSaint enemy to the manager.
     * @param enemy The ThicketSaint to add
     */
    public static void addEnemy(ThicketSaint enemy) {
        if (enemy == null) return;

        // Store by ID for quick lookup
        enemiesById.put(enemy.getId(), enemy);

        // Add to active enemies list
        activeEnemies.add(enemy);

        System.out.println("ThicketSaint added to manager with ID: " + enemy.getId());
    }

    /**
     * Removes a ThicketSaint from the manager.
     * @param enemy The ThicketSaint to remove
     */
    public static void removeEnemy(ThicketSaint enemy) {
        if (enemy == null) return;

        // Remove from maps
        enemiesById.remove(enemy.getId());
        activeEnemies.removeValue(enemy, true);

        System.out.println("ThicketSaint removed from manager with ID: " + enemy.getId());
    }

    /**
     * Gets a ThicketSaint by its ID.
     * @param id The ID of the ThicketSaint to find
     * @return The ThicketSaint with the given ID, or null if not found
     */
    public static ThicketSaint getEnemyById(int id) {
        return enemiesById.get(id);
    }

    /**
     * Gets all active ThicketSaint enemies.
     * @return Array of active ThicketSaint enemies
     */
    public static Array<ThicketSaint> getAllEnemies() {
        return activeEnemies;
    }

    /**
     * Gets the count of active enemies.
     * @return Number of active enemies
     */
    public static int getEnemyCount() {
        return activeEnemies.size;
    }

    /**
     * Clears all enemies from the manager.
     * Useful when changing levels or resetting the game.
     */
    public static void clearAll() {
        enemiesById.clear();
        activeEnemies.clear();
        System.out.println("All ThicketSaints cleared from manager");
    }

    /**
     * Updates all ThicketSaint enemies.
     * @param delta Time since last update
     */
    public static void updateAll(float delta) {
        for (ThicketSaint enemy : activeEnemies) {
            enemy.update(delta);
        }
    }
}