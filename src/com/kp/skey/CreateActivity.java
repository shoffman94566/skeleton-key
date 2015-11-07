package com.kp.skey;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import org.json.JSONException;

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
    private Button mAccept;
    private long timestamp;
    private String siteName;
    private Integer len;
    private String passkey;
    private Button mAdvancedButton;
    private LinearLayout mCheckBoxContainer;
    private LinearLayout mEditTextContainer;

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
        mAccept = (Button) findViewById(R.id.accept_password_button);

        mPreviousPassword = (TextView) findViewById(R.id.previous_password);
        mPreviousPassword.setVisibility(View.GONE);
        mChangeTextHelp = (TextView) findViewById(R.id.users_previous_password);
        mChangeTextHelp.setVisibility(View.GONE);

        mAdvancedButton = (Button) findViewById(R.id.advanced_button);
        mCheckBoxContainer = (LinearLayout) findViewById(R.id.checkbox_container);
        mEditTextContainer = (LinearLayout) findViewById(R.id.edittext_container);
        mEditTextContainer.setVisibility(View.GONE);

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
                siteName = mSiteName.getText().toString();
                timestamp = System.currentTimeMillis();

                Util.hideKeyboard(CreateActivity.this);
                if (allCheckBoxesUnchecked()) {
                    return;
                }
                
                if (invalidPasswordLength()){
                    return;
                }

                if(mChangeValue) {
                    String siteName = mSiteName.getText().toString();
                    if (siteName != null && siteName.length() != 0) {
                        updatePreviousPassword(siteName);
                    }
                    try {
                        generatePassword(true, siteName, timestamp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        generatePassword(false, siteName, timestamp);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        mRegeneratePassword.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                siteName = mSiteName.getText().toString();
                timestamp = System.currentTimeMillis();

                Util.hideKeyboard(CreateActivity.this);
                if (allCheckBoxesUnchecked()) {
                    return;
                }

                if (invalidPasswordLength()){
                    return;
                }

                try {
                    generatePassword(true, siteName, timestamp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Util.copyToClipboard(CreateActivity.this, passkey);
                    savePassword();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mAdvancedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCheckBoxContainer.setVisibility(View.GONE);
                mEditTextContainer.setVisibility(View.VISIBLE);
            }
        });

    }

    private boolean invalidPasswordLength() {
        int length = Integer.parseInt(mPasswordLength.getText().toString());
        if (length < 4 || length > 16){
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


    private void updatePreviousPassword(String siteName) {
        try {
            oldPassword = SkeyApplication.getPassword(siteName);
            mPreviousPassword.setVisibility(View.VISIBLE);
            mChangeTextHelp.setVisibility(View.VISIBLE);
            mChangeTextHelp.setText(oldPassword);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void generatePassword(boolean force, String siteName, long timestamp) throws JSONException {
        if (mSiteName.getText() == null
                || mSiteName.getText().length() == 0) {
            Toast.makeText(this, "Enter Site name", Toast.LENGTH_LONG)
                    .show();
            return;
        }
        if (mSiteName.getText().length() < 1) {
            Toast.makeText(this,
                    "Site name should at least be 2 charaters",
                    Toast.LENGTH_LONG).show();
            return;
        }

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            len = Integer.valueOf(mPasswordLength.getText().toString());
            if (len < 4) {
                len = 4;
                mPasswordLength.setText("4");
            } else if (len > 16) {
                len = 16;
                mPasswordLength.setText("16");
            }

        } catch (Exception e) {
            e.printStackTrace();
            len = 4;
        }

        passkey = null;
        try {
            passkey = PasswordGenerator.generate(siteName,
                    mNumeralsCheckbox.isChecked(), mSymbolCheckbox.isChecked(),
                    mCapsCheckbox.isChecked(), mlowercaseCheckbox.isChecked(),
                    len, timestamp);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        if (SkeyApplication.containsKey(siteName) && !force) {
            Toast.makeText(
                    this,
                    "Password previously created for this site.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        mGeneratePassword.setVisibility(View.GONE);
        mRegeneratePassword.setVisibility(View.VISIBLE);
        mGeneratedPasswordTextView.setText(passkey);
    }

    private void savePassword() throws JSONException {
        SkeyApplication.put(siteName, len, passkey, timestamp, mlowercaseCheckbox.isChecked(),
                mCapsCheckbox.isChecked(), mNumeralsCheckbox.isChecked(), mSymbolCheckbox.isChecked());

    }

}