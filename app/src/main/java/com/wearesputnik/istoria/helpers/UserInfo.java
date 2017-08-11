package com.wearesputnik.istoria.helpers;

import org.json.JSONObject;

/**
 * Created by admin on 30.06.17.
 */
public class UserInfo {
    public Integer user_id;
    public String email;
    public String app_key;
    public String firs_name;

    public static UserInfo parseJson(JSONObject json) {
        UserInfo result = new UserInfo();
        try {
            result.user_id = json.getInt("id");
            result.app_key = json.getString("app_key");
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
