package app;

import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;


import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class TiobeRatingParser implements LanguageRatingParser {



    final private String SOURCE_NAME = "TIOBE";

//    public TiobeRatingParser(@Value("$[parser.tiobe.name]")String sourceName) {
//        this.SOURCE_NAME = sourceName;
//    }

//    final private String SOURCE_NAME = "TIOBE";

    //TODO = examle
//    @Value("$[parser.tiobe.name]")
//    String SourceName;

    final private static String TIOBE_URL = "https://tiobe.com/tiobe-index/"; // warning!
    private static Document document;


    public static TiobeRatingParser getInstance() {
        try {
            document = Jsoup.connect(TIOBE_URL)
                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
                    .referrer("http://www.google.com")
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TiobeRatingParser();
    }

    @Override
    public List<LanguageDataPrototype> parseWholeData() {
        String data = dataSupplier.get();
        // System.out.println(plainDataParser.apply(data));
        // app.FileDataWriter.getInstance().writeData("TiobeWholeData.json", plainDataParser.apply(data));

        List<LanguageDataPrototype> languages = plainDataParser.andThen(wholeDataParser).apply(data);
        return languages;

    }
    


    private Supplier<String> dataSupplier = () -> document.select("script").stream()
            .flatMap(element -> element.dataNodes().stream())
            .filter(dataNode -> dataNode.getWholeData().contains("$(function () {"))
            .map(DataNode::getWholeData)
            .collect(Collectors.joining());


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


    private Function<String, String> plainDataParser = str -> {
        Pattern pattern = Pattern.compile("\\{name :.*}"); // problems with khanda
        Matcher matcher = pattern.matcher(str);
        StringBuilder stringBuilder = new StringBuilder();

        while (matcher.find()) {
            stringBuilder.append(matcher.group());
        }
        return stringBuilder.toString();
    };


    private String parsePlainData(String str) {
        Pattern pattern = Pattern.compile("\\{name :.*}"); // problems with khanda
        Matcher matcher = pattern.matcher(str);
        StringBuilder stringBuilder = new StringBuilder();

        while (matcher.find()) {
            stringBuilder.append(matcher.group());
        }
        return stringBuilder.toString();
    }


    private Function<String, LanguageDataPrototype> lineParser = str -> {
        Pattern namePattern = Pattern.compile("(?<=name : ').+(?=')");
        Pattern datePattern = Pattern.compile("(?<=UTC\\().+?(?=\\))");
        Pattern valuePattern = Pattern.compile("(?<=\\),\\s).+?(?=])");

        Matcher nameMatcher = namePattern.matcher(str);
        Matcher dateMatcher = datePattern.matcher(str);
        Matcher valueMatcher = valuePattern.matcher(str);

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
