package app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository("TiobeDownloader")
public class TiobeDataDownloader implements LanguageDataDownloader {

    private final String URL;
    private final String FILE_NAME;
    private static Pattern plainCutPattern = Pattern.compile("\\{name :.*}");

    private final FileDataWriter fileDataWriter;

    @Autowired
    public TiobeDataDownloader(FileDataWriter fileDataWriter,
                               @Value("${sources.tiobe.url}") String url,
                               @Value("${sources.tiobe.filename}") String fileName) {
        this.fileDataWriter = fileDataWriter;
        this.URL = url;
        this.FILE_NAME = fileName;
    }

    private  Document getDocument() {
        try {
            return Jsoup.connect(URL)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            throw new RuntimeException("DownloadException (Can't download data from TIOBE)", e);
        }
    }

    private String getStringData() {
        return getDocument().select("script").stream()
                .flatMap(element -> element.dataNodes().stream())
                .filter(dataNode -> dataNode.getWholeData().contains("$(function () {"))
                .map(DataNode::getWholeData)
                .collect(Collectors.joining());
    }

    private String getPlainData() {
        Matcher matcher = plainCutPattern.matcher(getStringData());
        StringBuilder stringBuilder = new StringBuilder();
        while (matcher.find()) {
            stringBuilder.append(matcher.group());
        }
        return stringBuilder.toString();
    }

    @Override
    public String getData() {
        return getPlainData();
    }

    @Override
    public void saveDataInPlainFormat() {
        fileDataWriter.writeData(FILE_NAME, getPlainData());
    }
}
