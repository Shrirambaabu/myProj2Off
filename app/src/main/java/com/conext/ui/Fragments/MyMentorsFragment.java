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
import com.conext.model.MyMentor;
import com.conext.model.Skill;
import com.conext.ui.OtherProfileActivity;
import com.conext.ui.custom.HexagonMaskView;
import com.conext.utils.ItemClickListener;
import com.conext.utils.Prefs;

import java.util.ArrayList;
import java.util.List;

import static com.conext.AppManager.getAppContext;


public class MyMentorsFragment extends Fragment {

    private Context context;
    private List<MyMentor> myMentorList;
    private LinearLayoutManager mLayoutManager;
    private RecyclerAdapterMyMentor recyclerAdapterMyMentor;
    MyMentor myMentor;
    ArrayList<MyMentor> myMentorArrayList;
    ArrayList<MyMentor> arrayList = null;
    RecyclerAdapterMyMentor mentorAdapter = null;
    ArrayList<Skill> skillArrayList1 = null;
    ArrayList<Skill> otherSkillList = null;
    private AppManager mAppManager = null;
    SkillAdapter skillAdapter = null;
    RecyclerView skillRecyclerView;
    ImageView skillDots;
    ArrayList<Skill> skillArrayList = null;
    RecyclerAdapterProfileInfo skillInfoAdapter = null;

    ArrayList<Long> skillFilter = null;
    ArrayList<Long> userKey = null;
    RecyclerView recyclerView;

    SQLiteDBHelper sqLiteDBHelper;
    SearchView searchView;


    public MyMentorsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_mentors, container, false);

        if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Mentors");

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());

        skillDots = (ImageView) view.findViewById(R.id.skill_dots);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_mentors);

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

        Drawable skillDotsDrawable = skillDots.getDrawable();
        if (skillDotsDrawable != null) {
            skillDotsDrawable.mutate();
            skillDotsDrawable.setColorFilter(ContextCompat.getColor(getAppContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }

        displaySkillListMentor(view);

        displayMentorView(view);

        //SEARCH
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //FILTER AS YOU TYPE
                mentorAdapter.getFilter().filter(query);
                return false;
            }
        });


        return view;

    }

    private void displayMentorView(View view) {

        arrayList = new ArrayList<>();
        if (userKey != null)
            arrayList = sqLiteDBHelper.getMyMentors(userKey, Prefs.getUserKey());

        Log.e("size", "" + arrayList.size());

        if (arrayList != null) {
                   /* adapter = new RecyclerViewAdapter(getActivity(), arrayList);
                    recyclerView.setAdapter(adapter);*/

            mentorAdapter = new RecyclerAdapterMyMentor(getAppContext(), arrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(mentorAdapter);
        } else {
            mentorAdapter = new RecyclerAdapterMyMentor(getAppContext(), new ArrayList<MyMentor>());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(mentorAdapter);

        }

    }


    private void displaySkillListMentor(View view) {

        skillFilter = new ArrayList<>();

        skillArrayList = sqLiteDBHelper.getUserTags(Prefs.getUserKey());
        if (skillArrayList.size() <= 0 && skillArrayList == null) {
            skillArrayList = new ArrayList<>();
        } else {
            for (int i = 0; i < skillArrayList.size(); i++)
                skillFilter.add(skillArrayList.get(i).getSkillId());
        }

        //userKey = sqLiteDBHelper.getUserKeyFromTag(skillFilter);
        userKey = sqLiteDBHelper.getEventKeyFromEvent(skillFilter);

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

                userKey = sqLiteDBHelper.getEventKeyFromEvent(skillFilter);

                arrayList = new ArrayList<>();
                if (userKey != null)
                    arrayList = sqLiteDBHelper.getMyMentors(userKey, Prefs.getUserKey());

                if (arrayList != null) {
                    Log.e("tag", "arrayList ==>arrayList here");
                   /* adapter = new RecyclerViewAdapter(getActivity(), arrayList);
                    recyclerView.setAdapter(adapter);*/
                    mentorAdapter = new RecyclerAdapterMyMentor(getAppContext(), arrayList);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
                    recyclerView.setLayoutManager(mLayoutManager);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setAdapter(mentorAdapter);
                }

            }
        });

    }
    class RecyclerAdapterMyMentor extends RecyclerView.Adapter<RecyclerAdapterMyMentor.MyViewHolder> implements Filterable {

        private List<MyMentor> myMentorList;
        private Context context;
        private LayoutInflater inflater;
        MentorCustomFilter filter;

        public RecyclerAdapterMyMentor(Context context, List<MyMentor> myMentorList) {

            this.context = context;
            this.myMentorList = myMentorList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = inflater.inflate(R.layout.card_view_my_mentors, parent, false);
            return new RecyclerAdapterMyMentor.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(MyViewHolder holder, final int position) {
            MyMentor myMentor = myMentorList.get(position);
            holder.mentorName.setText(myMentor.getMentorName());
            holder.mentorDescription.setText(myMentor.getMentorDescription());
            holder.contactImage.setImageBitmap(myMentor.getMentorImage());
            //   holder.otherSkillJoin.setText(myMentor.getOtherMentoring());
            holder.contactImage.setBorderColor(ContextCompat.getColor(context, R.color.bg_card));
            holder.mentorIn.setLayoutManager(new LinearLayoutManager(context));

            skillArrayList1 = sqLiteDBHelper.getUserMentorSkill(myMentor.getMentorID());

            if (skillArrayList1 == null || skillArrayList1.size() == 0) {
                holder.mentorInText.setVisibility(View.GONE);
                holder.mentorIn.setVisibility(View.GONE);
               /* holder.otherSkillJoin.setVisibility(View.GONE);
                holder.otherSkill.setVisibility(View.GONE);*/
            } else {
                skillInfoAdapter = new RecyclerAdapterProfileInfo(getAppContext(), skillArrayList1, R.layout.profile_other_interest);
            }

            skillInfoAdapter = new RecyclerAdapterProfileInfo(getAppContext(), skillArrayList1, R.layout.my_mentor_row);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
            holder.mentorIn.setLayoutManager(mLayoutManager);
            holder.mentorIn.setHasFixedSize(true);
            holder.mentorIn.setAdapter(skillInfoAdapter);


            otherSkillList = sqLiteDBHelper.getUserOtherSkill(myMentor.getMentorID());

            if (otherSkillList == null || otherSkillList.size() == 0) {
                holder.otherSkillJoin.setVisibility(View.GONE);
                holder.otherSkill.setVisibility(View.GONE);
                holder.view.setVisibility(View.GONE);
            } else {
                holder.otherSkillJoin.setVisibility(View.VISIBLE);
                holder.otherSkill.setVisibility(View.VISIBLE);
                holder.otherSkillJoin.setText(myMentor.getOtherMentoring());
            }


            //IMPLEMENT CLICK LISTENER
            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    Log.e("tag", myMentorList.get(pos).getMentorID());
                    Intent intent = new Intent(getActivity(), OtherProfileActivity.class);
                    intent.putExtra("id", myMentorList.get(position).getMentorID());
                    intent.putExtra("id", Integer.parseInt(myMentorList.get(position).getMentorID()));
                    startActivity(intent);
                }
            });

        }

        @Override
        public int getItemCount() {
            return myMentorList.size();
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new MentorCustomFilter(myMentorList, this);
            }
            return filter;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView mentorName, mentorDescription, otherSkillJoin, otherSkill, mentorInText;
            private RecyclerView mentorIn;
            private ImageView mentorImage;
            //private PorterShapeImageView contactImage;
            private HexagonMaskView contactImage;
            ItemClickListener itemClickListener;
            private View view;

            public MyViewHolder(View itemView) {
                super(itemView);
                mentorInText = (TextView) itemView.findViewById(R.id.mentor);
                mentorName = (TextView) itemView.findViewById(R.id.mentor_name);
                otherSkill = (TextView) itemView.findViewById(R.id.other_interest);
                mentorDescription = (TextView) itemView.findViewById(R.id.mentor_description);
                mentorIn = (RecyclerView) itemView.findViewById(R.id.mentor_in_recycler);
                otherSkillJoin = (TextView) itemView.findViewById(R.id.other_interest_join);
                mentorImage = (ImageView) itemView.findViewById(R.id.tic_contact_selected);
                //contactImage = (PorterShapeImageView) itemView.findViewById(R.id.contact_image);
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


    public class MentorCustomFilter extends Filter {
        private RecyclerAdapterMyMentor contactAdapter;
        List<MyMentor> menteeArrayList;

        public MentorCustomFilter(List<MyMentor> menteeArrayList, RecyclerAdapterMyMentor contactAdapter) {
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
                ArrayList<MyMentor> filteredContact = new ArrayList<>();
                for (int i = 0; i < menteeArrayList.size(); i++) {
                    //CHECK
                    if (menteeArrayList.get(i).getMentorName().toUpperCase().contains(constraint)) {
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
            contactAdapter.myMentorList = (ArrayList<MyMentor>) results.values;
            contactAdapter.notifyDataSetChanged();
        }
    }



}
