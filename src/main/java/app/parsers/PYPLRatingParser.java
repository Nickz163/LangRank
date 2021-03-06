package app.parsers;

import app.downloaders.LanguageDataDownloader;
import app.model.LanguageData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service("PYPLParser")
public class PYPLRatingParser implements LanguageRatingParser {

    private final String SOURCE_NAME;
    private final LanguageDataDownloader dataDownloader;

    @Autowired
    public PYPLRatingParser(@Qualifier("PYPLDownloader") LanguageDataDownloader dataDownloader,
                            @Value("${sources.pypl.name}") String sourceName) {
        this.dataDownloader = dataDownloader;
        this.SOURCE_NAME = sourceName;
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
    private final Function<String, String> directFormatParser = str -> {

        Matcher matcher = DIRECT_PATTERN.matcher(str);
        String newStr = "";
        while (matcher.find()) {
            newStr = matcher.group();
        }
        Matcher deleteMatcher = DELETE_COMMENTS_PATTERN.matcher(newStr);
        return deleteMatcher.replaceAll("").replaceAll("'Date', ", "");
    };

    private static final Pattern NAME_PATTERN = Pattern.compile("(?<=(\\[|\\s)').*?(?=')");
    private static final Pattern DATE_PATTERN = Pattern.compile("(?<=Date\\().*?(?=\\))");
    private static final Pattern VALUE_PATTERN = Pattern.compile("(0\\.\\d+)");

    private final Function<String, List<LanguageData>> wholeDataParser = str -> {

        Matcher nameMatcher = NAME_PATTERN.matcher(str);
        Matcher dateMatcher = DATE_PATTERN.matcher(str);
        Matcher valueMatcher = VALUE_PATTERN.matcher(str);

        List<LanguageData> languages = new ArrayList<>();
        List<String> dates = new ArrayList<>();
        List<Double> values = new ArrayList<>();

        while (nameMatcher.find()) {
            languages.add(new LanguageData(nameMatcher.group(), PYPLRatingParser.this.SOURCE_NAME));
        }
        while (dateMatcher.find())
            dates.add(dateMatcher.group());

        while (valueMatcher.find())
            values.add(Double.parseDouble(valueMatcher.group()));

        for (int i = 0; i < languages.size(); i++)
            for (int k = i; k < values.size(); k += languages.size())
                languages.get(i).appendData(dates.get(k / languages.size()), values.get(k));

        return languages;
    };

}
