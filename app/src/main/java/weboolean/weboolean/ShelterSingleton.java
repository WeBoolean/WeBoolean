package weboolean.weboolean;

import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import weboolean.weboolean.models.Shelter;

/**
 * Created by rajshrimali on 2/28/18.
 */

public class ShelterSingleton implements Runnable {

    //This is going to hold our copy of the shelters
    private static ArrayList<Shelter> shelters = new ArrayList<>();
    //firebase stuff
    private static FirebaseDatabase db;
    private static DatabaseReference reference;

    //Make sure only one copy of this is ever instantiated.
    //Ideally, we find some better way that deletes this on deletion but whatever.
    private static boolean instantiated = false;

    //Logging. Currently unused.
    private static final String TAG = Shelter.class.getSimpleName();

    //Mutex lock. Ensures we never try to write to the shelter array while someone is getting
    //a copy of it. Bad things could happen otherwise.
    private static final Lock mutexloc = new ReentrantLock();
    private static final Lock updateLock = new ReentrantLock();
    /** Creates our first shelter.
     * Upon it already being instantiated, it throws an instantiation exception.
     * If you do everything right and behave, it'll never happen ;)
     * @throws InstantiationException multiple instantiation
     */
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
        //initialize our connection.
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("shelters");

        //Add a permanent listener to our reference.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Go through our data update
                updateLock.lock();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    //get our individual shelter.
                    Log.d(TAG, singleSnapshot.toString());
                    Shelter s = singleSnapshot.getValue(Shelter.class);
                    Integer key = s.getKey();
                    if (shelters.size() <= key) {
                        mutexloc.lock();
                        shelters.add(key, s);
                        mutexloc.unlock();
                    } else {
                        mutexloc.lock();
                        shelters.set(key, s);
                        mutexloc.unlock();
                    }
                }
                updateLock.unlock();
                Log.d(TAG, "Initial Shelter Creation Finished");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e(TAG, "The read failed: " + databaseError.getCode());
            }
        });
        //insert code to connect to database and populate here
    }

    /** A thread-safe getter method
     * @return A copy of the shelter list. This will not be live.
     */
    public static List<Shelter> getShelterArrayCopy() {
        mutexloc.lock();
        ArrayList<Shelter> copy = (ArrayList<Shelter>) shelters.clone();
        mutexloc.unlock();
        return copy;
    }

    /** Thread-Safe updating method
     *
     * Locks both locks and updates local shelter copy. Then, it broadcasts to firebase.
     * WORKS OFFLINE. currently no offline persistence.
     * @param key  shelter key
     * @param s    the new shelter.
     */
    public static void updateShelter(int key, Shelter s) {
        updateLock.lock();
        mutexloc.lock();
        shelters.set(key, s);
        mutexloc.unlock();
        updateLock.unlock();
        //now I have to broadcast this change.
        reference.child((new Integer(key)).toString()).setValue(s);
    }

    /** Thread safe, but this shouldn't really be called unless resotring from backup
     *
     * Future-proofing implies we should have a way to reload the singleton from a copy
     * @param list the list to use
     */
    public static void forciblySetLocalBackingArray(ArrayList<Shelter> list) {
        shelters = list;
    }

    public static int getShelterKeyByCommonName(String name) {
        List<Shelter> shelters = getShelterArrayCopy();
        for (Shelter s: shelters) {
            if (s.getName().equals(name)) {
                return s.getKey();
            }
        }
        throw new NoSuchElementException("Shelter " + name + "Does not exist in db");
    }
}
