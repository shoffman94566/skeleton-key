package com.kp.skey;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
    private boolean advancedModeActivated;
    private EditText mLowercaseEditText;
    private EditText mCapsEditText;
    private EditText mNumeralsEditText;
    private EditText mSymbolsEditText;

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

        mLowercaseEditText = (EditText) findViewById(R.id.lower_case_edittext);
        mCapsEditText = (EditText) findViewById(R.id.caps_edittext);
        mNumeralsEditText = (EditText) findViewById(R.id.numerals_edittext);
        mSymbolsEditText = (EditText) findViewById(R.id.symbols_edittext);

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

                Util.hideKeyboard(CreateActivity.this);
                if (allCheckBoxesUnchecked()) {
                    return;
                }
                
                if (invalidPasswordLength()){
                    return;
                }

                if(mChangeValue) {
                    String siteName = mSiteName.getText().toString();
                    if (siteName != null && siteName.length() != 0 && SkeyApplication.containsKey(siteName)) {
                        updatePreviousPassword(siteName);
                        try {
                            generatePassword(true, siteName, timestamp);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(CreateActivity.this,
                                "There is no U-U password with that site name. Check the spelling.",
                                Toast.LENGTH_LONG).show();
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
                advancedModeActivated = true;
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

    private void generatePassword(boolean force, final String siteName, long timestamp) throws JSONException {
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

        if (SkeyApplication.containsKey(siteName) && !force) {
            Toast.makeText(
                    this,
                    "Password previously created for this site.",
                    Toast.LENGTH_LONG).show();
            return;
        }

        passkey = null;
        int lowerCaseLength = 0;
        int upperCaseLength = 0;
        int symbolsLength = 0;
        int numericalsLength = 0;
        try {
            if(advancedModeActivated) {
                if (mLowercaseEditText.getText() != null && mLowercaseEditText.getText().length() > 0 ) {
                    lowerCaseLength = Integer.parseInt(String.valueOf(mLowercaseEditText.getText()));
                }
                if (mCapsEditText.getText() != null && mCapsEditText.getText().length() > 0) {
                    upperCaseLength = Integer.parseInt(String.valueOf(mCapsEditText.getText()));
                }
                if (mNumeralsEditText.getText() != null && mNumeralsEditText.getText().length() > 0) {
                    numericalsLength = Integer.parseInt(String.valueOf(mNumeralsEditText.getText()));
                }
                if (mSymbolsEditText.getText() != null && mSymbolsEditText.getText().length() > 0) {
                    symbolsLength = Integer.parseInt(String.valueOf(mSymbolsEditText.getText()));
                }

                final int newLength = numericalsLength + symbolsLength + upperCaseLength + lowerCaseLength;
                if (newLength != len) {
                    Toast.makeText(this,
                            "Password characters don't equal password length.",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                final int finalNumericalsLength = numericalsLength;
                final int finalSymbolsLength = symbolsLength;
                final int finalUpperCaseLength = upperCaseLength;
                final int finalLowerCaseLength = lowerCaseLength;
                final boolean numChecked = mNumeralsCheckbox.isChecked();
                final boolean symbolChecked = mSymbolCheckbox.isChecked();
                final boolean capsChecked = mCapsCheckbox.isChecked();
                final boolean lowercaseChecked = mlowercaseCheckbox.isChecked();

                new AsyncTask<Void, Void, String>() {
                    @Override
                    protected String doInBackground(final Void ... params) {
                        while( passkey == null || !PasswordGenerator.containsCorrectCharacterCategories(passkey, finalNumericalsLength, finalSymbolsLength, finalUpperCaseLength, finalLowerCaseLength)) {
                            long newTimestamp = System.currentTimeMillis();
                            setTimestamp(newTimestamp);

                            try {
                                passkey = PasswordGenerator.generate(siteName,
                                        numChecked, symbolChecked,
                                        capsChecked, lowercaseChecked,
                                        newLength, newTimestamp);
                            } catch (NoSuchAlgorithmException e) {
                                e.printStackTrace();
                            }
                        }
                        return passkey;


                    }

                    @Override
                    protected void onPostExecute( final String result ) {
                        mGeneratePassword.setVisibility(View.GONE);
                        mRegeneratePassword.setVisibility(View.VISIBLE);
                        mGeneratedPasswordTextView.setText(result);
                    }
                }.doInBackground();


            } else {
                long newTimestamp = System.currentTimeMillis();
                setTimestamp(newTimestamp);
                passkey = PasswordGenerator.generate(siteName,
                        mNumeralsCheckbox.isChecked(), mSymbolCheckbox.isChecked(),
                        mCapsCheckbox.isChecked(), mlowercaseCheckbox.isChecked(),
                        len, newTimestamp);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }



        mGeneratePassword.setVisibility(View.GONE);
        mRegeneratePassword.setVisibility(View.VISIBLE);
        mGeneratedPasswordTextView.setText(passkey);
    }

    private void setTimestamp(long newTimestamp) {
        timestamp = newTimestamp;
    }

    private void savePassword() throws JSONException {
        SkeyApplication.put(siteName, len, passkey, timestamp, mlowercaseCheckbox.isChecked(),
                mCapsCheckbox.isChecked(), mNumeralsCheckbox.isChecked(), mSymbolCheckbox.isChecked());

    }

}