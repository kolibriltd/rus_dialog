package com.wearesputnik.istoria.helpers;

import com.activeandroid.query.Select;
import com.wearesputnik.istoria.models.BookModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 28.02.2018.
 */

public class SyncUser {
    public Integer points;
    public String status;
    public List<SyncBook> syncBookList;

    public static SyncUser parseJson(JSONObject jsonObject) {
        SyncUser result = new SyncUser();
        result.syncBookList = new ArrayList<>();

        try {
            result.points = jsonObject.getInt("points");
            result.status = jsonObject.getString("status");

            JSONArray jsonArray = jsonObject.getJSONArray("syncBookList");
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    SyncBook item = SyncBook.parseJson(jsonArray.getJSONObject(i));
                    if (item != null) {
                        result.syncBookList.add(item);
                    }
                }
            }

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String jsonGeneration() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("points", 0);
            jsonObject.put("status", "status");

            JSONArray jsonArray = new JSONArray();
            List<BookModel> bookModelList = new Select().from(BookModel.class).execute();
            if (bookModelList != null && bookModelList.size() != 0) {
                for (BookModel item : bookModelList) {
                    jsonArray.put(SyncBook.jsonGeneration(item));
                }
            }
            jsonObject.put("syncBookList", jsonArray);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
