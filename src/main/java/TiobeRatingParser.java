
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.tools.internal.xjc.Language;
import org.jsoup.Jsoup;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import sun.jvm.hotspot.debugger.MachineDescriptionPPC;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TiobeRatingParser implements LanguageRatingParser {
    final private static String TIOBE_URL = "https://tiobe.com/tiobe-index/"; // warning!
    private static Document document;

    public static TiobeRatingParser getInstance() { //Todo: возможно вынести в метод connect
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
        // FileDataWriter.getInstance().writeData("TiobeWholeData.json", plainDataParser.apply(data));
        List<LanguageDataPrototype>  languages = plainDataParser.andThen(wholeDataParser).apply(data);

        return languages;

    }

    @Override
    public void saveDataInPlainFormat() {
        // default JSON from TIOBE (temporaty txt)
        FileDataWriter.getInstance().writeData("TiobeWholeData.txt", plainDataParser.apply(dataSupplier.get()));
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

    private Function<String, LanguageDataPrototype> lineParser = str -> {
        Pattern namePattern = Pattern.compile("(?<=name : ').+(?=')");
        Pattern datePattern = Pattern.compile("(?<=UTC\\().+?(?=\\))");
        Pattern valuePattern = Pattern.compile("(?<=\\),\\s).+?(?=])");

        Matcher nameMatcher = namePattern.matcher(str);
        Matcher dateMatcher = datePattern.matcher(str);
        Matcher valueMatcher = valuePattern.matcher(str);

        LanguageDataPrototype language = new LanguageDataPrototype(nameMatcher.find() ? nameMatcher.group() : "NULL");

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
