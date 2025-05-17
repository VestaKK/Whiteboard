import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;

public class ClientMain {
    public static void main(String[] args) {
        // Build Arguments
        ArgumentParser parser = ArgumentParsers.newFor("JoinWhiteBoard").build()
                .defaultHelp(true)
                .description("Dictionary Client");
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
    }
}