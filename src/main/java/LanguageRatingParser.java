import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

public interface LanguageRatingParser {
    List<LanguageDataPrototype> parseWholeData();
    void saveDataInPlainFormat();
    //void parseLanguage(String language);
//    void parseDefaultLanguageSet();

}
