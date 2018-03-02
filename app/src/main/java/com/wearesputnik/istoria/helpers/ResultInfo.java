package com.wearesputnik.istoria.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 30.06.17.
 */
public class ResultInfo {
    public Integer status;
    public String error;
    public UserInfo userInfoResult;
    public List<Books> booksList;
    public SyncUser dataSync;

    public static ResultInfo parseJson(JSONObject json, String type) {
        ResultInfo result = new ResultInfo();

        try {
            result.status = json.getInt("status");
            result.error = json.getString("error");
            if (result.status == 0) {
                if (type.trim().equals("login")) {
                    JSONObject jsonObject = json.getJSONObject("result");
                    result.userInfoResult = UserInfo.parseJson(jsonObject.getJSONObject("login"));
                    if (jsonObject.has("dataSync")) {
                        result.dataSync = SyncUser.parseJson(jsonObject.getJSONObject("dataSync"));
                    }
                    else {
                        result.dataSync = null;
                    }

                }
                if (type.trim().equals("listBook")) {
                    result.booksList = new ArrayList<>();
                    JSONArray jsonArray = json.getJSONArray("result");
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            Books item = Books.parseJson(jsonArray.getJSONObject(i));
                            if (item != null) {
                                result.booksList.add(item);
                            }
                        }
                    }
                }
                if (type.trim().equals("dataSync")) {
                    result.dataSync = SyncUser.parseJson(json.getJSONObject("result"));
                }
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
