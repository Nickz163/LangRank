package app;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service("TiobeParser")
public class TiobeRatingParser implements LanguageRatingParser {


    //todo: put in properties
    private final String SOURCE_NAME = "TIOBE";

    private static final Pattern NAME_PATTERN = Pattern.compile("(?<=name : ').+(?=')");
    private static final Pattern DATE_PATTERN = Pattern.compile("(?<=UTC\\().+?(?=\\))");
    private static final Pattern VALUE_PATTERN = Pattern.compile("(?<=\\),\\s).+?(?=])");

    private final LanguageDataDownloader dataDownloader;

    @Autowired
    public TiobeRatingParser(@Qualifier("TiobeDownloader") LanguageDataDownloader dataDownloader) {
        this.dataDownloader = dataDownloader;
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
        return languages;
    };


}
