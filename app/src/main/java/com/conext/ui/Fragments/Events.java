package com.conext.ui.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.conext.Adapter.RecyclerAdapterProfileEvents;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.ProfileEvents;
import com.conext.model.db.USER_EVENT;
import com.conext.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

public class Events extends Fragment implements View.OnClickListener {

    private List<ProfileEvents> profileEventsList;
    private LinearLayoutManager mLayoutManager;
    private RecyclerAdapterProfileEvents recyclerAdapterProfileEvents;
    ProfileEvents profileEvents;
    ArrayList<USER_EVENT> profileEventsArrayList;
    RecyclerAdapterProfileEvents profileEventsAdapter = null;

    private ImageView eventsAttendedImageView, coffeeConnectImageView, lunchMeetImageView, alumniImageView, mentorshipImageView;
    private TextView eventsAttendedTextView, coffeeConnectTextView, lunchMeetTextView, alumniTextView, mentorshipTextView, eventsAttend;
    private RecyclerView eventsRecyclerView;
    private SQLiteDBHelper sqLiteDBHelper;

    String userKey;

    public Events() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_events_profile, container, false);

        userKey = Prefs.getUserKey();

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());
        //addressing view
        addressingView(view);
        //attaching listeners
        listeners();

        displayEventsProfile();
        return view;

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("result", "got result 1");

    }

    private void displayEventsProfile() {
        profileEventsArrayList = new ArrayList<>();

        profileEventsArrayList = sqLiteDBHelper.getMyAttendedEvents(userKey);

        profileEventsAdapter = new RecyclerAdapterProfileEvents(getActivity(), profileEventsArrayList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        eventsRecyclerView.setLayoutManager(mLayoutManager);
        eventsRecyclerView.setHasFixedSize(true);
        eventsRecyclerView.setAdapter(profileEventsAdapter);

        int attendEvent = sqLiteDBHelper.getMyAttendedEvents(userKey).size();

        String eventFinalCount;
        if (attendEvent < 10) {
            eventFinalCount = "" + 0 + attendEvent;
        } else {
            eventFinalCount = "" + attendEvent;
        }
        eventsAttendedTextView.setText(eventFinalCount);

        int coffeeConnectCount = sqLiteDBHelper.getParticularMyAttendedEvents(userKey, "4").size();

        String coffeeConnect;
        if (coffeeConnectCount < 10) {
            coffeeConnect = "" + 0 + coffeeConnectCount;
        } else {
            coffeeConnect = "" + coffeeConnectCount;
        }
        coffeeConnectTextView.setText(coffeeConnect);

        int lunchMeetCount = sqLiteDBHelper.getParticularMyAttendedEvents(userKey, "3").size();

        String lunchMeet;
        if (lunchMeetCount < 10) {
            lunchMeet = "" + 0 + lunchMeetCount;
        } else {
            lunchMeet = "" + lunchMeetCount;
        }
        lunchMeetTextView.setText(lunchMeet);

        int alumniCount = sqLiteDBHelper.getParticularMyAttendedEvents(userKey, "2").size();

        String alumniConnect;
        if (alumniCount < 10) {
            alumniConnect = "" + 0 + alumniCount;
        } else {
            alumniConnect = "" + alumniCount;
        }
        alumniTextView.setText(alumniConnect);

        int mentorShipCount = sqLiteDBHelper.getParticularMyAttendedEvents(userKey, "1").size();

        String mentorShip;
        if (mentorShipCount < 10) {
            mentorShip = "" + 0 + mentorShipCount;
        } else {
            mentorShip = "" + mentorShipCount;
        }
        mentorshipTextView.setText(mentorShip);

    }

    private void listeners() {

        eventsAttendedImageView.setOnClickListener(this);
        coffeeConnectImageView.setOnClickListener(this);
        lunchMeetImageView.setOnClickListener(this);
        alumniImageView.setOnClickListener(this);
        mentorshipImageView.setOnClickListener(this);

    }

    private void addressingView(View view) {

        eventsAttendedImageView = (ImageView) view.findViewById(R.id.event_attend_prof);
        coffeeConnectImageView = (ImageView) view.findViewById(R.id.event_coffee_prof);
        lunchMeetImageView = (ImageView) view.findViewById(R.id.event_lunch_prof);
        alumniImageView = (ImageView) view.findViewById(R.id.event_alumini_prof);
        mentorshipImageView = (ImageView) view.findViewById(R.id.event_mentor_prof);

        eventsAttendedTextView = (TextView) view.findViewById(R.id.event_prof_attended);
        coffeeConnectTextView = (TextView) view.findViewById(R.id.event_coffee_attended);
        lunchMeetTextView = (TextView) view.findViewById(R.id.event_lunch_attended);
        alumniTextView = (TextView) view.findViewById(R.id.event_alumni_meet);
        mentorshipTextView = (TextView) view.findViewById(R.id.event_prof_ship);
        eventsAttend = (TextView) view.findViewById(R.id.events_attended_text_view);
        eventsRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_events);
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.event_attend_prof:

                eventsAttendedImageView.setImageResource(R.drawable.event_attended);

                coffeeConnectImageView.setImageResource(R.drawable.cc_connect);

                lunchMeetImageView.setImageResource(R.drawable.cc_connect);

                alumniImageView.setImageResource(R.drawable.cc_connect);

                mentorshipImageView.setImageResource(R.drawable.cc_connect);


                profileEventsArrayList = new ArrayList<>();

                profileEventsArrayList = sqLiteDBHelper.getMyAttendedEvents(userKey);

                if (profileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                }else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }
                profileEventsAdapter = new RecyclerAdapterProfileEvents(getActivity(), profileEventsArrayList);

                eventsRecyclerView.setAdapter(profileEventsAdapter);


                break;
            case R.id.event_coffee_prof:

                eventsAttendedImageView.setImageResource(R.drawable.cc_connect);

                coffeeConnectImageView.setImageResource(R.drawable.event_attended);

                lunchMeetImageView.setImageResource(R.drawable.cc_connect);

                alumniImageView.setImageResource(R.drawable.cc_connect);

                mentorshipImageView.setImageResource(R.drawable.cc_connect);

                profileEventsArrayList = new ArrayList<>();

                profileEventsArrayList = sqLiteDBHelper.getParticularMyAttendedEvents(userKey, "4");

                if (profileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                }else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }

                profileEventsAdapter = new RecyclerAdapterProfileEvents(getActivity(), profileEventsArrayList);

                eventsRecyclerView.setAdapter(profileEventsAdapter);

                break;
            case R.id.event_lunch_prof:

                eventsAttendedImageView.setImageResource(R.drawable.cc_connect);

                coffeeConnectImageView.setImageResource(R.drawable.cc_connect);

                lunchMeetImageView.setImageResource(R.drawable.event_attended);

                alumniImageView.setImageResource(R.drawable.cc_connect);

                mentorshipImageView.setImageResource(R.drawable.cc_connect);

                profileEventsArrayList = new ArrayList<>();

                profileEventsArrayList = sqLiteDBHelper.getParticularMyAttendedEvents(userKey, "3");

                if (profileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                }else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }

                profileEventsAdapter = new RecyclerAdapterProfileEvents(getActivity(), profileEventsArrayList);

                eventsRecyclerView.setAdapter(profileEventsAdapter);

                break;
            case R.id.event_alumini_prof:

                eventsAttendedImageView.setImageResource(R.drawable.cc_connect);

                coffeeConnectImageView.setImageResource(R.drawable.cc_connect);

                lunchMeetImageView.setImageResource(R.drawable.cc_connect);

                alumniImageView.setImageResource(R.drawable.event_attended);

                mentorshipImageView.setImageResource(R.drawable.cc_connect);

                profileEventsArrayList = new ArrayList<>();

                profileEventsArrayList = sqLiteDBHelper.getParticularMyAttendedEvents(userKey, "2");

                if (profileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                }else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }

                profileEventsAdapter = new RecyclerAdapterProfileEvents(getActivity(), profileEventsArrayList);

                eventsRecyclerView.setAdapter(profileEventsAdapter);

                break;
            case R.id.event_mentor_prof:

                eventsAttendedImageView.setImageResource(R.drawable.cc_connect);

                coffeeConnectImageView.setImageResource(R.drawable.cc_connect);

                lunchMeetImageView.setImageResource(R.drawable.cc_connect);

                alumniImageView.setImageResource(R.drawable.cc_connect);

                mentorshipImageView.setImageResource(R.drawable.event_attended);

                profileEventsArrayList = new ArrayList<>();

                profileEventsArrayList = sqLiteDBHelper.getParticularMyAttendedEvents(userKey, "1");

                if (profileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                }else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }

                profileEventsAdapter = new RecyclerAdapterProfileEvents(getActivity(), profileEventsArrayList);

                eventsRecyclerView.setAdapter(profileEventsAdapter);

                break;
            default:

                eventsAttendedImageView.setImageResource(R.drawable.event_attended);

                coffeeConnectImageView.setImageResource(R.drawable.cc_connect);

                lunchMeetImageView.setImageResource(R.drawable.cc_connect);

                alumniImageView.setImageResource(R.drawable.cc_connect);

                mentorshipImageView.setImageResource(R.drawable.cc_connect);

                profileEventsArrayList = new ArrayList<>();

                profileEventsArrayList = sqLiteDBHelper.getMyAttendedEvents(userKey);

                if (profileEventsArrayList.isEmpty()) {
                    eventsAttend.setVisibility(View.GONE);
                }else {
                    eventsAttend.setVisibility(View.VISIBLE);
                }

                profileEventsAdapter = new RecyclerAdapterProfileEvents(getActivity(), profileEventsArrayList);

                eventsRecyclerView.setAdapter(profileEventsAdapter);


        }

    }

}
