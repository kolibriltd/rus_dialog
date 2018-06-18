package com.wearesputnik.istoria.jsonHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContentTxt {
    public String peopleA;
    public PeopleBmsg peopleB;
    public String context;
    public String metka;
    public String imgPeopleA;
    public PeopleBmsg imgPeopleB;
    public PeopleBmsg videoPeopleB;
    public String videoPeopleA;
    public String callPeopleB;
    public String missCallPeopleB;
    public PeopleBmsg audioPeopleB;
    public String audioPeopleA;
    public List<ContentBook> branch;
    public boolean flags;
    public boolean emptyFlag;

    public static ContentTxt parseJson(JSONObject json) {
        ContentTxt result = new ContentTxt();
        result.branch = new ArrayList<>();
        try {
            if (json.has("peopleA")) {
                result.peopleA = json.getString("peopleA");
            }
            if (json.has("peopleB")) {
                result.peopleB = PeopleBmsg.parseJson(json.getJSONObject("peopleB"));
            }
            if (json.has("context")) {
                result.context = json.getString("context");
            }
            if (json.has("metka")) {
                result.metka = json.getString("metka");
            }
            if (json.has("imgPeopleA")) {
                result.imgPeopleA = json.getString("imgPeopleA");
            }
            if (json.has("imgPeopleB")) {
                result.imgPeopleB =  PeopleBmsg.parseJson(json.getJSONObject("imgPeopleB"));
            }
            if (json.has("videoPeopleB")) {
                result.videoPeopleB = PeopleBmsg.parseJson(json.getJSONObject("videoPeopleB"));
            }
            if (json.has("videoPeopleA")) {
                result.videoPeopleA = json.getString("videoPeopleA");
            }
            if (json.has("callPeopleB")) {
                result.callPeopleB = json.getString("callPeopleB");
            }
            if (json.has("missCallPeopleB")) {
                result.missCallPeopleB = json.getString("missCallPeopleB");
            }
            if (json.has("audioPeopleB")) {
                result.audioPeopleB = PeopleBmsg.parseJson(json.getJSONObject("audioPeopleB"));
            }
            if (json.has("audioPeopleA")) {
                result.audioPeopleA = json.getString("audioPeopleA");
            }
            if (json.has("branch")) {
                JSONArray jsonArray = json.getJSONArray("branch");
                for (int i = 0; i < jsonArray.length(); i++) {
                    result.branch.add(ContentBook.parseJson(jsonArray.getJSONObject(i)));
                }
            }
            else {
                result.flags = false ;
            }
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
