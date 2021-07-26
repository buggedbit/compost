package com.harsha.eevee;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ShowOnlineEventsTable extends AppCompatActivity {

    private WebEventDetailsDBHandler web = new WebEventDetailsDBHandler(this, null, null, 1);
    private OnlineEventDetailsDBHandler online = new OnlineEventDetailsDBHandler(this, null, null, 1);
    private TextView t;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_online_events_table);

        t = (TextView) findViewById(R.id.dummy);

    }

    public void dropweb(View view) {
        web.dropTable();
    }

    public void droponline(View view) {
        online.dropTable();
    }

    public void webTable(View view) {
        t.setText(web.viewTable());
    }

    public void onlineTable(View view) {
        t.setText(online.viewTable());
    }
}
