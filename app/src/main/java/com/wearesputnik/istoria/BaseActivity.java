package com.wearesputnik.istoria;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wearesputnik.istoria.helpers.HttpConnectClass;

public class BaseActivity extends AppCompatActivity {
    public HttpConnectClass httpConect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        httpConect = HttpConnectClass.getInstance();
    }
}
