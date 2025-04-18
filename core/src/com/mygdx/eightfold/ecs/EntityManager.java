package com.mygdx.eightfold.ecs;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;
import objects.GameEntity;
import com.mygdx.eightfold.player.Player;
import objects.animals.squirrel.Squirrel;
import objects.animals.birds.Bird;
import objects.animals.farm_animals.Chicken;
import objects.animals.bugs.Bug;
import objects.animals.bugs.Butterfly;
import objects.animals.bugs.Dragonfly;
import objects.humans.Enemy;
import objects.humans.NPC;
import objects.inanimate.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Manages all game entities, providing centralized access and control over them.
 */
public class EntityManager {
    // Entity collections
    private final List<Bird> birds = new ArrayList<>();
    private final List<Building> buildings = new ArrayList<>();
    private final List<Boulder> boulders = new ArrayList<>();
    private final List<Tree> trees = new ArrayList<>();
    private final List<Bush> bushes = new ArrayList<>();
    private final List<Rock> rocks = new ArrayList<>();
    private final List<Rock> rockTops = new ArrayList<>();
    private final List<Pond> ponds = new ArrayList<>();
    private final List<Butterfly> butterflies = new ArrayList<>();
    private final List<Dragonfly> dragonflies = new ArrayList<>();
    private final List<Chicken> chickens = new ArrayList<>();
    private final List<Squirrel> squirrels = new ArrayList<>();
    private final List<NPC> npcs = new ArrayList<>();
    private final List<Door> doors = new ArrayList<>();
    private final List<Bug> bugs = new ArrayList<>();
    private final List<Fence> fences = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<GameEntity> entities = new ArrayList<>();

    // Map for NPC lookup by ID
    private final Map<Integer, NPC> npcMap = new HashMap<>();
    private final Map<Integer, Chicken> chickenMap = new HashMap<>();

    // Reference to player
    private Player player;

    // Reference to the world
    private World world;

    /**
     * Constructor that takes a World reference
     */
    public EntityManager(World world) {
        this.world = world;
    }

    /**
     * Updates all entities with the given delta time
     * @param delta time since last update
     */
    public void update(float delta) {
        // Update all entities
        for (Butterfly butterfly : butterflies) {
            butterfly.update(delta);
        }

        for (Fence fence : fences) {
            fence.update(delta);
        }

        for (Dragonfly dragonfly : dragonflies) {
            dragonfly.update(delta);
        }

        for (Squirrel squirrel : squirrels) {
            squirrel.update(delta);
        }

        for (Chicken chicken : chickens) {
            chicken.update(delta);
        }

        for (NPC npc : npcs) {
            npc.update(delta);
        }

        for (Tree tree : trees) {
            tree.update(delta);
        }

        for (Building building : buildings) {
            building.update(delta);
        }

        for (Bush bush : bushes) {
            //System.out.println(bush.getDepth());
            bush.update(delta);
        }

        if (player != null) {
            player.update(delta);
        }

        for (Bird bird : birds) {
            bird.update(delta);
        }

        for (Pond pond : ponds) {
            pond.update(delta);
        }

        for (Bug bug : bugs) {
            bug.update(delta);
        }

        for (Rock rock: rocks){
            rock.update(delta);
        }

        for (GameEntity entity : entities){
            entity.update(delta);
        }
    }

    /**
     * Renders all entities in the correct order
     * @param batch SpriteBatch to render with
     */
    public void render(SpriteBatch batch) {
        // Render ponds first (background)
        for (Pond pond : ponds) {
            pond.render(batch);
        }



        // Create a list for Y-sorted entities
        List<GameEntity> sortedEntities = new ArrayList<>();

        // Add all entities that should be Y-sorted
        if (player != null) sortedEntities.add(player);
        sortedEntities.addAll(chickens);
        sortedEntities.addAll(squirrels);
        sortedEntities.addAll(birds);
        sortedEntities.addAll(butterflies);
        sortedEntities.addAll(dragonflies);
        sortedEntities.addAll(npcs);
        sortedEntities.addAll(bushes);
        sortedEntities.addAll(trees);
        sortedEntities.addAll(buildings);
        sortedEntities.addAll(boulders);
        sortedEntities.addAll(bugs);
        sortedEntities.addAll(rocks);
        sortedEntities.addAll(fences);
        sortedEntities.addAll(enemies);
        sortedEntities.addAll(entities);

        // Sort by Y position
        Collections.sort(sortedEntities, GameEntity.Y_COMPARATOR);

        // Render all Y-sorted entities
        for (GameEntity entity : sortedEntities) {
            entity.render(batch);
        }



        for (Door door : doors) {
            door.render(batch);
        }
    }

    /**
     * Clean up resources when the manager is no longer needed
     */
    public void dispose() {
        // No resources to dispose directly,
        // but could be used to clear collections or other cleanup
        clear();
    }

    /**
     * Removes all entities from the manager
     */
    public void clear() {
        birds.clear();
        buildings.clear();
        boulders.clear();
        trees.clear();
        bushes.clear();
        rocks.clear();
        rockTops.clear();
        ponds.clear();
        butterflies.clear();
        dragonflies.clear();
        chickens.clear();
        squirrels.clear();
        npcs.clear();
        doors.clear();
        bugs.clear();
        npcMap.clear();

        // Don't set player to null to avoid issues,
        // let the game handle player references separately
    }

    /**
     * Removes an entity from all collections
     * @param entity The entity to remove
     * @return true if entity was found and removed, false otherwise
     */
    public boolean removeEntity(GameEntity entity) {
        boolean removed = false;

        if (entity instanceof Bird) removed = birds.remove(entity);
        else if (entity instanceof Building) removed = buildings.remove(entity);
        else if (entity instanceof Boulder) removed = boulders.remove(entity);
        else if (entity instanceof Tree) removed = trees.remove(entity);
        else if (entity instanceof Bush) removed = bushes.remove(entity);
        else if (entity instanceof Rock) removed = rocks.remove(entity);
        else if (entity instanceof Pond) removed = ponds.remove(entity);
        else if (entity instanceof Butterfly) removed = butterflies.remove(entity);
        else if (entity instanceof Dragonfly) removed = dragonflies.remove(entity);
        else if (entity instanceof Chicken) removed = chickens.remove(entity);
        else if (entity instanceof Squirrel) removed = squirrels.remove(entity);
        else if (entity instanceof NPC) {
            NPC npc = (NPC) entity;
            npcMap.remove(npc.getId());
            removed = npcs.remove(npc);
        }
        else if (entity instanceof Door) removed = doors.remove(entity);
        else if (entity instanceof Bug) removed = bugs.remove(entity);

        return removed;
    }

    /**
     * Destroys an entity's physics body and removes it from all collections
     * @param entity The entity to destroy
     */
    public void destroyEntity(GameEntity entity) {
        if (entity != null && entity.getBody() != null) {
            world.destroyBody(entity.getBody());
            entity.setBody(null);
            removeEntity(entity);
        }
    }

    // Getter methods for collections
    public List<Bird> getBirds() { return birds; }
    public List<Building> getBuildings() { return buildings; }
    public List<Boulder> getBoulders() { return boulders; }
    public List<Tree> getTrees() { return trees; }
    public List<Bush> getBushes() { return bushes; }
    public List<Rock> getRocks() { return rocks; }
    public List<Rock> getRockTops() { return rockTops; }
    public List<Pond> getPonds() { return ponds; }
    public List<Butterfly> getButterflies() { return butterflies; }
    public List<Dragonfly> getDragonflies() { return dragonflies; }
    public List<Chicken> getChickens() { return chickens; }
    public List<Squirrel> getSquirrels() { return squirrels; }
    public List<NPC> getNpcs() { return npcs; }
    public List<Door> getDoors() { return doors; }
    public List<Bug> getBugs() { return bugs; }

    // Add methods for each entity type
    public void addBird(Bird bird) {
        birds.add(bird);
    }

    public void addFence(Fence fence){
        fences.add(fence);
    }

    public void addBuilding(Building building) {
        buildings.add(building);
    }

    public void addBoulder(Boulder boulder) {
        boulders.add(boulder);
    }

    public void addTree(Tree tree) {
        trees.add(tree);
    }

    public void addBush(Bush bush) {
        bushes.add(bush);
    }

    public void addRock(Rock rock) {
        rocks.add(rock);
    }


    public void addPond(Pond pond) {
        ponds.add(pond);
    }

    public void addButterfly(Butterfly butterfly) {
        butterflies.add(butterfly);
    }

    public void addDragonfly(Dragonfly dragonfly) {
        dragonflies.add(dragonfly);
    }

    public void addChicken(Chicken chicken) {
        chickens.add(chicken);
        chickenMap.put(chicken.getId(), chicken);
    }

    public void addSquirrel(Squirrel squirrel) {
        squirrels.add(squirrel);
    }

    public void addNPC(NPC npc) {
        npcs.add(npc);
        npcMap.put(npc.getId(), npc);
    }

    public void addDoor(Door door) {
        doors.add(door);
    }

    public void addBug(Bug bug) {
        bugs.add(bug);
    }

    public void addEnemy(Enemy enemy) {
        enemies.add(enemy);
    }

    // Player methods
    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void removePlayerBody() {
        if (player != null && player.getBody() != null) {
            world.destroyBody(player.getBody());
            player.setBody(null);
        }
    }

    // NPC methods
    public NPC getNPCById(int id) {
        return npcMap.get(id);
    }

    // World methods
    public World getWorld() {
        return world;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public Chicken getChickenById(int id) {
        return chickenMap.get(id);
    }

    public void addEntity(GameEntity gameEntity) {
        entities.add(gameEntity);
    }
}