package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.stream.Collectors;


@Repository("SOvFDownloader")
public class SOvFDataDownloader implements LanguageDataDownloader {

    private final String URL;
    private final String FILE_NAME;
    private final FileDataWriter fileDataWriter;

    @Autowired
    public SOvFDataDownloader(FileDataWriter fileDataWriter,
                              @Value("${sources.stovf.url}") String url,
                              @Value("${sources.stovf.filename}") String fileName) {
        this.fileDataWriter = fileDataWriter;
        this.URL = url;
        this.FILE_NAME = fileName;
    }

    private String getStringData() {
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new URL(URL).openStream()))) {
            return bufferedReader.lines().collect(Collectors.joining());
        } catch (IOException e) {
            throw new RuntimeException("DownloadException (Can't download data from Stack Overflow)",e);
        }
    }

    @Override
    public String getData() {
        return getStringData();
    }

    @Override
    public void saveDataInPlainFormat() {
        fileDataWriter.writeData(FILE_NAME, getStringData());
    }
}
