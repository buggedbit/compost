<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = "public class EditAndViewEvent";
    $_SESSION["extendsClass"] = "AppCompatActivity";
    $_SESSION["classDes"] = "This is the activity from which one can view or edit or delete an offline event. This is very similar to EventInput";

    $_SESSION["implementsClasses"] = array();
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array();
    $_SESSION["varDes"] = array();

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array(
                            "public void fillEventData(int id)",
                            "public void editingDisabled()",
                            "public void editingEnabled()"
                            );
    $_SESSION["funDes"] = array(
                            "when inputted with the id of the event in the SQLite database, this function fills the data in all edit texts and also initialises the variables accordingly",
                            "disables the power to edit data and the form is shown in view only mode",
                            "gives the user the power to edit data"
                            );
        

    ##########################################
    header ('Location: documentationBook.php');
?>
