<?php
$xml_doc_path = "../data/data.xml";


if ($_SERVER["REQUEST_METHOD"] == "POST") {

    switch ($_POST["q"]) {
        case "sync":
            $doc = new DOMDocument();
            $doc->loadXML($_POST['doc']);
            $doc->save($xml_doc_path);
            break;
        case "open":
            header('Content-type: text/xml');
            $doc = file_get_contents($xml_doc_path);
            echo $doc;
            break;
        default:
            break;
    }

}