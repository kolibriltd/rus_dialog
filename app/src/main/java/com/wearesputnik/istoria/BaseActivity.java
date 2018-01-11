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
        if (HttpConnectClass.isOnline(BaseActivity.this)) {
            httpConect = HttpConnectClass.getInstance();
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
            builder.setTitle("Ошибка")
                    .setMessage("Интернет соединение отсутствует")
                    .setCancelable(false)
                    .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }
    }
}
