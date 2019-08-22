package app;

import org.springframework.stereotype.Repository;

import java.io.*;

@Repository
public class FileDataWriter {
    final private String DEFAULT_DIR_PATH = System.getProperty("user.dir") + File.separator + "LanguageDataSet";
    private final File defaultDirectory = new File(DEFAULT_DIR_PATH);

    public void writeData(String name, String data) {
        File writableFile = new File(defaultDirectory, name);
        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(writableFile)))) {
            writer.write(data);
        } catch (IOException e) {
            throw new RuntimeException("Can't write data in file (check FileDataWriter)");
        }
    }
}
