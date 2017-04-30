<?php

/**
 * Assert for any instance created
 *      if it is changed, either save() or delete() is called on it
 */
class Book
{
    // Json db
    private static $LEVELS_FILE_URL = 'books.json';
    private static $is_connected = false;
    private static $objects;

    private static function connect()
    {
        if (Book::$is_connected == false) {
            // Connect
            Book::$objects = json_decode(file_get_contents(Book::$LEVELS_FILE_URL), true);
            Book::$is_connected = true;
        }
    }

    private static function object_exists($pk)
    {
        if (is_null(Book::$objects[$pk])) {
            return false;
        } else {
            return true;
        }
    }

    public static function get_all_objects()
    {
        // Connect
        Book::connect();
        // This assignment is copy assignment
        $all_objects = Book::$objects;
        // So there is no effect on db due to unset()
        unset($all_objects['meta']);
        // Return all objects
        return $all_objects;
    }

    // Fields
    public $pk = 0;
    public $name = "";
    public $chapter_pks = array();
    public $chapter_names = array();
    public $time_stamp = null;

    function __construct()
    {
    }

    public function get($pk)
    {
        // Connect
        Book::connect();
        // Read
        $book = Book::$objects[$pk];
        if (!is_null($book)) {
            $this->pk = $pk;
            $this->name = $book['name'];
            $this->chapter_pks = $book['chapter_pks'];
            $this->chapter_names = $book['chapter_names'];
            $this->time_stamp = null;
        } else {
            throw new Exception('No such Book');
        }
    }

    public function save()
    {
        // Connect
        Book::connect();
        // Touch
        if ($this->pk == 0) {
            // Create
            // Determines Pk
            $this->pk = Book::$objects['meta']['new_pk'];
            Book::$objects[$this->pk] = array(
                'name' => $this->name,
                'chapter_pks' => $this->chapter_pks,
                'chapter_names' => $this->chapter_names,
                'time_stamp' => new DateTime());

            // Update meta data
            // Increment the stored new_pk
            Book::$objects['meta']['new_pk']++;
            // Increment the stored count
            Book::$objects['meta']['count']++;

        } else {
            // Update
            Book::$objects[$this->pk] = array(
                'name' => $this->name,
                'chapter_pks' => $this->chapter_pks,
                'chapter_names' => $this->chapter_names,
                'time_stamp' => new DateTime());
        }

        // Write
        file_put_contents(Book::$LEVELS_FILE_URL, json_encode(Book::$objects));
    }

    public function delete()
    {
        // Delete related chapters
        foreach ($this->chapter_pks as $chapter_pk) {
            $related_chapter = new Chapter();
            try {
                $related_chapter->get($chapter_pk);
                $related_chapter->delete();
            } catch (Exception $e) {

            }
        }

        // Connect
        Book::connect();
        // Delete
        if (Book::object_exists($this->pk)) {
            unset(Book::$objects[$this->pk]);

            // Update meta data
            // Decrement the stored count
            Book::$objects['meta']['count']--;

        }
        // Write
        file_put_contents(Book::$LEVELS_FILE_URL, json_encode(Book::$objects));
    }

    public function add_chapter($chapter_name)
    {
        // Create new chapter
        $new_chapter = new Chapter();
        $new_chapter->book_id = $this->pk;
        $new_chapter->name = $chapter_name;
        $new_chapter->save();

        // Keep track of it's pk and name
        array_push($this->chapter_pks, $new_chapter->pk);
        array_push($this->chapter_names, $new_chapter->name);

        // Save
        $this->save();

        return $new_chapter;
    }

    public function remove_chapter($chapter_pk)
    {
        // Removing the tracking variable
        $index = array_search($chapter_pk, $this->chapter_pks);
        if ($index === false) {
            // no such chapter pk
            return false;
        } else {
            // chapter exists in book
            unset($this->chapter_pks[$index]);
            unset($this->chapter_names[$index]);
            $this->chapter_pks = array_values($this->chapter_pks);
            $this->chapter_names = array_values($this->chapter_names);
            $this->save();
        }

        // Deleting chapter from json db
        try {
            $old_chapter = new Chapter();
            $old_chapter->get($chapter_pk);
            $old_chapter->delete();
            return true;
        } catch (Exception $e) {
            return false;
        }
    }

    public function update_chapter_name($chapter_pk, $new_name)
    {
        // Getting the tracking variable
        $index = array_search($chapter_pk, $this->chapter_pks);
        if ($index === false) {
            // no such chapter pk
            return false;
        } else {
            // chapter exists in book
            $this->chapter_names[$index] = $new_name;
            $this->save();
        }

        // Deleting chapter from json db
        try {
            $old_chapter = new Chapter();
            $old_chapter->get($chapter_pk);
            $old_chapter->name = $new_name;
            $old_chapter->save();
            return $old_chapter;
        } catch (Exception $e) {
            return false;
        }
    }
}
