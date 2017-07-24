package com.wearesputnik.istoria.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.HttpConnectClass;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;

public class InfoBookActivity extends BaseActivity {
    TextView  txtDescription, txtAuthor, txtName, txtEve, btnNext, txtRaiting;
    int id_book;
    RelativeLayout relInfoScreen1, relInfoScreen2, relGradient;
    ImageView imageViewCoverInfo;
    private DisplayImageOptions options;
    BookModel bookModelOne;
    boolean twoScrean;
    boolean guestFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
       // getSupportActionBar().setHomeAsUpIndicator(R.mipmap.arrow);

        relInfoScreen1 = (RelativeLayout) findViewById(R.id.relInfoScreen1);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtAuthor = (TextView) findViewById(R.id.txtAuthor);
        txtName = (TextView) findViewById(R.id.txtName);
        txtEve = (TextView) findViewById(R.id.txtEve);
        txtRaiting = (TextView) findViewById(R.id.txtRaiting);
        btnNext = (TextView) findViewById(R.id.btnNext);
        imageViewCoverInfo = (ImageView) findViewById(R.id.imageViewCoverInfo);
        relGradient = (RelativeLayout) findViewById(R.id.relGradient);

        relInfoScreen2 = (RelativeLayout) findViewById(R.id.relInfoScreen2);
        relInfoScreen2.setVisibility(View.GONE);

        options = new DisplayImageOptions.Builder()
            .showImageOnLoading(R.mipmap.null_foto_info)
            .showImageForEmptyUri(R.mipmap.null_foto_info)
            .showImageOnFail(R.mipmap.null_foto_info)
            .cacheInMemory(true)
            .cacheOnDisk(true)
            .considerExifParams(true)
            .bitmapConfig(Bitmap.Config.RGB_565)
            .build();

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id_book = bundle.getInt("id_book");
            guestFlag = bundle.getBoolean("guestFlag");
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

        Books itemBooks = new Books();
        itemBooks.name = bookModelOne.Name;
        itemBooks.author = bookModelOne.Author;
        itemBooks.description = bookModelOne.Description;
        itemBooks.pathCoverFile = bookModelOne.PathCoverFile;
        itemBooks.isViewCount = bookModelOne.IsViewCount;
        itemBooks.pathCoverFileStorage = bookModelOne.PathCoverFileStorage;
        itemBooks.raiting = bookModelOne.Raiting;
        ViewBookOne(itemBooks);

        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean intentBool = true;
                if (twoScrean) {
                    if (bookModelOne.TextInfoList != null || !bookModelOne.TextInfoList.trim().equals("null")) {
                        Intent intent = new Intent(InfoBookActivity.this, ItemBookReadActivity.class);
                        intent.putExtra("id_book", id_book);
                        finish();
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
                            finish();
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

    private void ViewBookOne(Books result) {
        txtDescription.setText(result.description);
        txtAuthor.setText(result.author);
        txtName.setText(result.name);
        txtEve.setText(result.isViewCount + "");
        txtRaiting.setText(result.raiting);
        if (result.pathCoverFileStorage != null) {
            imageViewCoverInfo.setImageURI(Uri.parse(result.pathCoverFileStorage));
        } else if (result.pathCoverFile != null) {
            String url_img = HttpConnectClass.URL_IMAGE + result.pathCoverFile;
            ImageLoader.getInstance()
                .displayImage(url_img, imageViewCoverInfo, options, new SimpleImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    }
                }, new ImageLoadingProgressListener() {
                    @Override
                    public void onProgressUpdate(String imageUri, View view, int current, int total) {

                    }
                });
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
            Books result = httpConect.getOneBook(id_book, guestFlag);
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
}
