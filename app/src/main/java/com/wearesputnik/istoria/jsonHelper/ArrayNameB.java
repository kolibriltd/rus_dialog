package com.wearesputnik.istoria.jsonHelper;

import org.json.JSONObject;

public class ArrayNameB {
    public String nameB;

    public static ArrayNameB parseJson(JSONObject json) {
        ArrayNameB result = new ArrayNameB();

        try {
            result.nameB = json.getString("nameB");

            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
