package helper;

public enum ContactType {
    PLAYER(0x0001, 0x0002 | 0x0004 | 0x0008 | 0x0010),
    BISON(0x0004, 0x0001 | 0x0002 | 0x0008 | 0x0010),
    BIRD(0x0008, 0x0001 | 0x0002 | 0x0004 | 0x0010),
    TREE(0x0020, 0),
    DOOR(0x0040, 0x0001 | 0x0002);
    // Allow collisions with PLAYER and INANIMATE

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
