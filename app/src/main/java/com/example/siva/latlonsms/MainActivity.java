package com.example.siva.latlonsms;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private Button send;
    private EditText phno;
    private boolean mLocationPermissionsGranted=false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private String mesg;
    AppLocationService appLocationService;
    Location gpsLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        send=(Button) findViewById(R.id.send);
        phno=(EditText) findViewById(R.id.phno);
        appLocationService = new AppLocationService(MainActivity.this);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] permissions = {Manifest.permission.SEND_SMS,
                        Manifest.permission.READ_SMS,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION};

                if(ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(),
                        Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED){
                    if(ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(),
                            Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED){
                        if(ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(),
                                Manifest.permission.ACCESS_COARSE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                            if(ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(),
                                    Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED){
                                mLocationPermissionsGranted = true;
                            }
                            else {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        permissions,
                                        LOCATION_PERMISSION_REQUEST_CODE);
                            }
                        }else {
                            ActivityCompat.requestPermissions(MainActivity.this,
                                    permissions,
                                    LOCATION_PERMISSION_REQUEST_CODE);
                        }

                    }else{
                        ActivityCompat.requestPermissions(MainActivity.this,
                                permissions,
                                LOCATION_PERMISSION_REQUEST_CODE);
                    }
                }else{
                    ActivityCompat.requestPermissions(MainActivity.this,
                            permissions,
                            LOCATION_PERMISSION_REQUEST_CODE);
                }
                if (mLocationPermissionsGranted) {
                    gpslocation();
                    String phono = phno.getText().toString();
                    int flag = 0;
                    if (phono == null || phono.length() == 0) {
                        Toast.makeText(MainActivity.this, "Enter a number to send", Toast.LENGTH_SHORT).show();
                        flag = 1;
                    }
                    if (flag == 0) {
                        Log.d("MainActivity sms: "," "+mesg);
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phono, null, mesg, null, null);
                        Toast.makeText(MainActivity.this, "Message sent!", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(MainActivity.this,"Permission is not granted!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        Log.d("MainActivity: ","Before sms intent");

        Intent sms_intent=getIntent();
        Bundle b=sms_intent.getExtras();
        TextView tv=(TextView)findViewById(R.id.txtview);

        if(b!=null) {
            if (b.getString("sms_str") != null) {
                if (b.getString("message") != null) {
                    Log.d("MainActivity: ", " " + b.getString("sms_str"));
                    tv.setText(b.getString("sms_str"));
                    String[] s = b.getString("message").split(" ");
                    Log.d("MainActivity: ", " " + s[0]);
                    Log.d("MainActivity: ", " " + s[1]);
                    Log.d("MainActivity: ", " " + s[2]);
                    Log.d("MainActivity: ", " " + s[3]);
                    double lat = Double.parseDouble(s[1]);
                    double lon = Double.parseDouble(s[3]);
                    Log.d("Smsreceiver: ", "Before calling google maps");
                    String uri = String.format(Locale.ENGLISH, "geo:%f,%f?q=%f,%f(Location)", lat, lon, lat, lon);
                    Intent inte = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                    startActivity(inte);

                }
            }
        }

    }

    public void gpslocation(){
        if(mLocationPermissionsGranted){
            Log.d("MainActivity: ","gpslocation function");
            Log.d("MainActivity: "," "+LocationManager.GPS_PROVIDER);
            gpsLocation=appLocationService.getLocation(LocationManager.GPS_PROVIDER);
            Log.d("MainActivity: "," "+gpsLocation);
            if(gpsLocation!=null){
                Log.d("MainActivity: ","gpslocation function: "+gpsLocation);
                double latitude=gpsLocation.getLatitude();
                double longitude=gpsLocation.getLongitude();
                Log.d("Latitude", String.valueOf(latitude));
                Log.d("Longitude", String.valueOf(longitude));
                mesg = "Latitude: "+latitude+" Longitude: "+longitude;

            }
            else{
                showSettingsAlert("GPS");
            }
        }
    }

    public void showSettingsAlert(String provider){
        final AlertDialog.Builder alertDialog=new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle(provider+" Settings");
        alertDialog.setMessage(provider+" is not enabled! Want to go to settings menu?");
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MainActivity.this.startActivity(intent);
            }
        });
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        alertDialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionsGranted = false;
        /*if(ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(),
                Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(),
                    Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                if(ContextCompat.checkSelfPermission(MainActivity.this.getApplicationContext(), Manifest.permission.RECEIVE_SMS)==PackageManager.PERMISSION_GRANTED) {
                    Log.d("MainActivity: ", " " + mLocationPermissionsGranted);
                    mLocationPermissionsGranted = true;
                }

            } else {
                Log.d("MainActielsepermisson: ", " " + mLocationPermissionsGranted);
            }
        }*/
        switch(requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }
                    mLocationPermissionsGranted = true;
                    Log.d("MainActivity","mLocationPermissionsGranted = true");

                }
            }
        }
    }
}

