package com.harsha.eevee;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ShowTasksTable extends AppCompatActivity {
    TextView dispTasksTableTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_tasks_table);

        Bundle dataFromTasksTable = getIntent().getExtras();
        String dispTableAsString = dataFromTasksTable.getString("tableTasksData");

        dispTasksTableTV = (TextView) findViewById(R.id.dispTasksTableTV);
        dispTasksTableTV.setText(dispTableAsString);
    }
}