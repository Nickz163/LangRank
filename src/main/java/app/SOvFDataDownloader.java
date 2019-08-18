package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.function.Supplier;
import java.util.stream.Collectors;


@Repository("SOvFDownloader")
public class SOvFDataDownloader implements LanguageDataDownloader {

    private static final String SOvF_ALL_URL = "https://cdn.sstatic.net/insights/data/month_tag_percents.json";

    @Autowired
    private FileDataWriter fileDataWriter;


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
        fileDataWriter.writeData("SOvFWholeData.txt", dataSupplier.get());
    }
}
