package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("SOvFParser")
public class SOvFRatingParser implements LanguageRatingParser {

    private static  final String SOURCE_NAME = "Stack_Overflow";
    private final LanguageDataDownloader dataDownloader;
    private List<String> languagesNames;

    @Autowired
    public SOvFRatingParser(@Qualifier("SOvFDownloader") LanguageDataDownloader dataDownloader,
                            @Value("${languages.names}") String names) {
        this.dataDownloader = dataDownloader;
        languagesNames = Arrays.asList(names.split(","));
    }

    @Override
    public List<LanguageData> parseWholeData() {
        String data = dataDownloader.getData();
        return wholeDataParser.apply(data, languagesNames);
    }

    @Override
    public List<LanguageData> parseWholeData(String data) {
        return wholeDataParser.apply(data,languagesNames);
    }

    private static final Pattern DATE_PATTERN = Pattern.compile("(?<=(\"Year\"|\"Month\"): \\[).*?(?=])");
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\d\\d\\d\\d");
    private static final Pattern MONTH_PATTERN = Pattern.compile("(?<=\")\\d{1,2}(?=\")");

    private Function<String, List<String>> dateParser = str -> {
        List<String> dates = new ArrayList<>();
        Matcher dateMatcher = DATE_PATTERN.matcher(str);

        StringBuilder builder = new StringBuilder();
        while (dateMatcher.find())
            builder.append(dateMatcher.group());

        Matcher yearMatcher = YEAR_PATTERN.matcher(builder.toString());
        Matcher monthMatcher = MONTH_PATTERN.matcher(builder.toString());

        while (yearMatcher.find() && monthMatcher.find())
            dates.add(yearMatcher.group() + "," + monthMatcher.group() + "," + "1");

        return dates;
    };

    private static final Pattern NAME_PATTERN = Pattern.compile("(?<=\").*(?=\")");
    private static final Pattern VALUE_PATTERN = Pattern.compile("\\d+\\.?\\d*");

    private Function<String, Map<String, List<String>>> valuesParser = str -> {
        Map<String, List<String>> data = new HashMap<>();
        List<String> values = new ArrayList<>();

        Matcher valueMatcher = VALUE_PATTERN.matcher(str);
        Matcher nameMatcher = NAME_PATTERN.matcher(str);

        String name = "";
        while (nameMatcher.find())
            name = nameMatcher.group();

        while (valueMatcher.find())
            values.add(valueMatcher.group());

        data.put(name, values);
        return data;
    };

    private Function<List<String>, String> namesRegexAdapter = list -> list.stream()
            .map(String::toLowerCase)
            .collect(Collectors.joining("|", "(", ")"));

    private BiFunction<String, List<String>, List<LanguageData>> wholeDataParser = (source, languagesNames) -> {

        List<LanguageData> languages = new ArrayList<>();

        String nameRegex = namesRegexAdapter.apply(languagesNames);

        Pattern dataPattern = Pattern.compile("\"" + nameRegex + "\": \\[.*?]");
        Matcher dataMatcher = dataPattern.matcher(source);

        List<String> dates = dateParser.apply(source);
        LinkedHashMap<String, List<String>> nameAndValues = new LinkedHashMap<>();
        while (dataMatcher.find()) {
            nameAndValues.putAll(valuesParser.apply(dataMatcher.group()));
        }

        // TODO use zip
        Set<String> langNames = nameAndValues.keySet();

        langNames.stream().forEach(lang -> {

            LanguageData language = new LanguageData(lang, SOURCE_NAME);

            List<String> values = nameAndValues.get(lang);
            for (int i = 0; i < values.size(); i++) {
                language.appendData(dates.get(i), values.get(i));
            }
            languages.add(language);

        });

        return languages;
    };


}
