package com.conext.ui.Fragments;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.conext.Adapter.RecyclerViewAdapter;
import com.conext.Adapter.SkillAdapter;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.Network;
import com.conext.model.Skill;
import com.conext.ui.custom.NetworkItemDecorator;
import com.conext.utils.ItemClickListener;
import com.conext.utils.Prefs;

import java.util.ArrayList;


public class NetworkFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<Network> arrayList = null;

    RecyclerView skillRecyclerView;

    private GridLayoutManager lLayout;
    RecyclerViewAdapter adapter;
    SkillAdapter skillAdapter = null;
    ArrayList<Skill> skillArrayList = null;

    SQLiteDBHelper sqLiteDBHelper;
    ImageView skillDots;
    ArrayList<Long> skillFilter = null;
    ArrayList<Long> userKey = null;


    public NetworkFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_network, container, false);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Network");

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());
        skillDots = (ImageView) view.findViewById(R.id.skill_dots);

        Drawable skillDotsDrawable = skillDots.getDrawable();
        if (skillDotsDrawable != null) {
            skillDotsDrawable.mutate();
            skillDotsDrawable.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }

        skillRecyclerView = (RecyclerView) view.findViewById(R.id.skill_container_scroll);

        skillFilter = new ArrayList<>();

        skillArrayList = sqLiteDBHelper.getUserTags(Prefs.getUserKey());
        if (skillArrayList.size() <= 0 && skillArrayList == null) {
            skillArrayList = new ArrayList<>();
        } else {
            for (int i = 0; i < skillArrayList.size(); i++)
                skillFilter.add(skillArrayList.get(i).getSkillId());
        }

        userKey = sqLiteDBHelper.getUserKeyFromTag(skillFilter);

        RecyclerView.LayoutManager mSkillLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        skillRecyclerView.setLayoutManager(mSkillLayoutManager);

        //create an ArrayAdapter from the String Array
        skillAdapter = new SkillAdapter(getActivity(), R.layout.card_skill, skillArrayList);

        //For performance, tell OS RecyclerView won't change size
        skillRecyclerView.setHasFixedSize(true);

        // Assign adapter to recyclerView
        skillRecyclerView.setAdapter(skillAdapter);

        skillAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int id) {
                arrayList = null;
                if (v.findViewById(R.id.skill_name) != null) {
                    if (skillFilter.size() > 0 && skillFilter != null) {
                        boolean sameFlag = false;
                        for (int i = 0; i < skillFilter.size(); i++) {
                            if (skillFilter.get(i) == id) {
                                sameFlag = true;
                            }
                        }
                        if (!sameFlag) {
                            skillFilter.add((long) id);
                        }
                    } else if (skillFilter.size() == 0) {
                        skillFilter.add((long) id);
                    }

                } else {

                    if (skillFilter.size() > 0 && skillFilter != null) {
                        boolean sameFlag = false;
                        for (int i = 0; i < skillFilter.size(); i++) {
                            if (skillFilter.get(i) == id) {
                                sameFlag = true;
                            }
                        }
                        if (sameFlag) {
                            skillFilter.remove((long) id);
                        }
                    } else if (skillFilter.size() == 0) {
                        skillFilter = new ArrayList<>();
                    }

                }
                Log.e("tag", "view Id ==> " + v.findViewById(R.id.skill_name));
                for (int i = 0; i < skillFilter.size(); i++)
                    Log.e("tag", "skillFilter ==> " + skillFilter.get(i));

                userKey = sqLiteDBHelper.getUserKeyFromTag(skillFilter);


                arrayList = new ArrayList<>();
                if (userKey != null)
                    arrayList = sqLiteDBHelper.getNetworkContacts(userKey, Prefs.getUserKey());

                for (int i = 0; i < arrayList.size(); i++) {
                    Network network = arrayList.get(i);
                    if (String.valueOf(network.getId()).equals(Prefs.getUserKey())) {
                        arrayList.remove(i);
                    }

                }

                if (arrayList != null) {
                    adapter = new RecyclerViewAdapter(getActivity(), arrayList);
                    recyclerView.setAdapter(adapter);
                } else {
                    adapter = new RecyclerViewAdapter(getActivity(), new ArrayList<Network>());
                    recyclerView.setAdapter(adapter);
                }

            }
        });

        // Create a grid layout with 12 columns
        // (least common multiple of 3 and 4
        lLayout = new GridLayoutManager(getActivity(), 12, LinearLayoutManager.VERTICAL, false); // MAX NUMBER OF SPACES

        lLayout.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                // 7 is the sum of items in one repeated section
                switch (position % 7) {
                    // first three items span 3 columns each
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        return 3;
                    case 4:
                    case 5:
                    case 6:
                        return 4;
                }
                throw new IllegalStateException("internal error");
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);

        arrayList = sqLiteDBHelper.getNetworkContacts(userKey, Prefs.getUserKey());

        for (int i = 0; i < arrayList.size(); i++) {
            Network network = arrayList.get(i);
            if (String.valueOf(network.getId()).equals(Prefs.getUserKey())) {
                arrayList.remove(i);
            }
        }

        recyclerView.setLayoutManager(lLayout);

        if (arrayList != null) {
            adapter = new RecyclerViewAdapter(getActivity(), arrayList);
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new NetworkItemDecorator());
        }

        return view;
    }

}

