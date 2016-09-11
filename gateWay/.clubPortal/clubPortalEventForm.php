<?php
require '../.groupFamily/groupFamilyTree.php';

$EventFormWithoutPath =    "<br>
        <input style='display:none;' type='password' name='check' value='proper'>
        <input style='display:none;' type='text' id='eeVeeID' name='eeVeeID' value='notSet'>
         <div class='row'>
             <div class='col-xs-4 formLabel'>Event</div>
             <div class='col-xs-8'><input class='form-control' id='eveName' type='text' name='eventName'>                         </div></div>
         <div class='row'>
             <div class='col-xs-4 formLabel'>Place</div>
             <div class='col-xs-8'><input class='form-control' id='evePlace' type='text' name='eventPlace' >               </div></div>
         <div class='row'>
             <div class='col-xs-4 formLabel'>Time</div>
             <div class='col-xs-8'>
                 <input  id='eveStTime' class='form-control' type='time' name='startTimes'>
             </div>
         </div>
         
         <div class='row'>
             <div class='col-xs-4 formLabel'>Date</div>
             <div class='col-xs-8'>
                <input id='eveStDate' class='form-control' type='date' name='startDates'>
             </div>
         </div>

         <div class='row'>
             <div class='col-xs-4 formLabel'>It's duration(mins)</div>
             <div class='col-xs-8'><input id='eveDur' class='form-control' type='number' name='durations'></div></div>
        <br>     
         <div class='row'>
             <div class='col-xs-4 formLabel'>How often</div>
             <div class='col-xs-4 frequency'>
             
<label class='checkbox-inline'><input type='radio'  checked id='noRepeatButton' name='repetition' value='noRepeat'> Once</label><br>
<label class='checkbox-inline'><input type='radio'  id='dailyRepeatButton' name='repetition' value='daily'> Daily</label><br>
<label class='checkbox-inline'><input type='radio'  id='weeklyRepeatButton' name='repetition' value='weekly'> Weekly</label><br>
             </div>
            <div id='weeklyRepeatContent' style='display:none;'                         class='col-xs-4 frequency'>
<label class='checkbox-inline'><input type='checkbox' id='Sun' name='Sun' value='sunday'> Sun</label><br>
<label class='checkbox-inline'><input type='checkbox' id='Mon' name='Mon' value='monday'> Mon</label><br>
<label class='checkbox-inline'><input type='checkbox' id='Tue' name='Tue' value='tuesday'> Tue</label><br>
<label class='checkbox-inline'><input type='checkbox' id='Wed' name='Wed' value='wednesday'> Wed</label><br>
<label class='checkbox-inline'><input type='checkbox' id='Thu' name='Thu' value='thursday'> Thu</label><br>
<label class='checkbox-inline'><input type='checkbox' id='Fri' name='Fri' value='friday'> Fri</label><br>
<label class='checkbox-inline'><input type='checkbox' id='Sat' name='Sat' value='saturday'> Sat</label>
             </div>
         </div>
         
         <br>
         
         <div class='row' >
            <div class='col-xs-4 formLabel'>Comment </div>
            <div class='col-xs-8'><textarea class='col-xs-4 form-control' rows='2' id='comment' name='comment' ></textarea></div>
        </div> 
         
         <div>
           <button id='registrationContentButton' type='button' class='btn                  btn-primary'>
             Registration Details
           </button>
         </div>
         
         <br>
         <div id='registrationContent' style='display:none;'>  
             
              <div class='row'>
                 <div class='col-xs-4 formLabel'>Last Date</div>
                 <div class='col-xs-8'>
                     <input id='regDeadDate' class='form-control' type='date' name='registrationDate'>
                </div>
            </div>
             
             <div class='row'>
                 <div class='col-xs-4 formLabel'>DeadLine - Time</div>
                 <div class='col-xs-8'>
                     <input id='regDeadTime' class='form-control' type='time' name='registrationTime'>
                </div>
            </div>    
                 
         <div class='row'>
             <div class='col-xs-4 formLabel'>Place</div>
             <div class='col-xs-8'><input class='form-control' id='regPlace' type='text' name='regPlace'></div></div>
         <div class='row'>
            <div class='col-xs-4 formLabel'>Website</div>
            <div class='col-xs-8'><input class='form-control' id='regWebsite' type='text' name='regWebsite'></div>
         </div>
         </div>

      <br>
      <br>
      <input type='button' class='btn btn-success' onclick='validateNewEventForm()'                                     value='Go' id='validateEventForm'>";

//This function is specific for the $EventPath variable mentioned below
$EventPath = "" ;
function storePathInVariable($para , $valHelp )
        {
        $GLOBALS['EventPath'] = $GLOBALS['EventPath'] . "<div 
        id='"."$valHelp"."0' style='padding-left:40px;'>
        <label class='checkbox-inline'><input type='checkbox' name='"."$valHelp"."0' value='"."$valHelp"."0' >$para[0]</label>" ;

        for($i=1;$i<count($para);$i++)
            {
            storePathInVariable($para[$i],$valHelp . "$i-");
            }
        $GLOBALS['EventPath'] = $GLOBALS['EventPath'] . "</div>";
        }

$empty = "";
storePathInVariable($HOME , $empty);
$EventPath = $EventPath . "<br>";
?>