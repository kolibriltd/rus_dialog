package com.wearesputnik.istoria.activity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.adapters.MapProgressAdapter;
import com.wearesputnik.istoria.helpers.Branch;
import com.wearesputnik.istoria.helpers.BranchJsonSave;
import com.wearesputnik.istoria.helpers.MapProgressInfo;
import com.wearesputnik.istoria.helpers.ProgressInfo;
import com.wearesputnik.istoria.helpers.TextInfo;
import com.wearesputnik.istoria.models.BookModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MapProgressActivity extends AppCompatActivity {
    int id_book;
    BookModel bookModelOne;
    MapProgressAdapter mapProgressAdapter;
    Button btnRead;
    TextView txtProgresProcent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_progress);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setElevation(0);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.colorBlack));
        colorDrawable.setAlpha(0);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        btnRead = (Button) findViewById(R.id.btnRead);
        ListView listMapProgress = (ListView) findViewById(R.id.listMapProgress);
        listMapProgress.setDividerHeight(0);
        txtProgresProcent = (TextView) findViewById(R.id.txtProgresProcent);
        txtProgresProcent.setText("ПРОЙДЕНО 0%");

        mapProgressAdapter = new MapProgressAdapter(MapProgressActivity.this);
        listMapProgress.setAdapter(mapProgressAdapter);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id_book = bundle.getInt("id_book");
        }

        bookModelOne = new Select().from(BookModel.class).where("IdDbServer = ?", id_book).executeSingle();

        getSupportActionBar().setTitle(bookModelOne.Name);

        ViewListMapProgress();

        btnRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapProgressActivity.this, ItemBookReadActivity.class);
                intent.putExtra("id_book", id_book);
                finish();
                startActivity(intent);
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

    public void ViewListMapProgress() {
        if (bookModelOne.BranchJson != null || !bookModelOne.BranchJson.trim().equals("")) {
            List<ProgressInfo> jsonSaveList = new ArrayList<>();

            try {
                JSONArray jsonArray = new JSONArray(bookModelOne.BranchJson);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonSaveList.add(ProgressInfo.parseJson(jsonArray.getJSONObject(i)));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!jsonSaveList.isEmpty()) {
                ViewProgressProcent(jsonSaveList);
                for (ProgressInfo item : jsonSaveList) {
                    mapProgressAdapter.add(item);
                }
            }
        }
        mapProgressAdapter.notifyDataSetChanged();
    }

    public void ViewProgressProcent(List<ProgressInfo> jsonSaveList) {
        Integer countAllMap = 0;
        Integer countActiveMap = 0;
        for (ProgressInfo item : jsonSaveList) {
            countAllMap = countAllMap + item.progressInfoList.size();
            for (MapProgressInfo itemMap : item.progressInfoList) {
                if (itemMap.isActiveImage) {
                    countActiveMap++;
                }
            }
        }

        int procentMap = countActiveMap * 100 / countAllMap;
        txtProgresProcent.setText("ПРОЙДЕНО " + procentMap + "%");
    }
}
