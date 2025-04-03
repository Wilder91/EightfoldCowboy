package objects.humans;



import java.util.HashMap;
import java.util.Map;



public class NPCManager {
    private static final Map<Integer, NPC> npcMap = new HashMap<>();

    public static void addNPC(NPC npc) {

        npcMap.put(npc.getId(), npc);

    }

    public static Map<Integer, NPC> getNPCMap() {
        return npcMap;
    }

    public static NPC getNPCById(int id) {
        return npcMap.get(id);
    }
}
