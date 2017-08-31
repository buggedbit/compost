/**
 * @requires jQuery
 * */
var yaSH = {
    Config: {
        color: {
            default: '#64dd17',
            pre: 'white',
            warning: 'yellow',
            error: 'red',
        },
    },
    _inputHId: undefined,
    _outputDivHId: undefined,
    _scrollDivHId: undefined,
    _prefixSpanHId: undefined,

    /**
     * binds terminal to HTML DOM elements
     * inputId      -> whose val can be taken as input
     * outputDivId  -> div where any output is printed
     * _scrollDivHId -> div is scrolled to bottom every time something gets printed in output div, typically parent of output div
     * */
    bind: function (inputId, outputDivId, scrollDivId, prefixSpanId) {
        this._inputHId = '#' + inputId;
        this._outputDivHId = '#' + outputDivId;
        this._scrollDivHId = '#' + scrollDivId;
        $(this._scrollDivHId).css({
            'overflow-y': 'auto'
        });
        this._prefixSpanHId = '#' + prefixSpanId;
        this.setPrefix(this._prefix);
    },

    /**
     * Registers listeners, starts REPL
     * */
    initShell: function () {
        $(this._inputHId).keydown(function (e) {
            var cmd;
            switch (e.keyCode || e.which) {
                // Enter
                case 13:
                    cmd = $(yaSH._inputHId).val();
                    yaSH.evaluate(cmd);
                    break;
                // Tab Key
                case 9:
                    event.preventDefault();
                    cmd = $(yaSH._inputHId).val();
                    // array with length >= 1
                    var tokens = cmd.trim().replace(/ +/g, ' ').split(' ');
                    var wordToAutocomplete = tokens[tokens.length - 1];
                    var autocompleteBucket;
                    // autocomplete from keywords
                    if (tokens.length === 1) {
                        autocompleteBucket = [];
                        for (var i = 0; i < yaSH._cmdList.length; ++i) {
                            var ithCmd = yaSH._cmdList[i];
                            for (var j = 0; j < ithCmd.keywords.length; ++j) {
                                var jthKeywordOfIthCmd = ithCmd.keywords[j];
                                autocompleteBucket.push(jthKeywordOfIthCmd);
                            }
                        }
                    }
                    // autocomplete from others
                    else {
                        autocompleteBucket = yaSH.secondaryAutoCompleteBucket;
                    }
                    var autoComplete = yaSH.autoComplete(wordToAutocomplete, autocompleteBucket);

                    // If only one command auto complete
                    if (autoComplete.matches.length === 1) {
                        tokens.pop();
                        tokens.push(autoComplete.matches[0] + ' ');
                        $(yaSH._inputHId).val(tokens.join(' '));
                    }
                    // If more display them on terminal
                    else if (autoComplete.matches.length > 1) {
                        // Cmd typed is best match
                        if (autoComplete.best.length - wordToAutocomplete.length === 0) {
                            // Show all available options
                            yaSH.println(yaSH._prefix + autoComplete.best);
                            var availableCommands = autoComplete.matches.join('\n');
                            yaSH.println(availableCommands, 'skyblue');
                        }
                        // Else
                        else {
                            // Auto complete to the best match
                            tokens.pop();
                            tokens.push(autoComplete.best);
                            $(yaSH._inputHId).val(tokens.join(' '));
                        }
                    }
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

    _block: function () {
        $(this._inputHId).prop('readonly', true).hide();
    },
    _unBlock: function () {
        $(this._inputHId).prop('readonly', false).show();
        $(this._inputHId).focus();
    },

    /**
     * @return {boolean}
     * cmd found -> True
     * else      -> False
     * */
    evaluate: function (cmd) {
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
                    // Empty the cmd line
                    $(this._inputHId).val("");
                    // Block the control
                    yaSH._block();
                    // Pass the all tokens of input to that cmd exec method
                    ithCmd.exec(tokens);
                    // unBlock the control
                    yaSH._unBlock();
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
    secondaryAutoCompleteBucket: [],
    /**
     * Tries to auto complete given a wordToAutocomplete and autocompleteBucket
     * no  match found  -> returns {best: '', matches: []}
     * matches found    -> returns {best: <best match>, matches: <array of matches>}
     * */
    autoComplete: function (wordToAutocomplete, autocompleteBucket) {
        var regex = new RegExp('^' + wordToAutocomplete);
        var matches = [];

        for (var i = 0; i < autocompleteBucket.length; ++i) {
            var ithWholeWord = autocompleteBucket[i];
            if (regex.test(ithWholeWord)) {
                matches.push(ithWholeWord);
            }
        }

        /**
         * Returns the longest common prefix of given array
         * */
        var getLongestCommonPrefix = function (matches) {
            // Finds best auto complete
            var bestAutoComplete = '';
            if (matches.length === 0) {
                return bestAutoComplete;
            } else if (matches.length === 1) {
                return matches[0];
            }

            var pos = 0;
            while (true) {
                var base = matches[0].charAt(pos);
                if (base === undefined)
                    return bestAutoComplete;
                for (var m = 1; m < matches.length; ++m) {
                    var mth = matches[m].charAt(pos);
                    // If this wordToAutocomplete ends
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

        var bestAutoComplete = getLongestCommonPrefix(matches);
        return {best: bestAutoComplete, matches: matches};
    },

    HistoryManager: {
        _history: [],
        _head: -1,
        goForward: function () {
            // head is not at end
            if (this._head < this._history.length - 1) {
                this._head++;
                $(yaSH._inputHId).val(this._history[this._head]);
            }
            // head is at the end
            else if (this._head === this._history.length - 1) {
                $(yaSH._inputHId).val("");
            }
        },
        goBackward: function () {
            // head is not at the start
            if (0 < this._head) {
                $(yaSH._inputHId).val(this._history[this._head]);
                this._head--;
            } else if (this._head === 0) {
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
            this._head = this._history.length - 1;
        }
    },

    _prefix: '$: ',
    setPrefix: function (prefix) {
        this._prefix = prefix;
        $(this._prefixSpanHId).html(this._prefix);
    },
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
                    if (ithCmd.keywords[0] === "")
                        continue;
                    var tab = '----------------';
                    var keywords = ithCmd.keywords.join(",  ");
                    yaSH.printPre(keywords, 'deeppink');
                    yaSH.printPre(tab + ithCmd.desc, 'white');
                }
            }
        },
        {
            keywords: ["clear"],
            desc: "clears the terminal",
            exec: function (tokens) {
                $(yaSH._outputDivHId).empty();
            }
        },
    ],
    /**
     * Adds cmd to _cmdList
     * User should ensure format and constraint rules of the param cmd
     * */
    addCmd: function (cmd) {
        this._cmdList.push(cmd);
    },
    /**
     * In a generation
     * 1. all codes must be unique
     * 2. all codes must indicate a proper key
     * 3. all actions must either be another generation or a function
     *
     * In a path from root to leaf
     * 1. all codes must be unique
     * */
};