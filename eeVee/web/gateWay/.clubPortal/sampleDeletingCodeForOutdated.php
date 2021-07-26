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


//If this page is accsessed using a different computer than the one which is logged in then the first if will redirect it to logIn page
        if($_SESSION["nameOfBody"] == NULL || $_SESSION["userName"] == NULL)
            {
            header('Location: ../logInPage/loginScreen.php');
            }

//even if a hacker has those session variables he has to get to this form with the post variable with name = 'check' as value = 'proper' so that he can accsess the interior code (which actually make changes in the data bases)
        else if($_SERVER["REQUEST_METHOD"] == "POST" && $_POST["check"] == "proper") 
            {
            require '../.groupFamily/groupFamilyTree.php';
            
            function deleteEventWitheeVeeID($eeVeeIDSample , $dbname , $tablename)
                {
                require '../.MySQLServer/credentials.php';

                // Create connection
                $conn = mysqli_connect($servername, $username, $password, $dbname);
                // Check connection
                if (!$conn) {
                    die("Connection failed: " . mysqli_connect_error());
                }

                // sql to delete a record
                $sql = "DELETE FROM " . $tablename . " WHERE eeVeeID=" . $eeVeeIDSample;

                if (mysqli_query($conn, $sql)) {
                    echo "Record deleted successfully in $tablename <br>";
                } else {
                    echo "Error deleting record in $tablename " . mysqli_error($conn);
                }

                mysqli_close($conn);
                
                }
            
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
            
            function deleteInAllGroups($eeVeeIDSample , $para)//the parameter $para passed should be $HOME for complete deletion from all groups 
                {
                deleteEventWitheeVeeID($eeVeeIDSample , "groups" , $para[0] ."eeVee" );
                
                for($i=1; $i<count($para);$i++)
                    {
                    deleteInAllGroups($eeVeeIDSample , $para[$i] );
                    }
                }
            
            require '../.MySQLServer/credentials.php';
            $dbname = $_SESSION["nameOfBody"];
            $tbname = $dbname . "eeVee";
           
            $eeVeeIDOfEvent = $_POST["deleteEvent"];
            
            deleteEventWitheeVeeID($eeVeeIDOfEvent , "main" , "maineeVee");
            deleteEventWitheeVeeID($eeVeeIDOfEvent , "recovery" , "recoveryeeVee");
            
            deleteInAllGroups($eeVeeIDOfEvent , $HOME);
            
            deleteEventWitheeVeeID($eeVeeIDOfEvent , $dbname , $dbname . "eeVee");
            
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