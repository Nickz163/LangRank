
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TiobeRatingParser implements LanguageRatingParser {
    final private static String TIOBE_URL = "https://tiobe.com/tiobe-index/"; // warning!
    private Document document;

    public static TiobeRatingParser getInstance() {
        return new TiobeRatingParser();
    }

    @Override
    public void parseWholeData() {
        try {
            document = Jsoup.connect(TIOBE_URL)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();


            String data = document.select("script").stream()
                    .flatMap(element -> element.dataNodes().stream())
                    .filter(dataNode -> dataNode.getWholeData().contains("$(function () {"))
                    .map(DataNode::getWholeData)
                    .collect(Collectors.joining());
           // System.out.println(wholeDataParser.apply(data));
            FileDataWriter.getInstance().writeData("TiobeWholeData.txt",wholeDataParser.apply(data));

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void parseLanguage(String name) { // TODO: need to complete this method (its can wait)

        try {

            Document document = Jsoup.connect(completeURL(name))
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();


            String data = document.select("script").stream()
                    .flatMap(element -> element.dataNodes().stream())
                    .filter(dataNode -> dataNode.getWholeData().contains("$(function () {"))
                    .map(DataNode::getWholeData)
                    .collect(Collectors.joining());


            System.out.println(parseData.apply(data));


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String completeURL(String name) {
        return TIOBE_URL + name + "/"; // TODO: Refactor this
    }

    private Function<String, String> parseData = str -> { //TODO rename
        //Pattern pattern = Pattern.compile("name : .*,data : .*(?=}\n)");
        Pattern pattern = Pattern.compile("\\{name : .*,data : .*}(?=\n)");
        Matcher matcher = pattern.matcher(str);
        String newStr = "";
        while (matcher.find()) {
            newStr = str.substring(matcher.start(), matcher.end());
        }
        return newStr;
    };


    private Function<String,String> wholeDataParser = str -> {
        Pattern pattern = Pattern.compile("\\{name :.*}"); // problems with khanda
        Matcher matcher = pattern.matcher(str);
        StringBuilder stringBuilder = new StringBuilder();

        while (matcher.find()) {
            stringBuilder.append(matcher.group());
        }
        return stringBuilder.toString();
    };



}
