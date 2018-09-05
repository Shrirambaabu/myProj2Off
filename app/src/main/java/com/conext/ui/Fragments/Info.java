package com.conext.ui.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.conext.Adapter.RecyclerAdapterProfileInfo;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.Skill;
import com.conext.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

import static com.conext.AppManager.getAppContext;


public class Info extends Fragment {


    private List<Skill> skillList;
    private LinearLayoutManager mLayoutManager;
    private RecyclerAdapterProfileInfo recyclerAdapterProfileInfo;
    Skill profileInfoMentee;
    ArrayList<Skill> infoMenteeArrayList;
    RecyclerAdapterProfileInfo infoMenteeAdapter = null;

    private CardView mentorCardView, menteeCardView;
    private TextView otherIntrestSKll;
    Skill profileInfoList;
    ArrayList<Skill> skillArrayList = null;
    RecyclerAdapterProfileInfo skillInfoAdapter = null;

    private LinearLayoutManager otherInterestLayoutManager;
    Skill profileInfoOtherList;
    ArrayList<Skill> skillOtherArrayList;
    RecyclerAdapterProfileInfo skillInfoOtherAdapter = null;

    private SQLiteDBHelper sqLiteDBHelper;

    public Info() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_info_profile, container, false);

        sqLiteDBHelper = new SQLiteDBHelper(getAppContext());

        mentorCardView = (CardView) view.findViewById(R.id.mentorCard);
        menteeCardView = (CardView) view.findViewById(R.id.menteeCard);
        otherIntrestSKll = (TextView) view.findViewById(R.id.other_interest_text);

        displayInfo(view);
        // Inflate the layout for this fragment
        return view;
    }

    private void displayInfo(View view) {
        /*
        skillArrayList=new ArrayList<Skill>();
        profileInfoList=new Skill(R.drawable.info_mentor_profile,"Basic Strategy");
        skillArrayList.add(profileInfoList);
        profileInfoList=new Skill(R.drawable.info_mentor_profile,"Mergers & Aquititions");
        skillArrayList.add(profileInfoList);*/

        /*infoMenteeArrayList=new ArrayList<Skill>();
        profileInfoMentee=new Skill(R.drawable.info_mentee_in,"Management Consultancy");
        infoMenteeArrayList.add(profileInfoMentee);
        profileInfoMentee=new Skill(R.drawable.info_mentee_in,"Product Development");
        infoMenteeArrayList.add(profileInfoMentee);
        profileInfoMentee=new Skill(R.drawable.info_mentee_in,"Competition Analysis");
        infoMenteeArrayList.add(profileInfoMentee);*/
/*
        skillOtherArrayList=new ArrayList<Skill>();
        profileInfoOtherList=new Skill(R.drawable.locate_orange,"Basic Strategy");
        skillOtherArrayList.add(profileInfoOtherList);
        profileInfoOtherList=new Skill(R.drawable.locate_orange,"Product Development");
        skillOtherArrayList.add(profileInfoOtherList);*/


        skillArrayList = sqLiteDBHelper.getUserMentorSkill(Prefs.getUserKey());

        if (skillArrayList.isEmpty()) {
            mentorCardView.setVisibility(View.GONE);
        } else {
            skillInfoAdapter = new RecyclerAdapterProfileInfo(getAppContext(), skillArrayList, R.layout.profile_other_interest);
        }


        skillInfoAdapter=new RecyclerAdapterProfileInfo(getAppContext(),skillArrayList,R.layout.profile_mentor);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.list_mentor_recycler);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(skillInfoAdapter);


        infoMenteeArrayList = sqLiteDBHelper.getUserMenteeSkill(Prefs.getUserKey());

        if (infoMenteeArrayList.isEmpty()) {
            menteeCardView.setVisibility(View.GONE);
        } else {
            infoMenteeAdapter = new RecyclerAdapterProfileInfo(getAppContext(), infoMenteeArrayList, R.layout.profile_mentee);
        }


        RecyclerView menteeRecyclerView = (RecyclerView) view.findViewById(R.id.list_mentee_recycler);
        RecyclerView.LayoutManager menteeLayoutManager = new LinearLayoutManager(getAppContext());
        menteeRecyclerView.setLayoutManager(menteeLayoutManager);
        menteeRecyclerView.setHasFixedSize(true);
        menteeRecyclerView.setAdapter(infoMenteeAdapter);

        skillOtherArrayList = sqLiteDBHelper.getUserOtherSkill(Prefs.getUserKey());

        if (skillOtherArrayList.isEmpty()) {

            otherIntrestSKll.setVisibility(View.GONE);
        } else {
            skillInfoOtherAdapter = new RecyclerAdapterProfileInfo(getAppContext(), skillOtherArrayList, R.layout.profile_other_interest);
        }



        RecyclerView otherInterestRecyclerView=(RecyclerView) view.findViewById(R.id.recycler_other_info);
        RecyclerView.LayoutManager otherInterestLayoutManager = new LinearLayoutManager(getAppContext());
        otherInterestRecyclerView.setLayoutManager(otherInterestLayoutManager);
        otherInterestRecyclerView.setHasFixedSize(true);
        otherInterestRecyclerView.setAdapter(skillInfoOtherAdapter);
    }

}
