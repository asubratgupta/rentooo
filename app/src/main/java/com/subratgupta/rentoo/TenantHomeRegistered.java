package com.subratgupta.rentoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class TenantHomeRegistered extends AppCompatActivity implements OwnerListRecyclerViewAdapter.ItemClickListener {

    public static String mode = "main";
    OwnerListRecyclerViewAdapter adapter;
    public static boolean edit = false;
    Switch switchBtn;
    DatabaseReference db;
    AlertDialog.Builder builder;

    Spinner citySpinner;
    Spinner localSpinner;
    List<String> mCityList = new ArrayList<String>();
    List<String> mLocalList = new ArrayList<String>();
    List<String> ownerList;
    public static Property profile;
    String location;
    public static ArrayList<Property> propertyArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home_registered);

        citySpinner = (Spinner) findViewById(R.id.cityList);
        localSpinner = (Spinner) findViewById(R.id.localList);
        switchBtn= (Switch) findViewById(R.id.switch1);

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

        db = RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id"));
        switchBtn= (Switch) findViewById(R.id.switch1);

        RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("details").child("name").getValue(String.class);
                String profile_url = dataSnapshot.child("profile_pic").child("imageUrl").getValue(String.class);
                ((TextView) findViewById(R.id.name)).setText(value);
                Glide.with(getApplicationContext()).load(profile_url).into((ImageView) findViewById(R.id.profile_pic));

                if(dataSnapshot.child("status").getValue(Boolean.class)!=null){
                    if(dataSnapshot.child("status").getValue(Boolean.class)){
                        switchBtn.setChecked(true);
                        switchBtn.setText("Active");
                    }
                    else {
                        switchBtn.setChecked(false);
                        switchBtn.setText("Snoozed");
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                findViewById(R.id.reg_view).setVisibility(View.GONE);
            }
        });

        builder = new AlertDialog.Builder(this);
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

                ArrayAdapter<String> citysAdapter = new ArrayAdapter<String>(TenantHomeRegistered.this, android.R.layout.simple_spinner_item, cityList);
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

                ArrayAdapter<String> localsAdapter = new ArrayAdapter<String>(TenantHomeRegistered.this, android.R.layout.simple_spinner_item, localList);
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
                ownerList = new ArrayList<String>();

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
                recyclerView.setLayoutManager(new LinearLayoutManager(TenantHomeRegistered.this));
                adapter = new OwnerListRecyclerViewAdapter(TenantHomeRegistered.this, propertyArrayList);
                adapter.setClickListener(TenantHomeRegistered.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "databaseError", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void shortListFetch() {
        RegisterTenantNum.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                propertyArrayList.clear();

                ownerList = new ArrayList<String>();

                for (DataSnapshot locationIDs : dataSnapshot.child("users").child(MainActivity.readData("user_id")).child("shortlisted").getChildren()) {
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
                RecyclerView recyclerView = findViewById(R.id.short_list);
                recyclerView.setLayoutManager(new LinearLayoutManager(TenantHomeRegistered.this));
                adapter = new OwnerListRecyclerViewAdapter(TenantHomeRegistered.this, propertyArrayList);
                adapter.setClickListener(TenantHomeRegistered.this);
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
        RegisterTenantNum.mDatabase.child("users").child(ownerList.get(position)).child("interested_tenant").child(MainActivity.readData("user_id")).setValue(MainActivity.readData("user_id"));
        RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).child("shortlisted").child(ownerList.get(position)).setValue(ownerList.get(position));
        profile = propertyArrayList.get(position);
        Intent goToProfile = new Intent(this, RoomView.class);
        startActivity(goToProfile);
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

    @Override
    public void onBackPressed() {
        switch(mode){
            case "main":
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(this);

                builder.setMessage("msg") .setTitle("title");

                //Setting message manually and performing action on button click
                builder.setMessage("Do you sure to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent _intentOBJ= new Intent(Intent.ACTION_MAIN);
                                _intentOBJ.addCategory(Intent.CATEGORY_HOME);
                                _intentOBJ.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                _intentOBJ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                getApplicationContext().startActivity(_intentOBJ);
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //  Action for 'NO' Button
                                dialog.cancel();
                            }
                        });
                //Creating dialog box
                AlertDialog alert = builder.create();
                //Setting the title manually
                alert.setTitle("Exit");
                alert.show();
                break;
            case "search":
                findViewById(R.id.profile).setVisibility(View.VISIBLE);
                findViewById(R.id.search_page).setVisibility(View.GONE);
                findViewById(R.id.search_page).setVisibility(View.GONE);
                mode = "main";
                break;
            case "short":
                findViewById(R.id.profile).setVisibility(View.VISIBLE);
                findViewById(R.id.search_page).setVisibility(View.GONE);
                findViewById(R.id.shortlist_page).setVisibility(View.GONE);
                mode = "main";
                break;
        }
    }

    public void tonClick(View view) {
        final DatabaseReference db = RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id"));
        if(switchBtn.isChecked()){
            switchBtn.setText("Active");
            db.child("status").setValue(true);
        }
        else {
            switchBtn.setText("Snoozed");
            db.child("status").setValue(false);
            switchBtn.setChecked(false);
        }
    }

    public void edit(View view) {
        edit = true;
        finish();
    }

    public void goto_rooms(View view) {
        findViewById(R.id.profile).setVisibility(View.GONE);
        findViewById(R.id.search_page).setVisibility(View.VISIBLE);
        findViewById(R.id.shortlist_page).setVisibility(View.GONE);
        mode = "search";
    }

    public void shortlisted(View view) {
        findViewById(R.id.profile).setVisibility(View.GONE);
        findViewById(R.id.search_page).setVisibility(View.GONE);
        findViewById(R.id.shortlist_page).setVisibility(View.VISIBLE);
        mode = "short";
        shortListFetch();
    }
}

