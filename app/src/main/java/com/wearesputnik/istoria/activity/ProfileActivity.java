package com.wearesputnik.istoria.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.UILApplication;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.UserInfo;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;
import com.wearesputnik.istoria.models.UserModel;

import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;

public class ProfileActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener{
    TextView txtNameDisplay, txtEmail, txtOplata;
    IstoriaInfo istoriaInfo;
    ImageView imgProfilePhoto;
    boolean isVibrate = false;

    private GoogleApiClient mGoogleApiClient;

/*    private class PurchaseListener extends EmptyRequestListener<Purchase> {
        // your code here
    }

    private class InventoryCallback implements Inventory.Callback {
        @Override
        public void onLoaded(Inventory.Products products) {
            // your code here
        }
    }

    private final ActivityCheckout mCheckout = Checkout.forActivity(this, UILApplication.getInstance().getBilling());
    private Inventory mInventory;*/

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
        txtOplata = (TextView) findViewById(R.id.txtOplata);
        imgProfilePhoto = (ImageView) findViewById(R.id.imgProfilePhoto);

        istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
        if (istoriaInfo != null) {
            if (istoriaInfo.IsPush) {
                isVibrate = true;
            }
        }

        /*txtOplata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(BillingRequests requests) {
                        requests.purchase(ProductTypes.IN_APP, "weekly", null, mCheckout.getPurchaseFlow());
                    }
                });
            }
*/
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        /*mCheckout.start();

        mCheckout.createPurchaseFlow(new PurchaseListener());

        mInventory = mCheckout.makeInventory();
        mInventory.load(Inventory.Request.create()
                .loadAllPurchases()
                .loadSkus(ProductTypes.IN_APP, "weekly"), new InventoryCallback());*/

        new getProfile().execute();
    }

   /* @Override
    protected void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        if (isVibrate) {
            menu.findItem(R.id.action_push).setTitle(R.string.action_push_on);
        }
        else {
            menu.findItem(R.id.action_push).setTitle(R.string.action_push_off);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
            case R.id.action_push:
                if (isVibrate) {
                    isVibrate = false;
                    istoriaInfo.IsPush = false;
                    istoriaInfo.save();

                }
                else {
                    isVibrate = true;
                    istoriaInfo.IsPush = true;
                    istoriaInfo.save();
                }
                invalidateOptionsMenu();
                break;
            case R.id.action_loguot:
                LogOut();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void LogOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
            new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
                    istoriaInfo.delete();
                    startActivity(new Intent(ProfileActivity.this, GuestActivity.class));
                    finish();
                }
            });

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        //Log.d(TAG, "onConnectionFailed:" + connectionResult);
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
                UserModel.AddEditUser(result);
                ViewProfile(result);
                dialog.dismiss();
            }
            else {
                dialog.dismiss();
            }
            super.onPostExecute(result);
        }
    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCheckout.onActivityResult(requestCode, resultCode, data);

    }*/

    private void ViewProfile(UserInfo userInfo) {
        txtNameDisplay.setText(userInfo.firs_name);
        txtEmail.setText(userInfo.email);
        if (!userInfo.photo.trim().equals("")) {
            Glide.with(ProfileActivity.this)
                .load(userInfo.photo)
                .apply(RequestOptions.circleCropTransform())
                .into(imgProfilePhoto);
        }
    }
}
