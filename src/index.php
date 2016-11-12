<!DOCTYPE html>
<html>
<head>
    <!--LIB HEADERS-->
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="//code.jquery.com/ui/1.12.0/jquery-ui.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="header.js"></script>
    <!-- TITLE -->
    <title>schoolbag</title>
    <link rel="icon" href="../static/logo.png">
    <!--CSS INCLUDES-->
    <style>
        .writable {
            width: 100%;
            height: 100%;
            border: none;
            overflow: auto;
            padding: 10px;
        }

        .list {
            font-size: large;
            max-height: 100%;
        }

        .select:hover {
            cursor: pointer;
        }

        .list a {
            color: black;
        }

        .floating_button {
            height: 100px;
            width: 100px;
            border-radius: 50px;
            position: fixed;
            bottom: 20px;
            right: 20px;
            z-index: 9999;
            font-family: monospace;
            background-color: deeppink;
            color: white;
            opacity: 0.7;
        }

        .floating_button:hover {
            background-color: deeppink;
            color: white;
            font-size: x-large;
            opacity: 1.0;
        }

        .floating_button:focus {
            background-color: deeppink;
            color: white;
            outline: none;
        }
    </style>
    <!-- JS DECLARATIONS -->
    <script>
        var manager;
        var ajax_manager = new AJAXManager();

        /**
         * Manages operation of schoolbag
         *      initializing schoolbag engine
         *      create read update delete <<< books and chapters
         * */
        function Manager(context) {

            this.context = context;

            /* read part */
            this.populate_book_list = function () {
                var list = this.context.book_list();
                if (list == undefined)return;
                var book_list = $('#book_list');
                var html1 = "<tr><td class='select' id='";
                var html2 = "'><a href='#'>";
                var html3 = "</a></td></tr>";

                $(book_list).empty();
                for (var i = 0; i < list.length; ++i) {
                    $(book_list).append(html1 + "book_list_item_" + String(list.item(i).getAttribute('pk')) + html2 + list.item(i).getAttribute('name') + html3);
                }
            };

            this.populate_chapter_list = function (book_pk) {
                var list = this.context.chapter_list(book_pk);
                if (list == undefined)return;
                var chapter_list = $('#chapter_list');
                var html1 = "<tr><td class='select' id='";
                var html2 = "'><a href='#'>";
                var html3 = "</a></td></tr>";

                $(chapter_list).empty();
                for (var i = 0; i < list.length; ++i) {
                    $(chapter_list).append(
                        html1 +
                        "chapter_list_item_" +
                        String(list.item(i).getAttribute('pk')) +
                        html2 +
                        list.item(i).getAttribute('name') +
                        html3
                    );
                }
            };

            this.initSelectListeners = function (context) {

                $('#book_list').on('click', '.select', function () {
                    var id = $(this).attr('id');
                    var first_chapter = context.first_chapter(Number(id.split("_")[3]));
                    manager.select(context.book(Number(id.split("_")[3])), first_chapter);
                });

                $('#chapter_list').on('click', '.select', function () {
                    var id = $(this).attr('id');
                    var book = context.book(context.book_pk);
                    manager.select(book, context.chapter(book.getAttribute('pk'), Number(id.split("_")[3])));
                });

                $('#book_search').on('keyup', function () {
                    var pattern = "^" + $(this).val();
                    var regex = new RegExp(pattern, 'i');
                    var book_list = context.book_list();
                    if (book_list == undefined)return;
                    for (var i = 0; i < book_list.length; ++i) {
                        var name = book_list.item(i).getAttribute('name');
                        if (regex.test(name)) {
                            $('#book_list_item_' + book_list.item(i).getAttribute('pk')).fadeIn(0);
                        }
                        else {
                            $('#book_list_item_' + book_list.item(i).getAttribute('pk')).fadeOut(0);
                        }
                    }
                });

                $('#chapter_search').on('keyup', function () {
                    var pattern = "^" + $(this).val();
                    var regex = new RegExp(pattern, 'i');
                    var chapter_list = context.chapter_list(context.book_pk);
                    if (chapter_list == undefined)return;
                    for (var i = 0; i < chapter_list.length; ++i) {
                        var name = chapter_list.item(i).getAttribute('name');
                        if (regex.test(name)) {
                            $('#chapter_list_item_' + chapter_list.item(i).getAttribute('pk')).fadeIn(0);
                        }
                        else {
                            $('#chapter_list_item_' + chapter_list.item(i).getAttribute('pk')).fadeOut(0);
                        }
                    }
                });

            };

            this.initUpdateListeners = function (context) {
                /* assuming that context.book_pk and context.chapter_pk
                 point to the currently viewing book and chapter */
                $('#book_name').on('keyup', function () {
                    var book = context.book(context.book_pk);
                    if (book == undefined)return;
                    var book_list_item = $('#book_list_item_' + book.getAttribute('pk'));
                    $(book_list_item).find('a').text($(this).val());
                    book.setAttribute('name', $(this).val());
                });
                $('#chapter_name').on('keyup', function () {
                    var chapter = context.chapter(context.book_pk, context.chapter_pk);
                    if (chapter == undefined)return;
                    var chapter_list_item = $('#chapter_list_item_' + chapter.getAttribute('pk'));
                    $(chapter_list_item).find('a').text($(this).val());
                    chapter.setAttribute('name', $(this).val());
                });
                $('#chapter_content').on('keyup', function () {
                    var chapter = context.chapter(context.book_pk, context.chapter_pk);
                    if (chapter == undefined)return;
                    chapter.textContent = $(this).val();
                });
                $('#synchronize').on('click', function () {
                    ajax_manager.open_and_send
                    (
                        {
                            "q": "sync",
                            "doc": new XMLSerializer().serializeToString(context.document)
                        },
                        [
                            function () {
                                alert("Sync Successful!");
                            }
                        ]
                    );
                });
            };

            this.select = function (book, chapter) {
                /* referencing */
                var book_name = $('#book_name');
                var chapter_name = $('#chapter_name');
                var chapter_content = $('#chapter_content');
                /* emptying */
                $(book_name).val("");
                $(chapter_name).val("");
                $(chapter_content).val("");
                /* filling */
                if (book == undefined) return;
                $(book_name).val(book.getAttribute('name'));
                this.populate_chapter_list(book.getAttribute('pk'));
                if (chapter == undefined) return;
                $(chapter_name).val(chapter.getAttribute('name'));
                $(chapter_content).val(chapter.textContent);
            }
        }

    </script>
    <!-- JS -->
    <script>
        $(document).ready(function () {
            /* In built's */
            $('[data-toggle="tooltip"]').tooltip();

            ajax_manager.open_and_send
            (
                {
                    "q": "open"
                },
                [
                    function () {
                        manager = new Manager(new Context(AJAXManager.xmlResponse));
                        manager.populate_book_list();
                        manager.initSelectListeners(manager.context);
                        manager.initUpdateListeners(manager.context);
                        manager.select(manager.context.book(manager.context.book_pk),
                            manager.context.chapter(manager.context.book_pk, manager.context.chapter_pk));
                    }
                ]
            );
        });
    </script>
</head>

<body class="container-fluid">
<div class="row"
     style="padding: 10px;">
    <div class="col-xs-2">
        <input id="chapter_search"
               class="writable"
               style="text-align: center;background-color: rgba(0, 0, 0, 0.05)"
               type="text"
               placeholder="Find Chapters">
        <br>
        <br>
        <table class="table table-hover table-striped">
            <tbody id="chapter_list"
                   class="list">

            </tbody>
        </table>
    </div>
    <div class="col-xs-8">
        <div class="row">
            <div class="col-xs-6">
                <input id="chapter_name"
                       class="writable"
                       type="text"
                       placeholder="Chapter">
            </div>
            <div class="col-xs-6">
                <input id="book_name"
                       class="writable"
                       type="text"
                       placeholder="Book">
            </div>
        </div>
        <textarea id="chapter_content"
                  class="writable"
                  placeholder="Content"
                  rows="40"></textarea>
    </div>
    <div class="col-xs-2">
        <input id="book_search"
               class="writable"
               style="text-align: center;background-color: rgba(0, 0, 0, 0.05)"
               type="text"
               placeholder="Find Books">
        <br>
        <br>
        <table class="table table-hover table-striped">
            <tbody id="book_list"
                   class="list">

            </tbody>
        </table>
    </div>
</div>

<button id="synchronize" class="floating_button btn btn-default"
        data-toggle="tooltip"
        title="Synchronize">
    <span class="glyphicon glyphicon-refresh"></span>
</button>
</body>

</html>
