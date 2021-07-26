package com.harsha.eevee;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.OvershootInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class WelcomeScreen extends AppCompatActivity {

    private TextView eone, etwo, V, efour, efive;

    private Handler trigger = new Handler();
    private int totalLength;
    private int endOfRun;
    private int startOfRun;
    private int runLength;
    private int indLength;

    private int dur = 1000;
    private float scale = 1.2f;

    private Runnable eone_run = new Runnable() {
        @Override
        public void run() {
            eone.animate()
                    .translationXBy(runLength - 1 * indLength)
                    .setDuration(dur)
                    .setInterpolator(new OvershootInterpolator())
                    .scaleX(scale)
                    .scaleY(scale)
                    .rotation(360);
        }
    };

    private Runnable etwo_run = new Runnable() {
        @Override
        public void run() {
            etwo.animate()
                    .translationXBy(runLength - 2 * indLength)
                    .setDuration(dur * 4 / 5)
                    .setInterpolator(new OvershootInterpolator())
                    .scaleX(scale)
                    .scaleY(scale)
                    .rotation(360);
        }
    };

    private Runnable V_run = new Runnable() {
        @Override
        public void run() {
            V.animate()
                    .translationXBy(runLength - 3 * indLength)
                    .setDuration(dur * 3 / 5)
                    .setInterpolator(new OvershootInterpolator())
                    .scaleX(scale)
                    .scaleY(scale)
                    .rotation(360);
        }
    };

    private Runnable efour_run = new Runnable() {
        @Override
        public void run() {
            efour.animate()
                    .translationXBy(runLength - 4 * indLength)
                    .setDuration(dur * 2 / 5)
                    .setInterpolator(new OvershootInterpolator())
                    .scaleX(scale)
                    .scaleY(scale)
                    .rotation(0);
        }
    };

    private Runnable efive_run = new Runnable() {
        @Override
        public void run() {
            efive.animate()
                    .translationXBy(runLength - 5 * indLength)
                    .setDuration(dur * 1 / 5)
                    .setInterpolator(new OvershootInterpolator())
                    .scaleX(scale)
                    .scaleY(scale)
                    .rotation(0);
        }
    };

    /**
     * Vibration part now .............................................................
     */

    private int dX = 100;
    //    private int noVibrations = 10;
    private int durVib = 1000;

    private Runnable eone_gotoside = new Runnable() {
        @Override
        public void run() {
            eone.animate().translationXBy(dX).setInterpolator(new OvershootInterpolator());
        }
    };

    private Runnable etwo_gotoside = new Runnable() {
        @Override
        public void run() {
            etwo.animate().translationXBy(dX).setInterpolator(new OvershootInterpolator());
        }
    };

    private Runnable V_gotoside = new Runnable() {
        @Override
        public void run() {
//            Animation shake = AnimationUtils.loadAnimation(getApplicationContext() , R.anim.shaking);
//            V.startAnimation(shake);
            V.animate()
                    .scaleX(2.5f)
                    .scaleY(2.5f);
            V.setText("V");
        }
    };

    private Runnable efour_gotoside = new Runnable() {
        @Override
        public void run() {
            efour.animate().translationXBy(-dX).setInterpolator(new OvershootInterpolator());
        }
    };

    private Runnable efive_gotoside = new Runnable() {
        @Override
        public void run() {
            efive.animate().translationXBy(-dX).setInterpolator(new OvershootInterpolator());
        }
    };

    /**
     *
     * */

    private boolean userDetailsThreadRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_screen);

        TextView eeVee = (TextView) findViewById(R.id.eeVee);
        Animation eeVee_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.welcome_screen_anim);
        assert eeVee != null;
        eeVee.startAnimation(eeVee_animation);

//        DisplayMetrics displayMetrics = new DisplayMetrics();
//        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
//        wm.getDefaultDisplay().getMetrics(displayMetrics);
//
//        totalLength = displayMetrics.widthPixels;
//        int totalHeight = displayMetrics.heightPixels;
//
//        endOfRun   = totalLength * 5 / 6;
//        startOfRun = totalLength * 1 / 6;
//        runLength = endOfRun - startOfRun;
//
//        indLength = runLength * 1 / 5;
//
//        eone = (TextView) findViewById(R.id.eone);
//        etwo = (TextView) findViewById(R.id.etwo);
//        V = (TextView) findViewById(R.id.V);
//        efour = (TextView) findViewById(R.id.efour);
//        efive = (TextView) findViewById(R.id.efive);
//
//        RelativeLayout.LayoutParams setThisToAll = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT , ViewGroup.LayoutParams.WRAP_CONTENT);
//        setThisToAll.setMargins(startOfRun  , totalHeight / 2 , 0 , 0 );
//
//        eone.setLayoutParams(setThisToAll);
//        etwo.setLayoutParams(setThisToAll);
//        V.setLayoutParams(setThisToAll);
//        efour.setLayoutParams(setThisToAll);
//        efive.setLayoutParams(setThisToAll);
//
//        startWelcomeAnimation();
    }

    private void startWelcomeAnimation() {
        // run
        trigger.postDelayed(eone_run, 0);
        trigger.postDelayed(etwo_run, 100);
        trigger.postDelayed(V_run, 200);
        trigger.postDelayed(efour_run, 300);
        trigger.postDelayed(efive_run, 400);

        int delay = 1200;
        // go to side
        trigger.postDelayed(eone_gotoside, delay);
        trigger.postDelayed(etwo_gotoside, delay);
        trigger.postDelayed(V_gotoside, delay);
        trigger.postDelayed(efour_gotoside, delay);
        trigger.postDelayed(efive_gotoside, delay);

        Animation eeVee_animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.welcome_screen_anim);
        eone.startAnimation(eeVee_animation);
        etwo.startAnimation(eeVee_animation);
        V.startAnimation(eeVee_animation);
        efour.startAnimation(eeVee_animation);
        efive.startAnimation(eeVee_animation);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Hiding status , action and nav bar of the activity

        //hide Action bar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }

        //Hiding other bars
        Handler hideAllBarsHandler = new Handler();
        Runnable hideAllBarsRunnable = new Runnable() {
            @Override
            public void run() {
                RelativeLayout welcomeScreen = (RelativeLayout) findViewById(R.id.welcomeScreen);
                if (welcomeScreen != null) {
                    welcomeScreen.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
                }
            }
        };

        hideAllBarsHandler.postDelayed(hideAllBarsRunnable, 300);
    }

    public void performStartUpCheck(View view) {

        // TODO : weaken the check

        boolean userDetailsAllSet = userDetailsCheck();

        if (userDetailsAllSet) {
            Intent nextAc = new Intent(this, Home.class);
            startActivity(nextAc);
        } else {

            if (userDetailsThreadRunning) return;

            final Handler ServerProblem = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Toast.makeText(WelcomeScreen.this, "I don't sense any Servers , try again in a little bit", Toast.LENGTH_LONG).show();
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

                        Intent nextAc = new Intent(WelcomeScreen.this, UserDetailsInput.class);
                        startActivity(nextAc);

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

    // TODO : overriding on back press where ever needed
    // TODO : Transition bug from events to tasks

    public boolean userDetailsCheck() {

        UserDetailsDBHandler dbHandler = new UserDetailsDBHandler(this, null, null, 1);

        if (dbHandler.isAllSet()) {
            dbHandler.setStaticUserDetails();
            return true;
        } else {
            return false;
        }
    }

    // TODO : DUMMY FUNCTION REMOVE IT AFTERWARDS
    public void go(View view) {
        Intent nextAc = new Intent(this, UserDetailsInput.class);
        startActivity(nextAc);
    }
}