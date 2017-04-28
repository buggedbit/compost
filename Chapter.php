<?php
/**
 * Assert for any instance created
 *      if it is changed, either save() or delete() is called on it
 */
class Chapter
{
    // Json db
    private static $LEVELS_FILE_URL = 'chapters.json';
    private static $is_connected = false;
    private static $objects;

    private static function connect()
    {
        if (Chapter::$is_connected == false) {
            // Connect
            Chapter::$objects = json_decode(file_get_contents(Chapter::$LEVELS_FILE_URL), true);
            Chapter::$is_connected = true;
        }
    }

    // Fields
    public $pk = 0;
    public $book_id = 0;
    public $name = "";
    public $content = "";
    public $time_stamp = null;

    function __construct()
    {
    }

    public function get($pk)
    {
        // Connect
        Chapter::connect();
        // Read
        $chapter = Chapter::$objects[$pk];
        if ($chapter != null) {
            $this->pk = $pk;
            $this->book_id = $chapter['book_id'];
            $this->name = $chapter['name'];
            $this->content = $chapter['content'];
            $this->time_stamp = null;

        } else {
            throw new Exception('No such Chapter');
        }
    }

    public function save()
    {
        // Connect
        Chapter::connect();
        // Touch
        if ($this->pk == 0) {
            // Create
            // Determines Pk
            $this->pk = Chapter::$objects['meta']['new_pk'];
            Chapter::$objects[$this->pk] = array(
                'name' => $this->name,
                'book_id' => $this->book_id,
                'content' => $this->content,
                'time_stamp' => new DateTime());

            // Update meta data
            // Increment the stored new_pk
            Chapter::$objects['meta']['new_pk']++;
            // Increment the stored count
            Chapter::$objects['meta']['count']++;

        } else {
            // Update
            Chapter::$objects[$this->pk] = array(
                'name' => $this->name,
                'book_id' => $this->book_id,
                'content' => $this->content,
                'time_stamp' => new DateTime());
        }

        // Write
        file_put_contents(Chapter::$LEVELS_FILE_URL, json_encode(Chapter::$objects));
    }

    public function delete()
    {
        // Connect
        Chapter::connect();
        // Delete
        if ($this->pk != 0) {
            unset(Chapter::$objects[$this->pk]);

            // Update meta data
            // Decrement the stored count
            Chapter::$objects['meta']['count']--;
        }
        // Write
        file_put_contents(Chapter::$LEVELS_FILE_URL, json_encode(Chapter::$objects));
    }

}
