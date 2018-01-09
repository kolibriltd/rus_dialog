package com.wearesputnik.istoria.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 10.10.17.
 */

public class BranchJsonSave {
    public Integer numBranch;

    public static BranchJsonSave parseJson (JSONObject json) {
        BranchJsonSave result = new BranchJsonSave();

        try {
            result.numBranch = json.getInt("numBranch");

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String jsonGenerete (String strNumBranch, int numBranchNew) {
        String jsonStrNew;
        try {
            if (strNumBranch == null) {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("numBranch", numBranchNew);
                jsonArray.put(jsonObject);
                jsonStrNew = jsonArray.toString();
            }
            else {
                List<BranchJsonSave> jsonSaveList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(strNumBranch);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonSaveList.add(BranchJsonSave.parseJson(jsonArray.getJSONObject(i)));
                }
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("numBranch", numBranchNew);
                jsonArray.put(jsonObject);
                jsonStrNew = jsonArray.toString();

            }
            return jsonStrNew;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
