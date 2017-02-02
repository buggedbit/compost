<!DOCTYPE html>
<html>
<head>
    <!--LIB HEADERS-->
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/css/materialize.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/js/materialize.min.js"></script>

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

        .writable {
            width: 100%;
            height: 100%;
            border: none;
            overflow: auto;
            padding: 10px;
            cursor: pointer;
        }

        .writable:focus {
            border: none;
            outline: none;
        }

        .list {
            font-size: large;
        }

        .select:hover {
            cursor: pointer;
        }

        .search {
            font-size: xx-large;
            text-align: center;
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
                var html1 = "<tr><td class='select truncate' id='";
                var html2 = "'><a href='#'>";
                var html3 = "</a></td><td><a id='";
                var html4 = "'class='btn-danger delete btn red white-text'><i class='material-icons'>delete</i></a></td><td><a id='";
                var html5 =
                    "'class='btn create' " +
                    "href='#chapter_new'><i class='material-icons'>note_add</i></a></tr>";

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
                var html1 = "<tr class='teal-text'><td class='select truncate' id='";
                var html2 = "'><a href='#'>";
                var html3 = "</a></td><td><a id='";
                var html4 = "'class='btn-danger delete btn red white-text'><i class='material-icons'>delete</i></a></td></tr>";

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
                Materialize.toast(message, 1500);
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
    <!-- MATERIAL JS -->
    <script>
        $(document).ready(function () {
                $('.modal').modal();
                $("#chapter_nav_btn").sideNav({
                    menuWidth: 300, // Default is 240
                    edge: 'left', // Choose the horizontal origin
                    closeOnClick: true, // Closes side-nav on <a> clicks, useful for Angular/Meteor
                    draggable: true // Choose whether you can drag to open on touch screens
                });
                $("#book_nav_btn").sideNav({
                        menuWidth: 400, // Default is 240
                        edge: 'right', // Choose the horizontal origin
                        closeOnClick: true, // Closes side-nav on <a> clicks, useful for Angular/Meteor
                        draggable: true // Choose whether you can drag to open on touch screens
                    }
                );
            }
        );
    </script>
</head>

<body>

<div id="chapter_nav" class="side-nav black-text">
    <div class="input-field">
        <input id="chapter_search"
               class="search pink-text"
               type="text"
               placeholder="Find Chapters">
    </div>
    <!--NEW CHAPTER-->
    <a href="#chapter_new" class="btn btn-large pink"><i class="material-icons">note_add</i></a>
    <h5 class="center-align teal-text"><span id="chapter_count"></span> chapter(s) available</h5>
    <table class="striped centered">
        <colgroup>
            <col width="90%">
            <col width="10%">
        </colgroup>
        <tbody style="overflow: auto" id="chapter_list"
               class="list">
        </tbody>
    </table>
</div>


<div id="book_nav" class="side-nav black-text">
    <div class="input-field">
        <input id="book_search"
               class="search pink-text"
               type="text"
               placeholder="Find Books">
    </div>
    <!--NEW BOOK-->
    <a href="#book_new" class="btn btn-large green"><i class="material-icons">queue</i></a>
    <h5 class="center-align teal-text"><span id="book_count"></span> book(s) available</h5>
    <table class="striped centered">
        <colgroup>
            <col width="80%">
            <col width="10%">
            <col width="10%">
        </colgroup>
        <tbody style="overflow: auto" id="book_list"
               class="list">
        </tbody>
    </table>
</div>

<div class="row">
    <div>
        <div class="row">
            <div class="col l8 m8 s8">
                <label for="chapter_name">Chapter</label>
                <input id="chapter_name"
                       class="writable text-info"
                       type="text"
                       placeholder="Chapter">
            </div>
            <div class="col l4 m4 s4">
                <label for="book_name">Book</label>
                <input id="book_name"
                       class="writable"
                       type="text"
                       placeholder="Book">
            </div>
        </div>
    </div>
</div>

<div class="container">
	<div class="hoverable card z-depth-2">
        <div class="card-content">
		<div class="card-title">Content</div>	
        <label for="chapter_content"></label>
        <textarea id="chapter_content"
                  class="writable"
                  style="height: 78vh;font-size: large;"></textarea>
        </div>
	</div>
</div>


<!--CUSTOM FABS-->

<!--CHAPTER NAV-->
<div style="position:fixed;left: 20px;bottom: 20px;">
    <a href="#" id="chapter_nav_btn" data-activates="chapter_nav"
       class="btn-large pink btn-floating button-collapse"><i class="material-icons">note</i></a>
</div>

<!--BOOK NAV-->
<div style="position:fixed;right: 180px;bottom: 20px;">
    <a href="#" id="book_nav_btn" data-activates="book_nav"
       class="btn-large btn-floating blue button-collapse"><i class="material-icons">library_books</i></a>
</div>

<!--SIGN-->
<div style="position:fixed;right: 100px;bottom: 20px;">
    <a href="#sign" class="btn-floating btn-large "><i class="material-icons">power_settings_new</i></a>
</div>

<!--SYNCHRONIZE-->
<div style="position:fixed;right: 20px;bottom: 20px;">
    <a id="synchronize" class="btn-floating btn-large red"><i class="material-icons">sync</i></a>
</div>

<!--MODALS-->
<div id="chapter_new" class="modal">
    <div class="row" style="margin: 0">
        <div class="col l6 m6 s6 input-field">
            <label for="chapter_create_name">New Chapter</label>
            <input type="text" id="chapter_create_name">
        </div>
        <a href="#"
           id="chapter_create"
           style="margin: 10px"
           class="btn-floating btn-large green white-text">
            <i class="material-icons">send</i>
        </a>
    </div>
</div>

<div id="book_new" class="modal">
    <div class="row" style="margin: 0">
        <div class="col l6 m6 s6 input-field">
            <label for="book_create_name">New Book</label>
            <input type="text"
                   id="book_create_name">
        </div>
        <a href="#"
           id="book_create"
           style="margin: 10px"
           class="btn-floating btn-large green white-text">
            <i class="material-icons">send</i>
        </a>
    </div>
</div>

<div id="sign" class="modal">
    <div class="row" style="margin: 0">
        <div class="col l6 m6 s6 input-field">
            <label for="log_credential">Identify Yourself</label>
            <input type="text"
                   id="log_credential">
        </div>
        <a href="#"
           id="log"
           style="margin: 10px"
           class="btn-floating btn-large green white-text">
            <i class="material-icons">send</i>
        </a>
    </div>
</div>

</body>

</html>
