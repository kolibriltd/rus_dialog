package com.wearesputnik.istoria.jsonHelper;

import org.json.JSONObject;

public class PeopleBmsg {
    public String message;
    public Integer indexName;

    public static PeopleBmsg parseJson(JSONObject json) {
        PeopleBmsg result = new PeopleBmsg();

        try {
            if (json.has("message")) {
                result.message = json.getString("message");
            }
            if (json.has("indexName")) {
                result.indexName = json.getInt("indexName");
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
