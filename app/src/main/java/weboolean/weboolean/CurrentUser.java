package weboolean.weboolean;

import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import weboolean.weboolean.models.User;
/**
 * The purpose of this class is to manage the users while the app is running
 * By having a single static class, we can reference this class to see the user type.
 * This way, we know what type they are without having to read from the database every time.
 */
@SuppressWarnings("AssignmentToStaticFieldFromInstanceMethod")
final class CurrentUser implements Runnable {
    private static final String TAG = "CurrentUserSingleton";
    //Current user object represents the current user logged in
    @Nullable
    private static FirebaseUser firebaseUser;
    @Nullable
    private static User user;
    private static FirebaseDatabase db;
    private static final Lock mutexLock = new ReentrantLock();
    private static boolean instantiated = false;
    @Nullable
    private static Thread thread;

    // [Getters and setters] =====================================================================//
    static User getCurrentUser() {
        return user;
    }

    private static UserInfo getCurrentFirebaseUser() {
        return firebaseUser;
    }

    static void setUserInstance(User user, FirebaseUser firebaseUser)
            throws InstantiationException {
        CurrentUser.user = user;
        CurrentUser.firebaseUser = firebaseUser;
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
            CurrentUser.instantiate();
        }
    }

    private static void instantiate() {
        CurrentUser.instantiated = true;
    }

    @Override
    public void run() {
        db = FirebaseDatabase.getInstance();
        UserInfo curUser = getCurrentFirebaseUser();
        Log.d(TAG , "users/" + curUser.getUid());
        DatabaseReference reference = db.getReference("users/" + curUser.getUid());

        //add listener to user list
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                Log.d(TAG, (u == null) ? "null" : u.toString());
                mutexLock.lock();
                CurrentUser.user = u;
                mutexLock.unlock();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "The read failed: " + databaseError.getCode());
            }
        });
    }

    /** Log the user out.
     * @return true/false based on if logout succeeded.
     */
    static boolean logOutUser() {
        mutexLock.lock();
        try {
            FirebaseAuth instance = FirebaseAuth.getInstance();
            instance.signOut();
            user = null;
            firebaseUser = null;
            mutexLock.unlock();
            assert thread != null;
            thread.interrupt();
            thread.join();
            thread = null;
            instantiated = false;
            return true;
        } catch (Exception e) {
            mutexLock.unlock();
            return false;
        }
    }


    /**
     * @param u update the user state.
     */
    @SuppressWarnings("ChainedMethodCall")
    static void updateUser(User u) {
        DatabaseReference ref = db.getReference("users/");
        if (u.getUid() != null) {
            ref.child(u.getUid()).setValue(u);
        }
    }

}
