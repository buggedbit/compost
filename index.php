<!DOCTYPE html>
<html>
<head>
    <title>School Bag</title>
    <link rel="icon" href="logo.png">

    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <link href="https://fonts.googleapis.com/icon?family=Material+Icons" rel="stylesheet">
    <script type="text/javascript" src="https://code.jquery.com/jquery-2.1.1.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/css/materialize.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/materialize/0.97.8/js/materialize.min.js"></script>

</head>
<body>
<div class="row">
    <div class="col l2 m2 s12">
        <ol id="book_list">
            <li>1</li>
            <li>1</li>
            <li>1</li>
            <li>1</li>
        </ol>
    </div>
    <div class="col l2 m2 s12">
        <div class="row">
            <form action="elephant.php" method="post" style="margin: 0">
                <div class="input-field">
                    <label for="new_chapter">Add Chapter</label><input id="new_chapter" type="text">
                    <input type="button" style="border: none" class="right btn-floating">
                </div>
            </form>
        </div>

        <div class="row">
            <ol id="chapter_list">
                <li>1</li>
                <li>1</li>
                <li>1</li>
                <li>1</li>
            </ol>
        </div>
    </div>
    <div class="col l8 m8 s12">
        <div>
            <div>Chapter</div>
            <textarea id="content"
                      title="Content"
                      style="height: 90vh"></textarea>
        </div>
    </div>
</div>
</body>
</html>
