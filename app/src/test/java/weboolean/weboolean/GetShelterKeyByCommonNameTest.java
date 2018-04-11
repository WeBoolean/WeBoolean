package weboolean.weboolean;

import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import weboolean.weboolean.models.Shelter;
import static org.junit.Assert.assertEquals;
import static weboolean.weboolean.ShelterSingleton.getShelterKeyByCommonName;

/**
 * Created by Max Brauer on 4/10/18.
 */
public class GetShelterKeyByCommonNameTest {

    @Test
    public void negativeData() throws Exception {
        Shelter testShelter = createShelter("", -1);
        List<Shelter> shelters = new ArrayList<>();
        shelters.add(testShelter);
        int foundKey = getShelterKeyByCommonName("", shelters);
        assertEquals(foundKey, -1);
    }

    @Test
    public void weirdNames() throws Exception {
        Shelter testShelter = createShelter("2387dhsacwelnsdnsdfcxa($Recsd", 1);
        List<Shelter> shelters = new ArrayList<>();
        shelters.add(testShelter);
        int foundKey = getShelterKeyByCommonName("2387dhsacwelnsdnsdfcxa($Recsd", shelters);
        assertEquals(foundKey, 1);
    }

    @Test
    public void longKey() throws Exception {
        Shelter testShelter = createShelter("test", 1191128334);
        List<Shelter> shelters = new ArrayList<>();
        shelters.add(testShelter);
        int foundKey = getShelterKeyByCommonName("test", shelters);
        assertEquals(foundKey, 1191128334);
    }

    @Test
    public void lotsOfEntries() throws Exception {
        List<Shelter> shelters = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Shelter test = createShelter("test" + i, i * i - i);
            shelters.add(test);
        }
        for (int i = 9999; i >= 0; i-- ) {
            int foundKey = getShelterKeyByCommonName("test" + i, shelters);
            assertEquals(foundKey, i * i - i);
        }
    }

    @Test(expected = NoSuchElementException.class)
    public void notPresentInEmptyList() {
        List<Shelter> shelters = new ArrayList<>();
        int foundKey = getShelterKeyByCommonName("test", shelters);
    }

    @Test(expected = NoSuchElementException.class)
    public void notPresentInLongList() {
        List<Shelter> shelters = new ArrayList<>();
        for (int i = 0; i < 10000; i++) {
            Shelter test = createShelter("test" + i, i * i - i);
            shelters.add(test);
        }
        int foundKey = getShelterKeyByCommonName("test", shelters);
    }

    private Shelter createShelter(String name, int key) {
        Map<String, Integer> available = new HashMap<>();
        available.put("beds", 0);
        available.put("rooms", 0);
        Map<String, Integer> capacity = new HashMap<>();
        capacity.put("beds", 0);
        capacity.put("rooms", 0);
        Map<String, Object> restrictions = new HashMap<>();
        return new Shelter("Testing Rd, Atlanta", available, capacity,
                key, 0, 0, name, "NA", "18001234567", restrictions);
    }
}
