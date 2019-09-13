package app;

import app.model.LanguageData;
import app.parsers.LanguageRatingParser;
import app.repository.LanguageDataRepository;
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
    private final LanguageDataRepository repository;
    private List<LanguageRatingParser> dataParsers;

    @Autowired
    public ScheduledTasks(LanguageDataRepository repository, List<LanguageRatingParser> dataParsers) {
        this.repository = repository;
        this.dataParsers = dataParsers;
    }

    @Scheduled(fixedRate = (1000 * 60 * 60 * 24 * 7 * 2), initialDelay = 1000)
    public void run() {
        logger.info("App started");
        List<LanguageData> data = dataParsers.stream()
                .flatMap(parser -> parser.parseWholeData().stream())
                .collect(Collectors.toList());

        repository.saveAll(data);
        logger.info("Launch Success");
    }

}
