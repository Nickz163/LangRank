import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

public class App {

    public static void main(String[] args) throws IOException {

        TiobeRatingParser parser = TiobeRatingParser.getInstance();
//        parser.parseWholeData();
      //  parser.parseLanguage("java");
//        parser.parseLanguage("python");

        PYPLRatingParser pyplRatingParser = PYPLRatingParser.getInstance();
        pyplRatingParser.parseData();
//        SOvFParser sOvFParser = SOvFParser.getInstance();
//        sOvFParser.parseData("java");
    }
}
