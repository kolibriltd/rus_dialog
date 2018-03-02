package com.wearesputnik.istoria.helpers;

import com.wearesputnik.istoria.models.BookModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 28.02.2018.
 */

public class SyncBook {
    public Integer id_book;
    public boolean is_view;
    public Integer is_view_tap_count;
    public String branch_json_save;
    public String branch_json_end;
    public String branch_json;

    public static SyncBook parseJson(JSONObject jsonObject) {
        SyncBook result = new SyncBook();

        try {
            if (jsonObject.has("id_book")) {
                result.id_book = jsonObject.getInt("id_book");
            }
            if (jsonObject.has("is_view")) {
                result.is_view = jsonObject.getBoolean("is_view");
            }
            if (jsonObject.has("is_view_tap_count")) {
                result.is_view_tap_count = jsonObject.getInt("is_view_tap_count");
            }
            if (jsonObject.has("branch_json_save")) {
                result.branch_json_save = jsonObject.getString("branch_json_save");
            }
            if (jsonObject.has("branch_json_end")) {
                result.branch_json_end = jsonObject.getString("branch_json_end");
            }
            if (jsonObject.has("branch_json")) {
                result.branch_json = jsonObject.getString("branch_json");
            }

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static JSONObject jsonGeneration(BookModel item) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("id_book", item.IdDbServer);
            jsonObject.put("is_view", item.IsView);
            jsonObject.put("is_view_tap_count", item.IsViewTapCount);
            jsonObject.put("branch_json_save", item.BranchJsonSave);
            jsonObject.put("branch_json_end", item.BranchJsonEnd);
            jsonObject.put("branch_json", item.BranchJson);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
