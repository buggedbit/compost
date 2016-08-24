<?php
    session_start();
?>
<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

    <title>SchoolBag</title>
    <script src="index.js"></script>

    <script>
        var MY_NOTEBOOK;
        var MY_NOTEBOOK_TOPICS;
        var NOTEBOOKS_FILE;
        var PRESENT_TOPIC;

        function loadThisNoteBook() {
            $("#NAME").val(MY_NOTEBOOK.getAttribute("course_name"));
            // PREPARING THE TOPICS
            var _html_topic_name_string_ = "";
            var allTopics = MY_NOTEBOOK_TOPICS;
            var noTopics = allTopics.length;
            for (var i = 0; i < noTopics; ++i) {
                var topicName = allTopics.item(i).getAttribute("topic_name");
                var _this_topic_html_string_ = "<li><a class='topicName' >" + topicName + "</a></li>";
                _html_topic_name_string_ += _this_topic_html_string_;
            }
            // APPENDING THE TOPICS AND SETTING A LISTENER TO IT
            $("#TOPICS").html(_html_topic_name_string_).find(".topicName").click(function () {
                var selectedTopicName = $(this).text();
                var selectedTopicContent;
                var noTopics = MY_NOTEBOOK_TOPICS.length;
                for (var i = 0; i < noTopics; ++i) {
                    if (selectedTopicName == MY_NOTEBOOK_TOPICS.item(i).getAttribute("topic_name")) {
                        // FOR FUTURE REFERENCE
                        PRESENT_TOPIC = MY_NOTEBOOK_TOPICS.item(i);

                        selectedTopicContent = MY_NOTEBOOK_TOPICS.item(i).textContent;

                        $("#TOPIC_NAME").val(selectedTopicName);
                        $("#TOPIC_CONTENT").val(selectedTopicContent);
                    }
                }
            });
            // INITIALIZING WITH THE THE FIRST TOPIC OF THE FIRST COURSE BY DEFAULT OR USING SESSION
            <?php
            if (isset($_SESSION["topic_name_SES"])) {
                $noTopicWithThatName = true;
                $noteBooks = new DOMDocument();
                $noteBooks->load("NoteBooks.xml");
                $subjects = $noteBooks->getElementsByTagName("subject");
                $noSubjects = $subjects->length;

                for ($i = 0; $i < $noSubjects; ++$i) {
                    if ($subjects->item($i)->getAttribute("course_code") == $_SESSION["course_code_SES"]) {
                        $topics = $subjects->item($i)->getElementsByTagName("topic");
                        $noTopics = $topics->length;

                        for ($j = 0; $j < $noTopics; ++$j) {
                            if ($topics->item($j)->getAttribute("topic_name") == $_SESSION["topic_name_SES"]) {
                                echo "PRESENT_TOPIC = MY_NOTEBOOK_TOPICS.item($j);";
                                $noTopicWithThatName = false;
                                break;
                            }
                        }
                        break;
                    }
                }
                if($noTopicWithThatName)echo "PRESENT_TOPIC = MY_NOTEBOOK_TOPICS.item(0);";
            } else {
                echo "PRESENT_TOPIC = MY_NOTEBOOK_TOPICS.item(0);";
            }
            ?>
            $("#TOPIC_NAME").val(PRESENT_TOPIC.getAttribute("topic_name"));
            $("#TOPIC_CONTENT").val(PRESENT_TOPIC.textContent);


            //TOPICS ARE ATTACHED TO THE PAGE AND CLICK LISTENERS ARE SET
            // PREPARING LOGISTICS
            var logistics = MY_NOTEBOOK.getElementsByTagName("logistics").item(0).textContent;
            $("#LOGISTICS").html(logistics);
            // LOGISTICS IS ATTACHED TO PAGE


            /*
             * ..................FORMS..................
             * */

        }

        $(document).ready(function () {

            // GETTING AN XML RESPONSE
            var xmlHttpConnection = new XMLHttpRequest();
            xmlHttpConnection.onreadystatechange = function () {
                // RESPONSE READY SITUATION
                if (xmlHttpConnection.readyState == 4 && xmlHttpConnection.status == 200) {
                    NOTEBOOKS_FILE = xmlHttpConnection.responseXML;

                    // INITIALIZING WITH FIRST NOTE BOOK OR USING SESSIONS
                    <?php
                    if (isset($_SESSION["course_code_SES"])) {
                        $noCourseWithThatCode = true;
                        $noteBooks = new DOMDocument();
                        $noteBooks->load("NoteBooks.xml");
                        $subjects = $noteBooks->getElementsByTagName("subject");
                        $noSubjects = $subjects->length;

                        for ($i = 0; $i < $noSubjects; ++$i) {
                            if ($subjects->item($i)->getAttribute("course_code") == $_SESSION["course_code_SES"]) {
                                echo "MY_NOTEBOOK = NOTEBOOKS_FILE.getElementsByTagName('subject').item($i);";
                                $noCourseWithThatCode = false;
                                break;
                            }
                        }
                        if($noCourseWithThatCode)echo "MY_NOTEBOOK = NOTEBOOKS_FILE.getElementsByTagName('subject').item(0);";
                    }
                    else {
                        echo "MY_NOTEBOOK = NOTEBOOKS_FILE.getElementsByTagName('subject').item(0);";
                    }
                    ?>
                    MY_NOTEBOOK_TOPICS = MY_NOTEBOOK.getElementsByTagName("topic");
                    loadThisNoteBook();
                }
            };
            xmlHttpConnection.open("POST", "NoteBooks.xml", true);
            xmlHttpConnection.send();
            // GETTING AN XML RESPONSE

            // LISTENERS FOR NOTEBOOKS
            $("#navigation").find("a").click(function () {
                var selectedSubject = $(this).attr("id");
                var allSubjects = NOTEBOOKS_FILE.getElementsByTagName("subject");
                var noSubjects = allSubjects.length;

                for (var i = 0; i < noSubjects; ++i) {
                    if (selectedSubject == allSubjects.item(i).getAttribute("course_code")) {
                        MY_NOTEBOOK = allSubjects.item(i);
                        MY_NOTEBOOK_TOPICS = MY_NOTEBOOK.getElementsByTagName("topic");
                        loadThisNoteBook();
                    }
                }
            });
            // LISTENERS FOR NOTEBOOKS

        });

    </script>

    <style>
        button:focus {
            outline: none;
        }

        .subjectLink:hover {
            cursor: pointer;
        }

        .topicName:hover {
            cursor: pointer;
        }

        textarea {
            overflow: scroll;
            min-height: 200px;
        }

        #navigation button{
            margin-top: 7px;
        }

        #NAME{
            border: none;
            text-align: center;
            font-size: 40px;

        }

        #edit_course_name_button ,#edit_course_name_submit {
            margin-bottom: 20px;
        }

        #allAboutTopics a {
            font-size: 20px;
            text-decoration: none;
        }


        #allAboutTopics a:hover{

        }

        #allAboutTopics ul {
            list-style: upper-latin;
        }

        #TOPIC_NAME{
            border: none;
            color: steelblue;
            font-size: 30px;
        }

        #TOPIC_CONTENT {
            min-height: 700px;
            border: none;
            color: black;
            font-size: 20px;
        }

        #LOGISTICS{
        }
    </style>

</head>

<body>

<nav class="">
    <div id="navigation" class="container-fluid">
        <ul class="nav nav-tabs">
            <li class="navbar-brand" style="background-color: steelblue;color: white">Note Books</li>
            <?php
            $noteBooks = new DOMDocument();
            $noteBooks->load("NoteBooks.xml");
            $subjects = $noteBooks->getElementsByTagName("subject");
            foreach ($subjects as $subject) {
                $subjectCode = $subject->getAttribute("course_code");
                echo "<li><a class='subjectLink'  id='$subjectCode'>";
                echo $subjectCode;
                echo "</a></li>";
            }
            ?>
            <button type="button" class="btn btn-default btn-sm" data-toggle="modal" data-target="#create-form-div"> +
            </button>
            <button type="button" class="btn btn-danger btn-sm" data-toggle="modal" data-target="#delete-form-div">-
            </button>
        </ul>
    </div>
</nav>

<div class="container-fluid">

    <div class="row">
        <!--SUBJECT NAME-->
            <div class="col-xs-12">
                <!--edit-course-name-form-->
                <form action="Processor.php" method="post" id="edit_course_name_form">
                    <input id="NAME" name="course_name" value="NOTE BOOK" style="min-width: 90%" type="text"
                           class="editable" readonly>
                    <input type="text" name="form_type" value="editCourseName" style="display: none;">
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
                                        if ($(this).val() != MY_NOTEBOOK.getAttribute("course_name")) {
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
                                    $("#NAME").attr("readonly", true).val(MY_NOTEBOOK.getAttribute("course_name"));
                                    $("#edit_course_name_submit").fadeOut();
                                }
                            });

                            $("#edit_course_name_submit").click(function () {
                                var course_code_t = MY_NOTEBOOK.getAttribute("course_code");
                                var course_code_input = "<input type='text' name='course_code' value='" + course_code_t + "' style='display: none;'>";

                                // FOR SESSION VARS
                                var course_code_SES = MY_NOTEBOOK.getAttribute("course_code");
                                var topic_name_SES = PRESENT_TOPIC.getAttribute("topic_name");
                                var course_code_SES_input = "<input type='text' name='course_code_SES' value='" + course_code_SES + "' style='display: none;'>";
                                var topic_name_SES_input = "<input type='text' name='topic_name_SES' value='" + topic_name_SES + "' style='display: none;'>";
                                // FOR SESSION VARS


                                $("#edit_course_name_form").append(course_code_input).append(course_code_SES_input).append(topic_name_SES_input).submit();
                            });
                        });
                    </script>
                </form>
                <!--edit-course-name-form-->
            </div>
    </div>
    <hr style="margin:0;">

    <div style="margin-top: 10px" class="middle row">

        <div class="col-xs-2" id="allAboutTopics">

            <!--ADD TOPIC FORM-->
            <form action="Processor.php" method="post" id="add_topic_form">
                <h4 id="note" style="display: none;">another topic exists with same name</h4>
                <input type="text" name="form_type" value="createTopic" style="display:none;">
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
                        var noTopics = MY_NOTEBOOK_TOPICS.length;

                        for (var j = 0; j < noTopics; ++j) {
                            if (newTopicName == MY_NOTEBOOK_TOPICS.item(j).getAttribute("topic_name")) {
                                addTopic = false;
                                break;
                            }
                        }
                        if (addTopic) {
                            var course_code_t = MY_NOTEBOOK.getAttribute("course_code");
                            var course_code_input = "<input type='text' name='course_code' value='" + course_code_t + "' style='display: none;'>";

                            // FOR SESSION VARS
                            var course_code_SES = course_code_t;
                            var topic_name_SES = newTopicName;
                            var course_code_SES_input = "<input type='text' name='course_code_SES' value='" + course_code_SES + "' style='display: none;'>";
                            var topic_name_SES_input = "<input type='text' name='topic_name_SES' value='" + topic_name_SES + "' style='display: none;'>";
                            // FOR SESSION VARS

                            $("#add_topic_form").append(course_code_input).append(course_code_SES_input).append(topic_name_SES_input).submit();
                        }
                        else {
                            $("#add_topic_form").find("#note").fadeIn().delay(2000).fadeOut();
                        }
                    });
                </script>
            </form>
            <!--ADD TOPIC FORM-->

            <ul id="TOPICS">
                <!--TOPICS-->
                T O P I C S
            </ul>

            <!--REMOVE TOPIC FORM-->
            <form action="Processor.php" method="post" id="remove_topic_form">
                <h4 id="note" style="display: none;">No such topic</h4>
                <h4 id="note2" style="display: none;">Atleast one topic would be nice</h4>
                <input type="text" name="form_type" value="deleteTopic" style="display:none;">
                <input type="button" value="-" class="btn btn-danger" id="remove_topic_button">

                <div id="removeTopicToggle" style="display: none">
                    <input id="oldTopicName" type="text" name="topic_name" placeholder="Topics' name">
                    <input type="button" id="remove_topic_submit" class="btn btn-danger" value="Go">
                </div>

                <script>
                    $("#remove_topic_button").click(function () {
                        if (MY_NOTEBOOK_TOPICS.length > 1) {
                            $("#removeTopicToggle").fadeToggle();
                        } else {
                            $("#remove_topic_form").find("#note2").fadeIn().delay(2000).fadeOut();
                        }
                    });

                    $("#remove_topic_submit").click(function () {
                        var removeTopic = false;
                        var oldTopicName = $("#oldTopicName").val();
                        var noTopics = MY_NOTEBOOK_TOPICS.length;
                        var course_code_t = MY_NOTEBOOK.getAttribute("course_code");

                        for (var j = 0; j < noTopics; ++j) {
                            if (oldTopicName == MY_NOTEBOOK_TOPICS.item(j).getAttribute("topic_name")) {
                                removeTopic = true;
                                break;
                            }
                        }

                        if (removeTopic) {
                            var course_code_input = "<input type='text' name='course_code' value='" + course_code_t + "' style='display: none;'>";

                            // FOR SESSION VARS
                            var course_code_SES = MY_NOTEBOOK.getAttribute("course_code");
                            var topic_name_SES = PRESENT_TOPIC.getAttribute("topic_name");
                            var course_code_SES_input = "<input type='text' name='course_code_SES' value='" + course_code_SES + "' style='display: none;'>";
                            var topic_name_SES_input = "<input type='text' name='topic_name_SES' value='" + topic_name_SES + "' style='display: none;'>";
                            // FOR SESSION VARS

                            $("#remove_topic_form").append(course_code_input).append(course_code_SES_input).append(topic_name_SES_input).submit();
                        } else {
                            $("#remove_topic_form").find("#note").fadeIn().delay(2000).fadeOut();
                        }
                    });
                </script>
            </form>
            <!--REMOVE TOPIC FORM-->

        </div>

        <div class="col-xs-8">

            <div>
                <!--edit-topic-form-->
                <form action="Processor.php" method="post" id="edit_topic_form">
                    <h4 id="note" style="display: none;">this topic already exists</h4>
                    <button id="edit_topic_submit" type="button" class="btn btn-success glyphicon glyphicon-floppy-open"
                            style="display: none"></button>
                    <blockquote>
                        <input id="TOPIC_NAME" name="topic_name" value="Fill your" style="min-width: 90%" type="text"
                               class="editable" readonly>
                    </blockquote>
                    <textarea id="TOPIC_CONTENT" name="topic_content" class="form-control editable" readonly>T h o u g h t s</textarea>
                    <input type="text" name="form_type" value="editTopic" style="display: none;">

                    <script>
                        var topicActive = false;

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
                                        var noTopic = MY_NOTEBOOK_TOPICS.length;
                                        for (var i = 0; i < noTopic; ++i) {
                                            if (MY_NOTEBOOK_TOPICS.item(i).getAttribute("topic_name") == $(TOPIC_NAME_INPUT).val()) {
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
                            var course_code_t = MY_NOTEBOOK.getAttribute("course_code");
                            var course_code_input = "<input type='text' name='course_code' value='" + course_code_t + "' style='display: none;'>";
                            var topic_name_t = PRESENT_TOPIC.getAttribute("topic_name");
                            var topic_name_input = "<input type='text' name='ref_topic_name' value='" + topic_name_t + "' style='display: none;'>";

                            // FOR SESSION VARS
                            var course_code_SES = MY_NOTEBOOK.getAttribute("course_code");
                            var topic_name_SES = PRESENT_TOPIC.getAttribute("topic_name");
                            var course_code_SES_input = "<input type='text' name='course_code_SES' value='" + course_code_SES + "' style='display: none;'>";
                            var topic_name_SES_input = "<input type='text' name='topic_name_SES' value='" + topic_name_SES + "' style='display: none;'>";
                            // FOR SESSION VARS

                            $("#edit_topic_form").append(course_code_input).append(topic_name_input).append(course_code_SES_input).append(topic_name_SES_input).submit();
                        });

                    </script>
                </form>
                <!--edit-topic-form-->
            </div>

        </div>

        <div class="col-xs-2">
            <span style="font-size: 15px;margin-top: 10px">KNOW MORE ABOUT YOUR COURSE</span>
            <hr>
            <div>
                <!--edit-logistics-form-->
                <form action="Processor.php" method="post" id="edit_logistics_form">
                    <textarea name="logistics" id="LOGISTICS" class="form-control" readonly></textarea>
                    <input type="text" name="form_type" value="editLogistics" style="display: none;">
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
                                    var prevLogistics = MY_NOTEBOOK.getElementsByTagName("logistics").item(0).textContent;
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
                            var course_code_t = MY_NOTEBOOK.getAttribute("course_code");
                            var course_code_input = "<input type='text' name='course_code' value='" + course_code_t + "' style='display: none;'>";

                            // FOR SESSION VARS
                            var course_code_SES = MY_NOTEBOOK.getAttribute("course_code");
                            var topic_name_SES = PRESENT_TOPIC.getAttribute("topic_name");
                            var course_code_SES_input = "<input type='text' name='course_code_SES' value='" + course_code_SES + "' style='display: none;'>";
                            var topic_name_SES_input = "<input type='text' name='topic_name_SES' value='" + topic_name_SES + "' style='display: none;'>";
                            // FOR SESSION VARS

                            $("#edit_logistics_form").append(course_code_input).append(course_code_SES_input).append(topic_name_SES_input).submit();
                        });
                    </script>
                </form>
                <!--edit-logistics-form-->
            </div>
        </div>
    </div>

</div>

<!--create-form-div-->
<div id="create-form-div" class="modal fade" role="dialog">
    <div class="modal-dialog">

        <div class="modal-content">
            <div class="modal-header">

                <button type="button" class="close" data-dismiss="modal">&times;</button>
            </div>
            <div class="modal-body">
                <form action="Processor.php" method="post" id="create_subject_form">
                    <input type="text" autofocus placeholder="COURSE NAME" name="course_name">
                    <input type="text" placeholder="COURSE CODE" id="create_subject_course_code" name="course_code">
                    <span id="create_subject_warning" class="glyphicon glyphicon-alert" style="display: none">you have a course with same code</span>
                    <input type="text" name="form_type" value="createSubject" style="display: none;">
                    <input type="button" id="create_subject_submit" value="Get Started" class="btn btn-default">
                    <script>
                        $(document).ready(function () {
                            var createConfirm = false;
                            $("#create_subject_course_code").keyup(function () {
                                var givenCourseCodeInput = $(this).val();
                                var subjects = NOTEBOOKS_FILE.getElementsByTagName("subject");
                                var noSubjects = subjects.length;
                                for (var i = 0; i < noSubjects; ++i) {
                                    if (subjects.item(i).getAttribute("course_code") == givenCourseCodeInput) {
                                        $("#create_subject_warning").fadeIn();
                                        createConfirm = false;
                                        break;
                                    }
                                    else {
                                        $("#create_subject_warning").fadeOut();
                                        createConfirm = true;
                                    }
                                }
                            });
                            $("#create_subject_submit").click(function () {

                                // FOR SESSION VARS
                                var course_code_SES = $("#create_subject_course_code").val();
                                var topic_name_SES = PRESENT_TOPIC.getAttribute("topic_name");
                                var course_code_SES_input = "<input type='text' name='course_code_SES' value='" + course_code_SES + "' style='display: none;'>";
                                var topic_name_SES_input = "<input type='text' name='topic_name_SES' value='" + topic_name_SES + "' style='display: none;'>";
                                // FOR SESSION VARS

                                if (createConfirm)$("#create_subject_form").append(course_code_SES_input).append(topic_name_SES_input).submit();
                            });
                        });
                    </script>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>

<!--delete-form-div-->
<div id="delete-form-div" class="modal fade" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal">&times;</button>
                <h4>Are you sure ?</h4>
                <h5>This removes all the notes you have prepared for this subject</h5>
                <h4 style="display:none;" id="note">Donot remove this Notebook .Its better to have a template
                    guide.</h4>
            </div>
            <div class="modal-body">
                <form action="Processor.php" method="post" id="del_subject_form">
                    <input type="text" style="min-width: 50%" name="course_code" id="course_code_key"
                           placeholder="Type in this courses' code to Confirm" autofocus>
                    <span id="del_subject_warning" class="glyphicon glyphicon-alert" style="display: none"></span>
                    <input type="text" name="form_type" value="deleteSubject" style="display: none;">
                    <input type="button" id="del_subject_submit" value="Remove This Subject" class="btn btn-default">
                    <script>
                        $(document).ready(function () {
                            var delConfirm = false;
                            $("#course_code_key").keyup(function () {

                                if ($(this).val() == "EX101") {
                                    $("#delete-form-div").find("#note").fadeIn();
                                    delConfirm = false;
                                }
                                else if ($(this).val() == MY_NOTEBOOK.getAttribute("course_code")) {
                                    $("#del_subject_warning").fadeIn();
                                    delConfirm = true;
                                }
                                else {
                                    $("#del_subject_warning").fadeOut();
                                    $("#delete-form-div").find("#note").fadeOut();
                                    delConfirm = false;
                                }

                            });

                            $("#del_subject_submit").click(function () {

                                if (delConfirm)$("#del_subject_form").submit();
                            });
                        });
                    </script>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
            </div>
        </div>

    </div>
</div>

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
