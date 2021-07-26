package com.harsha.eevee;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.util.Calendar;

import static java.lang.Math.min;

public class EventInput extends AppCompatActivity {

    EventDetailsDBHandler dbHandler;
    TimeInput startTimeEvent, endTimeEvent, startTimeEventA, endTimeEventA, regnTimeEvent;
    DateInput startDateEvent, endDateEvent, startDateEventB, endDateEventB, regnDateEvent;
    DurationInput durationEvent;

    boolean selectingTimeDoneEventA;
    String startDateStoreStringEventA, endDateStoreStingEventA;

    EditText eventNameET, eventPlaceET, startDateEventET, startTimeEventET, endDateEventET, endTimeEventET, startDateEventBET,
            endDateEventBET, startTimeEventAET, endTimeEventAET, durationEventAET, regnPlaceEventET, regnDateEventET, regnTimeEventET,
            regnWebsiteEventET, commentsEventET;

    Button skipEventOneBTN, previousEventTwoBTN, nextEventTwoBTN, previousEventThreeBTN, nextEventThreeBTN,
            previousEventThreeABTN, nextEventThreeABTN, previousEventThreeBBTN, skipEventThreeBBTN,
            previousEventFourBTN, nextEventFourBTN, previousEventFiveBTN, skipEventFiveBTN, previousEventSixBTN, addEventBTN,
            viewEventsTableBTN, dropEventsTableBTN;

    RadioGroup repetitionEventRG, typeEventRG;

    RadioButton noneEventRB, dailyEventRB, weeklyEventRB;

    CheckBox sundayEventCB, mondayEventCB, tuesdayEventCB, wednesdayEventCB, thursdayEventCB, fridayEventCB, saturdayEventCB;

    LinearLayout layoutEventOne, layoutEventTwo, layoutEventThree, layoutEventThreeA, layoutEventThreeB, layoutEventFour,
            layoutEventFive, layoutEventSix, dayEventCheckboxGrp, buttonPanelEventOne, buttonPanelEventTwo, buttonPanelEventThree,
            buttonPanelEventThreeA, buttonPanelEventThreeB, buttonPanelEventFour, buttonPanelEventFive,
            buttonPanelEventSix, buttonPanelEventViewDrop;
    int typeSize = Constants.FILTERS.typeFilters.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_input);

        //hide Action bar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }

        initialisingGlobalVariables();
        settingUpIDRef();
        addTypesField();
        resetFields();

        startDateEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(startDateEventET);
                showDatePicker(startDateEventET, startDateEvent, startDateEvent, true);
            }
        });

        startTimeEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(startTimeEventET);
                showTimePicker(startTimeEventET, startTimeEvent, startTimeEvent, true);
            }
        });

        startDateEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startDateEvent.InitialiseDateVariables();
                setDateText(startDateEventET, startDateEvent);
                return true;
            }
        });

        startTimeEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startTimeEvent.InitialiseTimeVariables();
                setTimeText(startTimeEventET, startTimeEvent);
                return true;
            }
        });

        endDateEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(endDateEventET);
                showDatePicker(endDateEventET, endDateEvent, startDateEvent, false);
            }
        });

        endTimeEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(endTimeEventET);
                showTimePicker(endTimeEventET, endTimeEvent, startTimeEvent, false);
            }
        });

        endDateEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                endDateEvent.InitialiseDateVariables();
                setDateText(endDateEventET, endDateEvent);
                return true;
            }
        });

        endTimeEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                endTimeEvent.InitialiseTimeVariables();
                setTimeText(endTimeEventET, endTimeEvent);
                return true;
            }
        });

        startTimeEventAET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(startTimeEventAET);
                showTimePicker(startTimeEventAET, startTimeEventA, startTimeEventA, true);
                // setDuration(startTimeEventA,endTimeEventA,durationEvent,durationEventAET);
            }
        });

        durationEventAET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(durationEventAET);
                showDurationPicker(durationEventAET, durationEvent);
                //  setEndTime(startTimeEventA,endTimeEventA,durationEvent,endTimeEventAET);
            }
        });

        endTimeEventAET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(endTimeEventAET);
                showTimePicker(endTimeEventAET, endTimeEventA, startTimeEventA, false);
                // setDuration(startTimeEventA,endTimeEventA,durationEvent,durationEventAET);
            }
        });

        startTimeEventAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectingTimeDoneEventA = false;
                setUpTimeETThreeA();
                return true;
            }
        });

        durationEventAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectingTimeDoneEventA = false;
                setUpTimeETThreeA();
                return true;
            }
        });

        endTimeEventAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectingTimeDoneEventA = false;
                setUpTimeETThreeA();
                return true;
            }
        });

        startDateEventBET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(startDateEventBET);
                showDatePicker(startDateEventBET, startDateEventB, startDateEventB, true);
            }
        });

        endDateEventBET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(endDateEventBET);
                showDatePicker(endDateEventBET, endDateEventB, startDateEventB, false);
            }
        });

        startDateEventBET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startDateEventB.InitialiseDateVariables();
                setDateText(startDateEventBET, startDateEventB);
                return true;
            }
        });

        endDateEventBET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                endDateEventB.InitialiseDateVariables();
                setDateText(endDateEventBET, endDateEventB);
                return true;
            }
        });

        regnDateEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(regnDateEventET);
                showDatePicker(regnDateEventET, regnDateEvent, startDateEvent, true);
            }
        });

        regnTimeEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(regnTimeEventET);
                showTimePicker(regnTimeEventET, regnTimeEvent, startTimeEvent, true);
            }
        });

        regnDateEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                regnDateEvent.InitialiseDateVariables();
                setDateText(regnDateEventET, regnDateEvent);
                return true;
            }
        });

        regnTimeEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                regnTimeEvent.InitialiseTimeVariables();
                setTimeText(regnTimeEventET, regnTimeEvent);
                return true;
            }
        });

        skipEventOneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEventOne.setVisibility(View.GONE);
                layoutEventTwo.setVisibility(View.VISIBLE);
                hideKeyboardRadioGroup(repetitionEventRG);
            }
        });

        previousEventTwoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEventTwo.setVisibility(View.GONE);
                layoutEventOne.setVisibility(View.VISIBLE);
            }
        });

        nextEventTwoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noneEventRB.isChecked()) {
                    layoutEventTwo.setVisibility(View.GONE);
                    layoutEventThree.setVisibility(View.VISIBLE);
                } else {
                    if (dailyEventRB.isChecked()) durationEvent.minDays = 1;
                    else {
                        if (sundayEventCB.isChecked() && mondayEventCB.isChecked() && tuesdayEventCB.isChecked() && wednesdayEventCB.isChecked()
                                && thursdayEventCB.isChecked() && fridayEventCB.isChecked() && saturdayEventCB.isChecked()) {
                            Toast.makeText(EventInput.this, "Please change the repetition type of your event to daily!!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!sundayEventCB.isChecked() && !mondayEventCB.isChecked() && !tuesdayEventCB.isChecked() && !wednesdayEventCB.isChecked()
                                && !thursdayEventCB.isChecked() && !fridayEventCB.isChecked() && !saturdayEventCB.isChecked()) {
                            Toast.makeText(EventInput.this, "Please change the repetition type of your event to none!!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean[] daysChecked = {false, false, false, false, false, false, false};

                        if (sundayEventCB.isChecked()) daysChecked[0] = true;
                        if (mondayEventCB.isChecked()) daysChecked[1] = true;
                        if (tuesdayEventCB.isChecked()) daysChecked[2] = true;
                        if (wednesdayEventCB.isChecked()) daysChecked[3] = true;
                        if (thursdayEventCB.isChecked()) daysChecked[4] = true;
                        if (fridayEventCB.isChecked()) daysChecked[5] = true;
                        if (saturdayEventCB.isChecked()) daysChecked[6] = true;

                        durationEvent.minDays = 7;
                        for (int i = 0; i < 6; i++) {
                            for (int j = i + 1; j < 7; j++) {
                                if (daysChecked[i] && daysChecked[j]) {
                                    durationEvent.minDays = min(durationEvent.minDays, j - i);
                                    durationEvent.minDays = min(durationEvent.minDays, 7 + i - j);
                                }
                            }
                        }
                    }
                    setUpTimeETThreeA();
                    layoutEventTwo.setVisibility(View.GONE);
                    layoutEventThreeA.setVisibility(View.VISIBLE);
                }
            }
        });

        previousEventThreeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEventThree.setVisibility(View.GONE);
                layoutEventTwo.setVisibility(View.VISIBLE);
            }
        });

        nextEventThreeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDateEvent.dateDispString().matches("")) {
                    Toast.makeText(EventInput.this, "Please fill in the start date of your event!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (endDateEvent.dateDispString().matches("")) {
                    Toast.makeText(EventInput.this, "Please fill in the end date of your event!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (startTimeEvent.timeDispString().matches("")) {
                    startTimeEvent.selectedMin = 0;
                    startTimeEvent.selectedHour = 0;
                }

                if (endTimeEvent.timeDispString().matches("")) {
                    if (!DateInput.isGreater(endDateEvent, startDateEvent)) {
                        endTimeEvent.selectedMin = 59;
                        endTimeEvent.selectedHour = 23;
                    } else {
                        endTimeEvent.selectedHour = 0;
                        endTimeEvent.selectedMin = 0;
                    }
                }

                DateTime startDT = new DateTime(startDateEvent.dateStoreString() + ":" + startTimeEvent.timeStoreString());
                DateTime endDT = new DateTime(endDateEvent.dateStoreString() + ":" + endTimeEvent.timeStoreString());
                if (DateTime.differenceInMin(startDT, endDT) < 0) {
                    Toast.makeText(EventInput.this, "Please check the end date/time of your event as it is inconsistent" +
                            " with your start date and time!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                layoutEventThree.setVisibility(View.GONE);
                layoutEventFour.setVisibility(View.VISIBLE);
            }
        });

        previousEventThreeABTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEventThreeA.setVisibility(View.GONE);
                layoutEventTwo.setVisibility(View.VISIBLE);
            }
        });

        nextEventThreeABTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime startDT = new DateTime("1:1:1" + ":" + startTimeEventA.timeStoreString());
                DateTime endDT = new DateTime("1:1:1" + ":" + endTimeEventA.timeStoreString());

                if (DateTime.differenceInMin(startDT, endDT) < 0) {
                    endDT.DAY += 1;
                }
                endDT.DAY += durationEvent.day;
                if (DateTime.differenceInMin(startDT, endDT) > 1440 * durationEvent.minDays) {
                    Toast.makeText(EventInput.this, "Please check the duration of your event", Toast.LENGTH_SHORT).show();
                    return;
                }

                startDateStoreStringEventA = startDT.toString();
                endDateStoreStingEventA = endDT.toString();

                layoutEventThreeA.setVisibility(View.GONE);
                layoutEventThreeB.setVisibility(View.VISIBLE);
            }
        });

        previousEventThreeBBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEventThreeB.setVisibility(View.GONE);
                layoutEventThreeA.setVisibility(View.VISIBLE);
            }
        });

        // not checking if the start date matches with one of the checked weekday
        skipEventThreeBBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startDateEventB.dateDispString().matches("") && !endDateEventB.dateDispString().matches("")) {
                    int diff = (60 * durationEvent.hour) + durationEvent.min + (60 * startTimeEventA.selectedHour) + startTimeEventA.selectedMin;
                    int days = durationEvent.day;
                    if (diff >= 1440) days += 1;
                    DateTime startDT = new DateTime(startDateEventB.dateStoreString() + ":" + "0:0");
                    DateTime endDT = new DateTime(endDateEventB.dateStoreString() + ":" + "0:0");

                    if (DateTime.differenceInMin(startDT, endDT) < 1440 * days) {
                        Toast.makeText(EventInput.this, "Please check the end date of your event as it is inconsistent" +
                                " with your start !!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                layoutEventThreeB.setVisibility(View.GONE);
                layoutEventFour.setVisibility(View.VISIBLE);
            }
        });

        previousEventFourBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEventFour.setVisibility(View.GONE);
                if (noneEventRB.isChecked()) {
                    layoutEventThree.setVisibility(View.VISIBLE);
                } else {
                    layoutEventThreeB.setVisibility(View.VISIBLE);
                }
            }
        });

        nextEventFourBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEventFour.setVisibility(View.GONE);
                layoutEventFive.setVisibility(View.VISIBLE);
            }
        });

        previousEventFiveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEventFive.setVisibility(View.GONE);
                layoutEventFour.setVisibility(View.VISIBLE);
                hideKeyboardRadioGroup(typeEventRG);
            }
        });

        skipEventFiveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!regnTimeEvent.timeDispString().matches("") && regnDateEvent.dateDispString().matches("")) {
                    Toast.makeText(EventInput.this, "Please fill in the regn date of your event!!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (regnTimeEvent.timeDispString().matches("") && !regnDateEvent.dateDispString().matches("")) {
                    if (noneEventRB.isChecked()) {
                        if (DateInput.isGreater(startDateEvent, regnDateEvent)) {
                            regnTimeEvent.selectedHour = 23;
                            regnTimeEvent.selectedMin = 59;
                        } else {
                            regnTimeEvent.selectedHour = 0;
                            regnTimeEvent.selectedMin = 0;
                        }
                    } else {
                        if (!startDateEventB.dateDispString().matches("") && !DateInput.isGreater(startDateEventB, regnDateEvent)) {
                            regnTimeEvent.selectedHour = 0;
                            regnTimeEvent.selectedMin = 0;
                        } else {
                            regnTimeEvent.selectedHour = 23;
                            regnTimeEvent.selectedMin = 59;
                        }
                    }
                }

                layoutEventFive.setVisibility(View.GONE);
                layoutEventSix.setVisibility(View.VISIBLE);
                hideKeyboard(commentsEventET);
            }
        });
        // TODO : should do validation of registration dates if possible

        previousEventSixBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutEventSix.setVisibility(View.GONE);
                layoutEventFive.setVisibility(View.VISIBLE);
                hideKeyboard(regnPlaceEventET);
            }
        });

        addEventBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String repetitionAsInt = getRepetitionAsInt();
                String nameOfType = getNameOfType();
                String inpEventName = eventNameET.getText().toString();
                String inpEventPlace = eventPlaceET.getText().toString();
                String inpRegnPlace = regnPlaceEventET.getText().toString();
                String inpWebsite = regnWebsiteEventET.getText().toString();
                String inpComments = commentsEventET.getText().toString();

                String inpStartDateTime, inpEndDateTime, inpRegnDateTime;
                if (noneEventRB.isChecked()) {
                    inpStartDateTime = startDateEvent.dateStoreString() + ":" + startTimeEvent.timeStoreString();
                    inpEndDateTime = endDateEvent.dateStoreString() + ":" + endTimeEvent.timeStoreString();
                } else {
                    inpStartDateTime = startDateStoreStringEventA;
                    inpEndDateTime = endDateStoreStingEventA;
                }
                if (regnDateEvent.dateDispString().matches("")) {
                    inpRegnDateTime = Constants.REGNDATETIME_NOTSET;
                } else {
                    inpRegnDateTime = regnDateEvent.dateStoreString() + ":" + regnTimeEvent.timeStoreString();
                }
                String inpTimeStamp = getTimeStamp();
                String inpStartDateDailyOrWeekly, inpEndDateDailyOrWeekly;

                if (inpEventName.matches("")) inpEventName = Constants.EVENTNAME_NOTSET;
                if (inpEventPlace.matches("")) inpEventPlace = Constants.EVENTPLACE_NOTSET;
                if (inpRegnPlace.matches("")) inpRegnPlace = Constants.REGNPLACE_NOTSET;
                if (inpWebsite.matches("")) inpWebsite = Constants.WEBSITE_NOTSET;
                if (inpComments.matches("")) inpComments = Constants.COMMENTS_NOTSET;

                if (noneEventRB.isChecked()) {
                    inpStartDateDailyOrWeekly = Constants.STARTS_FROM_NOT_APPLICABLE;
                    inpEndDateDailyOrWeekly = Constants.ENDS_ON_NOT_APPLICABLE;
                } else {
                    if (!startDateEventB.dateDispString().matches("")) {
                        inpStartDateDailyOrWeekly = startDateEventB.dateStoreString() + ":" + "0:0";
                    } else {
                        inpStartDateDailyOrWeekly = Constants.STARTS_FROM_NOTSET;
                    }
                    if (!endDateEventB.dateDispString().matches("")) {
                        inpEndDateDailyOrWeekly = endDateEventB.dateStoreString() + ":" + "0:0";
                    } else {
                        inpEndDateDailyOrWeekly = Constants.ENDS_ON_NOTSET;
                    }
                }

                EventDetails event = new EventDetails();
                event.set_TimeStamp(inpTimeStamp);
                event.set_EventName(inpEventName);
                event.set_EventPlace(inpEventPlace);
                event.set_StartDateTime(inpStartDateTime);
                event.set_EndDateTime(inpEndDateTime);
                event.set_Repetition(repetitionAsInt);
                event.set_Type(nameOfType);
                event.set_Comments(inpComments);
                event.set_Regn_Place(inpRegnPlace);
                event.set_Website(inpWebsite);
                event.set_DeadlineDateTime(inpRegnDateTime);
                event.set_StartsFromDailyOrWeekly(inpStartDateDailyOrWeekly);
                event.set_EndsFromDailyOrWeekly(inpEndDateDailyOrWeekly);

                dbHandler.insertRow(event);
                Toast.makeText(EventInput.this, "Hey !! Your event was added successfully", Toast.LENGTH_SHORT).show();
                resetFields();
                hideKeyboard(eventNameET);
            }
        });

        viewEventsTableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(EventInput.this, ShowEventsTable.class);
                String eventsTableToString = dbHandler.viewTable();
                changeActivity.putExtra("tableEventsData", eventsTableToString);
                startActivity(changeActivity);
            }
        });

        dropEventsTableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.dropTable();
                Toast.makeText(EventInput.this, "EventsDataTable was dropped successfully", Toast.LENGTH_SHORT).show();
            }
        });

        eventNameET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String eventName = eventNameET.getText().toString();
                String eventPlace = eventPlaceET.getText().toString();
                if (eventName.matches("") && eventPlace.matches(""))
                    skipEventOneBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String eventName = eventNameET.getText().toString();
                if (!eventName.matches("")) skipEventOneBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String eventName = eventNameET.getText().toString();
                String eventPlace = eventPlaceET.getText().toString();
                if (eventName.matches("") && eventPlace.matches(""))
                    skipEventOneBTN.setText("Skip");
            }
        });

        eventPlaceET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String eventName = eventNameET.getText().toString();
                String eventPlace = eventPlaceET.getText().toString();
                if (eventName.matches("") && eventPlace.matches(""))
                    skipEventOneBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String place = eventPlaceET.getText().toString();
                if (!place.matches("")) skipEventOneBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String eventName = eventNameET.getText().toString();
                String eventPlace = eventPlaceET.getText().toString();
                if (eventName.matches("") && eventPlace.matches(""))
                    skipEventOneBTN.setText("Skip");
            }
        });

        startDateEventBET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String startDate = startDateEventBET.getText().toString();
                String endDate = endDateEventBET.getText().toString();
                if (startDate.matches("") && endDate.matches(""))
                    skipEventThreeBBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String startDate = startDateEventBET.getText().toString();
                if (!startDate.matches("")) skipEventThreeBBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String startDate = startDateEventBET.getText().toString();
                String endDate = endDateEventBET.getText().toString();
                if (startDate.matches("") && endDate.matches(""))
                    skipEventThreeBBTN.setText("Skip");
            }
        });

        endDateEventBET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String startDate = startDateEventBET.getText().toString();
                String endDate = endDateEventBET.getText().toString();
                if (startDate.matches("") && endDate.matches(""))
                    skipEventThreeBBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String endDate = endDateEventBET.getText().toString();
                if (!endDate.matches("")) skipEventThreeBBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String startDate = startDateEventBET.getText().toString();
                String endDate = endDateEventBET.getText().toString();
                if (startDate.matches("") && endDate.matches(""))
                    skipEventThreeBBTN.setText("Skip");
            }
        });

        regnPlaceEventET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String regnPlace = regnPlaceEventET.getText().toString();
                String regnDate = regnDateEventET.getText().toString();
                String regnTime = regnTimeEventET.getText().toString();
                String regnWebsite = regnWebsiteEventET.getText().toString();
                if (regnPlace.matches("") && regnDate.matches("") && regnTime.matches("") && regnWebsite.matches(""))
                    skipEventFiveBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String regnPlace = regnPlaceEventET.getText().toString();
                if (!regnPlace.matches("")) skipEventFiveBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String regnPlace = regnPlaceEventET.getText().toString();
                String regnDate = regnDateEventET.getText().toString();
                String regnTime = regnTimeEventET.getText().toString();
                String regnWebsite = regnWebsiteEventET.getText().toString();
                if (regnPlace.matches("") && regnDate.matches("") && regnTime.matches("") && regnWebsite.matches(""))
                    skipEventFiveBTN.setText("Skip");
            }
        });

        regnDateEventET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String regnPlace = regnPlaceEventET.getText().toString();
                String regnDate = regnDateEventET.getText().toString();
                String regnTime = regnTimeEventET.getText().toString();
                String regnWebsite = regnWebsiteEventET.getText().toString();
                if (regnPlace.matches("") && regnDate.matches("") && regnTime.matches("") && regnWebsite.matches(""))
                    skipEventFiveBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String regnDate = regnDateEventET.getText().toString();
                if (!regnDate.matches("")) skipEventFiveBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String regnPlace = regnPlaceEventET.getText().toString();
                String regnDate = regnDateEventET.getText().toString();
                String regnTime = regnTimeEventET.getText().toString();
                String regnWebsite = regnWebsiteEventET.getText().toString();
                if (regnPlace.matches("") && regnDate.matches("") && regnTime.matches("") && regnWebsite.matches(""))
                    skipEventFiveBTN.setText("Skip");
            }
        });

        regnTimeEventET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String regnPlace = regnPlaceEventET.getText().toString();
                String regnDate = regnDateEventET.getText().toString();
                String regnTime = regnTimeEventET.getText().toString();
                String regnWebsite = regnWebsiteEventET.getText().toString();
                if (regnPlace.matches("") && regnDate.matches("") && regnTime.matches("") && regnWebsite.matches(""))
                    skipEventFiveBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String regnTime = regnTimeEventET.getText().toString();
                if (!regnTime.matches("")) skipEventFiveBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String regnPlace = regnPlaceEventET.getText().toString();
                String regnDate = regnDateEventET.getText().toString();
                String regnTime = regnTimeEventET.getText().toString();
                String regnWebsite = regnWebsiteEventET.getText().toString();
                if (regnPlace.matches("") && regnDate.matches("") && regnTime.matches("") && regnWebsite.matches(""))
                    skipEventFiveBTN.setText("Skip");
            }
        });

        regnWebsiteEventET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String regnPlace = regnPlaceEventET.getText().toString();
                String regnDate = regnDateEventET.getText().toString();
                String regnTime = regnTimeEventET.getText().toString();
                String regnWebsite = regnWebsiteEventET.getText().toString();
                if (regnPlace.matches("") && regnDate.matches("") && regnTime.matches("") && regnWebsite.matches(""))
                    skipEventFiveBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String regnWebsite = regnWebsiteEventET.getText().toString();
                if (!regnWebsite.matches("")) skipEventFiveBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String regnPlace = regnPlaceEventET.getText().toString();
                String regnDate = regnDateEventET.getText().toString();
                String regnTime = regnTimeEventET.getText().toString();
                String regnWebsite = regnWebsiteEventET.getText().toString();
                if (regnPlace.matches("") && regnDate.matches("") && regnTime.matches("") && regnWebsite.matches(""))
                    skipEventFiveBTN.setText("Skip");
            }
        });

    }

    // shows time picker in 12hr format
    public void showTimePicker(final EditText name, final TimeInput time, final TimeInput ref, boolean start) {
        int suggestionHour, suggestionMinute;
        if (time.selectingDone) {
            suggestionHour = time.selectedHour;
            suggestionMinute = time.selectedMin;
        } else {
            if (ref.selectingDone) {
                suggestionHour = ref.selectedHour;
                suggestionMinute = ref.selectedMin;
            } else {
                Calendar currentTime = Calendar.getInstance();
                suggestionHour = currentTime.get(Calendar.HOUR_OF_DAY);
                suggestionMinute = currentTime.get(Calendar.MINUTE);
            }

            if (!start) {
                suggestionHour += 2;
                if (suggestionHour >= 24) suggestionHour -= 24;
            }
        }

        TimePickerDialog TimePicker;
        TimePicker = new TimePickerDialog(EventInput.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker timePicker, int selectedHour, int selectedMinute) {
                time.selectedHour = selectedHour;
                time.selectedMin = selectedMinute;
                time.selectingDone = true;
                setTimeText(name, time);
                setDuration(startTimeEventA, endTimeEventA, durationEvent, durationEventAET);
            }
        }, suggestionHour, suggestionMinute, DateFormat.is24HourFormat(EventInput.this));
        TimePicker.setTitle("Select Time");
        TimePicker.show();
    }

    public void showDatePicker(final EditText name, final DateInput date, final DateInput ref, boolean start) {
        int suggestionYear, suggestionMonth, suggestionDay;
        if (date.selectingDone) {
            suggestionYear = date.selectedYear;
            suggestionMonth = date.selectedMonth - 1;
            suggestionDay = date.selectedDay;
        } else {
            if (ref.selectingDone) {
                suggestionYear = ref.selectedYear;
                suggestionMonth = ref.selectedMonth - 1;
                suggestionDay = ref.selectedDay;
            } else {
                Calendar currentTime = Calendar.getInstance();
                suggestionYear = currentTime.get(Calendar.YEAR);
                suggestionMonth = currentTime.get(Calendar.MONTH);
                suggestionDay = currentTime.get(Calendar.DAY_OF_MONTH);
            }

            if (!start) {
                // TODO : Can try to give suggestions for the end date
            }
        }
        DatePickerDialog DatePicker;
        DatePicker = new DatePickerDialog(EventInput.this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(android.widget.DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                date.selectedYear = selectedYear;
                date.selectedMonth = selectedMonth + 1;
                date.selectedDay = selectedDay;
                date.selectingDone = true;
                setDateText(name, date);
            }
        }, suggestionYear, suggestionMonth, suggestionDay);
        DatePicker.setTitle("Select Date");
        DatePicker.show();
    }

    public void showDurationPicker(final EditText name, final DurationInput duration) {
        View durationView = getLayoutInflater().inflate(R.layout.duration_inp, null);
        final NumberPicker dayNP = (NumberPicker) durationView.findViewById(R.id.dayNP);
        dayNP.setMaxValue(durationEvent.minDays);
        dayNP.setMinValue(0);
        dayNP.setValue(duration.day);
        dayNP.setWrapSelectorWheel(true);

        final NumberPicker hourNP = (NumberPicker) durationView.findViewById(R.id.hourNP);
        hourNP.setMaxValue(23);
        hourNP.setMinValue(0);
        hourNP.setValue(duration.hour);

        final NumberPicker minNP = (NumberPicker) durationView.findViewById(R.id.minNP);
        minNP.setMaxValue(59);
        minNP.setMinValue(0);
        minNP.setValue(duration.min);

        AlertDialog.Builder builder = new AlertDialog.Builder(EventInput.this);
        builder.setTitle("Set Duration:");
        builder.setView(durationView);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        duration.day = dayNP.getValue();
                        duration.hour = hourNP.getValue();
                        duration.min = minNP.getValue();
                        setDurationText(name, duration);
                        setEndTime(startTimeEventA, endTimeEventA, durationEvent, endTimeEventAET);
                    }
                });
        builder.setNegativeButton("CANCEL",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                });
        builder.create().show();
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

    public void hideKeyboardRadioGroup(RadioGroup name) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getApplicationWindowToken(), 0);
    }

    public void settingUpIDRef() {
        eventNameET = (EditText) findViewById(R.id.eventNameET);
        eventPlaceET = (EditText) findViewById(R.id.eventPlaceET);
        startDateEventET = (EditText) findViewById(R.id.startDateEventET);
        startTimeEventET = (EditText) findViewById(R.id.startTimeEventET);
        endDateEventET = (EditText) findViewById(R.id.endDateEventET);
        endTimeEventET = (EditText) findViewById(R.id.endTimeEventET);
        startDateEventBET = (EditText) findViewById(R.id.startDateEventBET);
        endDateEventBET = (EditText) findViewById(R.id.endDateEventBET);
        startTimeEventAET = (EditText) findViewById(R.id.startTimeEventAET);
        endTimeEventAET = (EditText) findViewById(R.id.endTimeEventAET);
        durationEventAET = (EditText) findViewById(R.id.durationEventAET);
        regnPlaceEventET = (EditText) findViewById(R.id.regnPlaceEventET);
        regnDateEventET = (EditText) findViewById(R.id.regnDateEventET);
        regnTimeEventET = (EditText) findViewById(R.id.regnTimeEventET);
        regnWebsiteEventET = (EditText) findViewById(R.id.regnWebsiteEventET);
        commentsEventET = (EditText) findViewById(R.id.commentsEventET);

        skipEventOneBTN = (Button) findViewById(R.id.skipEventOneBTN);
        previousEventTwoBTN = (Button) findViewById(R.id.previousEventTwoBTN);
        nextEventTwoBTN = (Button) findViewById(R.id.nextEventTwoBTN);
        previousEventThreeBTN = (Button) findViewById(R.id.previousEventThreeBTN);
        nextEventThreeBTN = (Button) findViewById(R.id.nextEventThreeBTN);
        previousEventThreeABTN = (Button) findViewById(R.id.previousEventThreeABTN);
        nextEventThreeABTN = (Button) findViewById(R.id.nextEventThreeABTN);
        previousEventThreeBBTN = (Button) findViewById(R.id.previousEventThreeBBTN);
        skipEventThreeBBTN = (Button) findViewById(R.id.skipEventThreeBBTN);
        previousEventFourBTN = (Button) findViewById(R.id.previousEventFourBTN);
        nextEventFourBTN = (Button) findViewById(R.id.nextEventFourBTN);
        previousEventFiveBTN = (Button) findViewById(R.id.previousEventFiveBTN);
        skipEventFiveBTN = (Button) findViewById(R.id.skipEventFiveBTN);
        previousEventSixBTN = (Button) findViewById(R.id.previousEventSixBTN);
        addEventBTN = (Button) findViewById(R.id.addEventBTN);
        viewEventsTableBTN = (Button) findViewById(R.id.viewEventsTableBTN);
        dropEventsTableBTN = (Button) findViewById(R.id.dropEventsTableBTN);

        repetitionEventRG = (RadioGroup) findViewById(R.id.repetitionEventRG);
        typeEventRG = (RadioGroup) findViewById(R.id.typeEventRG);

        noneEventRB = (RadioButton) findViewById(R.id.noneEventRB);
        dailyEventRB = (RadioButton) findViewById(R.id.dailyEventRB);
        weeklyEventRB = (RadioButton) findViewById(R.id.weeklyEventRB);

        sundayEventCB = (CheckBox) findViewById(R.id.sundayEventCB);
        mondayEventCB = (CheckBox) findViewById(R.id.mondayEventCB);
        tuesdayEventCB = (CheckBox) findViewById(R.id.tuesdayEventCB);
        wednesdayEventCB = (CheckBox) findViewById(R.id.wednesdayEventCB);
        thursdayEventCB = (CheckBox) findViewById(R.id.thursdayEventCB);
        fridayEventCB = (CheckBox) findViewById(R.id.fridayEventCB);
        saturdayEventCB = (CheckBox) findViewById(R.id.saturdayEventCB);

        layoutEventOne = (LinearLayout) findViewById(R.id.layoutEventOne);
        layoutEventTwo = (LinearLayout) findViewById(R.id.layoutEventTwo);
        layoutEventThree = (LinearLayout) findViewById(R.id.layoutEventThree);
        layoutEventThreeA = (LinearLayout) findViewById(R.id.layoutEventThreeA);
        layoutEventThreeB = (LinearLayout) findViewById(R.id.layoutEventThreeB);
        layoutEventFour = (LinearLayout) findViewById(R.id.layoutEventFour);
        layoutEventFive = (LinearLayout) findViewById(R.id.layoutEventFive);
        layoutEventSix = (LinearLayout) findViewById(R.id.layoutEventSix);
        dayEventCheckboxGrp = (LinearLayout) findViewById(R.id.dayEventCheckboxGrp);
        buttonPanelEventOne = (LinearLayout) findViewById(R.id.buttonPanelEventOne);
        buttonPanelEventTwo = (LinearLayout) findViewById(R.id.buttonPanelEventTwo);
        buttonPanelEventThree = (LinearLayout) findViewById(R.id.buttonPanelEventThree);
        buttonPanelEventThreeA = (LinearLayout) findViewById(R.id.buttonPanelEventThreeA);
        buttonPanelEventThreeB = (LinearLayout) findViewById(R.id.buttonPanelEventThreeB);
        buttonPanelEventFour = (LinearLayout) findViewById(R.id.buttonPanelEventFour);
        buttonPanelEventFive = (LinearLayout) findViewById(R.id.buttonPanelEventFive);
        buttonPanelEventSix = (LinearLayout) findViewById(R.id.buttonPanelEventSix);
        buttonPanelEventViewDrop = (LinearLayout) findViewById(R.id.buttonPanelEventViewDrop);
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
//        dbHandler = EventDetailsDBHandler.getInstance(this);
        dbHandler = new EventDetailsDBHandler(this, null, null, 1);
    }

    public void resetFields() {
        RadioButton miscRB = (RadioButton) typeEventRG.findViewById(7);

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

        layoutEventOne.setVisibility(View.VISIBLE);
        layoutEventTwo.setVisibility(View.GONE);
        layoutEventThree.setVisibility(View.GONE);
        layoutEventThreeA.setVisibility(View.GONE);
        layoutEventThreeB.setVisibility(View.GONE);
        layoutEventFour.setVisibility(View.GONE);
        layoutEventFive.setVisibility(View.GONE);
        layoutEventSix.setVisibility(View.GONE);
        dayEventCheckboxGrp.setVisibility(View.GONE);

        setETEmpty();
        uncheckWeekDaysCB();
        repetitionEventRG.clearCheck();
        typeEventRG.clearCheck();
        noneEventRB.setChecked(true);
        miscRB.setChecked(true);
    }

    public void dailyOrNoneEventRBClicked(View view) {
        dayEventCheckboxGrp.setVisibility(View.GONE);
    }

    public void weeklyEventRBClicked(View view) {
        dayEventCheckboxGrp.setVisibility(View.VISIBLE);
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

    public String getRepetitionAsInt() {
        String repetitionAsInt = "";
        if (noneEventRB.isChecked()) {
            repetitionAsInt = "0000000";
        } else if (dailyEventRB.isChecked()) {
            repetitionAsInt = "1111111";
        } else {
            if (sundayEventCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (mondayEventCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (tuesdayEventCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (wednesdayEventCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (thursdayEventCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (fridayEventCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (saturdayEventCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";
        }
        return repetitionAsInt;
    }

    public String getNameOfType() {
        String nameOfType = "";
        int j;
        for (j = 1; j <= typeSize; j++) {
            RadioButton typeOfTaskRB = (RadioButton) typeEventRG.findViewById(j);
            if (typeOfTaskRB.isChecked()) {
                nameOfType = (String) typeOfTaskRB.getText();
                break;
            }
        }
        return nameOfType;
    }

    public String getTimeStamp() {
        Calendar currentTime = Calendar.getInstance();
        int year = currentTime.get(Calendar.YEAR);
        int month = currentTime.get(Calendar.MONTH);
        month++;
        int day = currentTime.get(Calendar.DAY_OF_MONTH);
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);
        String YEAR = "" + year;
        String InpTimeStamp = YEAR + ":" + month + ":" + day + ":" + hour + ":" + minute;
        return InpTimeStamp;
    }
}