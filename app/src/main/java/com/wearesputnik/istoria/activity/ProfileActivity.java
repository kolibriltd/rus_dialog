package com.wearesputnik.istoria.activity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.Cache;
import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.UILApplication;
import com.wearesputnik.istoria.helpers.Config;
import com.wearesputnik.istoria.helpers.HttpConnectClass;
import com.wearesputnik.istoria.helpers.UserInfo;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;
import com.wearesputnik.istoria.models.UserModel;
import com.wearesputnik.istoria.models.UtilitModel;

import org.json.JSONObject;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;

import java.util.Date;
import java.util.List;

public class ProfileActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener{
    TextView txtNameDisplay, txtEmail, txtOplata;
    IstoriaInfo istoriaInfo;
    ImageView imgProfilePhoto;
    boolean isVibrate = false;
    private boolean isSubscription = false;

    private String versionName;

    private GoogleApiClient mGoogleApiClient;

    private class PurchaseListener extends EmptyRequestListener<Purchase> {
        @Override
        public void onSuccess(Purchase result) {
            super.onSuccess(result);
            if (result.sku.equals(Config.SUB_SKY_NAME)) {

            }
        }
    }

    private class InventoryCallback implements Inventory.Callback {
        @Override
        public void onLoaded(Inventory.Products products) {
            final Inventory.Product product = products.get(ProductTypes.SUBSCRIPTION);
            if (!product.supported) {
                return;
            }

            List<Purchase> list = product.getPurchases();
//            for (Purchase item : list) {
//                readPurchase(item.data, item.signature);
//            }
            if (product.getSku(Config.SUB_SKY_NAME) != null) {
                String priceP = product.getSku(Config.SUB_SKY_NAME).price;
            }
            if (product.isPurchased(Config.SUB_SKY_NAME)) {
                isSubscription = true;
                txtOplata.setText("Оплачено");
                UserModel.UserSubscrition(true);
                //readPurchase(list.);
            }
        }
    }

    private void readPurchase(String purchaseData, String signature) {
        try {
            JSONObject jsonObject = new JSONObject(purchaseData);
            // ид покупки, для тестовой покупки будет null
            String orderId = jsonObject.optString("orderId");
            // "com.example.myapp"
            String packageName = jsonObject.getString("packageName");
            // "com.example.myapp_testing_inapp1"
            String productId = jsonObject.getString("productId");
            // unix-timestamp времени покупки
            long purchaseTime = jsonObject.getLong("purchaseTime");
            Date date = new Date(Long.parseLong("1517208880269"));
            // PURCHASE_STATUS_PURCHASED
            // PURCHASE_STATUS_CANCELLED
            // PURCHASE_STATUS_REFUNDED
            int purchaseState = jsonObject.getInt("purchaseState");
            // токен покупки, с его помощью можно получить
            // данные о покупке на сервере
            String purchaseToken = jsonObject.getString("purchaseToken");
            // далее вы обрабатываете покупку
            ///new getSubscription().execute(packageName, productId, purchaseToken, signature);
        } catch (Exception e) {
            //...
        }
    }

    private final ActivityCheckout mCheckout = Checkout.forActivity(this, UILApplication.getInstance().getBilling());
    private Inventory mInventory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Профиль");

        PackageInfo packageInfo = null;

        try {
            packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        versionName = packageInfo.versionName;

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

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        txtOplata.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {
                 if (!isSubscription) {
                     mCheckout.whenReady(new Checkout.EmptyListener() {
                         @Override
                         public void onReady(BillingRequests requests) {
                             requests.purchase(ProductTypes.SUBSCRIPTION, Config.SUB_SKY_NAME, null, mCheckout.getPurchaseFlow());
                         }
                     });
                 }
             }
         });

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        mCheckout.start();

        mCheckout.createPurchaseFlow(new PurchaseListener());

        mInventory = mCheckout.makeInventory();
        mInventory.load(Inventory.Request.create()
                .loadAllPurchases()
                .loadSkus(ProductTypes.SUBSCRIPTION, Config.SUB_SKY_NAME), new InventoryCallback());


        UserInfo userInfo = UserModel.SelectUser();
        if (userInfo == null) {
            new getProfile().execute();
        }
        else {
            ViewProfile(userInfo);
        }
    }

//    private void refreshIdToken() {
//        // Attempt to silently refresh the GoogleSignInAccount. If the GoogleSignInAccount
//        // already has a valid token this method may complete immediately.
//        //
//        // If the user has not previously signed in on this device or the sign-in has expired,
//        // this asynchronous branch will attempt to sign in the user silently and get a valid
//        // ID token. Cross-device single sign on will occur in this branch.
//        mGoogleSignInClient.silentSignIn()
//                .addOnCompleteListener(this, new OnCompleteListener<GoogleSignInAccount>() {
//                    @Override
//                    public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
//                        handleSignInResult(task);
//                    }
//                });
//    }

    @Override
    protected void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
    }

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
        ///menu.findItem(R.id.version_name).setTitle(versionName);
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
                if (HttpConnectClass.isOnline(ProfileActivity.this)) {
                    LogOut();
                }
                else {
                    UtilitModel.DialogError(ProfileActivity.this, R.string.no_internet);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void LogOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
            new ResultCallback<Status>() {
                @Override
                public void onResult(Status status) {
                    UtilitModel.truncate(BookModel.class);
                    UtilitModel.truncate(IstoriaInfo.class);
                    UtilitModel.truncate(UserModel.class);
                    UILApplication.AppKey = null;
                    Cache.clear();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mCheckout.onActivityResult(requestCode, resultCode, data);

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

    class getSubscription extends AsyncTask<String, String, String> {
        Dialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            httpConect.getSubscription(strings[0], strings[1], strings[2], strings[3]);
            return "";
        }

        protected void onPostExecute(String result) {

            super.onPostExecute(result);
        }
    }

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
