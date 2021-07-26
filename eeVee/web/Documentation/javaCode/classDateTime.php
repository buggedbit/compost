<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = "public class DateTime";
    $_SESSION["extendsClass"] = "";    
    $_SESSION["classDes"] = "A class to store date and time to the precision of minutes";

    $_SESSION["implementsClasses"] = array();
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array(
                            "public int YEAR",
                            "public int MONTH",
                            "public int DAY",
                            "public int HOUR",
                            "public int MINUTE"
                            );
    $_SESSION["varDes"] = array(
                            "stores the year",
                            "stores the month numbering from 1 to 12",
                            "stores the day numbering from 1",
                            "stores the hour numbering from 0 to 23",
                            "stores the minutes numbering from 0 to 59"
                            );

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array(
                            "public DateTime(boolean setToPresent)",
                            "public DateTime(String dateString)",
                            "public DateTime(String dateString , int type)",
                            "public DateTime(DateTime reference)",
                            "public String toString()",
                            "private boolean isLeapYear()",
                            "private int sinceStart()",
                            "public void setTimeInput(TimeInput time)",
                            "public TimeInput getTimeInput()",
                            "public void setDateInput(DateInput date)",
                            "public DateInput getDateInput()",
                            "public void overflowConverter()",
                            "public void underflowConverter()",
                            "public static int differenceInMin(DateTime start, DateTime end)",
                            "public static int differenceInMinWithSign(DateTime start, DateTime end)",
                            "public static boolean isGreater(DateTime A, DateTime B)",
                            "public static DateTime getDateTime(DateTime input, int minutes)",
                            "public static DateTime maximum(DateTime A,DateTime B)",
                            "public static DateTime minimum(DateTime A,DateTime B)"
                            );
    $_SESSION["funDes"] = array(
                                "if the boolean is true returns current system time else same as above constructor",
                                "if the string is of the form as stored in app database(YYYY:MM:DD:HH:MM) gives corresponding DateTime",
                                "the int is just for distinction from above constructor, when the int is '0' and the string is of the form stored in web database(YYYY-MM-DD HH:MM:SS) gives corresponding DateTime without seconds",
                                "a copy constructor",
                                "returns the String corresponding to the DateTime in the format YYYY:MM:DD:HH:MM(for storing in app database)",
                                "returns true if the year is a leap year",
                                "returns number of minutes since start of the year",
                                "Sets the TimeInput corresponding to the DateTime",
                                "returns the TimeInput corresponding to the DateTime",
                                "Sets the DateInput corresponding to the DateTime",
                                "returns the DateInput corresponding to the DateTime",
                                "rearranges the member variables which have overflown their limit to make it a valid DateTime",
                                "rearranges the member variables which have underflown their limit to make it a valid DateTime",
                                "returns B-A if B>=A else returns -1",
                                "returns B-A",
                                "returns the truth value of A>B",
                                "returns input increased or decreased (depending on the sign) by minutes as a DateTime",
                                "returns max(A,B)",
                                "returns min(A,B)"
                                );
        

    ##########################################
    header ('Location: documentationBook.php');
?>
