package weboolean.weboolean.models;

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
@IgnoreExtraProperties
public class User implements Serializable {

    private String uid;
    private UserType usertype;
    private String sex;
    private Boolean fam;
    private int dependents;
    private int youngest;
    private String spouse;
    private Boolean vet;
    private int age;
    private boolean checkedIn;
    private int currentShelter;
    private boolean locked;


    public User() {
        this.uid = null;
        this.usertype = null;
        this.sex = null;
        this.fam = false;
        this.dependents = 0;
        this.youngest = 0;
        this.spouse = null;
        this.vet = null;
        this.age = 18;
        this.currentShelter = -1;
        this.locked = false;

    }

    public User(String UID, UserType type) {
        this();
        this.uid = UID;
        this.usertype = type;
    }

    public User(String uid1, UserType usertype1, String sex1, Boolean fam1, int dep1, int young1,
                String spouse1, Boolean vet1, int age1, boolean checked1, int curr1, boolean locked1) {
        this(uid1, usertype1);
        this.sex = sex1;
        this.fam = fam1;
        this.dependents = dep1;
        this.youngest = young1;
        this.spouse = spouse1;
        this.vet = vet1;
        this.age = age1;
        this.checkedIn = checked1;
        this.currentShelter = curr1;
        this.locked = locked1;
    }

    public boolean getCheckedIn() {
        return checkedIn;
    }

    public int getCurrentShelter() {
        return currentShelter;
    }

    public int getFamilySize() {
        int size = 1;
        if (dependents != 0) {
            size += dependents;
        } if (spouse != null) {
            size += 1;
        } return size;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public UserType getUserType() {
        return usertype;
    }

    public void setUserType(UserType usertype) {
        this.usertype = usertype;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Boolean getFam() {
        return fam;
    }

    public void setFam(Boolean fam) {
        this.fam = fam;
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

    public Boolean getVet() {
        return vet;
    }

    public void setVet(Boolean vet) {
        this.vet = vet;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public void setCheckedIn(boolean checkedIn) {
        this.checkedIn = checkedIn;
    }

    public void setCurrentShelter(int currentShelter) {
        this.currentShelter = currentShelter;
    }

    public boolean getLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
