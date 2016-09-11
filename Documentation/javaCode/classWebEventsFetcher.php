<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = "public class WebEventsFetcher";
    $_SESSION["extendsClass"] = "";    
    $_SESSION["classDes"] = "creates a copy of the online mySQL database in the app corresponding to the group subscribed";

    $_SESSION["implementsClasses"] = array();
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array(
                            "private URL JSON_ENCODER_URL",
                            "private String GROUP_TABLE_NAME_POST",
                            "private JSONArray ServerResponse"
                            );
    $_SESSION["varDes"] = array(
                                "stores the url for getting the online events",
                                "stores the group user subscribed to for fetching the events",
                                "stores the array of events returned by url"
                                );

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array(
                            "private void initialize()",
                            "public void fetchDataIntoServerResponse()",
                            "public void fillUpIntoWebEventsDataTable()"
                            );
    $_SESSION["funDes"] = array(
                                "Establishes a connection with the online mySQL database",
                                "Fetches the online events in to ServerResponse",
                                "Generates the copy of online database in the app"
                                );
        

    ##########################################
    header ('Location: documentationBook.php');
?>
