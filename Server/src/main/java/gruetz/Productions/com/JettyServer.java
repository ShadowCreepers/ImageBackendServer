package gruetz.Productions.com;

import com.fasterxml.jackson.databind.ObjectMapper;
import gruetz.Productions.com.handler.FolderHandler;
import gruetz.Productions.com.handler.ImageHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class JettyServer {
    private static final Map<String, Object> configurationFile = getConfiguration();
    public static void main(String[] args ) throws Exception {
        Server server = new Server((Integer) configurationFile.get("port"));

        setServlet(server);

        server.start();
        server.join();
    }

    private static Map<String, Object> getConfiguration() {
        Map<String, Object> result = new HashMap<>();

        File file = new File("Server/src/main/resources/http.json");

        try {
            result = new ObjectMapper().readValue(file, HashMap.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    private static void setServlet(Server server) {
        ServletHandler handler = new ServletHandler();
        handler.addServletWithMapping(ImageHandler.class, "/image");
        handler.addServletWithMapping(FolderHandler.class, "/folders");
        server.setHandler(handler);
    }
}
