/**
 * general AJAX request manager
 *
 * default :
 *      asynchronous = true
 *      request_method = POST
 *
 * Constraints:
 *      single ajax request at a time
 *
 * TODO: implement GET request method
 *
 * @param server address
 * */
function AJAXManager(server) {

    /* defaulting params */
    this.server = server == undefined ? "elephant.php" : server;

    /* settings */
    this.requestType = "POST";
    this.asyc = true;

    /* request properties and methods */
    this.request = null;

    this.open_and_send = function (queries, callbacks) {
        if (AJAXManager.requestRunning == true) {
            alert("An ajax request is already running !");
        }
        else {
            this.request = new XMLHttpRequest();
            this.request.onreadystatechange = function () {
                switch (this.readyState) {
                    // if response is ready
                    case 4:
                        // if OK
                        if (this.status == 200) {
                            AJAXManager.textResponse = this.responseText;
                            AJAXManager.xmlResponse = this.responseXML;
                            for (var i = 0; i < callbacks.length; ++i) {
                                callbacks[i]();
                            }
                        }
                        // if 404 error
                        else if (this.status == 404) {
                            alert("Server Unreachable");
                        }
                        AJAXManager.requestRunning = false;
                        break;
                    default:
                        break;
                }
            };
            this.request.open(this.requestType, this.server, this.asyc);
            this.request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
            /* preparing query string */
            var query_str_set = [];
            for (var key in queries) {
                if (queries.hasOwnProperty(key)) {
                    query_str_set.push(key + "=" + queries[key]);
                }
            }
            /* sending the request along with the queries */
            AJAXManager.requestRunning = true;
            this.request.send(query_str_set.join("&"));
        }
    };

}

/**
 * pk is assumed to be unique and constant for a given element
 *
 * Prototype "Context"
 * Usage:
 *      1.Assign xml doc into this.document choose a context (i.e. book_pk chapter_pk etc ...)
 *      2.Extract the information from document whenever necessary
 *      3.Whenever doc changes then repeat 1
 */
function Context(document) {
    /**
     * The entire xml document
     * */
    this.document = document;
    /**
     * By default
     *      the book and chapter are the first book and first chapter in the above book
     * if no book exists
     *      then both are undefined
     * if book exists but no chapters in the first book then
     *      book_pk is first book
     *      chapter_pk is undefined
     * */
    if (this.document != undefined) {
        if (this.document.getElementsByTagName('book').item(0) == null) {
            this.book_pk = undefined;
        } else {
            this.book_pk = Number(this.document.getElementsByTagName('book').item(0).getAttribute('pk'));
        }
        if (this.document.getElementsByTagName('chapter').item(0) == null) {
            this.chapter_pk = undefined;
        } else {
            this.chapter_pk = Number(this.document.getElementsByTagName('chapter').item(0).getAttribute('pk'));
        }
    }
    else {
        this.book_pk = undefined;
        this.chapter_pk = undefined;
    }
    /**
     * if(this.document == undefined)return undefined;
     * */
    this.max_book_pk = function () {
        var doc = this.document;
        if (doc == undefined)return undefined;
        var null_check = doc.getElementsByTagName('bag');
        return null_check == null ? undefined : Number(null_check.item(0).getAttribute('max_pk'));
    };
    /**
     * if this.document is undefined returns undefined
     * */
    this.book_list = function () {
        var doc = this.document;
        if (doc == undefined)return undefined;
        var null_check = doc.getElementsByTagName('book');
        return null_check == null ? undefined : null_check;
    };
    /**
     * if (this.book_pk == undefined) return undefined;
     * */
    this.book = function (book_pk) {
        this.book_pk = book_pk;
        if (this.book_pk == undefined) return undefined;
        var books = this.document.getElementsByTagName("book");
        if (books == null)return undefined;
        for (var i = 0; i < books.length; ++i) {
            if (books.item(i).getAttribute("pk") == this.book_pk)
                return books.item(i);
        }
        return undefined;
    };
    /**
     * if (this.document == undefined)return false
     * */
    this.book_create = function (book_name) {
        var doc = this.document;
        if (doc == undefined)return undefined;
        var bags = doc.getElementsByTagName('bag');
        if (bags == null)return false;
        var bag = bags.item(0);
        var max_book_pk = Number(bag.getAttribute('max_pk'));
        var book = this.document.createElement('book');
        book.setAttribute('pk', String(max_book_pk + 1));
        book.setAttribute('name', book_name);
        book.setAttribute('max_pk', "0");
        bag.appendChild(book);
        bag.setAttribute('max_pk', String(max_book_pk + 1));
        return true;
    };
    /**
     * return false if
     * no param
     * no books
     * no such book
     * */
    this.book_delete = function (book_pk) {
        if (book_pk == undefined)return false;
        var books = this.document.getElementsByTagName("book");
        if (books == null)return false;
        for (var i = 0; i < books.length; ++i) {
            if (books.item(i).getAttribute("pk") == book_pk) {
                var book = books.item(i);
                book.parentNode.removeChild(book);
                return true;
            }
        }
        return false;
    };
    /**
     * if (book == undefined)return undefined;
     * */
    this.book_name = function (book_pk) {
        var book = this.book(book_pk);
        if (book == undefined)return undefined;
        return book.getAttribute("name");
    };
    /**
     * if(this.book() == undefined)return undefined;
     * */
    this.max_chapter_pk = function (book_pk) {
        var book = this.book(book_pk);
        if (book == undefined)return undefined;
        return Number(book.getAttribute('max_pk'));
    };
    /**
     * if (book == undefined)return undefined;
     * */
    this.first_chapter = function (book_pk) {
        var book = this.book(book_pk);
        if (book == undefined)return undefined;
        var null_check = book.getElementsByTagName('chapter');
        return null_check == null ? undefined : null_check.item(0);
    };
    /**
     * if book_pk is undefined or no book exists with such pk returns undefined
     * if chapter_pk is undefined or no chapter exists with such pk returns undefined
     * */
    this.chapter = function (book_pk, chapter_pk) {
        this.book_pk = book_pk;
        this.chapter_pk = chapter_pk;
        if (this.book_pk == undefined || this.chapter_pk == undefined) {
            return undefined;
        }
        var book = this.book(book_pk);
        var chapters = book.getElementsByTagName("chapter");
        if (chapters == null)return undefined;
        for (var i = 0; i < chapters.length; ++i) {
            if (chapters.item(i).getAttribute("pk") == this.chapter_pk)
                return chapters.item(i);
        }
        return undefined;
    };
    /**
     * returns false if
     * param undefined
     * if (this.document == undefined)return false
     * no such book exists
     *
     * */
    this.chapter_create = function (chapter_name, book_pk) {
        if (chapter_name == undefined || book_pk == undefined)return false;
        if (this.document == undefined)return false;
        var book = this.book(book_pk);
        if (book == undefined)return false;
        var max_chapter_pk = Number(book.getAttribute('max_pk'));
        var chapter = this.document.createElement('chapter');
        chapter.setAttribute('pk', String(max_chapter_pk + 1));
        chapter.setAttribute('name', chapter_name);
        book.appendChild(chapter);
        book.setAttribute('max_pk', String(max_chapter_pk + 1));
        return true;
    };
    /**
     * return false if
     * no param
     * no such book
     * no chapters
     * no such chapter
     * */
    this.chapter_delete = function (book_pk, chapter_pk) {
        if (chapter_pk == undefined)return undefined;
        this.book_pk = book_pk;
        var book = this.book(book_pk);
        if (book == undefined)return false;
        var chapters = book.getElementsByTagName("chapter");
        if (chapters == null)return false;
        for (var i = 0; i < chapters.length; ++i) {
            if (chapters.item(i).getAttribute("pk") == chapter_pk) {
                var chapter = chapters.item(i);
                chapter.parentNode.removeChild(chapter);
                return true;
            }
        }
        return false;
    };
    /**
     * if book_pk is undefined or no book exists with such pk returns undefined
     * if chapter_pk is undefined or no chapter exists with such pk returns undefined
     * */
    this.chapter_name = function (book_pk, chapter_pk) {
        var chapter = this.chapter(book_pk, chapter_pk);
        if (chapter == undefined)return undefined;
        return chapter.getAttribute("name");
    };
    /**
     * if (chapter == undefined)return undefined;
     * */
    this.chapter_content = function (book_pk, chapter_pk) {
        var chapter = this.chapter(book_pk, chapter_pk);
        if (chapter == undefined)return undefined;
        return chapter.textContent;
    };
    /**
     * if (book == undefined)return undefined;
     * */
    this.chapter_list = function (book_pk) {
        var book = this.book(book_pk);
        if (book == undefined)return undefined;
        var null_check = book.getElementsByTagName('chapter');
        return null_check == null ? undefined : null_check;
    };

}