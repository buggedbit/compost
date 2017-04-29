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

</head>
<body>
<div class="row">
    <!--CONTEXT-->
    <script>
        var book = undefined;
        var chapter = undefined;
    </script>

    <!--TERMINAL-->
    <script>
        var $cmd;
        var $log;
        var $terminal;

        var CMD = {
            /**
             * Assert distinct keywords for all commands
             * The string part after <keyword><SPACE> is passed as an argument to action method of the cmd
             * */
            list: [
                {
                    keyword: "",
                    desc: "",
                    action: function (arg) {
                        print_out(ShellSymbol);
                    }
                },
                {
                    keyword: "help",
                    desc: "",
                    action: function (arg) {
                        for (var i = 0; i < CMD.list.length; i++) {
                            var ith_cmd = CMD.list[i];
                            print_span(ith_cmd.keyword, 'white');
                        }
                    }
                },
                {
                    keyword: "clear",
                    desc: "",
                    action: function (arg) {
                        clear();
                    }
                },
                {
                    keyword: "print",
                    desc: "",
                    action: function (arg) {
                        print_out(arg);
                    }
                },
                {
                    keyword: "book",
                    desc: "",
                    action: function (arg) {
                        if (book === undefined) {
                            print_war('No book selected')
                        }
                    }
                },
                {
                    keyword: "books",
                    desc: "",
                    action: function (arg) {
                        block();
                        print_out('Getting all books ...');
                        $.ajax({
                            url: 'elephant.php',
                            type: 'POST',
                            data: {
                                'q': 'rb'
                            },
                            error: function () {
                                print_err('Could not get all books');
                                un_block();
                            },
                            success: function (books) {
                                print_out('Got all books');
                                books = JSON.parse(books);
                                // Print table head
                                var _tab = '    ';
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
                }
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
                    // For all commands
                    for (var i = 0; i < CMD.list.length; ++i) {
                        var ith_cmd = CMD.list[i];
                        // If first word matches ith_cmd keyword
                        if (ith_cmd.keyword === cmd.split(" ")[0]) {
                            // Print the command
                            if (ith_cmd.keyword !== "") {
                                print_out(ShellSymbol + cmd);
                            }
                            // Pass the remaining part of the string to that cmd's action method
                            ith_cmd.action(cmd.slice(ith_cmd.keyword.length + 1, cmd.length));
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
             * no  cmd found        -> Nothing happens
             * one cmd found        -> Auto completes the command
             * multiple cmd found   -> Displays all matching cmds
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
                    CMD.evaluate(cmd);
                    break;
                // Tab Key
                case 9:
                    event.preventDefault();
                    cmd = $($cmd).val();
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
            font-family: 'Ubuntu Mono', monospace;color: #64dd17">
        <div id="log"></div>
        <span>$: <input id="cmd"
                        type="text"
                        style="border: none;margin: 0;width: 90%;height: 20px;box-shadow: none" autofocus>
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
