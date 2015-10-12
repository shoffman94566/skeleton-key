package com.kp.skey;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateActivity extends Activity {
    private Button mGeneratePassword;
    private EditText mPasswordLength;
    private CheckBox mSymboldCheckbox;
    private CheckBox mNumeralsCheckbox;
    private CheckBox mCapsCheckbox;
    private CheckBox mlowercaseCheckbox;
    private EditText mSiteName;
    private TextView mGeneratedPasswordTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        SharedPrefs.init(this);

        mSiteName = (EditText) findViewById(R.id.site_name);
        mlowercaseCheckbox = (CheckBox) findViewById(R.id.lower_case_checkbox);
        mCapsCheckbox = (CheckBox) findViewById(R.id.caps_checkbox);
        mNumeralsCheckbox = (CheckBox) findViewById(R.id.numerals_checkbox);
        mSymboldCheckbox = (CheckBox) findViewById(R.id.symbols_checkbox);
        mPasswordLength = (EditText) findViewById(R.id.password_length);

        mGeneratePassword = (Button) findViewById(R.id.generate_password_button);
        mGeneratedPasswordTextView = (TextView) findViewById(R.id.generated_password);

        mGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePassword();

            }
        });
    }

    private void generatePassword() {

        String sitename = mSiteName.getText().toString();
//        View view = instance.getCurrentFocus();

        if (mSiteName.getText() == null
                || mSiteName.getText().length() == 0) {
            Toast.makeText(this, "Enter Site name", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (mSiteName.getText().length() < 2) {
            Toast.makeText(this,
                    "Enter Site name should alteast be 2 charaters",
                    Toast.LENGTH_LONG).show();
            return;
        }
//        if (view != null) {
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Integer len = 5;
        try {
            len = Integer.valueOf(mPasswordLength.getText().toString());
            if (len < 5) {
                len = 5;
                mPasswordLength.setText("5");
            } else if (len > 24) {
                len = 24;
                mPasswordLength.setText("24");
            }

        } catch (Exception e) {
            e.printStackTrace();
            len = 5;
        }

        String passkey = null;
        try {
            passkey = PasswordGenerator.generate(sitename,
                    mNumeralsCheckbox.isChecked(), mSymboldCheckbox.isChecked(),
                    mCapsCheckbox.isChecked(), len);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (SkeyApplication.containsKey(sitename)) {
            Toast.makeText(
                    this,
                    "Password " + SkeyApplication.getMyMap(sitename)
                            + " previously created for this site.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        SkeyApplication.put(sitename, passkey);
        mGeneratedPasswordTextView.setText(passkey);
    }

}