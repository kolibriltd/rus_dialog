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
        setContentView(R.layout.activity_guest);

        mLayout = (View) findViewById(R.id.relLayout);
        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        Button btnSingUp = (Button) findViewById(R.id.btnSingUp);

        ListView listBooksGuest = (ListView) findViewById(R.id.listBooksGuest);
        listBooksGuest.setDividerHeight(0);
        booksAdapter = new BooksAdapter(GuestActivity.this, false);
        listBooksGuest.setAdapter(booksAdapter);

        btnLogin.setOnClickListener(new View.OnClickListener() {
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
        });

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
                Snackbar.make(mLayout, R.string.permision_available_storage, Snackbar.LENGTH_SHORT).show();
                ViewListBooks();
                getPermission = true;
            } else {
                Snackbar.make(mLayout, R.string.permissions_not_granted, Snackbar.LENGTH_SHORT).show();
            }
        }
        else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public void ViewListBooks() {
        List<BookModel> bookModelList = new Select().from(BookModel.class).execute();
        if (bookModelList.isEmpty()) {
            new getGuestBooks().execute();
        }
        else {
            for (BookModel item : bookModelList) {
                Books itemAdapter = new Books();
                itemAdapter.pathCoverFileStorage = item.getId().toString();
                itemAdapter.id_book = Integer.parseInt(item.IdDbServer);
                itemAdapter.name = item.Name;
                itemAdapter.author = item.Author;
                itemAdapter.isViewCount = item.IsViewCount;
                itemAdapter.pathCoverFile = item.PathCoverFile;
                itemAdapter.pathCoverFileStorage = item.PathCoverFileStorage;
                itemAdapter.flagGuest = false;
                booksAdapter.add(itemAdapter);
            }
            Books itemAdapterEmty = new Books();
            itemAdapterEmty.flagGuest = true;
            booksAdapter.add(itemAdapterEmty);
            booksAdapter.notifyDataSetChanged();
            id_book = Integer.parseInt(bookModelList.get(bookModelList.size() - 1).IdDbServer);
            new getGuestBooks().execute();
            //new getViewCountBooks().execute();
        }
    }

    class getGuestBooks extends AsyncTask<String, String, List<Books>> {
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
        protected List<Books> doInBackground(String... strings) {
            List<Books> result = httpConect.getGuestBooks(id_book);
            return result;
        }

        protected void onPostExecute(List<Books> result) {
            if (result != null) {
                for (Books item : result) {

                    BookModel bookModel = new BookModel();
                    bookModel.IdDbServer = item.id_book + "";
                    bookModel.Name = item.name;
                    bookModel.Author = item.author;
                    bookModel.Description = item.description;
                    bookModel.IsViewCount = item.isViewCount;
                    bookModel.PathCoverFile = item.pathCoverFile;
                    bookModel.save();

                    if (getPermission) {
                        booksAdapter.add(item);
                    }
                }
                if (getPermission) {
                    Books itemAdapterEmty = new Books();
                    itemAdapterEmty.flagGuest = true;
                    booksAdapter.add(itemAdapterEmty);
                    booksAdapter.notifyDataSetChanged();
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
