
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;


import java.io.IOException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TiobeRatingParser {
    final private static String TIOBE_URL = "https://tiobe.com/tiobe-index/"; // warning!

    public static TiobeRatingParser getInstance() {
        return new TiobeRatingParser();
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

//    Function <String,Json>

}
