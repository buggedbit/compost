<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = "public class SyncEvents";
    $_SESSION["extendsClass"] = "";    
    $_SESSION["classDes"] = "Syncs the app's database of online events with the copy of online mySQL database";

    $_SESSION["implementsClasses"] = array();
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array(
                            "private OnlineEventDetailsDBHandler onlineEventDetailsDBHandler;",
                            "private WebEventDetailsDBHandler webEventDetailsDBHandler;"
                            );
    $_SESSION["varDes"] = array(
                            "handles the app online events' database",
                            "handles the copy of online mySQL database"
                            );

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array(
                            "public SyncEvents(WebEventDetailsDBHandler web, OnlineEventDetailsDBHandler online, Context context)",
                            "public void startPushing()",
                            "private void oldEventCase(WebEventDetails webEvent, OnlineEventDetails onlineEvent)",
                            "private void newEventCase(WebEventDetails webEvent)"
                            );
    $_SESSION["funDes"] = array(
                            "Creates a SyncEvents object in the context given",
                            "updates the app database to the copy of online mySQL database and issues required notifications",
                            "updates an old event and issues required notification",
                            "copies an new event and issues required notification"
                            );
        

    ##########################################
    header ('Location: documentationBook.php');
?>
