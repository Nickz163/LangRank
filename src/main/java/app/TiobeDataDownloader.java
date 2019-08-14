package app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TiobeDataDownloader implements LanguageDataDownloader {

    final private static String TIOBE_URL = "https://tiobe.com/tiobe-index/"; // warning!
    private static Document document;
    private static Pattern plainCutPattern = Pattern.compile("\\{name :.*}");

    public static TiobeDataDownloader getInstance() { //Todo: возможно вынести в метод connect
        try {
            document = Jsoup.connect(TIOBE_URL)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TiobeDataDownloader();
    }


    private Supplier<String> dataSupplier = () -> document.select("script").stream()
            .flatMap(element -> element.dataNodes().stream())
            .filter(dataNode -> dataNode.getWholeData().contains("$(function () {"))
            .map(DataNode::getWholeData)
            .collect(Collectors.joining());


    private Function<String, String> plainDataParser = str -> {
        Pattern pattern = Pattern.compile("\\{name :.*}"); // problems with khanda
        Matcher matcher = pattern.matcher(str);
        StringBuilder stringBuilder = new StringBuilder();

        while (matcher.find()) {
            stringBuilder.append(matcher.group());
        }
        return stringBuilder.toString();
    };
    
    private String getPlainData(String str) {
        Matcher matcher = plainCutPattern.matcher(str);
        StringBuilder stringBuilder = new StringBuilder();
        while(matcher.find()) {
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
        // default JSON from TIOBE (temporaty txt)
//        FileDataWriter.getInstance().writeData("TiobeWholeData.txt", plainDataParser.apply(dataSupplier.get()));
        FileDataWriter.getInstance().writeData("TiobeWholeData.txt", getPlainData(dataSupplier.get()));
    }
}
