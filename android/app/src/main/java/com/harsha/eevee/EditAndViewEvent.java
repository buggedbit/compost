package com.harsha.eevee;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
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

public class EditAndViewEvent extends AppCompatActivity {

    EventDetailsDBHandler dbHandler;
    TimeInput startTimeEvent, endTimeEvent, startTimeEventA, endTimeEventA, regnTimeEvent;
    DateInput startDateEvent, endDateEvent, startDateEventB, endDateEventB, regnDateEvent;
    DurationInput durationEvent;

    boolean selectingTimeDoneEventA;
    String startDateStoreStringEventA, endDateStoreStingEventA;

    EditText eventNameET, eventPlaceET, startDateEventET, startTimeEventET, endDateEventET, endTimeEventET, startDateEventBET,
            endDateEventBET, startTimeEventAET, endTimeEventAET, durationEventAET, regnPlaceEventET, regnDateEventET,
            regnTimeEventET, regnWebsiteEventET, commentsEventET;

    Button cancelEditEventBTN, editEventBTN, viewEventsTableBTN, dropEventsTableBTN;

    RadioGroup repetitionEventRG, typeEventRG;

    RadioButton noneEventRB, dailyEventRB, weeklyEventRB;

    CheckBox sundayEventCB, mondayEventCB, tuesdayEventCB, wednesdayEventCB, thursdayEventCB, fridayEventCB, saturdayEventCB;

    LinearLayout layoutEventThree, layoutEventThreeA, layoutEventThreeB, dayEventCheckboxGrp,
            buttonPanelEventSix, eventViewPanelLL, buttonPanelEventViewDrop, layoutEventTwo, layoutEventFour;

    int id;
    boolean viewOnlyMode = true;
    MenuItem viewOnlyItem, editItem;
    int typeSize = Constants.FILTERS.typeFilters.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_and_view_event);

        settingUpIDRef();
        hideEditButtons();
        addTypesField();
        initialisingGlobalVariables();
        resetFields();

        if (getIntent() != null) {
            Bundle dataFromEventButton = getIntent().getExtras();
            id = dataFromEventButton.getInt("id");
        }

        fillEventData(id);
        editingDisabled();
        setUpTimeETThreeA();

        startDateEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(startDateEventET);
                    showDatePicker(startDateEventET, startDateEvent, startDateEvent, true);
                }
            }
        });

        startTimeEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(startTimeEventET);
                    showTimePicker(startTimeEventET, startTimeEvent, startTimeEvent, true);
                }
            }
        });

        startDateEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    startDateEvent.InitialiseDateVariables();
                    setDateText(startDateEventET, startDateEvent);
                }
                return true;
            }
        });

        startTimeEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    startTimeEvent.InitialiseTimeVariables();
                    setTimeText(startTimeEventET, startTimeEvent);
                }
                return true;
            }
        });

        endDateEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(endDateEventET);
                    showDatePicker(endDateEventET, endDateEvent, startDateEvent, false);
                }
            }
        });

        endTimeEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(endTimeEventET);
                    showTimePicker(endTimeEventET, endTimeEvent, startTimeEvent, false);
                }
            }
        });

        endDateEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    endDateEvent.InitialiseDateVariables();
                    setDateText(endDateEventET, endDateEvent);
                }
                return true;
            }
        });

        endTimeEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    endTimeEvent.InitialiseTimeVariables();
                    setTimeText(endTimeEventET, endTimeEvent);
                }
                return true;
            }
        });

        startTimeEventAET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(startTimeEventAET);
                    showTimePicker(startTimeEventAET, startTimeEventA, startTimeEventA, true);
                    // setDuration(startTimeEventA,endTimeEventA,durationEvent,durationEventAET);
                }
            }
        });

        durationEventAET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(durationEventAET);
                    showDurationPicker(durationEventAET, durationEvent);
                    //  setEndTime(startTimeEventA,endTimeEventA,durationEvent,endTimeEventAET);
                }
            }
        });

        endTimeEventAET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(endTimeEventAET);
                    showTimePicker(endTimeEventAET, endTimeEventA, startTimeEventA, false);
                    // setDuration(startTimeEventA,endTimeEventA,durationEvent,durationEventAET);
                }
            }
        });

        startTimeEventAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    selectingTimeDoneEventA = false;
                    setUpTimeETThreeA();
                }
                return true;
            }
        });

        durationEventAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    selectingTimeDoneEventA = false;
                    setUpTimeETThreeA();
                }
                return true;
            }
        });

        endTimeEventAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    selectingTimeDoneEventA = false;
                    setUpTimeETThreeA();
                }
                return true;
            }
        });

        startDateEventBET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(startDateEventBET);
                    showDatePicker(startDateEventBET, startDateEventB, startDateEventB, true);
                }
            }
        });

        endDateEventBET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(endDateEventBET);
                    showDatePicker(endDateEventBET, endDateEventB, startDateEventB, false);
                }
            }
        });

        startDateEventBET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    startDateEventB.InitialiseDateVariables();
                    setDateText(startDateEventBET, startDateEventB);
                }
                return true;
            }
        });

        endDateEventBET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    endDateEventB.InitialiseDateVariables();
                    setDateText(endDateEventBET, endDateEventB);
                }

                return true;
            }
        });

        regnDateEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(regnDateEventET);
                    showDatePicker(regnDateEventET, regnDateEvent, startDateEvent, true);
                }
            }
        });

        regnTimeEventET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(regnTimeEventET);
                    showTimePicker(regnTimeEventET, regnTimeEvent, startTimeEvent, true);
                }
            }
        });

        regnDateEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    regnDateEvent.InitialiseDateVariables();
                    setDateText(regnDateEventET, regnDateEvent);
                }
                return true;
            }
        });

        regnTimeEventET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    regnTimeEvent.InitialiseTimeVariables();
                    setTimeText(regnTimeEventET, regnTimeEvent);
                }
                return true;
            }
        });

        layoutEventTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardLinearLayout(layoutEventTwo);
            }
        });

        layoutEventFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardLinearLayout(layoutEventFour);
            }
        });

        viewEventsTableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(EditAndViewEvent.this, ShowEventsTable.class);
                String eventsTableToString = dbHandler.viewTable();
                changeActivity.putExtra("tableEventsData", eventsTableToString);
                startActivity(changeActivity);
            }
        });

        dropEventsTableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.dropTable();
                Toast.makeText(EditAndViewEvent.this, "EventsDataTable was dropped successfully", Toast.LENGTH_SHORT).show();
            }
        });

        editEventBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dailyEventRB.isChecked()) durationEvent.minDays = 1;
                else if (weeklyEventRB.isChecked()) {
                    if (sundayEventCB.isChecked() && mondayEventCB.isChecked() && tuesdayEventCB.isChecked() && wednesdayEventCB.isChecked()
                            && thursdayEventCB.isChecked() && fridayEventCB.isChecked() && saturdayEventCB.isChecked()) {
                        Toast.makeText(EditAndViewEvent.this, "Please change the repetition type of your event to daily!!", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!sundayEventCB.isChecked() && !mondayEventCB.isChecked() && !tuesdayEventCB.isChecked() && !wednesdayEventCB.isChecked()
                            && !thursdayEventCB.isChecked() && !fridayEventCB.isChecked() && !saturdayEventCB.isChecked()) {
                        Toast.makeText(EditAndViewEvent.this, "Please change the repetition type of your event to none!!", Toast.LENGTH_SHORT).show();
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

                if (noneEventRB.isChecked()) {
                    if (startDateEvent.dateDispString().matches("")) {
                        Toast.makeText(EditAndViewEvent.this, "Please fill in the start date of your event!!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (endDateEvent.dateDispString().matches("")) {
                        Toast.makeText(EditAndViewEvent.this, "Please fill in the end date of your event!!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(EditAndViewEvent.this, "Please check the end date/time of your event as it is inconsistent" +
                                " with your start date and time!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } else {
                    DateTime startDT = new DateTime("1:1:1" + ":" + startTimeEventA.timeStoreString());
                    DateTime endDT = new DateTime("1:1:1" + ":" + endTimeEventA.timeStoreString());

                    if (DateTime.differenceInMin(startDT, endDT) < 0) {
                        endDT.DAY += 1;
                    }
                    endDT.DAY += durationEvent.day;
                    if (DateTime.differenceInMin(startDT, endDT) > 1440 * durationEvent.minDays) {
                        Toast.makeText(EditAndViewEvent.this, "Please check the duration of your event", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    startDateStoreStringEventA = startDT.toString();
                    endDateStoreStingEventA = endDT.toString();

                    if (!startDateEventB.dateDispString().matches("") && !endDateEventB.dateDispString().matches("")) {
                        int diff = (60 * durationEvent.hour) + durationEvent.min + (60 * startTimeEventA.selectedHour) + startTimeEventA.selectedMin;
                        int days = durationEvent.day;
                        if (diff >= 1440) days += 1;
                        DateTime startDateTime = new DateTime(startDateEventB.dateStoreString() + ":" + "0:0");
                        DateTime endDateTime = new DateTime(endDateEventB.dateStoreString() + ":" + "0:0");

                        if (DateTime.differenceInMin(startDateTime, endDateTime) < 1440 * days) {
                            Toast.makeText(EditAndViewEvent.this, "Please check the end date of your event as it is inconsistent" +
                                    " with your start !!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                if (!regnTimeEvent.timeDispString().matches("") && regnDateEvent.dateDispString().matches("")) {
                    Toast.makeText(EditAndViewEvent.this, "Please fill in the regn date of your event!!", Toast.LENGTH_SHORT).show();
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

                dbHandler.updateRow(event, id);
                Toast.makeText(EditAndViewEvent.this, "Hey !! Your event was edited successfully", Toast.LENGTH_SHORT).show();
                setUpTimeETThreeA();
                editingEnabled();
                editingEnabledBTN();
                buttonPanelEventSix.setVisibility(View.GONE);
                Intent intent = new Intent(EditAndViewEvent.this, EventsHome.class);
                startActivity(intent);
            }
        });

        cancelEditEventBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPanelEventSix.setVisibility(View.GONE);
                resetFields();
                fillEventData(id);
                setUpTimeETThreeA();
                editingDisabled();
                editingDisabledBTN();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_view_delete, menu);
        editItem = menu.findItem(R.id.editItem);
        viewOnlyItem = menu.findItem(R.id.viewOnlyItem);
        if (viewOnlyMode) {
            viewOnlyItem.setVisible(false);
            editItem.setVisible(true);
        } else {
            viewOnlyItem.setVisible(true);
            editItem.setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.editItem:
                buttonPanelEventSix.setVisibility(View.VISIBLE);
                editingEnabled();
                editingEnabledBTN();
                return true;
            case R.id.viewOnlyItem:
                buttonPanelEventSix.setVisibility(View.GONE);
                editingDisabled();
                editingDisabledBTN();
                resetFields();
                fillEventData(id);
                setUpTimeETThreeA();
                return true;
            case R.id.deleteItem: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm your action");
                builder.setMessage("Are you sure about deleting this?");
                builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbHandler.deleteEvent(id);
                        dialog.dismiss();
                        Toast.makeText(EditAndViewEvent.this, "Hey your event was deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditAndViewEvent.this, EventsHome.class);
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
        TimePicker = new TimePickerDialog(EditAndViewEvent.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker timePicker, int selectedHour, int selectedMinute) {
                time.selectedHour = selectedHour;
                time.selectedMin = selectedMinute;
                time.selectingDone = true;
                setTimeText(name, time);
                setDuration(startTimeEventA, endTimeEventA, durationEvent, durationEventAET);
            }
        }, suggestionHour, suggestionMinute, DateFormat.is24HourFormat(EditAndViewEvent.this));
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
        DatePicker = new DatePickerDialog(EditAndViewEvent.this, new DatePickerDialog.OnDateSetListener() {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(EditAndViewEvent.this);
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

    public void hideKeyboardRG(RadioGroup name) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getApplicationWindowToken(), 0);
    }

    public void hideKeyboardLinearLayout(LinearLayout name) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getApplicationWindowToken(), 0);
    }

    public void settingUpIDRef() {
        eventViewPanelLL = (LinearLayout) findViewById(R.id.eventViewPanelLL);

        eventNameET = (EditText) eventViewPanelLL.findViewById(R.id.eventNameET);
        eventPlaceET = (EditText) eventViewPanelLL.findViewById(R.id.eventPlaceET);
        startDateEventET = (EditText) eventViewPanelLL.findViewById(R.id.startDateEventET);
        startTimeEventET = (EditText) eventViewPanelLL.findViewById(R.id.startTimeEventET);
        endDateEventET = (EditText) eventViewPanelLL.findViewById(R.id.endDateEventET);
        endTimeEventET = (EditText) eventViewPanelLL.findViewById(R.id.endTimeEventET);
        startDateEventBET = (EditText) eventViewPanelLL.findViewById(R.id.startDateEventBET);
        endDateEventBET = (EditText) eventViewPanelLL.findViewById(R.id.endDateEventBET);
        startTimeEventAET = (EditText) eventViewPanelLL.findViewById(R.id.startTimeEventAET);
        endTimeEventAET = (EditText) eventViewPanelLL.findViewById(R.id.endTimeEventAET);
        durationEventAET = (EditText) eventViewPanelLL.findViewById(R.id.durationEventAET);
        regnPlaceEventET = (EditText) eventViewPanelLL.findViewById(R.id.regnPlaceEventET);
        regnDateEventET = (EditText) eventViewPanelLL.findViewById(R.id.regnDateEventET);
        regnTimeEventET = (EditText) eventViewPanelLL.findViewById(R.id.regnTimeEventET);
        regnWebsiteEventET = (EditText) eventViewPanelLL.findViewById(R.id.regnWebsiteEventET);
        commentsEventET = (EditText) eventViewPanelLL.findViewById(R.id.commentsEventET);

        cancelEditEventBTN = (Button) eventViewPanelLL.findViewById(R.id.cancelEditEventBTN);
        editEventBTN = (Button) eventViewPanelLL.findViewById(R.id.editEventBTN);
        viewEventsTableBTN = (Button) eventViewPanelLL.findViewById(R.id.viewEventsTableBTN);
        dropEventsTableBTN = (Button) eventViewPanelLL.findViewById(R.id.dropEventsTableBTN);

        repetitionEventRG = (RadioGroup) eventViewPanelLL.findViewById(R.id.repetitionEventRG);
        typeEventRG = (RadioGroup) eventViewPanelLL.findViewById(R.id.typeEventRG);

        noneEventRB = (RadioButton) eventViewPanelLL.findViewById(R.id.noneEventRB);
        dailyEventRB = (RadioButton) eventViewPanelLL.findViewById(R.id.dailyEventRB);
        weeklyEventRB = (RadioButton) eventViewPanelLL.findViewById(R.id.weeklyEventRB);

        sundayEventCB = (CheckBox) eventViewPanelLL.findViewById(R.id.sundayEventCB);
        mondayEventCB = (CheckBox) eventViewPanelLL.findViewById(R.id.mondayEventCB);
        tuesdayEventCB = (CheckBox) eventViewPanelLL.findViewById(R.id.tuesdayEventCB);
        wednesdayEventCB = (CheckBox) eventViewPanelLL.findViewById(R.id.wednesdayEventCB);
        thursdayEventCB = (CheckBox) eventViewPanelLL.findViewById(R.id.thursdayEventCB);
        fridayEventCB = (CheckBox) eventViewPanelLL.findViewById(R.id.fridayEventCB);
        saturdayEventCB = (CheckBox) eventViewPanelLL.findViewById(R.id.saturdayEventCB);

        layoutEventThree = (LinearLayout) eventViewPanelLL.findViewById(R.id.layoutEventThree);
        layoutEventThreeA = (LinearLayout) eventViewPanelLL.findViewById(R.id.layoutEventThreeA);
        layoutEventThreeB = (LinearLayout) eventViewPanelLL.findViewById(R.id.layoutEventThreeB);
        dayEventCheckboxGrp = (LinearLayout) eventViewPanelLL.findViewById(R.id.dayEventCheckboxGrp);
        buttonPanelEventSix = (LinearLayout) eventViewPanelLL.findViewById(R.id.buttonPanelEventSix);
        buttonPanelEventViewDrop = (LinearLayout) eventViewPanelLL.findViewById(R.id.buttonPanelEventViewDrop);
        layoutEventTwo = (LinearLayout) eventViewPanelLL.findViewById(R.id.layoutEventTwo);
        layoutEventFour = (LinearLayout) eventViewPanelLL.findViewById(R.id.layoutEventFour);
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
        dbHandler = new EventDetailsDBHandler(this, null, null, 1);
    }

    public void resetFields() {
        viewOnlyMode = true;
        RadioGroup typeEventRGLocal = (RadioGroup) eventViewPanelLL.findViewById(R.id.typeEventRG);
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

    public void hideEditButtons() {
        buttonPanelEventSix.setVisibility(View.GONE);
    }

    public void fillEventData(int id) {
        String[] eventData = dbHandler.getEventData(id);
        if (!eventData[0].matches(Constants.EVENTNAME_NOTSET)) eventNameET.setText(eventData[0]);
        if (!eventData[1].matches(Constants.EVENTPLACE_NOTSET)) eventPlaceET.setText(eventData[1]);

        setRepetitionType(eventData[4]);
        setEventType(eventData[5]);
        if (!eventData[6].matches(Constants.COMMENTS_NOTSET)) commentsEventET.setText(eventData[6]);
        setDateAndTime(eventData);
        setRegnDetails(eventData);
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

    public void setDateAndTime(String[] eventData) {
        if (noneEventRB.isChecked()) {
            DateTime startDT = new DateTime(eventData[2]);
            startDT.setDateInput(startDateEvent);
            startDT.setTimeInput(startTimeEvent);
            DateTime endDT = new DateTime(eventData[3]);
            endDT.setDateInput(endDateEvent);
            endDT.setTimeInput(endTimeEvent);
            setDateText(startDateEventET, startDateEvent);
            setTimeText(startTimeEventET, startTimeEvent);
            setDateText(endDateEventET, endDateEvent);
            setTimeText(endTimeEventET, endTimeEvent);
        } else {
            DateTime startDT = new DateTime(eventData[2]);
            startDT.setTimeInput(startTimeEventA);
            DateTime endDT = new DateTime(eventData[3]);
            endDT.setTimeInput(endTimeEventA);
            durationEvent.day = (DateTime.differenceInMin(startDT, endDT) / 1440);
            selectingTimeDoneEventA = true;
            setUpTimeETThreeA();

            if (!eventData[10].matches(Constants.STARTS_FROM_NOTSET)) {
                startDT = new DateTime(eventData[10]);
                startDT.setDateInput(startDateEventB);
                setDateText(startDateEventBET, startDateEventB);
            }

            if (!eventData[11].matches(Constants.ENDS_ON_NOTSET)) {
                endDT = new DateTime(eventData[11]);
                endDT.setDateInput(endDateEventB);
                setDateText(endDateEventBET, endDateEventB);
            }
        }
    }

    public void setRegnDetails(String[] eventData) {
        if (!eventData[7].matches(Constants.REGNPLACE_NOTSET))
            regnPlaceEventET.setText(eventData[7]);
        if (!eventData[8].matches(Constants.WEBSITE_NOTSET))
            regnWebsiteEventET.setText(eventData[8]);
        if (!eventData[9].matches(Constants.REGNDATETIME_NOTSET)) {
            DateTime RegnDT = new DateTime(eventData[9]);
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

    public void editingEnabledET(EditText name) {
        name.setClickable(true);
        name.setCursorVisible(true);
        name.setFocusable(true);
        name.setFocusableInTouchMode(true);
    }

    public void editingDisabledET(EditText name) {
        name.setClickable(false);
        name.setCursorVisible(false);
        name.setFocusable(false);
        name.setFocusableInTouchMode(false);
    }

    public void editingEnabledETCursor(EditText name) {
        name.setCursorVisible(true);
    }

    public void editingDisabledETCursor(EditText name) {
        name.setCursorVisible(false);
    }

    public void editingEnabledRB(RadioButton name) {
        name.setClickable(true);
    }

    public void editingDisabledRB(RadioButton name) {
        name.setClickable(false);
    }

    public void editingEnabledCB(CheckBox name) {
        name.setClickable(true);
    }

    public void editingDisabledCB(CheckBox name) {
        name.setClickable(false);
    }

    public void editingEnabledCG() {
        editingEnabledCB(sundayEventCB);
        editingEnabledCB(mondayEventCB);
        editingEnabledCB(tuesdayEventCB);
        editingEnabledCB(wednesdayEventCB);
        editingEnabledCB(thursdayEventCB);
        editingEnabledCB(fridayEventCB);
        editingEnabledCB(saturdayEventCB);
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

    public void editingDisabled() {
        editingDisabledET(eventNameET);
        editingDisabledET(eventPlaceET);
        editingDisabledET(regnPlaceEventET);
        editingDisabledET(regnDateEventET);
        editingDisabledET(regnWebsiteEventET);
        editingDisabledET(commentsEventET);
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

    public void editingEnabled() {
        editingEnabledET(eventNameET);
        editingEnabledET(eventPlaceET);
        editingEnabledET(regnPlaceEventET);
        editingEnabledET(regnDateEventET);
        editingEnabledET(regnWebsiteEventET);
        editingEnabledET(commentsEventET);
        editingEnabledETCursor(startDateEventET);
        editingEnabledETCursor(endDateEventET);
        editingEnabledETCursor(startDateEventBET);
        editingEnabledETCursor(endDateEventBET);
        editingEnabledETCursor(startTimeEventET);
        editingEnabledETCursor(endTimeEventET);
        editingEnabledETCursor(startTimeEventAET);
        editingEnabledETCursor(endTimeEventAET);
        editingEnabledETCursor(durationEventAET);
        editingEnabledETCursor(regnDateEventET);
        editingEnabledETCursor(regnTimeEventET);
        for (int i = 0; i < repetitionEventRG.getChildCount(); i++) {
            editingEnabledRB((RadioButton) repetitionEventRG.getChildAt(i));
        }
        for (int i = 0; i < typeEventRG.getChildCount(); i++) {
            editingEnabledRB((RadioButton) typeEventRG.getChildAt(i));
        }
        editingEnabledCG();
    }

    public void editingEnabledBTN() {
        viewOnlyMode = false;
        editItem.setVisible(false);
        viewOnlyItem.setVisible(true);
    }

    public void editingDisabledBTN() {
        viewOnlyMode = true;
        editItem.setVisible(true);
        viewOnlyItem.setVisible(false);
    }
}