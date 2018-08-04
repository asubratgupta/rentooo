package com.subratgupta.rentoo;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.logging.Level;

public class RegisterTenant extends AppCompatActivity {

    final String IS_FIRST_TIME = "is_first_time";
    ImageView image;
    private int i;
    private static final int REQUEST_IMAGE = 1;

    private EditText mNameField;
    private EditText mAgeField;
    private EditText mPhoneNumberField;
    private EditText mEmailField;
    private EditText mOccupationField;

    private RadioGroup mMaritalStatus;
    private RadioButton radioButton;

    private static final String TAG = "RegisterTenant";

    public static DatabaseReference mDatabase;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

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

        try {
            RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("details").child("name").getValue(String.class);
                    String age = dataSnapshot.child("details").child("age").getValue(String.class);
                    String phone = dataSnapshot.child("details").child("phone").getValue(String.class);
                    String occupation = dataSnapshot.child("details").child("occupation").getValue(String.class);
                    String email = dataSnapshot.child("details").child("email").getValue(String.class);
                    String profile_pic = dataSnapshot.child("profile_pic").child("imageUrl").getValue(String.class);
                    Integer marital_int = dataSnapshot.child("details").child("marital_int").getValue(Integer.class);

                    try {
                        if (profile_pic.length()>=1){
                            Glide.with(getApplicationContext()).load(profile_pic).into((ImageView) findViewById(R.id.profile_pic));
                        }
                    }catch (Exception e){
                        Log.e("Rentoo Property Reg", e.getMessage());
                    }

                    ((TextView) findViewById(R.id.name)).setText(name);
                    ((TextView) findViewById(R.id.age)).setText(age);
                    ((TextView) findViewById(R.id.field_phone_number)).setText(phone);
                    ((TextView) findViewById(R.id.field_occupation)).setText(occupation);
                    ((TextView) findViewById(R.id.email)).setText(email);

                    try {
                        radioClick(marital_int);
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    findViewById(R.id.reg_view).setVisibility(View.GONE);
                }
            });

        } catch (Exception e) {
            Log.d("RegTenant", e.getMessage());
        }

        try {
            RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).child("details").child("isComplete").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    if (Objects.equals(value, "true")) {
                        goTo();
                    } else {
                        findViewById(R.id.register_page).setVisibility(View.VISIBLE);
                        findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    findViewById(R.id.reg_view).setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), "No Internet!", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.d("RegisterTenant", e.getMessage());
        }
    }

    private void goTo() {
        Intent goToOwnerHomePage = new Intent(this, TenantHomeReg.class);
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
                    Toast.makeText(getApplicationContext(), e.getCause() + "Please fill all details.", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                final Uri uri;
                uri = data.getData();
                deleteImage();

                MessageHelper tempMessage = new MessageHelper();
                RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).child("photos").push()
                        .setValue(tempMessage, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError,
                                                   DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    String key = databaseReference.getKey();
                                    StorageReference storageReference =
                                            FirebaseStorage.getInstance()
                                                    .getReference(MainActivity.readData("user_id"))
                                                    .child(key)
                                                    .child(uri.getLastPathSegment());
                                    image = (ImageView) findViewById(R.id.profile_pic);
                                    Glide.with(getApplicationContext()).
                                            load(LOADING_IMAGE_URL).into(image);
                                    putImageInStorage(storageReference, uri, key);
                                } else {
                                    Log.w(TAG, "Unable to write message to database.",
                                            databaseError.toException());
                                }
                            }
                        });

            }
        }
    }

    private void putImageInStorage(StorageReference storageReference, final Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            MessageHelper messageHelper =
                                    new MessageHelper(null, null,
                                            task.getResult().getDownloadUrl()
                                                    .toString());
                                RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).child("profile_pic").setValue(messageHelper);
                                RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).child("profile_pic_key").child("" + i).setValue(key);
                                RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).child("profile_pic_key").child("name" + i).setValue(uri.getLastPathSegment());
                            showImages(messageHelper.getImageUrl());
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    public void showImages(String url) {
        new RegisterTenant.DownLoadImageTask(image).execute(url);
    }

    private class DownLoadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView) {
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String... urls) {
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try {
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            } catch (Exception e) { // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
    }

    private void deleteImage() {
        // Create a storage reference from our app
        final StorageReference storageReference =
                FirebaseStorage.getInstance()
                        .getReference(MainActivity.readData("user_id"));

        RegisterTenantNum.mDatabase.child("users").child(MainActivity.readData("user_id")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = "";
                String imageName = "";
                try {
                        key = dataSnapshot.child("profile_pic_key").child("" + i).getValue(String.class);
                        imageName = dataSnapshot.child("profile_pic_key").child("name" + i).getValue(String.class);
                    // Create a reference to the file to delete
                    StorageReference profileRef = storageReference.child(key).child(imageName);

// Delete the file
                    profileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                            Toast.makeText(getApplicationContext(), "Succ", Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });
                } catch (Exception e) {
                    //Blank
                    Log.e("errordelete", e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                findViewById(R.id.reg_view).setVisibility(View.GONE);
            }
        });
    }

    public void imgClick(View view) {
        i = 10;
        openGallery();
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

}
