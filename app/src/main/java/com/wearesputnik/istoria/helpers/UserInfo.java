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
    public String photo;
    public Boolean subscription;

    public static UserInfo parseJson(JSONObject json) {
        UserInfo result = new UserInfo();
        try {
            result.user_id = json.getInt("id");
            if (json.has("app_key")) {
                result.app_key = json.getString("app_key");
            }
            if (json.has("email")) {
                result.email = json.getString("email");
            }
            if (json.has("display_name")) {
                result.firs_name = json.getString("display_name");
            }
            if (json.has("photo")) {
                result.photo = json.getString("photo");
            }
            if (json.has("subscription")) {
                result.subscription = json.getBoolean("subscription");
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
