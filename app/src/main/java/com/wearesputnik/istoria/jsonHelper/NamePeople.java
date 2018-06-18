package com.wearesputnik.istoria.jsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NamePeople {
    public String nameA;
    public List<ArrayNameB> nameB;

    public static NamePeople parseJson(JSONObject json) {
        NamePeople result = new NamePeople();
        result.nameB = new ArrayList<>();

        try {
            if (json.has("nameA")) {
                result.nameA = json.getString("nameA");
            }
            if (json.has("nameB")) {
                JSONArray jsonArray = json.getJSONArray("nameB");
                for (int i = 0; i < jsonArray.length(); i++) {
                    result.nameB.add(ArrayNameB.parseJson(jsonArray.getJSONObject(i)));
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
