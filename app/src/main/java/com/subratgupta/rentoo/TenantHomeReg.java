package com.subratgupta.rentoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class TenantHomeReg extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OwnerListRecyclerViewAdapter.ItemClickListener, InterestedProfileListRecyclerViewAdapter.ItemClickListener {

    RadioGroup mSearchType;
    RadioButton radioButton;
    int choice;
    Button mSearchButton;
    public static String mode = "main";
    OwnerListRecyclerViewAdapter adapter;
    InterestedProfileListRecyclerViewAdapter tenantAdapter;
    ServiceListRecyclerViewAdapter serviceAdapter;
    public static boolean edit = false;
    Switch switchBtn;
    DatabaseReference db;
    AlertDialog.Builder builder;

    Spinner citySpinner;
    Spinner localSpinner;
    List<String> mCityList = new ArrayList<String>();
    List<String> mLocalList = new ArrayList<String>();
    List<String> ownerList;
    List<Property> roomList;
    List<TenantDataType> flatMateList;
    List<ServiceDataType> serviceList;
    public static Property profile;
    String local;
    String city;
    public static ArrayList<Property> propertyArrayList = new ArrayList<>();
    public static ArrayList<TenantDataType> tenantArrayList = new ArrayList<>();
    public static ArrayList<ServiceDataType> serviceArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tenant_home_reg);

        citySpinner = (Spinner) findViewById(R.id.cityList);
        localSpinner = (Spinner) findViewById(R.id.localList);
        switchBtn = (Switch) findViewById(R.id.switch1);

        cityFill();

        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tempCity = mCityList.get((int) id);
                city = tempCity;
                localFill(tempCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mSearchButton = (Button) findViewById(R.id.search_btn);
        mSearchButton.setEnabled(false);

        localSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    local = mLocalList.get((int) id);
                    mSearchButton.setEnabled(true);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        db = RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id"));
        switchBtn = (Switch) findViewById(R.id.switch1);

        RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                try {
                    String name = dataSnapshot.child("details").child("name").getValue(String.class);
                    String email = dataSnapshot.child("details").child("email").getValue(String.class);
                    String profile_url = dataSnapshot.child("profile_pic").child("imageUrl").getValue(String.class);
                    ((TextView) findViewById(R.id.name)).setText(name);
                    ((TextView) findViewById(R.id.nav_name)).setText(name);
                    ((TextView) findViewById(R.id.nav_email)).setText(email);
                    Glide.with(getApplicationContext()).load(profile_url).into((ImageView) findViewById(R.id.profile_pic));
                    Glide.with(getApplicationContext()).load(profile_url).into((ImageView) findViewById(R.id.imageView));
                    if (dataSnapshot.child("status").getValue(Boolean.class) != null) {
                        if (dataSnapshot.child("status").getValue(Boolean.class)) {
                            switchBtn.setChecked(true);
                            switchBtn.setText("Active");
                        } else {
                            switchBtn.setChecked(false);
                            switchBtn.setText("Snoozed");
                        }
                    }
                } catch (Exception e) {
                    Log.e("TenantHomeReg", e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                findViewById(R.id.reg_view).setVisibility(View.GONE);
            }
        });

        builder = new AlertDialog.Builder(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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

                ArrayAdapter<String> citysAdapter = new ArrayAdapter<String>(TenantHomeReg.this, android.R.layout.simple_spinner_item, cityList);
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

                ArrayAdapter<String> localsAdapter = new ArrayAdapter<String>(TenantHomeReg.this, android.R.layout.simple_spinner_item, localList);
                localsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                localSpinner.setAdapter(localsAdapter);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "databaseError", Toast.LENGTH_LONG).show();
            }
        });

    }

 /*   private void ownerListFetch() {
        RegisterTenantNum.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                propertyArrayList.clear();
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                ownerList = new ArrayList<String>();
                Toast.makeText(getApplicationContext(),local,Toast.LENGTH_SHORT).show();
                for (DataSnapshot locationIDs : dataSnapshot.child("locationSpecifiedID").child(local).getChildren()) {
                    String id = locationIDs.getValue(String.class);
                    Toast.makeText(getApplicationContext(),local+"2nd",Toast.LENGTH_SHORT).show();
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
                recyclerView.setLayoutManager(new LinearLayoutManager(TenantHomeReg.this));
                adapter = new OwnerListRecyclerViewAdapter(TenantHomeReg.this, propertyArrayList);
                adapter.setClickListener(TenantHomeReg.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "databaseError", Toast.LENGTH_LONG).show();
            }
        });
    }*/

    private void ServiceListFetch(String type, String city, String local, final RecyclerView recycle) {
        final String mType = type;
        final String mCity = city;
        final String mLocal = local;
        RegisterTenantNum.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                serviceArrayList.clear();
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                serviceList = new ArrayList<ServiceDataType>();

                for (DataSnapshot locationIDs : dataSnapshot.child("users").getChildren()) {
                    ServiceDataType id = locationIDs.child("details").getValue(ServiceDataType.class);

                    try {
                        if (id.getType_of_service().equals(mType)) {
                            if (id.getCity().equals(mCity)) {
                                if (id.getLocal().equals(mLocal)) {
                                    serviceList.add(id);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Err", Toast.LENGTH_SHORT);
                    }

                }

                for (int i = 0; i < serviceList.size(); i++) {
                    try {
                        ServiceDataType mServiceList = serviceList.get(i);
                        serviceArrayList.add(mServiceList);
                    } catch (Exception e) {
                        Log.e("tenhomepage", e.getMessage());
                    }
                }
                RecyclerView recyclerView = recycle;
                recyclerView.setLayoutManager(new LinearLayoutManager(TenantHomeReg.this));
                serviceAdapter = new ServiceListRecyclerViewAdapter(TenantHomeReg.this, serviceArrayList);
                serviceAdapter.setClickListener(TenantHomeReg.this);
                recyclerView.setAdapter(serviceAdapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "databaseError", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void ownerListFetch(String type, String city, String local, final RecyclerView recycle) {
        final String mType = type;
        final String mCity = city;
        final String mLocal = local;
        RegisterTenantNum.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                propertyArrayList.clear();
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                roomList = new ArrayList<Property>();
                for (DataSnapshot locationIDs : dataSnapshot.child("users").getChildren()) {
                    Toast.makeText(getApplicationContext(), mType + mCity + mLocal + " 3rd Toast", Toast.LENGTH_SHORT).show();
                    Property id = locationIDs.child("details").getValue(Property.class);
                    try {
                        if (locationIDs.child("type").getValue(String.class).equals(mType)) {
                            if (id.getCity().equals(mCity)) {
                                if (id.getLocal().equals(mLocal)) {
                                    roomList.add(id);
                                }
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Err", Toast.LENGTH_SHORT);
                    }

                }

                for (int i = 0; i < roomList.size(); i++) {
                    try {
                        Property mRoomList = roomList.get(i);
                        propertyArrayList.add(mRoomList);
                    } catch (Exception e) {
                        Log.e("tenhomepage", e.getMessage());
                    }
                }
                RecyclerView recyclerView = recycle;
                recyclerView.setLayoutManager(new LinearLayoutManager(TenantHomeReg.this));
                adapter = new OwnerListRecyclerViewAdapter(TenantHomeReg.this, propertyArrayList);
                adapter.setClickListener(TenantHomeReg.this);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(getApplicationContext(), "databaseError", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void flatMateListFetch(String type, String city, String local, final RecyclerView recycle) {
        final String mType = type;
        final String mCity = city;
        final String mLocal = local;
        RegisterTenantNum.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                tenantArrayList.clear();
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                flatMateList = new ArrayList<TenantDataType>();
                for (DataSnapshot locationIDs : dataSnapshot.child("users").getChildren()) {
                    TenantDataType id = locationIDs.child("details").getValue(TenantDataType.class);
                    try {
                        if (locationIDs.child("type").getValue(String.class).equals(mType)) {
                            if (id.getCity().equals(mCity)) {
                                if (id.getLocal().equals(mLocal)) {
                                    if (locationIDs.child("status").getValue(Boolean.class))
                                    {
                                        flatMateList.add(id);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Err", Toast.LENGTH_SHORT);
                    }

                }

                for (int i = 0; i < flatMateList.size(); i++) {
                    try {
                        TenantDataType mFlatList = flatMateList.get(i);
                        tenantArrayList.add(mFlatList);
                    } catch (Exception e) {
                        Log.e("tenhomepage", e.getMessage());
                    }
                }
                RecyclerView recyclerView = recycle;
                recyclerView.setLayoutManager(new LinearLayoutManager(TenantHomeReg.this));
                tenantAdapter = new InterestedProfileListRecyclerViewAdapter(TenantHomeReg.this, tenantArrayList);
                tenantAdapter.setClickListener(TenantHomeReg.this);
                recyclerView.setAdapter(tenantAdapter);
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
                recyclerView.setLayoutManager(new LinearLayoutManager(TenantHomeReg.this));
                adapter = new OwnerListRecyclerViewAdapter(TenantHomeReg.this, propertyArrayList);
                adapter.setClickListener(TenantHomeReg.this);
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
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (mode.equals("main")) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(this);

                builder.setMessage("msg").setTitle("title");

                //Setting message manually and performing action on button click
                builder.setMessage("Do you sure to exit?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                Intent _intentOBJ = new Intent(Intent.ACTION_MAIN);
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
            } else {
                findViewById(R.id.profile).setVisibility(View.VISIBLE);
                findViewById(R.id.search_page).setVisibility(View.GONE);
                findViewById(R.id.search_page).setVisibility(View.GONE);
                findViewById(R.id.spinner).setVisibility(View.GONE);
                mode = "main";
            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void register(View view) {
        Intent goToRegisterNum = new Intent(this, RegisterTenantNum.class);
        startActivity(goToRegisterNum);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        switch (item.getItemId()) {
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

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        findViewById(R.id.profile).setVisibility(View.GONE);
        findViewById(R.id.search_page).setVisibility(View.GONE);
        findViewById(R.id.search_page).setVisibility(View.GONE);
        findViewById(R.id.rental_plans).setVisibility(View.GONE);
        findViewById(R.id.food).setVisibility(View.GONE);
        findViewById(R.id.movers_n_packers).setVisibility(View.GONE);
        findViewById(R.id.furniture_rent).setVisibility(View.GONE);
        findViewById(R.id.feedback).setVisibility(View.GONE);
        findViewById(R.id.faq).setVisibility(View.GONE);
        findViewById(R.id.about_us).setVisibility(View.GONE);
        findViewById(R.id.investors).setVisibility(View.GONE);
        findViewById(R.id.contact_us).setVisibility(View.GONE);
        findViewById(R.id.spinner).setVisibility(View.GONE);

        int id = item.getItemId();

        if (id == R.id.nav_home) {

            findViewById(R.id.profile).setVisibility(View.VISIBLE);
            mode = "main";

        } else if (id == R.id.nav_rental_plans) {

            findViewById(R.id.rental_plans).setVisibility(View.VISIBLE);
            mode = "something else";

        } else if (id == R.id.nav_food) {

            findViewById(R.id.food).setVisibility(View.VISIBLE);
            findViewById(R.id.spinner).setVisibility(View.VISIBLE);
            mode = "something else";
            mSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ServiceListFetch("Food Tiffin", city, local, (RecyclerView) findViewById(R.id.food_service_list));
                }
            });

        } else if (id == R.id.nav_movers_n_packers) {
            findViewById(R.id.movers_n_packers).setVisibility(View.VISIBLE);
            findViewById(R.id.spinner).setVisibility(View.VISIBLE);
            mode = "something else";
            mSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ServiceListFetch("Movers and Packers", city, local, (RecyclerView) findViewById(R.id.movers_n_packers_list));
                }
            });
        } else if (id == R.id.nav_furniture_rent) {

            findViewById(R.id.furniture_rent).setVisibility(View.VISIBLE);
            findViewById(R.id.spinner).setVisibility(View.VISIBLE);
            mode = "something else";
            mSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ServiceListFetch("Furniture Rent", city, local, (RecyclerView) findViewById(R.id.furniture_rent_list));
                }
            });
        } else if (id == R.id.nav_feedback) {

            findViewById(R.id.feedback).setVisibility(View.VISIBLE);
            mode = "something else";

        } else if (id == R.id.nav_faq) {

            findViewById(R.id.faq).setVisibility(View.VISIBLE);
            mode = "something else";

        } else if (id == R.id.nav_about_us) {

            findViewById(R.id.about_us).setVisibility(View.VISIBLE);
            mode = "something else";

        } else if (id == R.id.nav_investors) {

            findViewById(R.id.investors).setVisibility(View.VISIBLE);
            mode = "something else";

        } else if (id == R.id.nav_contact_us) {

            findViewById(R.id.contact_us).setVisibility(View.VISIBLE);
            mode = "something else";

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void tonClick(View view) {
        final DatabaseReference db = RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id"));
        if (switchBtn.isChecked()) {
            switchBtn.setText("Active");
            db.child("status").setValue(true);
        } else {
            switchBtn.setText("Snoozed");
            db.child("status").setValue(false);
            switchBtn.setChecked(false);
        }
    }

    public void edit(View view) {
        edit = true;
        Intent goToEdit = new Intent(this, RegisterTenant.class);
        startActivity(goToEdit);
    }

    public void goto_rooms(View view) {
        findViewById(R.id.profile).setVisibility(View.GONE);
        findViewById(R.id.search_page).setVisibility(View.VISIBLE);
        findViewById(R.id.shortlist_page).setVisibility(View.GONE);
        findViewById(R.id.spinner).setVisibility(View.VISIBLE);
        mode = "search";

        mSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchType = (RadioGroup) findViewById(R.id.room_or_mate);
                choice = mSearchType.getCheckedRadioButtonId();
                radioButton = (RadioButton) findViewById(choice);
                mode = "something_else";
                switch (radioButton.getText().toString()) {
                    case "Rooms":
                        ownerListFetch("owner", city, local, (RecyclerView) findViewById(R.id.owner_list));
                        break;
                    case "Flatmates":
                        flatMateListFetch("tenant", city, local, (RecyclerView) findViewById(R.id.owner_list));
                        break;
                }
            }
        });

    }

    public void shortlisted(View view) {
        findViewById(R.id.profile).setVisibility(View.GONE);
        findViewById(R.id.search_page).setVisibility(View.GONE);
        findViewById(R.id.shortlist_page).setVisibility(View.VISIBLE);
        mode = "short";
        shortListFetch();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}
