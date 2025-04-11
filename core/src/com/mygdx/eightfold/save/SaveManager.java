package com.mygdx.eightfold.save;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.mygdx.eightfold.ecs.EntityManager;
import com.mygdx.eightfold.screens.GameScreen;
import com.mygdx.eightfold.player.Player;
import objects.animals.farm_animals.Chicken;
import objects.humans.NPC;
import objects.inanimate.Door;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class SaveManager {
    private GameScreen gameScreen;
    private static final String SAVE_VERSION = "1.0";

    public SaveManager(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
    }

    public void saveGame(String fileName) {
        try {
            // Create main save data object
            Map<String, Object> saveData = new HashMap<>();

            // Add header information
            saveData.put("header", "EIGHTFOLD_SAVE");
            saveData.put("version", SAVE_VERSION);
            saveData.put("timestamp", System.currentTimeMillis());

            // Get entity manager
            EntityManager entityManager = gameScreen.getEntityManager();

            // Save player data
            Player player = entityManager.getPlayer();
            if (player != null && player.getBody() != null) {
                Map<String, Object> playerData = new HashMap<>();
                playerData.put("exists", true);
                playerData.put("posX", player.getBody().getPosition().x);
                playerData.put("posY", player.getBody().getPosition().y);
                // Add other player properties (inventory, stats, etc.)
                saveData.put("player", playerData);
            } else {
                Map<String, Object> playerData = new HashMap<>();
                playerData.put("exists", false);
                saveData.put("player", playerData);
            }

            // Save time of day
            saveData.put("timeOfDay", gameScreen.getCurrentTimeOfDay());

            // Save NPC data
            List<Map<String, Object>> npcsData = new ArrayList<>();
            for (NPC npc : entityManager.getNpcs()) {
                Map<String, Object> npcData = new HashMap<>();
                npcData.put("id", npc.getId());
                npcData.put("posX", npc.getBody().getPosition().x);
                npcData.put("posY", npc.getBody().getPosition().y);
                // Save other NPC properties
                npcsData.add(npcData);
            }
            saveData.put("npcs", npcsData);

            // Save chicken data
            List<Map<String, Object>> chickensData = new ArrayList<>();
            for (Chicken chicken : entityManager.getChickens()) {
                Map<String, Object> chickenData = new HashMap<>();
                chickenData.put("id", chicken.getId());
                chickenData.put("posX", chicken.getBody().getPosition().x);
                chickenData.put("posY", chicken.getBody().getPosition().y);
                // Save other chicken properties
                chickensData.add(chickenData);
            }
            saveData.put("chickens", chickensData);

            // Save door states
            List<Map<String, Object>> doorsData = new ArrayList<>();
            for (Door door : entityManager.getDoors()) {
                Map<String, Object> doorData = new HashMap<>();
                doorData.put("id", door.getId());
                // doorData.put("isOpen", door.isOpen());
                // Save other door properties
                doorsData.add(doorData);
            }
            saveData.put("doors", doorsData);

            // Use LibGDX Json utility for serialization
            Json json = new Json();
            json.setOutputType(OutputType.json); // For pretty formatting
            String jsonString = json.prettyPrint(saveData);

            // Write to file
            FileHandle file = Gdx.files.local(fileName);
            file.writeString(jsonString, false);

            Gdx.app.log("SaveManager", "Game saved successfully to " + fileName);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error saving game", e);
        }
    }

    public void loadGame(String fileName) {
        FileHandle file = Gdx.files.local(fileName);
        if (!file.exists()) {
            Gdx.app.log("SaveManager", "Save file not found: " + fileName);
            return;
        }

        try {
            // Read JSON data from file
            String jsonString = file.readString();
            JsonValue root = new JsonReader().parse(jsonString);
            System.out.println(root);
            // Verify header
            String header = root.getString("header");
            if (!header.equals("EIGHTFOLD_SAVE")) {
                Gdx.app.error("SaveManager", "Invalid save file format");
                return;
            }

            String version = root.getString("version");
          // Handle different save versions if needed

            EntityManager entityManager = gameScreen.getEntityManager();

            // Load player data
            JsonValue playerData = root.get("player");
            boolean playerExists = playerData.getBoolean("exists");
            if (playerExists) {
                System.out.println(playerData);
                float playerX = playerData.getFloat("posX");
                float playerY = playerData.getFloat("posY");
                Player player = entityManager.getPlayer();
                if (player != null && player.getBody() != null) {
                    player.getBody().setTransform(playerX, playerY, 0);
                    // Load other player properties
                }
            }

            // Load time of day
            String timeOfDay = root.getString("timeOfDay");
            gameScreen.setTimeOfDay(timeOfDay);

            // Load NPCs
            JsonValue npcsData = root.get("npcs");
            for (JsonValue npcData : npcsData) {
                int id = npcData.getInt("id");
                float x = npcData.getFloat("posX");
                float y = npcData.getFloat("posY");

                NPC npc = entityManager.getNPCById(id);
                if (npc != null && npc.getBody() != null) {
                    npc.getBody().setTransform(x, y, 0);
                    // Load other NPC properties
                }
            }

            // Load chickens
            JsonValue chickensData = root.get("chickens");
            for (JsonValue chickenData : chickensData) {
                int id = chickenData.getInt("id");
                float x = chickenData.getFloat("posX");
                float y = chickenData.getFloat("posY");

                Chicken chicken = entityManager.getChickenById(id);
                if (chicken != null && chicken.getBody() != null) {
                    chicken.getBody().setTransform(x, y, 0);
                    // Load other chicken properties
                }
            }

            // Load door states
            JsonValue doorsData = root.get("doors");
            for (JsonValue doorData : doorsData) {
                int id = doorData.getInt("id");
                // boolean isOpen = doorData.getBoolean("isOpen");

                //Door door = entityManager.getDoorById(id);
                //if (door != null) {
                    // door.setOpen(isOpen);
                    // Set other door properties
               //}
            }

            Gdx.app.log("SaveManager", "Game loaded successfully from " + fileName);
        } catch (Exception e) {
            Gdx.app.error("SaveManager", "Error loading game", e);
        }
    }
}