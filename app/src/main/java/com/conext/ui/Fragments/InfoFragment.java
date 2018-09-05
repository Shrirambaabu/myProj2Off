package com.conext.ui.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.Info;
import com.conext.ui.RegistrationPersonaliseActivity;
import com.conext.ui.custom.AddInfoDialog;
import com.conext.utils.Prefs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.conext.AppManager.getAppContext;

public class InfoFragment extends Fragment {

    public static final int REQUEST_CODE = 111;

    InfoAdapter dataAdapter = null;
    ImageView addEduImageView;
    TextView addEduTextView, place;
    ArrayList<Info> infoArrayList;
    Info info;
    SQLiteDBHelper sqLiteDBHelper;
    RecyclerView recyclerView;
    private Menu menu;
    Context context;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        setHasOptionsMenu(true);

        sqLiteDBHelper = new SQLiteDBHelper(getAppContext());

        //mapping
       /* addEduImageView = (ImageView) view.findViewById(R.id.add_edu);
        addEduTextView = (TextView) view.findViewById(R.id.add_text);*/
        place = (TextView) view.findViewById(R.id.place);

        //attaching listener
        //  addEduImageView.setOnClickListener(this);
        // addEduTextView.setOnClickListener(this);
        //Generate list View from ArrayList
        if (savedInstanceState == null || !savedInstanceState.containsKey("info")) {
            displayListView(view);
        } else {
            infoArrayList = savedInstanceState.getParcelableArrayList("info");
            dataAdapter = new InfoAdapter(getAppContext(), R.layout.info_row_list, infoArrayList);
            dataAdapter.notifyDataSetChanged();
        }

        String address = getArguments().getString("location");
        if (address != null)
            place.setText(address);


        final Handler ha = new Handler();
        ha.postDelayed(new Runnable() {

            @Override
            public void run() {
                //call function
                place.setText(Prefs.getLoc());
                ha.postDelayed(this, 10000);
            }
        }, 10000);

        return view;
    }

    @Override
    public void setUserVisibleHint(boolean visible) {
        super.setUserVisibleHint(visible);
        if (visible && isResumed()) {
            onResume();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!getUserVisibleHint()) {
            return;
        }

        RegistrationPersonaliseActivity RegistrationPersonaliseActivity = (RegistrationPersonaliseActivity) getActivity();
        RegistrationPersonaliseActivity.floatingActionButtonAddInfo.show();
        RegistrationPersonaliseActivity.floatingActionButtonAddInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addEduDetails();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void displayListView(View view) {
        //Array list of skillArrayList
        infoArrayList = new ArrayList<Info>();

        recyclerView = (RecyclerView) view.findViewById(R.id.info_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
        recyclerView.setLayoutManager(mLayoutManager);

        //create an ArrayAdapter from the String Array
        dataAdapter = new InfoAdapter(getAppContext(), R.layout.info_row_list, infoArrayList);

        //For performance, tell OS RecyclerView won't change size
        recyclerView.setHasFixedSize(true);

        // Assign adapter to recyclerView
        recyclerView.setAdapter(dataAdapter);

    }

    private void addEduDetails() {
        Intent popupAddIntent = new Intent(getAppContext(), AddInfoDialog.class);
        startActivityForResult(popupAddIntent, REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                String major = data.getStringExtra("major");
                String edu = data.getStringExtra("edu");
                String uni = data.getStringExtra("uni");
                String univ = data.getStringExtra("univ");
                Log.e("tag", "selected univ" + univ);
                String startYear = data.getStringExtra("year_start");
                String endYear = data.getStringExtra("year_end");

                info = new Info(major, edu, uni, startYear + "-" + endYear);

                sqLiteDBHelper.addUserInfo(Prefs.getUserKey(), String.valueOf(univ), startYear, endYear, major, edu);
                infoArrayList.add(info);
                Collections.sort(infoArrayList, new Comparator<Info>() {
                    @Override
                    public int compare(Info lhs, Info rhs) {
                        return rhs.getYear().compareTo(lhs.getYear());
                    }
                });
                dataAdapter.notifyDataSetChanged();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(getAppContext(), "userCancelled", Toast.LENGTH_LONG).show();
            }
        }
    }


    class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.InfoHolder> {

        private List<Info> infoList;
        private Context context;
        private int itemResource;

        InfoAdapter(Context context, int itemResource, List<Info> infoList) {
            this.infoList = infoList;
            this.context = context;
            this.itemResource = itemResource;
        }

        @Override
        public InfoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(itemResource, parent, false);
            return new InfoHolder(v);
        }

        @Override
        public void onBindViewHolder(InfoHolder holder, int position) {
            Info info = infoList.get(holder.getAdapterPosition());

            // 4. Bind the data to the ViewHolder
            if (info.getMajor().equals("")) {
                holder.major_layout.setVisibility(View.GONE);
                holder.img1.setAlpha(0.0f);
                holder.img2.setAlpha(1f);
            } else {
                holder.major_layout.setVisibility(View.VISIBLE);
                holder.img1.setAlpha(1f);
                holder.img2.setAlpha(0.0f);
            }
            holder.major.setText(info.getMajor());
            holder.eduQualification.setText(info.getEduQualification());
            holder.univName.setText(info.getUnivName());
            holder.year.setText(info.getYear());
        }

        @Override
        public int getItemCount() {
            return this.infoList.size();
        }

        class InfoHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

            private TextView major, eduQualification, univName, year;
            private LinearLayout major_layout;
            private ImageView img1, img2;

            private Info info;

            InfoHolder(View itemView) {
                super(itemView);
                // Set up the UI widgets of the holder
                this.img1 = (ImageView) itemView.findViewById(R.id.call_img);
                this.img2 = (ImageView) itemView.findViewById(R.id.call_img2);
                this.major_layout = (LinearLayout) itemView.findViewById(R.id.major_layout);
                this.major = (TextView) itemView.findViewById(R.id.major);
                this.eduQualification = (TextView) itemView.findViewById(R.id.edu_qualification);
                this.univName = (TextView) itemView.findViewById(R.id.univ_name);
                this.year = (TextView) itemView.findViewById(R.id.year);
                // Set the "onClick" listener of the holder

                itemView.setOnLongClickListener(this);
            }


            @Override
            public boolean onLongClick(View v) {
                return false;
            }
        }

    }

}
