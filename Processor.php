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
            header("Location: index.php");
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
            header("Location: index.php");
            break;

        default:
    }
}

?>