package WhiteBoard.Payloads;

public enum Response {
    NON_RESULT((byte)-1),
    ERROR((byte)0),
    SUCCESS((byte)1),
    HOST_EXISTS((byte)2),
    NO_ROOM((byte)3),
    DRAW_SHAPE((byte)4),
    DRAW_FREE((byte)5);

    private final byte value;

    Response(byte value) {
        this.value = value;
    }

    public static Response convertToResponse(byte value) {
        switch (value) {
            case 0 -> { return ERROR; }
            case 1 -> { return SUCCESS; }
            case 2 -> { return HOST_EXISTS; }
            case 3 -> { return NO_ROOM; }
            case 4 -> { return DRAW_SHAPE; }
            case 5 -> { return DRAW_FREE; }
        }
        return NON_RESULT;
    }

    public byte getValue() {
        return this.value;
    }
}
