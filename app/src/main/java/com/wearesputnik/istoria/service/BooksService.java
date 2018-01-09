package com.wearesputnik.istoria.service;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

import com.activeandroid.query.Select;
import com.activeandroid.util.Log;
import com.wearesputnik.istoria.helpers.Books;
import com.wearesputnik.istoria.helpers.HttpConnectClass;
import com.wearesputnik.istoria.helpers.ResultInfo;
import com.wearesputnik.istoria.models.BookModel;

public class BooksService extends Service {
    public HttpConnectClass httpConect;
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        httpConect = HttpConnectClass.getInstance();
        //Toast.makeText(this, "Create Setrvice", Toast.LENGTH_SHORT).show();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Toast.makeText(this, "Start Setrvice", Toast.LENGTH_SHORT).show();
        BooksList books = new BooksList();
        books.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        //Toast.makeText(this, "Stop Setrvice", Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }

    private class BooksList extends Thread {
        public boolean stopped = false;

        public void run() {
            try {
                while (!stopped) {
                    // Активность списка
                    new getBooks().execute();

                    try {
                        Thread.sleep(500000);
                    }
                    catch (Exception e) {
                    }
                }
            } catch (Exception e) {
            }
        }
    }

    class getBooks extends AsyncTask<String, String, ResultInfo> {
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected ResultInfo doInBackground(String... strings) {
            ResultInfo result = httpConect.getBooks(0, BooksService.this);
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
                }

            }
            super.onPostExecute(result);
        }
    }
}
