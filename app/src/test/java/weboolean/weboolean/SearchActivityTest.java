package weboolean.weboolean;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import weboolean.weboolean.models.Shelter;

import static org.junit.Assert.*;

/**
 * Created by rajshrimali on 3/8/18.
 */
public class SearchActivityTest {
    List<Shelter> shelters;
    @Before
    public void setUp() throws Exception {
        shelters = ShelterSingleton.getShelterArrayCopy();

//        new Thread(new ShelterSingleton()).start();
    }

    @Test
    public void searchSheltersNoRestrictions() throws Exception {
        HashMap<String, Object> restrictions= new HashMap<>();
        // Test
        List<Shelter> compatible = SearchActivity.searchShelters(restrictions);
    }

    @Test
    public void searchSheltersIndividualRestricted() throws Exception {
        HashMap<String, Object> restrictions= new HashMap<>();
        String[] keys = {"men", "women", "vets", "children", "fam", "anyone"};
        for (String key : keys) {
            restrictions.put(key, true);
            List<Shelter> compatible = SearchActivity.searchShelters(restrictions);
            for (Shelter s : compatible) {
                if (!(Boolean) s.getRestrictions().get(key)) {
                    throw new AssertionError(key + " " + s.toString() + " failed to match");
                }
            }
        }
    }

    @Test
    public void searchSheltersTwoRestrictions() throws Exception {
        HashMap<String, Object> restrictions= new HashMap<>();
        String[] keys = {"men", "women", "vets", "children", "fam", "anyone"};
        for (String key : keys) {
            for (String key2: keys) {
                //skip if they're the same
                if (!key.equals(key2)) {
                    restrictions.put(key, true);
                    restrictions.put(key2, true);
                    List<Shelter> compatible = SearchActivity.searchShelters(restrictions);
                    for (Shelter s : compatible) {
                        if (!(Boolean) s.getRestrictions().get(key)) {
                            throw new AssertionError(key + " " + s.toString() + " failed to match");
                        }
                    }
                }
            }
        }
    }


}