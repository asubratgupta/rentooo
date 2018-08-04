package com.subratgupta.rentoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class SettingsActivity extends AppCompatActivity {

    AlertDialog.Builder builder;
    DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        builder = new AlertDialog.Builder(this);

        TextView id = (TextView) findViewById(R.id.id);
        id.setText(MainActivity.readData("phone"));

        final TextView deleteAccount = (TextView) findViewById(R.id.delete);

        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage("msg").setTitle("title");
                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure to Delete Your Account?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                FirebaseAuth mAuth;
                                switch (MainActivity.readData("type")) {
                                    case "owner":
                                        db = RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id"));
                                        RegisterOwnerNumber.mDatabase.child("locationSpecifiedService").child(RegisterOwnerProperty.localArea).child(MainActivity.readData("user_id")).setValue(null);
                                        mAuth = RegisterOwnerNumber.mAuth;
                                        break;
                                    case "tenant":
                                        db = RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id"));
                                        RegisterTenantNum.mDatabase.child("locationSpecifiedService").child(RegisterTenant.localArea).child(MainActivity.readData("user_id")).setValue(null);
                                        mAuth = RegisterTenantNum.mAuth;
                                        break;
                                    case "service_provider":
                                        db = ServiceProviderActivity.mDatabase.child("users").child(MainActivity.readData("user_id"));
                                        ServiceProviderActivity.mDatabase.child("locationSpecifiedService").child(RegisterService.localArea).child(MainActivity.readData("user_id")).setValue(null);
                                        mAuth = ServiceProviderActivity.mAuth;
                                        break;
                                    default:
                                        mAuth = RegisterOwnerNumber.mAuth;
                                }
                                db.removeValue();
                                mAuth.signOut();
                                MainActivity.editor = MainActivity.sharedPref.edit();
                                MainActivity.editor.clear();
                                MainActivity.editor.apply();
                                Intent goToHome = new Intent(SettingsActivity.this, MainActivity.class);
                                startActivity(goToHome);
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
                alert.setTitle("Delete Account");
                alert.show();
            }
        });

    }
}
