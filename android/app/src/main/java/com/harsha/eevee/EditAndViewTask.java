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

public class EditAndViewTask extends AppCompatActivity {

    TaskDetailsDBHandler dbHandler;
    TimeInput startTimeTask, endTimeTask, startTimeTaskA, endTimeTaskA;
    DateInput startDateTask, endDateTask, startDateTaskB, endDateTaskB;
    DurationInput durationTask;

    boolean selectingTimeDoneTaskA;
    String startDateStoreStringTaskA, endDateStoreStingTaskA;

    EditText taskTagET, taskDescriptionET, startDateTaskET, startTimeTaskET, endDateTaskET, endTimeTaskET, startDateTaskBET,
            endDateTaskBET, startTimeTaskAET, endTimeTaskAET, durationTaskAET;

    Button cancelEditTaskBTN, editTaskBTN, viewTasksTableBTN, dropTasksTableBTN;

    RadioGroup repetitionTaskRG, typeTaskRG;

    RadioButton noneTaskRB, dailyTaskRB, weeklyTaskRB;

    CheckBox sundayTaskCB, mondayTaskCB, tuesdayTaskCB, wednesdayTaskCB, thursdayTaskCB, fridayTaskCB, saturdayTaskCB;

    LinearLayout layoutTaskThree, layoutTaskThreeA, layoutTaskThreeB, dayTaskCheckboxGrp,
            buttonPanelTaskFour, taskViewPanelLL, buttonPanelTaskViewDrop, layoutTaskTwo, layoutTaskFour;
    int typeSize = Constants.FILTERS.typeFilters.length;

    int id;
    boolean viewOnlyMode = true;
    MenuItem viewOnlyItem, editItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_and_view_task);

        settingUpIDRef();
        initialisingGlobalVariables();

        if (getIntent() != null) {
            Bundle dataFromTaskButton = getIntent().getExtras();
            id = dataFromTaskButton.getInt("id");
        }
        addTypesField();
        resetFields();
        hideEditButtons();
        editingDisabled();
        fillTaskData(id);
        setUpTimeETThreeA();

        startDateTaskET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast.makeText(EditAndViewTask.this, String.valueOf(viewOnlyMode), Toast.LENGTH_SHORT).show();
                if (!viewOnlyMode) {
                    hideKeyboard(startDateTaskET);
                    showDatePicker(startDateTaskET, startDateTask, startDateTask, true);
                }
            }
        });

        startTimeTaskET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(startTimeTaskET);
                    showTimePicker(startTimeTaskET, startTimeTask, startTimeTask, true);
                }
            }
        });

        startDateTaskET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    startDateTask.InitialiseDateVariables();
                    setDateText(startDateTaskET, startDateTask);
                }
                return true;
            }
        });

        startTimeTaskET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    startTimeTask.InitialiseTimeVariables();
                    setTimeText(startTimeTaskET, startTimeTask);
                }
                return true;
            }
        });

        endDateTaskET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(endDateTaskET);
                    showDatePicker(endDateTaskET, endDateTask, startDateTask, false);
                }
            }
        });

        endTimeTaskET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(endTimeTaskET);
                    showTimePicker(endTimeTaskET, endTimeTask, startTimeTask, false);
                }
            }
        });

        endDateTaskET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    endDateTask.InitialiseDateVariables();
                    setDateText(endDateTaskET, endDateTask);
                }
                return true;
            }
        });

        endTimeTaskET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    endTimeTask.InitialiseTimeVariables();
                    setTimeText(endTimeTaskET, endTimeTask);
                }
                return true;
            }
        });

        startTimeTaskAET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(startTimeTaskAET);
                    showTimePicker(startTimeTaskAET, startTimeTaskA, startTimeTaskA, true);
                    // setDuration(startTimeTaskA,endTimeTaskA,durationTask,durationTaskAET);
                }
            }
        });

        durationTaskAET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(durationTaskAET);
                    showDurationPicker(durationTaskAET, durationTask);
                    //  setEndTime(startTimeTaskA,endTimeTaskA,durationTask,endTimeTaskAET);
                }
            }
        });

        endTimeTaskAET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(endTimeTaskAET);
                    showTimePicker(endTimeTaskAET, endTimeTaskA, startTimeTaskA, false);
                    // setDuration(startTimeTaskA,endTimeTaskA,durationTask,durationTaskAET);
                }
            }
        });

        startTimeTaskAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    selectingTimeDoneTaskA = false;
                    setUpTimeETThreeA();
                }
                return true;
            }
        });

        durationTaskAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    selectingTimeDoneTaskA = false;
                    setUpTimeETThreeA();
                }
                return true;
            }
        });

        endTimeTaskAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    selectingTimeDoneTaskA = false;
                    setUpTimeETThreeA();
                }
                return true;
            }
        });

        startDateTaskBET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(startDateTaskBET);
                    showDatePicker(startDateTaskBET, startDateTaskB, startDateTaskB, true);
                }
            }
        });

        endDateTaskBET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (!viewOnlyMode) {
                    hideKeyboard(endDateTaskBET);
                    showDatePicker(endDateTaskBET, endDateTaskB, startDateTaskB, false);
                }
            }
        });

        startDateTaskBET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    startDateTaskB.InitialiseDateVariables();
                    setDateText(startDateTaskBET, startDateTaskB);
                }
                return true;
            }
        });

        endDateTaskBET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!viewOnlyMode) {
                    endDateTaskB.InitialiseDateVariables();
                    setDateText(endDateTaskBET, endDateTaskB);
                }
                return true;
            }
        });

        viewTasksTableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(EditAndViewTask.this, ShowTasksTable.class);
                String tasksTableToString = dbHandler.viewTable();
                changeActivity.putExtra("tableTasksData", tasksTableToString);
                startActivity(changeActivity);
            }
        });

        dropTasksTableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.dropTable();
                Toast.makeText(EditAndViewTask.this, "TasksDataTable was dropped successfully", Toast.LENGTH_SHORT).show();
            }
        });

        layoutTaskTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardLinearLayout(layoutTaskTwo);
            }
        });

        layoutTaskFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboardLinearLayout(layoutTaskTwo);
            }
        });

        editTaskBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (dailyTaskRB.isChecked()) durationTask.minDays = 1;
                else if (weeklyTaskRB.isChecked()) {
                    if (sundayTaskCB.isChecked() && mondayTaskCB.isChecked() && tuesdayTaskCB.isChecked() && wednesdayTaskCB.isChecked()
                            && thursdayTaskCB.isChecked() && fridayTaskCB.isChecked() && saturdayTaskCB.isChecked()) {
                        Toast.makeText(EditAndViewTask.this, "Please change the repetition type of your task to daily!!", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (!sundayTaskCB.isChecked() && !mondayTaskCB.isChecked() && !tuesdayTaskCB.isChecked() && !wednesdayTaskCB.isChecked()
                            && !thursdayTaskCB.isChecked() && !fridayTaskCB.isChecked() && !saturdayTaskCB.isChecked()) {
                        Toast.makeText(EditAndViewTask.this, "Please change the repetition type of your task to none!!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean[] daysChecked = {false, false, false, false, false, false, false};

                    if (sundayTaskCB.isChecked()) daysChecked[0] = true;
                    if (mondayTaskCB.isChecked()) daysChecked[1] = true;
                    if (tuesdayTaskCB.isChecked()) daysChecked[2] = true;
                    if (wednesdayTaskCB.isChecked()) daysChecked[3] = true;
                    if (thursdayTaskCB.isChecked()) daysChecked[4] = true;
                    if (fridayTaskCB.isChecked()) daysChecked[5] = true;
                    if (saturdayTaskCB.isChecked()) daysChecked[6] = true;

                    durationTask.minDays = 7;
                    for (int i = 0; i < 6; i++) {
                        for (int j = i + 1; j < 7; j++) {
                            if (daysChecked[i] && daysChecked[j]) {
                                durationTask.minDays = min(durationTask.minDays, j - i);
                                durationTask.minDays = min(durationTask.minDays, 7 + i - j);
                            }
                        }
                    }
                }
                setUpTimeETThreeA();

                if (noneTaskRB.isChecked()) {
                    if (startDateTask.dateDispString().matches("") && !startTimeTask.timeDispString().matches("")) {
                        Toast.makeText(EditAndViewTask.this, "Please fill in the start date of your task!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (endDateTask.dateDispString().matches("") && !endTimeTask.timeDispString().matches("")) {
                        Toast.makeText(EditAndViewTask.this, "Please fill in the end date of your task!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (startTimeTask.timeDispString().matches("") && !startDateTask.dateDispString().matches("")) {
                        startTimeTask.selectedMin = 0;
                        startTimeTask.selectedHour = 0;
                    }
                    if (endTimeTask.timeDispString().matches("") && !endDateTask.dateDispString().matches("")) {
                        if (!startDateTask.dateDispString().matches("")) {
                            if (!DateInput.isGreater(endDateTask, startDateTask)) {
                                endTimeTask.selectedMin = 59;
                                endTimeTask.selectedHour = 23;
                            } else {
                                endTimeTask.selectedHour = 0;
                                endTimeTask.selectedMin = 0;
                            }
                        } else {
                            endTimeTask.selectedHour = 0;
                            endTimeTask.selectedMin = 0;
                        }
                    }

                    if (!startDateTask.dateDispString().matches("") && !endDateTask.dateDispString().matches("")) {
                        DateTime startDT = new DateTime(startDateTask.dateStoreString() + ":" + startTimeTask.timeStoreString());
                        DateTime endDT = new DateTime(endDateTask.dateStoreString() + ":" + endTimeTask.timeStoreString());
                        if (DateTime.differenceInMin(startDT, endDT) < 0) {
                            Toast.makeText(EditAndViewTask.this, "Please check the end date/time of your task task_as it is inconsistent" +
                                    " with your start date and time!!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                } else {
                    DateTime startDT = new DateTime("1:1:1" + ":" + startTimeTaskA.timeStoreString());
                    DateTime endDT = new DateTime("1:1:1" + ":" + endTimeTaskA.timeStoreString());

                    if (DateTime.differenceInMin(startDT, endDT) < 0) {
                        endDT.DAY += 1;
                    }
                    endDT.DAY += durationTask.day;
                    if (DateTime.differenceInMin(startDT, endDT) > 1440 * durationTask.minDays) {
                        Toast.makeText(EditAndViewTask.this, "Please check the duration of your task", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    startDateStoreStringTaskA = startDT.toString();
                    endDateStoreStingTaskA = endDT.toString();

                    if (!startDateTaskB.dateDispString().matches("") && !endDateTaskB.dateDispString().matches("")) {
                        int diff = (60 * durationTask.hour) + durationTask.min + (60 * startTimeTaskA.selectedHour) + startTimeTaskA.selectedMin;
                        int days = durationTask.day;
                        if (diff >= 1440) days += 1;
                        DateTime startDateTime = new DateTime(startDateTaskB.dateStoreString() + ":" + "0:0");
                        DateTime endDateTime = new DateTime(endDateTaskB.dateStoreString() + ":" + "0:0");

                        if (DateTime.differenceInMin(startDateTime, endDateTime) < 1440 * days) {
                            Toast.makeText(EditAndViewTask.this, "Please check the end date of your task as it is inconsistent" +
                                    " with your start !!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                String repetitionAsInt = getRepetitionAsInt();
                String nameOfType = getNameOfType();
                String inpTaskDescription = taskDescriptionET.getText().toString();
                String inpTaskTag = taskTagET.getText().toString();

                String inpStartDateTime, inpEndDateTime;
                if (noneTaskRB.isChecked()) {
                    if (startDateTask.dateDispString().matches("")) {
                        inpStartDateTime = Constants.STARTDATETIME_NOTSET;
                    } else {
                        inpStartDateTime = startDateTask.dateStoreString() + ":" + startTimeTask.timeStoreString();
                    }
                    if (endDateTask.dateDispString().matches("")) {
                        inpEndDateTime = Constants.ENDDATETIME_NOTSET;
                    } else {
                        inpEndDateTime = endDateTask.dateStoreString() + ":" + endTimeTask.timeStoreString();
                    }
                } else {
                    inpStartDateTime = startDateStoreStringTaskA;
                    inpEndDateTime = endDateStoreStingTaskA;
                }

                String inpTimeStamp = getTimeStamp();
                String inpStartDateDailyOrWeekly, inpEndDateDailyOrWeekly;

                if (inpTaskDescription.matches(""))
                    inpTaskDescription = Constants.TASKDESCRIPTION_NOTSET;
                if (inpTaskTag.matches("")) inpTaskTag = Constants.TASKTAG_NOTSET;

                if (noneTaskRB.isChecked()) {
                    inpStartDateDailyOrWeekly = Constants.STARTS_FROM_NOT_APPLICABLE;
                    inpEndDateDailyOrWeekly = Constants.ENDS_ON_NOT_APPLICABLE;
                } else {
                    if (!startDateTaskB.dateDispString().matches("")) {
                        inpStartDateDailyOrWeekly = startDateTaskB.dateStoreString() + ":" + "0:0";
                    } else {
                        inpStartDateDailyOrWeekly = Constants.STARTS_FROM_NOTSET;
                    }
                    if (!endDateTaskB.dateDispString().matches("")) {
                        inpEndDateDailyOrWeekly = endDateTaskB.dateStoreString() + ":" + "0:0";
                    } else {
                        inpEndDateDailyOrWeekly = Constants.ENDS_ON_NOTSET;
                    }
                }

                TaskDetails task = new TaskDetails();

                task.set_TimeStamp(inpTimeStamp);
                task.set_TaskDescription(inpTaskDescription);
                task.set_TaskTag(inpTaskTag);
                task.set_StartDateTime(inpStartDateTime);
                task.set_EndDateTime(inpEndDateTime);
                task.set_Repetition(repetitionAsInt);
                task.set_Type(nameOfType);
                task.set_StartsFromDailyOrWeekly(inpStartDateDailyOrWeekly);
                task.set_EndsFromDailyOrWeekly(inpEndDateDailyOrWeekly);

                dbHandler.updateRow(task, id);
                Toast.makeText(EditAndViewTask.this, "Hey !! Your task was edited successfully", Toast.LENGTH_SHORT).show();
                setUpTimeETThreeA();
                editingEnabled();
                editingEnabledBTN();
                buttonPanelTaskFour.setVisibility(View.GONE);
                Intent intent = new Intent(EditAndViewTask.this, TasksHome.class);
                startActivity(intent);
            }
        });

        cancelEditTaskBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonPanelTaskFour.setVisibility(View.GONE);
                resetFields();
                fillTaskData(id);
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
                buttonPanelTaskFour.setVisibility(View.VISIBLE);
                editingEnabled();
                editingEnabledBTN();
                return true;
            case R.id.viewOnlyItem:
                buttonPanelTaskFour.setVisibility(View.GONE);
                editingDisabled();
                editingDisabledBTN();
                resetFields();
                fillTaskData(id);
                setUpTimeETThreeA();
                return true;
            case R.id.deleteItem: {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm your action");
                builder.setMessage("Are you sure about deleting this?");
                builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dbHandler.deleteTask(id);
                        dialog.dismiss();
                        Toast.makeText(EditAndViewTask.this, "Hey your task was deleted successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditAndViewTask.this, TasksHome.class);
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
        TimePicker = new TimePickerDialog(EditAndViewTask.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker timePicker, int selectedHour, int selectedMinute) {
                time.selectedHour = selectedHour;
                time.selectedMin = selectedMinute;
                time.selectingDone = true;
                setTimeText(name, time);
                setDuration(startTimeTaskA, endTimeTaskA, durationTask, durationTaskAET);
            }
        }, suggestionHour, suggestionMinute, DateFormat.is24HourFormat(EditAndViewTask.this));
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
        DatePicker = new DatePickerDialog(EditAndViewTask.this, new DatePickerDialog.OnDateSetListener() {
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
        dayNP.setMaxValue(durationTask.minDays);
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

        AlertDialog.Builder builder = new AlertDialog.Builder(EditAndViewTask.this);
        builder.setTitle("Set Duration:");
        builder.setView(durationView);
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        duration.day = dayNP.getValue();
                        duration.hour = hourNP.getValue();
                        duration.min = minNP.getValue();
                        setDurationText(name, duration);
                        setEndTime(startTimeTaskA, endTimeTaskA, durationTask, endTimeTaskAET);
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

    public void hideKeyboardLinearLayout(LinearLayout name) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getApplicationWindowToken(), 0);
    }

    public void hideKeyboardRadioGroup(RadioGroup name) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getApplicationWindowToken(), 0);
    }

    public void settingUpIDRef() {
        taskViewPanelLL = (LinearLayout) findViewById(R.id.taskViewPanelLL);

        taskTagET = (EditText) taskViewPanelLL.findViewById(R.id.taskTagET);
        taskDescriptionET = (EditText) taskViewPanelLL.findViewById(R.id.taskDescriptionET);
        startDateTaskET = (EditText) taskViewPanelLL.findViewById(R.id.startDateTaskET);
        startTimeTaskET = (EditText) taskViewPanelLL.findViewById(R.id.startTimeTaskET);
        endDateTaskET = (EditText) taskViewPanelLL.findViewById(R.id.endDateTaskET);
        endTimeTaskET = (EditText) taskViewPanelLL.findViewById(R.id.endTimeTaskET);
        startDateTaskBET = (EditText) taskViewPanelLL.findViewById(R.id.startDateTaskBET);
        endDateTaskBET = (EditText) taskViewPanelLL.findViewById(R.id.endDateTaskBET);
        startTimeTaskAET = (EditText) taskViewPanelLL.findViewById(R.id.startTimeTaskAET);
        endTimeTaskAET = (EditText) taskViewPanelLL.findViewById(R.id.endTimeTaskAET);
        durationTaskAET = (EditText) taskViewPanelLL.findViewById(R.id.durationTaskAET);

        cancelEditTaskBTN = (Button) taskViewPanelLL.findViewById(R.id.cancelEditTaskBTN);
        editTaskBTN = (Button) taskViewPanelLL.findViewById(R.id.editTaskBTN);
        viewTasksTableBTN = (Button) taskViewPanelLL.findViewById(R.id.viewTasksTableBTN);
        dropTasksTableBTN = (Button) taskViewPanelLL.findViewById(R.id.dropTasksTableBTN);

        repetitionTaskRG = (RadioGroup) taskViewPanelLL.findViewById(R.id.repetitionTaskRG);
        typeTaskRG = (RadioGroup) taskViewPanelLL.findViewById(R.id.typeTaskRG);

        noneTaskRB = (RadioButton) taskViewPanelLL.findViewById(R.id.noneTaskRB);
        dailyTaskRB = (RadioButton) taskViewPanelLL.findViewById(R.id.dailyTaskRB);
        weeklyTaskRB = (RadioButton) taskViewPanelLL.findViewById(R.id.weeklyTaskRB);

        sundayTaskCB = (CheckBox) taskViewPanelLL.findViewById(R.id.sundayTaskCB);
        mondayTaskCB = (CheckBox) taskViewPanelLL.findViewById(R.id.mondayTaskCB);
        tuesdayTaskCB = (CheckBox) taskViewPanelLL.findViewById(R.id.tuesdayTaskCB);
        wednesdayTaskCB = (CheckBox) taskViewPanelLL.findViewById(R.id.wednesdayTaskCB);
        thursdayTaskCB = (CheckBox) taskViewPanelLL.findViewById(R.id.thursdayTaskCB);
        fridayTaskCB = (CheckBox) taskViewPanelLL.findViewById(R.id.fridayTaskCB);
        saturdayTaskCB = (CheckBox) taskViewPanelLL.findViewById(R.id.saturdayTaskCB);

        layoutTaskThree = (LinearLayout) taskViewPanelLL.findViewById(R.id.layoutTaskThree);
        layoutTaskThreeA = (LinearLayout) taskViewPanelLL.findViewById(R.id.layoutTaskThreeA);
        layoutTaskThreeB = (LinearLayout) taskViewPanelLL.findViewById(R.id.layoutTaskThreeB);
        dayTaskCheckboxGrp = (LinearLayout) taskViewPanelLL.findViewById(R.id.dayTaskCheckboxGrp);
        buttonPanelTaskFour = (LinearLayout) taskViewPanelLL.findViewById(R.id.buttonPanelTaskFour);
        buttonPanelTaskViewDrop = (LinearLayout) taskViewPanelLL.findViewById(R.id.buttonPanelTaskViewDrop);
        layoutTaskTwo = (LinearLayout) taskViewPanelLL.findViewById(R.id.layoutTaskTwo);
        layoutTaskFour = (LinearLayout) taskViewPanelLL.findViewById(R.id.layoutTaskFour);
    }

    public void initialisingGlobalVariables() {
        startTimeTask = new TimeInput();
        endTimeTask = new TimeInput();
        startTimeTaskA = new TimeInput();
        endTimeTaskA = new TimeInput();
        startDateTask = new DateInput();
        endDateTask = new DateInput();
        startDateTaskB = new DateInput();
        endDateTaskB = new DateInput();
        durationTask = new DurationInput();
        dbHandler = new TaskDetailsDBHandler(this, null, null, 1);
    }

    public void resetFields() {
        viewOnlyMode = true;
        RadioGroup typeTaskRGLocal = (RadioGroup) taskViewPanelLL.findViewById(R.id.typeTaskRG);
        RadioButton miscRB = (RadioButton) typeTaskRGLocal.findViewById(7);

        selectingTimeDoneTaskA = false;
        startDateStoreStringTaskA = "";
        endDateStoreStingTaskA = "";

        startTimeTask.InitialiseTimeVariables();
        endTimeTask.InitialiseTimeVariables();
        startTimeTaskA.InitialiseTimeVariables();
        endTimeTaskA.InitialiseTimeVariables();
        startDateTask.InitialiseDateVariables();
        endDateTask.InitialiseDateVariables();
        startDateTaskB.InitialiseDateVariables();
        endDateTaskB.InitialiseDateVariables();
        durationTask.initialiseVariables();

        layoutTaskThree.setVisibility(View.GONE);
        layoutTaskThreeA.setVisibility(View.GONE);
        layoutTaskThreeB.setVisibility(View.GONE);
        dayTaskCheckboxGrp.setVisibility(View.GONE);

        setETEmpty();
        uncheckWeekDaysCB();
        repetitionTaskRG.clearCheck();
        typeTaskRG.clearCheck();
        noneTaskRB.setChecked(true);
        miscRB.setChecked(true);
    }

    public void noneTaskRBClicked(View view) {
        hideKeyboardRadioGroup(repetitionTaskRG);
        dayTaskCheckboxGrp.setVisibility(View.GONE);
        layoutTaskThreeA.setVisibility(View.GONE);
        layoutTaskThreeB.setVisibility(View.GONE);
        layoutTaskThree.setVisibility(View.VISIBLE);
    }

    public void dailyTaskRBClicked(View view) {
        hideKeyboardRadioGroup(repetitionTaskRG);
        dayTaskCheckboxGrp.setVisibility(View.GONE);
        layoutTaskThree.setVisibility(View.GONE);
        layoutTaskThreeA.setVisibility(View.VISIBLE);
        layoutTaskThreeB.setVisibility(View.VISIBLE);
    }

    public void weeklyTaskRBClicked(View view) {
        hideKeyboardRadioGroup(repetitionTaskRG);
        dayTaskCheckboxGrp.setVisibility(View.VISIBLE);
        layoutTaskThree.setVisibility(View.GONE);
        layoutTaskThreeA.setVisibility(View.VISIBLE);
        layoutTaskThreeB.setVisibility(View.VISIBLE);
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
            typeTaskRG.addView(typeRB[i], p);
        }
    }

    public void setUpTimeETThreeA() {
        if (!selectingTimeDoneTaskA) {
            Calendar currentTime = Calendar.getInstance();
            startTimeTaskA.selectedHour = currentTime.get(Calendar.HOUR_OF_DAY);
            startTimeTaskA.selectedMin = currentTime.get(Calendar.MINUTE);

            endTimeTaskA.selectedHour = startTimeTaskA.selectedHour + 2;
            endTimeTaskA.selectedMin = startTimeTaskA.selectedMin;

            durationTask.day = 0;
            durationTask.hour = 2;
            durationTask.min = 0;

            if (endTimeTaskA.selectedHour >= 24) endTimeTaskA.selectedHour -= 24;
            setTimeText(startTimeTaskAET, startTimeTaskA);
            setTimeText(endTimeTaskAET, endTimeTaskA);
            setDurationText(durationTaskAET, durationTask);
            selectingTimeDoneTaskA = true;
            startTimeTaskA.selectingDone = true;
            endTimeTaskA.selectingDone = true;
        } else {
            setTimeText(startTimeTaskAET, startTimeTaskA);
            setTimeText(endTimeTaskAET, endTimeTaskA);
            setDuration(startTimeTaskA, endTimeTaskA, durationTask, durationTaskAET);
        }
    }

    public void setETEmpty() {
        taskTagET.setText("");
        taskDescriptionET.setText("");
        startDateTaskET.setText("");
        startTimeTaskET.setText("");
        endDateTaskET.setText("");
        endTimeTaskET.setText("");
        startDateTaskBET.setText("");
        endDateTaskBET.setText("");
        startTimeTaskAET.setText("");
        endTimeTaskAET.setText("");
        durationTaskAET.setText("");
    }

    public void uncheckWeekDaysCB() {
        if (sundayTaskCB.isChecked()) sundayTaskCB.toggle();
        if (mondayTaskCB.isChecked()) mondayTaskCB.toggle();
        if (tuesdayTaskCB.isChecked()) tuesdayTaskCB.toggle();
        if (wednesdayTaskCB.isChecked()) wednesdayTaskCB.toggle();
        if (thursdayTaskCB.isChecked()) thursdayTaskCB.toggle();
        if (fridayTaskCB.isChecked()) fridayTaskCB.toggle();
        if (saturdayTaskCB.isChecked()) saturdayTaskCB.toggle();
    }

    public String getRepetitionAsInt() {
        String repetitionAsInt = "";
        if (noneTaskRB.isChecked()) {
            repetitionAsInt = "0000000";
        } else if (dailyTaskRB.isChecked()) {
            repetitionAsInt = "1111111";
        } else {
            if (sundayTaskCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (mondayTaskCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (tuesdayTaskCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (wednesdayTaskCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (thursdayTaskCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (fridayTaskCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";

            if (saturdayTaskCB.isChecked()) repetitionAsInt += "1";
            else repetitionAsInt += "0";
        }
        return repetitionAsInt;
    }

    public String getNameOfType() {
        String nameOfType = "";
        int j;
        for (j = 1; j <= typeSize; j++) {
            RadioButton typeOfTaskRB = (RadioButton) typeTaskRG.findViewById(j);
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
        buttonPanelTaskFour.setVisibility(View.GONE);
    }

    public void fillTaskData(int id) {
        String[] taskData = dbHandler.getTaskData(id);
        if (!taskData[0].matches(Constants.TASKDESCRIPTION_NOTSET))
            taskDescriptionET.setText(taskData[0]);
        if (!taskData[1].matches(Constants.TASKTAG_NOTSET)) taskTagET.setText(taskData[1]);

        setRepetitionType(taskData[4]);
        setTaskType(taskData[5]);
        setDateAndTime(taskData);
    }

    public void setRepetitionType(String repetition) {
        if (repetition.matches("0000000")) {
            noneTaskRB.setChecked(true);
            hideLayoutsNoneChecked();
        } else if (repetition.matches("1111111")) {
            dailyTaskRB.setChecked(true);
            hideLayoutsDailyOrWeeklyChecked();
        } else {
            weeklyTaskRB.setChecked(true);
            boolean[] weekDaysChecked = new boolean[7];
            for (int i = 0; i < 7; i++) {
                if (String.valueOf(repetition.charAt(i)).matches("0")) weekDaysChecked[i] = false;
                else weekDaysChecked[i] = true;
            }
            if (weekDaysChecked[0]) sundayTaskCB.setChecked(true);
            if (weekDaysChecked[1]) mondayTaskCB.setChecked(true);
            if (weekDaysChecked[2]) tuesdayTaskCB.setChecked(true);
            if (weekDaysChecked[3]) wednesdayTaskCB.setChecked(true);
            if (weekDaysChecked[4]) thursdayTaskCB.setChecked(true);
            if (weekDaysChecked[5]) fridayTaskCB.setChecked(true);
            if (weekDaysChecked[6]) saturdayTaskCB.setChecked(true);
            dayTaskCheckboxGrp.setVisibility(View.VISIBLE);
            hideLayoutsDailyOrWeeklyChecked();
        }
    }

    public void setTaskType(String type) {
        int i;
        for (i = 0; i < typeSize; i++) {
            String temp = Constants.FILTERS.typeFilters[i];
            if (temp.matches(type)) break;
        }

        RadioButton typeToBeCheckedRB = (RadioButton) typeTaskRG.findViewById(i + 1);
        typeToBeCheckedRB.setChecked(true);
    }

    public void setDateAndTime(String[] taskData) {
        if (noneTaskRB.isChecked()) {
            if (!taskData[2].matches(Constants.STARTDATETIME_NOTSET)) {
                DateTime startDT = new DateTime(taskData[2]);
                startDT.setDateInput(startDateTask);
                startDT.setTimeInput(startTimeTask);
                setDateText(startDateTaskET, startDateTask);
                setTimeText(startTimeTaskET, startTimeTask);
            }

            if (!taskData[3].matches(Constants.ENDDATETIME_NOTSET)) {
                DateTime endDT = new DateTime(taskData[3]);
                endDT.setDateInput(endDateTask);
                endDT.setTimeInput(endTimeTask);
                setDateText(endDateTaskET, endDateTask);
                setTimeText(endTimeTaskET, endTimeTask);
            }
        } else {
            DateTime startDT = new DateTime(taskData[2]);
            startDT.setTimeInput(startTimeTaskA);
            DateTime endDT = new DateTime(taskData[3]);
            endDT.setTimeInput(endTimeTaskA);
            durationTask.day = (DateTime.differenceInMin(startDT, endDT) / DateTime.MINS_IN_DAY);
            selectingTimeDoneTaskA = true;
            setUpTimeETThreeA();

            if (!taskData[6].matches(Constants.STARTS_FROM_NOTSET)) {
                startDT = new DateTime(taskData[6]);
                startDT.setDateInput(startDateTaskB);
                setDateText(startDateTaskBET, startDateTaskB);
            }

            if (!taskData[7].matches(Constants.ENDS_ON_NOTSET)) {
                endDT = new DateTime(taskData[7]);
                endDT.setDateInput(endDateTaskB);
                setDateText(endDateTaskBET, endDateTaskB);
            }
        }
    }

    public void hideLayoutsNoneChecked() {
        layoutTaskThree.setVisibility(View.VISIBLE);
        layoutTaskThreeA.setVisibility(View.GONE);
        layoutTaskThreeB.setVisibility(View.GONE);
    }

    public void hideLayoutsDailyOrWeeklyChecked() {
        layoutTaskThree.setVisibility(View.GONE);
        layoutTaskThreeA.setVisibility(View.VISIBLE);
        layoutTaskThreeB.setVisibility(View.VISIBLE);
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
        editingEnabledCB(sundayTaskCB);
        editingEnabledCB(mondayTaskCB);
        editingEnabledCB(tuesdayTaskCB);
        editingEnabledCB(wednesdayTaskCB);
        editingEnabledCB(thursdayTaskCB);
        editingEnabledCB(fridayTaskCB);
        editingEnabledCB(saturdayTaskCB);
    }

    public void editingDisabledCG() {
        editingDisabledCB(sundayTaskCB);
        editingDisabledCB(mondayTaskCB);
        editingDisabledCB(tuesdayTaskCB);
        editingDisabledCB(wednesdayTaskCB);
        editingDisabledCB(thursdayTaskCB);
        editingDisabledCB(fridayTaskCB);
        editingDisabledCB(saturdayTaskCB);
    }

    public void editingDisabled() {
        editingDisabledET(taskTagET);
        editingDisabledET(taskDescriptionET);
        editingDisabledETCursor(startDateTaskET);
        editingDisabledETCursor(startTimeTaskET);
        editingDisabledETCursor(endDateTaskET);
        editingDisabledETCursor(endTimeTaskET);
        editingDisabledETCursor(startTimeTaskAET);
        editingDisabledETCursor(durationTaskAET);
        editingDisabledETCursor(endTimeTaskAET);
        editingDisabledETCursor(startDateTaskBET);
        editingDisabledETCursor(endDateTaskBET);
        for (int i = 0; i < repetitionTaskRG.getChildCount(); i++) {
            editingDisabledRB((RadioButton) repetitionTaskRG.getChildAt(i));
        }
        for (int i = 0; i < typeTaskRG.getChildCount(); i++) {
            editingDisabledRB((RadioButton) typeTaskRG.getChildAt(i));
        }
        editingDisabledCG();
    }

    public void editingEnabled() {
        editingEnabledET(taskTagET);
        editingEnabledET(taskDescriptionET);
        editingEnabledETCursor(startDateTaskET);
        editingEnabledETCursor(startTimeTaskET);
        editingEnabledETCursor(endDateTaskET);
        editingEnabledETCursor(endTimeTaskET);
        editingEnabledETCursor(startTimeTaskAET);
        editingEnabledETCursor(durationTaskAET);
        editingEnabledETCursor(endTimeTaskAET);
        editingEnabledETCursor(startDateTaskBET);
        editingEnabledETCursor(endDateTaskBET);
        for (int i = 0; i < repetitionTaskRG.getChildCount(); i++) {
            editingEnabledRB((RadioButton) repetitionTaskRG.getChildAt(i));
        }
        for (int i = 0; i < typeTaskRG.getChildCount(); i++) {
            editingEnabledRB((RadioButton) typeTaskRG.getChildAt(i));
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