<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = "public class EventInput";
    $_SESSION["extendsClass"] = "AppCompatActivity";
    $_SESSION["classDes"] = "This is activity to create a new offline event";

    $_SESSION["implementsClasses"] = array();
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array();
    $_SESSION["varDes"] = array();

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array(
                            "public void showTimePicker(final EditText name, final TimeInput time, final TimeInput ref, boolean start)",
                            "public void showDatePicker(final EditText name, final DateInput date, final DateInput ref, boolean start)",
                            "public void showDurationPicker(final EditText name, final DurationInput duration)",
                            "public void setDateText(EditText name, DateInput date)",
                            "public void setTimeText(EditText name, TimeInput time)",
                            "public void setDurationText(EditText name, DurationInput duration)",
                            "public void setDuration(TimeInput start, TimeInput end, DurationInput duration, EditText name)",
                            "public void setEndTime(TimeInput start, TimeInput end, DurationInput duration, EditText name)",
                            "public void settingUpIDRef()",
                            "public void resetFields()",
                            "public void addTypesField()"
                            );
    $_SESSION["funDes"] = array(
                            "name is the EditText where this TimePicker is used, time is the TimeInput used for giving time suggestions[for instance, the user gets the present time as the suggestion for the first time]. 
                            if start is true, the TimePicker may be involved in picking up the start time else if start is false, the TimePicker is involved in picking up the end time.Now, suggestions are given based on the ref (TimeInput) [It becomes the start time in case of an end time] .Here suggestion time is given as start time increased by 2 hrs if the start time is inputted else the present time is given as suggestion.[Note that if the time crosses 10 PM , time is wrapped around while suggestions in Date have not been changed yet]. 
                            When the user selects the time picker for the next time, his/her suggestions are based on his/her initial selected time",
                            "name is the EditText where this DatePicker is used date is the DateInput used for giving date suggestions[for instance, the user gets the present date as the suggestion for the first time]. 
                            if start is true, the DatePicker may be involved in picking up the start date.else if start is false, the DatePicker is involved in picking up the end date.Now, suggestions are given based on the ref (DateInput) [It becomes the start date in case of an end date] . Here suggestion date is given as the start date if the start date is inputted else the present date is given as suggestion. 
                            When the user selects the date picker for the next time, his/her suggestions are based on his/her initial selected date.",
                            "name is the EditText where this DurationPicker is used date is the DurationInput used for giving duration suggestions",
                            "this sets the text of the EditText to the selected date format based on the given DateInput",
                            "this sets the text of the EditText to the selected time format based on the given TimeInput",
                            "this sets the text of the EditText to the selected duration based on the given DurationInput",
                            "this function sets the DurationInput and the corresponding EditText text automatically based on the change in input(s) of start or end times.",
                            "this function sets the EndDateInput and the corresponding EditText text automatically based on the change in input of duration.",
                            "used to setup all useful references of objects in corresponding xml file",
                            "used to reset all variables after adding an event",
                            "adds type RadioButtons dynamically based on the types array defined in the Constants file"
                            );
        

    ##########################################
    header ('Location: documentationBook.php');
?>
