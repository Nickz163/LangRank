package app.repository;

import app.model.LanguageData;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LanguageDataRepository extends MongoRepository<LanguageData, String> {
    LanguageData findByName(String langName);
    List<LanguageData> findAllByName(String langName);
    List<LanguageData> findAllBySource(String sourceName);
}
