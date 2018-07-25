package com.subratgupta.rentoo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class OwnerHomePage extends AppCompatActivity implements OwnerListRecyclerViewAdapter.ItemClickListener, InterestedProfileListRecyclerViewAdapter.ItemClickListener {

    ArrayList<TenantDataType> interested_tenant_array_list = new ArrayList<>();
    InterestedProfileListRecyclerViewAdapter adapter;
    Switch switchBtn;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_home_page);

        db = RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id"));

        switchBtn= (Switch) findViewById(R.id.switch1);

        RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.child("details").child("name").getValue(String.class);
                ((TextView) findViewById(R.id.name)).setText(value);

                if(dataSnapshot.child("status").getValue(Boolean.class)!=null){
                    if(dataSnapshot.child("status").getValue(Boolean.class)){
                        switchBtn.setChecked(true);
                    }
                    else {
                        switchBtn.setChecked(false);
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                findViewById(R.id.reg_view).setVisibility(View.GONE);
            }
        });

        setInterested_tenant_list();
    }

    public void onClick(View view) {
        DatabaseReference db = RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id"));
        if(switchBtn.isChecked()){
            switchBtn.setText("Active");
            db.child("status").setValue(true);
        }
        else {
            switchBtn.setText("Snoozed");
            db.child("status").setValue(false);
        }
    }

    private void setInterested_tenant_list() {
        RegisterOwnerNumber.mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                interested_tenant_array_list.clear();
                // Is better to use a List, because you don't know the size
                // of the iterator returned by dataSnapshot.getChildren() to
                // initialize the array
                final List<String> tenantList = new ArrayList<String>();

                for (DataSnapshot locationIDs : dataSnapshot.child("users").child(MainActivity.readData("user_id")).child("interested").getChildren()) {
                    String id = locationIDs.getValue(String.class);
                    tenantList.add(id);
                }


                for (int i = 0; i < tenantList.size(); i++) {
                    try {
                        TenantDataType tenant = dataSnapshot.child("users").child(tenantList.get(i)).child("details").getValue(TenantDataType.class);
                        interested_tenant_array_list.add(tenant);
                    } catch (Exception e) {
                        Log.e("tenhomepage", e.getMessage());
                    }
                }
                RecyclerView recyclerView = findViewById(R.id.interested_profiles);
                recyclerView.setLayoutManager(new LinearLayoutManager(OwnerHomePage.this));
                adapter = new InterestedProfileListRecyclerViewAdapter(OwnerHomePage.this, interested_tenant_array_list);
                adapter.setClickListener(OwnerHomePage.this);
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
        Toast.makeText(getApplicationContext(),interested_tenant_array_list.get(position).getName(),Toast.LENGTH_LONG).show();
    }

    public void onSignOut(View view) {
        RegisterOwnerNumber.mAuth.signOut();
        MainActivity.editor = MainActivity.sharedPref.edit();
        MainActivity.editor.clear();
        MainActivity.editor.apply();
        Intent goToHome = new Intent(this, MainActivity.class);
        startActivity(goToHome);
    }
}
