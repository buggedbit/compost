<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = "public class DurationInput";
    $_SESSION["extendsClass"] = "";    
    $_SESSION["classDes"] = "stores the input duration";

    $_SESSION["implementsClasses"] = array();
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array(
                            "public int day",
                            "public int hour",
                            "public int min",
                            "public int minDays"
                            );
    $_SESSION["varDes"] = array(
                            "stores the number of days",
                            "stores the number of hours",
                            "stores the number of minutes",
                            "refers to whether it is input or not"
                            );

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array(
                            "public DurationInput()",
                            "public String durationDispString()"
                            );
    $_SESSION["funDes"] = array(
                            "initialises hour to 2 and all other variables to zero",
                            "returns duration as a string in 'DDd HHh MMm' format"
                            );
        

    ##########################################
    header ('Location: documentationBook.php');
?>
