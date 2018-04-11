package weboolean.weboolean;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import weboolean.weboolean.models.Shelter;

/**
 * Tests.
 */
@SuppressWarnings("JavaDoc")
public class SearchSheltersTest {
    private List<Shelter> shelters;
    @Before
    public void setUp() throws Exception {
        shelters = ShelterSingleton.getShelterArrayCopy();

//        new Thread(new ShelterSingleton()).start();
    }

    @Test
    public void searchSheltersVoidParameter() throws Exception {
        List<Shelter> compatible = SearchActivity.searchShelters(null);
        assert(compatible.size() == shelters.size());
    }

    //@Test
    //public void searchSheltersNoRestrictions() throws Exception {
        //Map<String, Object> restrictions= new HashMap<>();
        // Test
        //List<Shelter> compatible = SearchActivity.searchShelters(restrictions);
    //}

    @Test
    public void searchSheltersIndividualRestricted() throws Exception {
        Map<String, Object> restrictions= new HashMap<>();
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
        Map<String, Object> restrictions= new HashMap<>();
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

    @Test
    public void searchSheltersExactMatch() throws Exception {
        Map<String, Object> restrictions= new HashMap<>();
        for (Shelter s: shelters) {
            restrictions.put("name", s.getName()); // put exact name into restrictions
            List<Shelter>  ret = SearchActivity.searchShelters(restrictions);
            assert(ret.size() == 1);
            assert(ret.get(0).equals(s));
        }
    }

    @Test
    public void searchSheltersNoExactMatch() throws Exception {
        Map<String, Object> restrictions= new HashMap<>();
        restrictions.put("name", "this does not exist");
        List<Shelter> ret = SearchActivity.searchShelters(restrictions);
        assert(ret.isEmpty());
    }
    @Test
    public void searchSheltersEmpty() throws Exception {
        Map<String, Object> restrictions= new HashMap<>();
        restrictions.put("name", "");
        List<Shelter> ret = SearchActivity.searchShelters(restrictions);
        assert(ret.size() == shelters.size());
    }

    @Test
    public void specifyAgeRestrictions() throws Exception {
         Map<String, Object> restrictions = new HashMap<>();
         restrictions.put("child_age", 5);
         List<Shelter> ret = SearchActivity.searchShelters(restrictions);
         // we have two shelters, one with 2 and one with 5
         assert(ret.size() == 2);
    }

    @Test
    public void specifyAgeRestrictionsNone() throws Exception {
        Map<String, Object> restrictions = new HashMap<>();
        restrictions.put("child_age", 0);
        List<Shelter> ret = SearchActivity.searchShelters(restrictions);
        assert(ret.size() == shelters.size());
    }

    @Test
    public void specifyAgeRestrictionsAll() throws Exception {
        Map<String, Object> restrictions = new HashMap<>();
        restrictions.put("child_age", Shelter.getMaxAge());
        List<Shelter> ret = SearchActivity.searchShelters(restrictions);
        assert(ret.isEmpty());
    }
}