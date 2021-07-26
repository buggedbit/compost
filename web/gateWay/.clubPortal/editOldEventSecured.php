<?php
session_start();

require '../.PHPsession/sessionTimeOut.php'; 
if (isset($_SESSION['inactiveTimeCheck']) && (time() - $_SESSION['inactiveTimeCheck'] >                                                                         $inactiveLimit)) 
    {
    // last request was more than 30 minutes ago
    session_unset();     // unset $_SESSION variable for the run-time 
    session_destroy();   // destroy session data in storage
    }
$_SESSION['inactiveTimeCheck'] = time(); // update last activity time stamp

    require '../.groupFamily/groupFamilyTree.php';

    function convertCheckBoxIntoStringsArr($para , $valHelp)//$index and $eveDivergencePoints need to be initialized to 0 and array() respectively before calling this function
            {
//            echo "in function :" ;
//            print_r($GLOBALS["eveDivergencePoints"]);
//            echo "<br>";
        
            $j = $GLOBALS["index"];
            
//            print_r($GLOBALS["index"]);
//            echo "<br>";
        
//            echo $_POST[$valHelp . "0"];
            if($_POST[$valHelp . "0"] != NULL)
                {
                $GLOBALS["eveDivergencePoints"][$j] = $valHelp . "0";
                $GLOBALS["index"] = $GLOBALS["index"] + 1;
                }
            for($i = 1;$i < count($para) ; $i++)
                {
                convertCheckBoxIntoStringsArr($para[$i] , $valHelp . "$i-");
                }
            }//takes the post variables as inputs and returns a array of strings which contain the paths for the event to be pushed into 
    
    function explodeIntoIntegers()//$index and $evePaths need to be initialized to 0 and an array  of strings containing paths {$eveDivergencePoints from the convertCheckBoxIntoStringsArr($para , $valHelp) function} respectively before calling this function
            {
            for($i=0; $i < count($GLOBALS["evePaths"]);$i++)
                {
                $GLOBALS["evePaths"][$i] = explode("-",$GLOBALS["evePaths"][$i]);
//                print_r($GLOBALS["evePaths"][$i]);
//                echo "<br>";
                for($j=0;$j<count($GLOBALS["evePaths"][$i]);$j++)
                    {
                    $GLOBALS["evePaths"][$i][$j] = (int)$GLOBALS["evePaths"][$i][$j];
                    }
                }
            }//creates an array of arrays of intergers. This indicates path(s) until divergence point(s) of the flow
    
    function createGroupPath()//$groupPath has to be initialized to "" (empty string) before calling this function 
            {
            $groupPathTemplate = "";
            echo count($GLOBALS["eveDivergencePoints"]) . "<br>";
            for($i =0 ; $i<count($GLOBALS["eveDivergencePoints"]) - 1;$i++)
                    {
                $groupPathTemplate = $groupPathTemplate .                                           $GLOBALS["eveDivergencePoints"][$i] . ",";
                    }
            echo $GLOBALS["groupPath"];
    $groupPathTemplate = $groupPathTemplate . 
        $GLOBALS["eveDivergencePoints"][count($GLOBALS["eveDivergencePoints"]) - 1];
        
            $GLOBALS["groupPath"] = $groupPathTemplate;
            }//creates s string containing all checked group path id's and separated by ',' and stores that in $groupPath outside the function
    
    function updateMainTable($dbname , $tablename ,$eeVeeIDSample)
            {
            require '../.MySQLServer/credentials.php';
            require '../signUpPage/clubColumnData.php';

            // Create connection
            $conn = mysqli_connect($servername, $username, $password, $dbname);
            // Check connection
            if (!$conn) {
                die("Connection failed: " . mysqli_connect_error());
            }

            $eveNameVal = $_POST["eventName"];
            $evePlaceVal = $_POST["eventPlace"];
            $eveStartTimes = $_POST["startTimes"];
            $eveStartDates = $_POST["startDates"];
            $eveDurations = $_POST["durations"];
            $eveRegDate = $_POST["registrationDate"];
            $eveRegTime = $_POST["registrationTime"];
            $eveRegPlace = $_POST["regPlace"];
            $eveRegWebsite = $_POST["regWebsite"];
            $repetitionDecrypt = $GLOBALS['repetitionDecrypt'];
            $groupPath = $GLOBALS["groupPath"];
            $eveComment = $_POST["comment"];
            $eveType = $GLOBALS["eveType"];
            $eveStatus = "edited";
        
            $nameOfBodyVal = $_SESSION["nameOfBody"];

            $sql = "UPDATE " . $tablename 
                . " SET 
                $MAIN_COL[1]='$eveNameVal',
                $MAIN_COL[2]='$evePlaceVal',
                $MAIN_COL[3]='$eveStartTimes',
                $MAIN_COL[4]='$eveStartDates',
                $MAIN_COL[5]='$eveDurations',
                $MAIN_COL[6]='$repetitionDecrypt',
                $MAIN_COL[7]='$eveRegTime',
                $MAIN_COL[8]='$eveRegDate',
                $MAIN_COL[9]='$eveRegPlace',
                $MAIN_COL[10]='$eveRegWebsite',
                $MAIN_COL[11]='$eveComment',
                $MAIN_COL[12]='$eveType',
                $MAIN_COL[13]='$groupPath',
                $MAIN_COL[14]='$eveStatus'

                WHERE $col[1] = $eeVeeIDSample";

            if (mysqli_query($conn, $sql)) 
                {
                echo "Record updated successfully in $tablename <br>";
                }
            else
                {
                echo "Error updating record: " . mysqli_error($conn);
                }

            mysqli_close($conn);
            }

    function updateClubTable($dbname , $tablename ,$eeVeeIDSample)
            {
            require '../.MySQLServer/credentials.php';
            require '../signUpPage/clubColumnData.php';

            // Create connection
            $conn = mysqli_connect($servername, $username, $password, $dbname);
            // Check connection
            if (!$conn) {
                die("Connection failed: " . mysqli_connect_error());
            }

            $eveNameVal = $_POST["eventName"];
            $evePlaceVal = $_POST["eventPlace"];
            $eveStartTimes = $_POST["startTimes"];
            $eveStartDates = $_POST["startDates"];
            $eveDurations = $_POST["durations"];
            $eveRegDate = $_POST["registrationDate"];
            $eveRegTime = $_POST["registrationTime"];
            $eveRegPlace = $_POST["regPlace"];
            $eveRegWebsite = $_POST["regWebsite"];
            $repetitionDecrypt = $GLOBALS['repetitionDecrypt'];
            $groupPath = $GLOBALS["groupPath"];
            $eveComment = $_POST["comment"];
            $eveType = $GLOBALS["eveType"];
            $eveStatus = "edited";    
        
            $nameOfBodyVal = $_SESSION["nameOfBody"];
        
            $sql = "UPDATE " . $tablename 
                . " SET 
                $CLUB_COL[2]='$eveNameVal',
                $CLUB_COL[3]='$evePlaceVal',
                $CLUB_COL[4]='$eveStartTimes',
                $CLUB_COL[5]='$eveStartDates',
                $CLUB_COL[6]='$eveDurations',
                $CLUB_COL[7]='$repetitionDecrypt',
                $CLUB_COL[8]='$eveRegTime',
                $CLUB_COL[9]='$eveRegDate',
                $CLUB_COL[10]='$eveRegPlace',
                $CLUB_COL[11]='$eveRegWebsite',
                $CLUB_COL[12]='$eveComment',
                $CLUB_COL[13]='$eveType',
                $CLUB_COL[14]='$groupPath',
                $CLUB_COL[15]='$eveStatus'


                WHERE $col[1] = $eeVeeIDSample";

            if (mysqli_query($conn, $sql)) 
                {
                echo "Record updated successfully in $tablename <br>";
                }
            else
                {
                echo "Error updating record: " . mysqli_error($conn);
                }

            mysqli_close($conn);
            }

    function updateGroupTable($dbname , $tablename ,$eeVeeIDSample)
            {
            require '../.MySQLServer/credentials.php';
            require '../signUpPage/clubColumnData.php';

            // Create connection
            $conn = mysqli_connect($servername, $username, $password, $dbname);
            // Check connection
            if (!$conn) {
                die("Connection failed: " . mysqli_connect_error());
            }

            $eveNameVal = $_POST["eventName"];
            $evePlaceVal = $_POST["eventPlace"];
            $eveStartTimes = $_POST["startTimes"];
            $eveStartDates = $_POST["startDates"];
            $eveDurations = $_POST["durations"];
            $eveRegDate = $_POST["registrationDate"];
            $eveRegTime = $_POST["registrationTime"];
            $eveRegPlace = $_POST["regPlace"];
            $eveRegWebsite = $_POST["regWebsite"];
            $repetitionDecrypt = $GLOBALS['repetitionDecrypt'];
            $groupPath = $GLOBALS["groupPath"];
            $eveComment = $_POST["comment"];
            $eveType = $GLOBALS["eveType"];
            $eveStatus = "edited";    
        
            $nameOfBodyVal = $_SESSION["nameOfBody"];

            $sql = "UPDATE " . $tablename 
                . " SET 
                $GROUP_COL[2]='$eveNameVal',
                $GROUP_COL[3]='$evePlaceVal',
                $GROUP_COL[4]='$eveStartTimes',
                $GROUP_COL[5]='$eveStartDates',
                $GROUP_COL[6]='$eveDurations',
                $GROUP_COL[7]='$repetitionDecrypt',
                $GROUP_COL[8]='$eveRegTime',
                $GROUP_COL[9]='$eveRegDate',
                $GROUP_COL[10]='$eveRegPlace',
                $GROUP_COL[11]='$eveRegWebsite',
                $GROUP_COL[12]='$eveComment',
                $GROUP_COL[13]='$eveType',
                $GROUP_COL[14]='$groupPath',
                $GROUP_COL[15]='$eveStatus'
                

                WHERE $col[1] = $eeVeeIDSample";

            if (mysqli_query($conn, $sql)) 
                {
                echo "Record updated successfully in $tablename <br>";
                }
            else
                {
                echo "Error updating record: " . mysqli_error($conn);
                }

            mysqli_close($conn);
            }

    function updateAllGroupTables($eeVeeIDSample , $para)//the parameter $para passed should be $HOME for complete updating from all groups
            {
            updateGroupTable("groups" , $para[0] ."eeVee" , $eeVeeIDSample);
                
                for($i=1; $i<count($para);$i++)
                    {
                    updateAllGroupTables($eeVeeIDSample , $para[$i] );
                    }
            }
//Functions End Here ...........................................................................


//If this page is accsessed using a different computer than the one which is logged in then the first if will redirect it to logIn page
        if($_SESSION["nameOfBody"] == NULL || $_SESSION["userName"] == NULL)
            {
            header('Location: ../logInPage/loginScreen.php');
            }
//even if a hacker has those session variables he has to get to this form with the post variable with name = 'check' as value = 'proper' so that he can accsess the interior code (which actually make changes in the data bases)
        else if($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["check"] == "proper")
            {
            
//Placing all the post and session variables in local variables 
            
            
            //-----------------------------------------------
            
            /*This code is in case where the type is allowed to be of checkbox type*/
            
//            $eveType = "/";//the new line here is essential
//            require '../typesOfEvents/typesOfEventsTable.php';
//            
//            foreach($types as $type)
//                {
//                if($_POST["$type"] != NULL)
//                    {
//                    $eveType = $eveType . " /" . $_POST["$type"]; 
//                    }
//                }
            
            $eveType = $_POST["type"];
            print_r($eveType);echo "<br>";  
            //$eveType is taken care of ---------------------
            
            $eeVeeIDVal = $_POST["eeVeeID"];
            $eveNameVal = $_POST["eventName"];
            $evePlaceVal = $_POST["eventPlace"];
            $eveStartTimes = $_POST["startTimes"];
            $eveStartDates = $_POST["startDates"];
            $eveDurations = $_POST["durations"];
            $eveRegDate = $_POST["registrationDate"];
            $eveRegTime = $_POST["registrationTime"];
            $eveRegPlace = $_POST["regPlace"];
            $eveRegWebsite = $_POST["regWebsite"];
            $eveComment = $_POST["comment"];
            
            $nameOfBodyVal = $_SESSION["nameOfBody"];
            
//This will be decrypting the repetetion input given by user into a 7 digit integer with 1 for checked and 0 for unchecked starting from sunday            
            $repetititonEncrypt = $_POST["repetition"];
            $repetitionDecrypt = 0000000;
        
            if($repetititonEncrypt == "noRepeat")
                {
                $repetitionDecrypt = 0000000 ;
                }
            else if($repetititonEncrypt == "daily")
                {
                $repetitionDecrypt = 1111111 ;
                }
            else if($repetititonEncrypt == "weekly")    
                {
                $build = 1;
                if($_POST["Sat"] != NULL)
                    {
                    $repetitionDecrypt = $repetitionDecrypt + $build;
                    }
                $build = $build * 10;
                if($_POST["Fri"] != NULL)
                    {
                    $repetitionDecrypt = $repetitionDecrypt + $build;
                    }
                $build = $build * 10;
                if($_POST["Thu"] != NULL)
                    {
                    $repetitionDecrypt = $repetitionDecrypt + $build;
                    }
                $build = $build * 10;
                if($_POST["Wed"] != NULL)
                    {
                    $repetitionDecrypt = $repetitionDecrypt + $build;
                    }
                $build = $build * 10;
                if($_POST["Tue"] != NULL)
                    {
                    $repetitionDecrypt = $repetitionDecrypt + $build;
                    }
                $build = $build * 10;
                if($_POST["Mon"] != NULL)
                    {
                    $repetitionDecrypt = $repetitionDecrypt + $build;
                    }
                $build = $build * 10;
                if($_POST["Sun"] != NULL)
                    {
                    $repetitionDecrypt = $repetitionDecrypt + $build;
                    }
                $build = $build * 10;
                }  
            
//path is decoded here 
            $eveDivergencePoints = array();
            $index = 0;
            convertCheckBoxIntoStringsArr($HOME , "" );//puts all the checked check box into the $eveDivergencePoints as an array   
            echo "eveDivergencePoints = ";print_r($eveDivergencePoints);
            echo "<br>";
            
            $groupPath = "";
            createGroupPath();
            echo "groupPath = " . $groupPath . "<br>";//creates a "," separated string of group paths  
            
            $index = 0;
            $evePaths = $eveDivergencePoints;
            explodeIntoIntegers();//explodes and puts the array of arrays of intergers in $evePaths
            
            echo "evePaths = ";print_r($evePaths);
            echo "<br>";
//All inputs are analysed until here except
            
            updateMainTable("recovery" , "recoveryeeVee" , $eeVeeIDVal);
            updateMainTable("main" , "maineeVee" , $eeVeeIDVal);
            
            updateAllGroupTables($eeVeeIDVal ,$HOME);
            
            updateClubTable($nameOfBodyVal , $nameOfBodyVal . "eeVee" ,$eeVeeIDVal );
            
            header('Location: clubPortal.php');
            }
        else
            {
            header('Location: ../logInPage/loginScreen.php');
            }
?>


<!--This is only to present a link to the clubPortal , For the case just mentioned above-->
<!DOCTYPE html>
<html>
    <head>
        <title>Editing event</title>
        <link href="../../BootStrap/bootstrap.min.css" rel="stylesheet" type="text/css">
        <script src="../../jQuery/jQuery.js"></script>
        <script src="../../BootStrap/bootstrap.min.js"></script>
    </head>
    <body>

        <div >
            <?php
            echo "<a href='clubPortal.php'>BACK TO MAIN CONSOLE</a>"
            ?>
        </div>

    </body>
</html>