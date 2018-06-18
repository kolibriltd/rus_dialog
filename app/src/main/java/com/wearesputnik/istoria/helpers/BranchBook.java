package com.wearesputnik.istoria.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class BranchBook {
    public List<BranchCount> branchCount;

    public static BranchBook parseJson(JSONObject json) {
        BranchBook result = new BranchBook();

        result.branchCount = new ArrayList<>();

        try {
            if (json.has("branchCount")) {
                JSONArray jsonArray = json.getJSONArray("branchCount");
                for (int i = 0; i < jsonArray.length(); i++) {
                    result.branchCount.add(BranchCount.parseJson(jsonArray.getJSONObject(i)));
                }
            }

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String jsonGeneration(BranchBook branchBook) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (BranchCount item : branchBook.branchCount) {
                jsonArray.put(BranchCount.jsonGeneration(item));
            }
            jsonObject.put("branchCount", jsonArray);
            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }
}
