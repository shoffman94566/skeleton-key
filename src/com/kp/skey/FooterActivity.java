package com.kp.skey;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class FooterActivity extends Activity {
    private Button closeApp;
    private Button mainMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Activity activity = this;

        // set up footer button click handlers
        closeApp = (Button) findViewById(R.id.close_app);
        closeApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finishAffinity();
            }
        });

        mainMenu = (Button) findViewById(R.id.main_menu);
        mainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = new Intent(activity, HomeActivity.class);
                activity.startActivity(myIntent);
            }
        });


    }
}