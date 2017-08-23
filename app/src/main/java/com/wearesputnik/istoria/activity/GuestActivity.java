package com.wearesputnik.istoria.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.adapters.BooksAdapter;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;

import java.util.List;

public class GuestActivity extends BaseActivity {
    BooksAdapter booksAdapter;
    int id_book = 0;

    String[] PermisionStorage = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    Integer RequestStorageId = 1;

    Boolean getPermission = false;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_guest);
/*
        mLayout = (View) findViewById(R.id.relLayout);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSingUp = (Button) findViewById(R.id.btnSingUp);

        ListView listBooksGuest = (ListView) findViewById(R.id.listBooksGuest);
        listBooksGuest.setDividerHeight(0);
        booksAdapter = new BooksAdapter(GuestActivity.this, false);
        listBooksGuest.setAdapter(booksAdapter);*/

        /*btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuestActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnSingUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GuestActivity.this, SingupActivity.class);
                startActivity(intent);
                finish();
            }
        });*/

        PermissionStorage();
    }

    public void PermissionStorage () {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(GuestActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(GuestActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestStoragePermission();
            }
            else {
                ViewListBooks();
                getPermission = true;
            }
        }
        else {
            ViewListBooks();
            getPermission = true;
        }
    }

    public void requestStoragePermission () {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Snackbar.make(mLayout, R.string.permission_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(GuestActivity.this, PermisionStorage, RequestStorageId);
                        }
                    })
                    .show();
        } else {
            ActivityCompat.requestPermissions(this, PermisionStorage, RequestStorageId);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == RequestStorageId) {
            if (verifyPermissions(grantResults)) {
                ViewListBooks();
                getPermission = true;
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void ViewListBooks() {
        IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
        if (istoriaInfo == null) {
            IstoriaInfo newIstoriaInfo = new IstoriaInfo();
            newIstoriaInfo.IsViewTwoScreen = true;
            newIstoriaInfo.IsTapViewScreen = false;
            newIstoriaInfo.save();
        }

        BookModel bookModel = new Select().from(BookModel.class).where("IdDbServer = ?", 1).executeSingle();
        if (bookModel == null) {
            new getGuestBooks().execute();
        }
        else {
            Intent intent = new Intent(GuestActivity.this, ItemBookReadActivity.class);
            intent.putExtra("id_book", Integer.parseInt(bookModel.IdDbServer));
            intent.putExtra("guestFlag", false);
            startActivity(intent);
            finish();
        }
    }

    class getGuestBooks extends AsyncTask<String, String, Books> {
        Dialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(GuestActivity.this, R.style.TransparentProgressDialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_progress_dialog);
            dialog.show();
        }

        @Override
        protected Books doInBackground(String... strings) {
            Books result = httpConect.getGuestBooks(id_book);
            return result;
        }

        protected void onPostExecute(Books result) {
            if (result != null) {

                    BookModel bookModel = new BookModel();
                    bookModel.IdDbServer = result.id_book + "";
                    bookModel.Name = result.name;
                    bookModel.Author = result.author;
                    bookModel.Description = result.description;
                    bookModel.IsViewCount = result.isViewCount;
                    bookModel.PathCoverFile = result.pathCoverFile;
                    bookModel.Raiting = result.raiting;
                    bookModel.TextInfoList = result.textInfoList;
                    bookModel.TypeId = result.type_id;
                    bookModel.save();

                if (getPermission) {
                    Intent intent = new Intent(GuestActivity.this, ItemBookReadActivity.class);
                    intent.putExtra("id_book", result.id_book);
                    intent.putExtra("guestFlag", false);
                    startActivity(intent);
                    finish();
                }

            }
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    public static boolean verifyPermissions(int[] grantResults) {
        // At least one result must be checked.
        if(grantResults.length < 1){
            return false;
        }

        // Verify that each required permission has been granted, otherwise return false.
        for (int result : grantResults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }
}
