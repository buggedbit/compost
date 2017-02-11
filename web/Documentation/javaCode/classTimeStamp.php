<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = "public class TimeStamp";
    $_SESSION["extendsClass"] = "";
    $_SESSION["classDes"] = "A class to store Timestamps to the precision of seconds";

    $_SESSION["implementsClasses"] = array();
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array(
                            "public int YEAR",
                            "public int MONTH",
                            "public int DAY",
                            "public int HOUR",
                            "public int MINUTE",
                            "public int SECOND"
                            );
    $_SESSION["varDes"] = array(
                                "stores the year of the timestamp",
                                "stores the month of the timestamp numbering from 1 to 12",
                                "stores the day of the timestamp numbering from 1",
                                "stores the hour of the timestamp numbering from 0 to 23",
                                "stores the minute of the timestamp numbering from 0 to 59",
                                "stores the seconds of the timestamp numbering from 0 to 59"
                                );

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array(
                            "public TimeStamp(boolean setToPresent)",
                            "public TimeStamp(String dateString)",
                            "public String toString()",
                            "public static boolean isGreater(TimeStamp A, TimeStamp B)"
                            );
    $_SESSION["funDes"] = array(
                                "if boolean is true ,creates a time stamp object set to present time",
                                "if the string is in YYYY-MM-DD HH:MM:SS format, creates the corresponding TimeStamp object",
                                "returns a String in YYYY-MM-DD HH:MM:SS format corresponding to the TimeStamp object",
                                "returns the truth value of A>B"
                                );
        

    ##########################################
    header ('Location: documentationBook.php');
?>
