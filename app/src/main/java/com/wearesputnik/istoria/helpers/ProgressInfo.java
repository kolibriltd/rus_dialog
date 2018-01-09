package com.wearesputnik.istoria.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 24.10.17.
 */

public class ProgressInfo {
    public List<MapProgressInfo> progressInfoList;
    public boolean metkaEnd;
    public boolean isActiveLineOne;
    public boolean isActiveLineTwo;
    public boolean isActiveLineThree;
    public boolean isActiveLineFour;
    public Integer branchFloor;
    public boolean isButtomActiveLineOne;
    public boolean isButtomActiveLineTwo;
    public boolean isButtomActiveLineThree;
    public boolean isButtomActiveLineFour;

    public static ProgressInfo parseJson (JSONObject json) {
        ProgressInfo result = new ProgressInfo();
        result.progressInfoList = new ArrayList<>();

        try {
            JSONArray jsonArray = json.getJSONArray("progressInfoList");
            for (int i = 0; i < jsonArray.length(); i++) {
                result.progressInfoList.add(MapProgressInfo.parseJson(jsonArray.getJSONObject(i)));
            }
            result.metkaEnd = json.getBoolean("metkaEnd");
            result.isActiveLineOne = json.getBoolean("isActiveLineOne");
            result.isActiveLineTwo = json.getBoolean("isActiveLineTwo");
            result.isActiveLineThree = json.getBoolean("isActiveLineThree");
            result.isActiveLineFour = json.getBoolean("isActiveLineFour");
            result.branchFloor = json.getInt("branchFloor");
            result.isButtomActiveLineOne = json.getBoolean("isButtomActiveLineOne");
            result.isButtomActiveLineTwo = json.getBoolean("isButtomActiveLineTwo");
            result.isButtomActiveLineThree = json.getBoolean("isButtomActiveLineThree");
            result.isButtomActiveLineFour = json.getBoolean("isButtomActiveLineFour");

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String jsonGenarationSave(String jsonStrBranchSave, ProgressInfo progressInfo) {
        String jsonStrNew;
        try {
            if (jsonStrBranchSave == null || jsonStrBranchSave.trim().equals("")) {
                JSONArray jsonArray = new JSONArray();
                JSONObject jsonObject = new JSONObject();
                JSONArray jsonImg = new JSONArray();
                for (MapProgressInfo item : progressInfo.progressInfoList) {
                    jsonImg.put(MapProgressInfo.jsonGeneration(item));
                }
                jsonObject.put("progressInfoList", jsonImg);
                jsonObject.put("metkaEnd", progressInfo.metkaEnd);
                jsonObject.put("isActiveLineOne", progressInfo.isActiveLineOne);
                jsonObject.put("isActiveLineTwo", progressInfo.isActiveLineTwo);
                jsonObject.put("isActiveLineThree", progressInfo.isActiveLineThree);
                jsonObject.put("isActiveLineFour", progressInfo.isActiveLineFour);
                jsonObject.put("branchFloor", progressInfo.branchFloor);
                jsonObject.put("isButtomActiveLineOne", progressInfo.isButtomActiveLineOne);
                jsonObject.put("isButtomActiveLineTwo", progressInfo.isButtomActiveLineTwo);
                jsonObject.put("isButtomActiveLineThree", progressInfo.isButtomActiveLineThree);
                jsonObject.put("isButtomActiveLineFour", progressInfo.isButtomActiveLineFour);
                jsonArray.put(jsonObject);
                jsonStrNew = jsonArray.toString();
            }
            else {
                List<ProgressInfo> jsonSaveList = new ArrayList<>();
                JSONArray jsonArray = new JSONArray(jsonStrBranchSave);
                for (int i = 0; i < jsonArray.length(); i++) {
                    jsonSaveList.add(ProgressInfo.parseJson(jsonArray.getJSONObject(i)));
                }
                jsonStrNew = editJsonBranch(jsonSaveList, progressInfo);
            }
            return jsonStrNew;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    private static String editJsonBranch(List<ProgressInfo> jsonSaveList, ProgressInfo progressInfo) {
        String jsonStrNew;
        try {
            boolean editIs = false;
            for (ProgressInfo item : jsonSaveList) {
                if (item.branchFloor == progressInfo.branchFloor) {
                    editIs = true;
                    if (progressInfo.isActiveLineOne) {
                        item.isActiveLineOne = progressInfo.isActiveLineOne;
                    }
                    if (progressInfo.isActiveLineTwo) {
                        item.isActiveLineTwo = progressInfo.isActiveLineTwo;
                    }
                    if (progressInfo.isActiveLineThree) {
                        item.isActiveLineThree = progressInfo.isActiveLineThree;
                    }
                    if (progressInfo.isActiveLineFour) {
                        item.isActiveLineFour = progressInfo.isActiveLineFour;
                    }
                    if (progressInfo.isButtomActiveLineOne) {
                        item.isButtomActiveLineOne = progressInfo.isButtomActiveLineOne;
                    }
                    if (progressInfo.isButtomActiveLineTwo) {
                        item.isButtomActiveLineTwo = progressInfo.isButtomActiveLineTwo;
                    }
                    if (progressInfo.isButtomActiveLineThree) {
                        item.isButtomActiveLineThree = progressInfo.isButtomActiveLineThree;
                    }
                    if (progressInfo.isButtomActiveLineFour) {
                        item.isButtomActiveLineFour = progressInfo.isButtomActiveLineFour;
                    }
                    for (int i = 0; i < item.progressInfoList.size(); i++) {
                        item.progressInfoList.get(i).ImageMap = progressInfo.progressInfoList.get(i).ImageMap;
                        if (progressInfo.progressInfoList.get(i).isActiveImage) {
                            item.progressInfoList.get(i).isActiveImage = progressInfo.progressInfoList.get(i).isActiveImage;
                        }
                    }
                }
            }
            if (!editIs) {
                jsonSaveList.add(progressInfo);
            }
            JSONArray jsonArray = new JSONArray();
            for (ProgressInfo item : jsonSaveList) {
                jsonArray.put(jsonGeneration(item));
            }
            jsonStrNew = jsonArray.toString();
            return jsonStrNew;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    private static JSONObject jsonGeneration(ProgressInfo progressInfo) {
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonImg = new JSONArray();
            for (MapProgressInfo item : progressInfo.progressInfoList) {
                jsonImg.put(MapProgressInfo.jsonGeneration(item));
            }
            jsonObject.put("progressInfoList", jsonImg);
            jsonObject.put("metkaEnd", progressInfo.metkaEnd);
            jsonObject.put("isActiveLineOne", progressInfo.isActiveLineOne);
            jsonObject.put("isActiveLineTwo", progressInfo.isActiveLineTwo);
            jsonObject.put("isActiveLineThree", progressInfo.isActiveLineThree);
            jsonObject.put("isActiveLineFour", progressInfo.isActiveLineFour);
            jsonObject.put("branchFloor", progressInfo.branchFloor);
            jsonObject.put("isButtomActiveLineOne", progressInfo.isButtomActiveLineOne);
            jsonObject.put("isButtomActiveLineTwo", progressInfo.isButtomActiveLineTwo);
            jsonObject.put("isButtomActiveLineThree", progressInfo.isButtomActiveLineThree);
            jsonObject.put("isButtomActiveLineFour", progressInfo.isButtomActiveLineFour);
            return jsonObject;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
