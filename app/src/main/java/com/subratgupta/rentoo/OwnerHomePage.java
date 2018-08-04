package com.subratgupta.rentoo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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

public class OwnerHomePage extends AppCompatActivity {

    public static boolean edit = false;
    Switch switchBtn;
    DatabaseReference db;
    AlertDialog.Builder builder;

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
                String profile_url = dataSnapshot.child("details").child("ppi410").getValue(String.class);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    public void onClick(View view) {
        final DatabaseReference db = RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id"));
        if(switchBtn.isChecked()){
            switchBtn.setText("Active");
            db.child("status").setValue(true);
        }
        else {

            builder.setMessage("msg") .setTitle("title");

            //Setting message manually and performing action on button click
            builder.setMessage("Do you want to Snooze your profile for 30 days ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            switchBtn.setText("Snoozed");
                            db.child("status").setValue(false);
                            switchBtn.setChecked(false);
                            Toast.makeText(getApplicationContext(),"Your profile snoozed for 30 days",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //  Action for 'NO' Button
                            dialog.cancel();
                            switchBtn.setChecked(true);
                        }
                    });
            //Creating dialog box
            AlertDialog alert = builder.create();
            //Setting the title manually
            alert.setTitle("Profile Snooze");
            alert.show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                //signout
                RegisterOwnerNumber.mAuth.signOut();
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
    public void onBackPressed() {

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
    }

    public void goto_interest(View view) {
        Intent goToInterest = new Intent(this, InterestedProfilesOnOwner.class);
        startActivity(goToInterest);
    }

    public void edit(View view) {
        edit = true;
        finish();
    }
}
