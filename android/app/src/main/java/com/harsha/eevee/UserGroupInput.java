package com.harsha.eevee;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

public class UserGroupInput extends AppCompatActivity {

    OnlineEventDetailsDBHandler onlineEventDetailsDBHandler = new OnlineEventDetailsDBHandler(this, null, null, 1);
    UserDetailsDBHandler userDetailsDBHandler = new UserDetailsDBHandler(this, null, null, 1);
    RadioGroup.LayoutParams RGparams = new RadioGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    RadioGroup MASTER;
    int radioButtonId = 0;
    private String prevChecked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_group_input);

        MASTER = (RadioGroup) findViewById(R.id.MASTER);
        plantGroupFamilyTree(Constants.$HOME);

        autofill();
    }

    private void autofill() {

        UserDetails userDetails = userDetailsDBHandler.getUserDetails();
        if (userDetails == null) return;

        String userGroup = userDetails.get_Subscription();
        if (userGroup.matches("")) return;

        prevChecked = userGroup;

        int noGroups = MASTER.getChildCount();
        for (int i = 0; i < noGroups; i++) {
            RadioButton iTH = (RadioButton) MASTER.findViewById(i);
            if (iTH.getText().toString().matches(userGroup)) {
                iTH.setChecked(true);
                return;
            }
        }
    }

    // TODO : To check the server status and prevent app crash
    private void plantGroupFamilyTree(JSONArray jsonArray) {
        try {
            RadioButton radioButton = new RadioButton(this);
            radioButton.setId(radioButtonId);
            radioButtonId++;
            radioButton.setText(jsonArray.getString(0));
            MASTER.addView(radioButton, RGparams);


            int noChildren = jsonArray.length();

            for (int i = 1; i < noChildren; i++) {
                plantGroupFamilyTree((JSONArray) jsonArray.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void goToHome(View view) {
        boolean groupSelected = false;

        int noGroups = MASTER.getChildCount();
        for (int i = 0; i < noGroups; i++) {
            RadioButton iTH = (RadioButton) MASTER.findViewById(i);
            if (iTH.isChecked()) {
                if (prevChecked != null) {
                    if (iTH.getText().toString().matches(prevChecked)) {
                        // not changed
                        // no need to drop online events
                    } else {
                        // changed
                        onlineEventDetailsDBHandler.dropTable();
                    }
                    userDetailsDBHandler.updateThisInFirstRow(UserDetailsDBHandler.COLUMN_SUBSCRIPTION, iTH.getText().toString());
                    groupSelected = true;
                    break;
                } else {
                    userDetailsDBHandler.updateThisInFirstRow(UserDetailsDBHandler.COLUMN_SUBSCRIPTION, iTH.getText().toString());
                    groupSelected = true;
                    break;
                }
            }
        }

        if (!groupSelected) {
            Toast.makeText(this, "The group really comes in handy", Toast.LENGTH_LONG).show();
            return;
        }

        Intent eventsHome = new Intent(this, Home.class);
        startActivity(eventsHome);
    }

}

