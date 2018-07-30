package com.subratgupta.rentoo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class InterestedProfilesOnOwner extends AppCompatActivity implements OwnerListRecyclerViewAdapter.ItemClickListener, InterestedProfileListRecyclerViewAdapter.ItemClickListener {

    ArrayList<TenantDataType> interested_tenant_array_list = new ArrayList<>();
    InterestedProfileListRecyclerViewAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interested_profiles_on_owner);

        setInterested_tenant_list();
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

                for (DataSnapshot locationIDs : dataSnapshot.child("users").child(MainActivity.readData("user_id")).child("interested_tenant").getChildren()) {
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
                recyclerView.setLayoutManager(new LinearLayoutManager(InterestedProfilesOnOwner.this));
                adapter = new InterestedProfileListRecyclerViewAdapter(InterestedProfilesOnOwner.this, interested_tenant_array_list);
                adapter.setClickListener(InterestedProfilesOnOwner.this);
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
}
