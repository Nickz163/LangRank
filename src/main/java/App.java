import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;

public class App {

    public static void main(String[] args) throws IOException {
//        Document document = Jsoup.connect("https://javarush.ru/groups/posts/2007-legkiy-parsing-html-s-pomojshjhju-jsoup")
//                .userAgent("Chrome/4.0.249.0 Safari/532.5")
//                .referrer("http://www.google.com")
//                .get();
//        Elements listNews = document.select("div.content");
//
//        listNews
//                .forEach(element -> System.out.println(element.text()));

        TiobeRatingParser parser = TiobeRatingParser.getInstance();
      //  parser.parseLanguage("java");
//        parser.parseLanguage("python");
        PYPLRatingParser pyplRatingParser = PYPLRatingParser.getInstance();
//        pyplRatingParser.parseData();
        SOvFParser sOvFParser = SOvFParser.getInstance();
        sOvFParser.parseData("java");
    }
}
