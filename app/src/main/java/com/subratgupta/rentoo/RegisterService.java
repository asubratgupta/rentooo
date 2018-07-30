package com.subratgupta.rentoo;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.Objects;

public class RegisterService extends AppCompatActivity {

    EditText mPhoneNumberField;
    EditText mNameField;
    EditText mAddressField;
    LinearLayout linearLayout;

    private int i;
    private RadioGroup mTypeOfService;
    private RadioButton radioButton;
    private static final String TAG = "RegisterService";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_service);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mNameField = (EditText) findViewById(R.id.name);
        mAddressField = (EditText) findViewById(R.id.address);
        mPhoneNumberField.setText(MainActivity.readData("contact_number"));

        mTypeOfService = (RadioGroup) findViewById(R.id.type_radio);

        try {
            ServiceProviderActivity.mDatabase.child("users").child(MainActivity.readData("user_id")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("details").child("name").getValue(String.class);
                    String address = dataSnapshot.child("details").child("address").getValue(String.class);
                    Integer type_of_service_int = dataSnapshot.child("details").child("type_of_service_int").getValue(Integer.class);
                    ((TextView) findViewById(R.id.name)).setText(name);
                    ((TextView) findViewById(R.id.address)).setText(address);
                    try {
                        radioClick(type_of_service_int);
                    } catch (Exception e) {
                        if (OwnerHomePage.edit || MainActivity.readData("isComplete").equals("true")){
                            Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_LONG).show();
                        }
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    findViewById(R.id.reg_view).setVisibility(View.GONE);
                }
            });
        } catch (Exception e) {
            Log.d("OwnProp", e.getMessage());
        }

        ServiceProviderActivity.mDatabase.child("users").child(MainActivity.readData("user_id")).child("details").child("isComplete").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (Objects.equals(value, "true")) {
//                    if (OwnerHomePage.edit){
                        findViewById(R.id.progress_bar).setVisibility(View.GONE);
                        findViewById(R.id.reg_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.skip_btn).setVisibility(View.VISIBLE);
//                    }
//                    else {
                        goTo();
//                    }
                }
                else {
                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    findViewById(R.id.reg_view).setVisibility(View.VISIBLE);
                    findViewById(R.id.skip_btn).setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                findViewById(R.id.reg_view).setVisibility(View.GONE);
                findViewById(R.id.skip_btn).setVisibility(View.GONE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.sign_out_menu:
                //signout
                ServiceProviderActivity.mAuth.signOut();
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

    private void radioClick(int id) {
        ((RadioButton) findViewById(id)).performClick();
    }

    public void skip(View view) {
        goTo();
    }

    public void onClick(View view) {

        try {
            DatabaseReference db = ServiceProviderActivity.mDatabase.child("users").child(MainActivity.readData("user_id")).child("details");
            int selectedId = mTypeOfService.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            db.child("type_of_service").setValue(radioButton.getText().toString());
            db.child("type_of_service_int").setValue(radioButton.getId());

            db.child("name").setValue(mNameField.getText().toString());
            db.child("address").setValue(mAddressField.getText().toString());
            db.child("phone").setValue(mPhoneNumberField.getText().toString());
            db.child("isComplete").setValue("true");
            goTo();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please fill all details.", Toast.LENGTH_LONG).show();
        }
    }

    private void goTo() {
        /*Intent goToOwnerHomePage = new Intent(this, OwnerHomePage.class);
        this.finish();
        startActivity(goToOwnerHomePage);*/

        Toast.makeText(getApplicationContext(),"Thank You, Service Registered Successfully.",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        Intent _intentOBJ= new Intent(Intent.ACTION_MAIN);
        _intentOBJ.addCategory(Intent.CATEGORY_HOME);
        _intentOBJ.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        _intentOBJ.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(_intentOBJ);
    }
}
