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
 * Handles all shelter management.
 */

@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
class ShelterSingleton implements Runnable {

    // Holds our copy of the shelters
    private static final ArrayList<Shelter> shelters = new ArrayList<>();

    private static DatabaseReference reference;

    // Make sure only one copy of this is ever instantiated
    private static boolean instantiated = false;

    // Logging. Currently unused.
    private static final String TAG = Shelter.class.getSimpleName();

    // Mutex lock ensures we never try to write to the shelter array while someone is getting
    // a copy of it.
    private static final Lock mutexLock = new ReentrantLock();
    private static final Lock updateLock = new ReentrantLock();

    /** Creates our first shelter.
     * Upon it already being instantiated, it throws an instantiation exception.
     * @throws InstantiationException multiple instantiation
     */

    ShelterSingleton() throws InstantiationException {
        if (ShelterSingleton.instantiated) {
            throw new InstantiationException("Only one ShelterSingleton instance allowed.");
        }
        else {
            ShelterSingleton.instantiate();
        }
    }

    private static void instantiate() {
        ShelterSingleton.instantiated = true;
    }

    @Override
    public void run() {
        // Initialize our connection.
        FirebaseDatabase db = FirebaseDatabase.getInstance();
        reference = db.getReference("shelters");

        // Add a permanent listener to our reference.
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot == null) {
                    return;
                }
                // Go through our data update
                updateLock.lock();
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    // Get our individual shelter
                    Log.d(TAG, singleSnapshot.toString());
                    Shelter s = singleSnapshot.getValue(Shelter.class);
                    if (s == null) {
                        continue;
                    }
                    Integer key = s.getKey();
                    if (shelters.size() <= key) {
                        mutexLock.lock();
                        shelters.add(key, s);
                        mutexLock.unlock();
                    } else {
                        mutexLock.lock();
                        shelters.set(key, s);
                        mutexLock.unlock();
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

    }

    /** A thread-safe getter method
     * @return A copy of the shelter list. This will not be live.
     */

    static List<Shelter> getShelterArrayCopy() {
        mutexLock.lock();
        List<Shelter> copy = new ArrayList<>(shelters);
        mutexLock.unlock();
        return copy;
    }

    /** Thread-Safe updating method
     *
     * Locks both locks and updates local shelter copy. Then, it broadcasts to Firebase.
     * WORKS OFFLINE. Currently no offline persistence.
     * @param key  shelter key
     * @param s    the new shelter.
     */
    static void updateShelter(int key, Shelter s) {
        updateLock.lock();
        mutexLock.lock();
        shelters.set(key, s);
        mutexLock.unlock();
        updateLock.unlock();
        // Now change will be broadcast
        Integer keyForChild = Integer.valueOf(key);
        String stringOfKey = keyForChild.toString();
        reference.child((stringOfKey)).setValue(s);
    }

//    /** Thread safe, but this shouldn't really be called unless restoring from backup
//     *
//     * Future-proofing implies we should have a way to reload the singleton from a copy
//     * @param list the list to use
//     */
//    public static void forciblySetLocalBackingArray(ArrayList<Shelter> list) {
//        shelters = list;
//    }

    static int getShelterKeyByCommonName(String name, Iterable<Shelter> shelters) {
        for (Shelter s: shelters) {
            String shelterName = s.getName();
            if (shelterName.equals(name)) {
                return s.getKey();
            }
        }
        throw new NoSuchElementException("Shelter " + name + " does not exist in database");
    }
}
