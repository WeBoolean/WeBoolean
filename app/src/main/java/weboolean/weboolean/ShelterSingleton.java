package weboolean.weboolean;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import weboolean.weboolean.models.Shelter;

/**
 * Created by rajshrimali on 2/28/18.
 */

public class ShelterSingleton implements Runnable {

    private static ArrayList<Shelter> shelters = new ArrayList<>();
    private static FirebaseDatabase db;
    private static DatabaseReference reference;
    private static boolean instantiated = false;
    public static final String TAG = Shelter.class.getSimpleName();
    private static final Lock mutexloc = new ReentrantLock();
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
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("shelters");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    Shelter s = singleSnapshot.getValue(Shelter.class);
                    boolean found = false;
                    for (int i = 0 ; i < ShelterSingleton.shelters.size(); i++) {
                        if (ShelterSingleton.shelters.get(i).equals(s)) {
                            mutexloc.lock();
                            ShelterSingleton.shelters.set(i, s); //update shelter at current point
                            mutexloc.unlock();
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        ShelterSingleton.shelters.add(s);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
        //insert code to connect to database and populate here
    }

    /** A thread-safe getter method
     * @return A copy of the shelter list. This will not be live.
     */
    public static ArrayList<Shelter> getShelterArrayCopy() {
        mutexloc.lock();
        ArrayList<Shelter> copy = (ArrayList<Shelter>) ShelterSingleton.shelters.clone();
        mutexloc.unlock();
        return copy;
    }


}
