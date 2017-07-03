package com.wearesputnik.istoria;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.activeandroid.query.Select;
import com.wearesputnik.istoria.activity.GuestActivity;
import com.wearesputnik.istoria.activity.ListBookActivity;
import com.wearesputnik.istoria.models.IstoriaInfo;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
        if (istoriaInfo != null) {
            if (istoriaInfo.AppKey != null) {
                UILApplication.AppKey = istoriaInfo.AppKey;
                Intent i = new Intent(SplashActivity.this, ListBookActivity.class);
                startActivity(i);
                finish();
            }
            else {
                Intent i = new Intent(SplashActivity.this, GuestActivity.class);
                startActivity(i);
                finish();
            }
        }
        else {
            Intent i = new Intent(SplashActivity.this, GuestActivity.class);
            startActivity(i);
            finish();
        }
    }
}
