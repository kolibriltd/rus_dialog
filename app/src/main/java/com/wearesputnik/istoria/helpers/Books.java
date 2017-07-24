package com.wearesputnik.istoria.helpers;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 02.06.17.
 */
public class Books {
    public int id_db_local;
    public int id_book;
    public String name;
    public String author;
    public String description;
    public String pathCoverFile;
    public int isViewCount;
    public String raiting;
    public String textInfoList;
    public String pathCoverFileStorage;
    public boolean flagGuest;

    public static Books parseJson(JSONObject json) {
        Books result = new Books();

        try {
            result.id_book = json.getInt("id_book");
            result.name = json.getString("name");
            result.author = json.getString("author");
            if (json.has("description")) {
                result.pathCoverFile = json.getString("path_cover_file");
            }
            if (json.has("description")) {
                result.description = json.getString("description");
            }
            if (json.has("is_view_count")) {
                result.isViewCount = json.getInt("is_view_count");
            }
            if (json.has("raiting")) {
                result.raiting = json.getString("raiting");
            }
            if (json.has("text")) {
                result.textInfoList = json.getString("text");
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
