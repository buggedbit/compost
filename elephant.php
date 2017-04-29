<?php
require 'Book.php';
require 'Chapter.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    switch ($_POST['q']) {
        // All books
        case 'ab':
            echo json_encode(Book::get_all_objects());
            break;
        // Select book
        case 'sb':
            $old_book = new Book();
            try {
                $old_book->get($_POST['pk']);
                echo json_encode($old_book);
            } catch (Exception $e) {
                echo '-1';
            }
            break;
        // Select chapter
        case 'sc':
            $old_chapter = new Chapter();
            try {
                $old_chapter->get($_POST['pk']);
                echo json_encode($old_chapter);
            } catch (Exception $e) {
                echo '-1';
            }
            break;
        case 'd':
            break;
        default:
            break;
    }

}