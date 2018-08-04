package com.subratgupta.rentoo;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputFilter;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TenantHomePage extends AppCompatActivity implements OwnerListRecyclerViewAdapter.ItemClickListener {

    OwnerListRecyclerViewAdapter adapter;

    Spinner citySpinner;
    Spinner localSpinner;
    List<String> mCityList = new ArrayList<String>();
    List<String> mLocalList = new ArrayList<String>();
    String location;
    public static ArrayList<Property> propertyArrayList = new ArrayList<>();

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
                String tempCity = mCityList.get((int) id);
                localFill(tempCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        final Button mSearchButton = (Button) findViewById(R.id.search_btn);
        mSearchButton.setEnabled(false);

        localSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position>0){
                    location = mLocalList.get((int) id);
                    mSearchButton.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ownerListFetch();
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
                cityList.add("Choose a city");

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
                localList.add("Choose Local Area");

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

    private void ownerListFetch() {
        RegisterTenantNum.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                propertyArrayList.clear();
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> ownerList = new ArrayList<String>();

                for (DataSnapshot locationIDs : dataSnapshot.child("locationSpecifiedID").child(location).getChildren()) {
                    String id = locationIDs.getValue(String.class);
                    ownerList.add(id);
                }


                for (int i = 0; i < ownerList.size(); i++) {
                    try {
                        Property owner = dataSnapshot.child("users").child(ownerList.get(i)).child("details").getValue(Property.class);
                        propertyArrayList.add(owner);
                    } catch (Exception e) {
                        Log.e("tenhomepage", e.getMessage());
                    }
                }
                RecyclerView recyclerView = findViewById(R.id.owner_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(TenantHomePage.this));
                adapter = new OwnerListRecyclerViewAdapter(TenantHomePage.this, propertyArrayList);
                adapter.setClickListener(TenantHomePage.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "databaseError", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
//        Toast.makeText(getApplicationContext(),propertyArrayList.get(position).getName(),Toast.LENGTH_LONG).show();
        Toast.makeText(getApplicationContext(),"First Register yourself",Toast.LENGTH_SHORT).show();
        register(findViewById(R.id.register));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                //signout
                RegisterTenantNum.mAuth.signOut();
                MainActivity.editor = MainActivity.sharedPref.edit();
                MainActivity.editor.clear();
                MainActivity.editor.apply();
                Intent goToHome = new Intent(this, MainActivity.class);
                startActivity(goToHome);
                return true;
            case R.id.settings_menu:
                Intent goToSettings = new Intent(this, SettingsActivity.class);
                startActivity(goToSettings);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void register(View view) {
        Intent goToRegisterNum = new Intent(this, RegisterTenantNum.class);
        startActivity(goToRegisterNum);
    }
}
