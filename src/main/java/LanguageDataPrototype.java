import java.util.*;

public class LanguageDataPrototype {
    private String name;

    private Map<String,Double> data = new LinkedHashMap<>();// temporary

    public  LanguageDataPrototype(String name) {
        this.name = name;
    }

//    public static LanguageDataPrototype getInstance(String name) {
//        return new LanguageDataPrototype(name);
//    }


    public void setData(Map<String, Double> data) {
        this.data = data;
    }

    public LanguageDataPrototype appendData(String date, Double value) {
        data.put(date,value);
        return this;
    }
    public LanguageDataPrototype appendData(String date, String value) {
        data.put(date,Double.parseDouble(value));
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
        data.forEach((date,value)->System.out.println(date + ":" + value + "; "));
    }
    public void printDataInfo(){
        System.out.println(data.size());
    }

}
