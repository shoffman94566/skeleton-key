package com.kp.skey;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateActivity extends FooterActivity {
    private Button mGeneratePassword;
    private EditText mPasswordLength;
    private CheckBox mSymboldCheckbox;
    private CheckBox mNumeralsCheckbox;
    private CheckBox mCapsCheckbox;
    private CheckBox mlowercaseCheckbox;
    private EditText mSiteName;
    private TextView mGeneratedPasswordTextView;
    private Button mRegeneratePassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_create);
        super.onCreate(savedInstanceState);
        SharedPrefs.init(this);

        mSiteName = (EditText) findViewById(R.id.check_site_name);
        mlowercaseCheckbox = (CheckBox) findViewById(R.id.lower_case_checkbox);
        mCapsCheckbox = (CheckBox) findViewById(R.id.caps_checkbox);
        mNumeralsCheckbox = (CheckBox) findViewById(R.id.numerals_checkbox);
        mSymboldCheckbox = (CheckBox) findViewById(R.id.symbols_checkbox);
        mPasswordLength = (EditText) findViewById(R.id.password_length);

        mGeneratePassword = (Button) findViewById(R.id.check_password_button);
        mRegeneratePassword = (Button) findViewById(R.id.regenerate_password_button);
        mGeneratedPasswordTextView = (TextView) findViewById(R.id.generated_password);
        mRegeneratePassword.setVisibility(View.GONE);

        mGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePassword(false);

            }
        });

        mRegeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generatePassword(true);

            }
        });
    }

    private void generatePassword(boolean force) {

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

        if (SkeyApplication.containsKey(sitename) && !force) {
            Toast.makeText(
                    this,
                    "Password " + SkeyApplication.getMyMap(sitename)
                            + " previously created for this site.",
                    Toast.LENGTH_LONG).show();
            return;
        }
        SkeyApplication.put(sitename, passkey);
        mGeneratePassword.setVisibility(View.GONE);
        mRegeneratePassword.setVisibility(View.VISIBLE);
        mGeneratedPasswordTextView.setText(passkey);
    }

}