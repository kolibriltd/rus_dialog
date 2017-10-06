package com.wearesputnik.istoria.activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.UILApplication;
import com.wearesputnik.istoria.adapters.ItemBookAdapter;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.ResultInfo;
import com.wearesputnik.istoria.helpers.TextInfo;
import com.wearesputnik.istoria.helpers.UserInfo;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;
import com.wearesputnik.istoria.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ItemBookReadActivity extends BaseActivity implements
        GoogleApiClient.OnConnectionFailedListener{
    ListView listTextBook;
    ItemBookAdapter itemBookAdapter;
    RelativeLayout relButton, relListViewClick, relButtonTimer, relTapViewInfo, relRaiting, relOtvet, relInApp, relWipMessaging, relCallPeople, relCallPeopleOff;
    int id_book;
    int countText, tapCount = 0;
    boolean tapBoolStop = false;
    BookModel bookModelOne;
    boolean tapListView;
    boolean isGuest = false;
    IstoriaInfo istoriaInfo;
    RatingBar ratingBar;
    TextView btnNext, txtOtvet1, txtOtvet2, txtBntInApp, txtMinView, txtSecView, txtNameB, txtWip, txtCall, txtCallOff;
    Button btnInApp;
    TimerStart timerStart;
    Integer TypeId;
    Integer TimerMessageWip;
    MessageWip messageWip;
    TextInfo textInfoWip;
    CallPeople callPeople;
    Integer TimerCall;
    Vibrator vibrator;
    boolean isVibrate = false;

    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    /*private class PurchaseListener extends EmptyRequestListener<Purchase> {
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
        setContentView(R.layout.activity_item_book_read);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id_book = bundle.getInt("id_book");
        }

        relButton = (RelativeLayout) findViewById(R.id.relButton);
        relListViewClick = (RelativeLayout) findViewById(R.id.relListViewClick);
        relTapViewInfo = (RelativeLayout) findViewById(R.id.relTapViewInfo);
        relButtonTimer = (RelativeLayout) findViewById(R.id.relButtonTimer);
        relRaiting = (RelativeLayout) findViewById(R.id.relRaiting);
        listTextBook = (ListView) findViewById(R.id.listTextBook);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnNext = (TextView) findViewById(R.id.btnNext);

        relOtvet = (RelativeLayout) findViewById(R.id.relOtvet);
        txtOtvet1 = (TextView) findViewById(R.id.txtOtvet1);
        txtOtvet2 = (TextView) findViewById(R.id.txtOtvet2);

        txtBntInApp = (TextView) findViewById(R.id.txtBntInApp);
        txtMinView = (TextView) findViewById(R.id.txtMinView);
        txtSecView = (TextView) findViewById(R.id.txtSecView);

        relInApp = (RelativeLayout) findViewById(R.id.relInApp);
        btnInApp = (Button) findViewById(R.id.btnInApp);
        relInApp.setVisibility(View.GONE);

        relWipMessaging = (RelativeLayout) findViewById(R.id.relWipMessaging);
        txtNameB = (TextView) findViewById(R.id.txtNameB);
        txtWip = (TextView) findViewById(R.id.txtWip);
        relWipMessaging.setVisibility(View.GONE);

        relCallPeople = (RelativeLayout) findViewById(R.id.relCallPeople);
        txtCall = (TextView) findViewById(R.id.txtCall);
        relCallPeople.setVisibility(View.GONE);

        relCallPeopleOff = (RelativeLayout) findViewById(R.id.relCallPeopleOff);
        txtCallOff = (TextView) findViewById(R.id.txtCallOff);
        relCallPeopleOff.setVisibility(View.GONE);

        txtBntInApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relButtonTimer.setVisibility(View.GONE);
                relInApp.setVisibility(View.VISIBLE);
            }
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

       /* btnInApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(BillingRequests requests) {
                        requests.purchase(ProductTypes.IN_APP, "weekly", null, mCheckout.getPurchaseFlow());
                    }
                });
            }
        });*/

        listTextBook.setDividerHeight(0);
        relOtvet.setVisibility(View.GONE);

        istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
        if (istoriaInfo != null) {
            if (istoriaInfo.IsTapViewScreen) {
                relTapViewInfo.setVisibility(View.GONE);
                tapListView = true;
            }
            else {
                tapListView = false;
                relTapViewInfo.setVisibility(View.VISIBLE);
            }
            if (istoriaInfo.AppKey == null) {
                getSupportActionBar().hide();
                relTapViewInfo.setVisibility(View.GONE);
                isGuest = true;
                signIn();
            }
            if (istoriaInfo.IsPush) {
                isVibrate = true;
            }
        }
        else {
//            relTapViewInfo.setVisibility(View.VISIBLE);
//            tapListView = false;
//            getSupportActionBar().hide();
//            isGuest = true;
        }

        if (isGuest) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)listTextBook.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            listTextBook.setLayoutParams(params);
        }

        bookModelOne = new Select().from(BookModel.class).where("IdDbServer = ?", id_book).executeSingle();
        TypeId = bookModelOne.TypeId;

        getSupportActionBar().setTitle(bookModelOne.Name);

        List<TextInfo> textInfoList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(bookModelOne.TextInfoList);
            for (int i = 0; i < jsonArray.length(); i++) {
                textInfoList.add(TextInfo.parseJson(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!textInfoList.isEmpty()) {
            InitListView(textInfoList);
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isGuest) {
                    new setRatingBook().execute(ratingBar.getRating() + "");
                    relRaiting.setVisibility(View.GONE);
                }
                else {
                    signIn();
                }
            }
        });

        /*mCheckout.start();

        mCheckout.createPurchaseFlow(new PurchaseListener());

        mInventory = mCheckout.makeInventory();
        mInventory.load(Inventory.Request.create()
                .loadAllPurchases()
                .loadSkus(ProductTypes.IN_APP, "weekly"), new InventoryCallback());*/
    }

    /*@Override
    protected void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
    }*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
        /*else {
            mCheckout.onActivityResult(requestCode, resultCode, data);
        }*/
    }

    // [START handleSignInResult]
    private void handleSignInResult(GoogleSignInResult result) {
        //Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess() && isGuest) {

            GoogleSignInAccount acct = result.getSignInAccount();
            //String code = acct.zzmx();

            UserInfo userInfo = new UserInfo();
            userInfo.email = acct.getEmail();
            userInfo.firs_name = acct.getDisplayName();
            userInfo.app_key = acct.getServerAuthCode();
            if (acct.getPhotoUrl() != null) {
                userInfo.photo = acct.getPhotoUrl().toString();
            }
            else {
                userInfo.photo = "";
            }

            UserModel.AddEditUser(userInfo);

            new getLoginTask().execute(userInfo);
        }
    }
    // [END handleSignInResult]

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void InitListView(List<TextInfo> textInfoList) {
        final List<TextInfo> textInfoListSort = new ArrayList<>();

        String nameA = null, nameB = null;
        for (TextInfo item : textInfoList) {
            if (item.nameA != null) {
                nameA = item.nameA;
            }
            if (item.nameB != null) {
                nameB = item.nameB;
                txtNameB.setText(item.nameB + " печатает");
                txtCallOff.setText(item.nameB + " сбросил вызов");
            }
            if (item.nameA == null && item.nameB == null) {
                textInfoListSort.add(item);
            }
        }

        itemBookAdapter = new ItemBookAdapter(ItemBookReadActivity.this, nameA, nameB);
        listTextBook.setAdapter(itemBookAdapter);

        TapItemListView(textInfoListSort, false);
    }


    public void TapItemListView(final List<TextInfo> textInfoListSort, boolean branch) {

        if (TypeId == 1) {
            if (bookModelOne.IsViewTapCount != null) {
                tapCount = bookModelOne.IsViewTapCount;
                if (tapCount >= 4 || branch) {
                    relListViewClick.setVisibility(View.GONE);
                }
                for (int i = 0; i < tapCount; i++) {
                    textInfoListSort.get(i).flags = true;
                    itemBookAdapter.add(textInfoListSort.get(i));
                }
            /*if (tapCount > 4) {
                TextInfo empty = new TextInfo();
                empty.flags = false;
                empty.emptyFlag = true;
                itemBookAdapter.add(empty);
            }*/
                itemBookAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
                if (bookModelOne.TapStooBool) {
                    tapBoolStop = true;
                }
            }
        }


        relListViewClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickRelList(textInfoListSort);
            }
        });

        listTextBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClickRelList(textInfoListSort);
            }
        });

        relButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickRelList(textInfoListSort);
            }
        });
    }

    public void ClickRelList(final List<TextInfo> textInfoListSort) {
        if (!tapBoolStop) {
            boolean notifAdapter = false;
            countText = textInfoListSort.size();
            if (!tapListView) {
                istoriaInfo.IsTapViewScreen = true;
                istoriaInfo.save();
                tapListView = true;
                relTapViewInfo.setVisibility(View.GONE);
            }
            if (tapCount == 4) {
                relListViewClick.setVisibility(View.GONE);
            }
            if (!textInfoListSort.get(tapCount).branch.isEmpty()) {
                final int tapBranch = tapCount;
                for (int i = 0; i < textInfoListSort.get(tapCount).branch.size(); i++) {
                    if (i == 0) {
                        txtOtvet1.setText(textInfoListSort.get(tapCount).branch.get(i).message);
                    }
                    if (i == 1) {
                        txtOtvet2.setText(textInfoListSort.get(tapCount).branch.get(i).message);
                    }
                }
                relOtvet.setVisibility(View.VISIBLE);
                txtOtvet1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemBookAdapter.add(textInfoListSort.get(tapBranch).branch.get(0).content.get(0));
                        itemBookAdapter.notifyDataSetChanged();
                        scrollMyListViewToBottom();
                        tapCount = 1;
                        relOtvet.setVisibility(View.GONE);
                        TapItemListView(textInfoListSort.get(tapBranch).branch.get(0).content, true);
                    }
                });
                txtOtvet2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tapCount = 1;
                        itemBookAdapter.add(textInfoListSort.get(tapBranch).branch.get(1).content.get(0));
                        itemBookAdapter.notifyDataSetChanged();
                        scrollMyListViewToBottom();
                        relOtvet.setVisibility(View.GONE);
                        TapItemListView(textInfoListSort.get(tapBranch).branch.get(1).content, true);
                    }
                });
            } else {
                if (tapCount < countText) {

            /*if (tapCount >= 4) {
                if (itemBookAdapter.getItem(itemBookAdapter.getCount() - 1).emptyFlag) {
                    itemBookAdapter.remove(itemBookAdapter.getItem(itemBookAdapter.getCount() - 1));
                }
            }*/
                    if (textInfoListSort.get(tapCount).metka != null) {
                        if (textInfoListSort.get(tapCount).metka.trim().equals("STOP")) {
                            relButtonTimer.setVisibility(View.VISIBLE);
                            tapCount++;
                            tapBoolStop = true;
                            timerStart = new TimerStart();
                            timerStart.start();
                        }
                    }
                    if (textInfoListSort.get(tapCount).callPeopleB != null) {
                        relCallPeople.setVisibility(View.VISIBLE);
                        if(!isVibrate) {
                            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(2000);
                        }
                        TimerCall = 18;
                        tapBoolStop = true;
                        textInfoWip = textInfoListSort.get(tapCount);
                        callPeople = new CallPeople();
                        callPeople.start();
                        notifAdapter = true;
                    }
                    if (textInfoListSort.get(tapCount).metka != null) {
                        if (textInfoListSort.get(tapCount).metka.trim().equals("END")) {
                            tapBoolStop = true;
                            itemBookAdapter.add(textInfoListSort.get(tapCount));
                            itemBookAdapter.notifyDataSetChanged();
                            relRaiting.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        if (textInfoListSort.get(tapCount).peopleB != null) {
                            relWipMessaging.setVisibility(View.VISIBLE);
                            TimerMessageWip = (textInfoListSort.get(tapCount).peopleB.length() / 2)/2;
                            tapBoolStop = true;
                            messageWip = new MessageWip();
                            messageWip.start();
                            textInfoWip = textInfoListSort.get(tapCount);
                        }
                        else {
                            if (!notifAdapter) {
                                itemBookAdapter.add(textInfoListSort.get(tapCount));
                                itemBookAdapter.notifyDataSetChanged();
                                tapCount++;
                            }
                        }

                    }
                    if (TypeId == 1) {
                        bookModelOne.IsViewTapCount = tapCount;
                        bookModelOne.save();
                    }
            /*if (tapCount > 4) {
                TextInfo empty = new TextInfo();
                empty.emptyFlag = true;
                empty.flags = false;
                itemBookAdapter.add(empty);
            }*/


                    scrollMyListViewToBottom();
                }
            }
        }
    }

    class setRatingBook extends AsyncTask<String, String, Books> {
        Dialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(ItemBookReadActivity.this, R.style.TransparentProgressDialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_progress_dialog);
            dialog.show();
        }
        @Override
        protected Books doInBackground(String... strings) {
            Books result = httpConect.setRatingBook(id_book, strings[0]);
            return result;
        }

        protected void onPostExecute(Books result) {
            if (result != null) {
                bookModelOne.IsViewCount = result.isViewCount;
                bookModelOne.Raiting = result.raiting;
                bookModelOne.save();
                if (isGuest) {
                    Intent intent = new Intent(ItemBookReadActivity.this, ListBookActivity.class);
                    startActivity(intent);
                    finish();
                }
                else {
                    finish();
                }
            }
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    private class CallPeople extends Thread {
        public boolean stopped = false;

        public void run() {
            try {
                while (!stopped) {
                    // Активность списка
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (TimerCall == 0) {
                                        relCallPeopleOff.setVisibility(View.GONE);
                                        callPeople.stopped = true;
                                        tapBoolStop = false;
                                        if(!isVibrate) {
                                            vibrator.cancel();
                                        }
                                        itemBookAdapter.add(textInfoWip);
                                        itemBookAdapter.notifyDataSetChanged();
                                        tapCount++;
                                        scrollMyListViewToBottom();
                                    }
                                    else {
                                        if (TimerCall == 15) {
                                            relCallPeople.setVisibility(View.GONE);
                                            relCallPeopleOff.setVisibility(View.VISIBLE);
                                        }
                                        else {
                                            int wipCount = txtCall.getText().length();
                                            if (wipCount == 5) {
                                                txtCall.setText(".");
                                            } else {
                                                String wipPoint = txtCall.getText().toString();
                                                txtCall.setText(wipPoint + ".");
                                            }
                                        }
                                        TimerCall --;
                                    }
                                }
                            }
                    );
                    try {
                        Thread.sleep(200);
                    }
                    catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private class MessageWip extends Thread {
        public boolean stopped = false;

        public void run() {
            try {
                while (!stopped) {
                    // Активность списка
                    runOnUiThread(
                            new Runnable() {
                                @Override
                                public void run() {
                                    if (TimerMessageWip == 0) {
                                        messageWip.stopped = true;
                                        tapBoolStop = false;
                                        relWipMessaging.setVisibility(View.GONE);
                                        itemBookAdapter.add(textInfoWip);
                                        itemBookAdapter.notifyDataSetChanged();
                                        tapCount++;
                                        scrollMyListViewToBottom();
                                    }
                                    else {
                                        int wipCount = txtWip.getText().length();
                                        if (wipCount == 5) {
                                            txtWip.setText(".");
                                        }
                                        else {
                                            String wipPoint = txtWip.getText().toString();
                                            txtWip.setText(wipPoint + ".");
                                        }
                                        TimerMessageWip --;
                                    }
                                }
                            }
                    );
                    try {
                        Thread.sleep(200);
                    }
                    catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private class TimerStart extends Thread {
        public boolean stopped = false;

        public void run() {
            try {
                while (!stopped) {
                    // Активность списка
                    runOnUiThread(
                        new Runnable() {
                            @Override
                            public void run() {
                                int secInt = Integer.parseInt(txtSecView.getText().toString());
                                if (secInt == 0) {
                                    timerStart.stopped = true;
                                    tapBoolStop = false;
                                    relButtonTimer.setVisibility(View.GONE);
                                }
                                else {
                                    secInt = secInt-1;
                                    if (secInt < 10) {
                                        txtSecView.setText("0" + secInt);
                                    }
                                    else {
                                        txtSecView.setText(secInt + "");
                                    }
                                }
                            }
                        }
                    );
                    try {
                        Thread.sleep(1000);
                    }
                    catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    private void scrollMyListViewToBottom() {

        listTextBook.postOnAnimation(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                //listTextBook.smoothScrollToPosition(itemBookAdapter.getCount() - 1);
                int h1 = listTextBook.getHeight();
                int h2 = listTextBook.getHeight();

                listTextBook.smoothScrollToPositionFromTop(itemBookAdapter.getCount() - 1, h1/2 - h2/2, 1500);
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
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            //Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    class getLoginTask extends AsyncTask<UserInfo, UserInfo, ResultInfo> {
        Dialog dialog;


        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(ItemBookReadActivity.this, R.style.TransparentProgressDialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_progress_dialog);
            dialog.show();
        }

        @Override
        protected ResultInfo doInBackground(UserInfo... userInfo) {
            ResultInfo result = httpConect.setSingUpGmail(userInfo[0]);
            return result;
        }

        protected void onPostExecute(ResultInfo result) {
            dialog.dismiss();
            if (result != null) {
                if (result.status == 0) {
                    UILApplication.AppKey = result.userInfoResult.app_key;
                    IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
                    if (istoriaInfo != null) {
                        istoriaInfo.AppKey = result.userInfoResult.app_key;
                        istoriaInfo.IsPush = false;
                        istoriaInfo.save();
                    }
                    else {
                        IstoriaInfo newIstoriaInfo = new IstoriaInfo();
                        newIstoriaInfo.AppKey = result.userInfoResult.app_key;
                        newIstoriaInfo.IsViewTwoScreen = false;
                        newIstoriaInfo.IsTapViewScreen = false;
                        newIstoriaInfo.IsPush = false;
                        newIstoriaInfo.save();
                    }

                    Intent intent = new Intent(ItemBookReadActivity.this, ListBookActivity.class);
                    startActivity(intent);
                    finish();

                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ItemBookReadActivity.this);
                    builder.setTitle("Ошибка")
                            .setMessage(result.error)
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
            super.onPostExecute(result);
        }
    }

    // [START signIn]
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    // [END signIn]

    // [START revokeAccess]
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END revokeAccess]



    @Override
    protected void onStop() {
        super.onStop();
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void updateUI(boolean signedIn) {
        if (signedIn) {
           /* findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.VISIBLE);*/
        } else {
            /*mStatusTextView.setText(R.string.signed_out);

            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_and_disconnect).setVisibility(View.GONE);*/
        }
    }
}
