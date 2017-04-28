<?php
require 'Book.php';
require 'Chapter.php';

// todo : post
if ($_SERVER["REQUEST_METHOD"] == "GET") {

    switch ($_GET["q"]) {
        case 'c':
            $book = new Book();
            try{
                $book->name = 'Science';
                $book->add_chapter(1, 'hello');
                $book->remove_chapter(1);
                $book->add_chapter(1, 'yash');
                $book->remove_chapter(1);
                $book->add_chapter(1);
                $book->save();
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