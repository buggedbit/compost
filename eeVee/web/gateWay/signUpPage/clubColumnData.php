<?php
//all table designs are ther in this file
//If you change this in between then new sign up's will have a database which has a table with the design below
//But the previously defined tables will not be changed


#The first four columns should not be disturbed

$col[0] = "clubId";
$col[1] = "eeVeeID";
$col[2] = "Name";
$col[3] = "Place";
$col[4] = "stTime";
$col[5] = "stDate";
$col[6] = "Duration";
$col[7] = "Frequency";
$col[8] = "regDTime";
$col[9] = "regDDate";
$col[10] = "regPlace";
$col[11] = "regWebsite";
$col[12] = "Comment";
$col[13] = "Type";
$col[14] = "groupPath";
$col[15] = "Status";
$col[16] = "LastTimeStamp";
$col[17] = "clubName";
$col[18] = "groupId";

// CREATE TABLE STRINGS ....................................

// CLUB TABLES //

$CLUB_COL[0] = "clubId";
$CLUB_COL[1] = "eeVeeID";
$CLUB_COL[2] = "Name";
$CLUB_COL[3] = "Place";
$CLUB_COL[4] = "stTime";
$CLUB_COL[5] = "stDate";
$CLUB_COL[6] = "Duration";
$CLUB_COL[7] = "Frequency";
$CLUB_COL[8] = "regDTime";
$CLUB_COL[9] = "regDDate";
$CLUB_COL[10] = "regPlace";
$CLUB_COL[11] = "regWebsite";
$CLUB_COL[12] = "Comment";
$CLUB_COL[13] = "Type";
$CLUB_COL[14] = "groupPath";
$CLUB_COL[15] = "Status";
$CLUB_COL[16] = "LastTimeStamp";
$CLUB_COL[17] = "clubName";

$createTableClub = "(
    $CLUB_COL[0] INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    $CLUB_COL[1] INT(10) UNSIGNED,
    $CLUB_COL[2] VARCHAR(100) NOT NULL,
    $CLUB_COL[3] VARCHAR(100) NOT NULL,
    $CLUB_COL[4] TIME NOT NULL,
    $CLUB_COL[5] DATE NOT NULL,
    $CLUB_COL[6] INT(100) UNSIGNED,
    $CLUB_COL[7] INT(7) NOT NULL,
    $CLUB_COL[8] TIME NOT NULL,
    $CLUB_COL[9] DATE NOT NULL,
    $CLUB_COL[10] VARCHAR(100) NOT NULL,
    $CLUB_COL[11] VARCHAR(100) NOT NULL,
    $CLUB_COL[12] TEXT NOT NULL,
    $CLUB_COL[13] VARCHAR(100) NOT NULL,
    $CLUB_COL[14] VARCHAR(100) NOT NULL,
    $CLUB_COL[15] VARCHAR(100) NOT NULL,
    $CLUB_COL[16] TIMESTAMP,
    $CLUB_COL[17] VARCHAR(100) NOT NULL
    )

    ";

// MAIN TABLES //

$MAIN_COL[0] = "eeVeeID";
$MAIN_COL[1] = "Name";
$MAIN_COL[2] = "Place";
$MAIN_COL[3] = "stTime";
$MAIN_COL[4] = "stDate";
$MAIN_COL[5] = "Duration";
$MAIN_COL[6] = "Frequency";
$MAIN_COL[7] = "regDTime";
$MAIN_COL[8] = "regDDate";
$MAIN_COL[9] = "regPlace";
$MAIN_COL[10] = "regWebsite";
$MAIN_COL[11] = "Comment";
$MAIN_COL[12] = "Type";
$MAIN_COL[13] = "groupPath";
$MAIN_COL[14] = "Status";
$MAIN_COL[15] = "LastTimeStamp";
$MAIN_COL[16] = "clubName";

$createTableMain = "(
    $MAIN_COL[0] INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    $MAIN_COL[1] VARCHAR(100) NOT NULL,
    $MAIN_COL[2] VARCHAR(100) NOT NULL,
    $MAIN_COL[3] TIME NOT NULL,
    $MAIN_COL[4] DATE NOT NULL,
    $MAIN_COL[5] INT(100) UNSIGNED,
    $MAIN_COL[6] INT(7) NOT NULL,
    $MAIN_COL[7] TIME NOT NULL,
    $MAIN_COL[8] DATE NOT NULL,
    $MAIN_COL[9] VARCHAR(100) NOT NULL,
    $MAIN_COL[10] VARCHAR(100) NOT NULL,
    $MAIN_COL[11] TEXT NOT NULL,
    $MAIN_COL[12] VARCHAR(100) NOT NULL,
    $MAIN_COL[13] VARCHAR(100) NOT NULL,
    $MAIN_COL[14] VARCHAR(100) NOT NULL,
    $MAIN_COL[15] TIMESTAMP,
    $MAIN_COL[16] VARCHAR(100) NOT NULL
    )";

// GROUP TABLES //

$GROUP_COL[0] = "groupId";
$GROUP_COL[1] = "eeVeeID";
$GROUP_COL[2] = "Name";
$GROUP_COL[3] = "Place";
$GROUP_COL[4] = "stTime";
$GROUP_COL[5] = "stDate";
$GROUP_COL[6] = "Duration";
$GROUP_COL[7] = "Frequency";
$GROUP_COL[8] = "regDTime";
$GROUP_COL[9] = "regDDate";
$GROUP_COL[10] = "regPlace";
$GROUP_COL[11] = "regWebsite";
$GROUP_COL[12] = "Comment";
$GROUP_COL[13] = "Type";
$GROUP_COL[14] = "groupPath";
$GROUP_COL[15] = "Status";
$GROUP_COL[16] = "LastTimeStamp";
$GROUP_COL[17] = "clubName";

$createTableGroup = "(
    $GROUP_COL[0] INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    $GROUP_COL[1] INT(6) UNSIGNED,
    $GROUP_COL[2] VARCHAR(100) NOT NULL,
    $GROUP_COL[3] VARCHAR(100) NOT NULL,
    $GROUP_COL[4] TIME NOT NULL,
    $GROUP_COL[5] DATE NOT NULL,
    $GROUP_COL[6] INT(100) UNSIGNED,
    $GROUP_COL[7] INT(7) NOT NULL,
    $GROUP_COL[8] TIME NOT NULL,
    $GROUP_COL[9] DATE NOT NULL,
    $GROUP_COL[10] VARCHAR(100) NOT NULL,
    $GROUP_COL[11] VARCHAR(100) NOT NULL,
    $GROUP_COL[12] TEXT NOT NULL,
    $GROUP_COL[13] VARCHAR(100) NOT NULL,
    $GROUP_COL[14] VARCHAR(100) NOT NULL,
    $GROUP_COL[15] VARCHAR(100) NOT NULL,
    $GROUP_COL[16] TIMESTAMP,
    $GROUP_COL[17] VARCHAR(100) NOT NULL
    )";

?>
