package app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

public class PYPLDataDownloader implements LanguageDataDownloader {

    final private static String PYPL_ALL_URL = "https://raw.githubusercontent.com/pypl/pypl.github.io/master/PYPL/All.js";
    private static Document document;

    public static PYPLDataDownloader getInstance() {
        try {
            document = Jsoup.connect(PYPL_ALL_URL)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new PYPLDataDownloader();
    }


    @Override
    public String getData() {
        return document.text();
    }

    @Override
    public void saveDataInPlainFormat() {
        FileDataWriter.getInstance().writeData("PYPLWholeData.txt", document.text()); //plain data
    }
}
