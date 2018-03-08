package weboolean.weboolean;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import weboolean.weboolean.models.User;
/**
 * Created by rajshrimali on 2/17/18.
 * The purpose of this class is to manage the users while the app is runnin g
 * By having a single static class, we can reference thsi class to see the user type
 * this way, we know what type they are without having to read from the database every time.
 *
 */
public class CurrentUser {
    //Current user object represents the current user logged in
    private static FirebaseUser t;
    private static User u;

    // [Getters and setters] =====================================================================//
    public static User getCurrentUser() {
        return u;
    }

    public static FirebaseUser getCurrentFirebaseUser() {
        return t;
    }

    public static void setUserInstance(User uu, FirebaseUser tt) {
        u = uu;
        t = tt;
    }

    // [Methods] =================================================================================//
    public static boolean logOutUser() {
        try {
            FirebaseAuth.getInstance().signOut();
            u = null;
            t = null;
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
