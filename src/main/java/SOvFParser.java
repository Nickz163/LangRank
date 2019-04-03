import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SOvFParser {
    final private static String SOvF_ALL_URL = "https://cdn.sstatic.net/insights/data/month_tag_percents.json";

    public static SOvFParser getInstance() {
        return new SOvFParser();
    }

    public void parseData(String language) {

        try {
//            Document document = Jsoup.connect(SOvF_ALL_URL) //TODO: сделать общий интерфейс и убрать это в default метод
//                    .userAgent("Chrome/4.0.249.0 Safari/532.5")
//                    .referrer("http://www.google.com")
//                    .get();

            URL url = new URL(SOvF_ALL_URL);

            InputStream inputStream = url.openStream();
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("SOvF_all.txt")))) {

                // String source = new BufferedReader(new InputStreamReader(inputStream)).lines().collect(Collectors.joining());
                new BufferedReader(new InputStreamReader(inputStream)).lines().forEach(line -> {
                    try {
                        writer.write(line);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

            }


//            String data = Stream.of(dateParser.apply(source), langDataParser.apply(source, language))
//                    .collect(Collectors.joining(",\n"));
            // System.out.println(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String langNameAdapter() {
        return "";// TODO complete this method
    }

    private BiFunction<String, String, String> langDataParser = (source, language) -> {
        //Pattern pattern = Pattern.compile("name : .*,data : .*(?=}\n)");
//        Pattern datePattern = Pattern.compile("\\{.*\"Year\":.*\"Month\":.*],");
//        Matcher matcher = datePattern.matcher(source);
        Pattern langDataPattern = Pattern.compile("\"" + language + "\": \\[.*]");
        Matcher matcher = langDataPattern.matcher(source);
        String newStr = "";
        while (matcher.find()) {
            newStr = source.substring(matcher.start(), matcher.end());
        }
        return newStr;
    };

    private Function<String, String> dateParser = (source) -> {
        Pattern datePattern = Pattern.compile("\\{.*\"Year\":.*\"Month\":.*]");
        Matcher matcher = datePattern.matcher(source);
        String newStr = "";
        while (matcher.find()) {
            newStr = source.substring(matcher.start(), matcher.end());
        }
        return newStr;
    };

}
