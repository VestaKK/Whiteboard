package WhiteBoard.Payloads;

import java.io.Serializable;
import java.util.List;

public class InitResponse implements Serializable {
    public byte[] boardData;
    public UserProfile client;
    public List<UserProfile> userProfiles;
    public InitResponse(byte[] boardData, UserProfile client, List<UserProfile> userProfiles) {
        this.boardData = boardData;
        this.client = client;
        this.userProfiles = userProfiles;
    }
}
