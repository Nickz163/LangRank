package app;

import java.util.List;

public interface LanguageRatingParser {
    /**
     * @return List of parsed data from web-source
     */
    List<LanguageData> parseWholeData();

    /**
     *
     * @param data data from web-source
     * @return List of parsed data from web-source
     */
    List<LanguageData> parseWholeData(String data);
}
