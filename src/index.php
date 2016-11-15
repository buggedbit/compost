<!DOCTYPE html>
<html>
<head>
    <!--LIB HEADERS-->
    <link href="https://fonts.googleapis.com/css?family=Audiowide|Crafty+Girls" rel="stylesheet">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>
    <script src="header.js"></script>
    <!-- TITLE -->
    <title>schoolbag</title>
    <link rel="icon" href="../static/logo.png">
    <!--CSS INCLUDES-->
    <style>
        ::-moz-selection { /* Code for Firefox */
            color: white;
            background: darkorange;
        }

        ::selection {
            color: white;
            background: darkorange;
        }

        body {
            background-image: url("http://www.walldevil.com/wallpapers/a82/wallpaper-graphic-design-colorful-square-background-texture-art-desktop-minimalist-black.jpg");
            font-family: 'Crafty Girls', cursive;
            font-size: xx-large;
        }

        .modal {
            font-size: xx-large;
        }

        .writable {
            width: 100%;
            height: 100%;
            border: none;
            overflow: auto;
            padding: 10px;
            cursor: pointer;
            background-color: rgba(0, 0, 0, 0.60);
            color: white;
        }

        .writable:focus {
            outline-style: dashed;
            outline-color: white;
            outline-width: thin;
            outline-offset: 1px;
        }

        .list {
            font-size: large;
            max-height: 100%;
            font-family: 'Audiowide', cursive;
        }

        .select:hover {
            cursor: pointer;
        }

        .search {
            font-size: large;
            text-align: center;
            color: white;
            background-color: transparent;
        }

        .floating_button {
            height: 100px;
            width: 100px;
            border-radius: 50px;
            position: fixed;
            bottom: 20px;
            right: 20px;
            z-index: 9990;
            font-family: monospace;
            font-size: x-large;
            background-color: white;
            color: white;
            opacity: 0.95;
            border-width: 0;
        }

        .floating_button:hover {
            background-color: deeppink;
            color: white;
            font-size: xx-large;
            opacity: 1.0;
        }

        .floating_button:focus {
            background-color: deeppink;
            color: white;
            outline: none;
        }

        .message_box {
            height: 500px;
            width: 500px;
            position: fixed;
            bottom: 0;
            left: 0;
            background-color: rgba(91, 91, 91, 0.95);
            z-index: 9999;
            padding: 20px;
            text-align: center;
        }

        .message {
            color: white;
            position: relative;
            font-family: 'Audiowide', cursive;
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
                var book_count = $('#book_count');
                if (list == undefined) {
                    $(book_count).html("0");
                    return;
                }
                $(book_count).html(String(list.length));
                var book_list = $('#book_list');
                var html1 = "<tr><td class='select' id='";
                var html2 = "'><a href='#'>";
                var html3 = "</a></td><td><button id='";
                var html4 = "'class='btn-danger delete'>del</button></td><td><button id='";
                var html5 =
                    "'class='btn-success create' " +
                    "data-toggle='modal' " +
                    "title='New Chapter' " +
                    "data-target='#chapter_create_modal'>+</button></tr>";

                $(book_list).empty();
                for (var i = 0; i < list.length; ++i) {
                    $(book_list).append(
                        html1
                        + "book_list_item_"
                        + String(list.item(i).getAttribute('pk'))
                        + html2
                        + list.item(i).getAttribute('name')
                        + html3
                        + "book_list_item_delete_"
                        + String(list.item(i).getAttribute('pk'))
                        + html4
                        + "book_list_item_chapter_create_"
                        + String(list.item(i).getAttribute('pk'))
                        + html5
                    );
                }
            };

            this.populate_chapter_list = function (book_pk) {
                var list = this.context.chapter_list(book_pk);
                var chapter_count = $('#chapter_count');
                if (list == undefined) {
                    $(chapter_count).html("0");
                    return;
                }
                $(chapter_count).html(String(list.length));
                var chapter_list = $('#chapter_list');
                var html1 = "<tr><td class='select' id='";
                var html2 = "'><a href='#'>";
                var html3 = "</a></td><td><button id='";
                var html4 = "'class='btn-danger delete'>del</button></td></tr>";

                $(chapter_list).empty();
                for (var i = 0; i < list.length; ++i) {
                    $(chapter_list).append(html1
                        + "chapter_list_item_"
                        + String(list.item(i).getAttribute('pk'))
                        + html2
                        + list.item(i).getAttribute('name')
                        + html3
                        + "chapter_list_item_delete_"
                        + String(list.item(i).getAttribute('pk'))
                        + html4
                    );
                }
            };

            this.initSelectAndDeleteListeners = function (context) {

                $('#book_list').on('click', '.select', function () {
                    var id = $(this).attr('id');
                    var first_chapter = context.first_chapter(Number(id.split("_")[3]));
                    manager.select(context.book(Number(id.split("_")[3])), first_chapter);
                }).on('click', '.delete', function () {
                    var id = $(this).attr('id');
                    context.book_delete(Number(id.split("_")[4]));
                    $(this).parent().parent().remove();
                    var book_count_hint = $('#book_count');
                    var book_count = Number($(book_count_hint).html());
                    $(book_count_hint).html(String(book_count - 1));
                }).on('click', '.create', function () {
                    var id = $(this).attr('id');
                    var first_chapter = context.first_chapter(Number(id.split("_")[5]));
                    manager.select(context.book(Number(id.split("_")[5])), first_chapter);
                });

                $('#chapter_list').on('click', '.select', function () {
                    var id = $(this).attr('id');
                    var book = context.book(context.book_pk);
                    manager.select(book, context.chapter(book.getAttribute('pk'), Number(id.split("_")[3])));
                }).on('click', '.delete', function () {
                    var id = $(this).attr('id');
                    context.chapter_delete(context.book_pk, Number(id.split("_")[4]));
                    $(this).parent().parent().remove();
                    var chapter_count_hint = $('#chapter_count');
                    var chapter_count = Number($(chapter_count_hint).html());
                    $(chapter_count_hint).html(String(chapter_count - 1));
                });

                $('#book_search').on('keyup', function () {
                    var pattern = "^" + $(this).val();
                    var regex = new RegExp(pattern, 'i');
                    var book_list = context.book_list();
                    if (book_list == undefined)return;
                    var count = 0;
                    for (var i = 0; i < book_list.length; ++i) {
                        var name = book_list.item(i).getAttribute('name');
                        if (regex.test(name)) {
                            $('#book_list_item_' + book_list.item(i).getAttribute('pk')).parent().fadeIn(0);
                            count++;
                        }
                        else {
                            $('#book_list_item_' + book_list.item(i).getAttribute('pk')).parent().fadeOut(0);
                        }
                    }
                    $('#book_count').html(String(count));
                });

                $('#chapter_search').on('keyup', function () {
                    var pattern = "^" + $(this).val();
                    var regex = new RegExp(pattern, 'i');
                    var chapter_list = context.chapter_list(context.book_pk);
                    if (chapter_list == undefined)return;
                    var count = 0;
                    for (var i = 0; i < chapter_list.length; ++i) {
                        var name = chapter_list.item(i).getAttribute('name');
                        if (regex.test(name)) {
                            $('#chapter_list_item_' + chapter_list.item(i).getAttribute('pk')).parent().fadeIn(0);
                            count++;
                        }
                        else {
                            $('#chapter_list_item_' + chapter_list.item(i).getAttribute('pk')).parent().fadeOut(0);
                        }
                    }
                    $('#chapter_count').html(String(count));
                });

            };

            this.initUpdateListeners = function (context) {
                /* assuming that context.book_pk and context.chapter_pk
                 point to the currently viewing book and chapter */
                $('#book_name').on('keyup', function () {
                    var regex = new RegExp("[<>&\"']");
                    var illegal = regex.test($(this).val());
                    if (illegal) {
                        alert("< , > , & , \" , ' are not allowed");
                        return;
                    }
                    var book = context.book(context.book_pk);
                    if (book == undefined)return;
                    var book_list_item = $('#book_list_item_' + book.getAttribute('pk'));
                    $(book_list_item).find('a').text($(this).val());
                    book.setAttribute('name', $(this).val());
                });
                $('#chapter_name').on('keyup', function () {
                    var regex = new RegExp("[<>&\"']");
                    var illegal = regex.test($(this).val());
                    if (illegal) {
                        alert("< , > , & , \" , ' are not allowed");
                        return;
                    }
                    var chapter = context.chapter(context.book_pk, context.chapter_pk);
                    if (chapter == undefined)return;
                    var chapter_list_item = $('#chapter_list_item_' + chapter.getAttribute('pk'));
                    $(chapter_list_item).find('a').text($(this).val());
                    chapter.setAttribute('name', $(this).val());
                });
                $('#chapter_content').on('keyup', function () {
                    var regex = new RegExp("[<>&\"']");
                    var illegal = regex.test($(this).val());
                    if (illegal) {
                        alert("< , > , & , \" , ' are not allowed");
                        return;
                    }
                    var chapter = context.chapter(context.book_pk, context.chapter_pk);
                    if (chapter == undefined)return;
                    chapter.textContent = $(this).val();
                }).on('keydown', function (e) {
                    if (e.keyCode == 9 || e.which == 9) {
                        e.preventDefault();
                        var s = this.selectionStart;
                        this.value = this.value.substring(0, this.selectionStart) + "\t" + this.value.substring(this.selectionEnd);
                        this.selectionEnd = s + 1;
                    }
                });
                $('#synchronize').on('click', function () {
                    manager.synchronize();
                });
            };

            this.initCreateListeners = function (context) {
                $('#chapter_create').on('click', function () {
                    var name = $('#chapter_create_name').val();
                    var regex = new RegExp("[<>&\"']");
                    var illegal = regex.test(name);
                    if (illegal) {
                        alert("< , > , & , \" , ' are not allowed");
                        return;
                    }
                    if (context.chapter_create(name, context.book_pk)) {
                        manager.populate_chapter_list(context.book_pk);
                        manager.showMessage("New Chapter "
                            + name
                            + " created in "
                            + context.book(context.book_pk).getAttribute('name'));
                    }
                });

                $('#book_create').on('click', function () {
                    var name = $('#book_create_name').val();
                    var regex = new RegExp("[<>&\"']");
                    var illegal = regex.test(name);
                    if (illegal) {
                        alert("< , > , & , \" , ' are not allowed");
                        return;
                    }
                    if (context.book_create(name)) {
                        manager.populate_book_list();
                        manager.showMessage(" New Book "
                            + name);
                    }
                });
            };

            this.initLogListener = function () {
                $('#log').on('click', function () {
                    manager.log();
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
            };

            this.synchronize = function () {
                ajax_manager.open_and_send
                (
                    {
                        "q": "sync",
                        "doc": new XMLSerializer().serializeToString(context.document)
                    },
                    [
                        function () {
                            manager.showMessage(AJAXManager.textResponse);
                        }
                    ]
                );
            };

            this.log = function () {
                ajax_manager.open_and_send
                (
                    {
                        "q": "log",
                        "log_credential": $('#log_credential').val()
                    },
                    [
                        function () {
                            $('#log_credential').val("");
                            manager.showMessage(AJAXManager.textResponse);
                        }
                    ]
                );
            };

            this.showMessage = function (message) {
                $('#message').text(message);
                var message_box = $('#message_box');
                $(message_box).slideDown().delay(1000).slideUp();
            };
        }

    </script>
    <!-- JS -->
    <script>
        $(document).ready(function () {
            /* In built's */
            $('[data-toggle="tooltip"]').tooltip();
            if (!String.prototype.encodeHTML) {
                String.prototype.encodeHTML = function () {
                    return this.replace(/&/g, '&amp;')
                        .replace(/</g, '&lt;')
                        .replace(/>/g, '&gt;')
                        .replace(/"/g, '&quot;')
                        .replace(/'/g, '&apos;');
                };
            }
            $(window).bind('beforeunload', function () {
//                manager.synchronize();
            });
            ajax_manager.open_and_send
            (
                {
                    "q": "open"
                },
                [
                    function () {
                        manager = new Manager(new Context(AJAXManager.xmlResponse));
                        manager.populate_book_list();
                        manager.initSelectAndDeleteListeners(manager.context);
                        manager.initUpdateListeners(manager.context);
                        manager.initCreateListeners(manager.context);
                        manager.initLogListener();
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
               class="writable search"
               type="text"
               placeholder="Find Chapters">
        <br>
        <br>
        <table class="table table-hover">
            <h3 style="color: white;"><span id="chapter_count"></span> chapters available </h3>
            <tbody id="chapter_list"
                   class="list">

            </tbody>
        </table>
    </div>
    <div class="col-xs-8">
        <blockquote>
            <div class="row">
                <div class="col-xs-8">
                    <input id="chapter_name"
                           class="writable text-info"
                           style="font-size: x-large;background-color: transparent"
                           data-toggle="tooltip"
                           data-placement="left"
                           title="Chapter"
                           type="text"
                           placeholder="Chapter">
                </div>
                <div class="col-xs-1"></div>
                <div class="col-xs-3">
                    <input id="book_name"
                           class="writable"
                           style="text-align: right;
                              font-size: larger;
                              background-color: transparent"
                           data-toggle="tooltip"
                           data-placement="right"
                           title="Book"
                           type="text"
                           placeholder="Book">
                </div>
            </div>
        </blockquote>
        <textarea id="chapter_content"
                  class="writable"
                  style="font-size: x-large;max-height: 800px;height: 800px"
                  data-toggle="tooltip"
                  data-placement="top"
                  title="Your Thoughts"
                  placeholder="Content"></textarea>
    </div>
    <div class="col-xs-2">
        <input id="book_search"
               class="writable search"
               type="text"
               placeholder="Find Books">
        <br>
        <br>
        <table class="table table-hover ">
            <h3 style="color: white;"><span id="book_count"></span> books available </h3>
            <tbody id="book_list"
                   class="list">
            </tbody>
        </table>
    </div>
</div>

<!--SYNCHRONIZE-->
<button id="synchronize"
        class="floating_button btn-default"
        style="background-color: white;color: black;"
        data-toggle="tooltip"
        title="Synchronize">
    <span class="glyphicon glyphicon-refresh"></span>
</button>

<!--MESSAGE BOX-->
<div id="message_box"
     class="message_box"
     style="display: none">
    <p id="message" class="message"></p>
</div>

<!--NEW CHAPTER-->
<div class="modal fade"
     id="chapter_create_modal"
     role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Give this chapter a name...</h4>
            </div>
            <div class="modal-body">
                <input type="text"
                       id="chapter_create_name">
                <input type="button"
                       id="chapter_create"
                       value="Go"
                       class="btn-success"
                       data-dismiss="modal">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<!--NEW BOOK-->
<div class="modal fade"
     id="book_create_modal"
     role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Give this book a name...</h4>
            </div>
            <div class="modal-body">
                <input type="text"
                       id="book_create_name">
                <input type="button"
                       id="book_create"
                       value="Go"
                       class="btn-success"
                       data-dismiss="modal">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<button class="floating_button"
        style="background-color: white;color: black;right: 160px;"
        data-toggle="modal"
        title="New Book"
        data-target="#book_create_modal">
    <span data-toggle="tooltip"
          title="New book"
          data-placement="top"
          class="glyphicon glyphicon-book"></span>
</button>

<!--LOG-->
<div class="modal fade"
     id="log_modal"
     role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4 class="modal-title">Give this book a name...</h4>
            </div>
            <div class="modal-body">
                <input type="password"
                       id="log_credential">
                <input type="button"
                       id="log"
                       value="Go"
                       class="btn-info"
                       data-dismiss="modal">
            </div>
            <div class="modal-footer">
                <button type="button" class="btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>
    </div>
</div>

<button class="floating_button"
        style="background-color: white;color: black;bottom: 160px;"
        data-toggle="modal"
        title="New Book"
        data-target="#log_modal">
    <span data-toggle="tooltip"
          title="Log"
          data-placement="top"
          class="glyphicon glyphicon-off"></span>
</button>

</body>

</html>














