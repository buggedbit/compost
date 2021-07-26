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

    //Create DATABASES function

    function createDataBase($DBName)
            {
                require '../.MySQLServer/credentials.php';

                // Create connection
                $connection = mysqli_connect($servername, $username, $password);
                // Check connection
                if (!$connection) {
                    die("Connection failed: " . mysqli_connect_error());
                }

                // Create database
                $sql = "CREATE DATABASE " . $DBName;
                if (mysqli_query($connection, $sql)) {
                    
                } else {
                    
                }

                mysqli_close($connection);
            }
    
    //Create TABLE functions

    function createTableMain($dbname , $tableName)
            {
                require '../.MySQLServer/credentials.php';
                require '../signUpPage/clubColumnData.php';
                // Create connection
                $connection = mysqli_connect($servername, $username, $password, $dbname);
                // Check connection
                if (!$connection) {
                    die("Connection failed: " . mysqli_connect_error());
                }

                // sql to create table
                $sql = "CREATE TABLE " . $tableName . $createTableMain;

                if (mysqli_query($connection, $sql)) {
                    
                } else {
                   
                }

                mysqli_close($connection);
            }
    
    function createTableGroup($dbname , $tableName)
            {
            require '../.MySQLServer/credentials.php';
            require '../signUpPage/clubColumnData.php';
                
                // Create connection
                $connection = mysqli_connect($servername, $username, $password, $dbname);
                // Check connection
                if (!$connection) {
                    die("Connection failed: " . mysqli_connect_error());
                }

                // sql to create table
                $sql = "CREATE TABLE " . $tableName . $createTableGroup;

                if (mysqli_query($connection, $sql)) {
                    
                } else {
                   
                }

                mysqli_close($connection);
            }
    
    function createTableOfAllGroups($dbname , $para)
            {
            createTableGroup($dbname , $para[0]."eeVee");
            for($i=1;$i<count($para);$i++)
                {
                createTableOfAllGroups($dbname , $para[$i]);    
                }
            }

    //INSERT INTO TABLES functions

    function insertThisEventIntoMainTable($dbname)
            {
            require '../.MySQLServer/credentials.php';
            require '../signUpPage/clubColumnData.php';
            $tablename = $dbname . "eeVee";
            
            // Create connection
            $conn = mysqli_connect($servername, $username, $password, $dbname);
            // Check connection
            if (!$conn) {
                
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
            $eveStatus = "created";
        
            $nameOfBodyVal = $_SESSION["nameOfBody"];    
        
            $sql = "INSERT INTO  " . $tablename 
                . " (
                    $MAIN_COL[1] ,
                    $MAIN_COL[2] ,
                    $MAIN_COL[3] ,
                    $MAIN_COL[4] ,
                    $MAIN_COL[5] ,
                    $MAIN_COL[6] ,
                    $MAIN_COL[7] ,
                    $MAIN_COL[8] ,
                    $MAIN_COL[9] ,
                    $MAIN_COL[10] ,
                    $MAIN_COL[11] ,
                    $MAIN_COL[12] ,
                    $MAIN_COL[13] ,
                    $MAIN_COL[14] ,
                    $MAIN_COL[16] 
                    )
            VALUES 
                    ( 
                    '$eveNameVal' , 
                    '$evePlaceVal' , 
                    '$eveStartTimes' ,
                    '$eveStartDates' ,
                    '$eveDurations' , 
                    '$repetitionDecrypt' , 
                    '$eveRegTime' ,
                    '$eveRegDate' , 
                    '$eveRegPlace' ,
                    '$eveRegWebsite' ,
                    '$eveComment' ,
                    '$eveType' ,
                    '$groupPath' , 
                    '$eveStatus' ,
                    '$nameOfBodyVal' 
                    )"; 
        
            if (mysqli_query($conn, $sql)) 
                {
                $GLOBALS['lasteeVeeId'] = mysqli_insert_id($conn);
                } 
            else 
                {
                die("this event has not been added properly to the" . $dbname );
                }
            
            mysqli_close($conn);
            }

    function insertThisEventIntoGroupTable($dbname , $tablename)
            {
            require '../.MySQLServer/credentials.php';
            require '../signUpPage/clubColumnData.php';
            
            // Create connection
            $conn = mysqli_connect($servername, $username, $password, $dbname);
            // Check connection
            if (!$conn) {
                
            }
            $lasteeVeeId = $GLOBALS["lasteeVeeId"];
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
            $eveStatus = "created";
        
            $nameOfBodyVal = $_SESSION["nameOfBody"];    
        
            $sql = "INSERT INTO  " . $tablename 
                . " (
                    $GROUP_COL[1] , 
                    $GROUP_COL[2] ,
                    $GROUP_COL[3] ,
                    $GROUP_COL[4] ,
                    $GROUP_COL[5] ,
                    $GROUP_COL[6] ,
                    $GROUP_COL[7] ,
                    $GROUP_COL[8] ,
                    $GROUP_COL[9] ,
                    $GROUP_COL[10] ,
                    $GROUP_COL[11] , 
                    $GROUP_COL[12] ,
                    $GROUP_COL[13] ,
                    $GROUP_COL[14] ,
                    $GROUP_COL[15] ,
                    $GROUP_COL[17] 
                    )
            VALUES 
                    (
                    '$lasteeVeeId' ,
                    '$eveNameVal' , 
                    '$evePlaceVal' , 
                    '$eveStartTimes' ,
                    '$eveStartDates' , 
                    '$eveDurations' , 
                    '$repetitionDecrypt' , 
                    '$eveRegTime' , 
                    '$eveRegDate' , 
                    '$eveRegPlace' , 
                    '$eveRegWebsite' , 
                    '$eveComment' ,
                    '$eveType' ,
                    '$groupPath' , 
                    '$eveStatus' ,
                    '$nameOfBodyVal'
                    )";
            
//            echo $sql;
        
            if (mysqli_query($conn, $sql)) {

            } else {
               die("this event has not been added properly to the" . $tablename );
            }
            
            mysqli_close($conn);
            }

    function insertIntoClubTable($dbname , $tablename)
            {
            require '../.MySQLServer/credentials.php';
            require '../signUpPage/clubColumnData.php';

            // Create connection
            $conn = mysqli_connect($servername, $username, $password, $dbname);
            // Check connection
            if (!$conn)
                {
                die("Failed connecting to database" . mysqli_connect_error());
                }
            
            $lasteeVeeId = $GLOBALS["lasteeVeeId"];
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
            $eveStatus = "created";
        
            $nameOfBodyVal = $_SESSION["nameOfBody"];
        
            $sql = "INSERT INTO " . $tablename 
                . "( 
                    $CLUB_COL[1] ,
                    $CLUB_COL[2] ,
                    $CLUB_COL[3] ,
                    $CLUB_COL[4] ,
                    $CLUB_COL[5] ,
                    $CLUB_COL[6] , 
                    $CLUB_COL[7] ,
                    $CLUB_COL[8] , 
                    $CLUB_COL[9] ,
                    $CLUB_COL[10] ,
                    $CLUB_COL[11] , 
                    $CLUB_COL[12] ,
                    $CLUB_COL[13] ,
                    $CLUB_COL[14] ,
                    $CLUB_COL[15] ,
                    $CLUB_COL[17] 
                    ) 
            VALUES 
                    ( 
                    '$lasteeVeeId' ,
                    '$eveNameVal' , 
                    '$evePlaceVal' , 
                    '$eveStartTimes' , 
                    '$eveStartDates' , 
                    '$eveDurations' ,
                    '$repetitionDecrypt ' ,
                    '$eveRegTime' ,
                    '$eveRegDate' , 
                    '$eveRegPlace' ,
                    '$eveRegWebsite' , 
                    '$eveComment' , 
                    '$eveType' , 
                    '$groupPath' , 
                    '$eveStatus' , 
                    '$nameOfBodyVal'
                    )";
        
        
            if (mysqli_query($conn, $sql))
                {
                echo "Event Sucessfully inserted in $tablename";
                }
            else
                {
                echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                }   
            mysqli_close($conn);
            }//this inserts the event into the clubTable

    function eventAlreadyThere($dbname , $tablename , $eeVeeIDSample)
            {
            require '../.MySQLServer/credentials.php';

            // Create connection
            $conn = mysqli_connect($servername, $username, $password, $dbname);
            // Check connection
            if (!$conn) {
                die("Connection failed: " . mysqli_connect_error());
            }

            $sql = "SELECT * FROM " . $tablename;
            $result = mysqli_query($conn, $sql);

            if (mysqli_num_rows($result) > 0) 
                {
                // output data of each row
                while($row = mysqli_fetch_assoc($result)) 
                    {
                    if($eeVeeIDSample == $row["eeVeeID"])
                        {
                        mysqli_close($conn);
                        echo "<br> record found of " . $eeVeeIDSample . " in " . $tablename;
                        return true;
                        }
                    }
                mysqli_close($conn);
                
                echo "<br> no record found of " . $eeVeeIDSample . " in " . $tablename;
                
                return false;
                }
            else 
               {
                mysqli_close($conn);
                return false; 
               }
               
            }// returns true if already the table contains an event with same eeVee ID

    function insertFromDivergencePoint($para , $eeVeeIDSample)
            {
//            echo  "<br>" . "diverging from $para wtih eeVeeID " . $eeVeeIDSample;
            if(!eventAlreadyThere("groups" , $para[0] . "eeVee" , $eeVeeIDSample))
                {
//                echo "<br>" . "insering into " . $para[0] . "eeVee";
                insertThisEventIntoGroupTable("groups" , $para[0] . "eeVee");
                }
            for($i =1;$i<count($para);$i++)
                {
                insertFromDivergencePoint($para[$i] , $eeVeeIDSample);
                }
            }

    function insertIntoAllConcernedGroups($evePathsSample , $eeVeeIDSample)
            {
            for($i = 0; $i <count($evePathsSample);$i++)
                {
                $para = $GLOBALS["HOME"];
                for($j=0;$j<count($evePathsSample[$i])-1;$j++)
                    {
                    if(!eventAlreadyThere("groups" , $para[0] . "eeVee" ,                                                                    $eeVeeIDSample))
                        {
//                        echo "<br>" . "insering into " . $para[0] . "eeVee";
                        insertThisEventIntoGroupTable("groups" , $para[0] . "eeVee");
                        }
                    $para = $para[$evePathsSample[$i][$j]];
                    }
                insertFromDivergencePoint($para , $eeVeeIDSample);
                }
            }
    
    function convertCheckBoxIntoStringsArr($para , $valHelp)//$index and $eveDivergencePoints need to be initialized to 0 and array() respectively before calling this function
            {
            $j = $GLOBALS["index"];
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
        for($i =0 ; $i<count($GLOBALS["eveDivergencePoints"]) - 1;$i++)
            {
            $groupPathTemplate = $groupPathTemplate . $GLOBALS["eveDivergencePoints"][$i] . ",";
            }
$groupPathTemplate = $groupPathTemplate . $GLOBALS["eveDivergencePoints"][count($GLOBALS["eveDivergencePoints"]) - 1];
        $GLOBALS["groupPath"] = $groupPathTemplate;
        
        }//creates s string containing all checked group path id's and separated by ',' and stores that in $groupPath outside the function


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
            
            echo "this is comment " . $eveComment;
            echo "this is type" . $eveType;
            
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
            
//            echo "<br> This is rep decrypt : " . $repetitionDecrypt . "<br>";
            
//path is decoded here 
           
            $eveDivergencePoints = array();
            $index = 0;
            convertCheckBoxIntoStringsArr($HOME , "" );//puts all the checxked check box into the $eveDivergencePoints as an array   
            
            $groupPath = "";
            createGroupPath();
            echo "this is groupPath " . $groupPath . "<br>";
            
            $index = 0;
            $evePaths = $eveDivergencePoints;
            explodeIntoIntegers();//explodes and puts the array of arrays of intergers in $evePaths
            
            echo "eveDivergencePoints = ";print_r($eveDivergencePoints);
            echo "<br>";
            echo "evePaths = ";print_r($evePaths);
            echo "<br>";
//All inputs are analysed until here except  
            
            
//The following functions create the databases and tables if they are not already present                      
            createDataBase("main");
            createTableMain("main" , "maineeVee");
            createDataBase("recovery");
            createTableMain("recovery" , "recoveryeeVee");
            createDataBase("groups");
            createTableOfAllGroups("groups" , $HOME);

//the below call sets the '$lasteeVeeId' to the latest eeVee ID in recovery and uses that to set eeVee ID of group tables 
            $lasteeVeeId = -1;
        
            insertThisEventIntoMainTable("recovery");
            echo "<br>  This event eeVeeID in recovery is " .$lasteeVeeId;
            
//this does the same thing. If every thing works properly the eeVee id of every event in both recovery DB and main DB must be same 
            insertThisEventIntoMainTable("main");
            echo "<br>  This event eeVeeID in main is " .$lasteeVeeId;
            
//this inserts the event into all groups that are checked in the path input region of .clubPortal.php , taking care that this event is NOT inserted more than once in any group         (uses eeVeeID to check if already present) 
            insertIntoAllConcernedGroups($evePaths , $lasteeVeeId);
            
//this inserts the event into the clubDataBase  
            insertIntoClubTable($nameOfBodyVal ,$nameOfBodyVal . "eeVee");
            
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
        <title>Inserting event</title>
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
