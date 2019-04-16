import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PYPLRatingParser implements LanguageRatingParser {
    final private static String PYPL_ALL_URL = "https://raw.githubusercontent.com/pypl/pypl.github.io/master/PYPL/All.js";
    private static Document document;

    public static PYPLRatingParser getInstance() {
        try {
            document = Jsoup.connect(PYPL_ALL_URL)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new PYPLRatingParser();
    }

    @Override
    public List<LanguageDataPrototype> parseWholeData() {
        List<LanguageDataPrototype> languages = new ArrayList<>();
           // String data = directFormatParser.andThen(wholeDataParser).apply(document.text());
            //System.out.println(data);
            languages = directFormatParser.andThen(wholeDataParser).apply(document.text());

        return languages;
    }

    @Override
    public void saveDataInPlainFormat() {
        FileDataWriter.getInstance().writeData("PYPLWholeData.txt", document.text()); //plain data
    }




    /**
     * Plain data parsing with little adjustments
     */
    private Function<String, String> directFormatParser = str -> {
        //Pattern pattern = Pattern.compile("name : .*,data : .*(?=}\n)");
        Pattern pattern = Pattern.compile("(?<= graphData = ).*(?=;)");
        Pattern deleteCommentsPattern = Pattern.compile("//.*? (?='|]|\\[)"); // check lazy quantification
        Matcher matcher = pattern.matcher(str);

        String newStr = "";
        while (matcher.find()) { // TODO: проверить этот момент
           // newStr = str.substring(matcher.start(), matcher.end());
            newStr = matcher.group();
        }
        Matcher deleterMatcher = deleteCommentsPattern.matcher(newStr);
//       return deleterMatcher.replaceAll("").replaceAll("'", "");
        return deleterMatcher.replaceAll("").replaceAll("'Date', ", "");
        //  return newStr;
    };


    private Function<String, List<LanguageDataPrototype>> wholeDataParser = str -> {

        Pattern namePattern = Pattern.compile("(?<=(\\[|\\s)').*?(?=')"); // lazy quantification
        //Pattern datePattern = Pattern.compile("(?<=Date\\().*\\)");
        Pattern datePattern = Pattern.compile("(?<=Date\\().*?(?=\\))"); // lazy again
        Pattern valuePattern = Pattern.compile("(0\\.\\d+)");//warning
        // Pattern valuePattern = Pattern.compile("\\[new.*?]");


        Matcher nameMatcher = namePattern.matcher(str);
        Matcher dateMatcher = datePattern.matcher(str);
        Matcher valueMatcher = valuePattern.matcher(str);


        List<LanguageDataPrototype> languages = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<Double> values = new ArrayList<>();


        while (nameMatcher.find()) {
            languages.add(new LanguageDataPrototype(nameMatcher.group()));
        }
        while (dateMatcher.find())
            dates.add(dateMatcher.group());

        while (valueMatcher.find())
            values.add(Double.parseDouble(valueMatcher.group()));


        for (int i = 0; i < languages.size(); i++)
            for (int k = i; k < values.size(); k += languages.size())
                languages.get(i).appendData(dates.get(k / languages.size()), values.get(k));//// check


       // languages.forEach(a -> a.printLang());
        //languages.forEach(a -> a.printDataInfo());


        return languages; // old string
    };

}
