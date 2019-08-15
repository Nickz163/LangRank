package app;


import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

//@Service
public class TiobeRatingParser implements LanguageRatingParser {


    //todo: put in properties
    private final String SOURCE_NAME = "TIOBE";

    private static final Pattern NAME_PATTERN = Pattern.compile("(?<=name : ').+(?=')");
    private static final Pattern DATE_PATTERN = Pattern.compile("(?<=UTC\\().+?(?=\\))");
    private static final Pattern VALUE_PATTERN = Pattern.compile("(?<=\\),\\s).+?(?=])");

    private LanguageDataDownloader dataDownloader;

//    String sourceData;

//    public TiobeRatingParser(@Value("$[parser.tiobe.name]")String sourceName) {
//        this.SOURCE_NAME = sourceName;
//    }

//    final private String SOURCE_NAME = "TIOBE";

    //TODO = examle
//    @Value("$[parser.tiobe.name]")
//    String SourceName;


//    final private static String TIOBE_URL = "https://tiobe.com/tiobe-index/"; // warning!
//    private static Document document;

    // todo: use spring solution (in the future)
    private TiobeRatingParser() {
        this.dataDownloader = TiobeDataDownloader.getInstance();
    }

    public static TiobeRatingParser getInstance() {
        return new TiobeRatingParser();

    }

    @Override
    public List<LanguageDataPrototype> parseWholeData() {
        String data = dataDownloader.getData();
        return wholeDataParser.apply(data);
    }

    @Override
    public List<LanguageDataPrototype> parseWholeData(String data) {
        return wholeDataParser.apply(data);
    }

    private Function<String, LanguageDataPrototype> lineParser = str -> {

        Matcher nameMatcher = NAME_PATTERN.matcher(str);
        Matcher dateMatcher = DATE_PATTERN.matcher(str);
        Matcher valueMatcher = VALUE_PATTERN.matcher(str);

        LanguageDataPrototype language = new LanguageDataPrototype(nameMatcher.find() ? nameMatcher.group() : "NULL", SOURCE_NAME);

        while (dateMatcher.find() && valueMatcher.find())
            language.appendData(dateMatcher.group().replaceAll("\\s", ""), valueMatcher.group());
//        language.printLang();

        return language;
    };

    private Function<String, List<LanguageDataPrototype>> wholeDataParser = str -> {
        String lines[] = str.split("}, \\{");
        List<LanguageDataPrototype> languages = Arrays.stream(lines).map(lineParser).collect(Collectors.toList());
        // languages.forEach(a->a.printLang());
        return languages;
    };


}
