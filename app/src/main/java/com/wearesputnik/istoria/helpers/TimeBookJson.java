package com.wearesputnik.istoria.helpers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 26.02.2018.
 */

public class TimeBookJson {
    public long timeLocal;
    public String timeMin;
    public String timeSec;
    public boolean stopTimer;

    public static String setJsonTime(String timeMinTxt, String timeSecTxt) {
        String jsonStrNew;
        try {
            //JSONArray jsonArray = new JSONArray();
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("timeLocal", new Date(new Date().getTime() + (30 * 60000)).getTime());
            jsonObject.put("timeMinTxt", timeMinTxt);
            jsonObject.put("timeSecTxt", timeSecTxt);
            //jsonArray.put(jsonObject);
            jsonStrNew = jsonObject.toString();
            return jsonStrNew;
        } catch (JSONException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static TimeBookJson getJsonTime(JSONObject jsonObject) {
        TimeBookJson result = new TimeBookJson();

        try {
            result.timeLocal = jsonObject.getLong("timeLocal");
            result.timeMin = jsonObject.getString("timeMinTxt");
            result.timeSec = jsonObject.getString("timeSecTxt");
            return result;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static TimeBookJson TimeOut(String TimerStopMin) {
        TimeBookJson timeBookJson = new TimeBookJson();
        try {
            timeBookJson = TimeBookJson.getJsonTime(new JSONObject(TimerStopMin));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (timeBookJson != null) {
            long dateDifferent = (timeBookJson.timeLocal - new Date().getTime());
            long secondsInMilli = 1000;
            long minutesInMilli = secondsInMilli * 60;
            long hoursInMilli = minutesInMilli * 60;
            long daysInMilli = hoursInMilli * 24;

            long elapsedDays = dateDifferent / daysInMilli;
            dateDifferent = dateDifferent % daysInMilli;

            long elapsedHours = dateDifferent / hoursInMilli;
            dateDifferent = dateDifferent % hoursInMilli;

            long elapsedMinutes = dateDifferent / minutesInMilli;
            dateDifferent = dateDifferent % minutesInMilli;

            long elapsedSeconds = dateDifferent / secondsInMilli;

            if (elapsedHours < 1 && elapsedDays == 0) {

                if (elapsedMinutes <= 0) {
                    if (elapsedSeconds <= 0) {
                        timeBookJson.stopTimer = true;
                        return timeBookJson;
                    } else {
                        timeBookJson.timeMin = "00";
                        if (elapsedSeconds < 10) {
                            timeBookJson.timeSec = "0" + elapsedSeconds;
                        } else {
                            timeBookJson.timeSec = elapsedSeconds + "";
                        }
                    }
                } else {
                    if (elapsedMinutes < 10) {
                        timeBookJson.timeMin = "0" + elapsedMinutes;
                    } else {
                        timeBookJson.timeMin = elapsedMinutes + "";
                    }
                    if (elapsedSeconds < 10) {
                        timeBookJson.timeSec = "0" + elapsedSeconds;
                    } else {
                        timeBookJson.timeSec = elapsedSeconds + "";
                    }
                }
            }
            else {
                timeBookJson.stopTimer = true;
                return timeBookJson;
            }
        }
        else {
            timeBookJson.stopTimer = false;
        }

        return timeBookJson;
    }
}
