package com.wearesputnik.istoria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wearesputnik.istoria.activity.ListBookActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent i = new Intent(SplashActivity.this, ListBookActivity.class);
        startActivity(i);
        finish();

    }
}
