package com.conext.ui.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.conext.Adapter.RecyclerAdapterProfileInfo;
import com.conext.Adapter.SkillAdapter;
import com.conext.AppManager;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.MyMentees;
import com.conext.model.Skill;
import com.conext.ui.OtherProfileActivity;
import com.conext.ui.custom.HexagonMaskView;
import com.conext.utils.ItemClickListener;
import com.conext.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

import static com.conext.AppManager.getAppContext;

public class MyMenteesFragment extends Fragment {

    private Context context;
    private List<MyMentees> myMenteesList;
    private RecyclerAdapterMyMentee recyclerAdapterMyMentee;
    private LinearLayoutManager mLayoutManager;
    MyMentees myMentee;
    ArrayList<MyMentees> myListMentee;
    ArrayList<MyMentees> arrayList = null;
    ArrayList<Skill> skillArrayList1 = null;
    ArrayList<Skill> otherSkillList = null;
    RecyclerAdapterMyMentee menteeAdapter = null;
    ArrayList<Skill> skillArrayList = null;
    private AppManager mAppManager = null;
    SkillAdapter skillAdapter = null;
    RecyclerView skillRecyclerView, recyclerView;
    ImageView skillDots;
    RecyclerAdapterProfileInfo skillInfoAdapter = null;

    SQLiteDBHelper sqLiteDBHelper;

    ArrayList<Long> skillFilter = null;
    ArrayList<Long> eventKey = null;
    SearchView searchView;

    public MyMenteesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_mentees, container, false);
        // Inflate the layout for this fragment

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Mentees");

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());

        skillDots = (ImageView) view.findViewById(R.id.skill_dots);
        searchView = (SearchView) view.findViewById(R.id.mSearch);

        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        ImageView searchButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        ImageView searchMagButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        ImageView searchButtonClose = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);

        searchEditText.setTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        searchEditText.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.white));
        searchButton.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        searchMagButton.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        searchButtonClose.setColorFilter(ContextCompat.getColor(getActivity(), R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        searchView.onActionViewExpanded();
        searchView.setIconified(true);
        searchView.setIconifiedByDefault(false);


        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_mentees);
/*


        skillArrayList = sqLiteDBHelper.getUserTags(Prefs.getUserKey());
        if (skillArrayList.size() <= 0 && skillArrayList == null) {
            skillArrayList = new ArrayList<>();
        } else {
            for (int i = 0; i < skillArrayList.size(); i++)
                skillFilter.add(skillArrayList.get(i).getSkillId());
        }
*/


        Drawable skillDotsDrawable = skillDots.getDrawable();
        if (skillDotsDrawable != null) {
            skillDotsDrawable.mutate();
            skillDotsDrawable.setColorFilter(ContextCompat.getColor(getAppContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }

        displaySkillList(view);

        displayMenteeView(view);


        //SEARCH
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //FILTER AS YOU TYPE
                menteeAdapter.getFilter().filter(query);
                return false;
            }
        });


        return view;

    }

    private void displaySkillList(View view) {

        skillFilter = new ArrayList<>();

        skillArrayList = sqLiteDBHelper.getUserTags(Prefs.getUserKey());
        if (skillArrayList.size() <= 0 && skillArrayList == null) {
            skillArrayList = new ArrayList<>();
        } else {
            for (int i = 0; i < skillArrayList.size(); i++)
                skillFilter.add(skillArrayList.get(i).getSkillId());
        }

        eventKey = sqLiteDBHelper.getEventKeyFromEvent(skillFilter);


        skillRecyclerView = (RecyclerView) view.findViewById(R.id.skill_container_scroll);
        RecyclerView.LayoutManager mSkillLayoutManager = new LinearLayoutManager(getAppContext(), LinearLayoutManager.HORIZONTAL, false);
        skillRecyclerView.setLayoutManager(mSkillLayoutManager);

        //create an ArrayAdaptar from the String Array
        skillAdapter = new SkillAdapter(getAppContext(), R.layout.card_skill, skillArrayList);

        //For performance, tell OS RecyclerView won't change size
        skillRecyclerView.setHasFixedSize(true);

        // Assign adapter to recyclerView
        skillRecyclerView.setAdapter(skillAdapter);


        skillAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int id) {
                //contactArrayList = null;
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

                eventKey = sqLiteDBHelper.getEventKeyFromEvent(skillFilter);

                arrayList = new ArrayList<>();
                if (eventKey != null)
                    arrayList = sqLiteDBHelper.getMyMentee(eventKey, Prefs.getUserKey());

                if (arrayList != null) {

                    menteeAdapter = new RecyclerAdapterMyMentee(getAppContext(), arrayList);

                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(menteeAdapter);
                }

            }
        });


    }

    private void displayMenteeView(View view) {


        arrayList = new ArrayList<>();
        if (eventKey != null)
            arrayList = sqLiteDBHelper.getMyMentee(eventKey, Prefs.getUserKey());

        Log.e("size", "" + arrayList.size());

        if (arrayList != null) {

            menteeAdapter = new RecyclerAdapterMyMentee(getAppContext(), arrayList);

        } else {

            menteeAdapter = new RecyclerAdapterMyMentee(getAppContext(), new ArrayList<MyMentees>());

        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(menteeAdapter);
    }

    class RecyclerAdapterMyMentee extends RecyclerView.Adapter<RecyclerAdapterMyMentee.MyViewHolder> implements Filterable {
        private List<MyMentees> myMenteesList;
        private Context context;
        private LayoutInflater inflater;
        MenteeCustomFilter filter;
        int posi;

        public RecyclerAdapterMyMentee(Context context, List<MyMentees> myMenteesList) {

            this.context = context;
            this.myMenteesList = myMenteesList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View rootView = inflater.inflate(R.layout.card_view_my_mentee, parent, false);
            return new RecyclerAdapterMyMentee.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            posi = position;
            MyMentees myMentees = myMenteesList.get(position);

            holder.menteeName.setText(myMentees.getMenteeName());
            holder.menteeDescription.setText(myMentees.getMenteeDescription());
            holder.contactImage.setImageBitmap(myMentees.getMenteeImage());
           // holder.otherSkill.setText(myMentees.getOtherMentoring());
            holder.contactImage.setBorderColor(ContextCompat.getColor(context, R.color.bg_card));
            holder.menteeInRecycler.setLayoutManager(new LinearLayoutManager(context));

            skillArrayList1 = sqLiteDBHelper.getUserMenteeSkill(myMentees.getMentorID());

            if (skillArrayList1 == null || skillArrayList1.size() == 0) {
                holder.menteeIn.setVisibility(View.GONE);
                holder.menteeInRecycler.setVisibility(View.GONE);
                holder.otherMentee.setVisibility(View.GONE);
                holder.otherSkill.setVisibility(View.GONE);
            } else {
                skillInfoAdapter = new RecyclerAdapterProfileInfo(getAppContext(), skillArrayList1, R.layout.profile_other_interest);
            }

            skillInfoAdapter = new RecyclerAdapterProfileInfo(getAppContext(), skillArrayList1, R.layout.my_mentee_row);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
            holder.menteeInRecycler.setLayoutManager(mLayoutManager);
            holder.menteeInRecycler.setHasFixedSize(true);
            holder.menteeInRecycler.setAdapter(skillInfoAdapter);


            otherSkillList = sqLiteDBHelper.getUserOtherSkill(myMentees.getMentorID());

            if (otherSkillList==null||otherSkillList.size()==0){
                holder.otherMentee.setVisibility(View.GONE);
                holder.view.setVisibility(View.GONE);
                holder.otherSkill.setVisibility(View.GONE);
            }else {
                holder.otherMentee.setVisibility(View.VISIBLE);
                holder.otherSkill.setVisibility(View.VISIBLE);
                holder.otherSkill.setText(myMentees.getOtherMentoring());
            }

            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    Intent intent = new Intent(getActivity(), OtherProfileActivity.class);
                    intent.putExtra("id", myMenteesList.get(position).getMentorID());
                    intent.putExtra("id", Integer.parseInt(myMenteesList.get(position).getMentorID()));
                    startActivity(intent);
                }
            });

            /*holder.menteeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()) {
                        case R.id.mentor_name:
                            Intent intent = new Intent().setClass(v.getContext(), OtherProfileActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            v.getContext().startActivity(intent);
                    }
                }
            });*/
        }

        @Override
        public int getItemCount() {
            return myMenteesList.size();
        }


        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new MenteeCustomFilter(myMenteesList, this);
            }
            return filter;
        }


        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            private TextView menteeName, menteeDescription, menteeIn, otherMentee, otherSkill;
            private ImageView menteeImage;
           // private PorterShapeImageView contactImage;
            private HexagonMaskView contactImage;
            private RecyclerView menteeInRecycler;
            ItemClickListener itemClickListener;
            private View view;
            public MyViewHolder(View itemView) {
                super(itemView);
                menteeName = (TextView) itemView.findViewById(R.id.mentor_name);
                menteeDescription = (TextView) itemView.findViewById(R.id.mentor_description);
                menteeIn = (TextView) itemView.findViewById(R.id.mentee_in);
                menteeInRecycler = (RecyclerView) itemView.findViewById(R.id.mentee_in_recycler);
                otherMentee = (TextView) itemView.findViewById(R.id.other_interest);
                otherSkill = (TextView) itemView.findViewById(R.id.other_skill);
                menteeImage = (ImageView) itemView.findViewById(R.id.tic_contact_selected);
               // contactImage = (PorterShapeImageView) itemView.findViewById(R.id.contact_image);
                contactImage = (HexagonMaskView) itemView.findViewById(R.id.contact_image);
                view = (View) itemView.findViewById(R.id.view);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                this.itemClickListener.onItemClick(v, getLayoutPosition());
            }

            void setItemClickListener(ItemClickListener ic) {
                this.itemClickListener = ic;
            }
        }

    }


    public class MenteeCustomFilter extends Filter {
        private RecyclerAdapterMyMentee contactAdapter;
        List<MyMentees> menteeArrayList;

        public MenteeCustomFilter(List<MyMentees> menteeArrayList, RecyclerAdapterMyMentee contactAdapter) {
            this.contactAdapter = contactAdapter;
            this.menteeArrayList = menteeArrayList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            //CHECK CONSTRAINT VALIDITY
            if (constraint != null && constraint.length() > 0) {
                //CHANGE TO UPPER
                constraint = constraint.toString().toUpperCase();
                //STORE OUR FILTERED PLAYERS
                ArrayList<MyMentees> filteredContact = new ArrayList<>();
                for (int i = 0; i < menteeArrayList.size(); i++) {
                    //CHECK
                    if (menteeArrayList.get(i).getMenteeName().toUpperCase().contains(constraint)) {
                        //ADD PLAYER TO FILTERED PLAYERS
                        filteredContact.add(menteeArrayList.get(i));
                    }
                }
                results.count = filteredContact.size();
                results.values = filteredContact;
            } else {
                results.count = menteeArrayList.size();
                results.values = menteeArrayList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            contactAdapter.myMenteesList = (ArrayList<MyMentees>) results.values;
            contactAdapter.notifyDataSetChanged();
        }
    }



}
