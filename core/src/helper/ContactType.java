package helper;

public enum ContactType {
    PLAYER((short) 0x0001, (short) (0x0002 | 0x0004 | 0x0008 | 0x0010)), // Excludes TREE
    INANIMATE((short) 0x0002, (short) (0x0001 | 0x0004 | 0x0008 | 0x0010)), // Excludes TREE
    BISON((short) 0x0004, (short) (0x0001 | 0x0002 | 0x0008 | 0x0010)), // Excludes TREE
    BIRD((short) 0x0008, (short) (0x0001 | 0x0002 | 0x0004 | 0x0010)), // Excludes TREE
    WILDANIMAL((short) 0x0010, (short) (0x0001 | 0x0002 | 0x0004 | 0x0008)), // Excludes TREE
    TREE((short) 0x0020, (short) 0); // No collisions with anything

    private final short categoryBits;
    private final short maskBits;

    ContactType(short categoryBits, short maskBits) {
        this.categoryBits = categoryBits;
        this.maskBits = maskBits;
    }

    public short getCategoryBits() {
        return categoryBits;
    }

    public short getMaskBits() {
        return maskBits;
    }
}
