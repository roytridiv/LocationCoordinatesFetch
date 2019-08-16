package com.example.mymap;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap m;
    private static final String KEY_LOCATION = "location";
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private GeoDataClient placeDetectionClient;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private GoogleApiClient googleApiClient;


    private final LatLng defaultLocation = new LatLng(23.7449105, 90.3983921);

    private LatLng current ;
    private boolean locationGrante;

    private Location lastknownlocation;
    private String s = "";


    Button b;
    TextView tx, tx2;
    private  boolean l = true;

    private static final String t = "Main Activity";
    private static final int err = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retrieve location
        if (savedInstanceState != null)
            lastknownlocation = savedInstanceState.getParcelable(KEY_LOCATION);


        setContentView(R.layout.activity_main);

       // googleApiClient = new GoogleApiClient.Builder(MainActivity.this).addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) MainActivity.this).addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) MainActivity.this).addApi(LocationServices.API).build();


        //requestPermission();

        placeDetectionClient = Places.getGeoDataClient(MainActivity.this, null);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        b = (Button) findViewById(R.id.m);
        tx = (TextView) findViewById(R.id.text);
        //getLocationPermission();


        if (ok()) {
            init();
        }
    }

//    @Override
//    protected void onStart(){
//        super.onStart();
//        googleApiClient.connect();
//    }
//
//    @Override
//    protected void onStop(){
//        if(googleApiClient.isConnected()){
//            googleApiClient.disconnect();
//        }
//    }

    private void init() {
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(MainActivity.this , mapac.class));
                Log.d("MY_APP_DEBUG" , "button pressed");
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    Log.d("MY_APP_DEBUG" , "permission");
                   requestPermission();
                }
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            Log.d("MY_APP_DEBUG" , "location found");
                            if(l == true) {
                                Toast.makeText(MainActivity.this, "Press one more time to get LOCATION NAME", Toast.LENGTH_LONG).show();
                                l= false;
                            }
                            tx2 = (TextView) findViewById(R.id.text2);
                            tx2.setText(location.toString());
                            current = new LatLng(location.getLatitude(), location.getLongitude());
                        }
                    }
                });

                tx.setText(cityName(current));
            }
        });
    }

    public boolean ok(){

        boolean res = false;
        Log.d("MY_APP_DEBUG","okok");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(MainActivity.this);

        if(available == ConnectionResult.SUCCESS){
            Log.d("MY_APP_DEBUG" , "its working");
            res = true;
        }else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            System.out.println("n");
            Log.d("MY_APP_DEBUG" , "error occured" );
           // Dialog d = GoogleApiAvailability.getInstance().getErrorDialog(MainActivity.this, available , Error);
            res =  false;

        }else{
            Toast.makeText(MainActivity.this , "Can not make a map" , Toast.LENGTH_SHORT).show();
        }
        return res;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private  String cityName(LatLng mayCoordinates) {

        String n = "";
        Geocoder geo = new Geocoder(MainActivity.this, Locale.getDefault());
        try {

            List<Address> addresses = geo.getFromLocation(mayCoordinates.latitude, mayCoordinates.longitude, 1);
            String a = addresses.get(0).getAddressLine(0);
            n = addresses.get(0).getLocality();
            n= n + "\n" + addresses.get(0).getFeatureName()+ ","
                    + addresses.get(0).getSubLocality() + ","
                    + addresses.get(0).getPremises() + ","
                    + addresses.get(0).getAdminArea() + ","
                    + addresses.get(0).getCountryName() + ","
                    + addresses.get(0).getCountryCode() + ","
                    + addresses.get(0).getPostalCode();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return n;
    }
//
//    /**
//     * Prompts the user for permission to use the device location.
//     */
//    private void getLocationPermission() {
//        /*
//         * Request location permission, so that we can get the location of the
//         * device. The result of the permission request is handled by a callback,
//         * onRequestPermissionsResult.
//         */
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
//                android.Manifest.permission.ACCESS_FINE_LOCATION)
//                == PackageManager.PERMISSION_GRANTED) {
//            mLocationPermissionGranted = true;
//        } else {
//            ActivityCompat.requestPermissions(this,
//                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
//                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
//        }
//    }
//
//    /**
//     * Handles the result of the request for location permissions.
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           @NonNull String permissions[],
//                                           @NonNull int[] grantResults) {
//        mLocationPermissionGranted = false;
//        switch (requestCode) {
//            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
//                // If request is cancelled, the result arrays are empty.
//                if (grantResults.length > 0
//                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    mLocationPermissionGranted = true;
//                }
//            }
//        }
//    }

    public void requestPermission(){
        ActivityCompat.requestPermissions(MainActivity.this , new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
    }


}
