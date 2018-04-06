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
 * The purpose of this class is to manage the users while the app is running
 * By having a single static class, we can reference this class to see the user type.
 * This way, we know what type they are without having to read from the database every time.
 */
public class CurrentUser implements Runnable {
    private static final String TAG = "CurrentUserSingleton";
    //Current user object represents the current user logged in
    private static FirebaseUser firebaseUser;
    private static User user;
    private static FirebaseDatabase db;
    private static DatabaseReference reference;
    private static final Lock mutexlock = new ReentrantLock();
    private static boolean instantiated = false;
    private static boolean userSet = false;
    private static Thread thread;

    // [Getters and setters] =====================================================================//
    static User getCurrentUser() {
        return user;
    }

    static FirebaseUser getCurrentFirebaseUser() {
        return firebaseUser;
    }

    static void setUserInstance(User user, FirebaseUser firebaseUser) throws InstantiationException {
        CurrentUser.user = user;
        CurrentUser.firebaseUser = firebaseUser;
        userSet = true;
        thread = new Thread(new CurrentUser());
        thread.start();
    }

    // [Methods] =================================================================================//

    /** Instantiation for worker thread
     *
     * This should NEVER be instantiated outside of this class.
     * @throws InstantiationException thrown when another CurrentUser instantiation attempt made
     */
    private CurrentUser() throws InstantiationException {
        if (CurrentUser.instantiated) {
            throw new InstantiationException("Only one current user is allowed at once.");
        } else {
            CurrentUser.instantiated = true;
        }
    }

    @Override
    public void run() {
        db = FirebaseDatabase.getInstance();

        Log.d(TAG , "users/" + getCurrentFirebaseUser().getUid());
        reference = db.getReference("users/" + getCurrentFirebaseUser().getUid());

        //add listener to user list
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                Log.d(TAG, u == null ? "null" : u.toString());
                mutexlock.lock();
                CurrentUser.user = u;
                mutexlock.unlock();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });
    }

    /** Log the user out.
     * @return true/false based on if logout succeeded.
     */
    static boolean logOutUser() {
        mutexlock.lock();
        try {
            FirebaseAuth.getInstance().signOut();
            user = null;
            firebaseUser = null;
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


    /**
     * @param u update the user state.
     */
    static void updateUser(User u) {
        DatabaseReference ref = db.getReference("users/");
        ref.child(u.getUid()).setValue(u);
    }

}
