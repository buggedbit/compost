package com.harsha.eevee;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ShowEventsTable extends AppCompatActivity {
    TextView dispEventsTableTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_events_table);

        Bundle dataFromEventsTable = getIntent().getExtras();
        String dispTableAsString = dataFromEventsTable.getString("tableEventsData");

        dispEventsTableTV = (TextView) findViewById(R.id.dispEventsTableTV);
        dispEventsTableTV.setText(dispTableAsString);
    }
}