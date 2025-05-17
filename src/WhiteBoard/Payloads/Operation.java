package WhiteBoard.Payloads;

public enum Operation {
    NON_OP((byte)-1),
    JOIN((byte)0),
    LEAVE((byte)1),
    KICK((byte)2),
    DRAW_SHAPE((byte)3),
    DRAW_FREE((byte)4),
    LOAD_BOARD((byte)5),
    SEND_MESSAGE((byte)6),
    CLOSE((byte)7);


    private final byte value;
    Operation(byte value) {
        this.value = value;
    }

    public static Operation convertToOperation(byte value) {
        switch (value) {
            case 0 -> { return JOIN; }
            case 1 -> { return LEAVE; }
            case 2 -> { return KICK; }
            case 3 -> { return DRAW_SHAPE; }
            case 4 -> { return DRAW_FREE; }
            case 5 -> { return LOAD_BOARD; }
            case 6 -> { return SEND_MESSAGE; }
            case 7 -> { return CLOSE; }
        }

        return NON_OP;
    }

    public byte getValue() {
        return this.value;
    }
}
