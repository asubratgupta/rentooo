package com.subratgupta.rentoo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.SweepGradient;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class OwnerActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    final String IS_FIRST_TIME = "is_first_time";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner);

        if (!MainActivity.readData(IS_FIRST_TIME).equals("false")) {
            findViewById(R.id.contentPanel).setVisibility(View.VISIBLE);
            findViewById(R.id.ok).setVisibility(View.VISIBLE);
        }

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null){
            Intent goToRegisterOwnerNumber = new Intent(this, RegisterOwnerNumber.class);
            startActivity(goToRegisterOwnerNumber);        }
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.ok:
                MainActivity.writeData(IS_FIRST_TIME, "false");
                goTo("ok");
                break;

            case R.id.register:
                goTo("register");
                break;

            case R.id.miss_call:
                goTo("miss_call");

            default:
                break;
        }
    }

    public void goTo(String button) {
        switch (button) {
            case "ok":
                findViewById(R.id.contentPanel).setVisibility(View.GONE);
                findViewById(R.id.ok).setVisibility(View.GONE);
                break;

            case "register":
                Intent goToRegisterOwnerNumber = new Intent(this, RegisterOwnerNumber.class);
                startActivity(goToRegisterOwnerNumber);
                break;

            case "miss_call":
                if (ContextCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_CONTACTS)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Permission is not granted
                    // Should we show an explanation?
                    if (ActivityCompat.shouldShowRequestPermissionRationale(OwnerActivity.this, Manifest.permission.CALL_PHONE)) {
                        // Show an explanation to the user *asynchronously* -- don't block
                        // this thread waiting for the user's response! After the user
                        // sees the explanation, try again to request the permission.
                        ActivityCompat.requestPermissions(OwnerActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    } else {
                        // No explanation needed; request the permission
                        ActivityCompat.requestPermissions(OwnerActivity.this,
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant. The callback method gets the
                        // result of the request.
                    }
                } else {
                    // Permission has already been granted
                }
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:9456908427"));

                if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                startActivity(callIntent);
                break;

            default:
                break;
        }
    }
}
