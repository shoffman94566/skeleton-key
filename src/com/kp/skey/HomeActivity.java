package com.kp.skey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends FooterActivity {
    private View checkButton;
    private View changeButton;
    private View createButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_home);
        super.onCreate(savedInstanceState);

        findViewById(R.id.main_menu).setVisibility(View.GONE);

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
                myIntent.putExtra("change", false);
                HomeActivity.this.startActivity(myIntent);
            }
        });

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(HomeActivity.this, CreateActivity.class);
                myIntent.putExtra("change", true);
                HomeActivity.this.startActivity(myIntent);
            }
        });

        Button mainMenuButton = (Button) findViewById(R.id.main_menu);
        mainMenuButton.setVisibility(View.GONE);


    }
}