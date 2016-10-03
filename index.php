<?php
//session_start();
//?>
    <!DOCTYPE html>
    <html>
    <head>
        <!--LIB HEADERS-->
        <link href="https://fonts.googleapis.com/css?family=Ruslan+Display|Lobster|Special+Elite" rel="stylesheet">
        <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
        <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

        <!--TITLE INFO-->
        <title>School bag</title>
        <link rel="shortcut icon" href="logo.png">

        <!--JS INCLUDES-->
        <script src="AJAX.js"></script>

        <!--PAGE GLOBALS-->
        <script>
            var SCHOOL_BAG;

            var PRESENT_NOTEBOOK;
            var PRESENT_NOTEBOOK_TOPICS;
            var PRESENT_TOPIC;
        </script>

        <!--RELOADING PARTS OF PAGE-->
        <script>
            function reloadCourseName(present_notebook) {
                $("#NAME").val(present_notebook.getAttribute("course_name"));
            }

            function selectTopic(topic,link) {
                // FOR FUTURE REFERENCE
                PRESENT_TOPIC = topic;

                $("#TOPIC_NAME").val(topic.getAttribute("topic_name"));
                $("#TOPIC_CONTENT").val(topic.textContent);

                if(link != undefined){
                    // highlighting style
                    $(".topicLink").removeClass("highlightTopicLink");
                    $(link).addClass("highlightTopicLink");
                }
            }

            function reloadCourseTopicsAndAssignClickListeners(present_notebook_topics,topic) {
                // PREPARING THE TOPICS
                var _html_topic_name_string_ = "";

                var allTopics = present_notebook_topics;
                var noTopics = allTopics.length;

                // APPENDING THE TOPICS TO STRING
                for (var i = 0; i < noTopics; ++i) {
                    var topicName = allTopics.item(i).getAttribute("topic_name");
                    _html_topic_name_string_ += "<li><a class='topicLink' id='"+topicName+"'>" + topicName + "</a></li>";
                }
//                alert(_html_topic_name_string_);
                // ADDING HTML TO PAGE
                // AND CLICK LISTENERS TO TOPICS SO THAT , THE SELECTED TOPIC APPEARS ON READING SECTION
                $("#TOPICS").html(_html_topic_name_string_).find(".topicLink").click(function () {
                    var selectedTopicName = $(this).text();
                    var noTopics = present_notebook_topics.length;
                    for (var i = 0; i < noTopics; ++i) {
                        if (selectedTopicName == present_notebook_topics.item(i).getAttribute("topic_name")) {
                            selectTopic(present_notebook_topics.item(i),this);
                        }
                    }

                });

                selectTopic(topic)
            }

            function reloadLogistics(present_notebook) {
                $("#LOGISTICS").html(present_notebook.getElementsByTagName("logistics").item(0).textContent);
            }

            function reloadSubject(notebook, topic) {
                topic = topic == undefined ? notebook.getElementsByTagName('topic').item(0) : topic;
                reloadCourseName(notebook);
                reloadLogistics(notebook);
                reloadCourseTopicsAndAssignClickListeners(notebook.getElementsByTagName('topic'),topic);
            }

            function selectSubject(notebook,topic) {
                PRESENT_NOTEBOOK = notebook;
                PRESENT_NOTEBOOK_TOPICS = notebook.getElementsByTagName('topic');

                $(".subjectLink").removeClass("highlightSubjectLink");
                $("#navigation").find("#" + notebook.getAttribute('course_code')).addClass("highlightSubjectLink");

                reloadSubject(notebook, topic);
            }

            function reloadNavBarAndAssignClickListeners(school_bag,notebook,topic) {
                // CREATING LINKS TO ALL NOTEBOOKS
                var _nav_links_html_string_ = "";
                var subjects = school_bag.getElementsByTagName('subject');
                for (var i = 0; i < subjects.length; ++i) {
                    var subjectCode = subjects.item(i).getAttribute('course_code');
                    _nav_links_html_string_ += "<li><a class='subjectLink'  id='" + subjectCode + "'>";
                    _nav_links_html_string_ += subjectCode;
                    _nav_links_html_string_ += "</a></li>";
                }
                $("#navigation").find(".subjectLink").remove();
                $("#navigation").append(_nav_links_html_string_).find("a").click(function () {
                    var selectedSubject = $(this).attr("id");
                    var allSubjects = school_bag.getElementsByTagName("subject");
                    var noSubjects = allSubjects.length;

                    for (var i = 0; i < noSubjects; ++i) {
                        if (selectedSubject == allSubjects.item(i).getAttribute("course_code")) {
                            selectSubject(allSubjects.item(i));
                        }
                    }
                });

                selectSubject(notebook,topic);
            }

            function reloadSystem(school_bag,notebook,topic) {
                reloadNavBarAndAssignClickListeners(school_bag,notebook,topic);
            }

        </script>

        <!--INITIALIZATION AND REFRESH-->
        <script>
            // calls functions with a responseXML param
            function syncWithFileServer() {
                ajax_request(undefined, [], [reloadWithResponse], "NoteBooks.xml");
            }

            function reloadWithResponse(response) {

                var school_bag = response.responseXML;
                var all_note_books = school_bag.getElementsByTagName('subject');

                if (PRESENT_NOTEBOOK != undefined && PRESENT_TOPIC != undefined) {
//                    alert('all defined');
                    var notebook = false;
                    var topic = false;
                    for (var i = 0; i < all_note_books.length; ++i) {
                        if (all_note_books.item(i).getAttribute('course_code') == PRESENT_NOTEBOOK.getAttribute('course_code')) {
                            notebook = all_note_books.item(i);
                        }
                    }
                    if (notebook === false)notebook = all_note_books.item(0);
                    // notebook has present opened subject or the first one

                    var all_topics = notebook.getElementsByTagName('topic');
                    for (var i = 0; i < all_topics.length; ++i) {
                        if (all_topics.item(i).getAttribute('topic_name') == PRESENT_TOPIC.getAttribute('topic_name')) {
                            topic = all_topics.item(i);
                        }
                    }
                    if (topic === false)topic = all_topics.item(0);
                    //topic has the present topic or the first one
                }
                else {
                    notebook = all_note_books.item(0);
                    topic = notebook.getElementsByTagName('topic').item(0);
                }

//                console.log(all_note_books.item(0));
//                console.log(notebook);
//                console.log(topic);

                SCHOOL_BAG = school_bag;
//                PRESENT_NOTEBOOK = notebook;
//                PRESENT_NOTEBOOK_TOPICS = notebook.getElementsByTagName('topic');
                PRESENT_TOPIC = topic;

                reloadSystem(school_bag, notebook, topic);
            }
        </script>

        <!--INDICATIONS-->
        <script>
            function indicateChangeThroughAlert(response) {
                $("#server_messages").html(response.responseText).fadeIn().delay(5000).fadeOut();
            }
        </script>

        <!--MAIN FUNCTION-->
        <script>
            $(document).ready(function () {
                syncWithFileServer();
            })
        </script>

        <!--CSS INCLUDES-->
        <style>
            body{
                background-image: url("http://wptest.fxbenard.com/files/2012/12/space-rocket.png");
            }

            #server_messages{
                font-family: monospace;
                background-color:rgba(255, 255, 255, 0.6); 
                float: left;
                position:fixed;
                top: 7%;
                display: none;
                color: grey;
                font-size: 20px;
                padding: 10px;
                border-radius: 4px;
            }

            button:focus,
            input:focus {
                outline: none;
            }

            button:focus {
                outline: none;
            }

            .subjectLink:hover {
                cursor: pointer;
            }

            .topicLink{
                font-size: 17px;
                text-decoration: none;
                color: green;
            }

            .topicLink:hover {
                cursor: pointer;
                text-decoration: none;
                font-size: 19px;
                color: green;
            }

            textarea {
                overflow: scroll;
                min-height: 200px;
            }

            #navigation{
                background-color: rgba(255, 255, 255, 0.6);
            }

            #navigation button {
                margin-top: 7px;
            }

            #NAME {
                border: none;
                text-align: center;
                font-family: 'Ruslan Display', cursive;
                font-size: 40px;
                background-color: transparent;
                color: white;
            }

            #heading{
                margin-top:5px;
            }

            #logistics_heading{
                font-size: 20px;
                color: white;;
            }

            #edit_course_name_button, #edit_course_name_submit {
                margin-bottom: 20px;
            }

            #allAboutTopics{
                padding:10px;
                background-color: rgba(255, 255, 255, 0.6);
            }

            #allAboutTopics ul {
                list-style: lower-latin;
                font-family: 'Special Elite', cursive;
            }

            #TOPIC_NAME {
                border: none;
                color: steelblue;
                font-size: 30px;
                font-family: 'Lobster', cursive;
                background-color: transparent;
                color: white;
            }

            #TOPIC_CONTENT {
                min-height: 700px;
                color: black;
                font-size: 20px;
                font-family: 'Special Elite', cursive;
                background-color: rgba(255, 255, 255, 0.49);
                overflow: auto;
            }

            #LOGISTICS {
                overflow: auto;
                background-color: rgba(255, 255, 255, 0.70);
                font-family: 'Special Elite', cursive;
            }

            .highlightSubjectLink {
                background-color: steelblue;
                color: white;
            }

            .highlightTopicLink {
                background-color: lightsalmon;
                border-radius: 5px;
                padding: 2px;
            }

        </style>

    </head>

    <body>

    <nav>
        <div class="container-fluid">
            <ul class="nav nav-tabs " id="navigation">
                <li class="navbar-brand" style="background-color: steelblue;color: white;margin-right: 10px;font-family:'Ruslan Display',cursive;">Your Books</li>
                <!--NOTEBOOK LINKS-->
                <button type="button" class="btn btn-default btn-sm" data-toggle="modal" data-target="#create-form-div">
                    +
                </button>
                <button type="button" class="btn btn-danger btn-sm" data-toggle="modal" data-target="#delete-form-div">-
                </button>
            </ul>
        </div>
    </nav>

    <div class="container-fluid">

        <span id="server_messages"></span>

        <!--NOTEBOOK NAME-->
        <div class="row" >
            <span class="col-xs-12" id="heading" style="text-align: center">
                <input id="NAME" type="text" style="width: 60%;"
                       class="editable" readonly>
                <button type="button" id="edit_course_name_button"
                        class="btn btn-warning btn-sm glyphicon glyphicon-pencil"></button>
                <button type="button" id="edit_course_name_submit"
                        class="btn btn-success btn-sm glyphicon glyphicon-refresh"
                        style="display: none"></button>

                <script>
                    $(document).ready(function () {

                        var editing = false;
                        $("#edit_course_name_button").click(function () {
                            if (!editing) {
                                // CHANGING THE ICONS
                                $(this).toggleClass("glyphicon-pencil");
                                $(this).toggleClass("glyphicon-eye-open");
                                editing = true;
                                $("#NAME").attr("readonly", false).keyup(function () {
                                    if ($(this).val() != PRESENT_NOTEBOOK.getAttribute("course_name")) {
                                        $("#edit_course_name_submit").fadeIn();
                                    } else {
                                        $("#edit_course_name_submit").fadeOut();
                                    }
                                });
                            } else {
                                // CHANGING THE ICONS
                                $(this).toggleClass("glyphicon-pencil");
                                $(this).toggleClass("glyphicon-eye-open");
                                editing = false;
                                $("#NAME").attr("readonly", true).val(PRESENT_NOTEBOOK.getAttribute("course_name"));
                                $("#edit_course_name_submit").fadeOut();
                            }
                        });

                        $("#edit_course_name_submit").click(function () {
                            var course_code = PRESENT_NOTEBOOK.getAttribute("course_code");
                            var new_course_name = $("#NAME").val();
                            ajax_request("editCourseName", ["course_code", course_code, "course_name", new_course_name], [indicateChangeThroughAlert, syncWithFileServer]);
                        });
                    });
                </script>
            </span>
        </div>
        <!--NOTEBOOK NAME-->

        <hr style="margin:0;">

        <!--MIDDLE REGION-->
        <div style="margin-top: 10px" class="middle row">

            <div class="col-xs-2" id="allAboutTopics">

                <!--ADD TOPIC-->
                <div>
                    <h4 id="note" style="display: none;">another topic exists with same name</h4>
                    <input type="button" value="+" class="btn btn-success" id="add_topic_button">

                    <div id="addTopicToggle" style="display: none">
                        <input id="newTopicName" type="text" name="topic_name" placeholder="Topics' name">
                        <input type="button" id="add_topic_submit" class="btn btn-success" value="Go">
                    </div>

                    <script>
                        $("#add_topic_button").click(function () {
                            $("#addTopicToggle").fadeToggle();
                        });

                        $("#add_topic_submit").click(function () {
                            var addTopic = true;
                            var newTopicName = $("#newTopicName").val();
                            var noTopics = PRESENT_NOTEBOOK_TOPICS.length;

                            for (var j = 0; j < noTopics; ++j) {
                                if (newTopicName == PRESENT_NOTEBOOK_TOPICS.item(j).getAttribute("topic_name")) {
                                    addTopic = false;
                                    break;
                                }
                            }
                            if (addTopic) {
                                var course_code = PRESENT_NOTEBOOK.getAttribute("course_code");
                                var new_topic_name = $("#newTopicName").val();
                                ajax_request("createTopic", ["course_code", course_code, "topic_name", new_topic_name], [indicateChangeThroughAlert, syncWithFileServer]);
                            }
                            else {
                                $("#add_topic_form").find("#note").fadeIn().delay(2000).fadeOut();
                            }
                        });
                    </script>
                </div>
                <!--ADD TOPIC-->

                <ul id="TOPICS">
                    <!--TOPICS-->
                    T O P I C S
                </ul>

                <!--REMOVE TOPIC-->
                <div>
                    <h4 id="note" style="display: none;">No such topic</h4>
                    <h4 id="note2" style="display: none;">At least one topic would be nice</h4>
                    <input type="button" value="-" class="btn btn-danger" id="remove_topic_button">

                    <div id="removeTopicToggle" style="display: none">
                        <input id="oldTopicName" type="text" name="topic_name" placeholder="Topics' name">
                        <input type="button" id="remove_topic_submit" class="btn btn-danger" value="Go">
                    </div>

                    <script>
                        $("#remove_topic_button").click(function () {
                            if (PRESENT_NOTEBOOK_TOPICS.length > 1) {
                                $("#removeTopicToggle").fadeToggle();
                            } else {
                                $("#remove_topic_form").find("#note2").fadeIn().delay(2000).fadeOut();
                            }
                        });

                        $("#remove_topic_submit").click(function () {
                            var removeTopic = false;
                            var oldTopicName = $("#oldTopicName").val();
                            var noTopics = PRESENT_NOTEBOOK_TOPICS.length;
                            var course_code_t = PRESENT_NOTEBOOK.getAttribute("course_code");

                            for (var j = 0; j < noTopics; ++j) {
                                if (oldTopicName == PRESENT_NOTEBOOK_TOPICS.item(j).getAttribute("topic_name")) {
                                    removeTopic = true;
                                    break;
                                }
                            }

                            if (removeTopic) {
                                var course_code = PRESENT_NOTEBOOK.getAttribute("course_code");
                                var old_topic_name = $("#oldTopicName").val();
                                ajax_request("deleteTopic", ["course_code", course_code, "topic_name", old_topic_name], [indicateChangeThroughAlert, syncWithFileServer]);

                            } else {
                                $("#remove_topic_form").find("#note").fadeIn().delay(2000).fadeOut();
                            }
                        });
                    </script>
                </div>
                <!--REMOVE TOPIC-->

            </div>

            <div class="col-xs-8">

                <!--TOPIC INSIGHTS-->
                <div>
                    <h4 id="note" style="display: none;">This topic already exists</h4>
                    <button id="edit_topic_submit" type="button" class="btn btn-success glyphicon glyphicon-floppy-open"
                            style="display: none"></button>
                    <blockquote>
                        <input id="TOPIC_NAME" name="topic_name" value="Fill your" style="min-width: 90%" type="text"
                               class="editable" readonly>
                        <button type="button" class="btn btn-default glyphicon glyphicon-user"
                                id="edit_view_topic_toggle"></button>
                    </blockquote>
                    <textarea id="TOPIC_CONTENT" name="topic_content" class="form-control editable"
                              readonly>T h o u g h t s</textarea>
                    <script>
                        var topicActive = false;

                        // clicking the button or double clicking the area will toggle activeness
                        $("#edit_view_topic_toggle").click(function () {
                            // TOPIC IS INACTIVE
                            if (topicActive) {
                                $("#TOPIC_NAME").attr("readonly", true);
                                $("#TOPIC_CONTENT").attr("readonly", true);

                                topicActive = false;
                            }
                            // TOPIC IS ACTIVE
                            else {
                                var TOPIC_NAME_INPUT = $("#TOPIC_NAME");
                                var TOPIC_CONTENT_INPUT = $("#TOPIC_CONTENT");

                                $(TOPIC_NAME_INPUT).attr("readonly", false);
                                $(TOPIC_CONTENT_INPUT).attr("readonly", false);

                                var prevTopicName = PRESENT_TOPIC.getAttribute("topic_name");
                                var prevTopicContent = PRESENT_TOPIC.textContent;
                                // GETTING ALL REQUIRED DATA
                                var duplicateTopic;
                                // SETTING LISTENER FOR TOPIC_NAME_INPUT
                                $(TOPIC_NAME_INPUT).keyup(function () {
                                    if ($(TOPIC_NAME_INPUT).val() != prevTopicName || $(TOPIC_CONTENT_INPUT).val() != prevTopicContent) {

                                        // TOPIC_NAME_INPUT cannot be the 'prevTopicName'
                                        duplicateTopic = false;
                                        var noTopic = PRESENT_NOTEBOOK_TOPICS.length;
                                        for (var i = 0; i < noTopic; ++i) {
                                            if (PRESENT_NOTEBOOK_TOPICS.item(i).getAttribute("topic_name") == $(TOPIC_NAME_INPUT).val()) {
                                                duplicateTopic = true;
                                                break;
                                            }
                                        }

                                        // 'duplicateTopic' value is determined
                                        if (duplicateTopic) {
                                            $("#edit_topic_form").find("#note").fadeIn();
                                            $("#edit_topic_submit").fadeOut();
                                        }
                                        else {
                                            $("#edit_topic_form").find("#note").fadeOut();
                                            $("#edit_topic_submit").fadeIn();
                                        }
                                    }
                                    else {
                                        // NO CHANGE SO WASTE TO SUBMIT
                                        $("#edit_topic_submit").fadeOut();
                                    }
                                });

                                // SETTING LISTENER FOR TOPIC_CONTENT_INPUT
                                $(TOPIC_CONTENT_INPUT).keyup(function () {
                                    if (($(TOPIC_NAME_INPUT).val() != prevTopicName || $(TOPIC_CONTENT_INPUT).val() != prevTopicContent) && !duplicateTopic) {
                                        $("#edit_topic_submit").fadeIn();
                                    } else {
                                        $("#edit_topic_submit").fadeOut();
                                    }
                                });

                                topicActive = true;
                            }
                        });

                        $("#TOPIC_NAME , #TOPIC_CONTENT").dblclick(function () {
                            // TOPIC IS INACTIVE
                            if (topicActive) {
                                $("#TOPIC_NAME").attr("readonly", true);
                                $("#TOPIC_CONTENT").attr("readonly", true);

                                topicActive = false;
                            }
                            // TOPIC IS ACTIVE
                            else {
                                var TOPIC_NAME_INPUT = $("#TOPIC_NAME");
                                var TOPIC_CONTENT_INPUT = $("#TOPIC_CONTENT");

                                $(TOPIC_NAME_INPUT).attr("readonly", false);
                                $(TOPIC_CONTENT_INPUT).attr("readonly", false);

                                var prevTopicName = PRESENT_TOPIC.getAttribute("topic_name");
                                var prevTopicContent = PRESENT_TOPIC.textContent;
                                // GETTING ALL REQUIRED DATA
                                var duplicateTopic;
                                // SETTING LISTENER FOR TOPIC_NAME_INPUT
                                $(TOPIC_NAME_INPUT).keyup(function () {
                                    if ($(TOPIC_NAME_INPUT).val() != prevTopicName || $(TOPIC_CONTENT_INPUT).val() != prevTopicContent) {

                                        // TOPIC_NAME_INPUT cannot be the 'prevTopicName'
                                        duplicateTopic = false;
                                        var noTopic = PRESENT_NOTEBOOK_TOPICS.length;
                                        for (var i = 0; i < noTopic; ++i) {
                                            if (PRESENT_NOTEBOOK_TOPICS.item(i).getAttribute("topic_name") == $(TOPIC_NAME_INPUT).val()) {
                                                duplicateTopic = true;
                                                break;
                                            }
                                        }

                                        // 'duplicateTopic' value is determined
                                        if (duplicateTopic) {
                                            $("#edit_topic_form").find("#note").fadeIn();
                                            $("#edit_topic_submit").fadeOut();
                                        }
                                        else {
                                            $("#edit_topic_form").find("#note").fadeOut();
                                            $("#edit_topic_submit").fadeIn();
                                        }
                                    }
                                    else {
                                        // NO CHANGE SO WASTE TO SUBMIT
                                        $("#edit_topic_submit").fadeOut();
                                    }
                                });

                                // SETTING LISTENER FOR TOPIC_CONTENT_INPUT
                                $(TOPIC_CONTENT_INPUT).keyup(function () {
                                    if (($(TOPIC_NAME_INPUT).val() != prevTopicName || $(TOPIC_CONTENT_INPUT).val() != prevTopicContent) && !duplicateTopic) {
                                        $("#edit_topic_submit").fadeIn();
                                    } else {
                                        $("#edit_topic_submit").fadeOut();
                                    }
                                });

                                topicActive = true;
                            }
                        });

                        $("#edit_topic_submit").click(function () {
                            var course_code = PRESENT_NOTEBOOK.getAttribute("course_code");
                            var ref_topic_name = PRESENT_TOPIC.getAttribute("topic_name");
                            var topic_name = $("#TOPIC_NAME").val();
                            var topic_content = $("#TOPIC_CONTENT").val();
                            ajax_request("editTopic", ["course_code", course_code, "ref_topic_name", ref_topic_name, "topic_name", topic_name, "topic_content", topic_content], [indicateChangeThroughAlert, syncWithFileServer])
                        });

                    </script>
                </div>
                <!--TOPIC INSIGHTS-->

            </div>

            <div class="col-xs-2">

                <!--LOGISTICS-->
                <span id="logistics_heading">More about this ...</span>
                <hr>
                <div>
                    <textarea id="LOGISTICS" class="form-control" readonly></textarea>
                    <button id="edit_logistics_submit" type="button"
                            class="btn btn-success glyphicon glyphicon-floppy-open" style="display: none"></button>
                    <script>
                        var logisticsActive = false;

                        $("#LOGISTICS").dblclick(function () {
                            if (logisticsActive) {
                                $(this).attr("readonly", true);
                                logisticsActive = false;
                            }
                            else {
                                $(this).attr("readonly", false).keyup(function () {
                                    var prevLogistics = PRESENT_NOTEBOOK.getElementsByTagName("logistics").item(0).textContent;
                                    if ($(this).val() != prevLogistics) {
                                        $("#edit_logistics_submit").fadeIn();
                                    } else {
                                        $("#edit_logistics_submit").fadeOut();
                                    }
                                });
                                logisticsActive = true;
                            }
                        });

                        $("#edit_logistics_submit").click(function () {
                            var course_code = PRESENT_NOTEBOOK.getAttribute("course_code");
                            var new_logistics = $("#LOGISTICS").val();
                            ajax_request("editLogistics", ["course_code", course_code, "logistics", new_logistics], [indicateChangeThroughAlert, syncWithFileServer]);
                        });
                    </script>
                </div>
                <!--LOGISTICS-->

            </div>
        </div>
        <!--MIDDLE REGION-->


        <!--MODAL DEFINITIONS-->
    </div>

    <!--NEW-NOTEBOOK-->
    <div id="create-form-div" class="modal fade" role="dialog">
        <div class="modal-dialog">

            <div class="modal-content">
                <div class="modal-header">

                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                </div>
                <div class="modal-body">
                    <div>
                        <input type="text" autofocus id="create_subject_course_name" placeholder="COURSE NAME">
                        <input type="text" placeholder="COURSE CODE" id="create_subject_course_code">
                        <input type="button" id="create_subject_submit" value="Get Started" class="btn btn-default">
                        <script>
                            $(document).ready(function () {
                                var createConfirm = false;
                                $("#create_subject_course_code").keyup(function () {
                                    var givenCourseCodeInput = $(this).val();
                                    var subjects = SCHOOL_BAG.getElementsByTagName("subject");
                                    var noSubjects = subjects.length;
                                    for (var i = 0; i < noSubjects; ++i) {
                                        if (subjects.item(i).getAttribute("course_code") == givenCourseCodeInput) {
                                            $("#create_subject_warning").fadeIn();
                                            $("#create_subject_submit").fadeOut();
                                            createConfirm = false;
                                            break;
                                        }
                                        else {
                                            $("#create_subject_warning").fadeOut();
                                            $("#create_subject_submit").fadeIn();
                                            createConfirm = true;
                                        }
                                    }
                                });
                                $("#create_subject_submit").click(function () {
                                    if (createConfirm) {
                                        var course_code = $("#create_subject_course_code").val();
                                        var course_name = $("#create_subject_course_name").val();
                                        ajax_request("createSubject", ["course_code", course_code, "course_name", course_name], [indicateChangeThroughAlert, syncWithFileServer]);
                                    }

                                });
                            });
                        </script>
                    </div>
                </div>
                <div class="modal-footer">
                <span id="create_subject_warning"
                      style="display: none">You already have a notebook with same code</span>
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>

        </div>
    </div>
    <!--NEW-NOTEBOOK-->

    <!--REMOVE-NOTEBOOK-->
    <div id="delete-form-div" class="modal fade" role="dialog">
        <div class="modal-dialog">
            <div class="modal-content">
                <div class="modal-header">
                    <button type="button" class="close" data-dismiss="modal">&times;</button>
                    <h4>Are you sure ?</h4>
                    <h5>This removes all the notes you have prepared for this subject</h5>
                    <h5 style="display:none;" id="note">Please do not remove this notebook
                        <br>Old memories are always refreshing.</h5>
                </div>
                <div class="modal-body">
                    <div>
                        <input type="text" style="min-width: 50%" name="course_code" id="course_code_key"
                               placeholder="Type in this courses' code to Confirm" autofocus>
                        <span id="del_subject_warning" class="glyphicon glyphicon-alert" style="display: none"></span>
                        <input type="button" id="del_subject_submit" style="display: none;" value="Remove This Subject"
                               class="btn btn-default">
                        <script>
                            $(document).ready(function () {
                                var delConfirm = false;
                                $("#course_code_key").keyup(function () {

                                    if ($(this).val() == "EX101" && PRESENT_NOTEBOOK.getAttribute("course_code") == "EX101") {
                                        $("#delete-form-div").find("#note").fadeIn();
                                        delConfirm = false;
                                    }
                                    else if ($(this).val() == PRESENT_NOTEBOOK.getAttribute("course_code")) {
                                        $("#del_subject_warning").fadeIn();
                                        $("#del_subject_submit").fadeIn();
                                        $("#delete-form-div").find("#note").fadeOut();
                                        delConfirm = true;
                                    }
                                    else {
                                        $("#del_subject_warning").fadeOut();
                                        $("#del_subject_submit").fadeOut();
                                        $("#delete-form-div").find("#note").fadeOut();
                                        delConfirm = false;
                                    }

                                });

                                $("#del_subject_submit").click(function () {
                                    if (delConfirm) {
                                        var course_code = PRESENT_NOTEBOOK.getAttribute("course_code");
                                        ajax_request("deleteSubject", ["course_code", course_code], [indicateChangeThroughAlert, syncWithFileServer]);
                                    }
                                });
                            });
                        </script>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
                </div>
            </div>

        </div>
    </div>
    <!--REMOVE-NOTEBOOK-->

    </body>

    </html>

<?php
//        function initializePage(selectedNoteBook) {
//            alert("infunc");
//            $("#NAME").text(selectedNoteBook.course_name);
//            var TOPICS_HTML = "<li><a>LOGISTICS</a></li>";
//            for(var i=0;i<selectedNoteBook.noTopics;++i){
//                TOPICS_HTML += "<li><a id='" + i + "'>" + selectedNoteBook.topic_name[i] + "</a></li>";
//            }
//            $("#TOPICS").html(TOPICS_HTML);
//            alert(selectedNoteBook.course_code);
//            $("#course_code_del_lock").val(selectedNoteBook.course_code);
//
//        }
//
//        $(document).ready(function () {
//
//            var selectedNoteBook;
//
//            $(".subjectLink").click(function () {
//                //GETTING ALL THE INFO OF THE SELECTED NOTEBOOK IN THIS OBJECT
//                selectedNoteBook = new __selectedNoteBook($(this).attr("id"));
//                initializePage(selectedNoteBook)
//            });
//
//        });

?>


<?php
// INITIALIZING WITH FIRST NOTE BOOK OR USING SESSIONS
//if (isset($_SESSION["course_code_SES"])) {
//    $noCourseWithThatCode = true;
//    $noteBooks = new DOMDocument();
//    $noteBooks->load("NoteBooks.xml");
//    $subjects = $noteBooks->getElementsByTagName("subject");
//    $noSubjects = $subjects->length;
//
//    for ($i = 0; $i < $noSubjects; ++$i) {
//        if ($subjects->item($i)->getAttribute("course_code") == $_SESSION["course_code_SES"]) {
//            echo "PRESENT_NOTEBOOK = SCHOOL_BAG.getElementsByTagName('subject').item($i);";
//            $noCourseWithThatCode = false;
//            break;
//        }
//    }
//    if ($noCourseWithThatCode) echo "PRESENT_NOTEBOOK = SCHOOL_BAG.getElementsByTagName('subject').item(0);";
//} else {
//    echo "PRESENT_NOTEBOOK = SCHOOL_BAG.getElementsByTagName('subject').item(0);";
//}
//?>


<?php
// INITIALIZING WITH THE THE FIRST TOPIC OF THE FIRST COURSE BY DEFAULT OR USING SESSION
//if (isset($_SESSION["topic_name_SES"])) {
//    $noTopicWithThatName = true;
//    $noteBooks = new DOMDocument();
//    $noteBooks->load("NoteBooks.xml");
//    $subjects = $noteBooks->getElementsByTagName("subject");
//    $noSubjects = $subjects->length;
//
//    for ($i = 0; $i < $noSubjects; ++$i) {
//        if ($subjects->item($i)->getAttribute("course_code") == $_SESSION["course_code_SES"]) {
//            $topics = $subjects->item($i)->getElementsByTagName('topic');
//            $noTopics = $topics->length;
//
//            for ($j = 0; $j < $noTopics; ++$j) {
//                if ($topics->item($j)->getAttribute("topic_name") == $_SESSION["topic_name_SES"]) {
//                    echo "PRESENT_TOPIC = PRESENT_NOTEBOOK_TOPICS.item($j);";
//                    $noTopicWithThatName = false;
//                    break;
//                }
//            }
//            break;
//        }
//    }
//    if ($noTopicWithThatName) echo "";
//} else {
//    echo "PRESENT_TOPIC = PRESENT_NOTEBOOK_TOPICS.item(0);";
//}
//?>


<?php
//            $noteBooks = new DOMDocument();
//            $noteBooks->load("NoteBooks.xml");
//            $subjects = $noteBooks->getElementsByTagName("subject");
//            foreach ($subjects as $subject) {
//                $subjectCode = $subject->getAttribute("course_code");
//                echo "<li><a class='subjectLink'  id='$subjectCode'>";
//                echo $subjectCode;
//                echo "</a></li>";
//            }
?>