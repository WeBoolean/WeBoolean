package weboolean.weboolean.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;

/**
 * Created by Max Brauer on 2/17/18.
 *
 */
@IgnoreExtraProperties
public class Shelter {
    private String address;
    private String available;
    private String capacity;
    private int key;
    private double latitude;
    private double longitude;
    private String name;
    private String note;
    private String number;
    private boolean anyone;
    private int child_age;
    private boolean childern;
    private boolean fam;
    private boolean men;
    private boolean vets;
    private boolean women;

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
        this.child_age = -1;
        this.childern = false;
        this.fam = false;
        this.men = false;
        this.vets = false;
        this.women = false;
    }

    public Shelter(String address, String available, String capacity, int key, double latitude, double longitude, String name, String note, String number, boolean anyone, int child_age, boolean childern, boolean fam, boolean men, boolean vets, boolean women) {
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
        this.child_age = child_age;
        this.childern = childern;
        this.fam = fam;
        this.men = men;
        this.vets = vets;
        this.women = women;
    }
}
