package app;

public interface LanguageDataDownloader {
    /**
     * @return language data from web-source
     */
    String getData();

    /**
     * Write language data to file
     */
    void saveDataInPlainFormat();
}
