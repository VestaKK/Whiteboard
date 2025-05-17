import WhiteBoard.WhiteboardServer;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

import java.io.IOError;
import java.io.IOException;
import java.net.ServerSocket;

public class ServerMain {

   public static void main(String[] args) {

       // Build Arguments
       ArgumentParser parser = ArgumentParsers.newFor("WhiteboardServer").build()
               .defaultHelp(true)
               .description("Whiteboard Server");
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
       Integer port = ns.getInt("<port>");

       try (ServerSocket serverSocket = new ServerSocket(port)) {
           WhiteboardServer server = new WhiteboardServer(serverSocket);
           server.start();
       } catch (IOException e) {
           System.out.println(e.getMessage());
           System.exit(1);
       }
   }
}
