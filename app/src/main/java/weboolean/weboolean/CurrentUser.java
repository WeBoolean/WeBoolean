package weboolean.weboolean;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import weboolean.weboolean.models.User;
/**
 * Created by rajshrimali on 2/17/18.
 * The purpose of this class is to manage the users while the app is runnin g
 * By having a single static class, we can reference thsi class to see the user type
 * this way, we know what type they are without having to read from the database every time.
 *
 */
public class CurrentUser implements Runnable {
    private static final String TAG = "CurrentUserSingleton";
    //Current user object represents the current user logged in
    private static FirebaseUser t;
    private static User u;
    private static FirebaseDatabase db;
    private static DatabaseReference reference;
    private static final Lock mutexlock = new ReentrantLock();
    private static boolean instantiated = false;
    private static boolean userSet = false;
    private static Thread thread;

    // [Getters and setters] =====================================================================//
    public static User getCurrentUser() {
        return u;
    }

    public static FirebaseUser getCurrentFirebaseUser() {
        return t;
    }

    public static void setUserInstance(User uu, FirebaseUser tt) throws InstantiationException {
        u = uu;
        t = tt;
        userSet = true;
        thread = new Thread(new CurrentUser());
        thread.start();
    }

    // [Methods] =================================================================================//

    public CurrentUser() throws InstantiationException {
        if (CurrentUser.instantiated) {
            throw new InstantiationException("Only one current user is allowed at once.");
        } else {
            CurrentUser.instantiated = true;
        }
    }

    public void run() {
        db = FirebaseDatabase.getInstance();

        while (!userSet) {
            Log.d(TAG, "Waiting for userset");
        };

        reference = db.getReference("user/" + getCurrentFirebaseUser().getUid());

        //add listener to user list
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                Log.d(TAG, u.toString());
                mutexlock.lock();
                CurrentUser.u = u;
                mutexlock.unlock();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    public static boolean logOutUser() {
        mutexlock.lock();
        try {
            FirebaseAuth.getInstance().signOut();
            u = null;
            t = null;
            mutexlock.unlock();
            thread.interrupt();
            thread.join();
            thread = null;
            instantiated = false;
            return true;
        } catch (Exception e) {
            mutexlock.unlock();
            return false;
        }
    }

}
