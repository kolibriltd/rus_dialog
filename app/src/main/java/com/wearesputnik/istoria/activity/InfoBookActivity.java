package com.wearesputnik.istoria.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.squareup.picasso.Picasso;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.Branch;
import com.wearesputnik.istoria.helpers.BranchJsonSave;
import com.wearesputnik.istoria.helpers.HttpConnectClass;
import com.wearesputnik.istoria.helpers.MapProgressInfo;
import com.wearesputnik.istoria.helpers.ProgressInfo;
import com.wearesputnik.istoria.helpers.TextInfo;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class InfoBookActivity extends BaseActivity {
    TextView  txtDescription, txtAuthor, txtName, txtEve;
    Button btnNext, btnProgressMap;
    int id_book;
    RelativeLayout relInfoScreen1, relInfoScreen2, relGradient;
    ImageView imageViewCoverInfo, imgMoney;
    BookModel bookModelOne;
    boolean twoScrean;
    boolean btnEndRead = false;

    List<ProgressInfo> progressInfoLists;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setElevation(0);
        ColorDrawable colorDrawable = new ColorDrawable(getResources().getColor(R.color.colorPrimary));
        colorDrawable.setAlpha(0);
        getSupportActionBar().setBackgroundDrawable(colorDrawable);

        progressInfoLists  = new ArrayList<>();

        relInfoScreen1 = (RelativeLayout) findViewById(R.id.relInfoScreen1);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtAuthor = (TextView) findViewById(R.id.txtAuthor);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEve = (TextView) findViewById(R.id.txtEve);
        btnNext = (Button) findViewById(R.id.btnNext);
        btnProgressMap = (Button) findViewById(R.id.btnProgressMap);
        imageViewCoverInfo = (ImageView) findViewById(R.id.imageViewCoverInfo);
        relGradient = (RelativeLayout) findViewById(R.id.relGradient);
        imgMoney = (ImageView) findViewById(R.id.imgMoney);

        imageViewCoverInfo.setColorFilter(R.color.colorBlack);

        relInfoScreen2 = (RelativeLayout) findViewById(R.id.relInfoScreen2);
        relInfoScreen2.setVisibility(View.GONE);

        imgMoney.setVisibility(View.GONE);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id_book = bundle.getInt("id_book");
        }

        IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
        if (istoriaInfo != null) {
            if (istoriaInfo.IsViewTwoScreen) {
                twoScrean = true;
            }
            else {
                istoriaInfo.IsViewTwoScreen = true;
                istoriaInfo.save();
                twoScrean = false;
            }
        }
        else {
            IstoriaInfo newIstoriaInfo = new IstoriaInfo();
            newIstoriaInfo.IsViewTwoScreen = true;
            newIstoriaInfo.IsTapViewScreen = false;
            newIstoriaInfo.save();
            twoScrean = false;
        }

        bookModelOne = new Select().from(BookModel.class).where("IdDbServer = ?", id_book).executeSingle();
        if (!bookModelOne.IsView) {
            bookModelOne.IsView = true;
            bookModelOne.save();
            new setViewBookCount().execute();
        }

        if (bookModelOne.TextInfoList == null || bookModelOne.TextInfoList.trim().equals("null")) {
            new getOneBook().execute();
        }
        if (bookModelOne.BranchJsonEnd != null) {
            if (bookModelOne.BranchJsonEnd.trim().equals("END")) {
                btnEndRead = true;
                btnNext.setText(R.string.info_read_end);
            }
        }

        if (bookModelOne.TypeId == 1) {
            btnProgressMap.setVisibility(View.GONE);
        }
        if (bookModelOne.TypeId == 3) {
            imgMoney.setVisibility(View.VISIBLE);
        }

        List<TextInfo> textInfoList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(bookModelOne.TextInfoList);
            for (int i = 0; i < jsonArray.length(); i++) {
                textInfoList.add(TextInfo.parseJson(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!textInfoList.isEmpty() && (bookModelOne.TypeId == 2 || bookModelOne.TypeId == 3)) {
            MapProgresListSort(textInfoList);
        }

        Books itemBooks = new Books();
        itemBooks.name = bookModelOne.Name;
        itemBooks.author = bookModelOne.Author;
        itemBooks.description = bookModelOne.Description;
        itemBooks.pathCoverFile = bookModelOne.PathCoverFile;
        itemBooks.isViewCount = bookModelOne.IsViewCount;
        itemBooks.pathCoverFileStorage = bookModelOne.PathCoverFileStorage;
        itemBooks.raiting = bookModelOne.Raiting;
        ViewBookOne(itemBooks);

        btnProgressMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(InfoBookActivity.this, MapProgressActivity.class);
                intent.putExtra("id_book", id_book);
                startActivity(intent);
            }
        });

        btnNext.setOnClickListener(btnClickNext);
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

    @Override
    protected void onRestart() {
        super.onRestart();
        btnEndRead = false;
        IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
        if (istoriaInfo != null) {
            if (istoriaInfo.IsViewTwoScreen) {
                twoScrean = true;
            }
            else {
                istoriaInfo.IsViewTwoScreen = true;
                istoriaInfo.save();
                twoScrean = false;
            }
        }
        else {
            IstoriaInfo newIstoriaInfo = new IstoriaInfo();
            newIstoriaInfo.IsViewTwoScreen = true;
            newIstoriaInfo.IsTapViewScreen = false;
            newIstoriaInfo.save();
            twoScrean = false;
        }

        if (twoScrean) {
            if (relInfoScreen2.getVisibility() == View.VISIBLE) {
                relInfoScreen1.setVisibility(View.VISIBLE);
                relGradient.setVisibility(View.VISIBLE);
                imageViewCoverInfo.setVisibility(View.VISIBLE);
                relInfoScreen2.setVisibility(View.GONE);
            }
        }

        if (bookModelOne.BranchJsonEnd != null) {
            if (bookModelOne.BranchJsonEnd.trim().equals("END")) {
                btnEndRead = true;
                btnNext.setText(R.string.info_read_end);
            }
        }
        else {
            btnNext.setText(R.string.info_read);
        }

        List<TextInfo> textInfoList = new ArrayList<>();

        try {
            JSONArray jsonArray = new JSONArray(bookModelOne.TextInfoList);
            for (int i = 0; i < jsonArray.length(); i++) {
                textInfoList.add(TextInfo.parseJson(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (!textInfoList.isEmpty() && bookModelOne.TypeId == 2) {
            MapProgresListSort(textInfoList);
        }

        Books itemBooks = new Books();
        itemBooks.name = bookModelOne.Name;
        itemBooks.author = bookModelOne.Author;
        itemBooks.description = bookModelOne.Description;
        itemBooks.pathCoverFile = bookModelOne.PathCoverFile;
        itemBooks.isViewCount = bookModelOne.IsViewCount;
        itemBooks.pathCoverFileStorage = bookModelOne.PathCoverFileStorage;
        itemBooks.raiting = bookModelOne.Raiting;
        ViewBookOne(itemBooks);

        ///btnNext.setOnClickListener(btnClickNext);
    }

    private void ViewBookOne(Books result) {
        txtDescription.setText(result.description);
        txtAuthor.setText(result.author);
        txtName.setText(result.name);
        txtEve.setText(result.isViewCount + "");
        if (result.pathCoverFileStorage != null) {
            imageViewCoverInfo.setImageURI(Uri.parse(result.pathCoverFileStorage));
        } else if (result.pathCoverFile != null) {
            String url_img = HttpConnectClass.URL_IMAGE + result.pathCoverFile;
            Picasso.with(this)
                    .load(url_img)
                    .into(imageViewCoverInfo);
            imageViewCoverInfo.setColorFilter(R.color.colorBlack);
        }
    }

    class getOneBook extends AsyncTask<String, String, Books> {
        Dialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(InfoBookActivity.this, R.style.TransparentProgressDialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_progress_dialog);
            dialog.show();
        }

        @Override
        protected Books doInBackground(String... strings) {
            Books result = httpConect.getOneBook(id_book);
            return result;
        }

        protected void onPostExecute(Books result) {
            if (result != null) {
                bookModelOne.TextInfoList = result.textInfoList;
                bookModelOne.save();
                dialog.dismiss();
            }
            else {
                dialog.dismiss();
            }
            super.onPostExecute(result);
        }
    }

    class setViewBookCount extends AsyncTask<String, String, Books> {

        @Override
        protected Books doInBackground(String... strings) {
            Books result = httpConect.setViewIncCount(id_book);
            return result;
        }

        protected void onPostExecute(Books result) {
            if (result != null) {
                bookModelOne.IsViewCount = result.isViewCount;
                bookModelOne.save();
            }
            super.onPostExecute(result);
        }
    }

    private final View.OnClickListener btnClickNext = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            boolean intentBool = true;
            if (twoScrean) {
                if (bookModelOne.TextInfoList != null || !bookModelOne.TextInfoList.trim().equals("null")) {
                    if(btnEndRead) {
                        bookModelOne.BranchJsonSave = null;
                        bookModelOne.IsViewTapCount = null;
                        bookModelOne.BranchJsonEnd = null;
                        bookModelOne.save();
                    }
                    Intent intent = new Intent(InfoBookActivity.this, ItemBookReadActivity.class);
                    intent.putExtra("id_book", id_book);
                    startActivity(intent);
                }
                else {
                    intentBool = false;
                }
            }
            else {
                if (relInfoScreen2.getVisibility() == View.GONE) {

                    btnNext.setText(R.string.info_next);
                    relInfoScreen1.setVisibility(View.GONE);
                    relGradient.setVisibility(View.GONE);
                    imageViewCoverInfo.setVisibility(View.GONE);
                    relInfoScreen2.setVisibility(View.VISIBLE);
                } else {
                    if (bookModelOne.TextInfoList != null || !bookModelOne.TextInfoList.trim().equals("null")) {
                        Intent intent = new Intent(InfoBookActivity.this, ItemBookReadActivity.class);
                        intent.putExtra("id_book", id_book);
                        startActivity(intent);
                    }
                    else {
                        intentBool = false;
                    }
                }
            }
            if (!intentBool) {
                AlertDialog.Builder builder = new AlertDialog.Builder(InfoBookActivity.this);
                builder.setTitle("Ошибка")
                        .setMessage("Чат история не загрузилась")
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
    };

    private void MapProgresListSort (List<TextInfo> textInfo) {
        Dialog dialog;
        dialog = new Dialog(InfoBookActivity.this, R.style.TransparentProgressDialog);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_progress_dialog);
        dialog.show();
        boolean imageB = false;
        boolean beginImage = false;
        Integer branchFloor = 1;
        List<BranchJsonSave> jsonListLoad = new ArrayList<>();
        if (bookModelOne.IsViewTapCount !=null) {
            beginImage = true;
        }
        if (bookModelOne.BranchJsonSave != null) {
            try {
                JSONArray jsonArray = new JSONArray(bookModelOne.BranchJsonSave);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonListLoad.add(BranchJsonSave.parseJson(jsonArray.getJSONObject(i)));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (TextInfo item : textInfo) {
            if (item.imgPeopleA != null && !imageB) {
                MapProgressInfo mapProgressInfo = new MapProgressInfo();
                mapProgressInfo.ImageMap = item.imgPeopleA;
                mapProgressInfo.isActiveImage = beginImage;
                ProgressInfo progressInfoList = new ProgressInfo();
                progressInfoList.progressInfoList = new ArrayList<>();
                if (!jsonListLoad.isEmpty()) {
                    if (jsonListLoad.get(0).numBranch == 0) {
                        progressInfoList.isActiveLineOne = true;
                        progressInfoList.isActiveLineTwo = false;
                        progressInfoList.isActiveLineThree = false;
                        progressInfoList.isActiveLineFour = false;
                    }
                    if (jsonListLoad.get(0).numBranch == 1) {
                        progressInfoList.isActiveLineOne = false;
                        progressInfoList.isActiveLineTwo = true;
                        progressInfoList.isActiveLineThree = false;
                        progressInfoList.isActiveLineFour = false;
                    }
                }
                else {
                    progressInfoList.isActiveLineOne = false;
                    progressInfoList.isActiveLineTwo = false;
                    progressInfoList.isActiveLineThree = false;
                    progressInfoList.isActiveLineFour = false;
                }
                progressInfoList.progressInfoList.add(mapProgressInfo);
                progressInfoList.branchFloor = 0;
                bookModelOne.BranchJson = ProgressInfo.jsonGenarationSave(bookModelOne.BranchJson, progressInfoList);
                bookModelOne.save();
                imageB = true;
            }
            if (item.imgPeopleB != null && !imageB) {
                MapProgressInfo mapProgressInfo = new MapProgressInfo();
                mapProgressInfo.ImageMap = item.imgPeopleB;
                mapProgressInfo.isActiveImage = beginImage;

                ProgressInfo progressInfoList = new ProgressInfo();
                progressInfoList.progressInfoList = new ArrayList<>();
                if (!jsonListLoad.isEmpty()) {
                    if (jsonListLoad.get(0).numBranch == 0) {
                        progressInfoList.isActiveLineOne = true;
                        progressInfoList.isActiveLineTwo = false;
                        progressInfoList.isActiveLineThree = false;
                        progressInfoList.isActiveLineFour = false;
                    }
                    if (jsonListLoad.get(0).numBranch == 1) {
                        progressInfoList.isActiveLineOne = false;
                        progressInfoList.isActiveLineTwo = true;
                        progressInfoList.isActiveLineThree = false;
                        progressInfoList.isActiveLineFour = false;
                    }
                }
                else {
                    progressInfoList.isActiveLineOne = false;
                    progressInfoList.isActiveLineTwo = false;
                    progressInfoList.isActiveLineThree = false;
                    progressInfoList.isActiveLineFour = false;
                }
                progressInfoList.progressInfoList.add(mapProgressInfo);
                progressInfoList.branchFloor = 0;
                bookModelOne.BranchJson = ProgressInfo.jsonGenarationSave(bookModelOne.BranchJson, progressInfoList);
                bookModelOne.save();
                imageB = true;
            }

            if (!item.branch.isEmpty()) {/// Branch or not empty
                if (!imageB) {
                    MapProgressInfo mapProgressInfoNull = new MapProgressInfo();
                    mapProgressInfoNull.ImageMap = "";
                    mapProgressInfoNull.isActiveImage = beginImage;
                    ProgressInfo progressInfoListNull = new ProgressInfo();
                    progressInfoListNull.progressInfoList = new ArrayList<>();
                    if (!jsonListLoad.isEmpty()) {
                        if (jsonListLoad.get(0).numBranch == 0) {
                            progressInfoListNull.isActiveLineOne = true;
                            progressInfoListNull.isActiveLineTwo = false;
                            progressInfoListNull.isActiveLineThree = false;
                            progressInfoListNull.isActiveLineFour = false;
                        }
                        if (jsonListLoad.get(0).numBranch == 1) {
                            progressInfoListNull.isActiveLineOne = false;
                            progressInfoListNull.isActiveLineTwo = true;
                            progressInfoListNull.isActiveLineThree = false;
                            progressInfoListNull.isActiveLineFour = false;
                        }
                    }
                    else {
                        progressInfoListNull.isActiveLineOne = false;
                        progressInfoListNull.isActiveLineTwo = false;
                        progressInfoListNull.isActiveLineThree = false;
                        progressInfoListNull.isActiveLineFour = false;
                    }
                    progressInfoListNull.progressInfoList.add(mapProgressInfoNull);
                    progressInfoListNull.branchFloor = 0;
                    bookModelOne.BranchJson = ProgressInfo.jsonGenarationSave(bookModelOne.BranchJson, progressInfoListNull);
                    bookModelOne.save();
                }
                ProgressInfo progressInfoList = new ProgressInfo();
                progressInfoList.progressInfoList = new ArrayList<>();
                progressInfoList.branchFloor = branchFloor;
                if (!jsonListLoad.isEmpty()) {
                    if (jsonListLoad.get(0).numBranch == 0) {
                        progressInfoList.isActiveLineOne = true;
                        progressInfoList.isActiveLineTwo = false;
                        progressInfoList.isActiveLineThree = false;
                        progressInfoList.isActiveLineFour = false;
                    }
                    if (jsonListLoad.get(0).numBranch == 1) {
                        progressInfoList.isActiveLineOne = false;
                        progressInfoList.isActiveLineTwo = true;
                        progressInfoList.isActiveLineThree = false;
                        progressInfoList.isActiveLineFour = false;
                    }
                }
                else {
                    progressInfoList.isActiveLineOne = false;
                    progressInfoList.isActiveLineTwo = false;
                    progressInfoList.isActiveLineThree = false;
                    progressInfoList.isActiveLineFour = false;
                }
                for (int i = 0; i < item.branch.size(); i++) {
                    MapProgressInfo mapProgressInfo;
                    if (!jsonListLoad.isEmpty()) {
                        if (jsonListLoad.get(0).numBranch == i) {
                            mapProgressInfo = BranchItem(item.branch.get(i).content, branchFloor, jsonListLoad);
                        }
                        else {
                            mapProgressInfo = BranchItem(item.branch.get(i).content, branchFloor, null);
                        }
                    }
                    else {
                        mapProgressInfo = BranchItem(item.branch.get(i).content, branchFloor, null);
                    }
                    if (!jsonListLoad.isEmpty()) {
                        if (jsonListLoad.get(0).numBranch == i) {
                            if (i == 0) {
                                mapProgressInfo.isActiveImage = true;
                                if (jsonListLoad.size() > 1) {
                                    if (jsonListLoad.get(1).numBranch == 0) {
                                        progressInfoList.isButtomActiveLineThree = true;
                                    }
                                    if (jsonListLoad.get(1).numBranch == 1) {
                                        progressInfoList.isButtomActiveLineFour = true;
                                    }
                                }
                            }
                            if (i == 1) {
                                mapProgressInfo.isActiveImage = true;
                                if (jsonListLoad.size() > 1) {
                                    if (jsonListLoad.get(1).numBranch == 0) {
                                        progressInfoList.isButtomActiveLineOne = true;
                                    }
                                    if (jsonListLoad.get(1).numBranch == 1) {
                                        progressInfoList.isButtomActiveLineTwo = true;
                                    }
                                }
                            }
                        }
                    }
                    progressInfoList.progressInfoList.add(mapProgressInfo);
                }
                if (progressInfoLists.isEmpty()) {
                    progressInfoList.metkaEnd = true;
                }
                else {
                    progressInfoList.metkaEnd = false;
                }
                bookModelOne.BranchJson = ProgressInfo.jsonGenarationSave(bookModelOne.BranchJson, progressInfoList);
                bookModelOne.save();
            }
        }
        if (!progressInfoLists.isEmpty()) {//more than two endings

            ProgressInfo progressInfoList = new ProgressInfo();
            progressInfoList.progressInfoList = new ArrayList<>();
            Integer tmpFloorBranch = 0;
            for (int i = progressInfoLists.size()-1; i >= 0; i--) {
                if (tmpFloorBranch != 0) {
                    if (tmpFloorBranch == progressInfoLists.get(i).branchFloor) {
                        if (i == 0) {
                            progressInfoList.metkaEnd = true;
                        }
                        else {
                            progressInfoList.metkaEnd = false;
                        }
                        for (MapProgressInfo item : progressInfoLists.get(i).progressInfoList) {
                            progressInfoList.progressInfoList.add(item);
                        }
                        if (progressInfoList.progressInfoList.size() == 4) {
                            if (progressInfoLists.get(i).isButtomActiveLineOne) {
                                progressInfoList.isButtomActiveLineOne = true;
                            }
                            if (progressInfoLists.get(i).isButtomActiveLineTwo) {
                                progressInfoList.isButtomActiveLineTwo = true;
                            }
                            for (int j = 0; j < progressInfoList.progressInfoList.size(); j++) {
                                if (progressInfoList.progressInfoList.get(j).isActiveImage) {
                                    if (j == 0) {
                                        progressInfoList.isActiveLineOne = true;
                                    }
                                    if (j == 1) {
                                        progressInfoList.isActiveLineTwo = true;
                                    }
                                    if (j == 2) {
                                        progressInfoList.isActiveLineThree = true;
                                    }
                                    if (j == 3) {
                                        progressInfoList.isActiveLineFour = true;
                                    }
                                }
                            }
                        }
                        if (i == 0) {
                            if (progressInfoList.progressInfoList.size() == 2) {
                                for (int j = 0; j < progressInfoList.progressInfoList.size(); j++) {
                                    if (progressInfoList.progressInfoList.get(j).isActiveImage) {
                                        if (j == 0) {
                                            progressInfoList.isActiveLineOne = true;
                                        }
                                        if (j == 1) {
                                            progressInfoList.isActiveLineTwo = true;
                                        }
                                    }
                                }
                            }
                            bookModelOne.BranchJson = ProgressInfo.jsonGenarationSave(bookModelOne.BranchJson, progressInfoList);
                            bookModelOne.save();
                            progressInfoList.progressInfoList.clear();
                        }
                    }
                    else {
                        tmpFloorBranch = progressInfoLists.get(i).branchFloor;
                        if (progressInfoList.progressInfoList.size() == 4) {
                            if (progressInfoLists.get(i).isButtomActiveLineOne) {
                                progressInfoList.isButtomActiveLineOne = true;
                            }
                            if (progressInfoLists.get(i).isButtomActiveLineTwo) {
                                progressInfoList.isButtomActiveLineTwo = true;
                            }
                            for (int j = 0; j < progressInfoList.progressInfoList.size(); j++) {
                                if (progressInfoList.progressInfoList.get(j).isActiveImage) {
                                    if (j == 0) {
                                        progressInfoList.isActiveLineOne = true;
                                    }
                                    if (j == 1) {
                                        progressInfoList.isActiveLineTwo = true;
                                    }
                                    if (j == 2) {
                                        progressInfoList.isActiveLineThree = true;
                                    }
                                    if (j == 3) {
                                        progressInfoList.isActiveLineFour = true;
                                    }
                                }
                            }
                        }
                        bookModelOne.BranchJson = ProgressInfo.jsonGenarationSave(bookModelOne.BranchJson, progressInfoList);
                        bookModelOne.save();
                        progressInfoList.branchFloor = progressInfoLists.get(i).branchFloor;
                        progressInfoList.progressInfoList.clear();
                        progressInfoList.isActiveLineOne = false;
                        progressInfoList.isActiveLineTwo = false;
                        progressInfoList.isActiveLineThree = false;
                        progressInfoList.isActiveLineFour = false;
                        if (i == 0) {
                            progressInfoList.metkaEnd = true;
                        }
                        else {
                            progressInfoList.metkaEnd = false;
                        }
                        for (MapProgressInfo item : progressInfoLists.get(i).progressInfoList) {
                            progressInfoList.progressInfoList.add(item);
                        }

                        if (i == 0) {
                            if (progressInfoList.progressInfoList.size() == 2) {
                                for (int j = 0; j < progressInfoList.progressInfoList.size(); j++) {
                                    if (progressInfoList.progressInfoList.get(j).isActiveImage) {
                                        if (j == 0) {
                                            progressInfoList.isActiveLineOne = true;
                                        }
                                        if (j == 1) {
                                            progressInfoList.isActiveLineTwo = true;
                                        }
                                    }
                                }
                            }
                            bookModelOne.BranchJson = ProgressInfo.jsonGenarationSave(bookModelOne.BranchJson, progressInfoList);
                            bookModelOne.save();
                            progressInfoList.progressInfoList.clear();
                        }
                    }
                }
                else {
                    tmpFloorBranch = progressInfoLists.get(i).branchFloor;
                    progressInfoList.branchFloor = progressInfoLists.get(i).branchFloor;
                    if (i == 0) {
                        progressInfoList.metkaEnd = true;
                    }
                    else {
                        progressInfoList.metkaEnd = false;
                    }
                    for (MapProgressInfo item : progressInfoLists.get(i).progressInfoList) {
                        progressInfoList.progressInfoList.add(item);
                    }
                }
            }
        }
        dialog.dismiss();
    }

    public MapProgressInfo BranchItem(List<TextInfo> infoList, Integer branchFloor, List<BranchJsonSave> jsonListLoad) {
        boolean imageB = false;
        MapProgressInfo mapProgressInfo = new MapProgressInfo();
        for (TextInfo item : infoList) {
            if (item.imgPeopleA != null && !imageB) {
                mapProgressInfo.ImageMap = item.imgPeopleA;
                mapProgressInfo.isActiveImage = false;
                imageB = true;
            }
            if (item.imgPeopleB != null && !imageB) {
                mapProgressInfo.ImageMap = item.imgPeopleB;
                mapProgressInfo.isActiveImage = false;
                imageB = true;
            }
            if (!item.branch.isEmpty()) {
                branchFloor++;
                ProgressInfo progressInfoList = new ProgressInfo();
                progressInfoList.progressInfoList = new ArrayList<>();
                progressInfoList.branchFloor = branchFloor;
                for (int i = 0; i < item.branch.size(); i++) {
                    MapProgressInfo mapProgressInfoB;
                    if (jsonListLoad != null && jsonListLoad.size() != (branchFloor - 1)) {
                        if (jsonListLoad.get(branchFloor - 1).numBranch == i) {
                            mapProgressInfoB = BranchItem(item.branch.get(i).content, branchFloor, jsonListLoad);
                            if (jsonListLoad.get(branchFloor - 1).numBranch == 0) {
                                progressInfoList.isButtomActiveLineOne = true;
                            }
                            if (jsonListLoad.get(branchFloor - 1).numBranch == 1) {
                                progressInfoList.isButtomActiveLineTwo = true;
                            }
                        }
                        else {
                            mapProgressInfoB = BranchItem(item.branch.get(i).content, branchFloor, null);
                        }
                    }
                    else {
                        mapProgressInfoB = BranchItem(item.branch.get(i).content, branchFloor, null);
                    }
                    if (jsonListLoad != null) {
                        if (!jsonListLoad.isEmpty() && jsonListLoad.size() != (branchFloor - 1)) {
                            if (jsonListLoad.get(branchFloor - 1).numBranch == i) {
                                if (i == 0) {
                                    mapProgressInfoB.isActiveImage = true;
                                }
                                if (i == 1) {
                                    mapProgressInfoB.isActiveImage = true;
                                }
                            }
                        }
                    }
                    progressInfoList.progressInfoList.add(mapProgressInfoB);
                }
                progressInfoLists.add(progressInfoList);
            }
        }
        return mapProgressInfo;
    }
}
