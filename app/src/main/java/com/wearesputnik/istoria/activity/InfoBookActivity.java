package com.wearesputnik.istoria.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.squareup.picasso.Picasso;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.BranchBook;
import com.wearesputnik.istoria.helpers.BranchCount;
import com.wearesputnik.istoria.helpers.HttpConnectClass;
import com.wearesputnik.istoria.helpers.TextInfo;
import com.wearesputnik.istoria.jsonHelper.ContentBook;
import com.wearesputnik.istoria.jsonHelper.ContentTxt;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class InfoBookActivity extends BaseActivity {
    TextView  txtDescription, txtAuthor, txtName, txtEve;
    Button btnNext;
    int id_book, countBranch;
    RelativeLayout relInfoScreen1, relInfoScreen2, relGradient;
    ImageView imageViewCoverInfo, imgMoney;
    BookModel bookModelOne;
    boolean twoScrean;
    boolean btnEndRead = false;
    BranchBook branchBook = new BranchBook();

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

        countBranch = 0;

        branchBook.branchCount = new ArrayList<>();

        relInfoScreen1 = (RelativeLayout) findViewById(R.id.relInfoScreen1);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtAuthor = (TextView) findViewById(R.id.txtAuthor);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEve = (TextView) findViewById(R.id.txtEve);
        btnNext = (Button) findViewById(R.id.btnNext);
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

        if (bookModelOne.BranchJsonEnd != null) {
            if (bookModelOne.BranchJsonEnd.trim().equals("END")) {
                btnEndRead = true;
                btnNext.setText(R.string.info_read_end);
            }
        }

        if (bookModelOne.TypeId == 3) {
            imgMoney.setVisibility(View.VISIBLE);
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

        Books itemBooks = new Books();
        itemBooks.name = bookModelOne.Name;
        itemBooks.author = bookModelOne.Author;
        itemBooks.description = bookModelOne.Description;
        itemBooks.pathCoverFile = bookModelOne.PathCoverFile;
        itemBooks.isViewCount = bookModelOne.IsViewCount;
        itemBooks.pathCoverFileStorage = bookModelOne.PathCoverFileStorage;
        itemBooks.raiting = bookModelOne.Raiting;
        ViewBookOne(itemBooks);
    }

    private void ViewBookOne(Books result) {
        if (bookModelOne.BranchJsonSave == null) {
            JsonSaveBook();
            bookModelOne.BranchJsonSave = BranchBook.jsonGeneration(branchBook);
        }
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

    private void JsonSaveBook () {
        TextInfo textInfo = new TextInfo();

        try {
            JSONObject json = new JSONObject(bookModelOne.TextInfoList);
            textInfo = TextInfo.parseJson(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        BranchCount branchCount = new BranchCount();
        branchCount.countMessage = textInfo.contentBook.contentTxt.size();
        branchCount.indexBranch = textInfo.contentBook.index;
        branchCount.indexFloor = textInfo.contentBook.floor;
        branchCount.isViewBranch = false;
        branchCount.tapCount = 0;

        branchBook.branchCount.add(branchCount);

        for (ContentTxt item : textInfo.contentBook.contentTxt) {
            if (!item.branch.isEmpty()) {
                for (ContentBook itemBook : item.branch) {
                    BranchJsonSaveBook(itemBook);
                }
            }
        }
    }


    private void BranchJsonSaveBook(ContentBook contentBook) {
        BranchCount branchCount = new BranchCount();
        branchCount.countMessage = contentBook.contentTxt.size();
        branchCount.indexBranch = contentBook.index;
        branchCount.indexFloor = contentBook.floor;
        branchCount.isViewBranch = false;
        branchCount.tapCount = 0;

        branchBook.branchCount.add(branchCount);

        for (ContentTxt item : contentBook.contentTxt) {
            if (!item.branch.isEmpty()) {
                for (ContentBook itemBook : item.branch) {
                    BranchJsonSaveBook(itemBook);
                }
            }
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
                builder.setTitle("Ошыбка")
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
}
