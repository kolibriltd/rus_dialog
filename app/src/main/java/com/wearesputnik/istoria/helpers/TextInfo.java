package com.wearesputnik.istoria.helpers;

import com.wearesputnik.istoria.jsonHelper.ContentBook;
import com.wearesputnik.istoria.jsonHelper.NamePeople;

import org.json.JSONObject;


/**
 * Created by admin on 05.06.17.
 */

public class TextInfo {

    public NamePeople namePeople;
    public ContentBook contentBook;

    public static TextInfo parseJson(JSONObject json) {
        TextInfo result = new TextInfo();

        try {
            if (json.has("namePeople")) {
                result.namePeople = NamePeople.parseJson(json.getJSONObject("namePeople"));
            }
            if (json.has("contentBook")) {
                result.contentBook = ContentBook.parseJson(json.getJSONObject("contentBook"));
            }


            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
