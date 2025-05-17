package WhiteBoard.Payloads;

import java.io.Serializable;

public class UserProfile implements Serializable {
    public int id;
    public String name;
    public boolean isHost;

    public UserProfile(int id, String name, boolean isHost) {
        this.id = id;
        this.name = name;
        this.isHost = isHost;
    }
}
