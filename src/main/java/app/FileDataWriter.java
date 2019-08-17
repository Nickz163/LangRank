package app;

import java.io.*;

public class FileDataWriter {
       final private String DEFAULT_DIR_PATH =  System.getProperty("user.dir") + File.separator + "LanguageDataSet";

    final File defaultDirectory = new File(DEFAULT_DIR_PATH);

    public static FileDataWriter getInstance() {
        return new FileDataWriter();
    }

    public void writeData(String name, String data) {

       System.out.println( defaultDirectory.mkdir());

        File writableFile = new File(defaultDirectory, name);

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(writableFile)))) {
            writer.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
