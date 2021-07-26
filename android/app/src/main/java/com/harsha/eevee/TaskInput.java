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

public class TaskInput extends AppCompatActivity {

    TaskDetailsDBHandler dbHandler;
    TimeInput startTimeTask, endTimeTask, startTimeTaskA, endTimeTaskA;
    DateInput startDateTask, endDateTask, startDateTaskB, endDateTaskB;
    DurationInput durationTask;

    boolean selectingTimeDoneTaskA;
    String startDateStoreStringTaskA, endDateStoreStingTaskA;

    EditText taskTagET, taskDescriptionET, startDateTaskET, startTimeTaskET, endDateTaskET, endTimeTaskET, startDateTaskBET,
            endDateTaskBET, startTimeTaskAET, endTimeTaskAET, durationTaskAET;

    Button skipTaskOneBTN, previousTaskTwoBTN, nextTaskTwoBTN, previousTaskThreeBTN, skipTaskThreeBTN,
            previousTaskThreeABTN, nextTaskThreeABTN, previousTaskThreeBBTN, skipTaskThreeBBTN,
            previousTaskFourBTN, addTaskBTN, viewTasksTableBTN, dropTasksTableBTN;

    RadioGroup repetitionTaskRG, typeTaskRG;

    RadioButton noneTaskRB, dailyTaskRB, weeklyTaskRB;

    CheckBox sundayTaskCB, mondayTaskCB, tuesdayTaskCB, wednesdayTaskCB, thursdayTaskCB, fridayTaskCB, saturdayTaskCB;

    LinearLayout layoutTaskOne, layoutTaskTwo, layoutTaskThree, layoutTaskThreeA, layoutTaskThreeB, layoutTaskFour,
            dayTaskCheckboxGrp, buttonPanelTaskOne, buttonPanelTaskTwo, buttonPanelTaskThree,
            buttonPanelTaskThreeA, buttonPanelTaskThreeB, buttonPanelTaskFour, buttonPanelTaskViewDrop;
    int typeSize = Constants.FILTERS.typeFilters.length;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_input);

        //hide Action bar
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.hide();
        }

        initialisingGlobalVariables();
        settingUpIDRef();
        addTypesField();
        resetFields();

        startDateTaskET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(startDateTaskET);
                showDatePicker(startDateTaskET, startDateTask, startDateTask, true);
            }
        });

        startTimeTaskET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(startTimeTaskET);
                showTimePicker(startTimeTaskET, startTimeTask, startTimeTask, true);
            }
        });

        startDateTaskET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startDateTask.InitialiseDateVariables();
                setDateText(startDateTaskET, startDateTask);
                return true;
            }
        });

        startTimeTaskET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startTimeTask.InitialiseTimeVariables();
                setTimeText(startTimeTaskET, startTimeTask);
                return true;
            }
        });

        endDateTaskET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(endDateTaskET);
                showDatePicker(endDateTaskET, endDateTask, startDateTask, false);
            }
        });

        endTimeTaskET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(endTimeTaskET);
                showTimePicker(endTimeTaskET, endTimeTask, startTimeTask, false);
            }
        });

        endDateTaskET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                endDateTask.InitialiseDateVariables();
                setDateText(endDateTaskET, endDateTask);
                return true;
            }
        });

        endTimeTaskET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                endTimeTask.InitialiseTimeVariables();
                setTimeText(endTimeTaskET, endTimeTask);
                return true;
            }
        });

        startTimeTaskAET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(startTimeTaskAET);
                showTimePicker(startTimeTaskAET, startTimeTaskA, startTimeTaskA, true);
                // setDuration(startTimeTaskA,endTimeTaskA,durationTask,durationTaskAET);
            }
        });

        durationTaskAET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(durationTaskAET);
                showDurationPicker(durationTaskAET, durationTask);
                //  setEndTime(startTimeTaskA,endTimeTaskA,durationTask,endTimeTaskAET);
            }
        });

        endTimeTaskAET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(endTimeTaskAET);
                showTimePicker(endTimeTaskAET, endTimeTaskA, startTimeTaskA, false);
                // setDuration(startTimeTaskA,endTimeTaskA,durationTask,durationTaskAET);
            }
        });

        startTimeTaskAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectingTimeDoneTaskA = false;
                setUpTimeETThreeA();
                return true;
            }
        });

        durationTaskAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectingTimeDoneTaskA = false;
                setUpTimeETThreeA();
                return true;
            }
        });

        endTimeTaskAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectingTimeDoneTaskA = false;
                setUpTimeETThreeA();
                return true;
            }
        });

        startTimeTaskAET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectingTimeDoneTaskA = false;
                setUpTimeETThreeA();
                return true;
            }
        });

        startDateTaskBET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(startDateTaskBET);
                showDatePicker(startDateTaskBET, startDateTaskB, startDateTaskB, true);
            }
        });

        endDateTaskBET.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                hideKeyboard(endDateTaskBET);
                showDatePicker(endDateTaskBET, endDateTaskB, startDateTaskB, false);
            }
        });

        startDateTaskBET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startDateTaskB.InitialiseDateVariables();
                setDateText(startDateTaskBET, startDateTaskB);
                return true;
            }
        });

        endDateTaskBET.setOnLongClickListener(new EditText.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                endDateTaskB.InitialiseDateVariables();
                setDateText(endDateTaskBET, endDateTaskB);
                return true;
            }
        });

        skipTaskOneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutTaskOne.setVisibility(View.GONE);
                layoutTaskTwo.setVisibility(View.VISIBLE);
                hideKeyboardRadioGroup(repetitionTaskRG);
            }
        });

        previousTaskTwoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutTaskTwo.setVisibility(View.GONE);
                layoutTaskOne.setVisibility(View.VISIBLE);
            }
        });

        nextTaskTwoBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (noneTaskRB.isChecked()) {
                    layoutTaskTwo.setVisibility(View.GONE);
                    layoutTaskThree.setVisibility(View.VISIBLE);
                } else {
                    if (dailyTaskRB.isChecked()) durationTask.minDays = 1;
                    else {
                        if (sundayTaskCB.isChecked() && mondayTaskCB.isChecked() && tuesdayTaskCB.isChecked() && wednesdayTaskCB.isChecked()
                                && thursdayTaskCB.isChecked() && fridayTaskCB.isChecked() && saturdayTaskCB.isChecked()) {
                            Toast.makeText(TaskInput.this, "Please change the repetition type of your task to daily!!", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (!sundayTaskCB.isChecked() && !mondayTaskCB.isChecked() && !tuesdayTaskCB.isChecked() && !wednesdayTaskCB.isChecked()
                                && !thursdayTaskCB.isChecked() && !fridayTaskCB.isChecked() && !saturdayTaskCB.isChecked()) {
                            Toast.makeText(TaskInput.this, "Please change the repetition type of your task to none!!", Toast.LENGTH_SHORT).show();
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
                    layoutTaskTwo.setVisibility(View.GONE);
                    layoutTaskThreeA.setVisibility(View.VISIBLE);
                }
            }
        });

        previousTaskThreeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutTaskThree.setVisibility(View.GONE);
                layoutTaskTwo.setVisibility(View.VISIBLE);
            }
        });

        skipTaskThreeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (startDateTask.dateDispString().matches("") && !startTimeTask.timeDispString().matches("")) {
                    Toast.makeText(TaskInput.this, "Please fill in the start date of your task!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (endDateTask.dateDispString().matches("") && !endTimeTask.timeDispString().matches("")) {
                    Toast.makeText(TaskInput.this, "Please fill in the end date of your task!!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(TaskInput.this, "Please check the end date/time of your task as it is inconsistent" +
                                " with your start date and time!!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                layoutTaskThree.setVisibility(View.GONE);
                layoutTaskFour.setVisibility(View.VISIBLE);
            }
        });

        previousTaskThreeABTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutTaskThreeA.setVisibility(View.GONE);
                layoutTaskTwo.setVisibility(View.VISIBLE);
            }
        });

        nextTaskThreeABTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateTime startDT = new DateTime("1:1:1" + ":" + startTimeTaskA.timeStoreString());
                DateTime endDT = new DateTime("1:1:1" + ":" + endTimeTaskA.timeStoreString());

                if (DateTime.differenceInMin(startDT, endDT) < 0) {
                    endDT.DAY += 1;
                }
                endDT.DAY += durationTask.day;
                if (DateTime.differenceInMin(startDT, endDT) > 1440 * durationTask.minDays) {
                    Toast.makeText(TaskInput.this, "Please check the duration of your task", Toast.LENGTH_SHORT).show();
                    return;
                }

                startDateStoreStringTaskA = startDT.toString();
                endDateStoreStingTaskA = endDT.toString();

                layoutTaskThreeA.setVisibility(View.GONE);
                layoutTaskThreeB.setVisibility(View.VISIBLE);
            }
        });

        previousTaskThreeBBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutTaskThreeB.setVisibility(View.GONE);
                layoutTaskThreeA.setVisibility(View.VISIBLE);
            }
        });

        // not checking if the start date matches with one of the checked weekday
        skipTaskThreeBBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!startDateTaskB.dateDispString().matches("") && !endDateTaskB.dateDispString().matches("")) {
                    int diff = (60 * durationTask.hour) + durationTask.min + (60 * startTimeTaskA.selectedHour) + startTimeTaskA.selectedMin;
                    int days = durationTask.day;
                    if (diff >= 1440) days += 1;
                    DateTime startDT = new DateTime(startDateTaskB.dateStoreString() + ":" + "0:0");
                    DateTime endDT = new DateTime(endDateTaskB.dateStoreString() + ":" + "0:0");

                    if (DateTime.differenceInMin(startDT, endDT) < 1440 * days) {
                        Toast.makeText(TaskInput.this, "Please check the end date of your task as it is inconsistent" +
                                " with your start !!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                layoutTaskThreeB.setVisibility(View.GONE);
                layoutTaskFour.setVisibility(View.VISIBLE);
            }
        });

        previousTaskFourBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutTaskFour.setVisibility(View.GONE);
                if (noneTaskRB.isChecked()) {
                    layoutTaskThree.setVisibility(View.VISIBLE);
                } else {
                    layoutTaskThreeB.setVisibility(View.VISIBLE);
                }
            }
        });

        addTaskBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String repetitionAsInt = getRepetitionAsInt();
                String nameOfType = getNameOfType();
                String inpTaskTag = taskTagET.getText().toString();
                String inpTaskDescription = taskDescriptionET.getText().toString();

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

                dbHandler.insertRow(task);
                Toast.makeText(TaskInput.this, "Hey !! Your task was added successfully", Toast.LENGTH_SHORT).show();
                resetFields();
            }
        });

        viewTasksTableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(TaskInput.this, ShowTasksTable.class);
                String tasksTableToString = dbHandler.viewTable();
                changeActivity.putExtra("tableTasksData", tasksTableToString);
                startActivity(changeActivity);
            }
        });

        dropTasksTableBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbHandler.dropTable();
                Toast.makeText(TaskInput.this, "TasksDataTable was dropped successfully", Toast.LENGTH_SHORT).show();
            }
        });

        taskDescriptionET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String taskDescription = taskDescriptionET.getText().toString();
                String taskTag = taskTagET.getText().toString();
                if (taskDescription.matches("") && taskTag.matches(""))
                    skipTaskOneBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String taskDescription = taskDescriptionET.getText().toString();
                if (!taskDescription.matches("")) skipTaskOneBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String taskDescription = taskDescriptionET.getText().toString();
                String taskTag = taskTagET.getText().toString();
                if (taskDescription.matches("") && taskTag.matches(""))
                    skipTaskOneBTN.setText("Skip");
            }
        });

        taskTagET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String taskDescription = taskDescriptionET.getText().toString();
                String taskTag = taskTagET.getText().toString();
                if (taskDescription.matches("") && taskTag.matches(""))
                    skipTaskOneBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String taskTag = taskTagET.getText().toString();
                if (!taskTag.matches("")) skipTaskOneBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String taskDescription = taskDescriptionET.getText().toString();
                String taskTag = taskTagET.getText().toString();
                if (taskDescription.matches("") && taskTag.matches(""))
                    skipTaskOneBTN.setText("Skip");
            }
        });

        startDateTaskBET.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String startDate = startDateTaskBET.getText().toString();
                String endDate = endDateTaskBET.getText().toString();
                if (startDate.matches("") && endDate.matches(""))
                    skipTaskThreeBBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String startDate = startDateTaskBET.getText().toString();
                if (!startDate.matches("")) skipTaskThreeBBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String startDate = startDateTaskBET.getText().toString();
                String endDate = endDateTaskBET.getText().toString();
                if (startDate.matches("") && endDate.matches(""))
                    skipTaskThreeBBTN.setText("Skip");
            }
        });

        endDateTaskBET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String startDate = startDateTaskBET.getText().toString();
                String endDate = endDateTaskBET.getText().toString();
                if (startDate.matches("") && endDate.matches(""))
                    skipTaskThreeBBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String endDate = endDateTaskBET.getText().toString();
                if (!endDate.matches("")) skipTaskThreeBBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String startDate = startDateTaskBET.getText().toString();
                String endDate = endDateTaskBET.getText().toString();
                if (startDate.matches("") && endDate.matches(""))
                    skipTaskThreeBBTN.setText("Skip");
            }
        });

        startDateTaskET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String startDate = startDateTaskET.getText().toString();
                String startTime = startTimeTaskET.getText().toString();
                String endDate = endDateTaskET.getText().toString();
                String endTime = endTimeTaskET.getText().toString();
                if (startDate.matches("") && startTime.matches("") && endDate.matches("") && endTime.matches(""))
                    skipTaskThreeBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String startDate = startDateTaskET.getText().toString();
                if (!startDate.matches("")) skipTaskThreeBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String startDate = startDateTaskET.getText().toString();
                String startTime = startTimeTaskET.getText().toString();
                String endDate = endDateTaskET.getText().toString();
                String endTime = endTimeTaskET.getText().toString();
                if (startDate.matches("") && startTime.matches("") && endDate.matches("") && endTime.matches(""))
                    skipTaskThreeBTN.setText("Skip");
            }
        });

        startTimeTaskET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String startDate = startDateTaskET.getText().toString();
                String startTime = startTimeTaskET.getText().toString();
                String endDate = endDateTaskET.getText().toString();
                String endTime = endTimeTaskET.getText().toString();
                if (startDate.matches("") && startTime.matches("") && endDate.matches("") && endTime.matches(""))
                    skipTaskThreeBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String startTime = startTimeTaskET.getText().toString();
                if (!startTime.matches("")) skipTaskThreeBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String startDate = startDateTaskET.getText().toString();
                String startTime = startTimeTaskET.getText().toString();
                String endDate = endDateTaskET.getText().toString();
                String endTime = endTimeTaskET.getText().toString();
                if (startDate.matches("") && startTime.matches("") && endDate.matches("") && endTime.matches(""))
                    skipTaskThreeBTN.setText("Skip");
            }
        });

        endDateTaskET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String startDate = startDateTaskET.getText().toString();
                String startTime = startTimeTaskET.getText().toString();
                String endDate = endDateTaskET.getText().toString();
                String endTime = endTimeTaskET.getText().toString();
                if (startDate.matches("") && startTime.matches("") && endDate.matches("") && endTime.matches(""))
                    skipTaskThreeBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String endDate = endDateTaskET.getText().toString();
                if (!endDate.matches("")) skipTaskThreeBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String startDate = startDateTaskET.getText().toString();
                String startTime = startTimeTaskET.getText().toString();
                String endDate = endDateTaskET.getText().toString();
                String endTime = endTimeTaskET.getText().toString();
                if (startDate.matches("") && startTime.matches("") && endDate.matches("") && endTime.matches(""))
                    skipTaskThreeBTN.setText("Skip");
            }
        });

        endTimeTaskET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                String startDate = startDateTaskET.getText().toString();
                String startTime = startTimeTaskET.getText().toString();
                String endDate = endDateTaskET.getText().toString();
                String endTime = endTimeTaskET.getText().toString();
                if (startDate.matches("") && startTime.matches("") && endDate.matches("") && endTime.matches(""))
                    skipTaskThreeBTN.setText("Skip");
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String endTime = endTimeTaskET.getText().toString();
                if (!endTime.matches("")) skipTaskThreeBTN.setText("Next");
            }

            @Override
            public void afterTextChanged(Editable s) {
                String startDate = startDateTaskET.getText().toString();
                String startTime = startTimeTaskET.getText().toString();
                String endDate = endDateTaskET.getText().toString();
                String endTime = endTimeTaskET.getText().toString();
                if (startDate.matches("") && startTime.matches("") && endDate.matches("") && endTime.matches(""))
                    skipTaskThreeBTN.setText("Skip");
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
        TimePicker = new TimePickerDialog(TaskInput.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(android.widget.TimePicker timePicker, int selectedHour, int selectedMinute) {
                time.selectedHour = selectedHour;
                time.selectedMin = selectedMinute;
                time.selectingDone = true;
                setTimeText(name, time);
                setDuration(startTimeTaskA, endTimeTaskA, durationTask, durationTaskAET);
            }
        }, suggestionHour, suggestionMinute, DateFormat.is24HourFormat(TaskInput.this));
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
        DatePicker = new DatePickerDialog(TaskInput.this, new DatePickerDialog.OnDateSetListener() {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(TaskInput.this);
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

    public void hideKeyboardRadioGroup(RadioGroup name) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(name.getApplicationWindowToken(), 0);
    }

    public void settingUpIDRef() {
        taskTagET = (EditText) findViewById(R.id.taskTagET);
        taskDescriptionET = (EditText) findViewById(R.id.taskDescriptionET);
        startDateTaskET = (EditText) findViewById(R.id.startDateTaskET);
        startTimeTaskET = (EditText) findViewById(R.id.startTimeTaskET);
        endDateTaskET = (EditText) findViewById(R.id.endDateTaskET);
        endTimeTaskET = (EditText) findViewById(R.id.endTimeTaskET);
        startDateTaskBET = (EditText) findViewById(R.id.startDateTaskBET);
        endDateTaskBET = (EditText) findViewById(R.id.endDateTaskBET);
        startTimeTaskAET = (EditText) findViewById(R.id.startTimeTaskAET);
        endTimeTaskAET = (EditText) findViewById(R.id.endTimeTaskAET);
        durationTaskAET = (EditText) findViewById(R.id.durationTaskAET);

        skipTaskOneBTN = (Button) findViewById(R.id.skipTaskOneBTN);
        previousTaskTwoBTN = (Button) findViewById(R.id.previousTaskTwoBTN);
        nextTaskTwoBTN = (Button) findViewById(R.id.nextTaskTwoBTN);
        previousTaskThreeBTN = (Button) findViewById(R.id.previousTaskThreeBTN);
        skipTaskThreeBTN = (Button) findViewById(R.id.skipTaskThreeBTN);
        previousTaskThreeABTN = (Button) findViewById(R.id.previousTaskThreeABTN);
        nextTaskThreeABTN = (Button) findViewById(R.id.nextTaskThreeABTN);
        previousTaskThreeBBTN = (Button) findViewById(R.id.previousTaskThreeBBTN);
        skipTaskThreeBBTN = (Button) findViewById(R.id.skipTaskThreeBBTN);
        previousTaskFourBTN = (Button) findViewById(R.id.previousTaskFourBTN);
        addTaskBTN = (Button) findViewById(R.id.addTaskBTN);
        viewTasksTableBTN = (Button) findViewById(R.id.viewTasksTableBTN);
        dropTasksTableBTN = (Button) findViewById(R.id.dropTasksTableBTN);

        repetitionTaskRG = (RadioGroup) findViewById(R.id.repetitionTaskRG);
        typeTaskRG = (RadioGroup) findViewById(R.id.typeTaskRG);

        noneTaskRB = (RadioButton) findViewById(R.id.noneTaskRB);
        dailyTaskRB = (RadioButton) findViewById(R.id.dailyTaskRB);
        weeklyTaskRB = (RadioButton) findViewById(R.id.weeklyTaskRB);

        sundayTaskCB = (CheckBox) findViewById(R.id.sundayTaskCB);
        mondayTaskCB = (CheckBox) findViewById(R.id.mondayTaskCB);
        tuesdayTaskCB = (CheckBox) findViewById(R.id.tuesdayTaskCB);
        wednesdayTaskCB = (CheckBox) findViewById(R.id.wednesdayTaskCB);
        thursdayTaskCB = (CheckBox) findViewById(R.id.thursdayTaskCB);
        fridayTaskCB = (CheckBox) findViewById(R.id.fridayTaskCB);
        saturdayTaskCB = (CheckBox) findViewById(R.id.saturdayTaskCB);

        layoutTaskOne = (LinearLayout) findViewById(R.id.layoutTaskOne);
        layoutTaskTwo = (LinearLayout) findViewById(R.id.layoutTaskTwo);
        layoutTaskThree = (LinearLayout) findViewById(R.id.layoutTaskThree);
        layoutTaskThreeA = (LinearLayout) findViewById(R.id.layoutTaskThreeA);
        layoutTaskThreeB = (LinearLayout) findViewById(R.id.layoutTaskThreeB);
        layoutTaskFour = (LinearLayout) findViewById(R.id.layoutTaskFour);
        dayTaskCheckboxGrp = (LinearLayout) findViewById(R.id.dayTaskCheckboxGrp);

        buttonPanelTaskOne = (LinearLayout) findViewById(R.id.buttonPanelTaskOne);
        buttonPanelTaskTwo = (LinearLayout) findViewById(R.id.buttonPanelTaskTwo);
        buttonPanelTaskThree = (LinearLayout) findViewById(R.id.buttonPanelTaskThree);
        buttonPanelTaskThreeA = (LinearLayout) findViewById(R.id.buttonPanelTaskThreeA);
        buttonPanelTaskThreeB = (LinearLayout) findViewById(R.id.buttonPanelTaskThreeB);
        buttonPanelTaskFour = (LinearLayout) findViewById(R.id.buttonPanelTaskFour);
        buttonPanelTaskViewDrop = (LinearLayout) findViewById(R.id.buttonPanelTaskViewDrop);
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
        RadioButton miscRB = (RadioButton) typeTaskRG.findViewById(7);

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

        layoutTaskOne.setVisibility(View.VISIBLE);
        layoutTaskTwo.setVisibility(View.GONE);
        layoutTaskThree.setVisibility(View.GONE);
        layoutTaskThreeA.setVisibility(View.GONE);
        layoutTaskThreeB.setVisibility(View.GONE);
        layoutTaskFour.setVisibility(View.GONE);
        dayTaskCheckboxGrp.setVisibility(View.GONE);

        setETEmpty();
        uncheckWeekDaysCB();
        repetitionTaskRG.clearCheck();
        typeTaskRG.clearCheck();
        noneTaskRB.setChecked(true);
        miscRB.setChecked(true);
    }

    public void dailyOrNoneTaskRBClicked(View view) {
        dayTaskCheckboxGrp.setVisibility(View.GONE);
    }

    public void weeklyTaskRBClicked(View view) {
        dayTaskCheckboxGrp.setVisibility(View.VISIBLE);
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
        }
    }

    public void setETEmpty() {
        taskDescriptionET.setText("");
        taskTagET.setText("");
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
}