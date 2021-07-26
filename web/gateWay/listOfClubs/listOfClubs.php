<?php
require 'listOfClubsArray.php';
$layout = "";
foreach($clubs as $club)
    {
    $layout = $layout . "<tr><td class='row'><input type='radio' name='nameOfBody' value=$club><span class='col-sm-10'>$club</span></td></tr>";
    }
    echo "<table id='chooseClub' style='' class='table table-hover'>";
    echo "<tbody>";
    echo $layout;
    echo "</tbody>";
    echo "</table>";
?>

<script>
    $("#chooseClub tr").click(function(){
        $(this).find("input").prop("checked" , true);
        $("#chooseClub tbody tr").css("background","white");
        $(this).css("background","lightpink");
    });
</script>
