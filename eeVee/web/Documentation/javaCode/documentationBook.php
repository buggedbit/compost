<?php
    
    // True if getting session variables corresponding to a class 
    $gettingSession;
    
    session_start();
    if($_SESSION["gettingSession"] == true){
        
        $gettingSession = true;
        
        // INITIALIZATION
        $class = $_SESSION["class"];
        $extendsClass = $_SESSION["extendsClass"];
        $classDes = $_SESSION["classDes"];
        $implementsClasses = $_SESSION["implementsClasses"];
        
        $var = $_SESSION["var"];
        $varDes = $_SESSION["varDes"];
        $noVar = count($var);
        
        $fun = $_SESSION["fun"];
        $funDes = $_SESSION["funDes"];
        $noFun = count($fun);
        // ALL VARIABLES INITIALIZED
		
    }
    else{
        $gettingSession = false;
    }

    function createTableVariables(){
        
        $var = $_SESSION["var"];
        $varDes = $_SESSION["varDes"];
        $noVar = count($var);
       
        $varTable = "";
        for($i = 0 ; $i < $noVar ; $i++)
            {
            $varTable = $varTable . "<tr>";
            $varTable = $varTable . "<td class='NAME' >$var[$i]</td>";
            $varTable = $varTable . "<td>$varDes[$i]</td>";
            $varTable = $varTable . "</tr>";    
            }
        
        return $varTable;
    }
    
    function createTableFunctions(){
    
    	$fun = $_SESSION["fun"];
    	$funDes = $_SESSION["funDes"];
    	$noFun = count($fun);
    	 
    	$funTable = "";
    	for($i = 0 ; $i < $noFun ; $i++)
    	{
    		$funTable = $funTable . "<tr>";
    		$funTable = $funTable . "<td class='NAME' >$fun[$i]</td>";
    		$funTable = $funTable . "<td>$funDes[$i]</td>";
    		$funTable = $funTable . "</tr>";
    	}
    
    	return $funTable;
    }

    function printImplementedClasses(){
    	$implementsClasses = $_SESSION["implementsClasses"];
    	$noImplements = count($implementsClasses);
    	
    	$ret = ""; 
    	for($i = 0;$i < $noImplements;$i++){
    		$ret = $ret . $implementsClasses[$i] . "  ";
    	}
    	return $ret;
    }
?>

<!DOCTYPE html>
<html>
    <head>
        <script src="../../jQuery/jQuery.js"></script>
        <link href="../../BootStrap/bootstrap.min.css" type="text/css" rel="stylesheet"/>
        <script src="../../BootStrap/bootstrap.min.js"></script>
        <title>Documentation - <?php echo $class?></title>
        <style>
            #heading{
                background-color: grey;
                color: white;
            }
            #verbalDescription{
                font-style: italic;
            }
            #selectClass{
                background-color: white;
            }
            #listOfVariables header{
                font-size: 20px;
            }
            #listOfFunctions header{
                font-size: 20px;
            }
            #class{
                font-size: 40px;
            }
            #extends{
                margin-left: 5%;
            }
            #implements{
               margin-left: 10%; 
            }
            #selectClass a{
                font-size: 16px;
            }
            #selectClass{
                padding: 20px;
            }
            #chooseAClass{
                font-size: 20px;
            }
            .NAME{
                font-size: 16px;
                color: dimgray;
            }
        </style>
    </head>
    
    <body>
        <div class="container-fluid">
            <div class="row">
            <div class="col-xs-12" id="heading">
                <h1><a href="../../homePage.php" style="color:white;">eeVee</a> Documentation</h1>
            </div>
        </div>
        
        <div class="row">
            <div class="col-xs-2" id="selectClass">
                <ul class="nav nav-pills nav-stacked">
                    <li id="chooseAClass"><dl>CHOOSE A CLASS</dl></li>
                    <li><a href="classConstants.php">Constants</a></li>
                    <li><a href="classHome.php">Home</a></li>
                    <li><a href="classSyncEvents.php">SyncEvents</a></li>
                    <li><a href="classDateTime.php">DateTime</a></li>
                    <li><a href="classWebEventsFetcher.php">WebEventsFetcher</a></li>
                    <li><a href="classDateInput.php">DateInput</a></li>
                    <li><a href="classDurationInput.php">DurationInput</a></li>
                    <li><a href="classEditAndViewEvent.php">EditAndViewEvent</a></li>
                    <li><a href="classEventInput.php">EventInput</a></li>
                    <li><a href="classEventsHome.php">EventsHome</a></li>
                    <li><a href="classTimeInput.php">TimeInput</a></li>
                    <li><a href="classTimeStamp.php">TimeStamp</a></li>
                </ul>
            </div>
            <div class="col-xs-10" id="mainPlane">
                
                <div class="row" id="classAndExtends">
                    <div id="class">
                        <?php 
                             if($gettingSession){
                                echo $class ;
                             }
					       ?> 
                    </div>
                    <div id="extends">
                        <?php
                            if($gettingSession && $extendsClass != ""){
                                echo  "<code>extends</code>  " . $extendsClass;
                              }
                        ?>
                    </div>
                    <div id="implements">
                        <?php
                            if($gettingSession && $implementsClasses[0] != ""){
                                echo  "<code>implements</code>  " . printImplementedClasses();
                              }
                        ?>
                    </div>
                </div>
                <div class="row" id="verbalDescription">
                	<h3>
                	<?php 
	                	if($gettingSession){
	                		echo $classDes;
	                	}else {
	                		
	                	}
                	?>
                	</h3>
                </div>
                <hr>
                <div class="row" id="listOfVariables">
                    <?php 
                    	$putVarTable = '<table  class="table table-hover">
					                	   <thead>
					                            <tr class="success">
					                            <td style="width:40%"><header>Fields</header></td>
					                           </tr>
					                        </thead>
					                        <tbody>'
    										.
					                         createTableVariables()
					                        .
					                        '</tbody>
					                	</table>';
                    	
                    	if($gettingSession && ($noVar>0)){
                    		echo $putVarTable;
                    	}
                    ?>
                </div>
                <div class="row" id="listOfFunctions">
                	<?php 
                    	$putFunTable = '<table  class="table table-hover">
					                	   <thead>
					                            <tr class="success">
					                            <td style="width:40%"><header>Methods & Constructors</header></td>
					                           </tr>
					                        </thead>
					                        <tbody>'
    										.
					                         createTableFunctions()
					                        .
					                        '</tbody>
					                	</table>';
                    	
                    	if($gettingSession && ($noFun>0)){
                    		echo $putFunTable;
                    	}
                    ?>
                </div>
            </div>
        </div>
        </div>
    </body>
</html>








