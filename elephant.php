<?php
require 'Book.php';
require 'Chapter.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    switch ($_POST["q"]) {
        case 'rb':
            echo json_encode(Book::get_all_objects());
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