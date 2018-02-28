package weboolean.weboolean;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import weboolean.weboolean.models.Shelter;

/**
 * Created by rajshrimali on 2/28/18.
 */

public class ShelterSingleton implements Runnable {

    private static ArrayList<Shelter> shelters = new ArrayList<>();
    private static FirebaseDatabase db;
    private static DatabaseReference reference;
    private static boolean instantiated = false;
    public ShelterSingleton() throws InstantiationException {
        if (ShelterSingleton.instantiated) {
            throw new InstantiationException("Only one ShelterSingleton instance allowed");
        }
        else {
            ShelterSingleton.instantiated = true;
        }
    }
    @Override
    public void run() {
        //insert code to connect to database and populate here
    }
}
