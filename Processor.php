<?php

$subjectNodeName = "subject";
$course_nameAttrName = "course_name";
$course_codeAttrName = "course_code";
$logisticsNodeName = "logistics";
$topicNodeName = "topic";
$topic_nameAttrName = "topic_name";

$initialFillUpForLogistics = "Nothing Yet";
$initialFillUpForTopicName = "Getting Started";
$initialFillUpForTopicContent = "Fill Your Thoughts";


if ($_SERVER["REQUEST_METHOD"] == "POST") {

    // SESSION PART
    session_start();

//    $_SESSION["course_code_SES"] = $_POST["course_code_SES"];
//    $_SESSION["topic_name_SES"] = $_POST["topic_name_SES"];

    //

    switch ($_POST["form_type"]) {
        case "createSubject":
            $create_course_name = $_POST['course_name'];
            $create_course_code = $_POST['course_code'];
            $noteBooks = new DOMDocument();
            $noteBooks->load("NoteBooks.xml");

            $rootNode = $noteBooks->getElementsByTagName("root")->item(0);

            // Creating nodes
            /**/
            $subjectNode = $noteBooks->createElement($subjectNodeName);
            /*--->*/
            $subjectNode->setAttribute($course_nameAttrName, $create_course_name);
            /*--->*/
            $subjectNode->setAttribute($course_codeAttrName, $create_course_code);

            /*------->*/
            $logisticsNode = $noteBooks->createElement($logisticsNodeName, $initialFillUpForLogistics);

            /*------->*/
            $topicNode = $noteBooks->createElement($topicNodeName, $initialFillUpForTopicContent);
            /*----------->*/
            $topicNode->setAttribute($topic_nameAttrName, $initialFillUpForTopicName);

            /**/
            $subjectNode->appendChild($logisticsNode);
            /**/
            $subjectNode->appendChild($topicNode);


            $rootNode->appendChild($subjectNode);

            $noteBooks->save("NoteBooks.xml");
            echo " Created at " . date("H:i:s");
            break;

        case "deleteSubject":
            $del_course_code = $_POST["course_code"];

            $noteBooks = new DOMDocument();
            $noteBooks->load("NoteBooks.xml");

            $subjects = $noteBooks->getElementsByTagName("subject");

            foreach ($subjects as $subject) {
                if ($subject->getAttribute("course_code") == $del_course_code) {
                    $subject->parentNode->removeChild($subject);
                    break;
                }
            }

            $noteBooks->save("NoteBooks.xml");
            echo " Removed at " . date("H:i:s");

            break;

        case "editCourseName":
            $reference_course_code = $_POST["course_code"];
            $edit_course_name = $_POST["course_name"];

            $noteBooks = new DOMDocument();
            $noteBooks->load("NoteBooks.xml");

            $subjects = $noteBooks->getElementsByTagName("subject");

            foreach ($subjects as $subject) {
                if ($subject->getAttribute("course_code") == $reference_course_code) {
                    $subject->setAttribute("course_name" , $edit_course_name);
                    break;
                }
            }

            $noteBooks->save("NoteBooks.xml");
            echo " Updated at " . date("H:i:s");
            break;

        case "editLogistics":
            $reference_course_code = $_POST["course_code"];
            $edit_logistics = $_POST["logistics"];

            $noteBooks = new DOMDocument();
            $noteBooks->load("NoteBooks.xml");

            $subjects = $noteBooks->getElementsByTagName("subject");

            foreach ($subjects as $subject) {
                if ($subject->getAttribute("course_code") == $reference_course_code) {
                    $subject->getElementsByTagName("logistics")->item(0)->nodeValue = $edit_logistics;
                    break;
                }
            }

            $noteBooks->save("NoteBooks.xml");
            echo " Updated at " . date("H:i:s");
            break;

        case "createTopic":

            $reference_course_code = $_POST["course_code"];
            $new_topic_name = $_POST["topic_name"];

            $noteBooks = new DOMDocument();
            $noteBooks->load("NoteBooks.xml");

            $subjects = $noteBooks->getElementsByTagName("subject");

            foreach ($subjects as $subject) {
                if ($subject->getAttribute("course_code") == $reference_course_code) {
                    $newTopicNode = $noteBooks->createElement("topic" , $initialFillUpForTopicContent);
                    $newTopicNode->setAttribute("topic_name" , $new_topic_name);
                    $subject->appendChild($newTopicNode);
                    break;
                }
            }

            $noteBooks->save("NoteBooks.xml");
            echo " Created at " . date("H:i:s");
            break;

        case "editTopic":

            $reference_course_code = $_POST["course_code"] ;
            $reference_topic_name = $_POST["ref_topic_name"];
            $edit_topic_name = $_POST["topic_name"];
            $edit_topic_content = $_POST["topic_content"];

            $noteBooks = new DOMDocument();
            $noteBooks->load("NoteBooks.xml");

            $subjects = $noteBooks->getElementsByTagName("subject");

            foreach ($subjects as $subject) {

                if ($subject->getAttribute("course_code") == $reference_course_code) {
                    $topics = $subject->getElementsByTagName("topic");
                    foreach ($topics as $topic){
                        if($topic->getAttribute("topic_name") == $reference_topic_name){
                            $topic->setAttribute("topic_name" , $edit_topic_name);
                            $topic->nodeValue = $edit_topic_content;
                            break;
                        }
                    }
                    break;
                }
            }

            $noteBooks->save("NoteBooks.xml");
            echo " Updated at " . date("H:i:s");
            break;

        case "deleteTopic":

            $reference_course_code = $_POST["course_code"];
            $old_topic_name = $_POST["topic_name"];

            $noteBooks = new DOMDocument();
            $noteBooks->load("NoteBooks.xml");

            $subjects = $noteBooks->getElementsByTagName("subject");

            foreach ($subjects as $subject) {
                if ($subject->getAttribute("course_code") == $reference_course_code) {
                    $topics = $subject->getElementsByTagName("topic");
                    foreach ($topics as $topic){
                        if($topic->getAttribute("topic_name") == $old_topic_name){
                            $topic->parentNode->removeChild($topic);
                        }
                    }
                    break;
                }
            }

            $noteBooks->save("NoteBooks.xml");
            echo " Removed at " . date("H:i:s");
            break;

        default:
    }
}

?>

