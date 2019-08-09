package app;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface LanguageDataRepository extends MongoRepository<LanguageDataPrototype, String> {

//    public List<LanguageDataPrototype> fingByLanguageName(String langName);
    public LanguageDataPrototype findByName(String langName);
    public List<LanguageDataPrototype> findAllByName(String langName);
    public List<LanguageDataPrototype> findAllBySource(String sourceName);
//    public List<LanguageDataPrototype> findBySourceName(String sourceName);
//    public LanguageDataPrototype findBySourceAndLang(String source, String langName);

}
