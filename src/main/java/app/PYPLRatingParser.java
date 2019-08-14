package app;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PYPLRatingParser implements LanguageRatingParser {

    private final String SOURCE_NAME = "PYPL";
    private LanguageDataDownloader dataDownloader;

    public PYPLRatingParser() {
        dataDownloader = PYPLDataDownloader.getInstance();
    }

    public static PYPLRatingParser getInstance() {
        return new PYPLRatingParser();
    }

    @Override
    public List<LanguageDataPrototype> parseWholeData() {
        String data = dataDownloader.getData();
        List<LanguageDataPrototype> languages = directFormatParser.andThen(wholeDataParser).apply(data);
        return languages;
    }


    private static final Pattern DIRECT_PATTERN = Pattern.compile("(?<= graphData = ).*(?=;)");
    private static final Pattern DELETE_COMMENTS_PATTERN = Pattern.compile("//.*? (?='|]|\\[)"); // check lazy quantification

    /**
     * Plain data parsing with few adjustments
     */
    private Function<String, String> directFormatParser = str -> {

        Matcher matcher = DIRECT_PATTERN.matcher(str);
        String newStr = "";
        while (matcher.find()) {
            // newStr = str.substring(matcher.start(), matcher.end());
            newStr = matcher.group();
        }
        Matcher deleterMatcher = DELETE_COMMENTS_PATTERN.matcher(newStr);
        return deleterMatcher.replaceAll("").replaceAll("'Date', ", "");
        //  return newStr;
    };



    private static final  Pattern NAME_PATTERN = Pattern.compile("(?<=(\\[|\\s)').*?(?=')"); // lazy quantification
    private static final Pattern DATE_PATTERN = Pattern.compile("(?<=Date\\().*?(?=\\))"); // lazy again
    private static final Pattern VALUE_PATTERN = Pattern.compile("(0\\.\\d+)");


    private Function<String, List<LanguageDataPrototype>> wholeDataParser = str -> {

        Matcher nameMatcher = NAME_PATTERN.matcher(str);
        Matcher dateMatcher = DATE_PATTERN.matcher(str);
        Matcher valueMatcher = VALUE_PATTERN.matcher(str);

        List<LanguageDataPrototype> languages = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        while (nameMatcher.find()) {
            languages.add(new LanguageDataPrototype(nameMatcher.group(), SOURCE_NAME));
        }
        while (dateMatcher.find())
            dates.add(dateMatcher.group());

        while (valueMatcher.find())
            values.add(Double.parseDouble(valueMatcher.group()));

        for (int i = 0; i < languages.size(); i++)
            for (int k = i; k < values.size(); k += languages.size())
                languages.get(i).appendData(dates.get(k / languages.size()), values.get(k));//// check


        return languages; // old string
    };

}
