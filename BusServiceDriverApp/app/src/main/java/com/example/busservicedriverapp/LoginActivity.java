package com.example.busservicedriverapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.busservicedriverapp.modelclass.DriverInformation;
import com.example.busservicedriverapp.registration.VerificationActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    EditText et_driverPhoneNumber;
    Button btn_verify;

    FirebaseAuth mAuth;

    private static boolean LocationPermission = false;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 999;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: calledL");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        et_driverPhoneNumber = findViewById(R.id.et_driverphonenumber_xml);
        btn_verify = findViewById(R.id.btn_verify_xml);

        mAuth = FirebaseAuth.getInstance();

        btn_verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(haveNetworkConnection()){
                    Intent sendUserPhoneNumber = new Intent(LoginActivity.this, VerificationActivity.class);
                    String driverPhnNumber = "+88"+et_driverPhoneNumber.getText().toString();

                    if(driverPhnNumber.length()!=14){
                        Toast.makeText(LoginActivity.this, "Invalid Phone Number.", Toast.LENGTH_SHORT).show();
                    }else{
                        sendUserPhoneNumber.putExtra("DriverPhnNumber", driverPhnNumber);
                        startActivity(sendUserPhoneNumber);
                    }

                }else{
                    Toast.makeText(LoginActivity.this, "Turn on Internet First.", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }//End of onCreate

    private boolean haveNetworkConnection() {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = cm.getAllNetworkInfo();
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
        finish();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: calledL");
        super.onStart();

        /*        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("driverlist");
        DriverInformation d=new DriverInformation("abc","N/A-COPY",
                "N/A","N/A","N/A",
                "https://firebasestorage.googleapis.com/v0/b/busserviceapp-512cb.appspot.com/o/DriverProfilePicture%2Fimage%3A76460?alt=media&token=ce7e5268-ad92-46d1-a92d-e7f5c5d1f644",
                "N/A","N/A","N/A","N/A","Bikash 34","");
        ref.child("abc").setValue(d);*/

        getLocationPermission();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, DriverHomeActivity.class);
            startActivity(intent);
        }
    }
    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: calledL");
        super.onResume();

    }

    @Override
    protected void onRestart() {
        Log.d(TAG, "onRestart: calledL");
        super.onRestart();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: calledL");
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: calledL");
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: calledL");
        super.onDestroy();
    }

    @Override
    public void finish() {
        Log.d(TAG, "finish: calledL");
        super.finish();
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permissions");
        String permissions[] = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) ==
                    PackageManager.PERMISSION_GRANTED) {
                LocationPermission = true;
                //initMap();
            } else {
                ActivityCompat.requestPermissions(this, permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }

        } else {
            ActivityCompat.requestPermissions(this, permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: called");
        LocationPermission = false;
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {
                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                            LocationPermission = true;
                            Log.d(TAG, "onRequestPermissionsResult: permission granted.");
                        }
                    }
                    Log.d(TAG, "onRequestPermissionsResult: permission failde");
                    LocationPermission = false;
                    //initialization of map
                    //initMap();
                }
            }
        }
    }

}
