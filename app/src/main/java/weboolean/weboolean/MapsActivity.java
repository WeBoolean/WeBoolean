package weboolean.weboolean;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import weboolean.weboolean.models.Shelter;

/**
 * Activity that is capable of plotting either a bundle of shelters or all shelters.
 */
public class MapsActivity extends FragmentActivity
        implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnMarkerClickListener {
    private static final String TAG = "MapsActivity";
    private static final int MAP_REQUEST_CODE = 12345;
    private GoogleMap mMap;
    private LatLng atlanta;
    private List<Shelter> shelters;
    private boolean filter_state = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        final Button searchButton = findViewById(R.id.search_button);
        Bundle instructions = getIntent().getExtras();
        if ((instructions != null) && !instructions.isEmpty()) {
            // manage shelter intent
            if (instructions.containsKey("shelters")) {
                Log.d(TAG, "Shelter Filter Requested");
                List<Integer> shelterIDs =  instructions.getIntegerArrayList("shelters");
                List<Shelter> shelters_list = ShelterSingleton.getShelterArrayCopy();
                shelters = new ArrayList<>();
                if (shelterIDs != null) {
                    for (Integer i : shelterIDs) {
                        shelters.add(shelters_list.get(i));
                    }
                }
                filter_state = true;
            }
            else {
                Log.d(TAG, "Instructions did not contain keys");
                shelters = ShelterSingleton.getShelterArrayCopy();
            }
        }
        else {
            Log.d(TAG, "Instructions were null or empty");
            shelters = ShelterSingleton.getShelterArrayCopy();
        }
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MapsActivity.this, "Advanced Search", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        final Button filterButton = findViewById(R.id.filter_button);
        filterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MapsActivity.this, SearchActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("map_filter", 1);
                intent.putExtras(bundle);
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
        plotShelters(shelters, mMap);

        mMap.setOnMarkerClickListener(this);
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                Context mContext = getApplicationContext();
                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);
                return info;

            }
        });
        if ((ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
                && (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)) {
            String[] perms = {"android.permission.ACCESS_FINE_LOCATION", "android.permission.ACCESS_COARSE_LOCATION"};

            ActivityCompat.requestPermissions(this, perms, MAP_REQUEST_CODE);
            return;
        }
        enableAndSetUserLocation();
    }

    /** Create a plot shelters function
     *
     * @param shelters  shelters to plot
     * @param map       map to plot on
     */
    private static void plotShelters(Iterable<Shelter> shelters, GoogleMap map) {
        LatLng pos;
        for (Shelter s : shelters) {
            pos = new LatLng(s.getLatitude(), s.getLongitude());
            Marker m = map.addMarker(
                    new MarkerOptions().position(pos).title(s.getName())
                            .snippet(s.getInformationStringSnippet()));
            m.setTag(s.getKey());
        }
    }

    /**  Actually find the user location
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
    public void onRequestPermissionsResult(int rq, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(rq, permissions, grantResults);
        if (rq == MAP_REQUEST_CODE) {
            if ((grantResults[0] != 1) && (grantResults[1] != 1)) {
                // Set location to ATL
                setLocationToAtlanta();
            } else {
                enableAndSetUserLocation();
            }
        }
    }
    private void setLocationToAtlanta() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(atlanta, 10));
        Log.e(TAG, "Location Not Granted: Missing Locations ");
    }
    /**
     * Log out user when they go "back" from MapActivity
     */
    @Override
    public void onBackPressed() {
        if (!filter_state) {
            CurrentUser.logOutUser();
        }
        finish();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }
}
