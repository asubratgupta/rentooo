package com.subratgupta.rentoo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.service.dreams.DreamService;
import android.support.annotation.IdRes;
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
import android.view.Window;
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
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.images.ImageRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class RegisterOwnerProperty extends AppCompatActivity {
    Boolean clickAbility = true;
    Uri uri_parent;
    private static final int REQUEST_IMAGE = 1;
    private static final int REQUEST_INVITE = 1;
    EditText mPhoneNumberField;
    EditText mNameField;
    EditText mAddressField;
    EditText mEmailField;
    EditText mRentField;
    ImageView image;
    LinearLayout linearLayout;

    private int i;
    private RadioGroup mTypeOfSpace;
    private RadioGroup mFacilities;
    private RadioGroup mTenantType;
    private RadioButton radioButton;
    private static final String TAG = "RegisterOwnerProperty";
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 1;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_owner_property);

        mPhoneNumberField = (EditText) findViewById(R.id.field_phone_number);
        mNameField = (EditText) findViewById(R.id.name);
        mAddressField = (EditText) findViewById(R.id.address);
        mEmailField = (EditText) findViewById(R.id.email);
        mPhoneNumberField.setText(MainActivity.readData("contact_number"));
        mRentField = (EditText) findViewById(R.id.rent);

        mTypeOfSpace = (RadioGroup) findViewById(R.id.type_radio);
        mFacilities = (RadioGroup) findViewById(R.id.facilities_radio);
        mTenantType = (RadioGroup) findViewById(R.id.tenant_type_radio);

        try {
            RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String name = dataSnapshot.child("details").child("name").getValue(String.class);
                    String address = dataSnapshot.child("details").child("address").getValue(String.class);
                    String email = dataSnapshot.child("details").child("email").getValue(String.class);
                    Integer facilities_int = dataSnapshot.child("details").child("facilities_int").getValue(Integer.class);
                    String rent = dataSnapshot.child("details").child("rent").getValue(String.class);
                    Integer tenant_type_int = dataSnapshot.child("details").child("tenant_type_int").getValue(Integer.class);
                    Integer type_of_space_int = dataSnapshot.child("details").child("type_of_space_int").getValue(Integer.class);
                    String img1 = dataSnapshot.child("photos").child("1").child("imageUrl").getValue(String.class);
                    String img2 = dataSnapshot.child("photos").child("2").child("imageUrl").getValue(String.class);
                    String img3 = dataSnapshot.child("photos").child("3").child("imageUrl").getValue(String.class);
                    String img4 = dataSnapshot.child("photos").child("4").child("imageUrl").getValue(String.class);
                    String img5 = dataSnapshot.child("photos").child("5").child("imageUrl").getValue(String.class);
                    String profile_pic = dataSnapshot.child("profile_pic").child("imageUrl").getValue(String.class);

                    try {
                        if (profile_pic.length()>=1){
                            Glide.with(getApplicationContext()).load(profile_pic).into((ImageView) findViewById(R.id.profile_pic));
                        }
                    }catch (Exception e){
                        Log.e("Rentoo Property Reg", e.getMessage());
                    }

                    ((TextView) findViewById(R.id.name)).setText(name);
                    ((TextView) findViewById(R.id.address)).setText(address);
                    ((TextView) findViewById(R.id.email)).setText(email);
                    ((TextView) findViewById(R.id.rent)).setText(rent);
                    try {
                        radioClick(facilities_int);
                        radioClick(tenant_type_int);
                        radioClick(type_of_space_int);
                    } catch (Exception e) {
                        if (OwnerHomePage.edit || MainActivity.readData("isComplete").equals("true")){
                            Toast.makeText(getApplicationContext(), "Please fill all details", Toast.LENGTH_LONG).show();
                        }
                    }

                    try{
                        if (img1.length()>=1){
                            Glide.with(getApplicationContext()).load(img1).into((ImageView) findViewById(R.id.addMessageImageView1));
                        }
                        if (img2.length()>=1){
                            Glide.with(getApplicationContext()).load(img2).into((ImageView) findViewById(R.id.addMessageImageView2));
                        }
                        if (img3.length()>=1){
                            Glide.with(getApplicationContext()).load(img3).into((ImageView) findViewById(R.id.addMessageImageView3));
                        }
                        if (img4.length()>=1){
                            Glide.with(getApplicationContext()).load(img4).into((ImageView) findViewById(R.id.addMessageImageView4));
                        }
                        if (img5.length()>=1){
                            Glide.with(getApplicationContext()).load(img5).into((ImageView) findViewById(R.id.addMessageImageView5));
                        }
                    }

                    catch (Exception e){
                        Log.e("Rentoo Property", e.getMessage());
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

        RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("details").child("isComplete").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if (Objects.equals(value, "true")) {
                    if (OwnerHomePage.edit){
                        findViewById(R.id.progress_bar).setVisibility(View.GONE);
                        findViewById(R.id.reg_view).setVisibility(View.VISIBLE);
                        findViewById(R.id.skip_btn).setVisibility(View.VISIBLE);
                    }
                    else {
                        goTo();
                    }
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
                RegisterOwnerNumber.mAuth.signOut();
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

    private void radioClick(int id) {
        ((RadioButton) findViewById(id)).performClick();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                final Uri uri;
                if (data != null) {
                    uri = data.getData();
                } else {
                    uri = uri_parent;/*data.getData();*/
                }
                Toast.makeText(getApplicationContext(), uri.getLastPathSegment(), Toast.LENGTH_LONG).show();
                try {
                    Log.d(TAG, "Uri: " + uri.toString());
                } catch (Exception e) {
                    Log.d(TAG, e.getMessage());
                }
                deleteImage();

                MessageHelper tempMessage = new MessageHelper();
                RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("photos").push()
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
                                    linearLayout = (LinearLayout) findViewById(R.id.imageViews);
                                    switch (i) {
                                        case 1:
                                            image = (ImageButton) findViewById(R.id.addMessageImageView1);
                                            break;
                                        case 2:
                                            image = (ImageButton) findViewById(R.id.addMessageImageView2);
                                            break;
                                        case 3:
                                            image = (ImageButton) findViewById(R.id.addMessageImageView3);
                                            break;
                                        case 4:
                                            image = (ImageButton) findViewById(R.id.addMessageImageView4);
                                            break;
                                        case 5:
                                            image = (ImageButton) findViewById(R.id.addMessageImageView5);
                                            break;
                                        case 10:
                                            image = (ImageView) findViewById(R.id.profile_pic);
                                            break;

                                    }
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
        } else if (requestCode == REQUEST_INVITE) {
            if (resultCode == RESULT_OK) {
                // Use Firebase Measurement to log that invitation was sent.
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_sent");

                // Check how many invitations were sent and log.
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
                Log.d(TAG, "Invitations sent: " + ids.length);
            } else {
                // Use Firebase Measurement to log that invitation was not sent
                Bundle payload = new Bundle();
                payload.putString(FirebaseAnalytics.Param.VALUE, "inv_not_sent");
                // Sending failed or it was canceled, show failure message to the user
                Log.d(TAG, "Failed to send invitation.");
            }
        }
    }

    public void imgClick(View view) {

        if (clickAbility) {
            switch (view.getId()) {
                case R.id.addMessageImageView1:
                    i = 1;
                    break;

                case R.id.addMessageImageView2:
                    i = 2;
                    break;
                case R.id.addMessageImageView3:
                    i = 3;
                    break;
                case R.id.addMessageImageView4:
                    i = 4;
                    break;
                case R.id.addMessageImageView5:
                    i = 5;
                    break;
                case R.id.dp_btn:
                    i = 10;
                    openGallery();
                    return;
            }

            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterOwnerProperty.this, Manifest.permission.CAMERA)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ActivityCompat.requestPermissions(RegisterOwnerProperty.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(RegisterOwnerProperty.this,
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
            }

            if (ContextCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                // Permission is not granted
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterOwnerProperty.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    ActivityCompat.requestPermissions(RegisterOwnerProperty.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                } else {
                    // No explanation needed; request the permission
                    ActivityCompat.requestPermissions(RegisterOwnerProperty.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            } else {
                // Permission has already been granted
            }

            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            uri_parent = openCam();
            clickAbility = false;
        }
    }

    private Uri openCam() {
        Uri uri = uri_parent;
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(getExternalCacheDir(),
                    String.valueOf(System.currentTimeMillis()) + ".jpg");
            uri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(intent, REQUEST_IMAGE);
        } catch (Exception e) {
            openGallery();
        }


        return uri;
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    public void skip(View view) {
        goTo();
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
            clickAbility = true;
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
                            if (i <= 5) {
                                RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("photos").child("" + i).setValue(messageHelper);
                                RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("photos_key").child("" + i).setValue(key);
                                RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("photos_key").child("name" + i).setValue(uri.getLastPathSegment());
                            } else if (i == 10) {
                                RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("profile_pic").setValue(messageHelper);
                                RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("profile_pic_key").child("" + i).setValue(key);
                                RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("profile_pic_key").child("name" + i).setValue(uri.getLastPathSegment());
                            }
                            showImages(messageHelper.getImageUrl());
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    public void showImages(String url) {
        new DownLoadImageTask(image).execute(url);
    }

    private void deleteImage(){
        // Create a storage reference from our app
        final StorageReference storageReference =
                FirebaseStorage.getInstance()
                        .getReference(MainActivity.readData("user_id"));

        RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String key = "";
                String imageName = "";
                try{
                    if (i<=5){
                        key = dataSnapshot.child("photos_key").child(""+i).getValue(String.class);
                        imageName = dataSnapshot.child("photos_key").child("name"+i).getValue(String.class);
                    }else if(i==10){
                        key = dataSnapshot.child("profile_pic_key").child(""+i).getValue(String.class);
                        imageName = dataSnapshot.child("profile_pic_key").child("name"+i).getValue(String.class);
                    }
                    // Create a reference to the file to delete
                    StorageReference profileRef = storageReference.child(key).child(imageName);

// Delete the file
                    profileRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // File deleted successfully
                            Toast.makeText(getApplicationContext(),"Succ",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Uh-oh, an error occurred!
                        }
                    });
                } catch (Exception e){
                    //Blank
                    Log.e("errordelete",e.getMessage());
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                findViewById(R.id.reg_view).setVisibility(View.GONE);
            }
        });

    }

    public void onClick(View view) {

        try {
            DatabaseReference db = RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("details");
            int selectedId = mTypeOfSpace.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            db.child("type_of_space").setValue(radioButton.getText().toString());
            db.child("type_of_space_int").setValue(radioButton.getId());
            selectedId = mFacilities.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            db.child("facilities").setValue(radioButton.getText().toString());
            db.child("facilities_int").setValue(radioButton.getId());
            selectedId = mTenantType.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            db.child("tenant_type").setValue(radioButton.getText().toString());
            db.child("tenant_type_int").setValue(radioButton.getId());
            db.child("name").setValue(mNameField.getText().toString());
            db.child("address").setValue(mAddressField.getText().toString());
            db.child("phone").setValue(mPhoneNumberField.getText().toString());
            db.child("email").setValue(mEmailField.getText().toString());
            db.child("rent").setValue(mRentField.getText().toString());
            db.child("isComplete").setValue("true");
            goTo();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Please fill all details.", Toast.LENGTH_LONG).show();
        }
    }

    private void goTo() {
        Intent goToOwnerHomePage = new Intent(this, OwnerHomePage.class);
        this.finish();
        startActivity(goToOwnerHomePage);
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