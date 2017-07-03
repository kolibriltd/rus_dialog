package com.wearesputnik.istoria.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.activeandroid.query.Select;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.adapters.ItemBookAdapter;
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
    RelativeLayout relButton, relListViewClick, relButtonTimer, relTapViewInfo;
    int id_book;
    int countText, tapCount = 0;
    boolean tapBoolStop = false;
    BookModel bookModelOne;
    boolean tapListView;
    IstoriaInfo istoriaInfo;

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
        listTextBook = (ListView) findViewById(R.id.listTextBook);
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
            if (tapCount > 4) {
                TextInfo empty = new TextInfo();
                empty.flags = true;
                empty.emptyFlag = true;
                itemBookAdapter.add(empty);
            }
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
            if (tapCount >= 4) {
                if (itemBookAdapter.getItem(itemBookAdapter.getCount() - 1).emptyFlag) {
                    itemBookAdapter.remove(itemBookAdapter.getItem(itemBookAdapter.getCount() - 1));
                }
            }

            itemBookAdapter.add(textInfoListSort.get(tapCount));
            if (tapCount > 4) {
                TextInfo empty = new TextInfo();
                empty.emptyFlag = true;
                itemBookAdapter.add(empty);
            }
            itemBookAdapter.notifyDataSetChanged();
            tapCount++;
            bookModelOne.IsViewTapCount = tapCount;
            bookModelOne.save();
            scrollMyListViewToBottom();
        }
    }

    private void scrollMyListViewToBottom() {
        listTextBook.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                listTextBook.smoothScrollToPosition(itemBookAdapter.getCount() - 1);
            }
        });
    }
}
