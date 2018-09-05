package com.conext.ui.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.conext.Adapter.RecyclerAdapterProfileAlumni;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.ProfileAlumni;
import com.conext.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

import static com.conext.AppManager.getAppContext;


public class Alumni extends Fragment {

    private List<ProfileAlumni> profileAlumniList;
    private LinearLayoutManager mLayoutManager;
    private RecyclerAdapterProfileAlumni recyclerAdapterProfileAlumni;
    ProfileAlumni profileAlumni;
    ArrayList<ProfileAlumni> profileAlumniArrayList;
    RecyclerAdapterProfileAlumni profileAlumniAdapter = null;

    SQLiteDBHelper sqLiteDBHelper;

    public Alumni() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_alumini_profile, container, false);

        sqLiteDBHelper = new SQLiteDBHelper(getAppContext());

        displayAlumni(view);
        // Inflate the layout for this fragment
        return view;
    }

    private void displayAlumni(View view) {
        profileAlumniArrayList = null;
/*
        profileAlumni=new ProfileAlumni("SRM University- kellong School of Management","MCA internatiional application","2007 - 2009","","","");
        profileAlumniArrayList.add(profileAlumni);*/

        profileAlumniArrayList = sqLiteDBHelper.getAluminiDetails(Prefs.getUserKey());

        if (profileAlumniArrayList != null) {
            profileAlumniAdapter = new RecyclerAdapterProfileAlumni(getAppContext(), profileAlumniArrayList);
        }

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_alumni);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(profileAlumniAdapter);
    }


}
