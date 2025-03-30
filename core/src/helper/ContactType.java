package helper;

public enum ContactType {
    PLAYER(0x0001, 0x0002 | 0x0004 | 0x0008 | 0x0010), // Can collide with everything
    BISON(0x0002, 0xFFFF),
    BIRD(0x0004, 0xFFFF),
    TREE(0x0008, 0x0002 | 0x0004),
    DOOR(0x0010, 0x0001 | 0x0002), // Only collide with player and bison
    BUG(0x0020, 0xFFFF),
    BUTTERFLY(0x0040, 0xFFFF),
    DRAGONFLY(0x0080, 0xFFFF),
    NPC(0x0100, 0xFFFF),
    JIM(0x0200, 0x0001), // Only collide with player
    LIGHT(0x0400, 0xFFFF); // Lights see everything

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
