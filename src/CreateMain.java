import WhiteBoard.GUI.Whiteboard;
import WhiteBoard.WhiteboardClient;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOException;
import java.net.Socket;

public class CreateMain {
    public static void main(String[] args) {

        // Build Arguments
        ArgumentParser parser = ArgumentParsers.newFor("CreateWhiteBoard").build()
                .defaultHelp(true)
                .description("Create Whiteboard");
        parser.addArgument("<ip>")
                .help("Server IP Address")
                .type(String.class);
        parser.addArgument("<port>")
                .help("Server Port")
                .type(Integer.class);

        // Parse Arguments
        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        String ip = ns.getString("<ip>");
        Integer port = ns.getInt("<port>");


        try (Socket socket = new Socket(ip, port)) {
            WhiteboardClient client = new WhiteboardClient(socket, WhiteboardClient.Role.HOST);
            client.start();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
