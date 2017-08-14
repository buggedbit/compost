var Editor = {
    _isInFocus: false,
    _containerDivHId: undefined,
    _bookNameInputHId: undefined,
    _pageNameInputHId: undefined,
    _textareaHId: undefined,
    _inReadMode: true,
    _currentBookName: undefined,
    _currentPageName: undefined,
    bind: function (containerId, bookNameInputId, pageNameInputId, textareaId) {
        this._containerDivHId = '#' + containerId;
        this._bookNameInputHId = '#' + bookNameInputId;
        this._pageNameInputHId = '#' + pageNameInputId;
        this._textareaHId = '#' + textareaId;
    },
    open: function (book, page, text, inReadMode) {
        if (book === undefined ||
            page === undefined ||
            text === undefined)
            return;

        inReadMode = inReadMode === undefined ? true : inReadMode;
        this._currentBookName = book;
        $(this._bookNameInputHId).html(book);
        this._currentPageName = page;
        $(this._pageNameInputHId).html(page);
        $(this._textareaHId).val(text);

        this._inReadMode = inReadMode;
        if (this._inReadMode) {
            $(this._textareaHId).prop('readonly', true);
        } else {
            $(this._textareaHId).prop('readonly', false);
            $(this._textareaHId).focus();
        }

        $(this._containerDivHId).show();
        this._isInFocus = true;
    },
    _save: function () {
        if (this._isInFocus && this.oSave !== undefined && typeof(this.oSave === 'function'))
            this.oSave()
    },
    close: function () {
        this._isInFocus = false;
        $(this._containerDivHId).hide();
    }
};