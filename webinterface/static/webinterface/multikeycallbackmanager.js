var _MultiKeyCallBackTree = [];
var MultiKeyCallBackManager = {
    _actionStack: undefined,
    _matchedPathStack: undefined,
    _pressedKeyStack: undefined,
    _reset: function () {
        this._actionStack = [_MultiKeyCallBackTree];
        this._matchedPathStack = [];
        this._pressedKeyStack = [];
    },
    _keyDown: function (e) {
        var eCode = e.keyCode || e.which;
        // Handles long key press
        if (eCode === this._pressedKeyStack[this._pressedKeyStack.length - 1]) {
            return;
        }
        if (this._matchedPathStack.length === this._pressedKeyStack.length) {
            var currentGen = this._actionStack[this._actionStack.length - 1];
            for (var i = 0; i < currentGen.length; i++) {
                var ithMember = currentGen[i];
                if (ithMember.code === eCode) {
                    this._matchedPathStack.push(ithMember.code);
                    this._actionStack.push(ithMember.action);
                    if (typeof(ithMember.action) === 'function') {
                        ithMember.action(e);
                    }
                }
            }
        }
        this._pressedKeyStack.push(eCode);
    },
    _keyUp: function (e) {
        if (this._matchedPathStack.length === this._pressedKeyStack.length) {
            this._matchedPathStack.pop();
            if (this._actionStack.length > 1) {
                this._actionStack.pop();
            }
        }
        this._pressedKeyStack.pop();
    },
    bind: function (tree) {
        tree = tree === undefined ? [] : tree;
        _MultiKeyCallBackTree = tree;
    },
    init: function () {
        this._reset();
        $(window).keydown(function (e) {
            MultiKeyCallBackManager._keyDown(e);
        });
        $(window).keyup(function (e) {
            MultiKeyCallBackManager._keyUp(e);
        });
        $(window).focus(function () {
            MultiKeyCallBackManager._reset();
        });
    }
};
