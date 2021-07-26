package com.harsha.eevee;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AppSettings extends AppCompatActivity {

    private Button userInfo, interfaceTour, filtersBTN;
    private boolean userDetailsThreadRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_settings);

        userInfo = (Button) findViewById(R.id.userInfo);
        userInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGroupFamilyServerTo$HOME();
            }
        });

        interfaceTour = (Button) findViewById(R.id.interfaceTour);
        interfaceTour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appTour = new Intent(AppSettings.this, AppTour.class);
                startActivity(appTour);
            }
        });

        filtersBTN = (Button) findViewById(R.id.filtersBTN);
        filtersBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent filters = new Intent(AppSettings.this, SetEventFilters.class);
                filters.putExtra("TO FILTERS ACTIVITY", "FROM SETTINGS");
                startActivity(filters);
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton settingsFab = (FloatingActionButton) findViewById(R.id.settingsFab);
        settingsFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent goToHome = new Intent(AppSettings.this, Home.class);
                startActivity(goToHome);
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(AppSettings.this, Home.class);
        startActivity(intent);
    }

    /**
     * Assumption : the parent is named server_response
     * Hard coded url
     */
    public void getGroupFamilyServerTo$HOME() {
        final Handler ServerProblem = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(AppSettings.this, "I don't sense any Servers , try again in a little bit", Toast.LENGTH_LONG).show();
            }
        };

        Runnable getFamilyTreeFromServerTo$HOME = new Runnable() {
            @Override
            public void run() {

                URL GROUP_FAMILY_TREE = null;
                try {
                    // OPENING CONN AND SETTING REQUEST METHOD TO POST
                    String urlString = Constants.GROUP_FAMILY_TREE_JSON_OBJECT;
                    GROUP_FAMILY_TREE = new URL(urlString);
                    HttpURLConnection conn = (HttpURLConnection) GROUP_FAMILY_TREE.openConnection();
                    conn.setConnectTimeout(5000);

                    //GET THE TABLE DATA IN FORM OF JSON
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder stringBuilder = new StringBuilder();
                    String line;

                    while ((line = bufferedReader.readLine()) != null) {
                        stringBuilder.append(line).append("\n");
                    }

                    bufferedReader.close();
                    conn.disconnect();
                    JSONObject wholeData = new JSONObject(stringBuilder.toString());
                    Constants.$HOME = (JSONArray) wholeData.get("server_response");

                    userDetailsThreadRunning = false;

                    Intent userInfo = new Intent(AppSettings.this, UserDetailsInput.class);
                    startActivity(userInfo);

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                    ServerProblem.sendEmptyMessage(0);
                    userDetailsThreadRunning = false;
                }


            }
        };

        Thread t = new Thread(getFamilyTreeFromServerTo$HOME);
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            Toast.makeText(this, "Connecting to Severs", Toast.LENGTH_SHORT).show();
            t.start();
            userDetailsThreadRunning = true;
        } else {
            Toast.makeText(this, "We will need network now", Toast.LENGTH_LONG).show();
            userDetailsThreadRunning = false;
        }
    }
}