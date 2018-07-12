package com.subratgupta.rentoo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;

public class RegisterTenant extends AppCompatActivity {

    final String IS_FIRST_TIME = "is_first_time";
    private static final String TAG = "TenantPhoneAuthActivity";

    private static final String KEY_VERIFY_IN_PROGRESS = "key_verify_in_progress";

    private static final int STATE_INITIALIZED = 1;
    private static final int STATE_CODE_SENT = 2;
    private static final int STATE_VERIFY_FAILED = 3;
    private static final int STATE_VERIFY_SUCCESS = 4;
    private static final int STATE_SIGNIN_FAILED = 5;
    private static final int STATE_SIGNIN_SUCCESS = 6;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private TextView mStatusText;
    private EditText mPhoneNumberField;
    private EditText mVerificationField;

    private Button mStartButton;
    private Button mVerifyButton;
    private Button mResendButton;

    public static DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_tenant);

        if (savedInstanceState != null) {
            onRestoreInstanceState(savedInstanceState);
        }

        if (!MainActivity.readData(IS_FIRST_TIME).equals("false")) {
            findViewById(R.id.contentPanel).setVisibility(View.VISIBLE);
            findViewById(R.id.ok).setVisibility(View.VISIBLE);
        }
    }

    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.ok:
                MainActivity.writeData(IS_FIRST_TIME, "false");
                findViewById(R.id.contentPanel).setVisibility(View.GONE);
                findViewById(R.id.ok).setVisibility(View.GONE);
                break;

            case R.id.submit:

                break;
        }
    }
}
