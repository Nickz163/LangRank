package app;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SOvFRatingParser implements LanguageRatingParser {

    private final String SOURCE_NAME = "Stack_Overflow";
    private LanguageDataDownloader dataDownloader;

   private List<String> languagesNames;


    // Todo: use separate file for languages names
    public static SOvFRatingParser getInstance(List<String> names) {
        return new SOvFRatingParser(names);
    }


    /**
     * private constructor for getInstance
     *
     * @param languagesNames  names for parsing
     */
    private SOvFRatingParser(List<String> languagesNames) { // todo: использовать данный список имен для парсинга
        this.languagesNames = languagesNames;
        dataDownloader = SOvFDataDownloader.getInstance();
    }

    @Override
    public List<LanguageDataPrototype> parseWholeData() {
        String data = dataDownloader.getData();
        return wholeDataParser.apply(data, languagesNames);
    }



    //date pattens
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
    private static final Pattern VALUE_PATTERN = Pattern.compile("\\d+\\.?\\d*"); // d+  -   d{1,}

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
            .map(str -> str.toLowerCase())
            .collect(Collectors.joining("|", "(", ")"));


    // TODO: refactor
    private BiFunction<String, List<String>, List<LanguageDataPrototype>> wholeDataParser = (source, languagesNames) -> {


        List<LanguageDataPrototype> languages = new ArrayList<>();

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

            LanguageDataPrototype language = new LanguageDataPrototype(lang, SOURCE_NAME);

            List<String> values = nameAndValues.get(lang);
            for (int i = 0; i < values.size(); i++) {
                language.appendData(dates.get(i), values.get(i));
            }
            languages.add(language);

        });


        return languages;
    };


}
