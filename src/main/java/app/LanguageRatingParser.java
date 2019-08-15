package app;

import java.util.List;

public interface LanguageRatingParser {
    List<LanguageDataPrototype> parseWholeData();
    List<LanguageDataPrototype> parseWholeData(String data);
}
