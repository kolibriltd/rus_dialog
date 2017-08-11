package com.wearesputnik.istoria.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.UILApplication;
import com.wearesputnik.istoria.helpers.ResultInfo;
import com.wearesputnik.istoria.models.IstoriaInfo;

public class SingupActivity extends BaseActivity {
    EditText editEmail, editPassword, editConfirmPassword;
    TextView btnLogin;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_singup);

        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
        editConfirmPassword = (EditText) findViewById(R.id.editConfirmPassword);
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        btnSignUp = (Button) findViewById(R.id.btnSignUp);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SingupActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editEmail.getText().toString().trim().isEmpty() && !editPassword.getText().toString().trim().isEmpty() && !editConfirmPassword.getText().toString().trim().isEmpty()) {
                    if (editPassword.getText().toString().trim().equals(editConfirmPassword.getText().toString().trim())) {
                        //new getSignUpTask().execute(editEmail.getText().toString(), editPassword.getText().toString());
                    }
                }
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(SingupActivity.this, GuestActivity.class);
            startActivity(intent);
            finish();
            return false;
        }

        return super.onKeyDown(keyCode, event);
    }

//    class getSignUpTask extends AsyncTask<String, String, ResultInfo> {
//        Dialog dialog;
//        protected void onPreExecute() {
//            super.onPreExecute();
//            dialog = new Dialog(SingupActivity.this, R.style.TransparentProgressDialog);
//            dialog.setCancelable(false);
//            dialog.setCanceledOnTouchOutside(false);
//            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
//            dialog.setContentView(R.layout.custom_progress_dialog);
//            dialog.show();
//        }
//
//        @Override
//        protected ResultInfo doInBackground(String... strings) {
//            ResultInfo result = httpConect.setSingUp(strings[0], strings[1]);
//            return result;
//        }
//
//        protected void onPostExecute(ResultInfo result) {
//            dialog.dismiss();
//            if (result != null) {
//                if (result.status == 0) {
//                    UILApplication.AppKey = result.userInfoResult.app_key;
//                    IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
//                    if (istoriaInfo != null) {
//                        istoriaInfo.AppKey = result.userInfoResult.app_key;
//                        istoriaInfo.save();
//                    }
//                    else {
//                        IstoriaInfo newIstoriaInfo = new IstoriaInfo();
//                        newIstoriaInfo.AppKey = result.userInfoResult.app_key;
//                        newIstoriaInfo.IsViewTwoScreen = false;
//                        newIstoriaInfo.IsTapViewScreen = false;
//                        newIstoriaInfo.save();
//                    }
//                    Intent intent = new Intent(SingupActivity.this, ListBookActivity.class);
//                    startActivity(intent);
//                    finish();
//                }
//                else {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(SingupActivity.this);
//                    builder.setTitle("Ошибка")
//                            .setMessage(result.error)
//                            .setCancelable(false)
//                            .setNegativeButton("OK", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialogInterface, int i) {
//                                    dialogInterface.cancel();
//                                }
//                            });
//                    AlertDialog alertDialog = builder.create();
//                    alertDialog.show();
//                }
//            }
//            super.onPostExecute(result);
//        }
//    }
}
