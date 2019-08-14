package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Repository
public class MainRunner implements CommandLineRunner {

    @Autowired
    private LanguageDataRepository repository;

    @Override
    public void run(String... args) throws Exception {

        TiobeRatingParser tiobeParser = app.TiobeRatingParser.getInstance();
        List<LanguageDataPrototype> tiobeData = tiobeParser.parseWholeData();

        List<String> languagesNames = tiobeData.stream()
                .map(lang -> lang.getName())
                .collect(Collectors.toList());

        SOvFRatingParser sOvFRatingParser = SOvFRatingParser.getInstance(languagesNames);
        List<LanguageDataPrototype> sovfData = sOvFRatingParser.parseWholeData();


        PYPLRatingParser pyplRatingParser = PYPLRatingParser.getInstance();
        List<LanguageDataPrototype> pyplData = pyplRatingParser.parseWholeData();


        repository.deleteAll();

        repository.saveAll(tiobeData);
        repository.saveAll(sovfData);
        repository.saveAll(pyplData);


        System.out.println("Tiobe languageData (findAll): ");
        System.out.println("------------------------------");

        for (LanguageDataPrototype languageDataPrototype : repository.findAllBySource("TIOBE"))
            System.out.println(languageDataPrototype);
    }
}
