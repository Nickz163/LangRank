package app;

import java.util.List;

public interface LanguageRatingParser {
    List<LanguageData> parseWholeData();
    List<LanguageData> parseWholeData(String data);
}
