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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.adapters.BooksAdapter;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.ResultInfo;
import com.wearesputnik.istoria.helpers.UserInfo;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.UserModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ListBookActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {
    BooksAdapter booksAdapter;
    int id_book = 0;
    ImageView imgProfile;

    Boolean getListBook = false;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_book);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");

        mLayout = (View) findViewById(R.id.relLayout);
        imgProfile = (ImageView) findViewById(R.id.imgProfile);
        ImageView imgAutorNew = (ImageView) findViewById(R.id.imgAutorNew);

        GridView listBooks = (GridView) findViewById(R.id.listBooks);
        ///listBooks.setDividerHeight(0);
        booksAdapter = new BooksAdapter(ListBookActivity.this, true);
        listBooks.setAdapter(booksAdapter);

        ViewListBooks();
        UserInfo userInfo = UserModel.SelectUser();
        if (userInfo != null) {
            if (!userInfo.photo.trim().equals("")) {
                Glide.with(ListBookActivity.this)
                        .load(userInfo.photo)
                        .apply(RequestOptions.circleCropTransform())
                        .into(imgProfile);
            }
        }

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListBookActivity.this, ProfileActivity.class));
            }
        });

        imgAutorNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNewAutor();
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        booksAdapter.clear();

        List<BookModel> bookModelList = new Select().from(BookModel.class).execute();
        if (bookModelList.isEmpty()) {
            new getBooks().execute();
            getListBook = true;
        }
        else {
            getListBook = false;
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
                itemAdapter.new_istori_int = item.NewIstori;
                itemAdapter.flagGuest = false;
                booksAdapter.add(itemAdapter);
            }
            booksAdapter.notifyDataSetChanged();
            new getBooks().execute();
            new getViewCountBooks().execute();
        }
    }

    public void ViewListBooks() {
        List<BookModel> bookModelList = new Select().from(BookModel.class).execute();
        if (bookModelList.isEmpty()) {
            getListBook = true;
            new getBooks().execute();
        }
        else {
            getListBook = false;
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
                if (item.NewIstori != null) {
                    itemAdapter.new_istori_int = item.NewIstori;
                }
                else {
                    itemAdapter.new_istori_int = 2;
                }
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
                        item.new_istori_int = 1;
                        if (getListBook) {
                            BookModel bookModel = new BookModel();
                            bookModel.IdDbServer = item.id_book + "";
                            bookModel.Name = item.name;
                            bookModel.Author = item.author;
                            bookModel.Description = item.description;
                            bookModel.IsViewCount = item.isViewCount;
                            bookModel.Raiting = item.raiting;
                            bookModel.PathCoverFile = item.pathCoverFile;
                            bookModel.TypeId = item.type_id;
                            bookModel.LastModified = item.last_modified;
                            bookModel.NewIstori = 1;
                            bookModel.save();
                            booksAdapter.add(item);

                        }
                        else {
                            BookModel bookModelOne = new Select().from(BookModel.class).where("IdDbServer = ?", item.id_book).executeSingle();
                            if (bookModelOne == null) {
                                BookModel bookModel = new BookModel();
                                bookModel.IdDbServer = item.id_book + "";
                                bookModel.Name = item.name;
                                bookModel.Author = item.author;
                                bookModel.Description = item.description;
                                bookModel.IsViewCount = item.isViewCount;
                                bookModel.Raiting = item.raiting;
                                bookModel.PathCoverFile = item.pathCoverFile;
                                bookModel.TypeId = item.type_id;
                                bookModel.LastModified = item.last_modified;
                                bookModel.NewIstori = 1;
                                bookModel.save();
                                booksAdapter.add(item);
                                booksAdapter.notifyDataSetChanged();
                            }
                            else {
//                                Date dateServ = null;
//                                Date dateLocal = null;
//                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                                try {
//                                    dateServ = format.parse(item.last_modified);
//                                    dateLocal = format.parse(bookModelOne.LastModified);
//
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
//                                if (dateLocal.getDate() < dateServ.getDate()) {
                                    bookModelOne.Name = item.name;
                                    bookModelOne.Author = item.author;
                                    bookModelOne.Description = item.description;
                                    bookModelOne.IsViewCount = item.isViewCount;
                                    bookModelOne.Raiting = item.raiting;
                                    bookModelOne.PathCoverFile = item.pathCoverFile;
                                    bookModelOne.TypeId = item.type_id;
                                    bookModelOne.TextInfoList = item.textInfoList;
                                    bookModelOne.LastModified = item.last_modified;
                                    if (bookModelOne.NewIstori == null) {
                                        bookModelOne.NewIstori = 2;
                                    }
                                    bookModelOne.save();
                                //}
                            }

                        }
                    }
                    if (getListBook) {

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

    class setNewAutor extends AsyncTask<String, String, ResultInfo> {
        @Override
        protected ResultInfo doInBackground(String... strings) {
            ResultInfo result = httpConect.setNewAutor();
            return result;
        }

        protected void onPostExecute(ResultInfo result) {
            if (result != null) {
                Snackbar.make(mLayout, R.string.yesNewAutor, Snackbar.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }

    public void DialogNewAutor() {
        final Dialog dialog = new Dialog(ListBookActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_avtor_new);
        TextView txtYes = (TextView) dialog.findViewById(R.id.txtYes);
        TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);

        txtYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new setNewAutor().execute();
                dialog.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
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
