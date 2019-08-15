package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    @Autowired
    private LanguageDataRepository repository;


//    @Scheduled(cron = "* * * */14 * *")


//    // mills|sec|min|hour|days|weeks
//    @Scheduled(fixedRate = 1000 * 60 * 60 * 24 * 7 * 2, initialDelay = 1000)

    // mills|sec|min|hour|days|weeks
    @Scheduled(fixedRate = (1000 * 60 * 60 * 24 * 7 * 2), initialDelay = 1000)
    public void run() {

        System.out.println("Started");

        TiobeRatingParser tiobeParser = app.TiobeRatingParser.getInstance();
        List<LanguageDataPrototype> tiobeData = tiobeParser.parseWholeData();

        List<String> languagesNames = tiobeData.stream()
                .map(LanguageDataPrototype::getName)
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
            logger.info("Base entry:    " + languageDataPrototype);

    }

}
