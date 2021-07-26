package com.harsha.eevee;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EventApprovalNotification extends Fragment {

    public String eeVeeID;

    public String _name;
    public String _frequency;
    public String _place;
    public DateTime _time_info;
    public Button Yes;
    public Button No;
    public LinearLayout background;
    public TextView name;
    public TextView frequency;
    public TextView place;
    public TextView time_info;
    public ImageView NotificationIcon;
    private String[] weekDaySymbol = {"Su", "M", "Tu", "W", "Th", "F", "Sa"};
    private Context context;
    private OnlineEventDetailsDBHandler onlineEventDetailsDBHandler;
    private boolean isInitialized = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.event_approval_notification, container, false);

        // Getting references to layout variables
        background = (LinearLayout) view.findViewById(R.id.background);
        Yes = (Button) view.findViewById(R.id.Yes);
        No = (Button) view.findViewById(R.id.No);
        name = (TextView) view.findViewById(R.id.name);
        place = (TextView) view.findViewById(R.id.place);
        frequency = (TextView) view.findViewById(R.id.frequency);
        time_info = (TextView) view.findViewById(R.id.time_info);
        NotificationIcon = (ImageView) view.findViewById(R.id.NotificationIcon);
        // COMPLETED

        initializeViews();


        Yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlineEventDetailsDBHandler.setThisHere(OnlineEventDetailsDBHandler.COLUMN_EEVEE_ID, eeVeeID, OnlineEventDetailsDBHandler.COLUMN_APPROVAL, Constants.APPROVAL_ACCEPTED);
                Toast.makeText(getActivity(), "Accepted", Toast.LENGTH_SHORT).show();
                getActivity().getFragmentManager().beginTransaction().remove(EventApprovalNotification.this).commit();
            }
        });

        Yes.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Accept event", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        No.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onlineEventDetailsDBHandler.setThisHere(OnlineEventDetailsDBHandler.COLUMN_EEVEE_ID, eeVeeID, OnlineEventDetailsDBHandler.COLUMN_APPROVAL, Constants.APPROVAL_REJECTED);
                Toast.makeText(getActivity(), "Ignored", Toast.LENGTH_SHORT).show();
                getActivity().getFragmentManager().beginTransaction().remove(EventApprovalNotification.this).commit();
            }
        });

        No.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(getActivity(), "Ignore event", Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Hold to view details", Toast.LENGTH_SHORT).show();
            }
        });

        background.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent viewDetails = new Intent(context, ViewOnlineEvent.class);
                viewDetails.putExtra(Constants.FROM_INTENT_EXTRAS, Constants.FROM_APPROVAL_NOTIFICATIONS);
                viewDetails.putExtra("id", Integer.parseInt(eeVeeID));
                startActivity(viewDetails);
                return true;
            }
        });

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        onlineEventDetailsDBHandler = new OnlineEventDetailsDBHandler(activity.getApplicationContext(), null, null, 1);
        context = activity.getApplicationContext();
    }

    public void initializeNotificationUsingOnlineEvent(OnlineEventDetails onlineEvent) {
        if (onlineEvent == null) return;

        eeVeeID = onlineEvent.get_eeVeeID();
        _name = onlineEvent.get_EventName();
        _place = onlineEvent.get_EventPlace();
        _frequency = onlineEvent.get_Repetition();
        DateTime START = new DateTime(onlineEvent.get_StartDateTime());
        _time_info = START;
        // Fragment is initialized
        isInitialized = true;
    }

    private void initializeViews() {
        if (!isInitialized) return;

        name.setText(_name);
        place.setText(_place);
        String _freqDecrypt = "";

        if (_frequency.matches("0000000")) {
            _freqDecrypt = "";
        } else if (_frequency.matches("1111111")) {
            _freqDecrypt = "daily";
        } else {
            for (int i = 0; i < 7; i++) {
                if (_frequency.charAt(i) == '1') {
                    _freqDecrypt += " " + weekDaySymbol[i];
                }
            }
        }

        frequency.setText(_freqDecrypt);

        if (_frequency.matches("0000000")) {
            DateInput date = new DateInput();
            TimeInput time = new TimeInput();
            _time_info.setDateInput(date);
            _time_info.setTimeInput(time);
            time_info.setText(time.timeDispString() + "\n" + date.dateDispString());
        } else {
            TimeInput time = new TimeInput();
            _time_info.setTimeInput(time);
            time_info.setText(time.timeDispString());
        }
    }

}