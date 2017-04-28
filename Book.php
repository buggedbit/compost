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

    // Fields
    public $pk = 0;
    public $name = "";
    public $chapters = array();
    public $time_stamp = null;

    function __construct()
    {
    }

    public function add_chapter($chapter_pk)
    {
        $index = array_search($chapter_pk, $this->chapters);

        if ($index === false) {
            // no such chapter pk
            array_push($this->chapters, $chapter_pk);
            return true;
        } else {
            // there is already a chapter pk
            return false;
        }
    }

    public function remove_chapter($chapter_pk)
    {
        $index = array_search($chapter_pk, $this->chapters);
        if ($index === false) {
            // no such chapter pk
            return false;
        } else {
            // there is a chapter pk
            unset($this->chapters[$index]);
            $this->chapters = array_values($this->chapters);
            return true;
        }
    }

    public function get($pk)
    {
        // Connect
        Book::connect();
        // Read
        $book = Book::$objects[$pk];
        if ($book != null) {
            $this->pk = $pk;
            $this->name = $book['name'];
            $this->chapters = $book['chapters'];
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
                'chapters' => $this->chapters,
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
                'chapters' => $this->chapters,
                'time_stamp' => new DateTime());
        }

        // Write
        file_put_contents(Book::$LEVELS_FILE_URL, json_encode(Book::$objects));
    }

    public function delete()
    {
        // Connect
        Book::connect();
        // Delete
        if ($this->pk != 0) {
            unset(Book::$objects[$this->pk]);

            // Update meta data
            // Decrement the stored count
            Book::$objects['meta']['count']--;

        }
        // Write
        file_put_contents(Book::$LEVELS_FILE_URL, json_encode(Book::$objects));
    }

}
