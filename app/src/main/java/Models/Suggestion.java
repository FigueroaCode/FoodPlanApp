package Models;

import java.util.Map;

public class Suggestion {

    private String name;
    private String food;
    private String keyId;
    private String weekday;
    private String color;
    private Map<String,Object> timestamp;


    public Suggestion(){}

    public Suggestion(String name, String food, String keyId,String weekday,String color,Map<String,Object> timestamp) {
        this.name = name;
        this.food = food;
        this.keyId = keyId;
        this.weekday = weekday;
        this.color = color;
        this.timestamp = timestamp;
    }

    public Map<String, Object> getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public String getFood() {
        return food;
    }

    public String getKeyId() {
        return keyId;
    }

    public String getWeekday() {
        return weekday;
    }

    public String getColor() {
        return color;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFood(String food) {
        this.food = food;
    }

    public void setKeyId(String keyId) {
        this.keyId = keyId;
    }

    public void setWeekday(String weekday) {
        this.weekday = weekday;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setTimestamp(Map<String, Object> timestamp) {
        this.timestamp = timestamp;
    }
}
