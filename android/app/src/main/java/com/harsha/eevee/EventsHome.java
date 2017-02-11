package com.harsha.eevee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import static java.lang.StrictMath.max;

public class EventsHome extends AppCompatActivity implements GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private static final int UI_ANIMATION_DELAY = 500;

    private final Handler mHideHandler = new Handler();
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    public UIManager uiManager;
    public int EventLongClickedID;
    public String TableName;
    private boolean mVisible;
    private View fullScreenToggle;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new task_input_bg of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, task_input_bg they are inlined
            // at compile-time and do nothing on earlier devices.
            fullScreenToggle.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private GestureDetectorCompat gestureDetector;
    private boolean gestureAreaUp = false;
    private int gestureAreaUp_dip = Constants.GESTURE_AREA_UP_DIP;
    private int gestureAreaDown_dip = Constants.GESTURE_AREA_DOWN_DIP;
    private Handler gestureAreaHandler = new Handler();
    private ImageButton overlaps_events_button;
    private ImageButton filtersEventBTN;
    private boolean overlapsAreDisplayed = false;
    private Map<String, Boolean> clubFilters;
    private Map<String, Boolean> typeFilters;

    //Instance Variables Finish -------------- Methods Start

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EventsHome.this, Home.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events_home);

        mVisible = true;

        fullScreenToggle = findViewById(R.id.greet_events);
        // Set up the user interaction to manually show or hide the system UI.


        //Gestures Setting
        gestureDetector = new GestureDetectorCompat(this, this);
        gestureDetector.setOnDoubleTapListener(this);

        Button greet_events = (Button) findViewById(R.id.greet_events);
        assert greet_events != null;
        greet_events.setText("Hey " + UserDetails.$USERNAME + ", here's all your events");
    }

    private void myUpdateOperation() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                WebEventDetailsDBHandler web = new WebEventDetailsDBHandler(EventsHome.this, null, null, 1);
                OnlineEventDetailsDBHandler online = new OnlineEventDetailsDBHandler(EventsHome.this, null, null, 1);

                WebEventsFetcher i = new WebEventsFetcher(web, EventsHome.this);
                i.fetchDataIntoServerResponse();
                i.fillUpIntoWebEventsDataTable();

                SyncEvents syncEvents = new SyncEvents(web, online, EventsHome.this);
//                    syncEvents.completePush();
                syncEvents.startPushing();

            }
        };

        Thread t = new Thread(r);
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            Toast.makeText(EventsHome.this, "Checking cloud ...", Toast.LENGTH_LONG).show();
            t.start();
        } else {
            Toast.makeText(this, "Network not available", Toast.LENGTH_SHORT).show();
        }
        LinearLayout EventPanel = (LinearLayout) findViewById(R.id.EventPanel);
        assert EventPanel != null;
        EventPanel.removeAllViews();
        UIManager uiManager = new UIManager();
        uiManager.display();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        EventLongClickedID = (int) v.getTag();
        TableName = (String) v.getContentDescription();
        super.onCreateContextMenu(menu, v, menuInfo);
        if (TableName.matches("offlineEventsData")) {
            menu.setHeaderTitle("What next!! (Offline Event)");
            getMenuInflater().inflate(R.menu.edit_view_delete_context_menu, menu);
        } else {
            menu.setHeaderTitle("What next!! (Online Event)");
            getMenuInflater().inflate(R.menu.view_ignore_online_event_context_menu, menu);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (TableName.matches("offlineEventsData")) {
            switch (item.getItemId()) {
                case R.id.viewOrEditItem:
                    Intent intent = new Intent(this, EditAndViewEvent.class);
                    intent.putExtra("id", EventLongClickedID);
                    startActivity(intent);
                    return true;
                case R.id.deleteItem:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Confirm your action");
                    builder.setInverseBackgroundForced(true);
                    builder.setMessage("Are you sure about deleting this?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            uiManager.dbHandlerOffline.deleteEvent(EventLongClickedID);
                            dialog.dismiss();
                            LinearLayout EventPanel = (LinearLayout) findViewById(R.id.EventPanel);
                            assert EventPanel != null;
                            EventPanel.removeAllViews();
                            UIManager uiManager = new UIManager();
                            uiManager.findLastEndingEventAndSetValues();
                            uiManager.display();
                            Toast.makeText(EventsHome.this, "Hey your event was deleted successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        } else {
            switch (item.getItemId()) {
                case R.id.viewOnlyItem:
                    Intent intent = new Intent(this, ViewOnlineEvent.class);
                    intent.putExtra("id", EventLongClickedID);
                    intent.putExtra(Constants.FROM_INTENT_EXTRAS, Constants.FROM_EVENTS_HOME);
                    startActivity(intent);
                    return true;
                case R.id.rejectItem:
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);

                    builder.setTitle("Confirm your action");
                    builder.setInverseBackgroundForced(true);

                    builder.setMessage("Are you sure about ignoring this?");

                    builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            uiManager.dbHandlerOnline.ignoreEvent(EventLongClickedID);
                            dialog.dismiss();
                            LinearLayout EventPanel = (LinearLayout) findViewById(R.id.EventPanel);
                            assert EventPanel != null;
                            EventPanel.removeAllViews();
                            UIManager uiManager = new UIManager();
                            uiManager.display();

                            Toast.makeText(EventsHome.this, "Hey your event was ignored successfully", Toast.LENGTH_SHORT).show();
                        }
                    });

                    builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Do nothing
                            dialog.dismiss();
                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                    return true;
                default:
                    return super.onContextItemSelected(item);
            }
        }
    }

    @Override
    protected void onStart() {
        delayedHide(500);
        super.onStart();

        clubFilters = new HashMap();
        typeFilters = new HashMap();
        setUpFilterMaps();

        //Setting past time
        setPastRegion();

        //Displaying Tasks
        uiManager = new UIManager();
        uiManager.display();

        overlaps_events_button = (ImageButton) findViewById(R.id.overlaps_events_button);
        overlaps_events_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!overlapsAreDisplayed) {
                    uiManager.showOverlaps();
                    overlapsAreDisplayed = !overlapsAreDisplayed;
                } else {
                    Toast.makeText(EventsHome.this, "Hiding Overlaps", Toast.LENGTH_SHORT).show();
                    uiManager.removeAllOverlaps();
                    overlapsAreDisplayed = !overlapsAreDisplayed;
                }
            }
        });

        overlaps_events_button.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EventsHome.this, "Tap to show Overlaps", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        filtersEventBTN = (ImageButton) findViewById(R.id.filtersEventBTN);
        filtersEventBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(EventsHome.this, SetEventFilters.class);
                nextActivity.putExtra("TO FILTERS ACTIVITY", "FROM EVENTS_HOME");
                startActivity(nextActivity);
            }
        });

        filtersEventBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(EventsHome.this, "Tap to set filters", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setPastRegion() {
        int WIDTH_OF_PAST_REGION = Constants.EVENT.ORIGIN;
        TextView pastRegionEvents = (TextView) findViewById(R.id.pastRegionEvents);
        assert pastRegionEvents != null;
        ViewGroup.LayoutParams params = pastRegionEvents.getLayoutParams();
        params.width = WIDTH_OF_PAST_REGION;
        pastRegionEvents.setLayoutParams(params);

        final HorizontalScrollView H_scroll_events = (HorizontalScrollView) findViewById(R.id.H_scroll_events);
        assert H_scroll_events != null;
        H_scroll_events.post(new Runnable() {
            @Override
            public void run() {
                Log.i("scroll", "eventsScroll");
                H_scroll_events.scrollTo(Constants.EVENT.ORIGIN, 0);
            }
        });
    }

    public void setTimeDivisions() {
        TimeDivisions setter = (TimeDivisions) findViewById(R.id.timedivisions_events);

        RelativeLayout.LayoutParams setterParams = (RelativeLayout.LayoutParams) setter.getLayoutParams();
        int WIDTH = (int) ((Constants.EVENT.MAX_FUTURE_TIME_ON_DISPLAY + Constants.EVENT.PAST_DISPLAY_TIME_MIN) * Constants.SCALE);
        setterParams.width = WIDTH;
        setter.setLayoutParams(setterParams);

    }

    private void gestureAreaUp() {
        Runnable up = new Runnable() {
            @Override
            public void run() {
                Resources r = getResources();
                int greet_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.GREET_HEIGHT_DP, r.getDisplayMetrics());

                RelativeLayout Events = (RelativeLayout) findViewById(R.id.Events);
//                TransitionManager.beginDelayedTransition(Events);

                //Getting gestureAreaEvents and Events_main_parent
                RelativeLayout gestureAreaEvents = (RelativeLayout) findViewById(R.id.gestureAreaEvents);
                assert gestureAreaEvents != null;
                RelativeLayout Events_main_parent = (RelativeLayout) findViewById(R.id.Events_main_parent);
                assert Events_main_parent != null;

                //Getting gestureAreaEvents and Events_main_parent layout parameters
                RelativeLayout.LayoutParams params_GESTURE_AREA = (RelativeLayout.LayoutParams) gestureAreaEvents.getLayoutParams();
                RelativeLayout.LayoutParams params_EVENTS_MAIN_PARENT = (RelativeLayout.LayoutParams) Events_main_parent.getLayoutParams();

                int up_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gestureAreaUp_dip, r.getDisplayMetrics());

                if (!gestureAreaUp) {
                    //Increasing Height of gestureAreaEvents and bottom margin of Events_main_parent
                    params_GESTURE_AREA.height = up_px;
                    params_EVENTS_MAIN_PARENT.setMargins(0, greet_height, 0, up_px);
                    gestureAreaEvents.setLayoutParams(params_GESTURE_AREA);
                    Events_main_parent.setLayoutParams(params_EVENTS_MAIN_PARENT);

                    //Changing state of gesture area
                    gestureAreaUp = !gestureAreaUp;
                }
            }
        };

        gestureAreaHandler.postDelayed(up, 0);
    }

    private void gestureAreaDown() {
        Runnable down = new Runnable() {
            @Override
            public void run() {
                Resources r = getResources();
                int greet_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.GREET_HEIGHT_DP, r.getDisplayMetrics());


                RelativeLayout Events = (RelativeLayout) findViewById(R.id.Events);
//                TransitionManager.beginDelayedTransition(Events);

                //Getting gestureAreaEvents and Events_main_parent
                RelativeLayout gestureAreaEvents = (RelativeLayout) findViewById(R.id.gestureAreaEvents);
                assert gestureAreaEvents != null;
                RelativeLayout Events_main_parent = (RelativeLayout) findViewById(R.id.Events_main_parent);
                assert Events_main_parent != null;

                //Getting gestureAreaEvents and Events_main_parent layout parameters
                RelativeLayout.LayoutParams params_GESTURE_AREA = (RelativeLayout.LayoutParams) gestureAreaEvents.getLayoutParams();
                RelativeLayout.LayoutParams params_EVENTS_MAIN_PARENT = (RelativeLayout.LayoutParams) Events_main_parent.getLayoutParams();

                int down_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gestureAreaDown_dip, r.getDisplayMetrics());

                if (gestureAreaUp) {
                    //Increasing Height of gestureAreaEvents and bottom margin of Events_main_parent
                    params_GESTURE_AREA.height = down_px;
                    params_EVENTS_MAIN_PARENT.setMargins(0, greet_height, 0, down_px);
                    gestureAreaEvents.setLayoutParams(params_GESTURE_AREA);
                    Events_main_parent.setLayoutParams(params_EVENTS_MAIN_PARENT);

                    //Changing state of gesture area
                    gestureAreaUp = !gestureAreaUp;
                }
            }
        };

        gestureAreaHandler.postDelayed(down, 0);
    }

    private void toggleGestureArea() {

        Runnable toggle = new Runnable() {
            @Override
            public void run() {
                Resources r = getResources();
                int greet_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.GREET_HEIGHT_DP, r.getDisplayMetrics());

                RelativeLayout Events = (RelativeLayout) findViewById(R.id.Events);
//                TransitionManager.beginDelayedTransition(Events);

                //Getting gestureAreaEvents and Events_main_parent
                RelativeLayout gestureAreaEvents = (RelativeLayout) findViewById(R.id.gestureAreaEvents);
                assert gestureAreaEvents != null;
                RelativeLayout Events_main_parent = (RelativeLayout) findViewById(R.id.Events_main_parent);
                assert Events_main_parent != null;

                //Getting gestureAreaEvents and Events_main_parent layout parameters
                RelativeLayout.LayoutParams params_GESTURE_AREA = (RelativeLayout.LayoutParams) gestureAreaEvents.getLayoutParams();
                RelativeLayout.LayoutParams params_EVENTS_MAIN_PARENT = (RelativeLayout.LayoutParams) Events_main_parent.getLayoutParams();

                int down_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gestureAreaDown_dip, r.getDisplayMetrics());
                int up_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gestureAreaUp_dip, r.getDisplayMetrics());

                if (!gestureAreaUp) {
                    //Increasing Height of gestureAreaEvents and bottom margin of Events_main_parent
                    params_GESTURE_AREA.height = up_px;
                    params_EVENTS_MAIN_PARENT.setMargins(0, greet_height, 0, up_px);
                    gestureAreaEvents.setLayoutParams(params_GESTURE_AREA);
                    Events_main_parent.setLayoutParams(params_EVENTS_MAIN_PARENT);

                    //Changing state of gesture area
                    gestureAreaUp = !gestureAreaUp;
                } else if (gestureAreaUp) {
                    //Increasing Height of gestureAreaEvents and bottom margin of Events_main_parent
                    params_GESTURE_AREA.height = down_px;
                    params_EVENTS_MAIN_PARENT.setMargins(0, greet_height, 0, down_px);
                    gestureAreaEvents.setLayoutParams(params_GESTURE_AREA);
                    Events_main_parent.setLayoutParams(params_EVENTS_MAIN_PARENT);

                    //Changing state of gesture area
                    gestureAreaUp = !gestureAreaUp;
                }
            }
        };

        gestureAreaHandler.postDelayed(toggle, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        LinearLayout EventPanel = (LinearLayout) findViewById(R.id.EventPanel);
        assert EventPanel != null;
        EventPanel.removeAllViews();
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        fullScreenToggle.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    //Gestures start here...
    @Override
    public boolean onSingleTapConfirmed(MotionEvent e) {
        Log.i("gesture", "onSingleTapConfirmed");
        return false;
    }

    @Override
    public boolean onDoubleTap(MotionEvent e) {
        Log.i("gesture", "onDoubleTap");
        Intent i = new Intent(this, EventInput.class);
        startActivity(i);
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent e) {
        Log.i("gesture", "onDoubleTapEvent");
        return false;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        Log.i("gesture", "onDown");
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {
        Log.i("gesture", "onShowPress");
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Log.i("gesture", "onSingleTapUp");
        toggleGestureArea();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Log.i("gesture", "onScroll");
        return true;
    }

    @Override
    public void onLongPress(MotionEvent e) {
        Log.i("gesture", "onLongPress");
        Intent GoTasks = new Intent(this, TasksHome.class);
        startActivity(GoTasks);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i("gesture", "onFling");
        Log.i("analysis", e1.toString() + " , " + e2.toString());

        final HorizontalScrollView H_scroll_events = (HorizontalScrollView) findViewById(R.id.H_scroll_events);

        int scrollBy_Min = Constants.POWER_SCROLL_MINUTES;
        final int scrollBy_Width = (int) (scrollBy_Min * Constants.SCALE);

        float startX = e1.getX();
        float endX = e2.getX();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int minScroll = displayMetrics.widthPixels / 5;

        if ((endX - startX) < -minScroll) {

            H_scroll_events.post(new Runnable() {
                @Override
                public void run() {
                    H_scroll_events.scrollBy(scrollBy_Width, 0);
                }
            });
        } else if ((endX - startX) > minScroll) {

            H_scroll_events.post(new Runnable() {
                @Override
                public void run() {
                    H_scroll_events.scrollBy(-scrollBy_Width, 0);
                }
            });
        }

        return true;
    }

    //Gestures end here...
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gestureDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    private void setUpFilterMaps() {
        int clubFiltersLength = Constants.FILTERS.clubFilters.length;
        int typeFiltersLength = Constants.FILTERS.typeFilters.length;
        EventFiltersDBHandler dbHandler = new EventFiltersDBHandler(this, null, null, 1);
        for (int i = 0; i < clubFiltersLength; i++) {
            clubFilters.put(Constants.FILTERS.clubFilters[i], dbHandler.isFilterChecked(i + 1));
        }

        for (int i = 0; i < typeFiltersLength; i++) {
            typeFilters.put(Constants.FILTERS.typeFilters[i], dbHandler.isFilterChecked(clubFiltersLength + i + 1));
        }
    }

    public class EventViewCompact {

        // TODO : use dip properly so task_input_bg to size events properly
        // TODO : make the scale more statistical so task_input_bg to make a good representation
        // TODO : keep an origin variable so task_input_bg to show some of the past time


        public String TABLE_NAME; // From Offline Table or Online

        public int ID;
        public String NAME;
        public String PLACE;
        public DateTime TS, START, END, REG;
        public String REPETITION; // Seven 1's and 0's starting from Sunday
        public DateTime STARTS_FROM, ENDS_ON;

        public String APPROVAL;
        public String STATUS;
        public String TYPE;
        public String CLUB_NAME;

        public int LEFT_MARGIN; // can be negative

        public Vector<DateTime> START_REPEATING = new Vector<>();
        public Vector<Integer> LEFT_MARGINS_REPEATING = new Vector<>();

        public int WIDTH; // in px


        public EventViewCompact(String eventInfo) {

            // TODO : Inspect what all are empty and correspondingly represent them to user like REG , START AND END (If they are missing then they can be represented on a different activity)

            String[] eventDetail = eventInfo.split(",");

            // Initializing
            DateTime _TS, _START, _END, _REG;

            // Setting ID
            ID = Integer.parseInt(eventDetail[0]);

            //Setting TS
            _TS = new DateTime(eventDetail[1]);
            TS = _TS;

            //Setting Description
            NAME = eventDetail[2];

            //Setting Tag
            PLACE = eventDetail[3];

            //Setting StartTime
            _START = new DateTime(eventDetail[4]);
            START = _START;

            //Setting EndTime
            _END = new DateTime(eventDetail[5]);
            END = _END;

            //Setting Repetition
            REPETITION = eventDetail[6];

            //Setting RegTime
            _REG = new DateTime(eventDetail[7]);
            REG = _REG;

            //Setting Starts From
            STARTS_FROM = new DateTime(eventDetail[8]);

            //Setting Ends On
            ENDS_ON = new DateTime(eventDetail[9]);

            // Setting Table name
            TABLE_NAME = eventDetail[10];

            // Setting Approval and Status
            if (eventDetail.length > 12 && TABLE_NAME.matches(OnlineEventDetailsDBHandler.TABLE_NAME)) {
                // Setting Approval
                APPROVAL = eventDetail[11];

                // Setting Status
                STATUS = eventDetail[12];

                // Setting Type
                TYPE = eventDetail[13];

                // Setting Club Name
                CLUB_NAME = eventDetail[14];
            } else {
                APPROVAL = Constants.APPROVAL_NOT_APPLICABLE;
                STATUS = Constants.STATUS_NOT_APPLICABLE;
                CLUB_NAME = Constants.CLUB_NAME_NOT_APPLICABLE;

                // Setting Type
                TYPE = eventDetail[11];
            }

            // Initialized
        }

        public void setWIDTH() {
            // ASSUMPTION : END is after START from validation

            if (START != null && END != null) {

                int diffInMin = DateTime.differenceInMinWithSign(START, END);
                WIDTH = (int) (diffInMin * Constants.SCALE);

            } else {
                Log.i("EventUISetupError", "the START and END are uninitialized");
            }


        }

        public void setLEFT_MARGIN() {
            //SETS LEFT MARGIN TO START OF THE TASK
            DateTime present = new DateTime(true);

            if (REPETITION.matches("0000000")) {
                if (START != null && END != null) {
                    int diffInMin = DateTime.differenceInMinWithSign(present, START);
                    LEFT_MARGIN = (int) (Constants.EVENT.ORIGIN + diffInMin * Constants.SCALE);
                } else {
                    Log.i("EventUISetupError", "  the START and END are uninitialized");
                }
            } else {
                DateTime prevDateTime = new DateTime(true);
                prevDateTime = DateTime.getDateTime(prevDateTime, -1 * Constants.EVENT.PAST_DISPLAY_TIME_MIN);

                for (int i = 0; i < START_REPEATING.size(); i++) {
                    int marginFromPrevious = DateTime.differenceInMinWithSign(prevDateTime, START_REPEATING.get(i));
                    if (i != 0) {
                        marginFromPrevious -= WIDTH / Constants.SCALE;
                    }
                    LEFT_MARGINS_REPEATING.add((int) (marginFromPrevious * Constants.SCALE));
                    prevDateTime = START_REPEATING.get(i);
                }
            }
        }

        public void displayYourself() {

            LinearLayout EventPanel = (LinearLayout) findViewById(R.id.EventPanel);

            // For non repeating events

            if (REPETITION.matches("0000000")) {

                // TODO : flag the reg deadline

                LinearLayout row = new LinearLayout(EventsHome.this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                EventPanel.addView(row, rowParams);

                setWIDTH();
                setLEFT_MARGIN();
                //ALL VARIABLES SET
                Log.i("event", "eventwidth" + String.valueOf(WIDTH));
                Log.i("event", "eventmargin" + String.valueOf(LEFT_MARGIN));

                Button event = new Button(EventsHome.this);
                event.setText(NAME);
                event.setTextSize(10);

                LinearLayout.LayoutParams eventParams = new LinearLayout.LayoutParams(WIDTH, Constants.EVENT.HEIGHT);
                eventParams.setMargins(LEFT_MARGIN, 0, 0, 0);

                row.addView(event, eventParams);

                // Setting the listeners
                event.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), String.valueOf(NAME), Toast.LENGTH_SHORT).show();
                    }
                });

                registerForContextMenu(event);
                event.setTag(ID);
                event.setContentDescription(TABLE_NAME);
            } else {

                LinearLayout row = new LinearLayout(EventsHome.this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                EventPanel.addView(row, rowParams);

                // START_REPEATING vector of start date times
                DateTime presentDT = new DateTime(true);
                presentDT = DateTime.getDateTime(presentDT, (-1) * Constants.EVENT.PAST_DISPLAY_TIME_MIN);

                String[] weekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
                boolean[] weekDaysChecked = new boolean[7];
                for (int i = 0; i < 7; i++) {
                    if (String.valueOf(REPETITION.charAt(i)).matches("0"))
                        weekDaysChecked[i] = false;
                    else weekDaysChecked[i] = true;
                }

                int dayRef = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("EE");
                Date d = new Date(presentDT.YEAR - 1900, presentDT.MONTH - 1, presentDT.DAY);
                String dayOfTheWeek = sdf.format(d);
                while (true) {
                    if (weekDays[dayRef].matches(dayOfTheWeek)) break;
                    dayRef++;
                }

                DateTime startDateTime = new DateTime(START);
                TimeInput startTime = new TimeInput();
                startDateTime.setTimeInput(startTime);
                DateTime endDateTime = new DateTime(END);
                TimeInput endTime = new TimeInput();
                endDateTime.setTimeInput(endTime);
                TimeInput presentTime = new TimeInput();
                presentDT.setTimeInput(presentTime);

                int diffDays = 0;
                if (!TimeInput.isGreater(presentTime, startTime)) {
                    diffDays = 1;
                    dayRef--;
                    if (dayRef < 0) dayRef += 7;
                }
                while (!weekDaysChecked[dayRef]) {
                    dayRef--;
                    diffDays++;
                    if (dayRef < 0) dayRef += 7;
                }

                DateTime StartDT;
                StartDT = DateTime.getDateTime(presentDT, -1 * diffDays * DateTime.MINS_IN_DAY);
                StartDT.HOUR = startTime.selectedHour;
                StartDT.MINUTE = startTime.selectedMin;
                DateTime compareDT;
                if (DateTime.isGreater(Constants.EVENT.NR_LAST_END_DATETIME, ENDS_ON)) {
                    compareDT = Constants.EVENT.NR_LAST_END_DATETIME;
                } else compareDT = ENDS_ON;
                while (DateTime.isGreater(compareDT, StartDT)) {
                    int sum = 0;
                    START_REPEATING.add(StartDT);
                    dayRef++;
                    sum++;
                    if (dayRef == 7) dayRef = 0;
                    while (!weekDaysChecked[dayRef]) {
                        dayRef++;
                        sum++;
                        if (dayRef == 7) dayRef = 0;
                    }
                    StartDT = DateTime.getDateTime(StartDT, sum * DateTime.MINS_IN_DAY);
                }
                // START_REPEATING vector of start date times

                setWIDTH();
                setLEFT_MARGIN();
                //ALL VARIABLES SET
                Log.i("event", "eventwidth" + String.valueOf(WIDTH));
                Log.i("event", "eventmargin" + String.valueOf(LEFT_MARGIN));

                Vector<Button> buttons = new Vector<>();

                for (int i = 0; i < START_REPEATING.size(); i++) {
                    Button source = new Button(EventsHome.this);
                    source.setText(NAME);
                    source.setTextSize(10);

                    Log.i("harsha", String.valueOf(START_REPEATING.get(i)));

                    LinearLayout.LayoutParams eventParams = new LinearLayout.LayoutParams(WIDTH, Constants.EVENT.HEIGHT);
                    eventParams.setMargins(LEFT_MARGINS_REPEATING.get(i), 0, 0, 0);
                    row.addView(source, eventParams);

                    // Setting the listeners
                    source.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), String.valueOf(NAME), Toast.LENGTH_SHORT).show();
                        }
                    });

                    buttons.add(source);
                    registerForContextMenu(source);
                    source.setTag(ID);
                    source.setContentDescription(TABLE_NAME);
                }
            }
        }

        // END OF CLASS
    }

    public class UIManager {
        public Vector<String> nonRepeating = new Vector<>();    // have both ST AND ET
        public Vector<String> daily = new Vector<>();           // have both ST AND ET and occurs daily
        public Vector<String> weekly = new Vector<>();          // have both ST AND ET and occurs weekly
        public Vector<String> nonRepeatingSorted = new Vector<>(); // have both ST AND ET
        public Vector<String> repeatingSorted = new Vector<>();   // have ET
        public int noEventsOffline = 0;
        public int noEventsOnline = 0;
        EventDetailsDBHandler dbHandlerOffline = new EventDetailsDBHandler(EventsHome.this, null, null, 1);
        OnlineEventDetailsDBHandler dbHandlerOnline = new OnlineEventDetailsDBHandler(EventsHome.this, null, null, 1);
        private String allOfflineEventsInfo = "";
        private String allOnlineEventsInfo = "";

        private Vector<DateTime> startHelper = new Vector<>();
        private Vector<DateTime> endHelper = new Vector<>();
        private Vector<DateTime> startIntersection = new Vector<>();
        private Vector<DateTime> endIntersection = new Vector<>();

        private boolean NREventsSorted = false;
        private boolean overlapThreadRunning = false;

        public UIManager() {
            //delete all outdated events
            dbHandlerOffline.deleteOutdatedEvents(); // TODO : implement for daily and weekly
            dbHandlerOnline.deleteOutdatedEvents();

            //get the no of events (which are not outdated)
            noEventsOffline = dbHandlerOffline.getCount();
            noEventsOnline = dbHandlerOnline.getCount();

            //get all the events in a raw string form
            allOfflineEventsInfo = dbHandlerOffline.returnReqInfo();
            allOnlineEventsInfo = dbHandlerOnline.returnReqInfo();

            Log.i("uidisplay", allOnlineEventsInfo);

            //Categorize the main string into six vector var categories
            categorizeOfflineEventsIntoVectorVar();
            Log.i("category", "  nonRepeating" + nonRepeating.toString() + "\n daily" + daily.toString() + "\n weekly" + weekly.toString());
            categorizeOnlineEventsIntoVectorVar();

            //Make nonRepeating Sorted List
            sortIntoNonRepeatingSorted();
            Log.i("category", "NonRepeatingSortedToString" + nonRepeatingSorted.toString());

            //Make repeating Sorted List
            sortIntoRepeatingSorted();
            Log.i("category", "  repeatingSortedToString" + repeatingSorted.toString());

            //Find maxFuture time displayed :: should be called only after sorting functions are called
            findLastEndingEventAndSetValues();

        }

        private void categorizeOfflineEventsIntoVectorVar() {
            if (allOfflineEventsInfo.matches("")) return;

            String[] offlineEvent = allOfflineEventsInfo.split("/");
            for (int i = 0; i < offlineEvent.length; i++) {
                String[] eventDetail = offlineEvent[i].split(",");
                String eventType = eventDetail[11];

                if(typeFilters.get(eventType)){
                    if (eventDetail[6].matches("1111111"))                                                    // daily
                    {
                        daily.add(offlineEvent[i]);
                    } else if (!eventDetail[6].matches("1111111") && !eventDetail[6].matches("0000000"))         //weekly
                    {
                        weekly.add(offlineEvent[i]);
                    } else if (eventDetail[6].matches("0000000"))                                               //once
                    {
                        nonRepeating.add(offlineEvent[i]);
                    }
                }
            }
        }

        private void categorizeOnlineEventsIntoVectorVar() {
            if (allOnlineEventsInfo.matches("")) return;

            String[] onlineEvent = allOnlineEventsInfo.split("/");

            for (int i = 0; i < onlineEvent.length; i++) {
                String[] eventDetail = onlineEvent[i].split(",");

                String eventApproval = eventDetail[11];
                String eventStatus = eventDetail[12];
                String eventClubName = eventDetail[14];

                if (eventApproval.matches(Constants.APPROVAL_ACCEPTED) && !eventStatus.matches(Constants.STATUS_DELETED)
                        && clubFilters.get(eventClubName)) {
                    if (eventDetail[6].matches("1111111"))                                                    // daily
                    {
                        daily.add(onlineEvent[i]);
                    } else if (!eventDetail[6].matches("1111111") && !eventDetail[6].matches("0000000"))      //weekly
                    {
                        weekly.add(onlineEvent[i]);
                    } else if (eventDetail[6].matches("0000000"))                                             //once
                    {
                        nonRepeating.add(onlineEvent[i]);
                    }
                }
            }
        }

        private void sortIntoNonRepeatingSorted() {
            int nonRepeatingSize = nonRepeating.size();
            if (nonRepeatingSize == 0) return;

            nonRepeatingSorted.add(nonRepeating.get(0)); // first variable inserted task_input_bg the nonRepeatingSorted is empty

            // Note the start index is 1
            for (int i = 1; i < nonRepeatingSize; i++)  // looping through nonRepeating and adding to nonRepeatingSorted
            {
                String iTH = nonRepeating.get(i);
                String[] iTHEventDetail = iTH.split(",");
                DateTime iTHEventStartDateTime = new DateTime(iTHEventDetail[4]); //sorting according to start Datetime

                int nonRepeatingSortedSize = nonRepeatingSorted.size();
                for (int j = 0; j < nonRepeatingSortedSize; j++) {
                    String jTH = nonRepeatingSorted.get(j);
                    String[] jTHEventDetail = jTH.split(",");
                    DateTime jTHEventStartDateTime = new DateTime(jTHEventDetail[4]);

                    boolean iBEFOREj = DateTime.isGreater(jTHEventStartDateTime, iTHEventStartDateTime);
                    if (iBEFOREj) {
                        nonRepeatingSorted.insertElementAt(iTH, j);
                        break;
                    } else if (j == nonRepeatingSortedSize - 1) {
                        nonRepeatingSorted.add(iTH);
                    }
                }
            }
            NREventsSorted = true;
        }

        //TODO : Think about the daily and weekly , features it should support and the input and table part
        private void sortIntoRepeatingSorted() {
            int dailySize = daily.size();
            int weeklySize = weekly.size();

            int dailyStartIterator;
            int weeklyStartIterator;

            if (dailySize == 0 && weeklySize == 0) return;
            else if (dailySize == 0 && weeklySize != 0) {
                repeatingSorted.add(weekly.get(0));         // first variable inserted task_input_bg the repeatingSorted is empty
                weeklyStartIterator = 1;
                dailyStartIterator = 0;
            } else if (dailySize != 0 && weeklySize == 0) {
                repeatingSorted.add(daily.get(0));          // first variable inserted task_input_bg the repeatingSorted is empty
                dailyStartIterator = 1;
                weeklyStartIterator = 0;
            } else {
                repeatingSorted.add(daily.get(0));           // first variable inserted task_input_bg the repeatingSorted is empty
                dailyStartIterator = 1;
                weeklyStartIterator = 0;
            }

            // Note the start index is variable
            for (int i = dailyStartIterator; i < dailySize; i++)   // looping through daily and adding to repeatingSorted
            {
                String iTH = daily.get(i);
                String[] iTHEventDetail = iTH.split(",");
                DateTime iTHEventStartDateTime = new DateTime(iTHEventDetail[4]);

                int repeatingSortedSize = repeatingSorted.size();
                for (int j = 0; j < repeatingSortedSize; j++) {
                    String jTH = repeatingSorted.get(j);
                    String[] jTHEventDetail = jTH.split(",");
                    DateTime jTHEventStartDateTime = new DateTime(jTHEventDetail[4]);

                    boolean iBEFOREj = DateTime.isGreater(jTHEventStartDateTime, iTHEventStartDateTime);
                    if (iBEFOREj) {
                        repeatingSorted.insertElementAt(iTH, j);
                        break;
                    } else if (j == repeatingSortedSize - 1) {
                        repeatingSorted.add(iTH);
                    }
                }

            }

            // Note the start index is variable
            for (int i = weeklyStartIterator; i < weeklySize; i++)  // looping through deadlined and adding to constrainedAndDeadlinedSorted
            {
                String iTH = weekly.get(i);
                String[] iTHEventDetail = iTH.split(",");
                DateTime iTHEventStartDateTime = new DateTime(iTHEventDetail[4]);

                int repeatingSortedSize = repeatingSorted.size();
                for (int j = 0; j < repeatingSortedSize; j++) {
                    String jTH = repeatingSorted.get(j);
                    String[] jTHEventDetail = jTH.split(",");
                    DateTime jTHEventStartDateTime = new DateTime(jTHEventDetail[4]);

                    boolean iBEFOREj = DateTime.isGreater(jTHEventStartDateTime, iTHEventStartDateTime);
                    if (iBEFOREj) {
                        repeatingSorted.insertElementAt(iTH, j);
                        break;
                    } else if (j == repeatingSortedSize - 1) {
                        repeatingSorted.add(iTH);
                    }
                }
            }
        }

        private void findLastEndingEventAndSetValues() {
            int nonRepeatingSize = nonRepeating.size();
            int repeatingSize = daily.size() + weekly.size();

            DateTime present = new DateTime(true);

            if (nonRepeatingSize == 0 && repeatingSize == 0) {
                Constants.EVENT.MAX_FUTURE_TIME_ON_DISPLAY = 2 * DateTime.MINS_IN_DAY;
                Constants.EVENT.LAST_END_DATETIME = DateTime.getDateTime(present, Constants.EVENT.MAX_FUTURE_TIME_ON_DISPLAY);
                Constants.EVENT.NR_LAST_END_DATETIME = Constants.EVENT.LAST_END_DATETIME;
                return;
            }

            int NR_maxEndingTimeFromPresentInMin = 0;

            for (int i = 0; i < nonRepeatingSize; i++)  // looping through nonRepeating checking for last ending event
            {
                String iTH = nonRepeating.get(i);
                String[] iTHEventDetail = iTH.split(",");
                DateTime iTHEventEndDateTime = new DateTime(iTHEventDetail[5]); //checking according to End Datetime

                int checker = DateTime.differenceInMinWithSign(present, iTHEventEndDateTime);
                if (checker > NR_maxEndingTimeFromPresentInMin) {
                    NR_maxEndingTimeFromPresentInMin = checker;
                }
            }
            // NR_maxEndingTimeFromPresentInMin is taken care of

            if (repeatingSize == 0) {
                int maxFutureTimeDisplayedEvents;
                maxFutureTimeDisplayedEvents = Math.max(2 * DateTime.MINS_IN_DAY, NR_maxEndingTimeFromPresentInMin);
                Constants.EVENT.MAX_FUTURE_TIME_ON_DISPLAY = maxFutureTimeDisplayedEvents;
                Constants.EVENT.LAST_END_DATETIME = DateTime.getDateTime(present, Constants.EVENT.MAX_FUTURE_TIME_ON_DISPLAY);
                Constants.EVENT.NR_LAST_END_DATETIME = Constants.EVENT.LAST_END_DATETIME;
                return;
            }
            // if no repeating events are there then we show a minimum of 2 days of future time
            // TODO : change this in considering with weekly and daily events
            else {
                int maxFutureTimeDisplayedEvents;
                maxFutureTimeDisplayedEvents = max(7 * DateTime.MINS_IN_DAY, NR_maxEndingTimeFromPresentInMin);
                DateTime refMax = DateTime.getDateTime(present, maxFutureTimeDisplayedEvents);
                Constants.EVENT.NR_LAST_END_DATETIME = refMax;
                for (int i = 0; i < repeatingSize; i++) {
                    refMax = getMaxDateTime(refMax, repeatingSorted.get(i));
                }
//                Vector<DateTime> maxTimes = new Vector<>();
//                for (int i =0; i < repeatingSize; i++) {
//                    maxTimes.add(getMaxDateTime(refMax, repeatingSorted.get(i)));
//                }
//
//                refMax = maxTimes.get(0);
//                for(int i=1;i<repeatingSize;i++) {
//                    refMax = DateTime.maximum(refMax,maxTimes.get(i));
//                }

                Constants.EVENT.MAX_FUTURE_TIME_ON_DISPLAY = DateTime.differenceInMin(present, refMax);
                Constants.EVENT.LAST_END_DATETIME = refMax;
                return;
            }
        }

        private DateTime getMaxDateTime(DateTime ref, String event) {
            String[] eventInfo = event.split(",");
            String repetitionAsString = eventInfo[6];
            String endsOn = eventInfo[9];
            if (!endsOn.matches(Constants.ENDS_ON_NOTSET)) {
                DateTime endsOnDT = new DateTime(endsOn);
                if (!DateTime.isGreater(endsOnDT, ref)) return ref;
            }

            String[] weekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
            boolean[] weekDaysChecked = new boolean[7];
            for (int i = 0; i < 7; i++) {
                if (String.valueOf(repetitionAsString.charAt(i)).matches("0"))
                    weekDaysChecked[i] = false;
                else weekDaysChecked[i] = true;
            }

            int dayRef = 0;
            SimpleDateFormat sdf = new SimpleDateFormat("EE");
            Date d = new Date(ref.YEAR - 1900, ref.MONTH - 1, ref.DAY);
            String dayOfTheWeek = sdf.format(d);
            while (true) {
                if (weekDays[dayRef].matches(dayOfTheWeek)) break;
                dayRef++;
            }

            DateTime startDateTime = new DateTime(eventInfo[4]);
            TimeInput startTime = new TimeInput();
            startDateTime.setTimeInput(startTime);
            DateTime endDateTime = new DateTime(eventInfo[5]);
            TimeInput endTime = new TimeInput();
            endDateTime.setTimeInput(endTime);
            TimeInput refTime = new TimeInput();
            ref.setTimeInput(refTime);

            int diffDays = 0;
            if (!TimeInput.isGreater(refTime, startTime)) {
                diffDays = 1;
                dayRef--;
                if (dayRef < 0) dayRef += 7;
            }
            while (!weekDaysChecked[dayRef]) {
                dayRef--;
                diffDays++;
                if (dayRef < 0) dayRef += 7;
            }

            int diff = DateTime.differenceInMin(startDateTime, endDateTime)
                    - (DateTime.MINS_IN_HOUR * (refTime.selectedHour - startTime.selectedHour))
                    - (refTime.selectedMin - startTime.selectedMin) - diffDays * DateTime.MINS_IN_DAY;
            if (diff > 0) {
                return DateTime.getDateTime(ref, diff);
            } else {
                return ref;
            }
        }

        private void display() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
            wm.getDefaultDisplay().getMetrics(displayMetrics);

            Constants.EVENT.HEIGHT = displayMetrics.heightPixels / 12;

            int noEvents = noEventsOffline + noEventsOnline;

            // TODO : find max display time including daily and weekly
            // TODO : use sorted list
            // TODO : display daily and weekly events

            if (noEvents != 0) {
                // First display nonRepeating Events
                int nonRepeatingSortedSize = nonRepeatingSorted.size();
                // nonRepeating array of EventViewCompact
                EventViewCompact[] eventViewCompact_NR = new EventViewCompact[nonRepeatingSortedSize];

                for (int i = 0; i < nonRepeatingSortedSize; i++) {
                    eventViewCompact_NR[i] = new EventViewCompact(nonRepeatingSorted.get(i));
                    eventViewCompact_NR[i].displayYourself();
                }

                // Then display repeating Events
                int repeatingSortedSize = repeatingSorted.size();
                // repeating array of EventViewCompact
                EventViewCompact[] eventViewCompact_R = new EventViewCompact[repeatingSortedSize];

                for (int i = 0; i < repeatingSortedSize; i++) {
                    eventViewCompact_R[i] = new EventViewCompact(repeatingSorted.get(i));
                    eventViewCompact_R[i].displayYourself();
                }

            }

            else {

            }

            setTimeDivisions();
        }

        public void showOverlaps() {
            final Handler setOverlapUI = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    Overlaps overlaps_layout = (Overlaps) findViewById(R.id.overlaps_events_layout);
                    ViewGroup.LayoutParams params = overlaps_layout.getLayoutParams();
                    params.width = (int) (((Constants.EVENT.MAX_FUTURE_TIME_ON_DISPLAY + Constants.EVENT.PAST_DISPLAY_TIME_MIN)) * Constants.SCALE);
                    overlaps_layout.setLayoutParams(params);
                    overlaps_layout.showOverlaps(startIntersection, endIntersection);
                    overlapThreadRunning = false;
                }
            };

            Runnable calculateOverlaps = new Runnable() {
                @Override
                public void run() {
                    setTheUnionAndIntersectionVectors();
                    setOverlapUI.sendEmptyMessage(0);
                }
            };

            Thread showOverlaps = new Thread(calculateOverlaps);

            if (!overlapThreadRunning) {
                showOverlaps.start();
                overlapThreadRunning = true;
                Toast.makeText(EventsHome.this, "Showing Overlaps", Toast.LENGTH_SHORT).show();
            } else {

            }


        }

        public void removeAllOverlaps() {
            Overlaps overlaps_layout = (Overlaps) findViewById(R.id.overlaps_events_layout);
            ViewGroup.LayoutParams params = overlaps_layout.getLayoutParams();
            params.width = 0;
            overlaps_layout.setLayoutParams(params);
        }

        private void setTheUnionAndIntersectionVectors() {
            addRepeatingEventsForOverlap();
            for (int i = 0; i < nonRepeatingSorted.size(); i++) {
                String iTH = nonRepeatingSorted.get(i);
                String[] iTHEventDetail = iTH.split(",");
                DateTime iTHEventStartDateTime = new DateTime(iTHEventDetail[4]); //sorting according to start Datetime
                DateTime iTHEventEndDateTime = new DateTime(iTHEventDetail[5]); //sorting according to start Datetime
                startHelper.add(iTHEventStartDateTime);
                endHelper.add(iTHEventEndDateTime);
            }
            for (int i = 0; i < startHelper.size(); i++) {
                for (int j = 0; j < i; j++) {
                    if (overlap(startHelper.get(i), endHelper.get(i), startHelper.get(j), endHelper.get(j))) {
                        startIntersection.add(DateTime.maximum(startHelper.get(i), startHelper.get(j)));
                        endIntersection.add(DateTime.minimum(endHelper.get(i), endHelper.get(j)));
                    }
                }
                modifyIntersectionVector();
            }
            startHelper = new Vector<>();
            endHelper = new Vector<>();
        }

        public boolean overlap(DateTime startDT_1, DateTime endDT_1, DateTime startDT_2, DateTime endDT_2) {
            if (!DateTime.isGreater(endDT_1, startDT_2)) return false;
            if (!DateTime.isGreater(endDT_2, startDT_1)) return false;
            return true;
        }

        public boolean overlapWithPoint(DateTime startDT_1, DateTime endDT_1, DateTime startDT_2, DateTime endDT_2) {
            if (DateTime.isGreater(startDT_2, endDT_1)) return false;
            if (DateTime.isGreater(startDT_1, endDT_2)) return false;
            return true;
        }

        public void modifyIntersectionVector() {
            for (int i = 0; i < startIntersection.size() - 1; i++) {
                for (int j = i + 1; j < startIntersection.size(); j++) {
                    if (overlapWithPoint(startIntersection.get(i), endIntersection.get(i), startIntersection.get(j), endIntersection.get(j))) {
                        startIntersection.set(i, DateTime.minimum(startIntersection.get(i), startIntersection.get(j)));
                        endIntersection.set(i, DateTime.maximum(endIntersection.get(i), endIntersection.get(j)));
                        startIntersection.remove(j);
                        endIntersection.remove(j);
                        j = i;
                    }
                }
            }
        }

        public void addRepeatingEventsForOverlap() {
            for (int i = 0; i < repeatingSorted.size(); i++) {
                String[] eventInfo = repeatingSorted.get(i).split(",");
                String REPETITION = eventInfo[6];


                DateTime presentDT = new DateTime(true);
                presentDT = DateTime.getDateTime(presentDT, (-1) * Constants.EVENT.PAST_DISPLAY_TIME_MIN);
                String[] weekDays = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
                boolean[] weekDaysChecked = new boolean[7];
                for (int j = 0; j < 7; j++) {
                    if (String.valueOf(REPETITION.charAt(j)).matches("0"))
                        weekDaysChecked[j] = false;
                    else weekDaysChecked[j] = true;
                }

                int dayRef = 0;
                SimpleDateFormat sdf = new SimpleDateFormat("EE");
                Date d = new Date(presentDT.YEAR - 1900, presentDT.MONTH - 1, presentDT.DAY);
                String dayOfTheWeek = sdf.format(d);
                while (true) {
                    if (weekDays[dayRef].matches(dayOfTheWeek)) break;
                    dayRef++;
                }

                DateTime startDateTime = new DateTime(eventInfo[4]);
                TimeInput startTime = new TimeInput();
                startDateTime.setTimeInput(startTime);
                DateTime endDateTime = new DateTime(eventInfo[5]);
                TimeInput endTime = new TimeInput();
                endDateTime.setTimeInput(endTime);
                TimeInput presentTime = new TimeInput();
                presentDT.setTimeInput(presentTime);
                String ends_on = eventInfo[9];
                DateTime ENDS_ON = new DateTime(ends_on);

                int diffDays = 0;
                if (!TimeInput.isGreater(presentTime, startTime)) {
                    diffDays = 1;
                    dayRef--;
                    if (dayRef < 0) dayRef += 7;
                }
                while (!weekDaysChecked[dayRef]) {
                    dayRef--;
                    diffDays++;
                    if (dayRef < 0) dayRef += 7;
                }

                DateTime StartDT = DateTime.getDateTime(presentDT, -1 * diffDays * DateTime.MINS_IN_DAY);
                StartDT.HOUR = startTime.selectedHour;
                StartDT.MINUTE = startTime.selectedMin;
                DateTime compareDT;
                if (DateTime.isGreater(Constants.EVENT.NR_LAST_END_DATETIME, ENDS_ON)) {
                    compareDT = Constants.EVENT.NR_LAST_END_DATETIME;
                } else compareDT = ENDS_ON;
                while (DateTime.isGreater(compareDT, StartDT)) {
                    int sum = 0;
                    startHelper.add(StartDT);
                    DateTime EndDT = DateTime.getDateTime(StartDT, DateTime.differenceInMin(startDateTime, endDateTime));
                    endHelper.add(EndDT);
                    dayRef++;
                    sum++;
                    if (dayRef == 7) dayRef = 0;
                    while (!weekDaysChecked[dayRef]) {
                        dayRef++;
                        sum++;
                        if (dayRef == 7) dayRef = 0;
                    }
                    StartDT = DateTime.getDateTime(StartDT, sum * DateTime.MINS_IN_DAY);
                }
            }
        }
    }
}