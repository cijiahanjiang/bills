package edu.bupt.beibei.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class FileUtil {

    public static void append(String path, String text) {
        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(path, true));
            bufferedWriter.append(text + "\n");
            bufferedWriter.flush();
            bufferedWriter.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<String> readByLine(String fileName, String charset) {
        try {
            Path path = Paths.get(fileName);
            Charset charsets = Charset.forName(charset);
            return Files.readAllLines(path, charsets);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new LinkedList<>();
    }

    public static List<String> readByLine(String fileName) {
        return readByLine(fileName, "UTF-8");
    }

    public static List<String> readByLine(MultipartFile file, String charset) {
        List<String> data = new LinkedList<>();
        String line;
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(file.getInputStream(), charset));
            while ((line = bufferedReader.readLine()) != null) {
                data.add(line);
            }
        } catch (Exception var5) {
            var5.printStackTrace();
        }
        return data;
    }

    public static List<String> readByLine(MultipartFile file) {
        return readByLine(file, "UTF-8");
    }

    public static String readAsString(String var0) {
        Exception var1;
        label18:
        {
            FileInputStream var5;
            try {
                var5 = new FileInputStream(var0);
                byte[] var2 = new byte[var5.available()];
                var5.read(var2);
                var0 = new String(var2, StandardCharsets.UTF_8);
            } catch (Exception var4) {
                var1 = var4;
                var0 = "";
                break label18;
            }

            try {
                var5.close();
                return var0;
            } catch (Exception var3) {
                var1 = var3;
            }
        }

        var1.printStackTrace();
        return var0;
    }
}
