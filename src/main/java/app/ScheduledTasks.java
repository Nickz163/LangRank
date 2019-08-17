package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.data.mongodb.core.query.Query;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private final LanguageDataRepository repository;


    private LanguageRatingParser tiobeParser;
    private LanguageRatingParser pyplRatingParser;
    private LanguageRatingParser sovfRatingParser;

    @Autowired
    public ScheduledTasks(LanguageDataRepository repository,
                          @Qualifier("TiobeParser") LanguageRatingParser tiobeParsrer,
                          @Qualifier("PYPLParser") LanguageRatingParser pyplRatingParser,
                          @Qualifier("SOvFParser") LanguageRatingParser sovfRatingParser) {
        this.repository = repository;
        this.tiobeParser = tiobeParsrer;
        this.pyplRatingParser = pyplRatingParser;
        this.sovfRatingParser = sovfRatingParser;
    }


//    @Scheduled(cron = "* * * */14 * *")


    // mills|sec|min|hour|days|weeks
    @Scheduled(fixedRate = (1000 * 60 * 60 * 24 * 7 * 2), initialDelay = 1000)
    public void run() {

        logger.info("App started");

        List<LanguageDataPrototype> tiobeData = tiobeParser.parseWholeData();
        List<LanguageDataPrototype> pyplData = pyplRatingParser.parseWholeData();
        List<LanguageDataPrototype> sovfData = sovfRatingParser.parseWholeData();

//       repository.deleteAll();

        repository.saveAll(tiobeData);
        repository.saveAll(pyplData);
        repository.saveAll(sovfData);


//        for (LanguageDataPrototype languageDataPrototype : repository.findAllBySource("TIOBE"))
//            logger.info("Base entry:    " + languageDataPrototype);

    }

}
