package com.hackortreat.candyio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.util.Arrays;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

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
    private String BASE_URL = "https://candyio-back.herokuapp.com/";

    private static String uniqueID = null;
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private GoogleMap MAP;

    private TextView location_1, location_2, location_3;
    private Address addr_1, addr_2, addr_3;
    private double lat, lon, lat1, lon1, lat2, lon2, lat3, lon3, lat4, lon4, lat5, lon5;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button candyqualbutton = findViewById(R.id.Candy);
        Button reportbutton = findViewById(R.id.notify);
        /*readincomedata();*/

        candyqualbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCandy();
            };

        });
        reportbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email();
            };

        });
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

    /*private void readincomedata() {
        InputStream is = getResources().openRawResource(R.raw.data);
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(is, Charset.forName("UTF-8"))
        );
        Integer line = "";
        try {
            while (line = reader.readLine() !=null) {
                //split by ","
                String[] tokens = line.split(",");
                //Read data
                queriedzip sample = new queriedzip();
                sample.setZipcode(Integer.parseInt(tokens[0]));
                sample.setMedian(Integer.parseInt(tokens[1]));

            }
        } catch (IOException ioException) {
            Log.wtf("MyActivity", "Error reading data file on line" + line, e);
            e.printStackTrace();
        }


    }
    private List<queriedzip> mediandata= new ArrayList<>();
*/
    private void email() {
        Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto","report@candy.io", null));
        intent.putExtra(Intent.EXTRA_SUBJECT, "Candy.io Error");
        intent.putExtra(Intent.EXTRA_TEXT, "Please explain your error");
        startActivity(Intent.createChooser(intent, "Choose an Email client :"));

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
                addr_2 = addresses.get(1);
                addr_3 = addresses.get(2);

                location_1.setText(addr_1.getAddressLine(0));
                location_2.setText(addr_2.getAddressLine(0));
                location_3.setText(addr_3.getAddressLine(0));

                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);
                mapFragment.getMapAsync(this);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String requestData(String urlstring) {
        try {
            final String[] response = new String[1];
            final CountDownLatch latch = new CountDownLatch(1);
            new Thread( new Runnable(){
                public void run(){
                    try{
                        Log.d("START","Starting GET");
                        URL url = new URL(urlstring);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(5000);
                        connection.setReadTimeout(5000);
                        connection.connect();
                        Log.d("INFO",urlstring);
                        Log.d("INFO",Integer.toString(connection.getResponseCode()));
                        Log.d("INFO",connection.getResponseMessage());
                        BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        String content = "", line;
                        while ((line = rd.readLine()) != null) {
                            content += line + "\n";
                        }
                        response[0] = content;
                        Log.d("SUCCESS",response[0]);
                        latch.countDown();
                    }
                    catch(Exception ex){
                        Log.d("ERROR", "Error Processing Get Request...");
                        for(int i = 0;i<ex.getStackTrace().length;i++){
                            Log.d("ERROR",ex.getStackTrace()[i].toString());
                        }
                        latch.countDown();
                    }
                }

            }).start();
            latch.await();
            return response[0];
        }
        catch(Exception ex){
            return "";
        }
    }

    private void openCandy() {
        Intent intent = new Intent(this, CandyQual.class);
        startActivity(intent);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        try{
            String zipp = addr_1.getPostalCode();

            String yakakus = requestData(BASE_URL + zipp);
            JSONObject yakakuss = null;
            try {
                yakakuss = new JSONObject(yakakus);
            } catch (JSONException e) {
                e.printStackTrace();
            }

                Integer med = yakakuss.getInt("Median");

                Integer zip = Integer.parseInt(zipp);
                Integer zip1 = zip + 1;

                String yakakus1 = requestData(BASE_URL + zip1);
                JSONObject yakakuss1 = null;

                yakakuss1 = new JSONObject(yakakus1);

                Integer med1 = yakakuss1.getInt("Median");


                Integer zip2 = zip + 2;

                String yakakus2 = requestData(BASE_URL + zip2);
                JSONObject yakakuss2 = null;

                yakakuss2 = new JSONObject(yakakus2);

                Integer med2 = yakakuss2.getInt("Median");


                Integer zip3 = zip - 1;

                String yakakus3 = requestData(BASE_URL + zip3);
                JSONObject yakakuss3 = null;

                yakakuss3 = new JSONObject(yakakus3);

                Integer med3 = yakakuss3.getInt("Median");


                Integer zip4 = zip - 2;

                String yakakus4 = requestData(BASE_URL + zip4);
                JSONObject yakakuss4 = null;

                yakakuss4 = new JSONObject(yakakus4);

                Integer med4 = yakakuss4.getInt("Median");




                Geocoder coder = new Geocoder(this);
                ArrayList<Address> addresses = null;
                MAP = googleMap;
                JSONObject[] fullArray = new JSONObject[]{yakakuss,yakakuss1,yakakuss2,yakakuss3,yakakuss4};
                int[] medianArray = new int[]{med, med1, med2, med3, med4};
                Arrays.sort(medianArray);
                for(int i=0;i<medianArray.length;i++){
                    for(JSONObject data : fullArray){
                        switch(i){
                            case 0:
                                if(data.getInt(("Median"))==medianArray[i]){
                                    addresses = (ArrayList<Address>) coder.getFromLocationName(data.getString("Zip"),3);
                                    for(Address add : addresses){
                                        lat = add.getLatitude();
                                        lon = add.getLongitude();
                                    };
                                    LatLng area = new LatLng(lat, lon);
                                    MAP.addMarker(new MarkerOptions().position(area).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                }
                                break;
                            case 1:
                                if(data.getInt(("Median"))==medianArray[i]){
                                    addresses = (ArrayList<Address>) coder.getFromLocationName(data.getString("Zip"),3);
                                    for(Address add : addresses){
                                        lat = add.getLatitude();
                                        lon = add.getLongitude();
                                    };
                                    LatLng area = new LatLng(lat, lon);
                                    MAP.addMarker(new MarkerOptions().position(area).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                                }
                                break;
                            case 2:
                                if(data.getInt(("Median"))==medianArray[i]){
                                    addresses = (ArrayList<Address>) coder.getFromLocationName(data.getString("Zip"),3);
                                    for(Address add : addresses){
                                        lat = add.getLatitude();
                                        lon = add.getLongitude();
                                    };
                                    LatLng area = new LatLng(lat, lon);
                                    MAP.addMarker(new MarkerOptions().position(area).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                                }
                                break;
                            case 3:
                                if(data.getInt(("Median"))==medianArray[i]){
                                    addresses = (ArrayList<Address>) coder.getFromLocationName(data.getString("Zip"),3);
                                    for(Address add : addresses){
                                        lat = add.getLatitude();
                                        lon = add.getLongitude();
                                    };
                                    LatLng area = new LatLng(lat, lon);
                                    MAP.addMarker(new MarkerOptions().position(area).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                }
                                break;
                            case 4:
                                if(data.getInt(("Median"))==medianArray[i]){
                                    addresses = (ArrayList<Address>) coder.getFromLocationName(data.getString("Zip"),3);
                                    for(Address add : addresses){
                                        lat = add.getLatitude();
                                        lon = add.getLongitude();
                                    };
                                    LatLng area = new LatLng(lat, lon);
                                    MAP.addMarker(new MarkerOptions().position(area).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                                }
                                break;
                        }
                    }
                }

/*
                try {
                    addresses = (ArrayList<Address>) coder.getFromLocationName(rnk5.toString(),3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(Address add : addresses){
                    lat1 = add.getLatitude();
                    lon1 = add.getLongitude();
                };

                try {
                    addresses = (ArrayList<Address>) coder.getFromLocationName(rnk4.toString(),3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(Address add2 : addresses){
                    lat2 = add2.getLatitude();
                    lon2 = add2.getLongitude();
                };

                try {
                    addresses = (ArrayList<Address>) coder.getFromLocationName(rnk3.toString(),3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(Address add3 : addresses){
                    lat3 = add3.getLatitude();
                    lon3 = add3.getLongitude();
                };

                try {
                    addresses = (ArrayList<Address>) coder.getFromLocationName(rnk2.toString(),3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(Address add4 : addresses){
                    lat4 = add4.getLatitude();
                    lon4 = add4.getLongitude();
                };
                try {
                    addresses = (ArrayList<Address>) coder.getFromLocationName(rnk1.toString(),3);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(Address add4 : addresses){
                    lat5 = add4.getLatitude();
                    lon5 = add4.getLongitude();
                };

                MAP = googleMap;
                LatLng area1 = new LatLng(lat5, lon5);
                LatLng area2 = new LatLng(lat1, lon1);
                LatLng area3 = new LatLng(lat2, lon2);
                LatLng area4 = new LatLng(lat3, lon3);
                LatLng area5 = new LatLng(lat4, lon4);
                LatLng area = new LatLng(lat, lon);

                MAP.addMarker(new MarkerOptions().position(area1).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                MAP.addMarker(new MarkerOptions().position(area2).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
                MAP.addMarker(new MarkerOptions().position(area3).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW)));
                MAP.addMarker(new MarkerOptions().position(area4).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                MAP.addMarker(new MarkerOptions().position(area5).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                */

                LatLng area = new LatLng(lat, lon);
                MAP.addMarker(new MarkerOptions().position(area).title("Current location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                MAP.moveCamera(CameraUpdateFactory.newLatLng(area));


        }  catch (Exception e) {
        e.printStackTrace();
        }

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