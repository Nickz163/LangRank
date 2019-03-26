import com.sun.tools.internal.xjc.Language;
import javafx.util.Pair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PYPLRatingParser {
    final private static String PYPL_ALL_URL = "https://raw.githubusercontent.com/pypl/pypl.github.io/master/PYPL/All.js";

    public static PYPLRatingParser getInstance() {
        return new PYPLRatingParser();
    }

    public void parseData() {
        try {

            Document document = Jsoup.connect(PYPL_ALL_URL)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();

            String data = dataParser.apply(document.text());
            System.out.println(data);


        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private Function<String, String> dataParser = str -> {
        //Pattern pattern = Pattern.compile("name : .*,data : .*(?=}\n)");
        Pattern pattern = Pattern.compile("(?<= graphData = ).*(?=;)");
        Pattern deleteCommentsPattern = Pattern.compile("//.*? (?='|]|\\[)"); // check lazy quantification

        Matcher matcher = pattern.matcher(str);

        String newStr = "";
        while (matcher.find()) {
            newStr = str.substring(matcher.start(), matcher.end());
        }
        Matcher deleterMatcher = deleteCommentsPattern.matcher(newStr);
        return deleterMatcher.replaceAll("");
        //  return newStr;
    };
}
