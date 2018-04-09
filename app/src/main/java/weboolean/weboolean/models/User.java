package weboolean.weboolean.models;

import android.support.annotation.Nullable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;

/**
 * Created by rajshrimali on 2/17/18.
 *
 * The purpose of this is to represent a User in our models.
 *
 * We never actually save the user, but we probably should.
 * Instead, right now, we save the UID on user creation
 * Upon login, we simply instantiate a new user.
 */
@SuppressWarnings("ConstructorWithTooManyParameters")
@IgnoreExtraProperties
public class User implements Serializable {

    // Private variables for user data
    @Nullable
    private String uid;
    @Nullable
    private UserType userType;
    @Nullable
    private String sex;
    private Boolean family;
    private int dependents;
    private int youngest;
    @Nullable
    private String spouse;
    @Nullable
    private Boolean veteran;
    private int age;
    private boolean checkedIn;
    private int currentShelter;
    private boolean locked;

    // [Constructors] ============================================================================//
    public User() {
        this.uid = null;
        this.userType = null;
        this.sex = null;
        this.family = false;
        this.dependents = 0;
        this.youngest = 0;
        this.spouse = null;
        this.veteran = null;
        this.age = 0;
        this.currentShelter = -1;
        this.locked = false;

    }

    public User(String UID, UserType type) {
        this();
        this.uid = UID;
        this.userType = type;
    }

    public User(String uid, UserType usertype, String sex, Boolean fam, int dep, int young,
                String spouse, Boolean vet, int age, boolean checked, int curr, boolean locked) {
        this(uid, usertype);
        this.sex = sex;
        this.family = fam;
        this.dependents = dep;
        this.youngest = young;
        this.spouse = spouse;
        this.veteran = vet;
        this.age = age;
        this.checkedIn = checked;
        this.currentShelter = curr;
        this.locked = locked;
    }

    // [Getters and Setters] =====================================================================//
    public boolean getCheckedIn() {
        return checkedIn;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public int getCurrentShelter() {
        return currentShelter;
    }

    public void setCurrentShelter(int currentShelter) {
        this.currentShelter = currentShelter;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType usertype) {
        this.userType = usertype;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Boolean getFamily() {
        return family;
    }

    public void setFamily(Boolean family) {
        this.family = family;
    }

    public int getDependents() {
        return dependents;
    }

    public void setDependents(int dependents) {
        this.dependents = dependents;
    }

    public int getYoungest() {
        return youngest;
    }

    public void setYoungest(int youngest) {
        this.youngest = youngest;
    }

    public String getSpouse() {
        return spouse;
    }

    public void setSpouse(String spouse) {
        this.spouse = spouse;
    }

    public Boolean getVeteran() {
        return veteran;
    }

    public void setVeteran(Boolean veteran) {
        this.veteran = veteran;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    // [Object Methods] ===========================================================================/
    @Override
    public String toString() {
        return getUid() + getSex();
    }

    /**
     * @return Computed family size for logic.
     */
    public int getFamilySize() {
        int size = 1;
        if (dependents != 0) {
            size += dependents;
        }
        if (spouse != null) {
            size += 1;
        } return size;
    }

}
