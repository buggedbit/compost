<!DOCTYPE html>
<html>
    <head>
        <title>eeVee</title>
        <script src="jQuery/jQuery.js"></script>
        <link href="BootStrap/bootstrap.min.css" type="text/css" rel="stylesheet"/>
        <script src="BootStrap/bootstrap.min.js"></script>
        <style>            
            #navBar {
                text-align: center;
            }
            #header{
                text-align: center;
                background-color: skyblue;
            }
            
            #title{
                font-size: 40px;
                color: white;
            }
            .jumbotron ul{
                font-size: 20px;
            }
            </style>
    </head>
    
    <body>
        <div class="container-fluid" >
            <div class="row" id="header">
                <span title="eeVee" id="title"> eeVee </span>
            </div>

          
            <div class="row" id="navBar">
                <table class="table">
                    <tr class="info">
                        <td class="col-xs-2"><button class="btn btn-primary" type="button">Home</button></td>
                        <td class="col-xs-2">
                            <a href="Documentation/howToUse/howToUse.php"><button class="btn btn-primary" type="button">How To Use ?</button></a>
                            <div id="featureDropDownContent">
                                       
                            </div>
                        </td>
                        <td class="col-xs-4">
                            <a href="Documentation/javaCode/documentationBook.php">
                                <button class="btn btn-primary" type="button">
                                    Documentation
                                </button>
                            </a>    
                        </td>
                        <td class="col-xs-2">
                            <a style="color:white" href="gateWay/logInPage/loginScreen.php" ><button class="btn btn-success" type="button">
                                Log In</button></a>
                        </td>
                        <td class="col-xs-2">
                            <a style="color:white" href="gateWay/signUpPage/signupScreen.php"><button class="btn btn-success" type='button' >Sign Up</button></a></td>
                    </tr>    
                </table>
            </div>
    </div>

            <div class="container">
            <div id="des">
                <div class="well well-sm"><h3>Introduction</h3></div>
                <h4>Our app is mainly built on two fundamental concepts</h4>
                    <ul>
                        <li><blockquote>Pictorial Perception and</blockquote></li>
                        <li><blockquote>Localization and Management of Events and Tasks</blockquote></li>    
                    </ul>
                <div class="jumbotron">
                    <h3>Pictorial Perception</h3>
                    <ul style="padding-left:20px;">
                        <li>Pictures are better than Words</li>
                        <li> This is the idea behind our app</li>
                        <li>In our app events and tasks are represented as horizontal bars with width of them representing the duration </li>
                        <li>And then they are placed on something called a timeline</li>
                        <li>The timeline is similar to a real number line , only change all numbers are replaced by times</li>
                        <li> The left edge of the screen represents present and the everything to the right represents future</li>
                        <li>The future is divided by time divisions</li>
                        <li>This way we can associate distance between time divisions with time interval</li>
                        <li>This way of representation has particularly two advantages </li>
                        <li>Just by a glance at it ,one can sense the duration and postition of the event in one's day  <br>
                        Also when we have multiple events it becomes extremely simple to figure out the overlaps between  the events</li>
                        <li>Therefore due to above reasons we obtain completely compute-free and simple interface </li>
                    </ul>
                    
                </div>
                <div class="jumbotron">
                    <h3>Pooling and Management</h3>
                    <ul style="padding-left:20px;">
                        <li>Spamming is a general scenario in any major institue like ours, This arises few problems </li>
                        <li>There will be lots and lots of events going on every day</li>
                        <li>Most of them which do not concern you or you are not interested in</li>
                        <li>However you end up getting mails and information about everything</li>
                        <li>To make it worse there is no fixed website or platform where these events are posted</li>
                        <li>Also there is no particular format in which events are posted</li>
                        <li>Result's tons and tons of unformatted raw data to be handled , filtered and remembered along with all the other issues one has to handle in daily life </li>
                        <li>Our App solves this issues ,by introducing two concepts Groups and Filters</li>
                        <li><h3>Groups</h3></li>
                        <li>Groups is an inheritance based concept. In this , first we divide our insti into various groups ,which are <mark>MUTUALLY EXCLUSIVE AND EXHAUSTIVE</mark> </li>
                        <li>Then again each group is divided into subgroups with same condition</li>
                        <li>In this way if a person belongs to a group then he/she must belong to the parent group</li>
                        <li>EXAMPLE
                            <ul>
                                <li>Insti</li>
                                    <ul>
                                        <li>UG</li>
                                            <ul>
                                                <li>BTech</li>
                                                <li>Dual Degree</li>
                                                <li>5yr MSc</li>
                                            </ul>
                                        <li>PG</li>
                                            <ul>
                                                <li>MTech</li>
                                                <li>MPhil</li>
                                                <li>MDes</li>
                                                <li>2yr MSc</li>
                                                <li>Management</li>
                                                <li>MSc PhD DualDegree</li>
                                                <li>MSc MTech DualDegree</li>
                                                <li>PGDIIT</li>
                                            </ul>
                                        <li>Research</li>
                                            <ul>
                                                <li>PhD</li>
                                            </ul>
                                    </ul>
                            </ul>
                        </li>
                        <li>One can post events through our portal , targeting the specifically the groups to whom the event is valid </li>
                        <li>Every app user can choose a group he belongs (or) is interested in so as to get the events posted in that particular group</li>
                        <li>Now the spamming is faily decreased and the user need not go through all the websites, analyse timings ,mentally make a timetable and (worst part) remember it!! </li>
                        <li>The event is automatically taken to the timeline and displayed according to its starting time and duration</li>
                        
                        <li><h3>Filters</h3></li>
                        <li>There are Two types
                            <ul>
                                <li>Club</li>
                                <li>Type</li>
                            </ul> 
                        </li>
                        <li>Now the events posted online can only be posted by authorized persons from authorized clubs
                        <br>Ex STAB , MI , TF</li>
                        <li>As a result there is a categorization of events based on clubs
                            <br>Ex 
                            <blockquote> 'TechFestATM' is posted from TechFest club
                            <br>'STABMeet' is posted from STAB club</blockquote>
                        </li>
                        <li>One can select Club filters so he/she can select only those clubs that he/she is interested in</li>
                        <li>Similarly events are also categorized and filtered using Type filter</li>
                    </ul>
                </div>
            </div>
        </div>
    </body>
</html>