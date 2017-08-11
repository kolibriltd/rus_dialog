package com.wearesputnik.istoria.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.wearesputnik.istoria.UILApplication;
import com.wearesputnik.istoria.adapters.ItemBookAdapter;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.TextInfo;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.solovyev.android.checkout.ActivityCheckout;
import org.solovyev.android.checkout.BillingRequests;
import org.solovyev.android.checkout.Checkout;
import org.solovyev.android.checkout.EmptyRequestListener;
import org.solovyev.android.checkout.Inventory;
import org.solovyev.android.checkout.ProductTypes;
import org.solovyev.android.checkout.Purchase;

import java.util.ArrayList;
import java.util.List;

public class ItemBookReadActivity extends BaseActivity {
    ListView listTextBook;
    ItemBookAdapter itemBookAdapter;
    RelativeLayout relButton, relListViewClick, relButtonTimer, relTapViewInfo, relRaiting, relOtvet, relInApp;
    int id_book;
    int countText, tapCount = 0;
    boolean tapBoolStop = false;
    BookModel bookModelOne;
    boolean tapListView;
    IstoriaInfo istoriaInfo;
    RatingBar ratingBar;
    TextView btnNext, txtOtvet1, txtOtvet2, txtBntInApp, txtMinView, txtSecView;
    Button btnInApp;
    TimerStart timerStart;

    private class PurchaseListener extends EmptyRequestListener<Purchase> {
        // your code here
    }

    private class InventoryCallback implements Inventory.Callback {
        @Override
        public void onLoaded(Inventory.Products products) {
            // your code here
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

        txtBntInApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relButtonTimer.setVisibility(View.GONE);
                relInApp.setVisibility(View.VISIBLE);
            }
        });

        btnInApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCheckout.whenReady(new Checkout.EmptyListener() {
                    @Override
                    public void onReady(BillingRequests requests) {
                        requests.purchase(ProductTypes.IN_APP, "weekly", null, mCheckout.getPurchaseFlow());
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
        }
        else {
            tapListView = false;
        }

        bookModelOne = new Select().from(BookModel.class).where("IdDbServer = ?", id_book).executeSingle();

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

        mCheckout.start();

        mCheckout.createPurchaseFlow(new PurchaseListener());

        mInventory = mCheckout.makeInventory();
        mInventory.load(Inventory.Request.create()
                .loadAllPurchases()
                .loadSkus(ProductTypes.IN_APP, "weekly"), new InventoryCallback());
    }

    @Override
    protected void onDestroy() {
        mCheckout.stop();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCheckout.onActivityResult(requestCode, resultCode, data);
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

    public void InitListView(List<TextInfo> textInfoList) {
        final List<TextInfo> textInfoListSort = new ArrayList<>();

        String nameA = null, nameB = null;
        for (TextInfo item : textInfoList) {
            if (item.nameA != null) {
                nameA = item.nameA;
            }
            if (item.nameB != null) {
                nameB = item.nameB;
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


        /*if (bookModelOne.IsViewTapCount != null) {
            tapCount = bookModelOne.IsViewTapCount;
            if (tapCount >= 4 || branch) {
                relListViewClick.setVisibility(View.GONE);
            }
            for (int i = 0; i < tapCount; i++) {
                textInfoListSort.get(i).flags = true;
                itemBookAdapter.add(textInfoListSort.get(i));
            }*/
            /*if (tapCount > 4) {
                TextInfo empty = new TextInfo();
                empty.flags = false;
                empty.emptyFlag = true;
                itemBookAdapter.add(empty);
            }*/
           /* itemBookAdapter.notifyDataSetChanged();
            scrollMyListViewToBottom();
            if (bookModelOne.TapStooBool) {
                tapBoolStop = true;
            }
        }*/


        relListViewClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ClickRelList(textInfoListSort);
            }
        });

        listTextBook.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (textInfoListSort.get(i).videoPeopleB != null) {
                    Intent intent = new Intent(ItemBookReadActivity.this, YoutubeActivity.class);
                    intent.putExtra("video", textInfoListSort.get(i).videoPeopleB);
                    startActivity(intent);
                }
                else {
                    ClickRelList(textInfoListSort);
                }
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
                        tapCount = 1;
                        relOtvet.setVisibility(View.GONE);
                        TapItemListView(textInfoListSort.get(tapBranch).branch.get(0).content, true);
                    }
                });
                txtOtvet2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tapCount = 1;
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
                    if (textInfoListSort.get(tapCount).metka != null) {
                        if (textInfoListSort.get(tapCount).metka.trim().equals("END")) {
                            itemBookAdapter.add(textInfoListSort.get(tapCount));
                            itemBookAdapter.notifyDataSetChanged();
                            relRaiting.setVisibility(View.VISIBLE);
                        }
                    } else {
                        itemBookAdapter.add(textInfoListSort.get(tapCount));
                        itemBookAdapter.notifyDataSetChanged();
                        tapCount++;
                        //bookModelOne.IsViewTapCount = tapCount;
                        //bookModelOne.save();
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
                finish();
            }
            dialog.dismiss();
            super.onPostExecute(result);
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
                    } catch (Exception e) {
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
