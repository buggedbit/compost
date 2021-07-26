package com.harsha.eevee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
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


public class TasksHome extends AppCompatActivity implements GestureDetector.OnGestureListener,
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
    public int TaskLongClickedID;
    ImageButton filtersTaskBTN;
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
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
    private GestureDetectorCompat gestureDetector;
    private boolean gestureAreaUp = false;
    private int gestureAreaUp_dip = Constants.GESTURE_AREA_UP_DIP;
    private int gestureAreaDown_dip = Constants.GESTURE_AREA_DOWN_DIP;
    private Handler gestureAreaHandler = new Handler();
    private Map<String, Boolean> clubFilters;
    private Map<String, Boolean> typeFilters;

    //Instance Variables Finish -------------- Methods Start

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(TasksHome.this, Home.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks_home);

        mVisible = true;

        fullScreenToggle = findViewById(R.id.greet_tasks);
        // Set up the user interaction to manually show or hide the system UI.


        //Gestures Setting
        gestureDetector = new GestureDetectorCompat(this, this);
        gestureDetector.setOnDoubleTapListener(this);

        Button greet_tasks = (Button) findViewById(R.id.greet_tasks);
        assert greet_tasks != null;
        greet_tasks.setText("Hey " + UserDetails.$USERNAME + ", here's all your tasks");
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("What next !!");
        TaskLongClickedID = (int) v.getTag();
        getMenuInflater().inflate(R.menu.edit_view_delete_context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewOrEditItem:
                Intent intent = new Intent(this, EditAndViewTask.class);
                intent.putExtra("id", TaskLongClickedID);
                startActivity(intent);
                return true;
            case R.id.deleteItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                builder.setTitle("Confirm your action");
                builder.setMessage("Are you sure about deleting this?");
                builder.setInverseBackgroundForced(true);
                builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        uiManager.dbHandlerOffline.deleteTask(TaskLongClickedID);
                        dialog.dismiss();
                        LinearLayout EventPanel = (LinearLayout) findViewById(R.id.EventPanel);
                        assert EventPanel != null;
                        EventPanel.removeAllViews();
                        UIManager uiManager = new UIManager();
                        uiManager.findMaxFutureTimeDisplayed();
                        uiManager.display();
                        Toast.makeText(TasksHome.this, "Hey your event was deleted successfully", Toast.LENGTH_SHORT).show();
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

    @Override
    protected void onStart() {
        delayedHide(500);
        super.onStart();

        //Setting past time
        setPastRegion();

        clubFilters = new HashMap();
        typeFilters = new HashMap();
        setUpFilterMaps();

        //Displaying Tasks
        uiManager = new UIManager();
        uiManager.display();

        filtersTaskBTN = (ImageButton) findViewById(R.id.filtersTaskBTN);
        filtersTaskBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nextActivity = new Intent(TasksHome.this, SetEventFilters.class);
                nextActivity.putExtra("TO FILTERS ACTIVITY", "FROM TASKS_HOME");
                startActivity(nextActivity);
            }
        });

        filtersTaskBTN.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(TasksHome.this, "Tap to set filters", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    private void setPastRegion() {
        int WIDTH_OF_PAST_REGION = Constants.TASK.ORIGIN;

        TextView pastRegionTasks = (TextView) findViewById(R.id.pastRegionTasks);
        assert pastRegionTasks != null;
        ViewGroup.LayoutParams params = pastRegionTasks.getLayoutParams();
        params.width = WIDTH_OF_PAST_REGION;
        pastRegionTasks.setLayoutParams(params);

        /**
         * We decided that for tasks a only little past time should be shown and the scroll should be from the left most point
         * */

        /*final HorizontalScrollView H_scroll_tasks = (HorizontalScrollView) findViewById(R.id.H_scroll_tasks);
        assert H_scroll_tasks != null;
        H_scroll_tasks.post(new Runnable() {
            @Override
            public void run() {
                Log.i("scroll" , "tasksScroll");
                H_scroll_tasks.scrollTo(Constants.TASK.ORIGIN, 0);
            }
        });*/


    }

    public void setTimeDivisions() {
        TimeDivisions setter = (TimeDivisions) findViewById(R.id.timedivisions_tasks);

        RelativeLayout.LayoutParams setterParams = (RelativeLayout.LayoutParams) setter.getLayoutParams();
        int WIDTH = (int) ((Constants.TASK.MAX_FUTURE_TIME_ON_DISPLAY + Constants.TASK.PAST_DISPLAY_TIME_MIN) * Constants.SCALE);
        setterParams.width = WIDTH;
        setter.setLayoutParams(setterParams);
    }

    private void gestureAreaUp() {
        Runnable up = new Runnable() {
            @Override
            public void run() {
                Resources r = getResources();
                int greet_height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, Constants.GREET_HEIGHT_DP, r.getDisplayMetrics());

                RelativeLayout Tasks = (RelativeLayout) findViewById(R.id.Tasks);
//                TransitionManager.beginDelayedTransition(Tasks);

                //Getting gestureAreaTasks and Tasks_main_parent
                RelativeLayout gestureAreaTasks = (RelativeLayout) findViewById(R.id.gestureAreaTasks);
                assert gestureAreaTasks != null;
                RelativeLayout Tasks_main_parent = (RelativeLayout) findViewById(R.id.Tasks_main_parent);
                assert Tasks_main_parent != null;

                //Getting gestureAreaTasks and Tasks_main_parent layout parameters
                RelativeLayout.LayoutParams params_GESTURE_AREA = (RelativeLayout.LayoutParams) gestureAreaTasks.getLayoutParams();
                RelativeLayout.LayoutParams params_Tasks_main_parent = (RelativeLayout.LayoutParams) Tasks_main_parent.getLayoutParams();


                int up_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gestureAreaUp_dip, r.getDisplayMetrics());

                if (!gestureAreaUp) {
                    //Increasing Height of gestureAreaTasks and bottom margin of Tasks_main_parent
                    params_GESTURE_AREA.height = up_px;
                    params_Tasks_main_parent.setMargins(0, greet_height, 0, up_px);
                    gestureAreaTasks.setLayoutParams(params_GESTURE_AREA);
                    Tasks_main_parent.setLayoutParams(params_Tasks_main_parent);

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


                RelativeLayout Tasks = (RelativeLayout) findViewById(R.id.Tasks);
//                TransitionManager.beginDelayedTransition(Tasks);

                //Getting gestureAreaTasks and Tasks_main_parent
                RelativeLayout gestureAreaTasks = (RelativeLayout) findViewById(R.id.gestureAreaTasks);
                assert gestureAreaTasks != null;
                RelativeLayout Tasks_main_parent = (RelativeLayout) findViewById(R.id.Tasks_main_parent);
                assert Tasks_main_parent != null;

                //Getting gestureAreaTasks and Tasks_main_parent layout parameters
                RelativeLayout.LayoutParams params_GESTURE_AREA = (RelativeLayout.LayoutParams) gestureAreaTasks.getLayoutParams();
                RelativeLayout.LayoutParams params_Tasks_main_parent = (RelativeLayout.LayoutParams) Tasks_main_parent.getLayoutParams();

                int down_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gestureAreaDown_dip, r.getDisplayMetrics());

                if (gestureAreaUp) {
                    //Increasing Height of gestureAreaTasks and bottom margin of Tasks_main_parent
                    params_GESTURE_AREA.height = down_px;
                    params_Tasks_main_parent.setMargins(0, greet_height, 0, down_px);
                    gestureAreaTasks.setLayoutParams(params_GESTURE_AREA);
                    Tasks_main_parent.setLayoutParams(params_Tasks_main_parent);

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

                RelativeLayout Tasks = (RelativeLayout) findViewById(R.id.Tasks);
//                TransitionManager.beginDelayedTransition(Tasks);

                //Getting gestureAreaTasks and Tasks_main_parent
                RelativeLayout gestureAreaTasks = (RelativeLayout) findViewById(R.id.gestureAreaTasks);
                assert gestureAreaTasks != null;
                RelativeLayout Tasks_main_parent = (RelativeLayout) findViewById(R.id.Tasks_main_parent);
                assert Tasks_main_parent != null;

                //Getting gestureAreaTasks and Tasks_main_parent layout parameters
                RelativeLayout.LayoutParams params_GESTURE_AREA = (RelativeLayout.LayoutParams) gestureAreaTasks.getLayoutParams();
                RelativeLayout.LayoutParams params_Tasks_main_parent = (RelativeLayout.LayoutParams) Tasks_main_parent.getLayoutParams();

                int down_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gestureAreaDown_dip, r.getDisplayMetrics());
                int up_px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, gestureAreaUp_dip, r.getDisplayMetrics());

                if (!gestureAreaUp) {
                    //Increasing Height of gestureAreaTasks and bottom margin of Tasks_main_parent
                    params_GESTURE_AREA.height = up_px;
                    params_Tasks_main_parent.setMargins(0, greet_height, 0, up_px);
                    gestureAreaTasks.setLayoutParams(params_GESTURE_AREA);
                    Tasks_main_parent.setLayoutParams(params_Tasks_main_parent);

                    //Changing state of gesture area
                    gestureAreaUp = !gestureAreaUp;
                } else if (gestureAreaUp) {
                    //Increasing Height of gestureAreaTasks and bottom margin of Tasks_main_parent
                    params_GESTURE_AREA.height = down_px;
                    params_Tasks_main_parent.setMargins(0, greet_height, 0, down_px);
                    gestureAreaTasks.setLayoutParams(params_GESTURE_AREA);
                    Tasks_main_parent.setLayoutParams(params_Tasks_main_parent);

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
        LinearLayout TaskPanel = (LinearLayout) findViewById(R.id.TaskPanel);
        assert TaskPanel != null;
        TaskPanel.removeAllViews();
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
        Intent i = new Intent(this, TaskInput.class);
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
        Intent GoEvents = new Intent(this, EventsHome.class);
        startActivity(GoEvents);
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        Log.i("gesture", "onFling");
        Log.i("analysis", e1.toString() + " , " + e2.toString());

        final HorizontalScrollView H_scroll_tasks = (HorizontalScrollView) findViewById(R.id.H_scroll_tasks);

        int scrollBy_Min = Constants.POWER_SCROLL_MINUTES;
        final int scrollBy_Width = (int) (scrollBy_Min * Constants.SCALE);

        float startX = e1.getX();
        float endX = e2.getX();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
        wm.getDefaultDisplay().getMetrics(displayMetrics);
        int minScroll = displayMetrics.widthPixels / 5;

        if ((endX - startX) < -minScroll) {

            H_scroll_tasks.post(new Runnable() {
                @Override
                public void run() {
                    Log.i("scroll", "tasksScroll");
                    H_scroll_tasks.scrollBy(scrollBy_Width, 0);
                }
            });
        } else if ((endX - startX) > minScroll) {

            H_scroll_tasks.post(new Runnable() {
                @Override
                public void run() {
                    Log.i("scroll", "tasksScroll");
                    H_scroll_tasks.scrollBy(-scrollBy_Width, 0);
                }
            });
        }

        return true;
    }

    //Gestures end here...
    @Override
    public boolean onTouchEvent(MotionEvent task) {
        gestureDetector.onTouchEvent(task);
        return super.onTouchEvent(task);
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

    public class TaskViewCompact {

        // TODO : use dip properly so task_input_bg to size tasks properly
        // TODO : make the scale more statistical so task_input_bg to make a good representation
        // TODO : keep an origin variable so task_input_bg to show some of the past time

        public String TABLE_NAME; // From Offline Table or Online
        public int ID;
        public String TAG;
        public String DESCRIPTION;
        public DateTime TS, START, END;
        public String REPETITION; // Seven 1's and 0's starting from Sunday
        public DateTime STARTS_FROM, ENDS_ON;
        public String TYPE;

        //measurements of the UI
        public int WIDTH; // in px

        public int LEFT_MARGIN; // can be negative

        public Vector<DateTime> START_REPEATING = new Vector<>();
        public Vector<Integer> LEFT_MARGINS_REPEATING = new Vector<>();

        public TaskViewCompact(String taskInfo) {

            // TODO : Inspect what all are empty and correspondingly represent them to user like START AND END (If they are missing then they can be represented on a different activity)

            String[] taskDetail = taskInfo.split(",");

            // Initializing
            DateTime _TS, _START, _END;

            // Setting ID
            ID = Integer.parseInt(taskDetail[0]);

            //Setting TS
            _TS = new DateTime(taskDetail[1]);
            TS = _TS;

            //Setting Description
            DESCRIPTION = taskDetail[2];

            //Setting Tag
            TAG = taskDetail[3];

            //Setting StartTime
            _START = new DateTime(taskDetail[4]);
            START = _START;

            //Setting EndTime
            _END = new DateTime(taskDetail[5]);
            END = _END;

            //Setting Repetition
            REPETITION = taskDetail[6];

            // Setting Starts From
            STARTS_FROM = new DateTime(taskDetail[7]);

            // Setting Ends On
            ENDS_ON = new DateTime(taskDetail[8]);

            TABLE_NAME = taskDetail[9];

            // Setting Type
            TYPE = taskDetail[10];
        }

        public void setWIDTH() {
            // ASSUMPTION : END is after START from validation

            if (REPETITION.matches("0000000")) {
                if (START != null && END != null) {

                    DateTime present = new DateTime(true);

                    // Constrained
                    if (START.isSetByString() && END.isSetByString()) {
                        int diffInMin = DateTime.differenceInMinWithSign(START, END);
                        WIDTH = (int) (diffInMin * Constants.SCALE);
                    }

                    // Deadline
                    else if (!START.isSetByString() && END.isSetByString()) {
                        // Here differenceInMinWithSign should only be used
                        int diffInMin = DateTime.differenceInMinWithSign(present, END) + Constants.TASK.PAST_DISPLAY_TIME_MIN;
                        WIDTH = (int) (diffInMin * Constants.SCALE);
                    }

                    // Inauguration
                    else if (START.isSetByString() && !END.isSetByString()) {
                        int diffInMin = DateTime.differenceInMinWithSign(START, Constants.TASK.LAST_END_DATETIME);
                        WIDTH = (int) (diffInMin * Constants.SCALE);
                    }
                    // Free
                    else if (!START.isSetByString() && !END.isSetByString()) {
                        int diffInMin = Constants.TASK.MAX_FUTURE_TIME_ON_DISPLAY + Constants.TASK.PAST_DISPLAY_TIME_MIN;
                        WIDTH = (int) (diffInMin * Constants.SCALE);
                    }

                } else {
                    Log.i("TaskUISetupError", "the START and END are uninitialized");
                }
            } else {
                int diffInMin = DateTime.differenceInMinWithSign(START, END);
                WIDTH = (int) (diffInMin * Constants.SCALE);
            }
        }

        public void setLEFT_MARGIN() {
            //SETS LEFT MARGIN TO START OF THE TASK
            DateTime present = new DateTime(true);

            if (REPETITION.matches("0000000")) {
                if (START != null && END != null) {

                    // Constrained and Inauguration
                    if (START.isSetByString()) {
                        int diffInMin = DateTime.differenceInMinWithSign(present, START);
                        LEFT_MARGIN = (int) (Constants.TASK.ORIGIN + diffInMin * Constants.SCALE);
                    }

                    // Deadline and Free
                    else if (!START.isSetByString()) {
                        int diffInMin = 0;
                        LEFT_MARGIN = (int) (diffInMin * Constants.SCALE); // == 0
                    }
                } else {
                    Log.i("TaskUISetupError", "the START and END are uninitialized");
                }
            } else {
                DateTime prevDateTime = new DateTime(true);
                prevDateTime = DateTime.getDateTime(prevDateTime, -1 * Constants.TASK.PAST_DISPLAY_TIME_MIN);

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

            LinearLayout TaskPanel = (LinearLayout) findViewById(R.id.TaskPanel);

            // For non repeating tasks

            if (REPETITION.matches("0000000")) {

                LinearLayout row = new LinearLayout(TasksHome.this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TaskPanel.addView(row, rowParams);

                setWIDTH();
                setLEFT_MARGIN();
                //ALL VARIABLES SET
                Log.i("task", "taskwidth" + String.valueOf(WIDTH));
                Log.i("task", "taskmargin" + String.valueOf(LEFT_MARGIN));

                Button task = new Button(TasksHome.this);
                task.setText(TAG);
                task.setTextSize(10);

                LinearLayout.LayoutParams taskParams = new LinearLayout.LayoutParams(WIDTH, Constants.TASK.HEIGHT);
                taskParams.setMargins(LEFT_MARGIN, 0, 0, 0);

                row.addView(task, taskParams);

                task.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(getApplicationContext(), String.valueOf(TAG), Toast.LENGTH_SHORT).show();
                    }
                });
                registerForContextMenu(task);
                task.setTag(ID);
            } else {
                LinearLayout row = new LinearLayout(TasksHome.this);
                row.setOrientation(LinearLayout.HORIZONTAL);

                LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                TaskPanel.addView(row, rowParams);

                // START_REPEATING vector of start date times
                DateTime presentDT = new DateTime(true);
                presentDT = DateTime.getDateTime(presentDT, (-1) * Constants.TASK.PAST_DISPLAY_TIME_MIN);

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

                DateTime StartDT = new DateTime();
                StartDT = DateTime.getDateTime(presentDT, -1 * diffDays * DateTime.MINS_IN_DAY);
                StartDT.HOUR = startTime.selectedHour;
                StartDT.MINUTE = startTime.selectedMin;
                DateTime compareDT = new DateTime();
                if (DateTime.isGreater(Constants.TASK.NR_LAST_END_DATETIME, ENDS_ON)) {
                    compareDT = Constants.TASK.NR_LAST_END_DATETIME;
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
                Log.i("task", "taskwidth" + String.valueOf(WIDTH));
                Log.i("task", "taskmargin" + String.valueOf(LEFT_MARGIN));

                Vector<Button> buttons = new Vector<>();

                for (int i = 0; i < START_REPEATING.size(); i++) {
                    Button source = new Button(TasksHome.this);
                    source.setText(TAG);
                    source.setTextSize(10);

                    LinearLayout.LayoutParams taskParams = new LinearLayout.LayoutParams(WIDTH, Constants.TASK.HEIGHT);
                    taskParams.setMargins(LEFT_MARGINS_REPEATING.get(i), 0, 0, 0);
                    row.addView(source, taskParams);

                    Log.i("harsha", String.valueOf(START_REPEATING.get(i)));
                    // Setting the listeners
                    source.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Toast.makeText(getApplicationContext(), String.valueOf(TAG), Toast.LENGTH_SHORT).show();
                        }
                    });

                    buttons.add(source);
                    registerForContextMenu(source);
                    source.setTag(ID);
                }
            }
        }


        // END OF INNER CLASS
    }

    public class UIManager {
        public Vector<String> constrained = new Vector<>(); // have both ST AND ET
        //TODO : same for online
        public Vector<String> deadlined = new Vector<>();   // have ET
        public Vector<String> daily = new Vector<>();       // have both ST AND ET and occurs daily
        public Vector<String> weekly = new Vector<>();      // have both ST AND ET and occurs weekly
        public Vector<String> inauguration = new Vector<>();// have both ST
        public Vector<String> free = new Vector<>();        // have none
        public Vector<String> constrainedAndDeadlinedSorted = new Vector<>();      //according to END TIME
        public Vector<String> repeatingSorted = new Vector<>();                    //according to END TIME -- weekly and daily
        public Vector<String> inaugurationSorted = new Vector<>();                 //according to START TIME
        public int noTasksOffline = 0;
        public int noTasksOnline = 0;
        TaskDetailsDBHandler dbHandlerOffline = new TaskDetailsDBHandler(TasksHome.this, null, null, 1);
        private String allOfflineTasksInfo = "";
        private String allOnlineTasksInfo = "";

        private boolean NRTasksSorted = false;

        public UIManager() {

            //delete all outdated tasks
            dbHandlerOffline.deleteOutdatedTasks();
            //same for online

            //get the no of Tasks (which are not outdated)
            noTasksOffline = dbHandlerOffline.getCount();
            //same for online

            //get all the tasks in a raw string form
            allOfflineTasksInfo = dbHandlerOffline.returnReqInfo();
            //same for online

            //Categorize the main string into six vector var categories
            categorizeOfflineTasksIntoVectorVar();
            Log.i("task_categorization", "cons" + constrained.toString() + "\n deadlined" + deadlined.toString() + "\n daily" + daily.toString() + "\n weekly" + weekly.toString() + "\n inaug" + inauguration.toString() + "\n free" + free.toString());
            //same for online

            //Make ConstrainedAndDeadlined Sorted List
            sortIntoConstrainedAndDeadlinedSorted();
            Log.i("task_categorization", "consAndDeadlinedSortedToString" + constrainedAndDeadlinedSorted.toString());

            //Make Inauguration Sorted List
            sortIntoInaugurationSorted();
            Log.i("task_categorization", "sortIntoInaugurationSortedToString" + inaugurationSorted.toString());

            //Make sortIntoRepeating Sorted List
            sortIntoRepeatingSorted();
            Log.i("task_categorization", "sortIntoRepeatingSortedToString" + repeatingSorted.toString());

            //Find maxFuture time displayed :: should be called only after sorting functions are called
            findMaxFutureTimeDisplayed();
        }

        private void categorizeOfflineTasksIntoVectorVar() {
            if (allOfflineTasksInfo.matches("")) return;

            String[] offlineTask = allOfflineTasksInfo.split("/");
            for (int i = 0; i < offlineTask.length; i++) {
                String[] taskDetail = offlineTask[i].split(",");

                String taskType = taskDetail[10];

                if(typeFilters.get(taskType)){
                    if (taskDetail[6].matches("1111111"))                                                    // daily
                    {
                        daily.add(offlineTask[i]);
                    } else if (!taskDetail[6].matches("1111111") && !taskDetail[6].matches("0000000"))         //weekly
                    {
                        weekly.add(offlineTask[i]);
                    } else if (taskDetail[6].matches("0000000"))                                               //once
                    {
                        DateTime START = new DateTime(taskDetail[4]), END = new DateTime(taskDetail[5]);
                        if (START.isSetByString() && END.isSetByString())        //constrained
                        {
                            constrained.add(offlineTask[i]);
                        } else if (!START.isSetByString() && END.isSetByString())  //deadlined
                        {
                            deadlined.add(offlineTask[i]);
                        } else if (START.isSetByString() && !END.isSetByString())  //inauguration
                        {
                            inauguration.add(offlineTask[i]);
                        } else if (!START.isSetByString() && !END.isSetByString()) //free
                        {
                            free.add(offlineTask[i]);
                        }
                    }
                }
            }
        }

        private void sortIntoConstrainedAndDeadlinedSorted() {
            int constrainedSize = constrained.size();
            int deadlinedSize = deadlined.size();

            int constrainedStartIterator;
            int deadlinedStartIterator;

            if (constrainedSize == 0 && deadlinedSize == 0) return;
            else if (constrainedSize == 0 && deadlinedSize != 0) {
                constrainedAndDeadlinedSorted.add(deadlined.get(0)); // first variable inserted task_input_bg the constrainedAndDeadlinedSorted is empty
                deadlinedStartIterator = 1;
                constrainedStartIterator = 0;
            } else if (constrainedSize != 0 && deadlinedSize == 0) {
                constrainedAndDeadlinedSorted.add(constrained.get(0)); // first variable inserted task_input_bg the constrainedAndDeadlinedSorted is empty
                constrainedStartIterator = 1;
                deadlinedStartIterator = 0;
            } else {
                constrainedAndDeadlinedSorted.add(constrained.get(0)); // first variable inserted task_input_bg the constrainedAndDeadlinedSorted is empty
                constrainedStartIterator = 1;
                deadlinedStartIterator = 0;
            }

            // Note the start index is 1
            for (int i = constrainedStartIterator; i < constrainedSize; i++)  // looping through constrained and adding to constrainedAndDeadlinedSorted
            {
                String iTH = constrained.get(i);
                String[] iTHTaskDetail = iTH.split(",");
                DateTime iTHTaskEndDateTime = new DateTime(iTHTaskDetail[5]);

                int constrainedAndDeadlinedSortedSize = constrainedAndDeadlinedSorted.size();
                for (int j = 0; j < constrainedAndDeadlinedSortedSize; j++) {
                    String jTH = constrainedAndDeadlinedSorted.get(j);
                    String[] jTHTaskDetail = jTH.split(",");
                    DateTime jTHTaskEndDateTime = new DateTime(jTHTaskDetail[5]);

                    boolean iBEFOREj = DateTime.isGreater(jTHTaskEndDateTime, iTHTaskEndDateTime);
                    if (iBEFOREj) {
                        constrainedAndDeadlinedSorted.insertElementAt(iTH, j);
                        break;
                    } else if (j == constrainedAndDeadlinedSortedSize - 1) {
                        constrainedAndDeadlinedSorted.add(iTH);
                    }
                }

            }

            for (int i = deadlinedStartIterator; i < deadlinedSize; i++)  // looping through deadlined and adding to constrainedAndDeadlinedSorted
            {
                String iTH = deadlined.get(i);
                String[] iTHTaskDetail = iTH.split(",");
                DateTime iTHTaskEndDateTime = new DateTime(iTHTaskDetail[5]);

                int constrainedAndDeadlinedSortedSize = constrainedAndDeadlinedSorted.size();
                for (int j = 0; j < constrainedAndDeadlinedSortedSize; j++) {
                    String jTH = constrainedAndDeadlinedSorted.get(j);
                    String[] jTHTaskDetail = jTH.split(",");
                    DateTime jTHTaskEndDateTime = new DateTime(jTHTaskDetail[5]);

                    boolean iBEFOREj = DateTime.isGreater(jTHTaskEndDateTime, iTHTaskEndDateTime);
                    if (iBEFOREj) {
                        constrainedAndDeadlinedSorted.insertElementAt(iTH, j);
                        break;
                    } else if (j == constrainedAndDeadlinedSortedSize - 1) {
                        constrainedAndDeadlinedSorted.add(iTH);
                    }
                }
            }

            NRTasksSorted = true;
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
                String[] iTHTaskDetail = iTH.split(",");
                DateTime iTHTaskEndDateTime = new DateTime(iTHTaskDetail[5]);

                int repeatingSortedSize = repeatingSorted.size();
                for (int j = 0; j < repeatingSortedSize; j++) {
                    String jTH = repeatingSorted.get(j);
                    String[] jTHTaskDetail = jTH.split(",");
                    DateTime jTHTaskEndDateTime = new DateTime(jTHTaskDetail[5]);

                    boolean iBEFOREj = DateTime.isGreater(jTHTaskEndDateTime, iTHTaskEndDateTime);
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
                String[] iTHTaskDetail = iTH.split(",");
                DateTime iTHTaskEndDateTime = new DateTime(iTHTaskDetail[5]);

                int repeatingSortedSize = repeatingSorted.size();
                for (int j = 0; j < repeatingSortedSize; j++) {
                    String jTH = repeatingSorted.get(j);
                    String[] jTHTaskDetail = jTH.split(",");
                    DateTime jTHTaskEndDateTime = new DateTime(jTHTaskDetail[5]);

                    boolean iBEFOREj = DateTime.isGreater(jTHTaskEndDateTime, iTHTaskEndDateTime);
                    if (iBEFOREj) {
                        repeatingSorted.insertElementAt(iTH, j);
                        break;
                    } else if (j == repeatingSortedSize - 1) {
                        repeatingSorted.add(iTH);
                    }
                }
            }
        }

        private void sortIntoInaugurationSorted() {
            int inaugurationSize = inauguration.size();
            if (inaugurationSize == 0) return;

            inaugurationSorted.add(inauguration.get(0)); // first variable inserted task_input_bg the inaugurationSorted is empty

            // Note the start index is 1
            for (int i = 1; i < inaugurationSize; i++)  // looping through inauguration and adding to inaugurationSorted
            {
                String iTH = inauguration.get(i);
                String[] iTHTaskDetail = iTH.split(",");
                DateTime iTHTaskStartDateTime = new DateTime(iTHTaskDetail[4]); //Sorting according to Start times

                int inaugurationSortedSize = inaugurationSorted.size();
                for (int j = 0; j < inaugurationSortedSize; j++) {
                    String jTH = inaugurationSorted.get(j);
                    String[] jTHTaskDetail = jTH.split(",");
                    DateTime jTHTaskStartDateTime = new DateTime(jTHTaskDetail[4]);

                    boolean iBEFOREj = DateTime.isGreater(jTHTaskStartDateTime, iTHTaskStartDateTime);
                    if (iBEFOREj) {
                        inaugurationSorted.insertElementAt(iTH, j);
                        break;
                    } else if (j == inaugurationSortedSize - 1) {
                        inaugurationSorted.add(iTH);
                    }
                }
            }
        }

        // TODO : change , considering daily and weekly repetition
        private void findMaxFutureTimeDisplayed() {
            int nonRepeatingSize = constrainedAndDeadlinedSorted.size();
            int repeatingSize = daily.size() + weekly.size();

            DateTime present = new DateTime(true);

            if (nonRepeatingSize == 0 && repeatingSize == 0) {
                Constants.TASK.MAX_FUTURE_TIME_ON_DISPLAY = 2 * DateTime.MINS_IN_DAY;
                Constants.TASK.LAST_END_DATETIME = DateTime.getDateTime(present, Constants.TASK.MAX_FUTURE_TIME_ON_DISPLAY);
                Constants.TASK.NR_LAST_END_DATETIME = Constants.TASK.LAST_END_DATETIME;
                return;
            }

            int NR_maxEndingTimeFromPresentInMin = 0;

            for (int i = 0; i < nonRepeatingSize; i++)  // looping through nonRepeating checking for last ending task
            {
                String iTH = constrainedAndDeadlinedSorted.get(i);
                String[] iTHTaskDetail = iTH.split(",");
                DateTime iTHTaskEndDateTime = new DateTime(iTHTaskDetail[5]); //checking according to End Datetime

                int checker = DateTime.differenceInMinWithSign(present, iTHTaskEndDateTime);
                if (checker > NR_maxEndingTimeFromPresentInMin) {
                    NR_maxEndingTimeFromPresentInMin = checker;
                }
            }
            // NR_maxEndingTimeFromPresentInMin is taken care of

            if (repeatingSize == 0) {
                int maxFutureTimeDisplayedTasks;
                maxFutureTimeDisplayedTasks = Math.max(2 * DateTime.MINS_IN_DAY, NR_maxEndingTimeFromPresentInMin);
                Constants.TASK.MAX_FUTURE_TIME_ON_DISPLAY = maxFutureTimeDisplayedTasks;
                Constants.TASK.LAST_END_DATETIME = DateTime.getDateTime(present, Constants.TASK.MAX_FUTURE_TIME_ON_DISPLAY);
                Constants.TASK.NR_LAST_END_DATETIME = Constants.TASK.LAST_END_DATETIME;
                return;
            }
            // if no repeating tasks are there then we show a minimum of 2 days of future time
            // TODO : change this in considering with weekly and daily tasks
            else {
                int maxFutureTimeDisplayedTasks;
                maxFutureTimeDisplayedTasks = max(7 * DateTime.MINS_IN_DAY, NR_maxEndingTimeFromPresentInMin);
                DateTime refMax = DateTime.getDateTime(present, maxFutureTimeDisplayedTasks);
                Constants.TASK.NR_LAST_END_DATETIME = refMax;
                for (int i = 0; i < repeatingSize; i++) {
                    refMax = getMaxDateTime(refMax, repeatingSorted.get(i));
                }
                Constants.TASK.MAX_FUTURE_TIME_ON_DISPLAY = DateTime.differenceInMin(present, refMax);
                Constants.TASK.LAST_END_DATETIME = refMax;
                return;
            }
        }

        private DateTime getMaxDateTime(DateTime ref, String task) {
            String[] taskInfo = task.split(",");
            String repetitionAsString = taskInfo[6];
            String endsOn = taskInfo[8];
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

            DateTime startDateTime = new DateTime(taskInfo[4]);
            TimeInput startTime = new TimeInput();
            startDateTime.setTimeInput(startTime);
            DateTime endDateTime = new DateTime(taskInfo[5]);
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

        public void display() {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            WindowManager wm = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE); // the results will be higher than using the activity context object or the getWindowManager() shortcut
            wm.getDefaultDisplay().getMetrics(displayMetrics);

            Constants.TASK.HEIGHT = displayMetrics.heightPixels / 12;

            // TODO : find max display time including daily and weekly
            // TODO : use sorted list
            // TODO : display daily and weekly tasks

            int noTasks = noTasksOffline + noTasksOnline;
            if (noTasks != 0) {

                // First display constrained and deadlined tasks
                int constrainedAndDeadlinedSortedSize = constrainedAndDeadlinedSorted.size();

                // Constrained and Deadlined TaskViewCompactarray
                TaskViewCompact[] taskViewCompact_CD = new TaskViewCompact[constrainedAndDeadlinedSortedSize];

                for (int i = 0; i < constrainedAndDeadlinedSortedSize; i++) {
                    // Called the Constructor of  iTH taskView_CD
                    taskViewCompact_CD[i] = new TaskViewCompact(constrainedAndDeadlinedSorted.get(i));
                    taskViewCompact_CD[i].displayYourself();
                }

                int repeatingSortedSize = repeatingSorted.size();

                TaskViewCompact[] taskViewCompact_R = new TaskViewCompact[repeatingSortedSize];

                for (int i = 0; i < repeatingSortedSize; i++) {
                    // Called the Constructor of  iTH taskView_CD
                    taskViewCompact_R[i] = new TaskViewCompact(repeatingSorted.get(i));
                    taskViewCompact_R[i].displayYourself();
                }

                // Then display inauguration tasks
                int inaugurationSortedSize = inaugurationSorted.size();

                // inauguration TaskViewCompact array
                TaskViewCompact[] taskViewCompact_I = new TaskViewCompact[inaugurationSortedSize];

                for (int i = 0; i < inaugurationSortedSize; i++) {

                    // Called the Constructor of  iTH taskView_I
                    taskViewCompact_I[i] = new TaskViewCompact(inaugurationSorted.get(i));
                    taskViewCompact_I[i].displayYourself();
                }

                // Then display free tasks
                int freeSize = free.size();

                // free TaskViewCompact array
                TaskViewCompact[] taskViewCompact_F = new TaskViewCompact[freeSize];

                for (int i = 0; i < freeSize; i++) {

                    // Called the Constructor of  iTH taskView_F
                    taskViewCompact_F[i] = new TaskViewCompact(free.get(i));
                    taskViewCompact_F[i].displayYourself();
                }
            } else {

            }
            setTimeDivisions();
        }

        public void showOverlaps() {
            Overlaps overlaps_layout = (Overlaps) findViewById(R.id.overlaps_tasks);
            ViewGroup.LayoutParams params = overlaps_layout.getLayoutParams();
            params.width = (int) ((Constants.TASK.MAX_FUTURE_TIME_ON_DISPLAY + Constants.TASK.PAST_DISPLAY_TIME_MIN) * Constants.SCALE);
            overlaps_layout.setLayoutParams(params);

            overlaps_layout.showOverlaps(null, null);
        }

        // END OF INNER CLASS
    }
}