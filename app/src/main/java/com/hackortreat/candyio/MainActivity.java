package com.hackortreat.candyio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.SharedPreferences;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import io.radar.sdk.model.RadarAddress;
import io.radar.sdk.model.RadarEvent;
import io.radar.sdk.model.RadarUser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import io.radar.sdk.Radar;

import static com.google.android.gms.location.FusedLocationProviderClient.*;

public class MainActivity<RadarCallback> extends AppCompatActivity implements OnMapReadyCallback {

    static final int DEFAULT_INTERVAL_MODIFIER = 30;
    static final int FASTEST_INTERVAL_MODIFIER = 5;
    static final int PERMISSIONS_FINE_LOCATION = 1;

    private LocationRequest locationRequest;
    LocationCallback locationCallback;
    FusedLocationProviderClient locationProvider;

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private GoogleMap MAP;

    private TextView location_1, location_2, location_3;
    private Address addr_1, addr_2, addr_3;
    private double lat, lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000 * DEFAULT_INTERVAL_MODIFIER);
        locationRequest.setFastestInterval(1000 * FASTEST_INTERVAL_MODIFIER);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                // save the location
                Location location = locationResult.getLastLocation();
                updateUIValues(location);
            }
        };

        location_1 = findViewById(R.id.local_1);
        location_2 = findViewById(R.id.local_2);
        location_3 = findViewById(R.id.local_3);
        String prj_live_pk_54e543cf5e684f0a312a2012361a8c25c048b830 = new String();
        Radar.initialize(this, prj_live_pk_54e543cf5e684f0a312a2012361a8c25c048b830);
        id(this);
        Radar.ipGeocode(new Radar.RadarIpGeocodeCallback() {
            @Override
            public void onComplete(Radar.RadarStatus status, RadarAddress address) {
                Address location2 = null;
                addr_2= location2;
                location_2.setText(addr_2.getAddressLine(0));
            }
        });
        
        /*
        //How to use HttpUtils
        HttpUtils example = new HttpUtils();
        //example.buildUserPostRequest("ff7dffc4-f967-4deb-a943-393523740ec9");
        //example.buildSymptomsPostRequest("ff7dffc4-f967-4deb-a943-393523740ec9",4,false);
        final String[] theReponse = {""};
        GetRequestListener mListener = new GetRequestListener() {
            @Override
            public String onSuccess(String response) {
                theReponse[0] = response;
                return response;
            }
            @Override
            public String onFailure(String errorMessage) {
                theReponse[0] = null;
                return errorMessage;
            }
        };
        System.out.println(theReponse[0]);
        example.registerOnGetRequestEventListener(mListener);
        example.buildGetRequest("user","ff7dffc4-f967-4deb-a943-393523740ec9", null);
         */


        updateGPS();
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSIONS_FINE_LOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) updateGPS();
            else {
                Toast.makeText(this, "This app requires permission to be granted in order to work properly", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateGPS() {

        locationProvider = LocationServices.getFusedLocationProviderClient(MainActivity.this);

        int isFineLocationOn = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionsCheck = PackageManager.PERMISSION_GRANTED;

        if (isFineLocationOn == permissionsCheck) {
            locationProvider.getLastLocation().addOnSuccessListener(this, location -> {
                if (location == null) {
                    location = new Location("");
                } else {
                    updateUIValues(location);
                }

            });
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_FINE_LOCATION);
    }

    private void updateUIValues(Location location) {

        try {

            Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
            lat = location.getLatitude();
            lon = location.getLongitude();
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 3);

            if (addresses.size() > 0) {
                addr_1 = addresses.get(0);
                addr_3 = addresses.get(1);
                location_1.setText(addr_1.getAddressLine(0));
                location_3.setText(addr_3.getAddressLine(0));

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        MAP = googleMap;
        LatLng area = new LatLng(lat, lon);
        MAP.addMarker(new MarkerOptions().position(area).title("Your recent area"));
        MAP.moveCamera(CameraUpdateFactory.newLatLng(area));
    }

    /*
    source: https://ssaurel.medium.com/how-to-retrieve-an-unique-id-to-identify-android-devices-6f99fd5369eb
    generates a unique user id
     */

    public synchronized static String id(Context context) {

        if (uniqueID == null) {

            SharedPreferences sharedPrefs = context.getSharedPreferences(PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {

                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }

        return uniqueID;
    }

}