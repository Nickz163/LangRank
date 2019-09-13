package app.downloaders;

import app.repository.FileDataWriter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;


@Repository("PYPLDownloader")
public class PYPLDataDownloader implements LanguageDataDownloader {

    private final String URL;
    private final String FILE_NAME;
    private final FileDataWriter fileDataWriter;

    @Autowired
    public PYPLDataDownloader(FileDataWriter fileDataWriter,
                              @Value("${sources.pypl.url}") String url,
                              @Value("${sources.pypl.filename}") String fileName) {
        this.fileDataWriter = fileDataWriter;
        this.URL = url;
        this.FILE_NAME = fileName;
    }

    private Document getDocument() {
        try {
            return Jsoup.connect(URL)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException("DownloadException (Can't download data from PYPL)", e);
        }
    }

    @Override
    public String getData() {
        return getDocument().text();
    }

    @Override
    public void saveDataInPlainFormat() {
        fileDataWriter.writeData(FILE_NAME, getData());
    }
}
