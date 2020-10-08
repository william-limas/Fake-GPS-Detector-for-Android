package com.example.checkfakegps;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    Button btLocation;
    TextView textView1,textView2,textView3,textView4,textView5,textView6,textView7;
    FusedLocationProviderClient fusedLocationProviderClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btLocation = findViewById(R.id.bt_location);
        textView1 = findViewById(R.id.text_view1);
        textView2 = findViewById(R.id.text_view2);
        textView3 = findViewById(R.id.text_view3);
        textView4 = findViewById(R.id.text_view4);
        textView5 = findViewById(R.id.text_view5);
        textView6 = findViewById(R.id.text_view6);
        textView7 = findViewById(R.id.text_view7);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    getLocation();
                    checkForAllowMockLocationsApps(MainActivity.this);
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
                }
            }
        });

    }

    //GET LOCATION
    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                Location location = task.getResult();
                if(location != null){
                    try {
                        //using geocoder to get location
                        Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                        List<Address> addresses = geocoder.getFromLocation(
                                location.getLatitude(),location.getLongitude(),1
                        );
                        //display latitude
                        textView1.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Latitude :</br><br></font>"
                                + addresses.get(0).getLatitude()
                        ));
                        //display longitude
                        textView2.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Longitude :</br><br></font>"
                                        + addresses.get(0).getLongitude()
                        ));
                        //display country
                        textView3.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Country :</br><br></font>"
                                        + addresses.get(0).getCountryName()
                        ));
                        //display locality
                        textView4.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Locality :</br><br></font>"
                                        + addresses.get(0).getLocality()
                        ));
                        //display address
                        textView5.setText(Html.fromHtml(
                                "<font color='#6200EE'><b>Address :</br><br></font>"
                                        + addresses.get(0).getAddressLine(0)
                        ));
                        //CHECK IF USER IS USING FAKE GPS OR NOT
                        if(location.isFromMockProvider()==true)
                        {
                            textView6.setText("Lokasi ini didapat dengan FAKE GPS! Silakan matikan Fake GPS mu!");
                        }
                        else
                        {
                            textView6.setText("Lokasi ini aman, terimakasih karena tidak menggunakan Fake GPS");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    //CHECK ADA BERAPA APLIKASI FAKE GPS YANG TERINSTALL DI DALAM DEVICE USER
    public boolean checkForAllowMockLocationsApps(Context context) {

        int count = 0;

        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages =
                pm.getInstalledApplications(PackageManager.GET_META_DATA);

        for (ApplicationInfo applicationInfo : packages) {
            try {
                PackageInfo packageInfo = pm.getPackageInfo(applicationInfo.packageName,
                        PackageManager.GET_PERMISSIONS);

                // Get Permissions
                String[] requestedPermissions = packageInfo.requestedPermissions;

                if (requestedPermissions != null) {
                    for (int i = 0; i < requestedPermissions.length; i++) {
                        if (requestedPermissions[i]
                                .equals("android.permission.ACCESS_MOCK_LOCATION")
                                && !applicationInfo.packageName.equals(context.getPackageName())) {
                            count++;
                        }
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                Log.e("Error", "Got exception " + e.getMessage());
            }
        }

        if (count > 0) {
            textView7.setText(count + " aplikasi fake gps terdeksi terinstall di device mu");
            return true;
        } else {
            textView7.setText("tidak ada aplikasi fake gps yang terinstall di device mu");
            return false;
        }
    }
}
