package WhiteBoard;

import WhiteBoard.Payloads.Message;
import WhiteBoard.Payloads.UserProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ServerPool {
    private WhiteboardServer server;
    private List<ServerThread> activeThreads;

    public ServerPool(WhiteboardServer server, int capacity) {
        this.server = server;
        this.activeThreads = new ArrayList<>();
        for (int i=0; i<capacity; i++) {
            new Thread(new ServerThread(this.server, this)).start();
        }
    }

    public synchronized <T> void broadcastMessage(Message message, T payload) throws IOException {
        for (ServerThread serverThread : activeThreads) {
            serverThread.sendMessage(message, payload);
        }
    }

    public synchronized UserProfile joinActiveThreads(ServerThread serverThread) throws IOException, ClassNotFoundException {
        UserProfile profile = server.initServerThread(serverThread);

        if (profile == null) {
           return null;
        }

        broadcastMessage(Message.ADD_USER, profile);
        serverThread.activeThreadIndex = activeThreads.size();
        activeThreads.addLast(serverThread);
        return profile;
    }

    public synchronized void removeFromActiveThreads(ServerThread serverThread, UserProfile profile) throws IOException {
        if (profile != null) {
            server.removeUser(profile.id);
        }
        activeThreads.set(serverThread.activeThreadIndex, activeThreads.getLast());
        ServerThread last = activeThreads.removeLast();
        last.activeThreadIndex = serverThread.activeThreadIndex;
        serverThread.activeThreadIndex = 0;
    }
}
