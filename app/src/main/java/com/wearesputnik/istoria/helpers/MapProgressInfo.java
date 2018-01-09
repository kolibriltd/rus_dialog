package com.wearesputnik.istoria.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 23.10.17.
 */

public class MapProgressInfo {
    public String ImageMap;
    public boolean isActiveImage;

    public static MapProgressInfo parseJson (JSONObject json) {
        MapProgressInfo result = new MapProgressInfo();

        try {
            result.ImageMap = json.getString("ImageMap");
            result.isActiveImage = json.getBoolean("isActiveImage");

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static JSONObject jsonGeneration(MapProgressInfo mapProgressInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            if (mapProgressInfo.ImageMap == null) {
                jsonObject.put("ImageMap", "");
            }
            else {
                jsonObject.put("ImageMap", mapProgressInfo.ImageMap);
            }
            jsonObject.put("isActiveImage", mapProgressInfo.isActiveImage);

            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
