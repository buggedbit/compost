<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = "public class TimeInput";
    $_SESSION["extendsClass"] = "";    
    $_SESSION["classDes"] = "A class to store and show input Time";

    $_SESSION["implementsClasses"] = array();
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array(
                            "public int selectedHour",
                            "public int selectedMin",
                            "public boolean selectingDone"
                            );
    $_SESSION["varDes"] = array(
                            "stores the input hour numbering from 0 to 23",
                            "stores the input minutes numbering from 0 to 59",
                            "refers to whether it is input or not"
                            );

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array(
                            "public TimeInput()",
                            "public TimeInput(DateTime dateTime)",
                            "public String timeDispString()",
                            "public String timeStoreString()",
                            "public static boolean isGreater(TimeInput A , TimeInput B)"
                            );
    $_SESSION["funDes"] = array(
                                "sets hour,minute to -1 and boolean to false",
                                "creates a TimeInput object corresponding to the given DateTime",
                                "Returns String of the format HH:MM(the Time Part of the DateTime stored in app database)",
                                "Returns String of time in 12 hour format",
                                "returns A>B"
                                );
        

    ##########################################
    header ('Location: documentationBook.php');
?>
