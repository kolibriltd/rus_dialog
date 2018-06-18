package com.wearesputnik.istoria.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;

import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.UILApplication;
import com.wearesputnik.istoria.adapters.ItemBookAdapter;
import com.wearesputnik.istoria.adapters.OtvetAdapter;
import com.wearesputnik.istoria.helpers.BranchBook;
import com.wearesputnik.istoria.helpers.BranchCount;
import com.wearesputnik.istoria.helpers.Config;
import com.wearesputnik.istoria.helpers.TextInfo;
import com.wearesputnik.istoria.helpers.TimeBookJson;
import com.wearesputnik.istoria.jsonHelper.ContentBook;
import com.wearesputnik.istoria.jsonHelper.ContentTxt;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;
import com.wearesputnik.istoria.models.UserModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;

import java.util.ArrayList;
import java.util.List;

public class ItemBookReadActivity extends BaseActivity{
    ListView listTextBook,listOtvetTxt;
    private Integer COUNT_START_TIME_AND_SUB = 30, newIstori = 1;
    ItemBookAdapter itemBookAdapter;
    OtvetAdapter otvetAdapter;
    RelativeLayout relButton, relListViewClick, relButtonTimer, relTapViewInfo, relRaiting, relOtvet, relInApp, relWipMessaging, relCallPeople, relCallPeopleOff;
    LinearLayout linePointsUser;
    int id_book;
    int countText, tapCount = 0, tapCountStop = 0;
    boolean tapBoolStop = false;
    BookModel bookModelOne;
    boolean tapListView;
    boolean isGuest = false;
    IstoriaInfo istoriaInfo;
    RatingBar ratingBar;
    TextView btnNext, txtBntInApp, txtMinView, txtSecView, txtNameB, txtWip, txtCall, txtCallOff, txtOplataRead, txtCountPointUer;
    TimerStart timerStart;
    Integer TypeId;
    Integer TimerMessageWip;
    MessageWip messageWip;
    ContentTxt textInfoWip;
    CallPeople callPeople;
    Integer TimerCall;
    Vibrator vibrator;
    Integer TypeCall;
    boolean isVibrate = false;
    boolean isViewListTap = true;
    List<ContentTxt> contentTxtListGoal;
    BranchBook branchBook;

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

            if (product.isPurchased(Config.SUB_SKY_NAME)) {
                bookModelOne.TapStooBool = false;
                bookModelOne.save();
                tapBoolStop = false;
                isViewListTap = true;
                relInApp.setVisibility(View.GONE);
                UserModel.UserSubscrition(true);
            }
        }
    }

    private final ActivityCheckout mCheckout = Checkout.forActivity(this, UILApplication.getInstance().getBilling());
    private Inventory mInventory;

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

        contentTxtListGoal = new ArrayList<>();

        otvetAdapter = new OtvetAdapter(ItemBookReadActivity.this);

        linePointsUser = (LinearLayout) findViewById(R.id.linePointsUser);
        txtCountPointUer = (TextView) findViewById(R.id.txtCountPointUer);
        if (UserModel.getPointUser() != null) {
            txtCountPointUer.setText(UserModel.getPointUser() + "");
        }

        listOtvetTxt = (ListView) findViewById(R.id.listOtvetTxt);
        listOtvetTxt.setAdapter(otvetAdapter);
        listOtvetTxt.setDividerHeight(0);
        relButton = (RelativeLayout) findViewById(R.id.relButton);
        relListViewClick = (RelativeLayout) findViewById(R.id.relListViewClick);
        relTapViewInfo = (RelativeLayout) findViewById(R.id.relTapViewInfo);
        relButtonTimer = (RelativeLayout) findViewById(R.id.relButtonTimer);
        relRaiting = (RelativeLayout) findViewById(R.id.relRaiting);
        listTextBook = (ListView) findViewById(R.id.listTextBook);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnNext = (TextView) findViewById(R.id.btnNext);

        relOtvet = (RelativeLayout) findViewById(R.id.relOtvet);

        txtBntInApp = (TextView) findViewById(R.id.txtBntInApp);
        txtMinView = (TextView) findViewById(R.id.txtMinView);
        txtSecView = (TextView) findViewById(R.id.txtSecView);

        relInApp = (RelativeLayout) findViewById(R.id.relInApp);
        txtOplataRead = (TextView) findViewById(R.id.txtOplataRead);
        txtOplataRead.setPaintFlags(txtOplataRead.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
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

        txtOplataRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                     @Override
                     public void onReady(BillingRequests requests) {
                         requests.purchase(ProductTypes.SUBSCRIPTION, Config.SUB_SKY_NAME, null, mCheckout.getPurchaseFlow());
                     }
                });
            }
        });

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
            try {
                if (istoriaInfo.IsPush) {
                    isVibrate = true;
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (isGuest) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listTextBook.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            listTextBook.setLayoutParams(params);
        }

        bookModelOne = new Select().from(BookModel.class).where("IdDbServer = ?", id_book).executeSingle();
        TypeId = bookModelOne.TypeId;

        if (bookModelOne.NewIstori == 1) {
            newIstori = 2;
        }

        getSupportActionBar().setTitle(bookModelOne.Name);

        TextInfo textInfo = new TextInfo();

        try {
            JSONObject json = new JSONObject(bookModelOne.TextInfoList);
            textInfo = TextInfo.parseJson(json);
            JSONObject jsonBranch = new JSONObject(bookModelOne.BranchJsonSave);
            branchBook = BranchBook.parseJson(jsonBranch);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (textInfo != null) {
            InitListView(textInfo);
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //new setRatingBook().execute(ratingBar.getRating() + "");
                relRaiting.setVisibility(View.GONE);
            }
        });

        mCheckout.start();

        mCheckout.createPurchaseFlow(new ItemBookReadActivity.PurchaseListener());

        mInventory = mCheckout.makeInventory();
        mInventory.load(Inventory.Request.create()
                .loadAllPurchases()
                .loadSkus(ProductTypes.SUBSCRIPTION, Config.SUB_SKY_NAME), new ItemBookReadActivity.InventoryCallback());
    }

    @Override
    protected void onDestroy() {
        mCheckout.stop();
        if (!isViewListTap) {
            timerStart.stopped = true;
        }
        super.onDestroy();
    }

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

    public void InitListView(TextInfo textInfo) {
        itemBookAdapter = new ItemBookAdapter(ItemBookReadActivity.this, textInfo.namePeople);
        listTextBook.setAdapter(itemBookAdapter);
        TapItemListView(textInfo.contentBook, false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        bookModelOne.TapStooBool = false;
//        bookModelOne.save();
//        tapBoolStop = false;
//        isViewListTap = true;
//        relInApp.setVisibility(View.GONE);
//        UserModel.UserSubscrition(true);
        mCheckout.onActivityResult(requestCode, resultCode, data);
        ///mCheckout.notifyAll();

    }
    public void filingGlobalTxtInfo(List<ContentTxt> textInfoListSort) {
        for (ContentTxt item : textInfoListSort) {
            contentTxtListGoal.add(item);
        }
    }

    public void TapItemListView(ContentBook contentBook, boolean branch) {
        if (!contentTxtListGoal.isEmpty()) {
            contentTxtListGoal.clear();
            filingGlobalTxtInfo(contentBook.contentTxt);
        }
        else {
            filingGlobalTxtInfo(contentBook.contentTxt);
        }


        relListViewClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickRelList();
            }
        });

        listTextBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ClickRelList();
            }
        });

        relButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickRelList();
            }
        });
    }

    private void BranchBookSave() {

    }

    public void ClickRelList() {
        if (!tapBoolStop) {
            boolean notifAdapter = false;
            countText = contentTxtListGoal.size();
            if (tapCount > (countText - 1)) {
                metkaEND(null);
            }
            else {
                if (TypeId == 3 && !UserModel.GetSubscrition()) {
                    if (tapCountStop == tapCount) {
                        if (tapCount == COUNT_START_TIME_AND_SUB || !contentTxtListGoal.get(tapCount).branch.isEmpty()) {
                            tapBoolStop = true;
                            isViewListTap = false;
                            bookModelOne.TimerStopMin = TimeBookJson.setJsonTime(txtMinView.getText().toString(), txtSecView.getText().toString());
                            bookModelOne.TapStooBool = true;
                            bookModelOne.save();
                            relInApp.setVisibility(View.VISIBLE);

                            timerStart = new TimerStart();
                            timerStart.start();
                        }
                    }
                }
                if (isViewListTap) {
                    if (!tapListView) {
                        istoriaInfo.IsTapViewScreen = true;
                        istoriaInfo.save();
                        tapListView = true;
                        relTapViewInfo.setVisibility(View.GONE);
                    }
                    if (tapCount == 4) {
                        relListViewClick.setVisibility(View.GONE);
                    }
                    if (!contentTxtListGoal.get(tapCount).branch.isEmpty()) {
                        if (!otvetAdapter.isEmpty()) {
                            otvetAdapter.clear();
                        }
                        for (int i = 0; i < contentTxtListGoal.get(tapCount).branch.size(); i++) {
                            otvetAdapter.add(contentTxtListGoal.get(tapCount).branch.get(i));
                        }
                        otvetAdapter.notifyDataSetChanged();

                        relOtvet.setVisibility(View.VISIBLE);
                        listOtvetTxt.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                ContentBook item = otvetAdapter.getItem(position);
                                itemBookAdapter.add(item.contentTxt.get(0));
                                itemBookAdapter.notifyDataSetChanged();
                                scrollMyListViewToBottom();
                                TapItemListView(item, true);
                                tapCount = 1;
                                relOtvet.setVisibility(View.GONE);

                                bookModelOne.IsViewTapCount = tapCount;
                                bookModelOne.save();
                            }
                        });
                    }
                    else {
                        if (tapCount < countText) {
                            if (contentTxtListGoal.get(tapCount).callPeopleB != null) {
                                relCallPeople.setVisibility(View.VISIBLE);
                                if (!isVibrate) {
                                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    vibrator.vibrate(2000);
                                }
                                TypeCall = 1;
                                TimerCall = 18;
                                tapBoolStop = true;
                                textInfoWip = contentTxtListGoal.get(tapCount);
                                callPeople = new CallPeople();
                                callPeople.start();
                                notifAdapter = true;
                            }
                            if (contentTxtListGoal.get(tapCount).missCallPeopleB != null) {
                                relCallPeople.setVisibility(View.VISIBLE);
                                if (!isVibrate) {
                                    vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                                    vibrator.vibrate(2000);
                                }
                                TypeCall = 2;
                                TimerCall = 18;
                                tapBoolStop = true;
                                textInfoWip = contentTxtListGoal.get(tapCount);
                                callPeople = new CallPeople();
                                callPeople.start();
                                notifAdapter = true;
                            }
                            if (contentTxtListGoal.get(tapCount).metka != null) {
                                if (contentTxtListGoal.get(tapCount).metka.trim().equals("END")) {
                                    metkaEND(contentTxtListGoal.get(tapCount));
                                }
                            }
                            else {
                                if (contentTxtListGoal.get(tapCount).peopleB != null) {
                                    relWipMessaging.setVisibility(View.VISIBLE);
                                    TimerMessageWip = 2;
                                    tapBoolStop = true;
                                    messageWip = new MessageWip();
                                    messageWip.start();
                                    textInfoWip = contentTxtListGoal.get(tapCount);
                                } else {
                                    if (!notifAdapter) {
                                        itemBookAdapter.add(contentTxtListGoal.get(tapCount));
                                        itemBookAdapter.notifyDataSetChanged();
                                        tapCount++;
                                    }
                                }
                            }
                            bookModelOne.IsViewTapCount = tapCount;
                            bookModelOne.save();
                            tapCountStop = tapCount;

                            scrollMyListViewToBottom();
                        }
                    }
                }
            }
        }
    }

    private void metkaEND(ContentTxt endText) {
        if (endText == null) {
            endText = new ContentTxt();
            endText.metka = "END";
        }
        tapBoolStop = true;
        bookModelOne.BranchJsonEnd = "END";
        bookModelOne.save();
        itemBookAdapter.add(endText);
        itemBookAdapter.notifyDataSetChanged();
        scrollMyListViewToBottom();
        txtCountPointUer.setText((Config.FLOOR_POINT * newIstori) + Integer.parseInt(txtCountPointUer.getText().toString()) + "");
        UserModel.setPointUser(Integer.parseInt(txtCountPointUer.getText().toString()));
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
                                            if (TypeCall == 2) {
                                                relCallPeopleOff.setBackgroundResource(R.color.colorDescBacground);
                                            }
                                            else {
                                                relCallPeopleOff.setBackgroundResource(R.color.colorRed);
                                            }
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
                                int minInt = Integer.parseInt(txtMinView.getText().toString());
                                int secInt = Integer.parseInt(txtSecView.getText().toString());
                                if (minInt == 0 && secInt == 0) {
                                    timerStart.stopped = true;
                                    tapCountStop = tapCount + 1;
                                    tapBoolStop = false;
                                    isViewListTap = true;
                                    relInApp.setVisibility(View.GONE);
                                }
                                else {
                                    if (secInt == 0) {
                                        secInt = 59;
                                        minInt--;
                                        if (minInt < 10) {
                                            txtMinView.setText("0" + minInt);
                                        } else {
                                            txtMinView.setText(minInt + "");
                                        }
                                        txtSecView.setText(secInt + "");
                                    }
                                    else {
                                        secInt--;
                                        if (secInt < 10) {
                                            txtSecView.setText("0" + secInt);
                                        } else {
                                            txtSecView.setText(secInt + "");
                                        }
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
}
