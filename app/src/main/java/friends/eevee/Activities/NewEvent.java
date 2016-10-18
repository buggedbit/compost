package friends.eevee.Activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
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

import friends.eevee.Calender.Constants;
import friends.eevee.Calender.Date;
import friends.eevee.Calender.DateTime;
import friends.eevee.Calender.Time;
import friends.eevee.DB.Def.PersonalEventDef;
import friends.eevee.DB.Helpers.Events;
import friends.eevee.Log.ZeroLog;
import friends.eevee.NewEventUtil.UIPreferences;
import friends.eevee.R;

public class NewEvent extends AppCompatActivity {

    InputUIManager inputUIManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_event);

        this.inputUIManager = new InputUIManager();
        this.inputUIManager.initInputUI();
    }

    private void flagInappropriateInput(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    // TODO : improve this to suite your needs
    private void verifyInput() {
        PersonalEventDef newEntry = new PersonalEventDef();

        /* name */
        String name = inputUIManager.name.getText().toString();
        if (name.matches("")) {
            flagInappropriateInput("What's it called?");
            return;
        } else newEntry.$NAME = name;
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
            newEntry.$START = dateTime.simpleRepresentation();
        }
        /*--*/

        /* duration */
        String duration = inputUIManager.duration_hint.getText().toString();
        // TODO : verify duration
        newEntry.$DURATION = duration;
        /*--*/

        /* comment */
        newEntry.$COMMENT = inputUIManager.comment.getText().toString();
        /*--*/

        /* If all the verifications are OK then insert into DB */
        insertIntoDB(newEntry);
    }

    private void insertIntoDB(PersonalEventDef newEntry) {
        Events eventsDB = new Events(this, Events.DB_NAME, null, Events.DB_VERSION);
        eventsDB.insert(newEntry, Events.TABLES.PERSONAL_EVENTS_TABLE.PERSONAL_EVENTS_TABLE_NAME);
        Log.i(ZeroLog.TAG, " new personal event added [ " + newEntry.get() + "] ");
        Toast.makeText(this," Good to go! ", Toast.LENGTH_SHORT).show();
    }

    class InputUIManager {

        InputMethodManager imm;

        ScrollView new_event;
        LinearLayout new_event_ll_container;
        EditText name;
        TextView start_time, start_date;
        TextView duration_hint;
        SeekBar duration_select;
        EditText comment;
        Button submit;

        public InputUIManager() {

        }

        public void initInputUI() {

            this.imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

            /* new_event scroll view  */
            this.new_event = (ScrollView) findViewById(R.id.new_event);
            /*--*/

            /* new_event_ll_container */
            this.new_event_ll_container = (LinearLayout) new_event.findViewById(R.id.new_event_ll_container);
            this.new_event_ll_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            });
            /*--*/

            /* event name  */
            this.name = (EditText) this.new_event.findViewById(R.id.name);
            /*--*/

            /* start time */
            this.start_time = (TextView) this.new_event.findViewById(R.id.start_time);
            this.start_time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    Time present_time = new Time(true);

                    TimePickerDialog timePickerDialog = new TimePickerDialog(NewEvent.this,
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
            this.start_date = (TextView) this.new_event.findViewById(R.id.start_date);
            this.start_date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                    Date present_date = new Date(true);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(NewEvent.this,
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
            this.duration_hint = (TextView) this.new_event.findViewById(R.id.duration_hint);
            this.duration_select = (SeekBar) this.new_event.findViewById(R.id.duration_select);
            int max = (UIPreferences.MAXIMUM_DURATION - UIPreferences.MINIMUM_DURATION) / UIPreferences.DURATION_STEP;
            this.duration_select.setMax(max);
            int progress = (UIPreferences.DURATION - UIPreferences.MINIMUM_DURATION) / UIPreferences.DURATION_STEP;
            this.duration_select.setProgress(progress);
            this.duration_select.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    UIPreferences.DURATION = UIPreferences.MINIMUM_DURATION + UIPreferences.DURATION_STEP * progress;
                    int hrs = UIPreferences.DURATION / Constants.MINUTES_IN_HOUR;
                    int min = UIPreferences.DURATION % Constants.MINUTES_IN_HOUR;

                    duration_hint.setText(String.valueOf(hrs) + " hrs " + String.valueOf(min) + " min ");
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            /*--*/

            /* comment */
            this.comment = (EditText) new_event.findViewById(R.id.comment);
            /*--*/

            /* submit button */
            this.submit = (Button) new_event.findViewById(R.id.submit);
            this.submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NewEvent.this.verifyInput();
                }
            });
            /*--*/
        }

    }


}
