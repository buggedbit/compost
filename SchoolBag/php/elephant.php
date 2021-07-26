<?php
require 'Book.php';
require 'Chapter.php';

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    $login_key = 'airturtle';
    $valid_user = 'valid_user';

    session_start();

    switch ($_POST['q']) {
        // LogIn
        case 'li':
            if ($_POST['key'] === $login_key) {
                $_SESSION[$valid_user] = true;
                echo '1';
                break;
            } else {
                $_SESSION[$valid_user] = false;
                echo '-1';
                break;
            }
            break;
        // LogOut
        case 'lo':
            $_SESSION[$valid_user] = false;
            echo '1';
            break;
        // Session status
        case 'ss':
            if ($_SESSION[$valid_user] === true)
                echo '1';
            else
                echo '-2';
            break;


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
            if ($_SESSION[$valid_user] !== true){
                echo '-2';
                break;
            }
            $new_book = new Book();
            $new_book->name = $_POST['name'];
            $new_book->save();
            echo json_encode($new_book);
            break;
        // Update book
        case 'ub':
            if ($_SESSION[$valid_user] !== true){
                echo '-2';
                break;
            }
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
            if ($_SESSION[$valid_user] !== true){
                echo '-2';
                break;
            }
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
            if ($_SESSION[$valid_user] !== true){
                echo '-2';
                break;
            }
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
        // Update chapter name
        case 'ucn':
            if ($_SESSION[$valid_user] !== true){
                echo '-2';
                break;
            }
            $old_book = new Book();
            try {
                $old_book->get($_POST['book_pk']);
                $updated_chapter = $old_book->update_chapter_name($_POST['pk'], $_POST['name']);
                if ($updated_chapter !== false) {
                    echo json_encode(array($updated_chapter, $old_book));
                } else {
                    echo '-1';
                }
            } catch (Exception $e) {
                echo '-1';
            }
            break;
        // Update chapter content
        case 'ucc':
            if ($_SESSION[$valid_user] !== true){
                echo '-2';
                break;
            }
            $old_book = new Book();
            try {
                $old_book->get($_POST['book_pk']);
                $updated_chapter = $old_book->update_chapter_content($_POST['pk'], $_POST['content']);
                if ($updated_chapter !== false) {
                    echo json_encode(array($updated_chapter, $old_book));
                } else {
                    echo '-1';
                }
            } catch (Exception $e) {
                echo '-1';
            }
            break;
        // Delete chapter
        case 'dc':
            if ($_SESSION[$valid_user] !== true){
                echo '-2';
                break;
            }
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