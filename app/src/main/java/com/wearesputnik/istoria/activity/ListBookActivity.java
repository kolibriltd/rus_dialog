package com.wearesputnik.istoria.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ListView;

import com.activeandroid.query.Select;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.adapters.BooksAdapter;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.ResultInfo;
import com.wearesputnik.istoria.models.BookModel;

import java.util.List;

public class ListBookActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
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
        setContentView(R.layout.activity_list_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLayout = (View) findViewById(R.id.relLayout);

        ListView listBooks = (ListView) findViewById(R.id.listBooks);
        listBooks.setDividerHeight(0);
        booksAdapter = new BooksAdapter(ListBookActivity.this, true);
        listBooks.setAdapter(booksAdapter);

        PermissionStorage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_list_book, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_profile:
                startActivity(new Intent(ListBookActivity.this, ProfileActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        booksAdapter.clear();

        List<BookModel> bookModelList = new Select().from(BookModel.class).execute();
        if (bookModelList.isEmpty()) {
            new getBooks().execute();
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
                itemAdapter.raiting = item.Raiting;
                itemAdapter.flagGuest = false;
                booksAdapter.add(itemAdapter);
            }
            booksAdapter.notifyDataSetChanged();
            id_book = Integer.parseInt(bookModelList.get(bookModelList.size() - 1).IdDbServer);
            new getBooks().execute();
            new getViewCountBooks().execute();
        }
    }

    public void PermissionStorage () {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(ListBookActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(ListBookActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
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
                            ActivityCompat.requestPermissions(ListBookActivity.this, PermisionStorage, RequestStorageId);
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
            new getBooks().execute();
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
                itemAdapter.raiting = item.Raiting;
                itemAdapter.pathCoverFileStorage = item.PathCoverFileStorage;
                itemAdapter.flagGuest = false;
                booksAdapter.add(itemAdapter);
            }
            booksAdapter.notifyDataSetChanged();
            id_book = Integer.parseInt(bookModelList.get(bookModelList.size() - 1).IdDbServer);
            new getBooks().execute();
            new getViewCountBooks().execute();
        }
    }

    class getBooks extends AsyncTask<String, String, ResultInfo> {
        Dialog dialog;
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(ListBookActivity.this, R.style.TransparentProgressDialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_progress_dialog);
            dialog.show();
        }

        @Override
        protected ResultInfo doInBackground(String... strings) {
            ResultInfo result = httpConect.getBooks(id_book, ListBookActivity.this);
            return result;
        }

        protected void onPostExecute(ResultInfo result) {
            if (result != null) {
                if (result.status == 0) {
                    for (Books item : result.booksList) {

                        BookModel bookModel = new BookModel();
                        bookModel.IdDbServer = item.id_book + "";
                        bookModel.Name = item.name;
                        bookModel.Author = item.author;
                        bookModel.Description = item.description;
                        bookModel.IsViewCount = item.isViewCount;
                        bookModel.Raiting = item.raiting;
                        bookModel.PathCoverFile = item.pathCoverFile;
                        bookModel.TypeId = item.type_id;
                        bookModel.save();

                        if (getPermission) {
                            booksAdapter.add(item);
                        }
                    }
                    if (getPermission) {
                        booksAdapter.notifyDataSetChanged();
                    }
                }
                else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(ListBookActivity.this);
                    builder.setTitle("Ошибка")
                        .setMessage(result.error)
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
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    class getViewCountBooks extends AsyncTask<String, String, List<Books>> {
        @Override
        protected List<Books> doInBackground(String... strings) {
            List<Books> result = httpConect.getViewIncCount();
            return result;
        }

        protected void onPostExecute(List<Books> result) {
            if (result != null) {
                for (Books item : result) {

                    BookModel bookModelOne = new Select().from(BookModel.class).where("IdDbServer = ?", item.id_book).executeSingle();
                    if (bookModelOne != null) {
                        bookModelOne.IsViewCount = item.isViewCount;
                        bookModelOne.Raiting = item.raiting;
                        bookModelOne.save();
                    }
                }
            }
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
