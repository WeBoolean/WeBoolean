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

    //This is going to hold our copy of the shelters
    private static ArrayList<Shelter> shelters = new ArrayList<>();

    //firebase stuff
    private static FirebaseDatabase db;
    private static DatabaseReference reference;

    //Make sure only one copy of this is ever instantiated.
    //Ideally, we find some better way that deletes this on deletion but whatever.
    private static boolean instantiated = false;

    //Logging. Currently unused.
    public static final String TAG = Shelter.class.getSimpleName();

    //Mutex lock. Ensures we never try to write to the shelter array while someone is getting
    //a copy of it. Bad things could happen otherwise.
    private static final Lock mutexloc = new ReentrantLock();

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
                for (DataSnapshot singleSnapshot : dataSnapshot.getChildren()) {
                    //get our individual shelter.
                    Shelter s = singleSnapshot.getValue(Shelter.class);

                    //This code here updates every shelter, making sure the keys match up.
                    boolean found = false;
                    for (int i = 0 ; i < ShelterSingleton.shelters.size(); i++) {
                        if (ShelterSingleton.shelters.get(i).equals(s)) {

                            //Lock the lock when updating
                            mutexloc.lock();
                            ShelterSingleton.shelters.set(i, s); //update shelter at current point
                            //and release instantly.
                            mutexloc.unlock();
                            found = true;
                            break;
                        }
                    }
                    //add it to the list if not found.
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
