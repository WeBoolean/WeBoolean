package weboolean.weboolean;


import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.HashSet;
import java.util.Set;

import weboolean.weboolean.models.Shelter;


/**
 * Created by sophiehandel on 4/9/18.
 */

public class SearchChildAgeTests {
    Set<Shelter> consideration = new HashSet<>();
    Set<Shelter> removeSet = new HashSet<>();

    @Before
    public void setUp() throws Exception {
        List<Shelter>shelters = ShelterSingleton.getShelterArrayCopy();
        for (Shelter shelter : shelters) {
            consideration.add(shelter);
        }
    }

    @Test
    public void searchShelterAge_noRestriction() throws Exception {
        // Test
        SearchActivity.searchChildAge(null, removeSet, consideration);
        assert(removeSet.size() == 0);
    }

    @Test
    public void searchShelterAge_adult() throws Exception {
        //Test
        SearchActivity.searchChildAge(19, removeSet, consideration);
        Set<Shelter> checkSet = new HashSet<>();
        for (Shelter shelter : consideration) {
            if ((Boolean) shelter.getRestrictions().get("children")) {
                checkSet.add(shelter);
            }
        }
        assert(removeSet.equals(checkSet));
        assert(removeSet.size() == 8);
    }

    @Test
    public void searchShelterAge_18() throws Exception {
        SearchActivity.searchChildAge(18, removeSet, consideration);
        Set<Shelter> checkSet = new HashSet<>();
        for (Shelter shelter : consideration) {
            if ((Integer) shelter.getRestrictions().get("child_age") != 18) {
                checkSet.add(shelter);
            }
            assert(removeSet.equals(checkSet));
            assert(removeSet.size() == 5);
        }
    }

    @Test
    public void searchShelterAge_5() throws Exception {
        SearchActivity.searchChildAge(18, removeSet, consideration);
        Set<Shelter> checkSet = new HashSet<>();
        for (Shelter shelter : consideration) {
            if ((Integer) shelter.getRestrictions().get("child_age") > 18 || (Integer) shelter.getRestrictions().get("child_age") < 5) {
                checkSet.add(shelter);
            }
            assert(removeSet.equals(checkSet));
            assert(removeSet.size() == 6);
        }
    }

    @Test
    public void searchShelterAge_2() throws Exception {
        SearchActivity.searchChildAge(2, removeSet, consideration);
        Set<Shelter> checkSet = new HashSet<>();
        for (Shelter shelter : consideration) {
            if ((Integer) shelter.getRestrictions().get("child_age") > 18 || (Integer) shelter.getRestrictions().get("child_age") < 2) {
                checkSet.add(shelter);
            }
            assert(removeSet.equals(checkSet));
            assert(removeSet.size() == 7);
        }
    }
}
