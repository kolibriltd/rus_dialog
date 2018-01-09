package com.wearesputnik.istoria.activity;

import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
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

import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.adapters.ItemBookAdapter;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.BranchJsonSave;
import com.wearesputnik.istoria.helpers.TextInfo;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ItemBookReadActivity extends BaseActivity{
    ListView listTextBook;
    ItemBookAdapter itemBookAdapter;
    RelativeLayout relButton, relListViewClick, relButtonTimer, relTapViewInfo, relRaiting, relOtvet, relInApp, relWipMessaging, relCallPeople, relCallPeopleOff;
    int id_book;
    int countText, tapCount = 0, branchNumLoad = -1;
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
    Integer TypeCall;
    boolean isVibrate = false;
    List<TextInfo> textInfoListSortGlobal;



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

        textInfoListSortGlobal = new ArrayList<>();

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
            } else {
                tapListView = false;
                relTapViewInfo.setVisibility(View.VISIBLE);
            }
            if (istoriaInfo.IsPush) {
                isVibrate = true;
            }
        }

        if (isGuest) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) listTextBook.getLayoutParams();
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
                new setRatingBook().execute(ratingBar.getRating() + "");
                relRaiting.setVisibility(View.GONE);
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
                txtCallOff.setText(item.nameB + " сбросил(-а) вызов");
            }
            if (item.nameA == null && item.nameB == null) {
                textInfoListSort.add(item);
            }
        }

        itemBookAdapter = new ItemBookAdapter(ItemBookReadActivity.this, nameA, nameB);
        listTextBook.setAdapter(itemBookAdapter);

        TapItemListView(textInfoListSort, false);
    }

    public void filingGlobalTxtInfo(List<TextInfo> textInfoListSort) {
        for (TextInfo item : textInfoListSort) {
            textInfoListSortGlobal.add(item);
        }
    }

    public void TapItemListView(List<TextInfo> textInfoListSort, boolean branch) {
        if (!textInfoListSortGlobal.isEmpty()) {
            textInfoListSortGlobal.clear();
            filingGlobalTxtInfo(textInfoListSort);
        }
        else {
            filingGlobalTxtInfo(textInfoListSort);
        }
        if (TypeId == 1) {
            if (bookModelOne.IsViewTapCount != null) {
                tapCount = bookModelOne.IsViewTapCount;
                if (tapCount >= 4 || branch) {
                    relListViewClick.setVisibility(View.GONE);
                }
                for (int i = 0; i < tapCount; i++) {
                    textInfoListSort.get(i).flags = true;
                    itemBookAdapter.add(textInfoListSortGlobal.get(i));
                }
                itemBookAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
                if (bookModelOne.TapStooBool) {
                    tapBoolStop = true;
                }
            }
        }
        else if (TypeId == 2 && !branch) {
            if (bookModelOne.IsViewTapCount != null) {
                tapCount = bookModelOne.IsViewTapCount;
                if (tapCount >= 4) {
                    relListViewClick.setVisibility(View.GONE);
                }
                if (bookModelOne.BranchJsonSave != null) {
                    List<BranchJsonSave> jsonListLoad = new ArrayList<>();
                    try {
                        JSONArray jsonArray = new JSONArray(bookModelOne.BranchJsonSave);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            jsonListLoad.add(BranchJsonSave.parseJson(jsonArray.getJSONObject(i)));
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    int countListTxtInfo;
                    if (branchNumLoad == (jsonListLoad.size() - 1)){
                        countListTxtInfo = tapCount;
                        if (countListTxtInfo == textInfoListSortGlobal.size()) {
                            countListTxtInfo --;
                        }
                    }
                    else {
                        countListTxtInfo = textInfoListSortGlobal.size();
                    }
                    for (int i = 0; i < countListTxtInfo; i++) {
                        if (!textInfoListSort.get(i).branch.isEmpty()) {
                            branchNumLoad ++;
                            TapItemListView(textInfoListSortGlobal.get(i).branch.get(jsonListLoad.get(branchNumLoad).numBranch).content, false);
                        }
                        else {
                            textInfoListSort.get(i).flags = true;
                            itemBookAdapter.add(textInfoListSortGlobal.get(i));
                        }
                    }
                }
                else {
                    for (int i = 0; i < tapCount; i++) {
                        textInfoListSort.get(i).flags = true;
                        itemBookAdapter.add(textInfoListSortGlobal.get(i));
                    }
                }

                itemBookAdapter.notifyDataSetChanged();
                scrollMyListViewToBottom();
            }
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

    public void ClickRelList() {
        if (!tapBoolStop) {
            boolean notifAdapter = false;
            countText = textInfoListSortGlobal.size();
            if (!tapListView) {
                istoriaInfo.IsTapViewScreen = true;
                istoriaInfo.save();
                tapListView = true;
                relTapViewInfo.setVisibility(View.GONE);
            }
            if (tapCount == 4) {
                relListViewClick.setVisibility(View.GONE);
            }
            if (!textInfoListSortGlobal.get(tapCount).branch.isEmpty()) {
                final int tapBranch = tapCount;
                for (int i = 0; i < textInfoListSortGlobal.get(tapCount).branch.size(); i++) {
                    if (i == 0) {
                        txtOtvet1.setText(textInfoListSortGlobal.get(tapCount).branch.get(i).message);
                    }
                    if (i == 1) {
                        txtOtvet2.setText(textInfoListSortGlobal.get(tapCount).branch.get(i).message);
                    }
                }
                relOtvet.setVisibility(View.VISIBLE);
                txtOtvet1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        itemBookAdapter.add(textInfoListSortGlobal.get(tapBranch).branch.get(0).content.get(0));
                        itemBookAdapter.notifyDataSetChanged();
                        scrollMyListViewToBottom();
                        tapCount = 1;
                        relOtvet.setVisibility(View.GONE);
                        bookModelOne.BranchJsonSave = BranchJsonSave.jsonGenerete(bookModelOne.BranchJsonSave, 0);
                        bookModelOne.IsViewTapCount = tapCount;
                        bookModelOne.save();
                        TapItemListView(textInfoListSortGlobal.get(tapBranch).branch.get(0).content, true);
                    }
                });
                txtOtvet2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tapCount = 1;
                        itemBookAdapter.add(textInfoListSortGlobal.get(tapBranch).branch.get(1).content.get(0));
                        itemBookAdapter.notifyDataSetChanged();
                        scrollMyListViewToBottom();
                        relOtvet.setVisibility(View.GONE);
                        bookModelOne.BranchJsonSave = BranchJsonSave.jsonGenerete(bookModelOne.BranchJsonSave, 1);
                        bookModelOne.IsViewTapCount = tapCount;
                        bookModelOne.save();
                        TapItemListView(textInfoListSortGlobal.get(tapBranch).branch.get(1).content, true);
                    }
                });
            } else {
                if (tapCount < countText) {
                    if (textInfoListSortGlobal.get(tapCount).metka != null) {
                        if (textInfoListSortGlobal.get(tapCount).metka.trim().equals("STOP")) {
                            relButtonTimer.setVisibility(View.VISIBLE);
                            tapCount++;
                            tapBoolStop = true;
                            timerStart = new TimerStart();
                            timerStart.start();
                        }
                    }
                    if (textInfoListSortGlobal.get(tapCount).callPeopleB != null) {
                        relCallPeople.setVisibility(View.VISIBLE);
                        if(!isVibrate) {
                            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(2000);
                        }
                        TypeCall = 1;
                        TimerCall = 18;
                        tapBoolStop = true;
                        textInfoWip = textInfoListSortGlobal.get(tapCount);
                        callPeople = new CallPeople();
                        callPeople.start();
                        notifAdapter = true;
                    }
                    if (textInfoListSortGlobal.get(tapCount).missCallPeopleB != null) {
                        relCallPeople.setVisibility(View.VISIBLE);
                        if(!isVibrate) {
                            vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(2000);
                        }
                        TypeCall = 2;
                        TimerCall = 18;
                        tapBoolStop = true;
                        textInfoWip = textInfoListSortGlobal.get(tapCount);
                        callPeople = new CallPeople();
                        callPeople.start();
                        notifAdapter = true;
                    }
                    if (textInfoListSortGlobal.get(tapCount).metka != null) {
                        if (textInfoListSortGlobal.get(tapCount).metka.trim().equals("END")) {
                            tapBoolStop = true;
                            bookModelOne.BranchJsonEnd = "END";
                            bookModelOne.save();
                            itemBookAdapter.add(textInfoListSortGlobal.get(tapCount));
                            itemBookAdapter.notifyDataSetChanged();
                            if (!bookModelOne.isRaiting) {
                                relRaiting.setVisibility(View.VISIBLE);
                            }
                        }
                    }
                    else {
                        if (textInfoListSortGlobal.get(tapCount).peopleB != null) {
                            relWipMessaging.setVisibility(View.VISIBLE);
                            TimerMessageWip = (textInfoListSortGlobal.get(tapCount).peopleB.length() / 2)/2;
                            tapBoolStop = true;
                            messageWip = new MessageWip();
                            messageWip.start();
                            textInfoWip = textInfoListSortGlobal.get(tapCount);
                        }
                        else {
                            if (!notifAdapter) {
                                itemBookAdapter.add(textInfoListSortGlobal.get(tapCount));
                                itemBookAdapter.notifyDataSetChanged();
                                tapCount++;
                            }
                        }

                    }
                    bookModelOne.IsViewTapCount = tapCount;
                    bookModelOne.save();

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
                bookModelOne.isRaiting = true;
                bookModelOne.save();
//                Intent intent = new Intent(ItemBookReadActivity.this, ListBookActivity.class);
//                startActivity(intent);
               finish();
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
}
