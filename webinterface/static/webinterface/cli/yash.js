var yaSH = {
    Config: {
        color: {
            default: 'chartreuse',
            pre: 'white',
            warning: 'yellow',
            error: 'red',
        },
    },
    _inputHId: undefined,
    _outputDivHId: undefined,
    _scrollDivHId: undefined,
    /**
     * binds terminal to HTML DOM elements
     * inputId      -> whose val can be taken as input
     * outputDivId  -> div where any output is printed
     * _scrollDivHId -> div is scrolled to bottom every time something gets printed in output div, typically parent of output div
     * */
    bind: function (inputId, outputDivId, scrollDivId) {
        this._inputHId = '#' + inputId;
        this._outputDivHId = '#' + outputDivId;
        this._scrollDivHId = '#' + scrollDivId;
        $(this._scrollDivHId).css({
            'overflow-y': 'auto'
        })
    },
    /**
     * Registers listeners, starts REPL
     * */
    initShell: function () {
        $(this._inputHId).keydown(function (e) {
            switch (e.keyCode || e.which) {
                // Enter
                case 13:
                    yaSH.evaluate();
                    break;
                // Tab Key
                case 9:
                    event.preventDefault();
                    yaSH.autoComplete();
                    break;
                // Up Arrow
                case 38:
                    // Going backward in history
                    yaSH.HistoryManager.goBackward();
                    break;
                // Down Arrow
                case 40:
                    // Going forward in history
                    yaSH.HistoryManager.goForward();
                    break;
                // Escape Key
                case 27:
                    // yaSH.evaluate("clear");
                    break;
                // None of the above
                default:
                    break;
            }
        });
    },
    /**
     * Builds html strings which can be directly appended to a DOM object
     * Can build one member per generation only
     * */
    _getHTML: function (tags, attributes, contents, closings) {
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
    },
    scrollToBottom: function () {
        $(this._scrollDivHId).scrollTop($(this._scrollDivHId).prop('scrollHeight'));
    },
    print: function (string, color) {
        color = color === undefined ? this.Config.color.default : color;
        $(this._outputDivHId).append(this._getHTML(
            ['span'],
            [{'style': 'color:' + color}],
            [string],
            [true]
        ));
        this.scrollToBottom();
    },
    println: function (string, color) {
        this.print(string, color);
        $(this._outputDivHId).append("<br>");
        this.scrollToBottom();
    },
    printPre: function (string, color) {
        color = color === undefined ? this.Config.color.pre : color;
        $(this._outputDivHId).append(this._getHTML(
            ['pre'],
            [
                {
                    'style': 'color:' + color + ';' +
                    'margin: 0px;'
                }
            ],
            [string],
            [true]
        ));
        this.scrollToBottom();
    },
    printWar: function (msg) {
        this.println(msg, this.Config.color.warning);
    },
    printErr: function (msg) {
        this.println(msg, this.Config.color.error);
    },

    _startPreloader: function () {
        $(this._inputHId).val('...');
    },
    _stopPreloader: function () {
        $(this._inputHId).val('');
    },

    _block: function () {
        $(this._inputHId).prop('readonly', true);
        this._startPreloader();
    },
    _unBlock: function () {
        this._stopPreloader();
        $(this._inputHId).prop('readonly', false);
        $(this._inputHId).focus();
    },

    /**
     * @return {boolean}
     * cmd found -> True
     * else      -> False
     * */
    evaluate: function () {
        var cmd = $(yaSH._inputHId).val();
        // Tokenize the cmd
        var spacedSepTokens = cmd.trim().replace(/ +/g, ' ');
        var tokens = spacedSepTokens.split(' ');
        // add this cmd to history
        this.HistoryManager._addCmd(spacedSepTokens);
        // the first token is the keyword
        var inputKeyword = tokens[0];
        // For all commands
        for (var i = 0; i < yaSH._cmdList.length; ++i) {
            var ithCmd = yaSH._cmdList[i];
            // If keyword of input matches some keywords of available cmd
            for (var j = 0; j < ithCmd.keywords.length; ++j) {
                var jthKeywordOfIthCmd = ithCmd.keywords[j];
                if (inputKeyword === jthKeywordOfIthCmd) {
                    // Print the command
                    this.println(yaSH._prefix + cmd);
                    // Pass the all tokens of input to that cmd exec method
                    ithCmd.exec(tokens);
                    // Empty the cmd line
                    $(this._inputHId).val("");
                    return true;
                }
            }
        }
        // Indicate cmd not available
        this.printErr(yaSH._prefix + cmd + ' : command not found');
        // Empty the cmd line
        $(this._inputHId).val("");
        return false;
    },
    /**
     * Tries to auto complete given a cmd and available cmds
     * Cannot auto complete args of cmd
     * no  match found          -> Nothing happens
     * one match found          -> Auto completes the command
     * multiple matches found   -> Displays all matching cmds
     * */
    autoComplete: function () {
        var cmd = $(yaSH._inputHId).val();
        var spacedSepTokens = cmd.trim().replace(/ +/g, ' ');
        var regex = new RegExp('^' + spacedSepTokens, 'i');
        var matches = [];
        // Get all available cmds matching regex
        for (var i = 0; i < yaSH._cmdList.length; ++i) {
            var ithCmd = yaSH._cmdList[i];
            for (var j = 0; j < ithCmd.keywords.length; ++j) {
                var jthKeywordOfIthCmd = ithCmd.keywords[j];
                // If the regex matches
                if (regex.test(jthKeywordOfIthCmd)) {
                    // Keep track of that keyword
                    matches.push(jthKeywordOfIthCmd);
                }
            }
        }

        /**
         * Returns the longest matching prefix among given param array
         * */
        var getBestAutoComplete = function (matches) {
            // Finds best auto complete
            var bestAutoComplete = '';
            if (matches.length === 0) {
                return bestAutoComplete;
            }

            var pos = 0;
            while (true) {
                var base = matches[0].charAt(pos);
                if (base === undefined)
                    return bestAutoComplete;
                for (var m = 1; m < matches.length; ++m) {
                    var mth = matches[m].charAt(pos);
                    // If this word ends
                    if (mth === undefined)
                        return bestAutoComplete;
                    // If this char does not match
                    if (mth !== base)
                        return bestAutoComplete;
                }
                bestAutoComplete += base;
                pos++;
            }
        };

        // If only one command auto complete
        if (matches.length === 1) {
            $(yaSH._inputHId).val(matches[0] + " ");
        }
        // If more display them on terminal
        else if (matches.length > 1) {
            var bestAutoComplete = getBestAutoComplete(matches);
            if (bestAutoComplete.length - cmd.length === 0) {
                // Show all options
                this.println(this._prefix + bestAutoComplete);
                var availableCommands = matches.join("\n");
                this.println(availableCommands, 'skyblue');
            } else {
                $(yaSH._inputHId).val(bestAutoComplete);
            }
        }
    },
    HistoryManager: {
        _history: [],
        _head: 0,
        goForward: function () {
            if (0 <= this._head + 1 && this._head + 1 <= this._history.length - 1) {
                this._head++;
                $(yaSH._inputHId).val(this._history[this._head]);
            }
            else if (this._head + 1 === this._history.length) {
                $(yaSH._inputHId).val("");
            }
        },
        goBackward: function () {
            if (0 <= this._head - 1 && this._head - 1 <= this._history.length - 1) {
                this._head--;
                $(yaSH._inputHId).val(this._history[this._head]);
            }
        },
        _addCmd: function (cmd) {
            // If the latest cmd is not "" & not the top one in _history
            if (cmd !== "" && this._history[this._history.length - 1] !== cmd) {
                // Stores cmd in _history
                this._history.push(cmd);
            }
            // Reset _head
            this._head = this._history.length;
        }
    },
    _prefix: '$: ',
    /**
     * Assert
     * All keywords are distinct
     * At least one keyword per cmd
     * desc should at least be empty string
     * exec method has to be implemented
     * */
    _cmdList: [
        {
            keywords: [""],
            desc: "do nothing",
            exec: function (arg) {
            }
        },
        {
            keywords: ["help", "guide"],
            desc: "shows commands and their descriptions",
            exec: function (tokens) {
                // Print
                for (var i = 0; i < yaSH._cmdList.length; i++) {
                    var ithCmd = yaSH._cmdList[i];
                    var tab = '  ------------  ';
                    var desc = ithCmd.keywords[0] + tab + ithCmd.desc;
                    yaSH.printPre(desc, 'white');
                    var keywords = ithCmd.keywords.splice(1).join("\n");
                    yaSH.printPre(keywords, 'white');
                }

            }
        },
        {
            keywords: ["about", "whatisthis"],
            desc: "brief description about school bag",
            exec: function (tokens) {
                yaSH.printPre('School Bag is a place where you keep all your knowledge', 'gold');
            }
        },
        {
            keywords: ["clear"],
            desc: "clears the terminal",
            exec: function (tokens) {
                $(yaSH._outputDivHId).empty();
            }
        },
    ]
};