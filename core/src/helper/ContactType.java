package helper;

public enum ContactType {
    // Player and basic entities
    PLAYER(0x0001, 0x0002 | 0x0004 | 0x0010 | 0x0400 | 0x0200 | 0x0080 | 0x4000),

    // Animals
    BISON(0x0002, 0x0001 | 0x0008 | 0x0010 | 0x0080),
    BIRD(0x0004, 0x0001 | 0x0008),
    CHICKEN(0x0200, 0x0001 | 0x0400 & ~0x1000),
    SQUIRREL(0x1000, 0x0001 | 0x0400 & ~0x0200),

    // Insects
    BUG(0x0020, 0x0001),
    BUTTERFLY(0x0040, 0x0001),
    DRAGONFLY(0x0080, 0x0001 | 0x0002),

    // Environment
    TREE(0x0008, 0x0001 | 0x0002 | 0x0004),
    DOOR(0x0010, 0x0001 | 0x0002),
    BUILDING(0x0800, 0x0001 | 0x0002),
    FENCE(0x0100, 0x0001 | 0x0002 | 0x0004),
    ROCK(0x0400, 0x0001 | 0x0002 | 0x0004),

    // NPCs & interaction
    NPC(0x2000, 0x0001),

    // Combat system
    ATTACK(0x4000, 0x0080),        // Player attack - hits enemies only
    ENEMY(0x0080, 0x0001 | 0x4000), // Enemy - hit by player and player attacks
    ENEMYATTACK(0x0200, 0x0001),    // Enemy attack - hits player only

    // Special states
    LIGHT(0x1000, 0x0001),
    DEAD(0x2000, 0);

    private final short categoryBits;
    private final short maskBits;

    ContactType(int categoryBits, int maskBits) {
        this.categoryBits = (short) categoryBits;
        this.maskBits = (short) maskBits;
    }

    public short getCategoryBits() {
        return categoryBits;
    }

    public short getMaskBits() {
        return maskBits;
    }
}