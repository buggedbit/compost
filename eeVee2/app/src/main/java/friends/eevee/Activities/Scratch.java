package friends.eevee.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import friends.eevee.Activities.TimeWall.TimeWall;
import friends.eevee.Activities.TouchEvent.TouchEvent;
import friends.eevee.R;

public class Scratch extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scratch);

//        DB events_db_helper = new Exvents(this, DB.DB_NAME, null, DB.DB_VERSION);
//        PersonalEventDef eventDef = new PersonalEventDef();
//
//        eventDef.$NAME = "yash";
//        eventDef.$START = "now";
//        eventDef.$DURATION = "later";
//        eventDef.$COMMENT = "comment";
//
//        events_db_helper.insert(eventDef,DB.TABLES.PERSONAL_EVENTS.TABLE_NAME);
//        EventDef eventDef1 = events_db_helper.getFirstEntryWithKeyValue(DB.TABLES.PERSONAL_EVENTS.TABLE_NAME,DB.TABLES.PERSONAL_EVENTS.NAME,"yash");
//
//        Log.i(ZeroLog.TAG, String.valueOf(events_db_helper.numberOfEntries(DB.TABLES.PERSONAL_EVENTS.TABLE_NAME)));
//        Log.i(ZeroLog.TAG, eventDef1.toString());


//        RelativeLayout stack = (RelativeLayout) findViewById(R.id.stack);
//        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(500, 100);
//        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(500, 500);
//        layoutParams.setMargins(100,100,0,0);
//        layoutParams2.setMargins(200,400,0,0);
//
//        EventStub eventStub1 = new EventStub(this);
//        eventStub1.setText("hello");
//        eventStub1.setLayoutParams(layoutParams);
//        EventStub eventStub2 = new EventStub(this);
//        eventStub2.setText("world");
//        eventStub2.setLayoutParams(layoutParams2);
//
//        stack.addView(eventStub1);
//        stack.addView(eventStub2);

    }

    public void timeWall(View view){
        Intent intent = new Intent(this,TimeWall.class);
        startActivity(intent);
    }

    public void got_new(View view){
        Intent intent = new Intent(this,TouchEvent.class);
        startActivity(intent);
    }
}
