<?php
$xml_doc_path = "../data/data.xml";

$tags = array(
    'bag' => 'bag',
    'book' => 'book',
    'chapter' => 'chapter',
);

$attributes = array(
    'pk' => 'pk',
    'name' => 'name',
);

$defaultChapterName = "Getting Started";
$defaultChapterContent = "Fill Your Thoughts";

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    switch ($_POST["q"]) {
        case "book_create":
            $book_name = $_POST['book_name'];
            $document = new DOMDocument();
            $document->load($xml_doc_path);

            $bag = $document->getElementsByTagName($tags['bag'])->item(0);

            $no_books_until_now = $document->getElementsByTagName($tags['book'])->length;

            // Creating nodes
            $book = $document->createElement($tags['book']);
            /*--->*/
            $book->setAttribute($attributes['name'], $book_name);
            /*--->*/
            $book->setAttribute($attributes['pk'], $no_books_until_now + 1);

            /*------->*/
            $chapter = $document->createElement($tags['chapter'], $defaultChapterContent);
            /*------->*/
            $chapter->setAttribute($attributes['name'], $defaultChapterName);
            /*------->*/
            $chapter->setAttribute($attributes['pk'], 1);

            $book->appendChild($chapter);
            $bag->appendChild($book);

            $document->save("data.xml");
            echo "hello";
            break;

        case "createSubject":
            $create_course_name = $_POST['course_name'];
            $create_course_code = $_POST['course_code'];
            $document = new DOMDocument();
            $document->load("data.xml");

            $bag = $document->getElementsByTagName("root")->item(0);

            // Creating nodes
            /**/
            $book = $document->createElement($subjectNodeName);
            /*--->*/
            $book->setAttribute($course_nameAttrName, $create_course_name);
            /*--->*/
            $book->setAttribute($course_codeAttrName, $create_course_code);

            /*------->*/
            $logisticsNode = $document->createElement($logisticsNodeName, $initialFillUpForLogistics);

            /*------->*/
            $chapter = $document->createElement($topicNodeName, $defaultChapterContent);
            /*----------->*/
            $chapter->setAttribute($topic_nameAttrName, $defaultChapterName);

            /**/
            $book->appendChild($logisticsNode);
            /**/
            $book->appendChild($chapter);


            $bag->appendChild($book);

            echo $document->save("data.xml");
            break;

        case "deleteSubject":
            $del_course_code = $_POST["course_code"];

            $document = new DOMDocument();
            $document->load("data.xml");

            $subjects = $document->getElementsByTagName("subject");

            foreach ($subjects as $subject) {
                if ($subject->getAttribute("course_code") == $del_course_code) {
                    $subject->parentNode->removeChild($subject);
                    break;
                }
            }

            $document->save("data.xml");
            echo " Removed at " . date("H:i:s");

            break;

        case "editCourseName":
            $reference_course_code = $_POST["course_code"];
            $edit_course_name = $_POST["course_name"];

            $document = new DOMDocument();
            $document->load("data.xml");

            $subjects = $document->getElementsByTagName("subject");

            foreach ($subjects as $subject) {
                if ($subject->getAttribute("course_code") == $reference_course_code) {
                    $subject->setAttribute("course_name", $edit_course_name);
                    break;
                }
            }

            $document->save("data.xml");
            echo " Updated at " . date("H:i:s");
            break;

        case "editLogistics":
            $reference_course_code = $_POST["course_code"];
            $edit_logistics = $_POST["logistics"];

            $document = new DOMDocument();
            $document->load("data.xml");

            $subjects = $document->getElementsByTagName("subject");

            foreach ($subjects as $subject) {
                if ($subject->getAttribute("course_code") == $reference_course_code) {
                    $subject->getElementsByTagName("logistics")->item(0)->nodeValue = $edit_logistics;
                    break;
                }
            }

            $document->save("data.xml");
            echo " Updated at " . date("H:i:s");
            break;

        case "createTopic":

            $reference_course_code = $_POST["course_code"];
            $new_topic_name = $_POST["topic_name"];

            $document = new DOMDocument();
            $document->load("data.xml");

            $subjects = $document->getElementsByTagName("subject");

            foreach ($subjects as $subject) {
                if ($subject->getAttribute("course_code") == $reference_course_code) {
                    $newTopicNode = $document->createElement("topic", $defaultChapterContent);
                    $newTopicNode->setAttribute("topic_name", $new_topic_name);
                    $subject->appendChild($newTopicNode);
                    break;
                }
            }

            $document->save("data.xml");
            echo " Created at " . date("H:i:s");
            break;

        case "editTopic":

            $reference_course_code = $_POST["course_code"];
            $reference_topic_name = $_POST["ref_topic_name"];
            $edit_topic_name = $_POST["topic_name"];
            $edit_topic_content = $_POST["topic_content"];

            $document = new DOMDocument();
            $document->load("data.xml");

            $subjects = $document->getElementsByTagName("subject");

            foreach ($subjects as $subject) {

                if ($subject->getAttribute("course_code") == $reference_course_code) {
                    $topics = $subject->getElementsByTagName("topic");
                    foreach ($topics as $topic) {
                        if ($topic->getAttribute("topic_name") == $reference_topic_name) {
                            $topic->setAttribute("topic_name", $edit_topic_name);
                            $topic->nodeValue = $edit_topic_content;
                            break;
                        }
                    }
                    break;
                }
            }

            $document->save("data.xml");
            echo " Updated at " . date("H:i:s");
            break;

        case "deleteTopic":

            $reference_course_code = $_POST["course_code"];
            $old_topic_name = $_POST["topic_name"];

            $document = new DOMDocument();
            $document->load("data.xml");

            $subjects = $document->getElementsByTagName("subject");

            foreach ($subjects as $subject) {
                if ($subject->getAttribute("course_code") == $reference_course_code) {
                    $topics = $subject->getElementsByTagName("topic");
                    foreach ($topics as $topic) {
                        if ($topic->getAttribute("topic_name") == $old_topic_name) {
                            $topic->parentNode->removeChild($topic);
                        }
                    }
                    break;
                }
            }

            $document->save("data.xml");
            echo " Removed at " . date("H:i:s");
            break;

        default:
    }
}

?>

