<?php
    session_start();
    require '../.MySQLServer/credentials.php';

    $dbname = $_SESSION["nameOfBody"];
    $tableName = $dbname . "eeVee"; 

    $conn = mysqli_connect($servername, $username, $password);
    // Create database
    $sql = "CREATE DATABASE " . $dbname;
    mysqli_query($conn, $sql);

    $conn = mysqli_connect($servername, $username, $password, $dbname);

    require 'clubColumnData.php';
    // sql to create table
    $sql = "CREATE TABLE " . $tableName . $createTableClub;

    if (mysqli_query($conn, $sql))
        {
        mysqli_close($conn);
        header('Location: ../.clubPortal/clubPortal.php');
        } 
    else 
        {
        echo "Error creating table: " . mysqli_error($conn);
        }
    

    mysqli_close($conn);
?>