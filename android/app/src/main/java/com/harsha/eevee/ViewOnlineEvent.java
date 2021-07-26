package com.harsha.eevee;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;

public class ViewOnlineEvent extends AppCompatActivity {

    OnlineEventDetailsDBHandler dbHandler;
    TimeInput startTimeEvent, endTimeEvent, startTimeEventA, endTimeEventA, regnTimeEvent;
    DateInput startDateEvent, endDateEvent, startDateEventB, endDateEventB, regnDateEvent;
    DurationInput durationEvent;

    boolean selectingTimeDoneEventA;
    String startDateStoreStringEventA, endDateStoreStingEventA;

    EditText eventNameET, eventPlaceET, startDateEventET, startTimeEventET, endDateEventET, endTimeEventET, startDateEventBET,
            endDateEventBET, startTimeEventAET, endTimeEventAET, durationEventAET, regnPlaceEventET, regnDateEventET,
            regnTimeEventET, regnWebsiteEventET, commentsEventET, clubNameET;

    Button viewEventsTableBTN, dropEventsTableBTN;

    RadioGroup repetitionEventRG, typeEventRG;

    RadioButton noneEventRB, dailyEventRB, weeklyEventRB;

    CheckBox sundayEventCB, mondayEventCB, tuesdayEventCB, wednesdayEventCB, thursdayEventCB, fridayEventCB, saturdayEventCB;

    LinearLayout layoutEventThree, layoutEventThreeA, layoutEventThreeB, dayEventCheckboxGrp, layoutEventFourA,
            buttonPanelEventSix, eventViewOnlinePanelLL, buttonPanelEventViewDrop, layoutEventTwo, layoutEventFour;
    int id;
    boolean fromAcceptedOnlineEvents;
    boolean fromDeletedOnlineEvents;
    int typeSize = Constants.FILTERS.typeFilters.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_online_event);

        settingUpIDRef();
        addTypesField();
        initialisingGlobalVariables();
        resetFields();
        editingDisabled();

        if (getIntent() != null) {
            Bundle dataFromEventButton = getIntent().getExtras();

            if (dataFromEventButton.getString(Constants.FROM_INTENT_EXTRAS).matches(Constants.FROM_EVENTS_HOME)) {
                id = dataFromEventButton.getInt("id");
                fromAcceptedOnlineEvents = true;
                fromDeletedOnlineEvents = false;
            } else if (dataFromEventButton.getString(Constants.FROM_INTENT_EXTRAS).matches(Constants.FROM_APPROVAL_NOTIFICATIONS)) {
                int eeVeeID = dataFromEventButton.getInt("id");
                id = dbHandler.getSQLiteID(eeVeeID);
                fromAcceptedOnlineEvents = false;
                fromDeletedOnlineEvents = false;
            }
            else if(dataFromEventButton.getString(Constants.FROM_INTENT_EXTRAS).matches(Constants.FROM_EDITED_NOTIFICATIONS)) {
                int eeVeeID = dataFromEventButton.getInt("id");
                id = dbHandler.getSQLiteID(eeVeeID);
                fromAcceptedOnlineEvents = true;
                fromDeletedOnlineEvents = false;
            }
            else {
                int eeVeeID = dataFromEventButton.getInt("id");
                id = dbHandler.getSQLiteID(eeVeeID);
                fromAcceptedOnlineEvents = true;
                fromDeletedOnlineEvents = true;
            }
        }

        fillEventData(id);
        setUpTimeETThreeA();

        viewEventsTableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(ViewOnlineEvent.this, ShowOnlineEventsTable.class);
                startActivity(changeActivity);
            }
        });

        dropEventsTableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.dropTable();
                Toast.makeText(ViewOnlineEvent.this, "EventsDataTable was dropped successfully", Toast.LENGTH_SHORT).show();
            }
        });

        layoutEventTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        layoutEventFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_online_event, menu);
        MenuItem acceptItem = menu.findItem(R.id.acceptItem);
        MenuItem rejectItem = menu.findItem(R.id.rejectItem);
        MenuItem ignoreItem = menu.findItem(R.id.ignoreItem);

        if (!fromAcceptedOnlineEvents && !fromDeletedOnlineEvents) {
            ignoreItem.setVisible(false);
        }
        else if(fromAcceptedOnlineEvents && fromDeletedOnlineEvents){
            acceptItem.setVisible(false);
            rejectItem.setVisible(false);
            ignoreItem.setVisible(false);
        }
        else {
            acceptItem.setVisible(false);
            rejectItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.acceptItem: {
                dbHandler.acceptEvent(id);
                Toast.makeText(ViewOnlineEvent.this, "Hey your event was accepted successfully", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ViewOnlineEvent.this, EventsHome.class);
                startActivity(intent);
            }
            return true;
            case R.id.rejectItem: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm your action");
                builder.setMessage("Are you sure about ignoring this?");
                builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbHandler.ignoreEvent(id);
                        dialog.dismiss();
                        Toast.makeText(ViewOnlineEvent.this, "Hey your event was ignored successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ViewOnlineEvent.this, EventsHome.class);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
            return true;
            case R.id.ignoreItem: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm your action");
                builder.setMessage("Are you sure about ignoring this?");
                builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbHandler.ignoreEvent(id);
                        dialog.dismiss();
                        Toast.makeText(ViewOnlineEvent.this, "Hey your event was ignored successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(ViewOnlineEvent.this, EventsHome.class);
                        startActivity(intent);
                    }
                });

                builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                        dialog.dismiss();
                    }
                });

                AlertDialog alert = builder.create();
                alert.show();
            }
            return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setDateText(EditText name, DateInput date) {
        name.setText(date.dateDispString());
    }

    public void setTimeText(EditText name, TimeInput time) {
        name.setText(time.timeDispString());
    }

    public void setDurationText(EditText name, DurationInput duration) {
        name.setText(duration.durationDispString());
    }

    public void setDuration(TimeInput start, TimeInput end, DurationInput duration, EditText name) {
        int diff = (60 * end.selectedHour) + end.selectedMin - (60 * start.selectedHour) - start.selectedMin;
        if (diff < 0) diff += 1440;
        duration.hour = diff / 60;
        duration.min = diff % 60;
        setDurationText(name, duration);
    }

    public void setEndTime(TimeInput start, TimeInput end, DurationInput duration, EditText name) {
        int diff = (60 * duration.hour) + duration.min + (60 * start.selectedHour) + start.selectedMin;
        if (diff >= 1440) diff -= 1440;
        end.selectedHour = diff / 60;
        end.selectedMin = diff % 60;
        setTimeText(name, end);
    }

    public void hideKeyboard(EditText name) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getApplicationWindowToken(), 0);
    }

    public void hideKeyboardRG(RadioGroup name) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getApplicationWindowToken(), 0);
    }

    public void settingUpIDRef() {
        eventViewOnlinePanelLL = (LinearLayout) findViewById(R.id.onlineEventViewPanelLL);

        eventNameET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.eventNameET);
        eventPlaceET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.eventPlaceET);
        startDateEventET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.startDateEventET);
        startTimeEventET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.startTimeEventET);
        endDateEventET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.endDateEventET);
        endTimeEventET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.endTimeEventET);
        startDateEventBET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.startDateEventBET);
        endDateEventBET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.endDateEventBET);
        startTimeEventAET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.startTimeEventAET);
        endTimeEventAET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.endTimeEventAET);
        durationEventAET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.durationEventAET);
        regnPlaceEventET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.regnPlaceEventET);
        regnDateEventET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.regnDateEventET);
        regnTimeEventET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.regnTimeEventET);
        regnWebsiteEventET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.regnWebsiteEventET);
        commentsEventET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.commentsEventET);
        clubNameET = (EditText) eventViewOnlinePanelLL.findViewById(R.id.clubNameET);

        viewEventsTableBTN = (Button) eventViewOnlinePanelLL.findViewById(R.id.viewEventsTableBTN);
        dropEventsTableBTN = (Button) eventViewOnlinePanelLL.findViewById(R.id.dropEventsTableBTN);

        repetitionEventRG = (RadioGroup) eventViewOnlinePanelLL.findViewById(R.id.repetitionEventRG);
        typeEventRG = (RadioGroup) eventViewOnlinePanelLL.findViewById(R.id.typeEventRG);

        noneEventRB = (RadioButton) eventViewOnlinePanelLL.findViewById(R.id.noneEventRB);
        dailyEventRB = (RadioButton) eventViewOnlinePanelLL.findViewById(R.id.dailyEventRB);
        weeklyEventRB = (RadioButton) eventViewOnlinePanelLL.findViewById(R.id.weeklyEventRB);

        sundayEventCB = (CheckBox) eventViewOnlinePanelLL.findViewById(R.id.sundayEventCB);
        mondayEventCB = (CheckBox) eventViewOnlinePanelLL.findViewById(R.id.mondayEventCB);
        tuesdayEventCB = (CheckBox) eventViewOnlinePanelLL.findViewById(R.id.tuesdayEventCB);
        wednesdayEventCB = (CheckBox) eventViewOnlinePanelLL.findViewById(R.id.wednesdayEventCB);
        thursdayEventCB = (CheckBox) eventViewOnlinePanelLL.findViewById(R.id.thursdayEventCB);
        fridayEventCB = (CheckBox) eventViewOnlinePanelLL.findViewById(R.id.fridayEventCB);
        saturdayEventCB = (CheckBox) eventViewOnlinePanelLL.findViewById(R.id.saturdayEventCB);

        layoutEventThree = (LinearLayout) eventViewOnlinePanelLL.findViewById(R.id.layoutEventThree);
        layoutEventThreeA = (LinearLayout) eventViewOnlinePanelLL.findViewById(R.id.layoutEventThreeA);
        layoutEventThreeB = (LinearLayout) eventViewOnlinePanelLL.findViewById(R.id.layoutEventThreeB);
        dayEventCheckboxGrp = (LinearLayout) eventViewOnlinePanelLL.findViewById(R.id.dayEventCheckboxGrp);
        buttonPanelEventSix = (LinearLayout) eventViewOnlinePanelLL.findViewById(R.id.buttonPanelEventSix);
        buttonPanelEventViewDrop = (LinearLayout) eventViewOnlinePanelLL.findViewById(R.id.buttonPanelEventViewDrop);
        layoutEventTwo = (LinearLayout) eventViewOnlinePanelLL.findViewById(R.id.layoutEventTwo);
        layoutEventFour = (LinearLayout) eventViewOnlinePanelLL.findViewById(R.id.layoutEventFour);
        layoutEventFourA = (LinearLayout) eventViewOnlinePanelLL.findViewById(R.id.layoutEventFourA);
    }

    public void initialisingGlobalVariables() {
        startTimeEvent = new TimeInput();
        endTimeEvent = new TimeInput();
        startTimeEventA = new TimeInput();
        endTimeEventA = new TimeInput();
        regnTimeEvent = new TimeInput();
        startDateEvent = new DateInput();
        endDateEvent = new DateInput();
        startDateEventB = new DateInput();
        endDateEventB = new DateInput();
        regnDateEvent = new DateInput();
        durationEvent = new DurationInput();
        dbHandler = new OnlineEventDetailsDBHandler(this, null, null, 1);
    }

    public void resetFields() {
        RadioGroup typeEventRGLocal = (RadioGroup) eventViewOnlinePanelLL.findViewById(R.id.typeEventRG);
        RadioButton miscRB = (RadioButton) typeEventRGLocal.findViewById(7);

        selectingTimeDoneEventA = false;
        startDateStoreStringEventA = "";
        endDateStoreStingEventA = "";

        startTimeEvent.InitialiseTimeVariables();
        endTimeEvent.InitialiseTimeVariables();
        startTimeEventA.InitialiseTimeVariables();
        endTimeEventA.InitialiseTimeVariables();
        regnTimeEvent.InitialiseTimeVariables();
        startDateEvent.InitialiseDateVariables();
        endDateEvent.InitialiseDateVariables();
        startDateEventB.InitialiseDateVariables();
        endDateEventB.InitialiseDateVariables();
        regnDateEvent.InitialiseDateVariables();
        durationEvent.initialiseVariables();

        layoutEventThree.setVisibility(View.GONE);
        layoutEventThreeA.setVisibility(View.GONE);
        layoutEventThreeB.setVisibility(View.GONE);
        dayEventCheckboxGrp.setVisibility(View.GONE);

        setETEmpty();
        uncheckWeekDaysCB();
        repetitionEventRG.clearCheck();
        typeEventRG.clearCheck();
        noneEventRB.setChecked(true);
        miscRB.setChecked(true);
    }

    public void noneEventRBClicked(View view) {
        hideKeyboardRG(repetitionEventRG);
        dayEventCheckboxGrp.setVisibility(View.GONE);
        layoutEventThreeA.setVisibility(View.GONE);
        layoutEventThreeB.setVisibility(View.GONE);
        layoutEventThree.setVisibility(View.VISIBLE);
    }

    public void dailyEventRBClicked(View view) {
        hideKeyboardRG(repetitionEventRG);
        dayEventCheckboxGrp.setVisibility(View.GONE);
        layoutEventThree.setVisibility(View.GONE);
        layoutEventThreeA.setVisibility(View.VISIBLE);
        layoutEventThreeB.setVisibility(View.VISIBLE);
    }

    public void weeklyEventRBClicked(View view) {
        hideKeyboardRG(repetitionEventRG);
        dayEventCheckboxGrp.setVisibility(View.VISIBLE);
        layoutEventThree.setVisibility(View.GONE);
        layoutEventThreeA.setVisibility(View.VISIBLE);
        layoutEventThreeB.setVisibility(View.VISIBLE);
    }

    public void addTypesField() {
        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        RadioButton[] typeRB = new RadioButton[typeSize];
        int i;
        for (i = 0; i < typeSize; i++) {
            typeRB[i] = new RadioButton(this);
            typeRB[i].setText(Constants.FILTERS.typeFilters[i]);
            typeRB[i].setId(i + 1);
            typeEventRG.addView(typeRB[i], p);
        }
    }

    public void setUpTimeETThreeA() {
        if (!selectingTimeDoneEventA) {
            Calendar currentTime = Calendar.getInstance();
            startTimeEventA.selectedHour = currentTime.get(Calendar.HOUR_OF_DAY);
            startTimeEventA.selectedMin = currentTime.get(Calendar.MINUTE);

            endTimeEventA.selectedHour = startTimeEventA.selectedHour + 2;
            endTimeEventA.selectedMin = startTimeEventA.selectedMin;

            durationEvent.day = 0;
            durationEvent.hour = 2;
            durationEvent.min = 0;

            if (endTimeEventA.selectedHour >= 24) endTimeEventA.selectedHour -= 24;
            setTimeText(startTimeEventAET, startTimeEventA);
            setTimeText(endTimeEventAET, endTimeEventA);
            setDurationText(durationEventAET, durationEvent);
            selectingTimeDoneEventA = true;
            startTimeEventA.selectingDone = true;
            endTimeEventA.selectingDone = true;
        } else {
            setTimeText(startTimeEventAET, startTimeEventA);
            setTimeText(endTimeEventAET, endTimeEventA);
            setDuration(startTimeEventA, endTimeEventA, durationEvent, durationEventAET);
        }
    }

    public void setETEmpty() {
        eventNameET.setText("");
        eventPlaceET.setText("");
        startDateEventET.setText("");
        startTimeEventET.setText("");
        endDateEventET.setText("");
        endTimeEventET.setText("");
        startDateEventBET.setText("");
        endDateEventBET.setText("");
        startTimeEventAET.setText("");
        endTimeEventAET.setText("");
        durationEventAET.setText("");
        regnPlaceEventET.setText("");
        regnDateEventET.setText("");
        regnTimeEventET.setText("");
        regnWebsiteEventET.setText("");
        commentsEventET.setText("");
        clubNameET.setText("");
    }

    public void uncheckWeekDaysCB() {
        if (sundayEventCB.isChecked()) sundayEventCB.toggle();
        if (mondayEventCB.isChecked()) mondayEventCB.toggle();
        if (tuesdayEventCB.isChecked()) tuesdayEventCB.toggle();
        if (wednesdayEventCB.isChecked()) wednesdayEventCB.toggle();
        if (thursdayEventCB.isChecked()) thursdayEventCB.toggle();
        if (fridayEventCB.isChecked()) fridayEventCB.toggle();
        if (saturdayEventCB.isChecked()) saturdayEventCB.toggle();
    }

    public void fillEventData(int id) {
        OnlineEventDetails onlineEvent;
        onlineEvent = dbHandler.getObjectWith(OnlineEventDetailsDBHandler.COLUMN_ID, String.valueOf(id));
        if (!onlineEvent.get_EventName().matches(Constants.EVENTNAME_NOTSET))
            eventNameET.setText(onlineEvent.get_EventName());
        if (!onlineEvent.get_EventPlace().matches(Constants.EVENTPLACE_NOTSET))
            eventPlaceET.setText(onlineEvent.get_EventPlace());
        setRepetitionType(onlineEvent.get_Repetition());
        setEventType(onlineEvent.get_Type());
        if (!onlineEvent.get_Comments().matches(Constants.COMMENTS_NOTSET))
            commentsEventET.setText(onlineEvent.get_Comments());
        setDateAndTime(onlineEvent);
        setRegnDetails(onlineEvent);
        clubNameET.setText(onlineEvent.get_ClubName());
    }

    public void setRepetitionType(String repetition) {
        if (repetition.matches("0000000")) {
            noneEventRB.setChecked(true);
            hideLayoutsNoneChecked();
        } else if (repetition.matches("1111111")) {
            dailyEventRB.setChecked(true);
            hideLayoutsDailyOrWeeklyChecked();
        } else {
            weeklyEventRB.setChecked(true);
            boolean[] weekDaysChecked = new boolean[7];
            for (int i = 0; i < 7; i++) {
                if (String.valueOf(repetition.charAt(i)).matches("0")) weekDaysChecked[i] = false;
                else weekDaysChecked[i] = true;
            }
            if (weekDaysChecked[0]) sundayEventCB.setChecked(true);
            if (weekDaysChecked[1]) mondayEventCB.setChecked(true);
            if (weekDaysChecked[2]) tuesdayEventCB.setChecked(true);
            if (weekDaysChecked[3]) wednesdayEventCB.setChecked(true);
            if (weekDaysChecked[4]) thursdayEventCB.setChecked(true);
            if (weekDaysChecked[5]) fridayEventCB.setChecked(true);
            if (weekDaysChecked[6]) saturdayEventCB.setChecked(true);
            dayEventCheckboxGrp.setVisibility(View.VISIBLE);
            hideLayoutsDailyOrWeeklyChecked();
        }
    }

    public void setEventType(String type) {
        int i;
        for (i = 0; i < typeSize; i++) {
            String temp = Constants.FILTERS.typeFilters[i];
            if (temp.matches(type)) break;
        }

        RadioButton typeToBeCheckedRB = (RadioButton) typeEventRG.findViewById(i + 1);
        typeToBeCheckedRB.setChecked(true);
    }


    public void setDateAndTime(OnlineEventDetails event) {
        if (noneEventRB.isChecked()) {
            DateTime startDT = new DateTime(event.get_StartDateTime());
            startDT.setDateInput(startDateEvent);
            startDT.setTimeInput(startTimeEvent);
            DateTime endDT = new DateTime(event.get_EndDateTime());
            endDT.setDateInput(endDateEvent);
            endDT.setTimeInput(endTimeEvent);
            setDateText(startDateEventET, startDateEvent);
            setTimeText(startTimeEventET, startTimeEvent);
            setDateText(endDateEventET, endDateEvent);
            setTimeText(endTimeEventET, endTimeEvent);
        } else {
            DateTime startDT = new DateTime(event.get_StartDateTime());
            startDT.setTimeInput(startTimeEventA);
            DateTime endDT = new DateTime(event.get_EndDateTime());
            endDT.setTimeInput(endTimeEventA);
            durationEvent.day = (DateTime.differenceInMin(startDT, endDT) / 1440);
            selectingTimeDoneEventA = true;
            setUpTimeETThreeA();

            if (!event.get_StartsFromDailyOrWeekly().matches(Constants.STARTS_FROM_NOTSET)) {
                startDT = new DateTime(event.get_StartsFromDailyOrWeekly());
                startDT.setDateInput(startDateEventB);
                setDateText(startDateEventBET, startDateEventB);
            }

            if (!event.get_EndsFromDailyOrWeekly().matches(Constants.ENDS_ON_NOTSET)) {
                endDT = new DateTime(event.get_EndsFromDailyOrWeekly());
                endDT.setDateInput(endDateEventB);
                setDateText(endDateEventBET, endDateEventB);
            }
        }
    }

    public void setRegnDetails(OnlineEventDetails event) {
        if (!event.get_Regn_Place().matches(Constants.REGNPLACE_NOTSET))
            regnPlaceEventET.setText(event.get_Regn_Place());
        if (!event.get_Website().matches(Constants.WEBSITE_NOTSET))
            regnWebsiteEventET.setText(event.get_Website());
        if (!event.get_DeadlineDateTime().matches(Constants.REGNDATETIME_NOTSET)) {
            DateTime RegnDT = new DateTime(event.get_DeadlineDateTime());
            RegnDT.setDateInput(regnDateEvent);
            RegnDT.setTimeInput(regnTimeEvent);
            setDateText(regnDateEventET, regnDateEvent);
            setTimeText(regnTimeEventET, regnTimeEvent);
        }
    }

    public void hideLayoutsNoneChecked() {
        layoutEventThree.setVisibility(View.VISIBLE);
        layoutEventThreeA.setVisibility(View.GONE);
        layoutEventThreeB.setVisibility(View.GONE);
    }

    public void hideLayoutsDailyOrWeeklyChecked() {
        layoutEventThree.setVisibility(View.GONE);
        layoutEventThreeA.setVisibility(View.VISIBLE);
        layoutEventThreeB.setVisibility(View.VISIBLE);
    }

    public void editingDisabled() {
        editingDisabledET(eventNameET);
        editingDisabledET(eventPlaceET);
        editingDisabledET(regnPlaceEventET);
        editingDisabledET(regnDateEventET);
        editingDisabledET(regnWebsiteEventET);
        editingDisabledET(commentsEventET);
        editingDisabledET(clubNameET);
        editingDisabledETCursor(startDateEventET);
        editingDisabledETCursor(endDateEventET);
        editingDisabledETCursor(startDateEventBET);
        editingDisabledETCursor(endDateEventBET);
        editingDisabledETCursor(startTimeEventET);
        editingDisabledETCursor(endTimeEventET);
        editingDisabledETCursor(startTimeEventAET);
        editingDisabledETCursor(endTimeEventAET);
        editingDisabledETCursor(durationEventAET);
        editingDisabledETCursor(regnDateEventET);
        editingDisabledETCursor(regnTimeEventET);
        for (int i = 0; i < repetitionEventRG.getChildCount(); i++) {
            editingDisabledRB((RadioButton) repetitionEventRG.getChildAt(i));
        }
        for (int i = 0; i < typeEventRG.getChildCount(); i++) {
            editingDisabledRB((RadioButton) typeEventRG.getChildAt(i));
        }
        editingDisabledCG();
    }

    public void editingDisabledET(EditText name) {
        name.setClickable(false);
        name.setCursorVisible(false);
        name.setFocusable(false);
        name.setFocusableInTouchMode(false);
    }

    public void editingDisabledETCursor(EditText name) {
        name.setCursorVisible(false);
    }

    public void editingDisabledCG() {
        editingDisabledCB(sundayEventCB);
        editingDisabledCB(mondayEventCB);
        editingDisabledCB(tuesdayEventCB);
        editingDisabledCB(wednesdayEventCB);
        editingDisabledCB(thursdayEventCB);
        editingDisabledCB(fridayEventCB);
        editingDisabledCB(saturdayEventCB);
    }

    public void editingDisabledRB(RadioButton name) {
        name.setClickable(false);
    }

    public void editingDisabledCB(CheckBox name) {
        name.setClickable(false);
    }
}