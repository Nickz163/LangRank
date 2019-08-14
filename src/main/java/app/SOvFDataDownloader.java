package app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SOvFDataDownloader implements LanguageDataDownloader {

    final private static String SOvF_ALL_URL = "https://cdn.sstatic.net/insights/data/month_tag_percents.json";

    private Supplier<String> dataSupplier = () -> {
        String data = "";
        try (BufferedReader bufferedReader = new BufferedReader(
                new InputStreamReader(new URL(SOvF_ALL_URL).openStream()))) {
            //  System.out.println(bufferedReader.readLine());
            data = bufferedReader.lines().collect(Collectors.joining());
            // System.out.println(data);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;

    };


    @Override
    public String getData() {
        return dataSupplier.get();
    }

    @Override
    public void saveDataInPlainFormat() {
        FileDataWriter.getInstance().writeData("SOvFWholeData.txt", dataSupplier.get());
    }
}
