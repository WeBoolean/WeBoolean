package weboolean.weboolean;


/**
 * Created by rajshrimali on 3/27/18.
 */

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.internal.zzdym;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.FirebaseUserMetadata;
import com.google.firebase.auth.UserInfo;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import weboolean.weboolean.models.User;
import weboolean.weboolean.models.UserType;

import static org.junit.Assert.*;

/**
 * Created by rajshrimali on 3/27/18.
 */
public class CurrentUserTest {
    @Before
    public void setup() throws InstantiationException {
        CurrentUser.setUserInstance(
                new User("8iNpJhRPUug7XQxBk5pc0Qo1oxh2", UserType.User),
                new FirebaseUser() {
                    @NonNull
                    @Override
                    public String getUid() {
                        return "8iNpJhRPUug7XQxBk5pc0Qo1oxh2";
                    }

                    @NonNull
                    @Override
                    public String getProviderId() {
                        return null;
                    }

                    @Override
                    public boolean isAnonymous() {
                        return false;
                    }

                    @Nullable
                    @Override
                    public List<String> getProviders() {
                        return null;
                    }

                    @NonNull
                    @Override
                    public List<? extends UserInfo> getProviderData() {
                        return null;
                    }

                    @NonNull
                    @Override
                    public FirebaseUser zzaq(@NonNull List<? extends UserInfo> list) {
                        return null;
                    }

                    @Override
                    public FirebaseUser zzcf(boolean b) {
                        return null;
                    }

                    @NonNull
                    @Override
                    public FirebaseApp zzbre() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public String getDisplayName() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public Uri getPhotoUrl() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public String getEmail() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public String getPhoneNumber() {
                        return null;
                    }

                    @NonNull
                    @Override
                    public zzdym zzbrf() {
                        return null;
                    }

                    @Override
                    public void zza(@NonNull zzdym zzdym) {

                    }

                    @NonNull
                    @Override
                    public String zzbrg() {
                        return null;
                    }

                    @NonNull
                    @Override
                    public String zzbrh() {
                        return null;
                    }

                    @Nullable
                    @Override
                    public FirebaseUserMetadata getMetadata() {
                        return null;
                    }

                    @Override
                    public boolean isEmailVerified() {
                        return false;
                    }
                });
    }

    @Test
    public void getCurrentUser() throws Exception {
        assert (CurrentUser.getCurrentUser().equals(new User("8iNpJhRPUug7XQxBk5pc0Qo1oxh2", UserType.User)));
    }


    @Test
    public void setUserInstance() throws Exception {
    }

    @Test
    public void logOutUser() throws Exception {
    }

}