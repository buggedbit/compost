package com.harsha.eevee;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class WebEventsFetcher {

    private WebEventDetailsDBHandler webDB;

    private URL JSON_ENCODER_URL;
    private String GROUP_TABLE_NAME_POST;
    private String masterString;
    private JSONArray ServerResponse;
    private Context context;

    private String ENCRYPT = "eeVee";

    public WebEventsFetcher(WebEventDetailsDBHandler pass, Context context) {
        webDB = pass;
        this.context = context;

        // TODO : take from UserDetails
        // TODO : make the column names dynamic

        initialize();
    }

    private void initialize() {
        String groupTable = UserDetails.$USERGROUP + ENCRYPT;

        try {
            JSON_ENCODER_URL = new URL(Constants.GROUP_TABLE_JSON_OBJECT);
            GROUP_TABLE_NAME_POST = URLEncoder.encode("group", "UTF-8") + "=" + URLEncoder.encode(groupTable, "UTF-8");
        } catch (UnsupportedEncodingException | MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private boolean isNetworkOkay() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void fetchDataIntoServerResponse() {
        if (!isNetworkOkay()) return;

        try {
            // OPENING CONN AND SETTING REQUEST METHOD TO POST
            HttpURLConnection conn = (HttpURLConnection) JSON_ENCODER_URL.openConnection();
            conn.setConnectTimeout(5000);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");

            //POST GROUP TABLE NAME TO SERVER
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            wr.write(GROUP_TABLE_NAME_POST);
            wr.flush();

            //GET THE TABLE DATA IN FORM OF JSON
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }

            bufferedReader.close();
            conn.disconnect();

            masterString = stringBuilder.toString();
            JSONObject wholeData = new JSONObject(masterString);
            ServerResponse = (JSONArray) wholeData.get("server_response");

            // TOTAL JSON DATA IN 'ServerResponse'
            //Log.i("yash" , masterString);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    public void fillUpIntoWebEventsDataTable() {
        if (masterString == null || ServerResponse == null) return;

        webDB.dropTable();

        try {
            int noRows = ServerResponse.length();
            for (int i = 0; i < noRows; i++) {
                addRowToWebEventsDataTable(ServerResponse.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addRowToWebEventsDataTable(JSONObject jsonObject) {
        if (jsonObject == null) return;

        WebEventDetails newRow = new WebEventDetails();

        try {
            newRow.set_TimeStamp(jsonObject.getString("LastTimeStamp"));

            newRow.set_eeVeeID(jsonObject.getString("eeVeeID"));
            newRow.set_EventName(jsonObject.getString("Name"));
            newRow.set_EventPlace(jsonObject.getString("Place"));

            // Parsing START END DEADLINE Date times, converting them to strings and inserting them into table
            String webDate = jsonObject.getString("stDate");
            String webTime = jsonObject.getString("stTime");
            int duration = Integer.parseInt(jsonObject.getString("Duration"));

            DateTime START = new DateTime(webDate + " " + webTime, 0);
            DateTime END = DateTime.getDateTime(START, duration);

            String startDateTime = START.toString();
            String endDateTime = END.toString();

            newRow.set_StartDateTime(startDateTime);
            newRow.set_EndDateTime(endDateTime);
            // PARSING START AND END IS DONE

            String[] preZero = {"0", "0", "0", "0", "0", "0"};
            int repInt = Integer.parseInt(jsonObject.getString("Frequency"));

            StringBuilder repSB = new StringBuilder();

            int divisor = 10;
            for (int i = 0; i < 7; i++) {
                if (repInt / divisor == 0) {
                    // implies repInt is i+1-digited
                    for (int j = 6; j >= i + 1; j--) {
                        repSB.append(preZero[j - 1]);
                    }
                    repSB.append(jsonObject.getString("Frequency"));
                    break;
                }
                divisor *= 10;
            }
            String repetition = repSB.toString();
            newRow.set_Repetition(repetition);

            // PARSING REPETITION IS

            String DDate = jsonObject.getString("regDDate");
            String DTime = jsonObject.getString("regDTime");

            DateTime REG_DEADLINE = new DateTime(DDate + " " + DTime, 0);

            String deadlineDateTime;
            if (!REG_DEADLINE.isSetByString()) deadlineDateTime = Constants.REGNDATETIME_NOTSET;
            else deadlineDateTime = REG_DEADLINE.toString();

            newRow.set_DeadlineDateTime(deadlineDateTime);
            // PARSING REG DEADLINE DONE

            // ALL PARSINGS DONE

            String regPlace = jsonObject.getString("regPlace");
            String regWebsite = jsonObject.getString("regWebsite");
            String comment = jsonObject.getString("Comment");

            if (regPlace.matches("") || regPlace == null) regPlace = Constants.REGNPLACE_NOTSET;
            if (regWebsite.matches("") || regWebsite == null) regWebsite = Constants.WEBSITE_NOTSET;
            if (comment.matches("") || comment == null) comment = Constants.COMMENTS_NOTSET;

            newRow.set_Regn_Place(regPlace);
            newRow.set_Website(regWebsite);
            newRow.set_Comments(comment);


            newRow.set_Type(jsonObject.getString("Type"));
            newRow.set_Status(jsonObject.getString("Status"));
            newRow.set_ClubName(jsonObject.getString("clubName"));

            // TODO : implement these
            String startsFrom;
            String endOn;

            if (repetition.matches("0000000")) {
                startsFrom = Constants.STARTS_FROM_NOT_APPLICABLE;
                endOn = Constants.ENDS_ON_NOT_APPLICABLE;
            } else {
                startsFrom = Constants.STARTS_FROM_NOTSET;
                endOn = Constants.ENDS_ON_NOTSET;
            }

            newRow.set_StartsFromDailyOrWeekly(startsFrom);
            newRow.set_EndsFromDailyOrWeekly(endOn);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        webDB.insertRow(newRow);
    }

}