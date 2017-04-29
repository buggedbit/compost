<!DOCTYPE html>
<html>
<head>
    <title>School Bag</title>
    <link rel="icon" href="logo.png">

    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css?family=Ubuntu+Mono" rel="stylesheet">
    <script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/css/materialize.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/js/materialize.min.js"></script>

    <style>
        pre {
            font-size: medium;
        }
    </style>
</head>
<body>
<div class="row">
    <!--CONTEXT-->
    <script>
        var CONTEXT = {
            BOOK: undefined,
            CHAPTER: undefined
        };
    </script>

    <!--TERMINAL-->
    <script>
        var $cmd;
        var $log;
        var $terminal;

        var CMD = {
            /**
             * Assert distinct keywords for all commands
             * Assert keyword does not have any space character
             * The other words of cmd are passed as an array argument to action method of the cmd
             * */
            list: [
                {
                    keyword: "",
                    desc: "",
                    action: function (arg) {
                    }
                },
                {
                    keyword: "help",
                    desc: "shows commands and their descriptions",
                    action: function (arg) {
                        for (var i = 0; i < CMD.list.length; i++) {
                            var ith_cmd = CMD.list[i];
                            var _tab = '        ';
                            var _help = ith_cmd.keyword + _tab + ith_cmd.desc;
                            print_pre(_help, 'white');
                        }
                    }
                },
                {
                    keyword: "about",
                    desc: "brief description about school bag",
                    action: function (arg) {
                        print_pre('School Bag is a place where you keep all your knowledge', 'gold');
                    }
                },
                {
                    keyword: "clear",
                    desc: "clears the terminal",
                    action: function (arg) {
                        clear();
                    }
                },
                {
                    keyword: "print",
                    desc: "prints the text given next to it",
                    action: function (arg) {
                        print_out(arg.join(" "));
                    }
                },
                /* Prints selected book */
                {
                    keyword: "book",
                    desc: "shows currently selected book",
                    action: function (arg) {
                        // Show the book context
                        if (CONTEXT.BOOK === undefined) {
                            print_war('No book selected');
                            return;
                        }
                        // Print selected book
                        var _tab = '                ';
                        // Book
                        print_pre('Book', 'wheat');
                        var _head = 'pk' + _tab + 'name';
                        print_pre(_head, 'wheat');
                        var _book = CONTEXT.BOOK.pk + _tab + CONTEXT.BOOK.name;
                        print_pre(_book, 'white');
                        print_br();

                        // Chapters
                        print_pre('Chapters', 'wheat');
                        _head = 'pk' + _tab + 'name';
                        print_pre(_head, 'wheat');
                        for (var i = 0; i < CONTEXT.BOOK.chapter_pks.length; ++i) {
                            var ith_pk = CONTEXT.BOOK.chapter_pks[i];
                            var ith_name = CONTEXT.BOOK.chapter_names[i];
                            var _chapter = ith_pk + _tab + ith_name;
                            print_pre(_chapter, 'white');
                        }
                        if (CONTEXT.BOOK.chapter_pks.length === 0) {
                            print_pre('No chapters yet', 'white');
                        }

                    }
                },
                /* Lists all books available */
                {
                    keyword: "book-a",
                    desc: "lists all books available",
                    action: function (arg) {
                        block();
                        print_out('Getting all books ...');
                        $.ajax({
                            url: 'elephant.php',
                            type: 'POST',
                            data: {
                                'q': 'ab'
                            },
                            error: function () {
                                print_err('Could not get all books');
                                un_block();
                            },
                            success: function (books) {
                                books = JSON.parse(books);
                                // Print table head
                                var _tab = '            ';
                                print_pre('Books', 'wheat');
                                var _head = 'pk' + _tab + 'name';
                                print_pre(_head, 'wheat');
                                // Print books one by one
                                for (var book_pk in books) {
                                    if (books.hasOwnProperty(book_pk)) {
                                        var _display = book_pk + _tab + books[book_pk].name;
                                        print_pre(_display, 'white');
                                    }
                                }
                                un_block();
                            }
                        });
                    }
                },
                /* Selects a book using pk */
                {
                    keyword: "book-s",
                    desc: "selects a book with it's pk as argument",
                    action: function (arg) {
                        if (arg.length === 1) {
                            block();
                            print_out('Selecting book with pk=' + arg[0] + ' ...');
                            $.ajax({
                                url: 'elephant.php',
                                type: 'POST',
                                data: {
                                    'q': 'sb',
                                    'pk': arg[0]
                                },
                                error: function () {
                                    print_err('Could not select book');
                                    un_block();
                                },
                                success: function (book) {
                                    if (book === '-1') {
                                        print_err('No book with pk=' + arg[0]);
                                        print_err('This error may be due to outdated db, please refresh and try again');
                                    }
                                    else {
                                        CONTEXT.BOOK = JSON.parse(book);
                                        CONTEXT.CHAPTER = undefined;
                                        print_out('[OK]');
                                        // Print selected book
                                        var _tab = '                ';
                                        // Head
                                        var _head = 'pk' + _tab + 'name' + _tab + 'chapter pks';
                                        print_pre(_head, 'wheat');
                                        // Body
                                        var _book;
                                        if (CONTEXT.BOOK.chapter_pks.length === 0) {
                                            _book = CONTEXT.BOOK.pk + _tab + CONTEXT.BOOK.name + _tab + 'No chapters yet';
                                            print_pre(_book, 'white');
                                        } else if (CONTEXT.BOOK.chapter_pks.length > 0) {
                                            _book = CONTEXT.BOOK.pk + _tab + CONTEXT.BOOK.name + _tab + CONTEXT.BOOK.chapter_pks;
                                            print_pre(_book, 'white');
                                        }
                                    }
                                    un_block();
                                }
                            });
                        }
                        else {
                            print_err('Usage : book-s "pk"');
                        }
                    }
                },
                /* Updates the selected book */
                {
                    keyword: "book-u",
                    desc: "updates the selected book with it's new name as argument",
                    action: function (arg) {
                        if (CONTEXT.BOOK === undefined) {
                            print_err('No book selected');
                            print_err('Select a book first to update');
                            return;
                        }
                        if (arg.length === 1) {
                            block();
                            print_out('Updating book with pk=' + CONTEXT.BOOK.pk + ' ...');
                            $.ajax({
                                url: 'elephant.php',
                                type: 'POST',
                                data: {
                                    'q': 'ub',
                                    'pk': CONTEXT.BOOK.pk,
                                    'name': arg[0]
                                },
                                error: function () {
                                    print_err('Could not update book');
                                    un_block();
                                },
                                success: function (updated_book) {
                                    CONTEXT.BOOK = JSON.parse(updated_book);
                                    print_out('[OK]');
                                    // Print updated book
                                    var _tab = '                ';
                                    // Head
                                    var _head = 'pk' + _tab + 'name' + _tab + 'chapter pks';
                                    print_pre(_head, 'wheat');
                                    // Body
                                    var _book;
                                    if (CONTEXT.BOOK.chapter_pks.length === 0) {
                                        _book = CONTEXT.BOOK.pk + _tab + CONTEXT.BOOK.name + _tab + 'No chapters yet';
                                        print_pre(_book, 'white');
                                    } else if (CONTEXT.BOOK.chapter_pks.length > 0) {
                                        _book = CONTEXT.BOOK.pk + _tab + CONTEXT.BOOK.name + _tab + CONTEXT.BOOK.chapter_pks;
                                        print_pre(_book, 'white');
                                    }
                                    un_block();
                                }
                            });
                        }
                        else {
                            print_err('Usage : book-u "new-name"');
                        }
                    }
                },
                /* Creates a new book */
                {
                    keyword: "book-n",
                    desc: "creates a new book with it's name as argument",
                    action: function (arg) {
                        if (arg.length === 1) {
                            block();
                            print_out('Creating book ' + arg[0] + ' ...');
                            $.ajax({
                                url: 'elephant.php',
                                type: 'POST',
                                data: {
                                    'q': 'cb',
                                    'name': arg[0]
                                },
                                error: function () {
                                    print_err('Could not create book');
                                    un_block();
                                },
                                success: function (new_book) {
                                    CONTEXT.BOOK = JSON.parse(new_book);
                                    CONTEXT.CHAPTER = undefined;
                                    print_out('[OK]');
                                    // Print selected book
                                    var _tab = '                ';
                                    // Head
                                    var _head = 'pk' + _tab + 'name' + _tab + 'chapter pks';
                                    print_pre(_head, 'wheat');
                                    // Body
                                    var _book;
                                    _book = CONTEXT.BOOK.pk + _tab + CONTEXT.BOOK.name + _tab + 'No chapters yet';
                                    print_pre(_book, 'white');
                                    un_block();
                                }
                            });
                        }
                        else {
                            print_err('Usage : book-n "name"');
                        }
                    }
                },
                /* Deletes selected book */
                {
                    keyword: "book-d",
                    desc: "deletes selected book",
                    action: function (arg) {
                        block();
                        print_out('Deleting book with pk=' + CONTEXT.BOOK.pk + ' ...');
                        $.ajax({
                            url: 'elephant.php',
                            type: 'POST',
                            data: {
                                'q': 'db',
                                'pk': CONTEXT.BOOK.pk
                            },
                            error: function () {
                                print_err('Could not delete book');
                                un_block();
                            },
                            success: function (success) {
                                if (success === '-1') {
                                    print_err('No book with pk=' + CONTEXT.BOOK.pk);
                                    print_err('This error may be due to outdated db, please refresh and try again');
                                }
                                else if (success === '1') {
                                    CONTEXT.BOOK = undefined;
                                    CONTEXT.CHAPTER = undefined;
                                    print_out('Delete successful')
                                }
                                un_block();
                            }
                        });
                    }
                },
                /* Prints selected chapter */
                {
                    keyword: "chapter",
                    desc: "shows the currently selected chapter",
                    action: function (arg) {
                        // Show the chapter context
                        if (CONTEXT.CHAPTER === undefined) {
                            print_war('No chapter selected')
                        }
                        else {
                            // Print selected chapter
                            var _tab = '                ';
                            // Head
                            var _head = 'pk' + _tab + 'name';
                            print_pre(_head, 'wheat');
                            // Meta
                            var _book = CONTEXT.CHAPTER.pk + _tab + CONTEXT.CHAPTER.name;
                            print_pre(_book, 'white');
                            // Content
                            print_pre('Content', 'wheat');
                            print_pre(CONTEXT.CHAPTER.content, 'white');
                        }
                    }
                },
                /* Lists all chapters of selected book */
                {
                    keyword: "chapter-a",
                    desc: "lists all chapters in the selected book",
                    action: function (arg) {
                        if (CONTEXT.BOOK === undefined) {
                            print_err('No book selected');
                            print_err('Select a book first to see chapters in it');
                            return;
                        }
                        // Chapters
                        var _tab = '                ';
                        print_pre('Chapters', 'wheat');
                        var _head = 'pk' + _tab + 'name';
                        print_pre(_head, 'wheat');
                        for (var i = 0; i < CONTEXT.BOOK.chapter_pks.length; ++i) {
                            var ith_pk = CONTEXT.BOOK.chapter_pks[i];
                            var ith_name = CONTEXT.BOOK.chapter_names[i];
                            var _chapter = ith_pk + _tab + ith_name;
                            print_pre(_chapter, 'white');
                        }
                    }
                },
                /* Selects a chapter using pk in the selected book */
                {
                    keyword: "chapter-s",
                    desc: "selects a chapter with it's pk as argument",
                    action: function (arg) {
                        // If no book select book first
                        if (CONTEXT.BOOK === undefined) {
                            print_err('No book selected');
                            print_err('Select a book first to select chapters in it');
                            return;
                        }
                        if (arg.length === 1) {
                            // If selected book contains chapter with pk = arg
                            for (var i = 0; i < CONTEXT.BOOK.chapter_pks.length; i++) {
                                var ith_chapter_pk = CONTEXT.BOOK.chapter_pks[i];
                                if (arg[0] === String(ith_chapter_pk)) {
                                    block();
                                    print_out('Selecting chapter ' + CONTEXT.BOOK.chapter_names[i]);
                                    $.ajax({
                                        url: 'elephant.php',
                                        type: 'POST',
                                        data: {
                                            'q': 'sc',
                                            'pk': arg[0]
                                        },
                                        error: function () {
                                            print_err('Could not select chapter');
                                            un_block();
                                        },
                                        success: function (chapter) {
                                            if (chapter === '-1') {
                                                print_err('No chapter with pk=' + arg[0]);
                                                print_err('This error may be due to outdated db, please refresh and try again');
                                            }
                                            else {
                                                CONTEXT.CHAPTER = JSON.parse(chapter);
                                                print_out('[OK]');
                                                // Print selected book
                                                var _tab = '                ';
                                                // Head
                                                var _head = 'pk' + _tab + 'name';
                                                print_pre(_head, 'wheat');
                                                // Body
                                                var _chapter;
                                                _chapter = CONTEXT.CHAPTER.pk + _tab + CONTEXT.CHAPTER.name;
                                                print_pre(_chapter, 'white');
                                            }
                                            un_block();
                                        }
                                    });
                                    return;
                                }
                            }
                            // Else flag no chapter found
                            print_err('No chapter with pk=' + arg[0] + ' is found in book ' + CONTEXT.BOOK.name);
                        }
                        else {
                            print_err('Usage : chapter-s "pk"');
                        }
                    }
                },
                /* Creates a chapter using name in the selected book */
                {
                    keyword: "chapter-n",
                    desc: "creates a new chapter in the selected book with it's name as argument",
                    action: function (arg) {
                        // If no book select book first
                        if (CONTEXT.BOOK === undefined) {
                            print_err('No book selected');
                            print_err('Select a book first to create chapter in');
                            return;
                        }
                        if (arg.length === 1) {
                            block();
                            print_out('Creating chapter ' + arg[0] + ' in book ' + CONTEXT.BOOK.name + ' ...');
                            $.ajax({
                                url: 'elephant.php',
                                type: 'POST',
                                data: {
                                    'q': 'cc',
                                    'book_pk': CONTEXT.BOOK.pk,
                                    'name': arg[0]
                                },
                                error: function () {
                                    print_err('Could not create chapter');
                                    un_block();
                                },
                                success: function (chapter_book_pair) {
                                    if (chapter_book_pair === '-1') {
                                        print_err('No book with pk=' + CONTEXT.BOOK.pk);
                                        print_err('This error may be due to outdated db, please refresh and try again');
                                    }
                                    else {
                                        chapter_book_pair = JSON.parse(chapter_book_pair);
                                        CONTEXT.CHAPTER = chapter_book_pair[0];
                                        CONTEXT.BOOK = chapter_book_pair[1];
                                        print_out('[OK]');
                                        // Print selected book
                                        var _tab = '                ';
                                        // Head
                                        print_pre('Chapter', 'wheat');
                                        var _head = 'pk' + _tab + 'name';
                                        print_pre(_head, 'wheat');
                                        // Body
                                        var _chapter = CONTEXT.CHAPTER.pk + _tab + CONTEXT.CHAPTER.name;
                                        print_pre(_chapter, 'white');
                                    }
                                    un_block();
                                }
                            });
                        }
                        else {
                            print_err('Usage : chapter-n "name"');
                        }
                    }
                },
                /* Deletes selected chapter in the selected book */
                {
                    keyword: "chapter-d",
                    desc: "deletes selected chapter in the selected book",
                    action: function (arg) {
                        // If no book selected, select book first
                        if (CONTEXT.BOOK === undefined) {
                            print_err('No book selected');
                            print_err('Select a book first to delete chapters in it');
                            return;
                        }
                        // If no chapter selected, select chapter first
                        if (CONTEXT.CHAPTER === undefined) {
                            print_err('No chapter selected');
                            print_err('Select a chapter first to delete it');
                            return;
                        }

                        block();
                        print_out('Deleting chapter ' + CONTEXT.CHAPTER.name);
                        $.ajax({
                            url: 'elephant.php',
                            type: 'POST',
                            data: {
                                'q': 'dc',
                                'book_pk': CONTEXT.BOOK.pk,
                                'pk': CONTEXT.CHAPTER.pk
                            },
                            error: function () {
                                print_err('Could not select chapter');
                                un_block();
                            },
                            success: function (reduced_book) {
                                if (reduced_book === '-1') {
                                    print_err('No such book or no such chapter found');
                                    print_err('This error may be due to outdated db, please refresh and try again');
                                }
                                else {
                                    CONTEXT.BOOK = JSON.parse(reduced_book);
                                    CONTEXT.CHAPTER = undefined;
                                    print_out('Delete successful')
                                }
                                un_block();
                            }
                        });
                    }
                },
            ],
            /**
             * @return {boolean}
             * cmd found -> True
             * else      -> False
             * */
            evaluate: function (cmd) {
                // If the latest cmd is not "" & not the top one in history
                if (cmd !== "" && CMD.history[CMD.history.length - 1] !== cmd) {
                    // Stores cmd in history
                    CMD.history.push(cmd);
                }
                // Reset head
                CMD.head = CMD.history.length;

                try {
                    // Split the cmd
                    var cmd_split = cmd.split(" ");
                    // For all commands
                    for (var i = 0; i < CMD.list.length; ++i) {
                        var ith_cmd = CMD.list[i];
                        // If first word of cmd matches ith_cmd keyword
                        if (cmd_split[0] === ith_cmd.keyword) {
                            // Print the command
                            print_out(ShellSymbol + cmd);
                            // Pass the remaining words of cmd to that cmd's action method
                            ith_cmd.action(cmd_split.splice(1));
                            // Empty the cmd line
                            $($cmd).val("");
                            return true;
                        }
                    }
                    // Indicate cmd not available
                    print_err(ShellSymbol + cmd + ' : command not found');
                    // Empty the cmd line
                    $($cmd).val("");
                    return false;
                }
                catch (e) {
                    console.log(e);
                    return false;
                }
            },
            /**
             * Tries to match given cmd with any of the listed cmds keywords
             * no  match found          -> Nothing happens
             * one match found          -> Auto completes the command
             * multiple matches found   -> Displays all matching cmds
             * */
            auto_complete: function (cmd) {
                if (cmd === "")return;
                var regex = new RegExp('^' + cmd, 'i');
                var matches = [];
                // For each cmd
                for (var i = 0; i < CMD.list.length; ++i) {
                    // If the regex matches
                    if (regex.test(CMD.list[i].keyword)) {
                        // Keep track of it
                        matches.push(CMD.list[i].keyword);
                    }
                }
                // If only one command auto complete
                if (matches.length === 1) {
                    $($cmd).val(matches[0] + " ");
                }
                // If more display them on terminal
                else if (matches.length > 1) {
                    // Print the command
                    print_out(ShellSymbol + cmd);
                    var available_commands = matches.join("\n");
                    print_span(available_commands, 'skyblue');
                }
            },

            history: [],
            head: 0,
            go_forward: function () {
                if (0 <= CMD.head + 1 && CMD.head + 1 <= CMD.history.length - 1) {
                    CMD.head++;
                    $($cmd).val(CMD.history[CMD.head]);
                }
                else if (CMD.head + 1 === CMD.history.length) {
                    $($cmd).val("");
                }
            },
            go_backward: function () {
                if (0 <= CMD.head - 1 && CMD.head - 1 <= CMD.history.length - 1) {
                    CMD.head--;
                    $($cmd).val(CMD.history[CMD.head]);
                }
            }
        };

        var ShellSymbol = _element(
            ['span'],
            [{'style': 'color="#64dd17"'}],
            ['$: '],
            [true]
        );

        /**
         * Builds html strings which can be directly appended to a DOM object
         * */
        function _element(tags, attributes, contents, closings) {
            var $element = "";// returning string
            var iter;
            for (iter = 0; iter < tags.length; ++iter) {

                $element += "<" + tags[iter];

                for (var key in attributes[iter]) {
                    if (attributes[iter].hasOwnProperty(key)) {
                        //# adding an attribute to the tags[i]#
                        $element += ( " " + key + "=\'" + attributes[iter][key] + "\'");
                    }
                }

                $element += ">";
                $element += contents[iter];
            }

            for (iter = tags.length - 1; iter > -1; --iter) {
                if (closings[iter]) {
                    $element += "</" + tags[iter] + ">"
                }
            }

            return $element;
        }

        function print_span(string, color) {
            color = color === undefined ? 'green' : color;
            $($log).append(_element(
                ['span'],
                [{'style': 'color:' + color}],
                [string],
                [true]
            ));
            $($log).append("<br>");
            $($terminal).scrollTop($($terminal).prop('scrollHeight'));
        }

        function print_pre(msg, color) {
            color = color === undefined ? 'green' : color;
            $($log).append(_element(
                ['pre'],
                [
                    {
                        'style': 'color:' + color + ';' +
                        'margin: 0px;'
                    }
                ],
                [msg],
                [true]
            ));
            $($terminal).scrollTop($($terminal).prop('scrollHeight'));
        }

        function clear() {
            $($log).empty();
        }

        function print_out(msg) {
            print_span(msg, '#64dd17');
        }

        function print_war(msg) {
            print_span(msg, 'yellow');
        }

        function print_err(msg) {
            print_span(msg, 'red');
        }

        function print_br() {
            print_span('');
        }

        function block() {
            $($cmd).prop('disabled', true).parent().hide();
        }

        function un_block() {
            $($cmd).prop('disabled', false).parent().show();
            $($cmd).focus();
        }

        function REPL(event) {
            var cmd;
            switch (event.keyCode || event.which) {
                // Enter
                case 13:
                    cmd = $($cmd).val();
                    cmd = cmd.trim().replace(/ +/g, ' ');
                    CMD.evaluate(cmd);
                    break;
                // Tab Key
                case 9:
                    event.preventDefault();
                    cmd = $($cmd).val();
                    cmd = cmd.trim().replace(/ +/g, ' ');
                    CMD.auto_complete(cmd);
                    break;
                // Up Arrow
                case 38:
                    // Going backward in history
                    CMD.go_backward();
                    break;
                // Down Arrow
                case 40:
                    // Going forward in history
                    CMD.go_forward();
                    break;
                // Escape Key
                case 27:
                    clear();
                    break;
                // None of the above
                default:
                    break;
            }
        }

        $(document).ready(function () {
            // Gets references
            $cmd = $('#cmd');
            $log = $('#log');
            $terminal = $('#terminal');

            // Cmd line listener
            $($cmd).keydown(function (e) {
                    REPL(e);
                }
            );
        });
    </script>
    <div id="terminal"
         class="row black"
         style="position: fixed;top: 0;width: 100vw;height: 100vh;
            margin: 0;z-index: 100;overflow-y: auto;padding: 20px;
            font-family: 'Ubuntu Mono', monospace;color: #64dd17;
            font-size: large">
        <div id="log"></div>
        <span>$: <input id="cmd"
                        type="text"
                        style="border: none;margin: 0;width: 90%;height: 20px;box-shadow: none;font-size: large"
                        autofocus>
        </span>
    </div>

    <!--EDITOR-->
    <script>
        var $editor;
        var $book_name;
        var $chapter_name;
        var $chapter_content;

        function close() {
            $($editor).hide();
        }

        $(document).ready(function () {
            // Gets references
            $editor = $('#editor');
            $book_name = $("#book_name");
            $chapter_name = $('#chapter_name');
            $chapter_content = $('#chapter_content');

            // Cmd line listener
            $($chapter_content).on(
                'keydown',
                function (e) {
                    switch (e.keyCode) {
                        case 27:
                            close();
                            break;
                        default:
                            break;
                    }
                }
            )
        });
    </script>
    <div id="editor" class="row white"
         style="position: fixed;top: 0;width: 100vw;height: 100vh;margin: 0;z-index: 99;overflow-y: auto;padding: 20px;">
        <div id="book_name">book</div>
        <div id="chapter_name">chapter</div>
        <textarea id="chapter_content"
                  class=""
                  title="content"></textarea>
    </div>
</div>
</body>
</html>
