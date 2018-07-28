package com.subratgupta.rentoo;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;
import java.util.logging.Level;

public class RegisterTenant extends AppCompatActivity {

    final String IS_FIRST_TIME = "is_first_time";

    private EditText mNameField;
    private EditText mAgeField;
    private EditText mPhoneNumberField;
    private EditText mEmailField;
    private EditText mOccupationField;

    private RadioGroup mMaritalStatus;
    private RadioButton radioButton;

    private static final String TAG = "RegisterTenant";

    public static DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tenant);

        mNameField = (EditText) findViewById(R.id.name);
        mAgeField = (EditText) findViewById(R.id.age);
        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mEmailField = (EditText) findViewById(R.id.email);
        mOccupationField = (EditText) findViewById(R.id.field_occupation);
        mPhoneNumberField.setText(MainActivity.readData("contact_number"));

        mMaritalStatus = (RadioGroup) findViewById(R.id.marital_radio);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        if (!MainActivity.readData(IS_FIRST_TIME).equals("false")) {
            findViewById(R.id.contentPanel).setVisibility(View.VISIBLE);
            findViewById(R.id.ok).setVisibility(View.VISIBLE);
        }

        try{
            RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("details").child("name").getValue(String.class);
                    String age = dataSnapshot.child("details").child("age").getValue(String.class);
                    String phone = dataSnapshot.child("details").child("phone").getValue(String.class);
                    String occupation = dataSnapshot.child("details").child("occupation").getValue(String.class);
                    String email = dataSnapshot.child("details").child("email").getValue(String.class);

                    Integer marital_int = dataSnapshot.child("details").child("marital_int").getValue(Integer.class);

                    ((TextView) findViewById(R.id.name)).setText(name);
                    ((TextView) findViewById(R.id.age)).setText(age);
                    ((TextView) findViewById(R.id.field_phone_number)).setText(phone);
                    ((TextView) findViewById(R.id.field_occupation)).setText(occupation);
                    ((TextView) findViewById(R.id.email)).setText(email);

                    try {
                        radioClick(marital_int);
                    } catch (Exception e){
                        Toast.makeText(getApplicationContext(),"Please fill all details",Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    findViewById(R.id.reg_view).setVisibility(View.GONE);
                }
            });

        }catch (Exception e){
            Log.d("RegTenant",e.getMessage());
        }

        try {
            RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).child("details").child("isComplete").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    if (Objects.equals(value, "true")) {
                        goTo();
                    }
                    else {
                        findViewById(R.id.register_page).setVisibility(View.VISIBLE);
                        findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    findViewById(R.id.reg_view).setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "No Internet!",Toast.LENGTH_SHORT).show();
                }
            });
        }catch (Exception e){
            Log.d("RegisterTenant",e.getMessage());
        }
    }

    private void goTo() {
        Intent goToOwnerHomePage = new Intent(this, TenantHomeRegistered.class);
        startActivity(goToOwnerHomePage);
    }

    private void radioClick(int id) {
        ((RadioButton) findViewById(id)).performClick();
    }


    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ok:
                MainActivity.writeData(IS_FIRST_TIME, "false");
                findViewById(R.id.contentPanel).setVisibility(View.GONE);
                findViewById(R.id.ok).setVisibility(View.GONE);
                break;

            case R.id.submit:
                try {
                    DatabaseReference db = RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).child("details");
                    int selectedId = mMaritalStatus.getCheckedRadioButtonId();
                    radioButton = (RadioButton) findViewById(selectedId);
                    db.child("marital").setValue(radioButton.getText().toString());
                    db.child("marital_int").setValue(radioButton.getId());

                    db.child("name").setValue(mNameField.getText().toString());
                    db.child("age").setValue(mAgeField.getText().toString());
                    db.child("phone").setValue(mPhoneNumberField.getText().toString());
                    db.child("occupation").setValue(mOccupationField.getText().toString());
                    db.child("email").setValue(mEmailField.getText().toString());
                    db.child("isComplete").setValue("true");
                    goTo();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), e.getCause()+"Please fill all details.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

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
    }
}
