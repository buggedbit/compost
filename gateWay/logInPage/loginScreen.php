<?php
session_start();
//********************   TO BE IMPLEMENTED **********************************

//if the session variables are non-null then this block checks whether the credentials belong to a registered user or not 
//if he/she is registered then the block re-directs to the clubPortal with the same session variables (from there he/she might continue if the session is not expired else if the session is expired then clubPortal redirects back to the loginPage with destroyed session variables)
//if he/she is not registered then the block destroys session variables and presents a fresh log-in page

//**************************************************************************

    if($_SESSION["nameOfBody"] != NULL || $_SESSION["userName"] != NULL)
        {
//        echo "the ". $_SESSION["nameOfBody"] . " account was opened in this browser and was not logged out properly . Please log out from that account or restart your browser.";
        
        header('Location: ../.clubPortal/clubPortal.php');
        }
    
    else if($_SERVER["REQUEST_METHOD"] == "POST")
        {
        if($_POST["nameOfBody"] != NULL && $_POST["userName"] != NULL && $_POST["password"] != NULL )
            {
            require '../.MySQLServer/credentials.php';
            $dbname = "accounts";

            // Create connection
            $conn = mysqli_connect($servername, $username, $password, $dbname);

            $nameOfBodyVal = $_POST["nameOfBody"];
            $userNameVal = $_POST["userName"];
            $passwordVal = $_POST["password"]; 

            $sql = "SELECT * FROM accountsTable";
            $result = mysqli_query($conn, $sql);
            $userRegistered = false;

        //The code below searches in the database and if all the credentials match exactly then, it redirects the user to his/her clubPortal 
            if (mysqli_num_rows($result) > 0)
                {
                while($row = mysqli_fetch_assoc($result)) 
                    {
                    if($row["nameOfBody"] == $nameOfBodyVal && $row["userName"] == $userNameVal && $row["password"] == $passwordVal)
                            {
                            $_SESSION["nameOfBody"] = $nameOfBodyVal;
                            $_SESSION["userName"] = $userNameVal;
                            header('Location: ../.clubPortal/clubPortal.php');
                            } 
                    }
                
                echo "We couldn't find your account . Please check your credentials ";
                }
            else
                {
                echo "sign Up please";
                }

            mysqli_close($conn);
            }
        else
            {
            echo "Some fields are incomplete";
            }
        }
?>

<!DOCTYPE html>
<html>
    <head>
        <script src="../../jQuery/jQuery.js"></script>
        <link href="../../BootStrap/bootstrap.min.css" type="text/css" rel="stylesheet"/>
        <script src="../../BootStrap/bootstrap.min.js"></script>
        <title>eeVee Log In</title>
        <script>
            $(document).ready(function(){
                $("#listOfBodiesButton").click(function(){
                    $("#listOfBodies").slideToggle();
                });
            });
            
        </script>
    </head>
    
    <body>
        <div style="padding: 10px;" class="container-fluid">
            <div class="row" id="top">
                <div class="col-sm-2" style="font-size: 40px;" id="title">Log In</div>
                <div class="col-sm-8"></div>
                <div class="col-sm-2">
                    <a href="../signUpPage/signupScreen.php">
                        <button style="color:white;background-color:grey;" class="btn btn-defalut">Sign Up</button>
                    </a>
                </div>
            </div>
            <hr>
            <div class="row">
                <div class="col-sm-3"></div>
                <div class="col-sm-6">
            <form method="post" action="<?php                                                             htmlspecialchars($_SERVER["PHP_SELF"]); ?>" >
                <div class="row">
                <div class="col-sm-12">
                    <div class="row">
                        <div class="col-sm-6">
                            Select Your Club :
                        </div>
                        <div class="col-sm-6">
                            <button class="form-control btn btn-primary" id="listOfBodiesButton" type="button">Choose</button>
                            <div style="display:none;" id="listOfBodies">
                                <?php
                                require '../listOfClubs/listOfClubs.php';    
                                ?>    
                            </div>  
                        </div>
                    </div>
                </div>
                </div>
                <br>
                <br>
        <div class="row"><div class="col-sm-6">User Name :</div><div class="col-sm-6"><input class="form-control" type="text" name="userName" ></div></div>
        <div class="row"><div class="col-sm-6">Password :</div><div class="col-sm-6"><input class="form-control" type="password" name="password"></div></div>
                
                <br>
                <br>
                <input type="submit" value="Log In" class="btn btn-success">
            </form>
                </div>
                <div class="col-lg-3"></div>
            </div>
        </div>
    </body>

</html>