package app;

import java.util.List;

public interface LanguageRatingParser {
    List<LanguageDataPrototype> parseWholeData();
    void saveDataInPlainFormat();
    //void parseLanguage(String language);
//    void parseDefaultLanguageSet();

}
