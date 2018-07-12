package com.subratgupta.rentoo;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class OwnerHomePage extends AppCompatActivity {

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

                if(dataSnapshot.child("interested_profile").child("1").getValue()!=null){
                    ((TextView) findViewById(R.id.iname1)).setText(dataSnapshot.child("interested_profile").child("1").child("name").getValue(String.class));
                    ((TextView) findViewById(R.id.iphone1)).setText(dataSnapshot.child("interested_profile").child("1").child("phone").getValue(String.class));
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                findViewById(R.id.reg_view).setVisibility(View.GONE);
            }
        });

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
}
