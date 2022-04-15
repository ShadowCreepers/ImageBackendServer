package gruetz.Productions.com.handler;

import com.google.gson.Gson;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import org.codehaus.plexus.util.Base64;
import java.util.Map;

public class ImageHandler extends HttpServlet {
    private final String rootPath = "./Images";
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();

        File rootFile = new File(rootPath);

        if (!rootFile.exists()) {
            rootFile.mkdir();
        }

        while (reader.ready()) {
            sb.append(reader.readLine().trim());
        }

        Map<String, Object> data = gson.fromJson(sb.toString(), Map.class);
        String folder = (String) data.get("folderName");
        String fileName = (String) data.get("name");
        String fileType = (String) data.get("type");
        String[] fTSplit = fileType.split("/");
        String dataType = fTSplit[1];
        String fileData = (String) data.get("fileData");
        String[] fDSplit = fileData.split(",");
        String base64Image = fDSplit[1];
        byte[] dataByte = Base64.decodeBase64(base64Image.getBytes(StandardCharsets.UTF_8));
        BufferedImage img = ImageIO.read(new ByteArrayInputStream(dataByte));
        File outputFile = new File(rootPath + "\\" + folder + "\\" + fileName + "." + dataType);
        if (!outputFile.exists()) {
            boolean created = outputFile.createNewFile();

            if (created) {
                System.out.println("File Created: " + fileName + "." + dataType);
                boolean written = ImageIO.write(img, dataType, outputFile);

                if (written) {
                    System.out.println("File Written: " + fileName + "." + dataType);
                } else {
                    System.out.println("File couldn't be Written: " + fileName + "." + dataType);
                }
            } else {
                System.out.println("File couldn't be Created: " + fileName + "." + fileType);
            }
        }

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
