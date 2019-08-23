package app;

import org.springframework.data.annotation.Id;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Information for writing to the database
 */
public class LanguageData {

    @Id
    private String id;

    private String name;
    private String source;
    private Map<String, Double> data = new LinkedHashMap<>();

    public LanguageData() {
    }

    public LanguageData(String name, String sourceName) {
        this.name = name.toLowerCase();
        this.source = sourceName;
        id = (sourceName + name).toLowerCase(); // todo: temporary solution
    }

    public void setData(Map<String, Double> data) {
        this.data = data;
    }

    public LanguageData appendData(String date, Double value) {
        data.put(date, value);
        return this;
    }

    public LanguageData appendData(String date, String value) {
        data.put(date, Double.parseDouble(value));
        return this;
    }

    public String getName() {
        return name;
    }

    public Map<String, Double> getData() {
        return data;
    }

    @Override
    public String toString() {
        return "LanguageData{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", data=" + data +
                '}';
    }
}
