<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = "public class EventsHome";
    $_SESSION["extendsClass"] = "AppCompatActivity";
    $_SESSION["classDes"] = "This is the activity that displays ofline and online events in a pictorial way";

    $_SESSION["implementsClasses"] = array("GestureDetector.OnGestureListener","GestureDetector.OnDoubleTapListener");
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array(
                            "public class EventViewCompact",
                            "public class UIManager"
                            );
    $_SESSION["varDes"] = array(
                            "A nested class which has all the event details and puts it to display",
                            "A nested class which manages all the events to be displayed"
                            );

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array(
                            "private void setPastRegion()",
                            "public void setTimeDivisions()",
                            "private void setUpFilterMaps()"
                            );
    $_SESSION["funDes"] = array(
                                "sets the attributes of the past region according to the Constants file",
                                "sets the attributes of the time division lines according to the Constants file and draws them",
                                "sets the clubFilter and typeFilter maps according to the filters selected by reading it from the database"
                                );
        

    ##########################################
    header ('Location: documentationBook.php');
?>
