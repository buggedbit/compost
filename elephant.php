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
        // Create book
        case 'cb':
            $new_book = new Book();
            $new_book->name = $_POST['name'];
            $new_book->save();
            echo json_encode($new_book);
            break;
        // Update book
        case 'ub':
            $old_book = new Book();
            try {
                $old_book->get($_POST['pk']);
                $old_book->name = $_POST['name'];
                $old_book->save();
                echo json_encode($old_book);
            } catch (Exception $e) {
                echo '-1';
            }
            break;
        // Delete book
        case 'db':
            $old_book = new Book();
            try {
                $old_book->get($_POST['pk']);
                $old_book->delete();
                echo '1';
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
        // Create chapter
        case 'cc':
            $old_book = new Book();
            try {
                $old_book->get($_POST['book_pk']);
                $new_chapter = $old_book->add_chapter($_POST['name']);
                $old_book->save();
                echo json_encode(array($new_chapter, $old_book));
            } catch (Exception $e) {
                echo '-1';
            }
            break;
        // Delete chapter
        case 'dc':
            $old_book = new Book();
            try {
                $old_book->get($_POST['book_pk']);
                if ($old_book->remove_chapter($_POST['pk']))
                    echo json_encode($old_book);
                else
                    echo '-1';
            } catch (Exception $e) {
                echo '-1';
            }
            break;
        default:
            break;
    }

}