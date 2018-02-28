package weboolean.weboolean.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

/**
 * Created by Max Brauer on 2/17/18.
 *
 */
@IgnoreExtraProperties
public class Shelter {
    private String address;
    private Map<String, Integer> available;
    private Map<String, Integer> capacity;
    private int key;
    private double latitude;
    private double longitude;
    private String name;
    private String note;
    private String number;
    private boolean anyone;
    private Map<String, Integer> restrictions;
    public Shelter(){
        this.address = null;
        this.available = null;
        this.capacity = null;
        this.key = -1;
        this.latitude = -1;
        this.longitude = -1;
        this.name = null;
        this.note = null;
        this.number = null;
        this.anyone = false;
        this.restrictions = null;
    }

    public Shelter(String address, Map<String, Integer> available, Map<String, Integer> capacity, int key, double latitude, double longitude, String name, String note, String number,
                   Map<String, Integer> restrictions) {
        this.address = address;
        this.available = available;
        this.capacity = capacity;
        this.key = key;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.note = note;
        this.number = number;
        this.anyone = anyone;
        this.restrictions = restrictions;
    }
}
