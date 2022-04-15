package gruetz.Productions.com.handler;

import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FolderHandler extends HttpServlet {
    private final String rootPath = "./Images";
    private final File rootFile = new File(rootPath);
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Map<String, Object>> list = new ArrayList<>();

        if (!rootFile.exists()) {
            rootFile.mkdir();
        }

        String[] files = rootFile.list();
        for (String fileName : files) {
            Map<String, Object> fileMap = new HashMap<>();
            List<Map<String, Object>> childFileList = new ArrayList<>();
            File childFile = new File(rootPath + "\\" + fileName);
            String[] childFiles = childFile.list();
            int PicCount = 0;
            for (String s : childFiles) {
                File sFile = new File(childFile.getAbsolutePath() + "\\" + s);
                if (sFile.exists() && !sFile.isDirectory()) {
                    PicCount++;
                }
            }

            for (String s : childFiles) {
                File file = new File(rootPath + "\\" + fileName + "\\" + s);


                if (file.isDirectory()) {
                    continue;
                }

                byte[] fileContent = FileUtils.readFileToByteArray(file);

                String encodedString = Base64.getEncoder().encodeToString(fileContent);
                Map<String, Object> imageMap = new HashMap<>();
                String[] sSplit = s.split("\\.");
                imageMap.put("data", encodedString);
                imageMap.put("name", sSplit[0]);
                imageMap.put("type", sSplit[1]);
                childFileList.add(imageMap);
            }

            Path filePath = Paths.get(childFile.getAbsolutePath());
            BasicFileAttributes attr = Files.readAttributes(filePath, BasicFileAttributes.class);
            Date date = new Date(attr.creationTime().to(TimeUnit.MILLISECONDS));
            fileMap.put("name", fileName);
            fileMap.put("PicCount", PicCount);
            fileMap.put("CreationDate", date.getDate() + "." + (date.getMonth() + 1) + "." + (date.getYear() + 1900));
            fileMap.put("childMaps", childFileList);
            list.add(fileMap);
        }

        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().println(gson.toJson(list));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        BufferedReader reader = req.getReader();
        StringBuilder sb = new StringBuilder();

        if (!rootFile.exists()) {
            rootFile.mkdir();
        }

        while (reader.ready()) {
            sb.append(reader.readLine().trim());
        }

        Map<String, Object> map = gson.fromJson(sb.toString(), Map.class);
        String folderName = (String) map.get("name");
        File file = new File(rootPath + "\\" + folderName);
        if (!file.exists()) {
            file.mkdir();
        }
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus(HttpServletResponse.SC_OK);
    }
}
