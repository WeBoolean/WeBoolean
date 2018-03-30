package weboolean.weboolean;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import weboolean.weboolean.models.Shelter;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private LatLng atlanta;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        final Button searchButton = findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "Advanced Search", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });
        atlanta = new LatLng(33.7940, -84.3880);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Create buttons and listeners
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("MapsActivity:", "Map Ready");
        // Add a marker in Atlanta and move the camera
//        mMap.addMarker(new MarkerOptions().position(atlanta).title("Marker in Atlanta"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atlanta, 8));
        //Add all shelters to position
        List<Shelter> shelters = ShelterSingleton.getShelterArrayCopy();
        LatLng pos;
        for (Shelter s : shelters) {
            pos = new LatLng(s.getLatitude(), s.getLongitude());
            mMap.addMarker(new MarkerOptions().position(pos).title(s.getName()));
        }
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            String[] perms = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};

            ActivityCompat.requestPermissions(this, perms, 12345);
            return;
        }
        enableAndSetUserLocation();
    }


    /** TODO: Actually find the user location
     * Enables myLocation and hypothetically centers camera
     * Currently just puts it to ATL
     * @throws SecurityException    If security permissions haven't been granted yet
     */
    private void enableAndSetUserLocation() throws SecurityException {
        Log.d(TAG, "Setting Location - Already Had Location Requests ");
        mMap.setMyLocationEnabled(true);
        setLocationToAtlanta();
    }

    @SuppressLint("MissingPermission") //else will only run on granted perms
    @Override
    public void onRequestPermissionsResult(int rq, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(rq, permissions, grantResults);
        if (rq == 12345) {
            if (grantResults[0] != 1 && grantResults[1] != 1) {
                // Set location to ATL
                setLocationToAtlanta();
            } else {
                enableAndSetUserLocation();
            }
        }
    }
    private void setLocationToAtlanta() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atlanta, 10));
        Log.e(TAG, "Location not Granted: missing locations ");
    }
    /**
     * Log out user when they go "back" from MapActivity
     */
    public void onBackPressed() {
        CurrentUser.logOutUser();
        finish();
    }
}
