package weboolean.weboolean.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by rajshrimali on 2/17/18.
 *
 * The purpose of this is to represent a User in our models.
 *
 * We never actually save the user, but we probably should.
 * Instead, right now, we save the UID on user creation
 * Upon login, we simply instantiate a new user.
 */
@IgnoreExtraProperties
public class User {
    public String uid;
    public UserType usertype;

    public User(){
        //Default constructor required;
    }

    public User(String UID, UserType type) {
        this.uid = uid;
        this.usertype = type;
    }
}
