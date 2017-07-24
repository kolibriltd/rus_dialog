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
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.adapters.ItemBookAdapter;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.TextInfo;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class ItemBookReadActivity extends BaseActivity {
    ListView listTextBook;
    ItemBookAdapter itemBookAdapter;
    RelativeLayout relButton, relListViewClick, relButtonTimer, relTapViewInfo, relRaiting;
    int id_book;
    int countText, tapCount = 0;
    boolean tapBoolStop = false;
    BookModel bookModelOne;
    boolean tapListView;
    IstoriaInfo istoriaInfo;
    RatingBar ratingBar;
    TextView btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_book_read);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //getSupportActionBar().setHomeAsUpIndicator(R.mipmap.arrow);

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
        listTextBook.setDividerHeight(0);

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
            TapItemListView(textInfoList);
        }

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new setRatingBook().execute(ratingBar.getRating() + "");
                relRaiting.setVisibility(View.GONE);

            }
        });
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


    public void TapItemListView(List<TextInfo> textInfoList) {

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


        countText = textInfoListSort.size();
        itemBookAdapter = new ItemBookAdapter(ItemBookReadActivity.this, nameA, nameB);
        listTextBook.setAdapter(itemBookAdapter);

        if (bookModelOne.IsViewTapCount != null) {
            tapCount = bookModelOne.IsViewTapCount;
            if (tapCount >= 4) {
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

    public void ClickRelList(List<TextInfo> textInfoListSort) {
        if (!tapListView) {
            istoriaInfo.IsTapViewScreen = true;
            istoriaInfo.save();
            tapListView = true;
            relTapViewInfo.setVisibility(View.GONE);
        }
        if (tapCount == 4) {
            relListViewClick.setVisibility(View.GONE);
        }
        if (tapCount < countText) {

            /*if (tapCount >= 4) {
                if (itemBookAdapter.getItem(itemBookAdapter.getCount() - 1).emptyFlag) {
                    itemBookAdapter.remove(itemBookAdapter.getItem(itemBookAdapter.getCount() - 1));
                }
            }*/

            itemBookAdapter.add(textInfoListSort.get(tapCount));
            itemBookAdapter.notifyDataSetChanged();
            if (textInfoListSort.get(tapCount).metka != null) {
                if (textInfoListSort.get(tapCount).metka.trim().equals("END")) {
                    relRaiting.setVisibility(View.VISIBLE);
                }
            }
            else {
                tapCount++;
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
