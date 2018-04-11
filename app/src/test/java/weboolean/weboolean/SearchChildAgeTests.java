package weboolean.weboolean;


import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

import weboolean.weboolean.models.Shelter;


/**
 * JUnit tests for searching child by age
 */
public class SearchChildAgeTests {
    Collection<Shelter> consideration = new HashSet<>();
    Collection<Shelter> removeSet = new HashSet<>();

    /**
     * 
     * @throws Exception throws an exception if can't add shelters properly
     */
    @Before
    public void setUp() throws Exception {
        List<Shelter>shelters = ShelterSingleton.getShelterArrayCopy();
        consideration.addAll(shelters);
    }

    /**
     *
     * @throws Exception throws exception if search without restriction fails
     */
    @Test
    public void searchShelterAge_noRestriction() throws Exception {
        // Test
        SearchActivity.searchChildAge(null, removeSet, consideration);
        assert(removeSet.isEmpty());
    }

    @Test
    public void searchShelterAge_adult() throws Exception {
        //Test
        SearchActivity.searchChildAge(19, removeSet, consideration);
        Collection<Shelter> checkSet = new HashSet<>();
        for (Shelter shelter : consideration) {
            if ((Boolean) shelter.getRestrictions().get("children")) {
                checkSet.add(shelter);
            }
        }
        assert(removeSet.equals(checkSet));
        assert(removeSet.size() == 8);
        removeSet.clear();
    }

    @Test
    public void searchShelterAge_18() throws Exception {
        SearchActivity.searchChildAge(18, removeSet, consideration);
        Collection<Shelter> checkSet = new HashSet<>();
        for (Shelter shelter : consideration) {
            if ((Integer) shelter.getRestrictions().get("child_age") != 18) {
                checkSet.add(shelter);
            }
        }
        assert(removeSet.equals(checkSet));
        assert(removeSet.size() == 5);
        removeSet.clear();
    }

    @Test
    public void searchShelterAge_5() throws Exception {
        SearchActivity.searchChildAge(18, removeSet, consideration);
        Collection<Shelter> checkSet = new HashSet<>();
        for (Shelter shelter : consideration) {
            if ((Integer) shelter.getRestrictions().get("child_age") > 18 || (Integer) shelter.getRestrictions().get("child_age") < 5) {
                checkSet.add(shelter);
            }
        }
        assert(removeSet.equals(checkSet));
        assert(removeSet.size() == 6);
        removeSet.clear();
    }

    @Test
    public void searchShelterAge_2() throws Exception {
        SearchActivity.searchChildAge(2, removeSet, consideration);
        Collection<Shelter> checkSet = new HashSet<>();
        for (Shelter shelter : consideration) {
            if ((Integer) shelter.getRestrictions().get("child_age") > 18 || (Integer) shelter.getRestrictions().get("child_age") < 2) {
                checkSet.add(shelter);
            }
        }
        assert(removeSet.equals(checkSet));
        assert(removeSet.size() == 7);
        removeSet.clear();
    }
}
