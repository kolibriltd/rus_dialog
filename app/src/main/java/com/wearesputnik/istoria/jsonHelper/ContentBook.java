package com.wearesputnik.istoria.jsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContentBook {
    public Integer index;
    public Integer floor;
    public String msg;
    public List<ContentTxt> contentTxt;

    public static ContentBook parseJson(JSONObject json) {
        ContentBook result = new ContentBook();
        result.contentTxt = new ArrayList<>();

        try {
            if (json.has("index")) {
                result.index = json.getInt("index");
            }
            if (json.has("floor")) {
                result.floor = json.getInt("floor");
            }
            if (json.has("msg")) {
                result.msg = json.getString("msg");
            }
            if (json.has("content")) {
                JSONArray jsonArray = json.getJSONArray("content");
                for (int i = 0; i < jsonArray.length(); i++) {
                    result.contentTxt.add(ContentTxt.parseJson(jsonArray.getJSONObject(i)));
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
