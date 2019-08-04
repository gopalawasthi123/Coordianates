package com.example.coordianates;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FusedLocationProviderClient client;
    LocationRequest request;
    double longi, latti;
    boolean mLocationPermissionGranted;
    GoogleApiClient googleApiClient;
    Geocoder geocoder;
    Location mLastKnownLocation;
    public static final String  TAG  = "LOCATION";
    public static final int DEFAULT_ZOOM = 17;
    public static final int  PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    //public float[] distance = new float[30];
    Location mDefaultLocation;
    List<UserLocation> mylist = new ArrayList<>();
    MyDBHelper myDBHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        client = LocationServices.getFusedLocationProviderClient(this);
        myDBHelper = MyDBHelper.getInstance(this);
         database = myDBHelper.getWritableDatabase();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        } else {

            mLocationPermissionGranted= true;

        }
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        updateLocationUI();
        
        getDeviceLocation();
      // mMap.addMarker(new MarkerOptions().position(new LatLng(latti, longi)).title("Marker"));
       //mMap.addCircle( new CircleOptions().center(new LatLng(latti,longi)).radius(30.0).strokeColor(Color.BLUE));

    }


    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    private void getDeviceLocation() {

        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                final Task locationResult = client.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                             mLastKnownLocation =(Location) task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()),DEFAULT_ZOOM));
                         final Circle mcircle =  mMap.addCircle(new CircleOptions().center(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude())).radius(5.0).strokeColor(Color.RED).strokeWidth(3.0f));
                          Marker marker =  mMap.addMarker(new MarkerOptions().position(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude())).title("Marker"));
                          mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
                              @Override
                              public void onMyLocationChange(Location location) {
                                  float[] distance = new float[2];


                                  Location.distanceBetween( location.getLatitude(), location.getLongitude(),
                                          mcircle.getCenter().latitude, mcircle.getCenter().longitude, distance);

                                  if( distance[0] > mcircle.getRadius() ){
                                      Toast.makeText(getBaseContext(), "Outside, distance from center: " + distance[0] + " radius: " + mcircle.getRadius(), Toast.LENGTH_LONG).show();
                                    long date = System.currentTimeMillis();

                                    String mycoordinates = location.getLatitude() + "," + location.getLongitude();
                                      mylist.add(new UserLocation(mycoordinates,date));
                                      ContentValues contentValues=new ContentValues();
                                      contentValues.put(MyDBHelper.COORDIANATES,mycoordinates);
                                      contentValues.put(MyDBHelper.TIME_STAMP,date+"");
                                      long id= database.insert(MyDBHelper.TABLE_NAME,null,contentValues);
                                      int ee=(int)id;

                                  }
                              }
                          });
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());

                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mDefaultLocation.getLatitude(),
                                            mDefaultLocation.getLongitude()),DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }

                    }
                });
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);

            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }
}

