package com.wearesputnik.istoria.helpers;

import org.json.JSONObject;

/**
 * Created by admin on 05.06.17.
 */
public class TextInfo {
    public String nameA;
    public String nameB;
    public String peopleA;
    public String peopleB;
    public String context;
    public String metka;
    public boolean flags;
    public boolean emptyFlag;

    public static TextInfo parseJson(JSONObject json) {
        TextInfo result = new TextInfo();
        try {
            if (json.has("nameA")) {
                result.nameA = json.getString("nameA");
            }
            if (json.has("nameB")) {
                result.nameB = json.getString("nameB");
            }
            if (json.has("peopleA")) {
                result.peopleA = json.getString("peopleA");
            }
            if (json.has("peopleB")) {
                result.peopleB = json.getString("peopleB");
            }
            if (json.has("context")) {
                result.context = json.getString("context");
            }
            if (json.has("metka")) {
                result.metka = json.getString("metka");
            }
            result.flags = false;
            result.emptyFlag = false;
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
