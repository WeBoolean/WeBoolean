package weboolean.weboolean;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import weboolean.weboolean.models.Shelter;
import static org.junit.Assert.assertEquals;
import static weboolean.weboolean.ShelterSingleton.getShelterKeyByCommonName;

/**
 *
 */
@SuppressWarnings("MagicNumber")
public class GetShelterKeyByCommonNameTest {
    /**
     *
     * @throws Exception failed test
     */
    @Test
    public void negativeData() throws Exception {
        Shelter testShelter = createShelter("", -1);
        Collection<Shelter> shelters = new ArrayList<>();
        shelters.add(testShelter);
        int foundKey = getShelterKeyByCommonName("", shelters);
        assertEquals(foundKey, -1);
    }
    /**
     *
     * @throws Exception failed test
     */
    @Test
    public void weirdNames() throws Exception {
        Shelter testShelter = createShelter("2387dhsacwelnsdnsdfcxa($Recsd", 1);
        Collection<Shelter> shelters = new ArrayList<>();
        shelters.add(testShelter);
        int foundKey = getShelterKeyByCommonName("2387dhsacwelnsdnsdfcxa($Recsd", shelters);
        assertEquals(foundKey, 1);
    }
    /**
     *
     * @throws Exception failed test
     */
    @Test
    public void longKey() throws Exception {
        final int key = 1191128334;
        Shelter testShelter = createShelter("test", key);
        Collection<Shelter> shelters = new ArrayList<>();
        shelters.add(testShelter);
        int foundKey = getShelterKeyByCommonName("test", shelters);
        assertEquals(foundKey, key);
    }
    /**
     *
     * @throws Exception failed test
     */
    @Test
    public void lotsOfEntries() throws Exception {
        Collection<Shelter> shelters = new ArrayList<>();
        final int numberOfShelters = 10000;
        for (int i = 0; i < numberOfShelters; i++) {
            Shelter test = createShelter("test" + i, (i * i) - i);
            shelters.add(test);
        }
        for (int i = numberOfShelters - 1; i >= 0; i-- ) {
            int foundKey = getShelterKeyByCommonName("test" + i, shelters);
            assertEquals(foundKey, (i * i) - i);
        }
    }
    /**
     *
     */
    @Test(expected = NoSuchElementException.class)
    public void notPresentInEmptyList() {
        Iterable<Shelter> shelters = new ArrayList<>();
        getShelterKeyByCommonName("test", shelters);
    }
    /**
     *
     */
    @Test(expected = NoSuchElementException.class)
    public void notPresentInLongList() {
        final int numberOfShelters = 10000;
        Collection<Shelter> shelters = new ArrayList<>();
        for (int i = 0; i < numberOfShelters; i++) {
            Shelter test = createShelter("test" + i, (i * i) - i);
            shelters.add(test);
        }
        getShelterKeyByCommonName("test", shelters);
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
