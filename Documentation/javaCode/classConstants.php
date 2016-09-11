<?php
    session_start();
    session_unset();     // unset $_SESSION variable for the run-time
    
    $_SESSION["gettingSession"] = true;
    ###########################################
	#IF YOU DONT HAVE DESCRIPTION FOR ANY ENTITY THEN MAKE IT AN EMPTY STRING
	
    // Declare class parent class and class Description
    $_SESSION["class"] = " public class Constants ";
    $_SESSION["extendsClass"] = "";    
    $_SESSION["classDes"] = " This class is assortment of all important constants and variables whose scope must extend to all other classes ";

    $_SESSION["implementsClasses"] = array();
    
    // Declare and intialize variables and datatype strings , and their description
    $_SESSION["var"] = array(
                            "public static float SCALE" ,
                            "public static int GESTURE_AREA_UP_DIP" ,
                             "public static int GESTURE_AREA_DOWN_DIP" ,
                             "public static int POWER_SCROLL_MINUTES" ,
                             "public static int TIME_TICKER_TEXT_SIZE" ,
                             "public static int GREET_HEIGHT_DP" ,
                             "public static JSONArray HOME" ,
                             "public static String IP" ,
                             "public static String GROUP_FAMILY_TREE_JSON_OBJECT" ,
                             "public static String GROUP_TABLE_JSON_OBJECT"
                            );
    $_SESSION["varDes"] = array(
                            " to scale up , one minute is shown SCALE px's " , 
                            " px's of height of gesture area when it is up " , 
                            " px's of height of gesture area when it is down " , 
                            " number of minutes of scroll  " , 
                            " none " ,
                            " Greeting area height " ,
                            " the json array of all the groups " ,
                            " IP of the server " , 
                            " URL for the family tree json object " ,
                            " URL for the corresponding group json object "
                                );

    // Declare and intialize funtion signiture strings , and their description
    $_SESSION["fun"] = array();
    $_SESSION["funDes"] = array();
        

    ##########################################
    header ('Location: documentationBook.php');
?>
