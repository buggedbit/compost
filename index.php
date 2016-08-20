<!DOCTYPE html>
<html>
<head>
    <link rel="stylesheet" href="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/1.12.4/jquery.min.js"></script>
    <script src="http://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js"></script>

    <title>SchoolBag</title>
    <script src="index.js"></script>

    <script>
        var MY_NOTEBOOK;
        var MY_NOTEBOOK_TOPICS;
        var NOTEBOOKS_FILE;

        function loadThisNoteBook() {
            $("#NAME").text(MY_NOTEBOOK.getAttribute("course_name"));
            // PREPARING THE TOPICS
            var _html_topic_name_string_ = "";
            var allTopics = MY_NOTEBOOK_TOPICS;
            var noTopics = allTopics.length;
            for (var i = 0; i < noTopics; ++i) {
                var topicName = allTopics.item(i).getAttribute("topic_name");
                var _this_topic_html_string_ = "<li><a class='topicName' >" + topicName + "</a></li>";
                _html_topic_name_string_ += _this_topic_html_string_;
            }
            $("#TOPICS").html(_html_topic_name_string_).find(".topicName").click(function () {
                var selectedTopicName = $(this).text();
                var selectedTopicContent;
                var noTopics = MY_NOTEBOOK_TOPICS.length;
                for (var i = 0; i < noTopics; ++i) {
                    if (selectedTopicName == MY_NOTEBOOK_TOPICS.item(i).getAttribute("topic_name")) {
                        selectedTopicContent = MY_NOTEBOOK_TOPICS.item(i).textContent
                        $("#TOPIC_CONTENT").text(selectedTopicContent);
                    }
                }
            });
            //TOPICS ARE ATTACHED TO THE PAGE AND CLICK LISTENERS ARE SET
            // PREPARING LOGISTICS
            var logistics = MY_NOTEBOOK.getElementsByTagName("logistics").item(0).textContent;
            $("#LOGISTICS").html(logistics);
            // LOGISTICS IS ATTACHED TO PAGE
            // SETTING TOPIC_CONTENT TO A DEFAULT VALUE
            $("#TOPIC_CONTENT").text("Your Thoughts");

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
        .row {
            border: 1px solid black;
        }

        button:focus {
            outline: none;
        }

        .subjectLink:hover {
            cursor: pointer;
        }

        .topicName:hover {
            cursor: pointer;
        }
    </style>

</head>

<body>

<nav class="navbar">
    <div id="navigation" class="container-fluid">
        <ul class="nav nav-tabs">
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
        </ul>
    </div>
</nav>

<div class="container-fluid">

    <div id="selectedSubject" class="middle row">
        <div class="col-xs-2">
            <ul id="TOPICS">
                <!--TOPICS-->
                TOPICS
            </ul>
        </div>
        <div class="col-xs-8">
            <!--SUBJECT NAME-->
            <div class="row">
                <h1 class="col-xs-12" id="NAME">
                    NoteBook
                </h1>
            </div>

            <div class="row">
                <h1 class="col-xs-12" id="TOPIC_CONTENT">
                    Your Thoughts
                </h1>
            </div>

        </div>
        <div class="col-xs-2">
            Some Logistical Points
            <hr>
            <div id="LOGISTICS">

            </div>
        </div>
    </div>

</div>

<button type="button" class="btn btn-default btn-sm" data-toggle="modal" data-target="#create-form-div"> +</button>
<button type="button" class="btn btn-danger btn-sm" data-toggle="modal" data-target="#delete-form-div"> -</button>

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
                    <span id="create_subject_warning" class="glyphicon glyphicon-alert" style="display: none"></span>
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
                                        $("#create_subject_warning").fadeIn().text("Duplicate");

                                        createConfirm = false;
                                    }
                                    else {
                                        $("#create_subject_warning").fadeOut().text("");
                                        createConfirm = true;
                                    }
                                }
                            });
                            $("#create_subject_submit").click(function () {
                                if (createConfirm)$("#create_subject_form").submit();
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
                                if ($(this).val() == MY_NOTEBOOK.getAttribute("course_code")) {
                                    $("#del_subject_warning").fadeIn();
                                    delConfirm = true;
                                }
                                else {
                                    $("#del_subject_warning").fadeOut();
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