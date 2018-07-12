package com.subratgupta.rentoo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.service.dreams.DreamService;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;

public class RegisterOwnerProperty extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 1;
    private static final int REQUEST_INVITE = 1;
    EditText mPhoneNumberField;
    EditText mNameField;
    EditText mAddressField;
    EditText mEmailField;
    EditText mRentField;
    ImageView image;
    LinearLayout linearLayout;

    private int i=1;
    private RadioGroup mTypeOfSpace;
    private RadioGroup mFacilities;
    private RadioGroup mTenantType;
    private RadioButton radioButton;
    private ImageView mAddMessageImageView;
    private static final String TAG = "RegisterOwnerProperty";
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

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

        mAddMessageImageView = (ImageView) findViewById(R.id.addMessageImageView);
        mAddMessageImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(i<=5){
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_IMAGE);
                } else {
                    Toast.makeText(getApplicationContext(),"Sorry, You can add only 5 images.",Toast.LENGTH_LONG).show();
                }
            }
        });

        RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("details").child("isComplete").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String value = dataSnapshot.getValue(String.class);
                if(Objects.equals(value, "true")){
                    goTo();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                findViewById(R.id.reg_view).setVisibility(View.GONE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    try {
                        Log.d(TAG, "Uri: " + uri.toString());
                    }
                    catch (Exception e){
                        Log.d(TAG, e.getMessage());
                    }

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
                                        image = new ImageView(getApplicationContext());
                                        mAddMessageImageView.setClickable(false);
                                        Glide.with(getApplicationContext()).
                                                load(LOADING_IMAGE_URL).into(image);

                                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(240, 240);
                                        image.setLayoutParams(layoutParams);
                                        linearLayout.addView(image);
                                        putImageInStorage(storageReference, uri, key);
                                    } else {
                                        Log.w(TAG, "Unable to write message to database.",
                                                databaseError.toException());
                                    }
                                }
                            });
                }
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

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
        }
    }

    private void putImageInStorage(StorageReference storageReference, Uri uri, final String key) {
        storageReference.putFile(uri).addOnCompleteListener(this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            MessageHelper messageHelper =
                                    new MessageHelper(null, null,
                                            task.getResult().getDownloadUrl()
                                                    .toString());

                            RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("photos").child(""+i).setValue(messageHelper);
                            i++;

                            showImages(messageHelper.getImageUrl());
                            mAddMessageImageView.setClickable(true);
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

    public void showImages(String url){
        new DownLoadImageTask(image).execute(url);
    }

    public void onClick(View view) {

        try {
            DatabaseReference db = RegisterOwnerNumber.mDatabase.child("users").child(MainActivity.readData("user_id")).child("details");
            int selectedId = mTypeOfSpace.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            db.child("type_of_space").setValue(radioButton.getText().toString());
            selectedId = mFacilities.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            db.child("facilities").setValue(radioButton.getText().toString());
            selectedId = mTenantType.getCheckedRadioButtonId();
            radioButton = (RadioButton) findViewById(selectedId);
            db.child("tenant_type").setValue(radioButton.getText().toString());
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

    private void goTo(){
        Intent goToOwnerHomePage = new Intent(this, OwnerHomePage.class);
        startActivity(goToOwnerHomePage);
    }
}