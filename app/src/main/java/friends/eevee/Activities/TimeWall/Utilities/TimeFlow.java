package friends.eevee.Activities.TimeWall.Utilities;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ScrollView;

import friends.eevee.Activities.TimeWall.TimeWall;


public class TimeFlow extends ScrollView {

    public TimeWall.TimeDivisionsManager timeDivisionsManager;
    public TimeWall.StubsStackManager stubsStackManager;

    public TimeFlow(Context context) {
        super(context);
    }

    public TimeFlow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);

        if (timeDivisionsManager != null) {
            timeDivisionsManager.scrollWithOffset();
        }


        if (timeDivisionsManager != null && stubsStackManager != null) {

            /* Over scroll to go to adjacent days */
            View view = (View) getChildAt(getChildCount() - 1);
            int forward = (view.getBottom() - (getHeight() + getScrollY()));
            int back = getScrollY();

            if (forward == 0) {
                // if forward is zero, then the bottom has been reached
                Snackbar.make(this, "Next day?", Snackbar.LENGTH_SHORT)
                        .setAction("Go", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeDay(1);
                                scrollTo(0,1);
                            }
                        }).show();
            } else if (back == 0) {
                // if back is zero, then the top has been reached
                Snackbar.make(this, "Previous day?", Snackbar.LENGTH_SHORT)
                        .setAction("Go", new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                changeDay(-1);
                                scrollTo(0,1);
                            }
                        }).show();
            }
        }


    }


    private void changeDay(int no_days) {

        if (timeDivisionsManager != null && stubsStackManager != null) {
            UIPreferences.SHOWING_DATE.addDays(no_days);
            timeDivisionsManager.showThisDay(UIPreferences.SHOWING_DATE);
            stubsStackManager.showThisDay(UIPreferences.SHOWING_DATE);
        }

    }



}
