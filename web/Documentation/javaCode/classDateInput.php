<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = "public class DateInput";
    $_SESSION["extendsClass"] = "";
    $_SESSION["classDes"] = "A class to store and show input Date";

    $_SESSION["implementsClasses"] = array();
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array(
                            "public int selectedYear",
                            "public int selectedMonth",
                            "public int selectedDay",
                            "public boolean selectingDone"
                            );
    $_SESSION["varDes"] = array(
                            "stores the input year",
                            "stores the input month numbering from 1 to 12",
                            "stores the input day numbering from 1",
                            "refers to whether date is input or not"
                            );

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array(
                            "public DateInput()",
                            "public DateInput(DateTime dateTime)",
                            "public String dateStoreString()",
                            "public String dateDispString()",
                            "public static boolean isGreater(DateInput A , DateInput B)"
                            );
    $_SESSION["funDes"] = array(
                                "sets year,month,day to -1 and boolean to false",
                                "creates a DateInput object corresponding to the given DateTime",
                                "Returns String of the format YYYY:MM:DD(the Date Part of the DateTime stored in app database)",
                                "Returns String of the form date with suffix followed by month name followed by year(ex 1st Jul 2016)",
                                "returns A>B"
                                );
        

    ##########################################
    header ('Location: documentationBook.php');
?>
