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
    require '../.MySQLServer/credentials.php';

    // unused for now
    function findeeVeeID($clubIDSample , $clubDBname)//finds eeVee ID using clubID and clubname
        {
        require '../.MySQLServer/credentials.php';
        $tablename = $clubDBname . "eeVee";

        // Create connection
        $conn = mysqli_connect($servername, $username, $password, $clubDBname);
        // Check connection
        if (!$conn) {
            die("Connection failed: " . mysqli_connect_error());
        }

        $sql = "SELECT * FROM " . $tablename;
        $result = mysqli_query($conn, $sql);

        if (mysqli_num_rows($result) > 0)
            {
            while($row = mysqli_fetch_assoc($result)) 
                {
                if($row["clubId"] == $clubIDSample)return $row["eeVeeID"];
                }
            return -1;
            } 
        else
            {
            return -1;
            }

        mysqli_close($conn);
        }

    function makeStatusDeleteEventWitheeVeeID($eeVeeIDSample , $dbname , $tablename)
        {
        require '../.MySQLServer/credentials.php';
        require '../signUpPage/clubColumnData.php';
        
        // Create connection
        $conn = mysqli_connect($servername, $username, $password, $dbname);
        // Check connection
        if (!$conn) {
            die("Connection failed: " . mysqli_connect_error());
        }
            
        $eveStatus = "deleted";
        
        // sql to delete a record
        $sql = "UPDATE " . $tablename
                . " SET 
                $col[15] = '$eveStatus' 
                WHERE $col[1] = $eeVeeIDSample" ;

        if (mysqli_query($conn, $sql)) {
            echo "Record deleted (Status changed) successfully in $tablename <br>";
        } else {
            echo "Error deleting (Status changing) record in $tablename " . mysqli_error($conn);
        }

        mysqli_close($conn);

        }

    function makeStatusDeleteInAllGroups($eeVeeIDSample , $para)//the parameter $para passed should be $HOME for complete deletion from all groups 
        {
        makeStatusDeleteEventWitheeVeeID($eeVeeIDSample , "groups" , $para[0] ."eeVee" );

        for($i=1; $i<count($para);$i++)
            {
            makeStatusDeleteInAllGroups($eeVeeIDSample , $para[$i] );
            }
        }

//If this page is accsessed using a different computer than the one which is logged in then the first if will redirect it to logIn page
        if($_SESSION["nameOfBody"] == NULL || $_SESSION["userName"] == NULL)
            {
            header('Location: ../logInPage/loginScreen.php');
            }

//even if a hacker has those session variables he has to get to this form with the post variable with name = 'check' as value = 'proper' so that he can accsess the interior code (which actually make changes in the data bases)
        else if($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["check"] == "proper") 
            {
            $dbname = $_SESSION["nameOfBody"];
            $tbname = $dbname . "eeVee";
           
            $eeVeeIDOfEvent = $_POST["deleteEvent"];
            
            // Recovery and main
            makeStatusDeleteEventWitheeVeeID($eeVeeIDOfEvent , "main" , "maineeVee");
            makeStatusDeleteEventWitheeVeeID($eeVeeIDOfEvent , "recovery" , "recoveryeeVee");
            
            //Groups
            makeStatusDeleteInAllGroups($eeVeeIDOfEvent , $HOME);
            
            //Club
            makeStatusDeleteEventWitheeVeeID($eeVeeIDOfEvent , $dbname , $dbname . "eeVee");
            
            header('Location: clubPortal.php');
            }
        

        else
            {
             header('Location: ../logInPage/loginScreen.php');
            }
?>
<!DOCTYPE html>
<html>
    <head>
        <link rel="stylesheet" type="text/css" href="../../BootStrap/bootstrap.min.css">
        <script src="../../jQuery/jQuery.js"></script>
        <script src="../../BootStrap/bootstrap.min.js"></script>
    </head>

    <head>
        <a href="clubPortal.php">BACK TO CONSOLE</a>
    </head>
</html>