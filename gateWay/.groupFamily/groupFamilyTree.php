<?php

//------------------------------------------------------------------------------------

$HOME = array( "INSTI" , array() , array() , array()); //INSTI

//------------------------------------------------------------------------------------

#INSTI children

$HOME [1] = array( "UG" , array() , array() , array()); //UG

$HOME [2] = array( "PG" , array() , array() , array() , array() , array() , array() ,                                                        array() , array()); //PG
$HOME [3] = array( "Research" , array()); //Research


//------------------------------------------------------------------------------------
#UG children
$HOME [1][1] = array( "BTech"  ); //B.Tech
$HOME [1][2] = array( "DualDegree"  ); //DualDegree
$HOME [1][3] = array( "5yrMSc"  ); //5yr M.Sc Integrated
#PG children
$HOME [2][1] = array( "MTech"  ); //M.Tech
$HOME [2][2] = array( "MPhil"  ); //M.Phil
$HOME [2][3] = array( "MDes"  ); //M.Des
$HOME [2][4] = array( "2yrMSc"  ); //2yr M.Sc
$HOME [2][5] = array( "Management"  ); //Management
$HOME [2][6] = array( "MScPhDDualDegree"  ); //M.Sc Ph.D DualDegree
$HOME [2][7] = array( "MScMTechDualDegree"  ); //M.Sc M.Tech DualDegree
$HOME [2][8] = array( "PGDIIT"  ); //PGDIIT
# Research children
$HOME [3][1] = array( "PhD"  ); //Ph.D

//------------------------------------------------------------------------------------



?>