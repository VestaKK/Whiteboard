package WhiteBoard;

import WhiteBoard.GUI.Whiteboard;
import WhiteBoard.Payloads.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class WhiteboardClient {

    public enum Role {
        HOST,
        JOIN,
    }


    private Whiteboard whiteboard;
    private Role role;
    private Socket socket;
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    private UserProfile clientProfile;
    private List<UserProfile> others;

    public WhiteboardClient(Socket socket, Role role) throws IOException, ClassNotFoundException {
        this.socket = socket;
        this.role = role;
        this.outputStream = new ObjectOutputStream(this.socket.getOutputStream());
        this.outputStream.flush();
        this.inputStream = new ObjectInputStream(this.socket.getInputStream());
        this.whiteboard = new Whiteboard(new Dimension(1500, 1000));
        Dimension wD = this.whiteboard.getActualDimensions();

        sendOperation(
                Operation.JOIN,
                new JoinRequest(
                        role == Role.HOST,
                        "Joseph",
                        (int)wD.getWidth(),
                        (int)wD.getHeight()
                ));

        Message message = Message.convertToMessage(this.inputStream.readByte());
        if (message != Message.SUCCESS) {

            switch (message) {
                case ERROR -> {
                    JOptionPane.showMessageDialog(whiteboard, "Protocol Violated");
                }
                case HOST_EXISTS -> {
                    JOptionPane.showMessageDialog(whiteboard, "A Host already exists");
                }
                case NO_ROOM -> {
                    JOptionPane.showMessageDialog(whiteboard, "No host exists for this room");
                }
            }
            sendOperation(Operation.LEAVE);
            close();
        }

        if (role == Role.HOST) {
            InitResponse initResponse = (InitResponse) this.inputStream.readObject();
            this.clientProfile = initResponse.client;
            this.others = initResponse.userProfiles;
//            System.out.println(this.clientProfile.name);
//            System.out.println(others);

            ByteArrayInputStream bais = new ByteArrayInputStream(initResponse.boardData);
            this.whiteboard.loadCanvas(ImageIO.read(bais));
            this.whiteboard.setFocusableWindowState(true);
        }
    }

    private void close() throws IOException {
        this.whiteboard.close();
        this.outputStream.close();
        this.inputStream.close();
        this.socket.close();
    }

    private synchronized <T> void sendOperation(Operation op, T payload) throws IOException {
        this.outputStream.writeByte(op.getValue());
        this.outputStream.writeObject(payload);
        this.outputStream.flush();
    }

    private synchronized  void sendOperation(Operation op) throws IOException {
        this.outputStream.writeByte(op.getValue());
        this.outputStream.flush();
    }

    public void start() throws IOException {
        for (;;) {
            Message message = Message.convertToMessage(this.inputStream.readByte());
        }
    }

}
