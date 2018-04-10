package weboolean.weboolean;

/**
 * Created by Michael Xiao Local on 4/9/2018.
 */

import android.util.Log;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import weboolean.weboolean.models.Shelter;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ShelterModelTest {

    @Test
    public void noAvailabilityInformation() throws Exception {
        Shelter testShelter = initialize();
        String test = testShelter.getInformationStringSnippet();
        // Test case: No Availability Information
        assertEquals(test, "18009001\ntestAddress, \nNo Availability Information");
    }

    @Test
    public void bedsAndRooms() throws Exception {

        Shelter testShelter = initialize();
        Map<String, Integer> available = new HashMap<>();
        available.put("beds", 1);
        available.put("rooms", 1);
        Map<String, Integer> capacity = new HashMap<>();
        capacity.put("beds", 1);
        capacity.put("rooms", 1);
        testShelter.setAvailable(available);
        testShelter.setCapacity(capacity);
        String test = testShelter.getInformationStringSnippet();
        // Test case: No Availability Information
        assertEquals(test, "18009001\ntestAddress, \nBeds Available: 1\nRooms Available: 1\n");
    }
    @Test
    public void bedsOnly() throws Exception {
        Shelter testShelter = initialize();
        Map<String, Integer> available = new HashMap<>();
        available.put("beds", 1);
        available.put("rooms", 0);
        Map<String, Integer> capacity = new HashMap<>();
        capacity.put("beds", 1);
        capacity.put("rooms", 0);
        testShelter.setAvailable(available);
        testShelter.setCapacity(capacity);
        String test = testShelter.getInformationStringSnippet();
        // Test case: No Availability Information
        assertEquals(test, "18009001\ntestAddress, \nBeds Available: 1\n");
    }

    @Test
    public void roomsOnly() throws Exception {
        Shelter testShelter = initialize();
        Map<String, Integer> available = new HashMap<>();
        available.put("beds", 0);
        available.put("rooms", 1);
        Map<String, Integer> capacity = new HashMap<>();
        capacity.put("beds", 0);
        capacity.put("rooms", 1);
        testShelter.setAvailable(available);
        testShelter.setCapacity(capacity);
        String test = testShelter.getInformationStringSnippet();
        // Test case: No Availability Information
        assertEquals(test, "18009001\ntestAddress, \nRooms Available: 1\n");
    }

    private Shelter initialize() {
        // Initialize
        Map<String, Integer> available = new HashMap<>();
        available.put("beds", 0);
        available.put("rooms", 0);
        Map<String, Integer> capacity = new HashMap<>();
        capacity.put("beds", 0);
        capacity.put("rooms", 0);
        int key = 0;
        double latitude = 100;
        double longitude = 101;
        String name = "TestShelter";
        String note = "TestNote";
        String phoneNumber = "18009001";
        Map<String, Object> restrictions = new HashMap<>();
        return new Shelter("testAddress, Atlanta", available, capacity,
            key, latitude, longitude, name, note, phoneNumber, restrictions);
    }
}