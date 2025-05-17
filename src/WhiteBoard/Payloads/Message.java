package WhiteBoard.Payloads;

public enum Message {
    NON_RESULT((byte)-1),
    ERROR((byte)0),
    SUCCESS((byte)1),
    HOST_EXISTS((byte)2),
    NO_ROOM((byte)3),
    DRAW_SHAPE((byte)4),
    DRAW_FREE((byte)5),
    RELOAD((byte)6),
    ADD_USER((byte)8),
    KICKED((byte)9),
    NEW_MSG((byte)10);

    private final byte value;

    Message(byte value) {
        this.value = value;
    }

    public static Message convertToMessage(byte value) {
        switch (value) {
            case 0 -> { return ERROR; }
            case 1 -> { return SUCCESS; }
            case 2 -> { return HOST_EXISTS; }
            case 3 -> { return NO_ROOM; }
            case 4 -> { return DRAW_SHAPE; }
            case 5 -> { return DRAW_FREE; }
            case 6 -> { return RELOAD; }
            case 8 -> { return ADD_USER; }
            case 9 -> { return KICKED; }
            case 10 -> { return NEW_MSG; }
        }

        return NON_RESULT;
    }

    public byte getValue() {
        return this.value;
    }
}
