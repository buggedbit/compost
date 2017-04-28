<?php
require 'Book.php';
require 'Chapter.php';

// todo : post
if ($_SERVER["REQUEST_METHOD"] == "GET") {

    switch ($_GET["q"]) {
        case 'c':
            $chapter = new Chapter();
            try{
                $chapter->get(1);
                $chapter->name = 'Mathematics';
                $chapter->content = 'a+b';
                $chapter->delete();
            } catch (Exception $e) {
                echo $e->getMessage();
            }
            echo 'C';
            break;
        case 'r':
            break;
        case 'u':
            break;
        case 'd':
            break;
        default:
            break;
    }

}