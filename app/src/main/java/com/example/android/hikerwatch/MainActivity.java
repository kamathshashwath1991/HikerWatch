package com.example.android.hikerwatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
               startListening();
            }
        }
    }
    public void startListening(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
    }

    public void updateInfo(Location location){
        Log.i("Location Info", location.toString());
        TextView latText= (TextView) findViewById(R.id.lat_text);
        TextView longText= (TextView) findViewById(R.id.long_text);
        TextView altTextView= (TextView) findViewById(R.id.altitude);
        TextView accTextView= (TextView) findViewById(R.id.accuracy);
        latText.setText("Latitude: " + location.getLatitude());
        longText.setText("Longitude: " + location.getLongitude());
        altTextView.setText("Altitude: " + location.getAltitude());
        accTextView.setText("Accuracy: " + location.getAccuracy());

        Geocoder geocoder= new Geocoder(getApplicationContext(), Locale.getDefault());
        try {

            String address= "Could not find address";
            List<Address> list_address= geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if (list_address!=null && list_address.size()>0){
                Log.i("Place info: ",list_address.get(0).toString());
                address = "";

                if (list_address.get(0).getSubThoroughfare()!=null){
                    address += list_address.get(0).getSubThoroughfare()+ "\n ";
                }
                
                if (list_address.get(0).getLocality()!=null){
                    address += list_address.get(0).getLocality()+ "\n";
                }
                if (list_address.get(0).getPostalCode()!=null){
                    address+=list_address.get(0).getPostalCode()+ "\n";
                }
                if (list_address.get(0).getCountryName()!=null){
                    address+=list_address.get(0).getCountryName();
                }
                TextView addressText= (TextView) findViewById(R.id.address);
                addressText.setText("Address: "+ address);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager= (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateInfo(location);
                //Toast.makeText(getApplicationContext(),location.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

                Location lastUnknownLocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                if (lastUnknownLocation!=null){
                    updateInfo(lastUnknownLocation);
                }
            }
        }

    }

}

