package com.harsha.eevee;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ShowUsersTable extends AppCompatActivity {
    TextView displayTableTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_users_table);

        Bundle dataFromUsersTable = getIntent().getExtras();
        String dispTableAsString = dataFromUsersTable.getString("tableUsersData");

        displayTableTV = (TextView) findViewById(R.id.dispalyTableTV);
        displayTableTV.setText(dispTableAsString);
    }
}