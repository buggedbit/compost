package com.harsha.eevee;

import org.json.JSONArray;

public class Constants {

    public static final String EVENTNAME_NOTSET = "EVENTNAME_NOTSET";
    public static final String EVENTPLACE_NOTSET = "EVENTPLACE_NOTSET";
    public static final String TASKDESCRIPTION_NOTSET = "TASKNAME_NOTSET";
    public static final String TASKTAG_NOTSET = "TASKPLACE_NOTSET";
    public static final String REGNPLACE_NOTSET = "REGNPLACE_NOTSET";
    public static final String WEBSITE_NOTSET = "WEBSITE_NOTSET";
    public static final String COMMENTS_NOTSET = "COMMENTS_NOTSET";
    public static final String STARTS_FROM_NOTSET = "STARTS_FROM_NOTSET";
    public static final String ENDS_ON_NOTSET = "ENDS_ON_NOTSET";
    public static final String STARTS_FROM_NOT_APPLICABLE = "NOT_APPLICABLE";
    public static final String ENDS_ON_NOT_APPLICABLE = "NOT_APPLICABLE";
    public static final String REGNDATETIME_NOTSET = "REGNDATETIME_NOTSET";
    public static final String STARTDATETIME_NOTSET = "STARTDATETIME_NOTSET";
    public static final String ENDDATETIME_NOTSET = "ENDDATETIME_NOTSET";
    public static final String APPROVAL_PENDING = "PENDING";
    public static final String APPROVAL_ACCEPTED = "ACCEPTED";
    public static final String APPROVAL_REJECTED = "REJECTED";
    public static final String APPROVAL_NOT_APPLICABLE = "APPROVAL_NOT_APPLICABLE";
    public static final String STATUS_NOT_APPLICABLE = "STATUS_NOT_APPLICABLE";
    public static final String CLUB_NAME_NOT_APPLICABLE = "CLUB_NAME_NOT_APPLICABLE";
    public static final String STATUS_CREATED = "created";
    public static final String STATUS_EDITED = "edited";
    public static final String STATUS_DELETED = "deleted";
    public static final String FROM_INTENT_EXTRAS = "FROM";
    public static final String FROM_APPROVAL_NOTIFICATIONS = "APPROVAL_FRAGMENT";
    public static final String FROM_EVENTS_HOME = "EVENTS_HOME";
    public static final String FROM_EDITED_NOTIFICATIONS = "EDITED_ACCEPTED_NOTIFICATIONS";
    public static final String FROM_DELETED_NOTIFICATIONS = "DELETED_ACCEPTED_EVENTS_NOTIFICATIONS";
    public static float SCALE = (float) 5.0; // to scale up , one minute is shown task_input_bg SCALE px's
    public static int GESTURE_AREA_UP_DIP = 200;
    public static int GESTURE_AREA_DOWN_DIP = 35;
    public static int POWER_SCROLL_MINUTES = 1440;
    public static int TIME_TICKER_TEXT_SIZE = 30;
    public static int GREET_HEIGHT_DP = 35; // THIS should be the height of the greet area in task and event home
    public static JSONArray $HOME;
    public static String GROUP_FAMILY_TREE_JSON_OBJECT = "http://10.9.64.13/eeVeeWeb/JSONconverter/groupFamilyTreeToJSON.php";
    public static String GROUP_TABLE_JSON_OBJECT = "http://10.9.64.13/eeVeeWeb/JSONconverter/convertGivenEventsTableIntoJSON.php";

    public static class EVENT {

        public static int MAX_FUTURE_TIME_ON_DISPLAY = 0;
        public static DateTime LAST_END_DATETIME;
        public static int HEIGHT = 100; // in px
        public static int PAST_DISPLAY_TIME_MIN = 120; // this many minutes in past is shown
        public static int ORIGIN = (int) (PAST_DISPLAY_TIME_MIN * SCALE);
        public static DateTime NR_LAST_END_DATETIME;
    }

    public static class TASK {

        public static int MAX_FUTURE_TIME_ON_DISPLAY = 0;
        public static DateTime LAST_END_DATETIME;
        public static int HEIGHT = 100; // in px
        public static int PAST_DISPLAY_TIME_MIN = 120; // this many minutes in past is shown
        public static int ORIGIN = (int) (PAST_DISPLAY_TIME_MIN * SCALE);
        public static DateTime NR_LAST_END_DATETIME;
    }

    public static class FILTERS {
        public static String[] clubFilters = {"Academics", "Sports", "Cultural", "STAB", "HostelAffairs",
                "MoodIndigo", "Techfest", "Ecell", "SARC", "Insight"};
        public static String[] typeFilters = {"Academics", "Sports", "Technical", "Cultural", "Social", "Entrepreneurship", "Misc"};
    }
}