package com.kp.skey;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CreateActivity extends FooterActivity {
    private Button mGeneratePassword;
    private EditText mPasswordLength;
    private CheckBox mSymbolCheckbox;
    private CheckBox mNumeralsCheckbox;
    private CheckBox mCapsCheckbox;
    private CheckBox mlowercaseCheckbox;
    private EditText mSiteName;
    private TextView mGeneratedPasswordTextView;
    private Button mRegeneratePassword;
    private boolean mChangeValue;
    private TextView mTitle;
    private TextView mChangeTextHelp;
    private TextView mPreviousPassword;
    private String oldPassword;
    private String mpassedSiteName;

    private void validateAtLeastOneCheckBoxChecked() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_create);
        super.onCreate(savedInstanceState);
        SharedPrefs.init(this);

        mChangeValue = getIntent().getExtras().getBoolean("change", false);
        mpassedSiteName = getIntent().getExtras().getString("siteName");

        mTitle = (TextView) findViewById(R.id.create_title);
        mSiteName = (EditText) findViewById(R.id.check_site_name);
        mlowercaseCheckbox = (CheckBox) findViewById(R.id.lower_case_checkbox);
        mCapsCheckbox = (CheckBox) findViewById(R.id.caps_checkbox);
        mNumeralsCheckbox = (CheckBox) findViewById(R.id.numerals_checkbox);
        mSymbolCheckbox = (CheckBox) findViewById(R.id.symbols_checkbox);
        mPasswordLength = (EditText) findViewById(R.id.password_length);

        mGeneratePassword = (Button) findViewById(R.id.check_password_button);
        mRegeneratePassword = (Button) findViewById(R.id.regenerate_password_button);
        mGeneratedPasswordTextView = (TextView) findViewById(R.id.generated_password);
        mRegeneratePassword.setVisibility(View.GONE);

        mPreviousPassword = (TextView) findViewById(R.id.previous_password);
        mPreviousPassword.setVisibility(View.GONE);
        mChangeTextHelp = (TextView) findViewById(R.id.change_text_help);
        mChangeTextHelp.setVisibility(View.GONE);

        // set sitename is coming from check activity
        if (mpassedSiteName != null) {
            mSiteName.setText(mpassedSiteName);
        }

        // different setup if change screen
        if (mChangeValue) {
          mTitle.setText("To Change a U-U Password");
        }

        mGeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(CreateActivity.this);
                if (allCheckBoxesUnchecked()) {
                    return;
                }
                
                if (invalidPasswordLength()){
                    return;
                }

                if(mChangeValue) {
                    updatePreviousPassword();
                    generatePassword(true);
                } else {
                    generatePassword(false);
                }

            }
        });

        mRegeneratePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.hideKeyboard(CreateActivity.this);
                if (allCheckBoxesUnchecked()) {
                    return;
                }

                if (invalidPasswordLength()){
                    return;
                }

              generatePassword(true);
            }
        });

    }

    private boolean invalidPasswordLength() {
        int length = Integer.parseInt(mPasswordLength.getText().toString());
        Log.d("TAg", String.valueOf(length));
        if (length < 5 || length > 16){
            Toast.makeText(this,
                    "Invalid Length",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    private boolean allCheckBoxesUnchecked() {
        if (!mlowercaseCheckbox.isChecked() && !mCapsCheckbox.isChecked() && !mNumeralsCheckbox.isChecked() && !mSymbolCheckbox.isChecked()) {
            Toast.makeText(this,
                    "Invalid Entry",
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }


    private void updatePreviousPassword() {
        String siteName = mSiteName.getText().toString();
        oldPassword = SkeyApplication.getMyMap(siteName);
        mPreviousPassword.setVisibility(View.VISIBLE);
        String baseString = "Previous U-U  Password :";
        String combinedString = baseString.concat(oldPassword);
        mPreviousPassword.setText(combinedString);
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
                    mNumeralsCheckbox.isChecked(), mSymbolCheckbox.isChecked(),
                    mCapsCheckbox.isChecked(), mlowercaseCheckbox.isChecked() ,len);
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