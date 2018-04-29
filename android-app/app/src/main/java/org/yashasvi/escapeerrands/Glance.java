package org.yashasvi.escapeerrands;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import org.yashasvi.escapeerrands.beans.Goal;
import org.yashasvi.escapeerrands.daos.GoalDAO;
import org.yashasvi.escapeerrands.daoimpls.GoalDAOImpl;

import java.util.List;

import es.dmoral.toasty.Toasty;

public class Glance extends AppCompatActivity {

    private GoalDAO goalDAO = new GoalDAOImpl();
    private ListView goalListView;
    private SwipeRefreshLayout goalListSwipeRefresh;
    private boolean globalSearch = false;
    private String prevRegexPattern = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.a_glance);
        goalListView = (ListView) findViewById(R.id.a_glance_goal_list);
        goalListSwipeRefresh = (SwipeRefreshLayout) findViewById(R.id.a_glance_goal_list_swipe_refresh);
        goalListSwipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        goalListSwipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new GoalPuller(prevRegexPattern).execute();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.glance_menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.a_glance_menu_search_item).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String string) {
                prevRegexPattern = string;
                new GoalPuller(string).execute();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String string) {
                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.a_glance_menu_pull_all_goals_item:
                prevRegexPattern = "";
                new GoalPuller("").execute();
                return false;
            case R.id.a_glance_menu_global_search_item:
                boolean isChecked = !item.isChecked();
                globalSearch = isChecked;
                item.setChecked(isChecked);
                new GoalPuller(prevRegexPattern).execute();
            default:
                return false;
        }
    }

    private class GoalPuller extends AsyncTask<String, Integer, List<Goal>> {
        private ProgressDialog progressDialog;
        private String pattern;

        private GoalPuller(final String pattern) {
            this.pattern = pattern;
            progressDialog = new ProgressDialog(Glance.this);
            progressDialog.setMessage("Searching ...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setIndeterminate(true);
            progressDialog.show();
        }

        @Override
        protected List<Goal> doInBackground(final String... strings) {
            return goalDAO.getGoalsByRegex(pattern, globalSearch);
        }

        @Override
        protected void onPostExecute(final List<Goal> goalList) {
            if (goalList == null) {
                Toasty.error(Glance.this, "Could not fetch goals!", Toast.LENGTH_SHORT).show();
            } else {
                goalListView.setAdapter(new GoalListAdapter(Glance.this, goalList));
            }
            goalListSwipeRefresh.setRefreshing(false);
            progressDialog.dismiss();
        }
    }

}
