package com.kp.skey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class CheckActivity extends FooterActivity {
    private Button mGeneratePasswordButton;
    private TextView mCheckPasswordTextView;
    private EditText mCheckSiteNameEditText;
    private Button mCheckCreatePasswordButton;
    private Button mCopyPasswordButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_check);
        super.onCreate(savedInstanceState);
        SharedPrefs.init(this);

        mGeneratePasswordButton = (Button) findViewById(R.id.check_password_button);
        mCheckPasswordTextView = (TextView) findViewById(R.id.check_password_textview);
        mCheckSiteNameEditText = (EditText) findViewById(R.id.check_site_name);
        mCheckCreatePasswordButton = (Button) findViewById(R.id.check_create_password);
        mCopyPasswordButton = (Button) findViewById(R.id.copy_password);

        mCheckCreatePasswordButton.setVisibility(View.GONE);
        mCopyPasswordButton.setVisibility(View.GONE);

        mCheckCreatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CheckActivity.this, CreateActivity.class);
                myIntent.putExtra("change", false);
                if (mCheckSiteNameEditText.getText().toString() != null && mCheckSiteNameEditText.getText().toString().length() != 0) {
                    myIntent.putExtra("siteName", mCheckSiteNameEditText.getText().toString());
                }
                CheckActivity.this.startActivity(myIntent);
            }
        });

        mGeneratePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String siteName = mCheckSiteNameEditText.getText().toString();
                Util.hideKeyboard(CheckActivity.this);
                if (SkeyApplication.containsKey(Util.md5(siteName))) {
                    String password = SkeyApplication.getPassword(siteName);
                    mCheckPasswordTextView.setText(password);
                    Util.copyToClipboard(CheckActivity.this, password);
                } else {
                    mCheckPasswordTextView.setText("You have no U-U password for this site name. Check the spelling");
                    mCheckCreatePasswordButton.setVisibility(View.VISIBLE);
                    mCheckCreatePasswordButton.setText("Create a U-U Password for the site name");

                }

            }
        });

        mCopyPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Implement password copying
            }
        });
    }
}