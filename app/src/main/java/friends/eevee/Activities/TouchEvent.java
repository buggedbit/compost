package friends.eevee.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import friends.eevee.Calender.Date;
import friends.eevee.Calender.DateTime;
import friends.eevee.Calender.DateTimeDiff;
import friends.eevee.Calender.Time;
import friends.eevee.DB.Def.EventDef;
import friends.eevee.DB.Def.PersonalEventDef;
import friends.eevee.DB.Helpers.DB;
import friends.eevee.Log.ZeroLog;
import friends.eevee.NewEventUtil.UIPreferences;
import friends.eevee.R;

public class TouchEvent extends AppCompatActivity {

    InputUIManager inputUIManager;
    boolean IS_NEW_EVENT = true;
    EventDef EVENT_IN_FOCUS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.touch_event);

        this.inputUIManager = new InputUIManager();
    }

    private void flagInappropriateInput(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // TODO : improve this to suite your needs
    private void submit() {
        /* Validation */
        if (IS_NEW_EVENT)
            this.EVENT_IN_FOCUS = new PersonalEventDef();

        /* name */
        String name = inputUIManager.name.getText().toString();
        if (name.matches("")) {
            flagInappropriateInput("What's it called?");
            return;
        } else this.EVENT_IN_FOCUS.$NAME = name;
        /*--*/

        /* start_date_time */
        String start_date, start_time;
        start_time = inputUIManager.start_time.getText().toString();
        start_date = inputUIManager.start_date.getText().toString();

        Date date = new Date(start_date, Date.SIMPLE_REPR_SEPARATOR);
        Time time = new Time(start_time, Time.SIMPLE_REPR_SEPARATOR);
        if (!date.isValid() || !time.isValid()) {
            flagInappropriateInput("When is it?");
            return;
        } else {
            DateTime dateTime = new DateTime(date, time);
            this.EVENT_IN_FOCUS.$START = dateTime.simpleRepresentation();
        }
        /*--*/

        /* duration */
        String duration = inputUIManager.duration_hint.getText().toString();
        if (DateTimeDiff.isValidHrMinRepresentation(duration)) {
            flagInappropriateInput("Duration?");
            return;
        }
        this.EVENT_IN_FOCUS.$DURATION = duration;
        /*--*/

        /* comment */
        this.EVENT_IN_FOCUS.set$COMMENT(inputUIManager.comment.getText().toString());
        /*--*/

        /* Inserting or updating */
        if (IS_NEW_EVENT) {
            /* If all the verifications are OK then insert into DB */
            insertInDB(this.EVENT_IN_FOCUS);
        }
        else {
            /* If all the verifications are OK then update into DB */
            updateInDB(this.EVENT_IN_FOCUS);
        }

        Intent time_wall = new Intent(this, TimeWall.class);
        startActivity(time_wall);
    }

    private void insertInDB(EventDef newEntry) {
        DB db = new DB(this, DB.DB_NAME, null, DB.DB_VERSION);
        db.insert(newEntry, DB.TABLES.PERSONAL_EVENTS.TABLE_NAME);
        Log.i(ZeroLog.TAG, " new personal event added [ " + newEntry.toString() + "] ");
        Toast.makeText(this, "Created", Toast.LENGTH_SHORT).show();
    }

    private void updateInDB(EventDef oldEntry) {
        DB db = new DB(this, DB.DB_NAME, null, DB.DB_VERSION);
        db.updateEntryWithKeyValue(oldEntry, DB.PRIMARY_KEY, String.valueOf(oldEntry.$PK));
        Log.i(ZeroLog.TAG, " old personal event updated to [ " + oldEntry.toString() + "] ");
        Toast.makeText(this, "Updated", Toast.LENGTH_SHORT).show();
    }

    class InputUIManager {

        InputMethodManager imm;

        ScrollView touch_event;
        LinearLayout touch_event_ll_container;
        EditText name;
        TextView start_time, start_date;
        TextView duration_hint;
        SeekBar duration_select;
        EditText comment;
        Button duration_increment, duration_decrement;
        Button submit;

        public InputUIManager() {
            this.initInputUI();
        }

        public void initInputUI() {

            this.imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            /* touch_event scroll view  */
            this.touch_event = (ScrollView) findViewById(R.id.touch_event);
            /*--*/

            /* touch_event_ll_container */
            this.touch_event_ll_container = (LinearLayout) touch_event.findViewById(R.id.touch_event_ll_container);
            this.touch_event_ll_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            });
            /*--*/

            /* event name  */
            this.name = (EditText) this.touch_event.findViewById(R.id.name);
            /*--*/

            /* start time */
            this.start_time = (TextView) this.touch_event.findViewById(R.id.start_time);
            this.start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    Time present_time = new Time(true);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(TouchEvent.this,
                            new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                    Time selected = new Time(hourOfDay, minute, 0);
                                    start_time.setText(selected.simpleRepresentation());
                                }
                            },
                            present_time.$HOUR, present_time.$MINUTE, false);

                    timePickerDialog.show();
                }
            });
            /*--*/

            /* start date */
            this.start_date = (TextView) this.touch_event.findViewById(R.id.start_date);
            this.start_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    Date present_date = new Date(true);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(TouchEvent.this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                    Date selected = new Date(year, monthOfYear + 1, dayOfMonth);
                                    start_date.setText(selected.simpleRepresentation());
                                }
                            },
                            present_date.$YEAR, present_date.$MONTH - 1, present_date.$DAY);

                    datePickerDialog.show();
                }
            });
            /*--*/

            /* duration select and hint */
            this.duration_hint = (TextView) this.touch_event.findViewById(R.id.duration_hint);
            DateTimeDiff initial_duration = new DateTimeDiff(UIPreferences.DURATION);
            this.duration_hint.setText(initial_duration.hourMinRepresentation());

            this.duration_select = (SeekBar) this.touch_event.findViewById(R.id.duration_select);
            int max = (UIPreferences.MAXIMUM_DURATION - UIPreferences.MINIMUM_DURATION) / UIPreferences.DURATION_STEP;
            this.duration_select.setMax(max);
            int progress = (UIPreferences.DURATION - UIPreferences.MINIMUM_DURATION) / UIPreferences.DURATION_STEP;
            this.duration_select.setProgress(progress);
            this.duration_select.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    UIPreferences.DURATION = UIPreferences.MINIMUM_DURATION + UIPreferences.DURATION_STEP * progress;
                    DateTimeDiff present_duration = new DateTimeDiff(UIPreferences.DURATION);
                    duration_hint.setText(present_duration.hourMinRepresentation());
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            /*--*/

            /* duration increment and decrement */
            this.duration_increment = (Button) touch_event.findViewById(R.id.duration_increment);
            this.duration_increment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    duration_select.setProgress(duration_select.getProgress() + 1);
                }
            });
            this.duration_decrement = (Button) touch_event.findViewById(R.id.duration_decrement);
            this.duration_decrement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    duration_select.setProgress(duration_select.getProgress() - 1);
                }
            });
            /*--*/

            /* comment */
            this.comment = (EditText) touch_event.findViewById(R.id.comment);
            /*--*/

            /* submit button */
            this.submit = (Button) touch_event.findViewById(R.id.submit);
            this.submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TouchEvent.this.submit();
                }
            });
            /*--*/

            this.fillInput();
        }

        private void fillInput() {
            EVENT_IN_FOCUS = getIntent().getParcelableExtra("EventDef");
            if (EVENT_IN_FOCUS != null) {
                name.setText(EVENT_IN_FOCUS.$NAME);
                DateTime start = new DateTime(EVENT_IN_FOCUS.$START, Date.SIMPLE_REPR_SEPARATOR, Time.SIMPLE_REPR_SEPARATOR, DateTime.SIMPLE_REPR_SEPARATOR);
                start_time.setText(start.$TIME.simpleRepresentation());
                start_date.setText(start.$DATE.simpleRepresentation());
                DateTimeDiff duration = new DateTimeDiff(EVENT_IN_FOCUS.$DURATION);
                UIPreferences.DURATION = (int) duration.minutesDiff();
                int progress = (UIPreferences.DURATION - UIPreferences.MINIMUM_DURATION) / UIPreferences.DURATION_STEP;
                this.duration_select.setProgress(progress);
                duration_hint.setText(EVENT_IN_FOCUS.$DURATION);
                comment.setText(EVENT_IN_FOCUS.get$COMMENT());
                IS_NEW_EVENT = false;
            } else {
                IS_NEW_EVENT = true;
            }
        }

    }

}
