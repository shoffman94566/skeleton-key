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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_check);
        super.onCreate(savedInstanceState);
        SharedPrefs.init(this);

        mGeneratePasswordButton = (Button) findViewById(R.id.check_password_button);
        mCheckPasswordTextView = (TextView) findViewById(R.id.check_password_textview);
        mCheckSiteNameEditText = (EditText) findViewById(R.id.check_site_name);
        mCheckCreatePasswordButton = (Button) findViewById(R.id.check_create_password);

        mCheckCreatePasswordButton.setVisibility(View.GONE);

        mCheckCreatePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(CheckActivity.this, CreateActivity.class);
                CheckActivity.this.startActivity(myIntent);
            }
        });

        mGeneratePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String siteName = mCheckSiteNameEditText.getText().toString();
                if (SkeyApplication.containsKey(siteName)) {
                    String password = SkeyApplication.getMyMap(siteName);
                    mCheckPasswordTextView.setText(password);
                } else {
                    mCheckPasswordTextView.setText("You have no U-U password for this site name");
                    mCheckCreatePasswordButton.setVisibility(View.VISIBLE);

                }

            }
        });
    }
}