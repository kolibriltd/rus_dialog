package com.wearesputnik.istoria.helpers;

import org.json.JSONObject;

/**
 * Created by admin on 30.06.17.
 */
public class ResultInfo {
    public Integer status;
    public String error;
    public UserInfo userInfoResult;

    public static ResultInfo parseJson(JSONObject json, String type) {
        ResultInfo result = new ResultInfo();

        try {
            result.status = json.getInt("status");
            result.error = json.getString("error");
            if (result.status == 0) {
                if (type.trim().equals("login")) {
                    result.userInfoResult = UserInfo.parseJson(json.getJSONObject("result"));
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
