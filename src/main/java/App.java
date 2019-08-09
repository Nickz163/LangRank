//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//
//import org.jsoup.select.Elements;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.List;
//import java.util.Set;
//import java.util.function.Function;
//import java.util.stream.Collectors;
//
//public class App {
//
//    public static void main(String[] args) throws IOException {
//
//        app.TiobeRatingParser tiobeParser = app.TiobeRatingParser.getInstance();
//        //tiobeParser.parseWholeData();
//        List<String> languagesNames = tiobeParser.parseWholeData().stream()
//                .map(lang -> lang.getName())
//                .collect(Collectors.toList());
//
////       languagesNames.forEach(a->System.out.println(a));
//
//
//        app.SOvFParser sOvFParser = app.SOvFParser.getInstance(languagesNames);
//        // sOvFParser.parseData("java");
//        List<app.LanguageDataPrototype> langSOvF = sOvFParser.parseWholeData();
////        langSOvF.forEach(a -> a.printLang());
//
////        sOvFParser.saveDataInPlainFormat();
//
//
//        app.PYPLRatingParser pyplRatingParser = app.PYPLRatingParser.getInstance();
//        pyplRatingParser.parseWholeData();
//        // pyplRatingParser.saveDataInPlainFormat();
//    }
//}
