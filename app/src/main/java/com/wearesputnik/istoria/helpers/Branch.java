package com.wearesputnik.istoria.helpers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 25.07.17.
 */
public class Branch {
    public String message;
    public List<TextInfo> content;

    public static Branch parseJson(JSONObject json) {
        Branch result = new Branch();
        result.content = new ArrayList<>();

        try {
            result.message = json.getString("message");
            JSONArray jsonArray = json.getJSONArray("content");
            for (int i = 0; i < jsonArray.length(); i++) {
                result.content.add(TextInfo.parseJson(jsonArray.getJSONObject(i)));
            }

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
