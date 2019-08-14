package app;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class SOvFParser implements LanguageRatingParser {

    final private String SOURCE_NAME = "Stack Overflow";
    final private static String SOvF_ALL_URL = "https://cdn.sstatic.net/insights/data/month_tag_percents.json";
    List<String> languagesNames;

    public static SOvFParser getInstance(List<String> names) {
        return new SOvFParser(names);
    }

    /**
     * private constructor for getInstance
     *
     * @param languagesNames
     */
    private SOvFParser(List<String> languagesNames) { // todo: использовать данный список имен для парсинга
        this.languagesNames = languagesNames;
    }

    @Override
    public List<LanguageDataPrototype> parseWholeData() {
        String data = dataSupplier.get();
        //dateParser.apply(data).forEach((a) -> System.out.println(a));
        //wholeDataParser.apply(data, languagesNames); // was

        return wholeDataParser.apply(data, languagesNames); // changed!!!!
    }

    @Override
    public void saveDataInPlainFormat() {
        FileDataWriter.getInstance().writeData("SOvFWholeData.txt", dataSupplier.get());
    }


    private Supplier<String> dataSupplier = () -> {
        String data = "";
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new URL(SOvF_ALL_URL).openStream()))) {
            //  System.out.println(bufferedReader.readLine());
            data = bufferedReader.lines().collect(Collectors.joining());
            // System.out.println(data);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;

    };


    private Function<String, List<String>> dateParser = str -> {

        List<String> dates = new ArrayList<>();

        Pattern datePattern = Pattern.compile("(?<=(\"Year\"|\"Month\"): \\[).*?(?=])");

        // not effective
        Pattern yearPattern = Pattern.compile("\\d\\d\\d\\d");
        Pattern monthPattern = Pattern.compile("(?<=\")\\d{1,2}(?=\")");


        Matcher dateMatcher = datePattern.matcher(str);

        StringBuilder builder = new StringBuilder();
        while (dateMatcher.find())
            builder.append(dateMatcher.group());

        Matcher yearMatcher = yearPattern.matcher(builder.toString());
        Matcher monthMatcher = monthPattern.matcher(builder.toString());

//        builder = new StringBuilder();

        while (yearMatcher.find() && monthMatcher.find())
            dates.add(yearMatcher.group() + "," + monthMatcher.group() + "," + "1");

        return dates;
    };


    private Function<String, Map<String, List<String>>> valuesParser = str -> {


        Map<String, List<String>> data = new HashMap<>();

        List<String> values = new ArrayList<>();
        Pattern namePattern = Pattern.compile("(?<=\").*(?=\")");
        Pattern valuePattern = Pattern.compile("\\d{1,}\\.?\\d*"); // d+

        Matcher valueMatcher = valuePattern.matcher(str);
        Matcher nameMatcher = namePattern.matcher(str);

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


    private BiFunction<String, List<String>, List<LanguageDataPrototype>> wholeDataParser = (source, languagesNames) -> {


        List<LanguageDataPrototype> languages = new ArrayList<>();

        String nameRegex = namesRegexAdapter.apply(languagesNames);

        Pattern dataPattern = Pattern.compile("\"" + nameRegex + "\": \\[.*?]");
        Matcher dataMatcher = dataPattern.matcher(source);


        List<String> dates = dateParser.apply(source);
        LinkedHashMap<String, List<String>> nameAndValues = new LinkedHashMap<>();
        while (dataMatcher.find()) {
//            String str = dataMatcher.group();
//            System.out.println(dataMatcher.group());
            nameAndValues.putAll(valuesParser.apply(dataMatcher.group()));
        }


        // TODO заменить на zip
        Set<String> langNames = nameAndValues.keySet();

        langNames.stream().forEach(lang -> {

            LanguageDataPrototype language = new LanguageDataPrototype(lang, SOURCE_NAME);

            List<String> values = nameAndValues.get(lang);
            for (int i = 0; i < values.size(); i++) {
                language.appendData(dates.get(i), values.get(i));
            }
            languages.add(language);

        });

        //languages.forEach(a->a.printLang());

        return languages;
    };


//    private BiFunction<String, String, String> langDataParser = (source, language) -> {
//        //Pattern pattern = Pattern.compile("name : .*,data : .*(?=}\n)");
////        Pattern datePattern = Pattern.compile("\\{.*\"Year\":.*\"Month\":.*],");
////        Matcher matcher = datePattern.matcher(source);
//        Pattern langDataPattern = Pattern.compile("\"" + language + "\": \\[.*]");
//        Matcher matcher = langDataPattern.matcher(source);
//        String newStr = "";
//        while (matcher.find()) {
//            newStr = source.substring(matcher.start(), matcher.end());
//        }
//        return newStr;
//    };

//    private Function<String, String> dateParser = (source) -> {
//        Pattern datePattern = Pattern.compile("\\{.*\"Year\":.*\"Month\":.*]");
//        Matcher matcher = datePattern.matcher(source);
//        String newStr = "";
//        while (matcher.find()) {
//            newStr = source.substring(matcher.start(), matcher.end());
//        }
//        return newStr;
//    };


}
