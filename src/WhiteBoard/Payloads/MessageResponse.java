package WhiteBoard.Payloads;

import java.io.Serializable;

public class MessageResponse implements Serializable {
    public int from;
    public String message;

    public MessageResponse(int from, String message) {
        this.from = from;
        this.message = message;
    }
}
