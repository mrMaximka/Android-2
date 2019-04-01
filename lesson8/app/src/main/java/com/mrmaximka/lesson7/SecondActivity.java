package com.mrmaximka.lesson7;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import static android.content.res.Configuration.ORIENTATION_LANDSCAPE;

public class SecondActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getResources().getConfiguration().orientation == ORIENTATION_LANDSCAPE) {   // Уничтожение активити при повороте
            finish();
            return;
        }

        if (savedInstanceState == null) {
            SecondFragment details = new SecondFragment();
            details.setArguments(getIntent().getExtras());
            getSupportFragmentManager().beginTransaction().add(android.R.id.content, details).commit();
        }
    }
}
