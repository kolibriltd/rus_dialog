package com.wearesputnik.istoria.helpers;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.activeandroid.query.Select;
import com.wearesputnik.istoria.UILApplication;
import com.wearesputnik.istoria.models.IstoriaInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HttpConnectClass {

    public static final String URL = "http://www.wearesputnik.com/knigs/index.php/api/";
    public static final String URL_IMAGE = "http://www.wearesputnik.com/knigs/protected";
//    public static final String URL = "http://www.wearesputnik.com/development.knigs/index.php/api/";
//    public static final String URL_IMAGE = "http://www.wearesputnik.com/development.knigs/protected";
    public static HttpClient http;

    private HttpConnectClass() {
        http = new DefaultHttpClient();
        ClientConnectionManager mgr = http.getConnectionManager();
        HttpParams params = http.getParams();
        http = new DefaultHttpClient(new ThreadSafeClientConnManager(params, mgr.getSchemeRegistry()), params);
        IstoriaInfo istoriaInfo = new Select().from(IstoriaInfo.class).where("Id=?", 1).executeSingle();
        if (istoriaInfo != null) {
            if (istoriaInfo.AppKey != null) {
                UILApplication.AppKey = istoriaInfo.AppKey;
            }
        }
    }

    public static HttpConnectClass getInstance() {
        synchronized (HttpConnectClass.class) {
            if (UILApplication.restInstance == null) {
                UILApplication.restInstance = new HttpConnectClass();
            }
        }
        return UILApplication.restInstance;
    }

    public static ResultInfo getLogin(String email, String password) {
        ResultInfo result = new ResultInfo();

        HttpPost request = new HttpPost(URL + "account_login");

        List<BasicNameValuePair> parametrs = Arrays.asList(
            new BasicNameValuePair("email", email),
            new BasicNameValuePair("password", password)
        );

        try {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parametrs, "UTF-8");
            request.setEntity(formEntity);
            HttpResponse response = http.execute(request);
            String jsonStr = streamToString(response.getEntity().getContent());

            JSONObject jsonObject = new JSONObject(jsonStr);
            result = ResultInfo.parseJson(jsonObject, "login");

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public static ResultInfo setSingUpGmail(UserInfo userInfo) {
        ResultInfo result = new ResultInfo();

        HttpPost request = new HttpPost(URL + "account_gmail");

        List<BasicNameValuePair> parametrs = Arrays.asList(
                new BasicNameValuePair("email", userInfo.email),
                new BasicNameValuePair("displayName", userInfo.firs_name),
                new BasicNameValuePair("code", userInfo.app_key),
                new BasicNameValuePair("photo", userInfo.photo)
        );

        try {
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(parametrs, "UTF-8");
            request.setEntity(formEntity);
            HttpResponse response = http.execute(request);
            String jsonStr = streamToString(response.getEntity().getContent());

            JSONObject jsonObject = new JSONObject(jsonStr);
            result = ResultInfo.parseJson(jsonObject, "login");

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    public static UserInfo getProfile() {
        UserInfo result = new UserInfo();

        HttpGet request = new HttpGet(URL + "get_profile");
        request.addHeader( "app_key", UILApplication.AppKey);

        try {
            HttpResponse response = http.execute(request);
            String jsonStr = streamToString(response.getEntity().getContent());
            Log.e("Profile", jsonStr);

            if (!jsonStr.equals("")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                result = UserInfo.parseJson(jsonObject.getJSONObject("result"));
            }
            else {
                return null;
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResultInfo setNewAutor() {
        ResultInfo result = new ResultInfo();

        HttpGet request = new HttpGet(URL + "set_new_autor");
        request.addHeader( "app_key", UILApplication.AppKey);

        try {
            HttpResponse response = http.execute(request);
            String jsonStr = streamToString(response.getEntity().getContent());
            ///Log.e("Profile", jsonStr);

            /*if (!jsonStr.equals("")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                result = UserInfo.parseJson(jsonObject.getJSONObject("result"));
            }
            else {
                return null;
            }*/
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Books getGuestBooks(int id_book) {
        Books result = new Books();

        HttpGet request = new HttpGet(URL + "guest_books");


        try {
            HttpResponse response = http.execute(request);
            String jsonStr = streamToString(response.getEntity().getContent());
            Log.e("GUEST", jsonStr);

            if (!jsonStr.equals("")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                result = Books.parseJson(jsonObject.getJSONObject("result"));
            }
            else {
                return null;
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static ResultInfo getBooks(int id_book, Context context) {
        ResultInfo result = new ResultInfo();
        PackageInfo packageInfo = null;

        try {
            packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }


        HttpGet request = new HttpGet(URL + "list_books?id=" + id_book);
        request.addHeader( "app_key", UILApplication.AppKey);
        request.addHeader( "version_code", packageInfo.versionCode + "");

        try {
            HttpResponse response = http.execute(request);
            String jsonStr = streamToString(response.getEntity().getContent());

            JSONObject jsonObject = new JSONObject(jsonStr);
            result = ResultInfo.parseJson(jsonObject, "listBook");

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Books getOneBook(int id_book, boolean guestFlag) {
        Books result = new Books();

        HttpGet request = new HttpGet(URL + "descript_book?id=" + id_book + "&flag=" + guestFlag);
        request.addHeader( "app_key" , UILApplication.AppKey);

        try {
            HttpResponse response = http.execute(request);
            String jsonStr = streamToString(response.getEntity().getContent());

            if (!jsonStr.equals("")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                result = Books.parseJson(jsonObject.getJSONObject("result"));
            }
            else {
                return null;
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Books setViewIncCount(int id_book) {
        Books result = new Books();

        HttpGet request = new HttpGet(URL + "view_inc_count?id=" + id_book);
        request.addHeader( "app_key", UILApplication.AppKey);

        try {
            HttpResponse response = http.execute(request);
            String jsonStr = streamToString(response.getEntity().getContent());

            if (!jsonStr.equals("")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                result = Books.parseJson(jsonObject.getJSONObject("result"));
            }
            else {
                return null;
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static List<Books> getViewIncCount() {
        List<Books> result = new ArrayList<>();

        HttpGet request = new HttpGet(URL + "get_view_inc_count");
        request.addHeader( "app_key", UILApplication.AppKey);

        try {
            HttpResponse response = http.execute(request);
            String jsonStr = streamToString(response.getEntity().getContent());

            if (!jsonStr.equals("")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                if (jsonArray.length() > 0) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Books item = Books.parseJson(jsonArray.getJSONObject(i));
                        if (item != null) {
                            result.add(item);
                        }
                    }
                } else {
                    return null;
                }
            }
            else {
                return null;
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static Books setRatingBook(int id_book, String rating) {
        Books result = new Books();

        HttpGet request = new HttpGet(URL + "set_raiting_book?id=" + id_book + "&rating=" + rating);
        request.addHeader( "app_key", UILApplication.AppKey);

        try {
            HttpResponse response = http.execute(request);
            String jsonStr = streamToString(response.getEntity().getContent());

            if (!jsonStr.equals("")) {
                JSONObject jsonObject = new JSONObject(jsonStr);
                result = Books.parseJson(jsonObject.getJSONObject("result"));
            }
            else {
                return null;
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String streamToString(InputStream stream) {

        InputStreamReader reader = new InputStreamReader(stream);
        BufferedReader buff = new BufferedReader(reader);
        StringBuffer strBuff = new StringBuffer();

        String s;
        try {
            while ((s = buff.readLine()) != null) {
                strBuff.append(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strBuff.toString();
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
