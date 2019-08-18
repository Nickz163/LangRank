package app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Repository("TiobeDownloader")
public class TiobeDataDownloader implements LanguageDataDownloader {

    final private static String TIOBE_URL = "https://tiobe.com/tiobe-index/";
    private static Pattern plainCutPattern = Pattern.compile("\\{name :.*}"); // problems with khanda

    @Autowired
    private FileDataWriter fileDataWriter;



    private Supplier<Document> documentSupplier = () -> {
        try {
            Document document = Jsoup.connect(TIOBE_URL)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
            return document;
        } catch (IOException e) {
            throw new RuntimeException("DownloadException (Can't download data from TIOBE)");
        }
    };


    private Supplier<String> dataSupplier = () -> documentSupplier.get().select("script").stream()
            .flatMap(element -> element.dataNodes().stream())
            .filter(dataNode -> dataNode.getWholeData().contains("$(function () {"))
            .map(DataNode::getWholeData)
            .collect(Collectors.joining());


    private String getPlainData(String str) {
        Matcher matcher = plainCutPattern.matcher(str);
        StringBuilder stringBuilder = new StringBuilder();
        while (matcher.find()) {
            stringBuilder.append(matcher.group());
        }
        return stringBuilder.toString();
    }


    @Override
    public String getData() {
        return getPlainData(dataSupplier.get());
    }


    @Override
    public void saveDataInPlainFormat() {
        fileDataWriter.writeData("TiobeWholeData.txt", getPlainData(dataSupplier.get()));
    }
}
