package com.kp.skey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends Activity {
    private View checkButton;
    private View changeButton;
    private View createButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        checkButton = findViewById(R.id.check_button);
        createButton = findViewById(R.id.create_button);
        changeButton = findViewById(R.id.change_button);

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomeActivity.this, CheckActivity.class);
                HomeActivity.this.startActivity(myIntent);
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomeActivity.this, CreateActivity.class);
                HomeActivity.this.startActivity(myIntent);
            }
        });


    }
}