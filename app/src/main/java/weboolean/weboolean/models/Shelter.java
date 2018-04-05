package weboolean.weboolean.models;

import android.util.Log;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Max Brauer on 2/17/18.
 *
 */
@IgnoreExtraProperties
public class Shelter implements Serializable {

    // Private variables for shelter data. Self explanatory you dunce.
    private String address;
    private Map<String, Integer> available;
    private Map<String, Integer> capacity;
    private int key;
    private double latitude;
    private double longitude;
    private String name;
    private String note;
    private String phoneNumber;
    private Map<String, Object> restrictions;
    private final String TAG = "Shelter";

    // [Constructors] ============================================================================//
    // No-args constructor
    public Shelter(){
        this.address = null;
        this.available = null;
        this.capacity = null;
        this.key = -1;
        this.latitude = -1;
        this.longitude = -1;
        this.name = null;
        this.note = null;
        this.phoneNumber = null;
        this.restrictions = new HashMap<>();
    }
    // Full constructor
    public Shelter(String address, Map<String, Integer> available, Map<String, Integer> capacity,
                   int key, double latitude, double longitude, String name, String note,
                   String phoneNumber, Map<String, Object> restrictions) {
        this.address = address;
        this.available = available;
        this.capacity = capacity;
        this.key = key;
        this.latitude = latitude;
        this.longitude = longitude;
        this.name = name;
        this.note = note;
        this.phoneNumber = phoneNumber;
        this.restrictions = restrictions;
    }


    // [Getters and Setters] =====================================================================//
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, Integer> getAvailable() {
        return available;
    }

    public void setAvailable(Map<String, Integer> available) {
        this.available = available;
    }

    //Indexed with string "beds" and "rooms"
    public Map<String, Integer> getCapacity() {
        return capacity;
    }

    public void setCapacity(Map<String, Integer> capacity) {
        this.capacity = capacity;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean getAnyone() {
        return (Boolean) restrictions.get("anyone");
    }

    public void setAnyone(boolean anyone) {
        Log.d(TAG, String.valueOf(anyone));
        Log.d(TAG, restrictions.toString());
        restrictions.put("anyone", anyone);
    }

    public Map<String, Object> getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(Map<String, Object> restrictions) {
        this.restrictions = restrictions;
    }


    // [Object Methods] ==========================================================================//
    @Override
    public boolean equals(Object other) {
        if (! (other instanceof Shelter)) {
            return false;
        }
        return ((Shelter)other).getKey() == this.getKey();
    }

    @Override
    public String toString() {
        return this.getName();
    }


    /**
     * @param okShelters        Shelters to get strings from
     * @return                  Returns array of toString for the shelter.
     */
    public static ArrayList<String> toStrings(List<Shelter> okShelters) {
        ArrayList<String> listItems = new ArrayList<>();
        for (int i = 0; i < okShelters.size(); i++) {
            listItems.add(okShelters.get(i).toString());
        }
        return listItems;
    }

    /**
     * @return An information string, containing basic information
     */
    public String getInformationStringSnippet() {
        String s = "";
        // First add phone phoneNumber
        s += this.getPhoneNumber() + "\n";
        // Address
        // Find substring without "atlanta in it"
        int i = this.getAddress().indexOf("Atlanta");
        s += this.getAddress().substring(0, i) + "\n";
        // Capacity
        if (this.capacity.containsKey("beds") && this.capacity.containsKey("rooms")
                && this.capacity.get("beds") != 0 && this.capacity.get("rooms") != 0) {
            s += "Beds Available: " + this.available.get("beds") + "\n";
            s += "Rooms Available: " + this.available.get("rooms") + "\n";
        } else if (this.capacity.containsKey("beds") && this.capacity.get("beds") != 0) {
            s += "Beds Available: " + this.available.get("beds") + "\n";
        } else if (this.capacity.containsKey("rooms") && this.capacity.get("rooms") != 0) {
            s += "Rooms Available: " + this.available.get("rooms") + "\n";
        } else {
            s += "No Availability Information";
        }
        return s;
    }

    /**
     * @param shelters      Shelters to get an ID list from
     * @return              An ArrayList<Integer> of Shelter Keys/IDs
     */
    public static ArrayList<Integer> toIDList(List<Shelter> shelters) {
        // Null control structure
        if (shelters == null) {
            return new ArrayList<>();
        }
        ArrayList<Integer> ret = new ArrayList<>(shelters.size());
        for (Shelter s : shelters) {
            ret.add(s.getKey());
        }
        return ret;
    }
}
