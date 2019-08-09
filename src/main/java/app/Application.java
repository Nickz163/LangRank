package app;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class Application implements CommandLineRunner {

    @Autowired
    private LanguageDataRepository repository;


    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) throws Exception {


        TiobeRatingParser tiobeParser = app.TiobeRatingParser.getInstance();
        List<LanguageDataPrototype> tiobeData = tiobeParser.parseWholeData();

        List<String> languagesNames = tiobeData.stream()
                .map(lang -> lang.getName())
                .collect(Collectors.toList());

        SOvFParser sOvFParser = app.SOvFParser.getInstance(languagesNames);
        List<LanguageDataPrototype> sovfData = sOvFParser.parseWholeData();


        PYPLRatingParser pyplRatingParser = PYPLRatingParser.getInstance();
        List<LanguageDataPrototype> pyplData = pyplRatingParser.parseWholeData();


        repository.deleteAll();

        repository.saveAll(tiobeData);
        repository.saveAll(sovfData);
        repository.saveAll(pyplData);


        System.out.println("Tiobe languageData (findAll): ");
        System.out.println("------------------------------");
//        for (app.LanguageDataPrototype languageDataPrototype: repository.findAll())
//            System.out.println(languageDataPrototype);

//        for (LanguageDataPrototype languageDataPrototype : repository.findAllByName("java"))
//            System.out.println(languageDataPrototype);

        for (LanguageDataPrototype languageDataPrototype : repository.findAllBySource("TIOBE"))
            System.out.println(languageDataPrototype);
//        System.out.println( repository.findAllByName("C"));
    }
}
