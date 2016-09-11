<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = "public class Home";
    $_SESSION["extendsClass"] = "AppCompatActivity";    
    $_SESSION["classDes"] = "This is the homepage of our app from where we can go to events,tasks,Settings activities and fetching online events";

    $_SESSION["implementsClasses"] = array();
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array();
    $_SESSION["varDes"] = array();

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array(
                            "private void setUpNotifications()",
                            "private void postApprovalNotification(OnlineEventDetails onlineEvent)", 
                            "public void onlineDBUpdateOperation()"
                            );
    $_SESSION["funDes"] = array(
                            "Deletes outdated events and gives approval notifications for new events",
                            "Gives approval notifications on Home",
                            "Updates app database of online events by extracting data from the online mySQL server"
                            );
        

    ##########################################
    header ('Location: documentationBook.php');
?>
