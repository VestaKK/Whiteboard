package WhiteBoard;

import WhiteBoard.Payloads.*;

import java.io.*;
import java.net.Socket;
import java.nio.channels.ClosedSelectorException;

public class ServerThread implements Runnable {

    Socket connection;
    int activeThreadIndex;
    private WhiteboardServer server;
    private ServerPool pool;

    ObjectOutputStream outputStream;
    ObjectInputStream inputStream;

    public ServerThread(
            WhiteboardServer server,
            ServerPool pool
    ) {
        this.server = server;
        this.pool = pool;
    }

    private UserProfile clientProfile;

    public void run() {
        for (;;) {
            server.lock.lock();
            if (server.clients.isEmpty()) {
                try {
                    System.out.println("WAITING");
                    server.clientAdded.await();
                } catch(InterruptedException e) {
                    System.out.println(e.getMessage());
                    Thread.currentThread().interrupt();
                    continue;
                }
            }
            this.connection = server.clients.removeFirst();
            this.server.lock.unlock();

            try {
                if (initSession()) {
                    handleClient();
                    deInitSession();
                    reset();
                }
                reset();
            } catch (ClassNotFoundException e) {
                System.out.println(e.getMessage());
            } catch (IOException e) {
                System.out.println("HAHAHAH");
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean initSession() throws IOException, ClassNotFoundException {
        outputStream = new ObjectOutputStream(connection.getOutputStream());
        outputStream.flush();
        inputStream = new ObjectInputStream(connection.getInputStream());
        clientProfile = pool.joinActiveThreads(this);
        return clientProfile != null;
    }


    private void deInitSession() throws IOException, ClassNotFoundException {
       pool.removeFromActiveThreads(this, clientProfile);
       if (clientProfile != null && clientProfile.isHost) {
          server.reset();
          pool.broadcastMessage(Message.KICKED, clientProfile.id);
       }
    }

    private void reset() throws IOException {
        outputStream.close();
        inputStream.close();
        connection.close();
        clientProfile = null;
        inputStream = null;
        outputStream = null;
        connection = null;
    }

    private void handleClient() {
        boolean shouldQuit = false;
        while (connection.isConnected() && !shouldQuit) {
            try {
                Operation op = Operation.convertToOperation(inputStream.readByte());
                switch (op) {
                    case DRAW_FREE -> {
                        handleDrawFree();
                    }
                    case DRAW_SHAPE -> {
                        handleDrawShape();
                    }
                    case KICK -> {
                        handleKick();
                    }
                    case LOAD_BOARD -> {
                        handleLoad();
                    }
                    case SEND_MESSAGE -> {
                        handleMessage();
                    }
                    case CLOSE -> {
                        shouldQuit = true;
                    }
                    default -> {}
                }
            }  catch(ClassNotFoundException e) {
                shouldQuit = true;
                System.out.println("Client did not send the correct data over for a given procedure");
            }
            catch (IOException e) {
                shouldQuit = true;
                System.out.println("Issue with connection of client quit unexpectedly");
            }
        }
    }

    public synchronized <T> void sendMessage(Message message, T payload) throws IOException {
        this.outputStream.writeByte(message.getValue());
        this.outputStream.writeObject(payload);
        this.outputStream.flush();
    }

    public synchronized void sendError(String message) throws IOException {
        this.outputStream.writeByte(Message.ERROR.getValue());
        this.outputStream.writeUTF(message);
        this.outputStream.flush();
    }

    public synchronized void sendMessage(Message message) throws IOException {
        this.outputStream.writeByte(message.getValue());
        this.outputStream.flush();
    }

    private void handleDrawFree()
            throws IOException,
            ClassNotFoundException
    {
        DrawFree payload = (DrawFree) inputStream.readObject();
        server.drawFree(payload);
        pool.broadcastMessage(Message.DRAW_FREE, payload);
    }

    private void handleDrawShape()
            throws IOException,
            ClassNotFoundException
    {
        DrawShape payload = (DrawShape) inputStream.readObject();
        server.drawShape(payload);
        pool.broadcastMessage(Message.DRAW_SHAPE, payload);
    }

    private void handleKick()
            throws IOException,
            ClassNotFoundException
    {
        if (!clientProfile.isHost) {
            sendError("You are not the host");
            return;
        }

        int kickedID = inputStream.readInt();
        boolean kicked = server.removeUser(kickedID);
        if (!kicked) {
            sendError("User does not exist");
        }
        sendMessage(Message.SUCCESS);
        pool.broadcastMessage(Message.KICKED, kickedID);
    }

    private void handleLoad()
            throws IOException,
            ClassNotFoundException
    {
        if (!clientProfile.isHost) {
            sendError("You are not the host");
            return;
        }
        byte[] boardData = (byte[]) this.inputStream.readObject();
        server.loadBoard(boardData);
        this.pool.broadcastMessage(Message.RELOAD, boardData);
    }

    private void handleMessage()
            throws IOException
    {
        String message = this.inputStream.readUTF();
        this.pool.broadcastMessage(Message.NEW_MSG, new MessageResponse(clientProfile.id, message));
    }
}
