<?php
//No two types must be same 
$types = array("Academics" , "Sports" , "Technical" , "Cultural" , "Social" , "Entrepreneurship" , "Misc" );

$layout = "";

                    /*For the type to be of checkbox input then change name in below loop to $type
                     and un-comment the corresponding code in insert file and delete file
                     and change the code in autofill js function in clubPortal.php*/

foreach($types as $type)
    {
    $layout = $layout . "<li class='list-group-item'><a href='#' >
            <label class='checkbox-inline'>
            <input type='radio' id='$type' name='type' value='$type'>$type</label></a></li>";
    }

$layout =  "<br>
            <div style='font-size:18px;' class='panel-group'>
                <div class='panel panel-default'>

                    <div style='text-align:center;' class='panel-heading'>
                      <div >
                        <a data-toggle='collapse' href='#collapseList'>Choose Type</a>
                      </div>
                    </div>

                    <div id='collapseList' class='panel-collapse collapse in'>
                        <ul id='chooseTypeOptions' class='list-group' style='list-                  style-type:none;'>"
                            . $layout .
                        "</ul>
                    </div>

                </div>
            </div>"
?>
<script>
    $(document).ready(function(){        
        $("#chooseTypeOptions li").click(function(){
//            var inputBox = $(this).find("input");
//            if($(inputBox).is(':checked'))
//                {
//                $(inputBox).prop("checked" , false);
//                $(this).css("background-color" , "white");    
//                }
//            else if(!$(inputBox).is(':checked'))
//                {
//                $(inputBox).prop("checked" , true);
//                $(this).css("background-color" , "#A9F5A9");    
//                }
        });
    });
</script>