package com.subratgupta.rentoo;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TenantHomePage extends AppCompatActivity {

    Spinner citySpinner;
    Spinner localSpinner;
    List<String> mCityList = new ArrayList<String>();
    List<String> mLocalList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home_page);

        citySpinner = (Spinner) findViewById(R.id.cityList);
        localSpinner = (Spinner) findViewById(R.id.localList);

        cityFill();

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tempCity = mCityList.get((int)id);
                localFill(tempCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        localSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tempCity = mLocalList.get((int)id);
                Toast.makeText(getApplicationContext(),tempCity,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void cityFill() {
        RegisterTenantNum.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> cityList = new ArrayList<String>();

                for (DataSnapshot citySnapshot : dataSnapshot.child("cityList").getChildren()) {
                    String city = citySnapshot.getValue(String.class);
                    cityList.add(city);
                }

                mCityList = cityList;

                ArrayAdapter<String> citysAdapter = new ArrayAdapter<String>(TenantHomePage.this, android.R.layout.simple_spinner_item, cityList);
                citysAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                citySpinner.setAdapter(citysAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "databaseError", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void localFill(String city) {
        final String mCity = city;
        RegisterTenantNum.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> localList = new ArrayList<String>();

                for (DataSnapshot localSnapshot : dataSnapshot.child("localList").child(mCity).getChildren()) {
                    String local = localSnapshot.getValue(String.class);
                    localList.add(local);
                }

                mLocalList = localList;

                ArrayAdapter<String> localsAdapter = new ArrayAdapter<String>(TenantHomePage.this, android.R.layout.simple_spinner_item, localList);
                localsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                localSpinner.setAdapter(localsAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "databaseError", Toast.LENGTH_LONG).show();
            }
        });

    }
}
