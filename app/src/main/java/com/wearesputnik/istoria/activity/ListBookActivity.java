package com.wearesputnik.istoria.activity;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Window;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.activeandroid.query.Select;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.wearesputnik.istoria.BaseActivity;
import com.wearesputnik.istoria.R;
import com.wearesputnik.istoria.UILApplication;
import com.wearesputnik.istoria.adapters.BooksAdapter;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.ResultInfo;
import com.wearesputnik.istoria.helpers.UserInfo;
import com.wearesputnik.istoria.models.BookModel;
import com.wearesputnik.istoria.models.IstoriaInfo;
import com.wearesputnik.istoria.models.UserModel;
import com.wearesputnik.istoria.service.BooksService;

import java.util.Collections;
import java.util.List;

public class ListBookActivity extends BaseActivity  implements
        GoogleApiClient.OnConnectionFailedListener{
    BooksAdapter booksAdapter;
    int id_book = 0;
    ImageView imgProfile;
    ImageView imgAutorNew;
    private static final int RC_SIGN_IN = 9001;

    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mProgressDialog;

    Boolean getListBookGuest = false;

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
        imgAutorNew = (ImageView) findViewById(R.id.imgAutorNew);
        imgAutorNew.setVisibility(View.GONE);

        GridView listBooks = (GridView) findViewById(R.id.listBooks);
        booksAdapter = new BooksAdapter(ListBookActivity.this);
        listBooks.setAdapter(booksAdapter);

        new getBooks().execute();

        if (!isMyServiceRunning(BooksService.class)) {
            startService(new Intent(ListBookActivity.this, BooksService.class));
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(getString(R.string.server_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        imgAutorNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogNewAutor();
            }
        });

        imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(ListBookActivity.this, ProfileActivity.class));
            }
        });

        IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
        if (istoriaInfo != null) {
            if (istoriaInfo.AppKey != null) {
                imgProfile.setVisibility(View.VISIBLE);
                UserInfo userInfo = UserModel.SelectUser();
                if (userInfo != null) {
                    if (!userInfo.photo.trim().equals("")) {
                        Glide.with(ListBookActivity.this)
                                .load(userInfo.photo)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imgProfile);
                    }
                }
                getListBookGuest = false;
            }
            else {
                imgProfile.setVisibility(View.GONE);
                signIn();
                getListBookGuest = true;
            }
        }
        else {
            imgProfile.setVisibility(View.GONE);
            signIn();
            getListBookGuest = true;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo serviceInfo : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        List<Books> booksList = BookModel.ListBooksIstori();
        if (booksList.size() == 0) {
            new getBooks().execute();
        }
        else {
            ViewListBooks();
        }

        IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
        if (istoriaInfo != null) {
            if (istoriaInfo.AppKey != null) {
                imgProfile.setVisibility(View.VISIBLE);
                UserInfo userInfo = UserModel.SelectUser();
                if (userInfo != null) {
                    if (!userInfo.photo.trim().equals("")) {
                        Glide.with(ListBookActivity.this)
                                .load(userInfo.photo)
                                .apply(RequestOptions.circleCropTransform())
                                .into(imgProfile);
                    }
                }
                getListBookGuest = false;
            }
            else {
                imgProfile.setVisibility(View.GONE);
                signIn();
                getListBookGuest = true;
            }
        }
        else {
            imgProfile.setVisibility(View.GONE);
            signIn();
            getListBookGuest = true;
        }
    }

    private void ViewListBooks() {
        if (!booksAdapter.isEmpty()) {
            booksAdapter.clear();
        }
        List<Books> booksList = BookModel.ListBooksIstori();
        Collections.reverse(booksList);
        for (Books item : booksList) {
            booksAdapter.add(item);
        }
        booksAdapter.notifyDataSetChanged();
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
                        BookModel bookModelOne = new Select().from(BookModel.class).where("IdDbServer = ?", item.id_book).executeSingle();
                        if (bookModelOne != null) {
                            BookModel.EditBook(item);
                        }
                        else {
                            BookModel.AddBook(item);
                        }
                    }
                    ViewListBooks();
                    startService(new Intent(ListBookActivity.this, BooksService.class));
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
            else {
                List<Books> booksList = BookModel.ListBooksIstori();
                if (booksList != null) {
                    if (booksList.size() > 0) {
                        ViewListBooks();
                    }
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

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        //Log.d(TAG, "onConnectionFailed:" + connectionResult);
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            //Log.d(TAG, "Got cached sign-in");
            GoogleSignInResult result = opr.get();
            handleSignInResult(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showProgressDialog();
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    hideProgressDialog();
                    handleSignInResult(googleSignInResult);
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            UserInfo userInfo = new UserInfo();
            userInfo.email = acct.getEmail();
            userInfo.firs_name = acct.getDisplayName();
            userInfo.app_key = acct.getServerAuthCode();
            if (acct.getPhotoUrl() != null) {
                userInfo.photo = acct.getPhotoUrl().toString();
            }
            else {
                userInfo.photo = "";
            }

            UserModel.AddEditUser(userInfo);

            new getLoginTask().execute(userInfo);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopService(new Intent(ListBookActivity.this, BooksService.class));
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ///stopService(new Intent(ListBookActivity.this, BooksService.class));
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage(getString(R.string.loading));
            mProgressDialog.setIndeterminate(true);
        }
        if (getListBookGuest) {
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    class getLoginTask extends AsyncTask<UserInfo, UserInfo, ResultInfo> {
        Dialog dialog;

        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new Dialog(ListBookActivity.this, R.style.TransparentProgressDialog);
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
            dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.custom_progress_dialog);
            if (getListBookGuest) {
                dialog.show();
            }
        }

        @Override
        protected ResultInfo doInBackground(UserInfo... userInfo) {
            ResultInfo result = httpConect.setSingUpGmail(userInfo[0]);
            return result;
        }

        protected void onPostExecute(ResultInfo result) {
            dialog.dismiss();
            if (result != null) {
                if (result.status == 0) {
                    UILApplication.AppKey = result.userInfoResult.app_key;
                    IstoriaInfo.AddEditIstoriInfo(result.userInfoResult);
                    if (getListBookGuest) {
                        onRestart();
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
            super.onPostExecute(result);
        }
    }
}
