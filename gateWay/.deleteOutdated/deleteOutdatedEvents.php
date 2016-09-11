<?php 
	require '../.MySQLServer/credentials.php';
	require '../signUpPage/clubColumnData.php';

# Functions Start here....................................................................................................
	function deleteEventWitheeVeeIDInTable($eeVeeIDSample , $dbname , $tablename)
		{
		require '../.MySQLServer/credentials.php';
		require '../signUpPage/clubColumnData.php';
        
		// Create connection
		$conn = mysqli_connect($servername, $username, $password, $dbname);
		// Check connection
		if (!$conn) 
			{
			echo "Failed to connect to $dbname " . mysqli_connect_error() ;
			}
		
		// sql to delete a record
		$sql = "DELETE FROM " . $tablename . " WHERE $col[1] = " . $eeVeeIDSample;
	
		if (mysqli_query($conn, $sql)) 
			{
			echo "<br> deleting query went smoothly in $tablename <br>";
			} 
		else 
			{
			echo "<br> Error deleting OutDatedEvents in $tablename " . mysqli_error($conn) . "<br>";
			}
	
		mysqli_close($conn);
		}

	function deleteInAllGroups($eeVeeIDSample , $para)//the parameter $para passed should be $HOME for complete deletion from all groups
		{
		deleteEventWitheeVeeIDInTable($eeVeeIDSample , "groups" , $para[0] ."eeVee" );
	
		for($i=1; $i<count($para);$i++)
			{
			deleteInAllGroups($eeVeeIDSample , $para[$i] );
			}
		}
# Functions End here....................................................................................................	
		
	//First go through the main table and search for outdated events
	$dbMain = "main";
	$dbMainTable = "maineeVee";
	
	$conn = mysqli_connect($servername, $username, $password, $dbMain);
	if(!$conn)
		{
		die("connecting to main database failed " . mysqli_connect_error());
		}
	else
		{
		echo "connected to main database <br>";
		}
	
	$presentDateTime = date_create("now");
	
	$outdated_eeVeeID_array = array();
	
	// Extracting eeVeeID , stTime , stDate , Duration , Frequency from maineeVee Table 
	
	$sql = "SELECT $MAIN_COL[0], $MAIN_COL[3] , $MAIN_COL[4], $MAIN_COL[5], $MAIN_COL[6] FROM $dbMainTable";		// 3 stTime
	$result = mysqli_query($conn, $sql);												// 4 stDate
																						// 5 Duration
	if (mysqli_num_rows($result) > 0) 													// 6 Frequency
		{																				
		while($row = mysqli_fetch_assoc($result)) 
			{	
			if($row[$MAIN_COL[6]] != "0000000")continue;
			
			$START = date_create($row[$MAIN_COL[4]] . " " . $row[$MAIN_COL[3]]);
			$duration = date_interval_create_from_date_string($row[$MAIN_COL[5]] . "minutes");
			$END = date_add($START , $duration);
			
			if($END < $presentDateTime)
				{
				echo date_format($END , "Y-m-d H:i") . "<br>";
				array_push($outdated_eeVeeID_array , $row[$MAIN_COL[0]]);
				}
			}
		} 
	else 
		{
		echo "0 results in main table <br>";
		}
	
	//$outdated_eeVeeID_array array has all the eeVeeID's of the Events that have to be deleted as they are outdated
	echo "<br>These are the eeVeeID's of outdated non-repeating events <br>";
	print_r($outdated_eeVeeID_array);
	
	// Deleting means deleting all records with eeVeeID matching to anyone in the above $outdated_eeVeeID_array array
	
	require '../.groupFamily/groupFamilyTree.php';
	require '../listOfClubs/listOfClubsArray.php';
	
	foreach ($outdated_eeVeeID_array as $outdated_eeVeeID)
		{	
		// Deleting in main
		deleteEventWitheeVeeIDInTable($outdated_eeVeeID , "main" , "maineeVee");
		// Deleting in recovery
		deleteEventWitheeVeeIDInTable($outdated_eeVeeID , "recovery" , "recoveryeeVee");
		// Deleting in all groups
		deleteInAllGroups($outdated_eeVeeID , $HOME);
		// Deleting in all clubs
		foreach ($clubs as $club)
			{
			deleteEventWitheeVeeIDInTable($outdated_eeVeeID , $club , $club . "eeVee");
			}
		}
	
	mysqli_close($conn);
?>