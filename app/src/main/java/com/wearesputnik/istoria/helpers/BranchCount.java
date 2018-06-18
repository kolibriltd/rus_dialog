package com.wearesputnik.istoria.helpers;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by admin on 14.03.2018.
 */

public class BranchCount {
    public Integer indexBranch;
    public Integer indexFloor;
    public Boolean isViewBranch;
    public Integer countMessage;
    public Integer tapCount;

    public static BranchCount parseJson(JSONObject json) {
        BranchCount result = new BranchCount();

        try {
            result.indexBranch = json.getInt("indexBranch");
            result.isViewBranch = json.getBoolean("isViewBranch");
            result.countMessage = json.getInt("countMessage");
            result.indexFloor = json.getInt("indexFloor");
            result.tapCount = json.getInt("tapCount");

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static JSONObject jsonGeneration(BranchCount branchCount) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("indexBranch", branchCount.indexBranch);
            jsonObject.put("isViewBranch", branchCount.isViewBranch);
            jsonObject.put("countMessage", branchCount.countMessage);
            jsonObject.put("indexFloor", branchCount.indexFloor);
            jsonObject.put("tapCount", branchCount.tapCount);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
