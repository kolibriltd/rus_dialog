package com.wearesputnik.istoria.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.UserInfo;
import com.wearesputnik.istoria.models.IstoriaInfo;

public class ProfileActivity extends BaseActivity {
    TextView txtNameDisplay, txtEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Профиль");

        txtNameDisplay = (TextView) findViewById(R.id.txtNameDisplay);
        txtEmail = (TextView) findViewById(R.id.txtEmail);

        new getProfile().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
            case R.id.action_loguot:
                LogOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void LogOut() {
        IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
        istoriaInfo.delete();
        startActivity(new Intent(ProfileActivity.this, GuestActivity.class));
        finish();
    }

    class getProfile extends AsyncTask<String, String, UserInfo> {
        Dialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(ProfileActivity.this, R.style.TransparentProgressDialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_progress_dialog);
            dialog.show();
        }

        @Override
        protected UserInfo doInBackground(String... strings) {
            UserInfo result = httpConect.getProfile();
            return result;
        }

        protected void onPostExecute(UserInfo result) {
            if (result != null) {
                ViewProfile(result);
                dialog.dismiss();
            }
            else {
                dialog.dismiss();
            }
            super.onPostExecute(result);
        }
    }

    private void ViewProfile(UserInfo userInfo) {
        txtNameDisplay.setText(userInfo.firs_name);
        txtEmail.setText(userInfo.email);
    }
}
