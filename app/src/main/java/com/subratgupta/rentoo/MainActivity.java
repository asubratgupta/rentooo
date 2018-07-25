package com.subratgupta.rentoo;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;
import android.widget.Toast;

import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static SharedPreferences sharedPref;
    public final String TYPE = "type";
    final String OWNER = "owner";
    final String TENANT = "tenant";
    final String SERVICE_PROVIDER = "service_provider";
    static SharedPreferences.Editor editor;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        goTo(readData(TYPE));
    }

    public static void writeData(String key, String value) {
        editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String readData(String key) {
        return "" + sharedPref.getString(key, "default");
    }

    public static void deleteData(String key) {
        editor.remove(key);
        editor.apply();
    }

    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.owner:
                writeData(TYPE, OWNER);
                goTo(OWNER);
                break;

            case R.id.tenant:
                writeData(TYPE, TENANT);
                goTo(TENANT);
                break;

            case R.id.service_provider:
                writeData(TYPE, SERVICE_PROVIDER);
                goTo(SERVICE_PROVIDER);
                break;
        }
    }

    public void goTo(String activity_name) {
        switch (activity_name) {
            case OWNER:
                Intent goToOwner = new Intent(this, OwnerActivity.class);
                startActivity(goToOwner);
                break;

            case TENANT:
                Intent goToTenant = new Intent(this, RegisterTenantNum.class);
                startActivity(goToTenant);
                break;
/*
            case SERVICE_PROVIDER:
                Intent goToSP = new Intent(this, ServiceProviderActivity.class);
                startActivity(goToSP);
                break;*/

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(),"123",Toast.LENGTH_LONG).show();
        return;
    }
}
