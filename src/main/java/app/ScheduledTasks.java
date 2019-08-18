package app;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class ScheduledTasks {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

    private final LanguageDataRepository repository;



    private List<LanguageRatingParser> dataParsers;



   @Autowired
    public ScheduledTasks(LanguageDataRepository repository, List<LanguageRatingParser> dataParsers) {
        this.repository = repository;
        this.dataParsers = dataParsers;
    }


//    @Scheduled(cron = "* * * */14 * *")


    // mills|sec|min|hour|days|weeks
    @Scheduled(fixedRate = (1000 * 60 * 60 * 24 * 7 * 2), initialDelay = 1000)
    public void run() {

        logger.info("App started");


        List<LanguageDataPrototype> data = dataParsers.stream()
                .flatMap(parser ->  parser.parseWholeData().stream()).collect(Collectors.toList());

        repository.saveAll(data);



//        for (LanguageDataPrototype languageDataPrototype : repository.findAllBySource("TIOBE"))
//            logger.info("Base entry:    " + languageDataPrototype);

    }

}
