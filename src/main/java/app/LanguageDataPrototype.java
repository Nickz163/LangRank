package app;

import org.springframework.data.annotation.Id;

import javax.annotation.Generated;
import java.util.*;

public class LanguageDataPrototype {

    @Id
    private String id;

    private String name;
    private String source;

    private Map<String, Double> data = new LinkedHashMap<>();// temporary

    public LanguageDataPrototype() {
    }

    public LanguageDataPrototype(String name, String sourceName) {
        this.name = name.toLowerCase();
        this.source = sourceName;
        id = (sourceName + name).toLowerCase(); // todo: temporary solution
    }


    public void setData(Map<String, Double> data) {
        this.data = data;
    }


    public LanguageDataPrototype appendData(String date, Double value) {
        data.put(date, value);
        return this;
    }

    public LanguageDataPrototype appendData(String date, String value) {
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
        return "LanguageDataPrototype{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", source='" + source + '\'' +
                ", data=" + data +
                '}';
    }
}
