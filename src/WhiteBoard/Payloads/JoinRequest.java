package WhiteBoard.Payloads;

import java.io.Serializable;

public class JoinRequest implements Serializable {
    public boolean isHost;
    public String name;
    public int width, height;
    public JoinRequest(boolean isHost, String name, int width, int height) {
        this.isHost = isHost;
        this.name = name;
        this.width = width;
        this.height = height;
    }
}
