<?php        
    function createAccountsDB()
    {   
        require '../.MySQLServer/credentials.php'; 
        $dbname = "accounts"; 

        // Create connection with server
        $conn = mysqli_connect($servername, $username, $password);
        if (!$conn) 
            {
            die("Connection failed:  " . mysqli_connect_error());
            }

        // Create a database with name $dbname
        $sql = "CREATE DATABASE " . $dbname;
        if (mysqli_query($conn, $sql))
            {
            // Create connection with $dbname
            $connToDB = mysqli_connect($servername, $username, $password, $dbname);
            // Check connection
            if (!$connToDB) {
                die("Connection failed: " . mysqli_connect_error());
            }

            // sql to create table
            $sql = "CREATE TABLE accountsTable (
            id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY, 
            nameOfBody VARCHAR(30) NOT NULL,
            userName VARCHAR(30) NOT NULL,
            password VARCHAR(50) NOT NULL
            )";
            //creating table accountsTable
            
            mysqli_query($connToDB, $sql);
            }
        // IF DB ALREADY EXISTS THEN THE INNER IF BLOCK IS NOT EXECUTED
        
        // Create connection to the data base
        $connToTable = mysqli_connect($servername, $username, $password, $dbname);
        if (!$connToTable) 
            {
            die("Connection failed: " . mysqli_connect_error());
            }
          
        $nameOfBodyVal = $_POST["nameOfBody"];
        $userNameVal = $_POST["userName"];
        $passwordVal = $_POST["password"]; 
        
        $sql = "SELECT * FROM accountsTable";
        $result = mysqli_query($connToTable, $sql);
        $notAUserAlready = true;
        
        if (mysqli_num_rows($result) > 0) 
            {
            while($row = mysqli_fetch_assoc($result)) 
                {
                if($row["nameOfBody"] == $nameOfBodyVal || $row["userName"] == $userNameVal)
                    {
                    $notAUserAlready = false;
                    break;
                    } 
                }
            }
        
        if($notAUserAlready)
        {
        session_start();
        if($_SESSION["nameOfBody"] == NULL && $_SESSION["userName"] == NULL)
            {
            
            $sql = "INSERT INTO accountsTable 
                    (
                    nameOfBody ,
                    userName ,
                    password
                    )
                VALUES 
                    (
                    ? ,
                    ? , 
                    ?
                    )";
            
            // Preparing
            $stmt = mysqli_prepare($connToTable, $sql);
            
            // Binding
            $bind = mysqli_stmt_bind_param($stmt, "sss" , $nameOfBodyVal , $userNameVal , $passwordVal );
            
            // Executing
            $exec = mysqli_stmt_execute($stmt);
            
            if ($exec)
                {
                echo "New account created successfully";
                } 

            else 
                {
                echo "Error: " . $sql . "<br>" . mysqli_error($conn);
                }

            $_SESSION["nameOfBody"] = $nameOfBodyVal;
            $_SESSION["userName"] = $userNameVal;
            
            mysqli_stmt_close($stmt);
            mysqli_close($conn);
            
            header ('Location: createClubDB.php');
            }
        else 
            {
            echo "Another session is running in this system ,Please wait until the session is finished OR log Out from the previous account.";
            }
        }
        
        else 
            {
            echo "Account Already created for the club . If NOT THEN TRY A DIFFERENT USER NAME";
            }

        mysqli_stmt_close($stmt);
        mysqli_close($conn);
    } 
?>

<!DOCTYPE html>
<html>
    <head>
        <script src="../../jQuery/jQuery.js" ></script>
        <link href="../../BootStrap/bootstrap.min.css" type="text/css" rel="stylesheet"/>
        <script src="../../BootStrap/bootstrap.min.js"></script>
        <title>eeVee Sign Up</title>
        <script>
            $(document).ready(function(){
                $("#listOfBodiesButton").click(function(){
                    $("#listOfBodies").slideToggle();
                });
            });
        </script>
    </head>
    
    <body>
        <?php
        $OTP = "0000";
        if($_SERVER["REQUEST_METHOD"] == "POST")
        {
            if($_POST["nameOfBody"] == NULL || $_POST["userName"] == NULL || $_POST["password"] == NULL || $_POST["confirmPassword"] == NULL || $_POST["approval"] == NULL)
            {
                echo "some fields are incomplete";    
            }
            
            else if($_POST["password"] != $_POST["confirmPassword"])
            {
                echo "passwords dont match";
            }
             
            else if($_POST["approval"] != $OTP) {
                 echo "OTP doesn't match";
            }
            
            else
            {
                createAccountsDB();
            }    
        }
        ?>
        
        <div style="padding: 10px;" class="container-fluid">
            <div class="row">
                <div class="col-sm-3" id="top">
                <span style="font-size: 40px;" id="title">Sign Up</span>
                </div>
                <div class="col-sm-7">
                </div>
                <div class="col-sm-2" >
                    <a href="../logInPage/loginScreen.php"><button style="color:white;background-color:grey;" class="btn btn-defalut">Log In</button></a>
                </div>
            </div>
            <hr>
            
<!--
            <div class="row" >
                <div class="col-sm-3"></div>
                <div class="col-sm-6"></div>
                <div class="col-sm-3"></div>
            </div>
-->
            
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
                        <button  class="form-control btn btn-primary" id="listOfBodiesButton" type="button">Choose</button>
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
                        <div class="row">
                            <div class="col-sm-6">User Name :</div>  
                            <div class="col-sm-6"><input type="text" class="form-control" name="userName"></div>  
                        </div>
                        <div class="row">
                            <div class="col-sm-6">Password :</div>  
                            <div class="col-sm-6"><input type="password" class="form-control" name="password"></div>  
                        </div>
                        <div class="row">
                            <div class="col-sm-6"> Confirm Password :</div>  
                            <div class="col-sm-6"><input class="form-control" type="password" name="confirmPassword">
                            </div>  
                        </div>
                        <div class="row">
                            <div class="col-sm-6">OTP :</div>  
                            <div class="col-sm-6"> <input class="form-control" type="password"                           name="approval"></div>  
                        </div>
                        
                        <br>   
                        <input class="btn btn-success" type="submit" value="Sign Up">
                    </form>
                </div>
                <div class="col-sm-3"></div>
            </div> 
           
        </div>
    </body>
</html>