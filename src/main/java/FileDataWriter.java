import java.io.*;

public class FileDataWriter {
    //    final private String DEFAULT_DIR_PATH = "./sources";
    final File defaultDirectory = new File("./LanguageDataSet");

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
