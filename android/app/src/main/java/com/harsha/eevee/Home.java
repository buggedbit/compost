package com.harsha.eevee;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Home extends AppCompatActivity {

    EventFiltersDBHandler dbHandler;
    SwipeRefreshLayout swipeRefresh;
    private LinearLayout NotificationPanel;
    private boolean fetchDataFromOnline = false;
    private Map<String, Boolean> clubFilters;
    private Map<String, Boolean> typeFilters;

    // Disabling the back button in the Home Page
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Getting reference to NotificationPanel
        NotificationPanel = (LinearLayout) findViewById(R.id.NotificationPanel);
        final int Notification_ID = 12345;
        NotificationPanel.setId(Notification_ID);

        // Setting up swipe refresh
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipeRefresh);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                onlineDBUpdateOperation();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        clubFilters = new HashMap();
        typeFilters = new HashMap();
        initialiseFilters();
        setUpFilterMaps();

        setUpNotifications();
    }

    @Override
    protected void onStop() {
        super.onStop();
        removeAllNotifications();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refreshItem:
                swipeRefresh.setRefreshing(true);
                onlineDBUpdateOperation();
                return true;
            case R.id.settingsItem:
                Intent goToSettingsAc = new Intent(this, AppSettings.class);
                startActivity(goToSettingsAc);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void setUpNotifications() {
        OnlineEventDetailsDBHandler onlineEventDetailsDBHandler = new OnlineEventDetailsDBHandler(this, null, null, 1);
        onlineEventDetailsDBHandler.deleteOutdatedEvents();
        Vector<OnlineEventDetails> pendingCases = new Vector<>();
        pendingCases = onlineEventDetailsDBHandler.getAllObjectsWith(OnlineEventDetailsDBHandler.COLUMN_APPROVAL, Constants.APPROVAL_PENDING);

        int pendingCount = pendingCases.size();
        for (int i = 0; i < pendingCount; i++) {
            if (!pendingCases.get(i).get_Status().matches(Constants.STATUS_DELETED)
                    && clubFilters.get(pendingCases.get(i).get_ClubName())) {
                postApprovalNotification(pendingCases.get(i));
            }
        }
    }

    private void postApprovalNotification(OnlineEventDetails onlineEvent) {
        EventApprovalNotification newNotification = new EventApprovalNotification();
        // fragment is initialized
        newNotification.initializeNotificationUsingOnlineEvent(onlineEvent);
        // fragment is displayed
        getFragmentManager().beginTransaction().add(NotificationPanel.getId(), newNotification, onlineEvent.get_eeVeeID()).commit();
    }

    private void removeAllNotifications() {
        NotificationPanel.removeAllViews();
    }

    public void goToEvents(View view) {
        Intent i = new Intent(this, EventsHome.class);
        startActivity(i);
    }

    public void goToTasks(View view) {
        Intent i = new Intent(this, TasksHome.class);
        startActivity(i);
    }

    public void onlineDBUpdateOperation() {
        Log.i("fetch", "bool is : " + String.valueOf(fetchDataFromOnline));

        final Handler resetNotifications = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                removeAllNotifications();
                setUpNotifications();
                swipeRefresh.setRefreshing(false);
                Log.i("fetch", "bool is : " + String.valueOf(fetchDataFromOnline));
                fetchDataFromOnline = false;
                Log.i("fetch", "fetching complete");
                Toast.makeText(Home.this, "You are now all caught up", Toast.LENGTH_SHORT).show();
            }
        };

        Runnable r = new Runnable() {
            @Override
            public void run() {
                WebEventDetailsDBHandler web = new WebEventDetailsDBHandler(Home.this, null, null, 1);
                OnlineEventDetailsDBHandler online = new OnlineEventDetailsDBHandler(Home.this, null, null, 1);

                WebEventsFetcher i = new WebEventsFetcher(web, Home.this);
                i.fetchDataIntoServerResponse();
                i.fillUpIntoWebEventsDataTable();

                SyncEvents syncEvents = new SyncEvents(web, online, Home.this);
//                    syncEvents.completePush();
                syncEvents.startPushing();

                resetNotifications.sendEmptyMessage(0);
            }
        };

        final Handler noNetworkToast = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                Toast.makeText(Home.this, "Network not available", Toast.LENGTH_SHORT).show();
                swipeRefresh.setRefreshing(false);
            }
        };

        Runnable noNetworkDelay = new Runnable() {
            @Override
            public void run() {
                synchronized (this) {
                    try {
                        wait(1000);
                        noNetworkToast.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread t = new Thread(r);
        Thread noNetworkThread = new Thread(noNetworkDelay);

        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
            if (!fetchDataFromOnline) {
                Toast.makeText(this, "Fetching from cloud...", Toast.LENGTH_SHORT).show();
                fetchDataFromOnline = true;
                t.start();
            } else {

            }
        } else {
            noNetworkThread.start();
        }

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

    public void initialiseFilters() {
        dbHandler = new EventFiltersDBHandler(this, null, null, 1);
        dbHandler.resetFilters();
    }

    //TODO : DUMMY REMOVE
    public void goOnlineEvents(View view) {
        Intent i = new Intent(this, ShowOnlineEventsTable.class);
        startActivity(i);
    }
}