package app;

import org.springframework.data.annotation.Id;

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
        this.name = name.toLowerCase();//todo check this
        this.source = sourceName;
    }

//    public static app.LanguageDataPrototype getInstance(String name) {
//        return new app.LanguageDataPrototype(name);
//    }


    public void setData(Map<String, Double> data) {
        this.data = data;
    }

//    public void setSource(String sourceName) {
//        this.source = sourceName;
//    }

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

    public void printLang() {
        System.out.println(name);
        data.forEach((date, value) -> System.out.println(date + ":" + value + "; "));
    }

    public void printDataInfo() {
        System.out.println(data.size());
    }

//    @Override
//    public String toString() {
//        return source + " | " + name + " data: " + data + "\n";
//    }


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
