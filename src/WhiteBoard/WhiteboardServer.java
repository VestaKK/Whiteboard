package WhiteBoard;

import WhiteBoard.Payloads.*;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class WhiteboardServer {

    private ServerSocket socket;

    boolean initialised;
    private BufferedImage imageState;
    private Graphics2D graphics2D;
    private final ServerPool pool;

    private Map<Integer, Integer> userEntryMap;
    private List<UserProfile> users;

    List<Socket> clients;
    final ReentrantLock lock;
    final Condition clientAdded;

    private static final int HOST_ID = 0;
    private BufferedImage state;

    public WhiteboardServer(ServerSocket socket ) {
        this.socket = socket;
        this.lock = new ReentrantLock();
        this.clientAdded = this.lock.newCondition();
        this.clients = new LinkedList<>();
        this.pool = new ServerPool(this, 20);

        this.userEntryMap = new HashMap<>();
        this.users = new ArrayList<>();

        this.initialised = false;
        this.imageState = null;
        this.graphics2D = null;
    }

    public void start() {
        for (;;) {
            // Listen for connections continuously
            try {
                Socket client = socket.accept();

                // Add client to queue and request a thread to pick up the connection
                lock.lock();
                clients.add(client);
                clientAdded.signal();
                lock.unlock();
            } catch (IOException e ) {
                System.out.println(e.getMessage());
            }
        }
    }

    public void addUser(UserProfile profile) {
       userEntryMap.put(profile.id, users.size());
       users.add(profile);
    }

    public boolean removeUser(int id) {
        if (!initialised) return false;
        if (!userEntryMap.containsKey(id)) {
            return false;
        }

        Integer index = userEntryMap.get(id);
        if (index.equals(users.size() - 1)) {
            userEntryMap.remove(index);
            users.removeLast();
            return true;
        }
        UserProfile last = users.getLast();
        users.set(index, last);
        users.removeLast();
        userEntryMap.replace(last.id, index);
        return true;
    }

    public synchronized UserProfile initServerThread(ServerThread serverThread) throws IOException, ClassNotFoundException {
        Operation op = Operation.convertToOperation(serverThread.inputStream.readByte());
        if (op.getValue() != Operation.JOIN.getValue()) {
            serverThread.outputStream.writeByte(Message.ERROR.getValue());
            Operation leaveOp = Operation.convertToOperation(serverThread.inputStream.readByte());
            assert(leaveOp == Operation.LEAVE);
            return null;
        }

        JoinRequest joinRequest = (JoinRequest) serverThread.inputStream.readObject();
        if (joinRequest.isHost && initialised) {
            serverThread.sendMessage(Message.HOST_EXISTS);
            Operation leaveOp = Operation.convertToOperation(serverThread.inputStream.readByte());
            assert(leaveOp == Operation.LEAVE);
            return null;
        }

        if (!joinRequest.isHost && !initialised) {
            serverThread.sendMessage(Message.NO_ROOM);
            Operation leaveOp = Operation.convertToOperation(serverThread.inputStream.readByte());
            assert(leaveOp == Operation.LEAVE);
            return null;
        }

        if (joinRequest.isHost) {
            initialised = true;
            imageState = new BufferedImage(joinRequest.width, joinRequest.height, BufferedImage.TYPE_INT_ARGB);
            graphics2D = imageState.createGraphics();
            graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics2D.setPaint(Color.white);
            graphics2D.fillRect(0, 0, imageState.getWidth(), imageState.getHeight());
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(imageState, "png", baos);
        UserProfile profile = new UserProfile(users.size(), joinRequest.name, joinRequest.isHost);
        serverThread.sendMessage(Message.SUCCESS, new InitResponse(baos.toByteArray(), profile, users));
        pool.broadcastMessage(Message.ADD_USER, profile);
        addUser(profile);
        return profile;
    }

    public synchronized void drawFree(DrawFree p) {
        if (!initialised) return;
        graphics2D.setColor(p.drawColour);
        for (Point point : p.points) {
            graphics2D.fillRect(point.x - p.cursorSize/2, point.y - p.cursorSize/2, p.cursorSize, p.cursorSize);
        }
    }

    public synchronized void drawShape(DrawShape p) {
        if (!initialised) return;
        Utilities.drawShape(
                graphics2D,
                p.shapeType,
                p.drawColour,
                p.fillColour,
                p.cursorSize,
                p.shouldFill,
                p.x1, p.y1,
                p.x2, p.y2
        );
    }

    public synchronized void reset() {
        initialised = false;
        imageState = null;
        graphics2D = null;
        users = new ArrayList<>();
        userEntryMap = new HashMap<>();
    }

    public synchronized void loadBoard(byte[] newBoard) throws IOException {
        if (!initialised) return;
        imageState = null;
        graphics2D = null;

        ByteArrayInputStream bais = new ByteArrayInputStream(newBoard);
        imageState = ImageIO.read(bais);
        graphics2D = imageState.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.setPaint(Color.white);
        graphics2D.fillRect(0, 0, imageState.getWidth(), imageState.getHeight());
    }
}
