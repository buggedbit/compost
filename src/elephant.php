<?php
$xml_doc_path = "../data/data.xml";
session_start();

if ($_SERVER["REQUEST_METHOD"] == "POST") {

    switch ($_POST["q"]) {
        case "log":
            if ($_POST['log_credential'] == "airturtle"){
                $_SESSION['logged_in'] = true;
                echo "Logged in";
            }
            else{
                $_SESSION['logged_in'] = false;
                echo "Logged out";
            }
            break;
        case "sync":
            if ($_SESSION['logged_in'] == true){
                $doc = new DOMDocument();
                $doc->loadXML($_POST['doc']);
                $doc->save($xml_doc_path);
                echo "Synchronized";
            }
            else{
                echo "Log in please";
            }
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