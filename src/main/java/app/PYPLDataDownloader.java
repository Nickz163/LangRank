package app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.function.Supplier;


@Repository("PYPLDownloader")
public class PYPLDataDownloader implements LanguageDataDownloader {

    private static final String PYPL_ALL_URL = "https://raw.githubusercontent.com/pypl/pypl.github.io/master/PYPL/All.js";
    private final FileDataWriter fileDataWriter;

    @Autowired
    public PYPLDataDownloader(FileDataWriter fileDataWriter) {
        this.fileDataWriter = fileDataWriter;
    }

    private Supplier<Document> documentSupplier = () -> {
        try {
            return Jsoup.connect(PYPL_ALL_URL)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException("DownloadException (Can't download data from PYPL)");
        }
    };

    @Override
    public String getData() {
        return documentSupplier.get().text();
    }

    @Override
    public void saveDataInPlainFormat() {
        fileDataWriter.writeData("PYPLWholeData.txt", getData()); //plain data
    }
}
