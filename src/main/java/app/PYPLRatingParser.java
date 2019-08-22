package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("PYPLParser")
public class PYPLRatingParser implements LanguageRatingParser {

    private final String SOURCE_NAME = "PYPL";
    private final LanguageDataDownloader dataDownloader;

    @Autowired
    public PYPLRatingParser(@Qualifier("PYPLDownloader") LanguageDataDownloader dataDownloader) {
        this.dataDownloader = dataDownloader;
    }

    @Override
    public List<LanguageData> parseWholeData() {
        String data = dataDownloader.getData();
        return directFormatParser.andThen(wholeDataParser).apply(data);
    }

    @Override
    public List<LanguageData> parseWholeData(String data) {
        return directFormatParser.andThen(wholeDataParser).apply(data);
    }

    private static final Pattern DIRECT_PATTERN = Pattern.compile("(?<= graphData = ).*(?=;)");
    private static final Pattern DELETE_COMMENTS_PATTERN = Pattern.compile("//.*? (?='|]|\\[)");

    /**
     * Plain data parsing with few adjustments
     */
    private Function<String, String> directFormatParser = str -> {

        Matcher matcher = DIRECT_PATTERN.matcher(str);
        String newStr = "";
        while (matcher.find()) {
            newStr = matcher.group();
        }
        Matcher deleteMatcher = DELETE_COMMENTS_PATTERN.matcher(newStr);
        return deleteMatcher.replaceAll("").replaceAll("'Date', ", "");
    };

    private static final  Pattern NAME_PATTERN = Pattern.compile("(?<=(\\[|\\s)').*?(?=')");
    private static final Pattern DATE_PATTERN = Pattern.compile("(?<=Date\\().*?(?=\\))");
    private static final Pattern VALUE_PATTERN = Pattern.compile("(0\\.\\d+)");

    private Function<String, List<LanguageData>> wholeDataParser = str -> {

        Matcher nameMatcher = NAME_PATTERN.matcher(str);
        Matcher dateMatcher = DATE_PATTERN.matcher(str);
        Matcher valueMatcher = VALUE_PATTERN.matcher(str);

        List<LanguageData> languages = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        while (nameMatcher.find()) {
            languages.add(new LanguageData(nameMatcher.group(), SOURCE_NAME));
        }
        while (dateMatcher.find())
            dates.add(dateMatcher.group());

        while (valueMatcher.find())
            values.add(Double.parseDouble(valueMatcher.group()));

        for (int i = 0; i < languages.size(); i++)
            for (int k = i; k < values.size(); k += languages.size())
                languages.get(i).appendData(dates.get(k / languages.size()), values.get(k));//// check


        return languages;
    };

}
