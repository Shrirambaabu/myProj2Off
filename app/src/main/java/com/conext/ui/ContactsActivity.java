package com.conext.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.conext.Adapter.SkillAdapter;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.Contact;
import com.conext.model.Skill;
import com.conext.ui.custom.CustomFilter;
import com.conext.ui.custom.HexagonMaskView;
import com.conext.utils.AlphabetItem;
import com.conext.utils.ItemClickListener;
import com.conext.utils.Prefs;
import com.conext.utils.Utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.myinnos.alphabetsindexfastscrollrecycler.IndexFastScrollRecyclerView;

public class ContactsActivity extends AppCompatActivity implements ItemClickListener {

    IndexFastScrollRecyclerView mRecyclerView, contactsRecyclerViewT;
    RecyclerView skillRecyclerView;
    SearchView searchView;

    ContactAdapter dataAdapter = null;
    SkillAdapter skillAdapter = null;
    ArrayList<Contact> contactArrayList;
    ArrayList<Skill> skillArrayList = null;
    Toolbar toolbar;

    boolean isMultiSelect = false;
    ActionMode mActionMode;

    Menu context_menu;
    ImageView skillDots;
    SQLiteDBHelper sqLiteDBHelper;

    ArrayList<Contact> multiselect_list = new ArrayList<>();

    ArrayList<Long> skillFilter = null;
    ArrayList<Long> userKey = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("All Contacts");
        }

        sqLiteDBHelper = new SQLiteDBHelper(ContactsActivity.this);

        contactsRecyclerViewT = (IndexFastScrollRecyclerView) findViewById(R.id.recyclerViewContact);
        skillRecyclerView = (RecyclerView) findViewById(R.id.skill_container_scroll);
        mRecyclerView = (IndexFastScrollRecyclerView) findViewById(R.id.fast_scroller);

        skillDots = (ImageView) findViewById(R.id.skill_dots);

        searchViewImplementation();

        skillFilter();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(ContactsActivity.this);
        contactsRecyclerViewT.setLayoutManager(mLayoutManager);

        //create an ArrayAdaptar from the String Array
        dataAdapter = new ContactAdapter(ContactsActivity.this, R.layout.card_contact, contactArrayList, multiselect_list);

        //For performance, tell OS RecyclerView won't change size
        contactsRecyclerViewT.setHasFixedSize(true);
        contactsRecyclerViewT.setItemViewCacheSize(20);
        contactsRecyclerViewT.setDrawingCacheEnabled(true);
        contactsRecyclerViewT.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        // Assign adapter to recyclerView
        contactsRecyclerViewT.setAdapter(dataAdapter);

        //SEARCH
        queryTextListener();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        // mRecyclerView.setAdapter(dataAdapter);
        contactsRecyclerViewT.setIndexTextSize(12);
        contactsRecyclerViewT.setIndexBarColor("#33334c");
        contactsRecyclerViewT.setIndexBarCornerRadius(0);
        contactsRecyclerViewT.setIndexBarTransparentValue((float) 0.4);
        contactsRecyclerViewT.setIndexbarMargin(0);
        contactsRecyclerViewT.setIndexbarWidth(40);
        contactsRecyclerViewT.setPreviewPadding(0);
        contactsRecyclerViewT.setIndexBarTextColor("#FFFFFF");
        contactsRecyclerViewT.setIndexBarVisibility(true);

        ArrayList<AlphabetItem> mAlphabetItems = new ArrayList<>();
        List<String> strAlphabets = new ArrayList<>();
        for (int i = 0; i < contactArrayList.size(); i++) {
            Contact contact = contactArrayList.get(i);
            String name = contact.getName();
            if (name == null || name.trim().isEmpty())
                continue;
            String word = name.substring(0, 1);
            if (!strAlphabets.contains(word)) {
                strAlphabets.add(word);
                mAlphabetItems.add(new AlphabetItem(i, word, false));
            }
        }
    }

    private void multi_select(ArrayList<Contact> multiselect_list) {

        if (mActionMode == null)
            mActionMode = startActionMode(mActionModeCallback);

        if (multiselect_list.size() > 0) {
            mActionMode.setTitle("" + multiselect_list.size() + " Selected");
        } else {
            mActionMode.setTitle("All Contacts");
        }
        refreshAdapter();
    }


    public void refreshAdapter() {
        dataAdapter.selected_usersList = multiselect_list;
        dataAdapter.contactList = contactArrayList;
        dataAdapter.notifyDataSetChanged();
    }


    private void skillFilter() {

        skillFilter = new ArrayList<>();

        skillArrayList = sqLiteDBHelper.getUserTags(Prefs.getUserKey());
        if (skillArrayList.size() <= 0 && skillArrayList == null) {
            skillArrayList = new ArrayList<>();
        } else {
            for (int i = 0; i < skillArrayList.size(); i++)
                skillFilter.add(skillArrayList.get(i).getSkillId());
        }

        Drawable skillDotsDrawable = skillDots.getDrawable();
        if (skillDotsDrawable != null) {
            skillDotsDrawable.mutate();
            skillDotsDrawable.setColorFilter(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimary), PorterDuff.Mode.SRC_ATOP);
        }

        RecyclerView.LayoutManager mSkillLayoutManager = new LinearLayoutManager(ContactsActivity.this, LinearLayoutManager.HORIZONTAL, false);
        skillRecyclerView.setLayoutManager(mSkillLayoutManager);

        //create an ArrayAdapter from the String Array
        skillAdapter = new SkillAdapter(ContactsActivity.this, R.layout.card_skill, skillArrayList, this);

        //For performance, tell OS RecyclerView won't change size
        skillRecyclerView.setHasFixedSize(true);

        // Assign adapter to recyclerView
        skillRecyclerView.setAdapter(skillAdapter);

        skillAdapter.setOnItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int id) {
                contactArrayList = null;
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

                contactArrayList = new ArrayList<>();
                if (userKey != null)
                    contactArrayList.addAll(sqLiteDBHelper.getContact(userKey));

                for (int i = 0; i < contactArrayList.size(); i++) {
                    Contact contact = contactArrayList.get(i);
                    if (contact.getId().equals(Prefs.getUserKey())) {
                        contactArrayList.remove(i);
                    }
                }

                //create an ArrayAdaptar from the String Array
                dataAdapter = new ContactAdapter(ContactsActivity.this, R.layout.card_contact, contactArrayList, multiselect_list);

                //For performance, tell OS RecyclerView won't change size
                contactsRecyclerViewT.setHasFixedSize(true);

                // Assign adapter to recyclerView
                contactsRecyclerViewT.setAdapter(dataAdapter);

            }
        });

        userKey = sqLiteDBHelper.getUserKeyFromTag(skillFilter);

        contactArrayList = new ArrayList<>();

        contactArrayList.addAll(sqLiteDBHelper.getContact(userKey));

        for (int i = 0; i < contactArrayList.size(); i++) {
            Contact contact = contactArrayList.get(i);
            if (contact.getId().equals(Prefs.getUserKey())) {
                contactArrayList.remove(i);
            }
        }

        Collections.sort(contactArrayList, new Comparator<Contact>() {
            @Override
            public int compare(Contact lhs, Contact rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
    }

    private void queryTextListener() {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                //FILTER AS YOU TYPE
                dataAdapter.getFilter().filter(query);
                return false;
            }
        });

    }

    private void searchViewImplementation() {

        searchView = (SearchView) findViewById(R.id.mSearch);

        EditText searchEditText = (EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
        ImageView searchButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_button);
        ImageView searchMagButton = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_mag_icon);
        ImageView searchButtonClose = (ImageView) searchView.findViewById(android.support.v7.appcompat.R.id.search_close_btn);

        searchEditText.setTextColor(ContextCompat.getColor(ContactsActivity.this, R.color.white));
        searchEditText.setHintTextColor(ContextCompat.getColor(ContactsActivity.this, R.color.white));
        searchButton.setColorFilter(ContextCompat.getColor(ContactsActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        searchMagButton.setColorFilter(ContextCompat.getColor(ContactsActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        searchButtonClose.setColorFilter(ContextCompat.getColor(ContactsActivity.this, R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);

        searchView.onActionViewExpanded();
        searchView.setIconified(true);
        searchView.setIconifiedByDefault(false);
    }

    @Override
    public void onItemClick(View v, int pos) {
        if (skillArrayList != null)
            Log.e("tag", "id==> " + skillArrayList.get(pos).getSkillId());
        Log.e("tag", "id==> " + pos);
        if (skillAdapter == null)
            Log.e("tag", "clicked");
    }

    private ActionMode.Callback mActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            // Inflate a menu resource providing context menu items
            toolbar.setVisibility(View.GONE);
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contact, menu);
            context_menu = menu;
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false; // Return false if nothing is done
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.done:
                    //alertDialogHelper.showAlertDialog("","Delete Contact","DELETE","CANCEL",1,false);
                    if (multiselect_list != null) {
                        int param = getIntent().getIntExtra("selectedPosition", -1);
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("selectedPosition", param);
                        for (int i = 0; i < multiselect_list.size(); i++) {
                            Log.e("tag", "selectedPosition ==> " + multiselect_list.get(i).getStatus());
                            multiselect_list.get(i).setImage(null);
                        }
                        resultIntent.putExtra("contacts", multiselect_list);
                        setResult(Activity.RESULT_OK, resultIntent);
                        onBackPressed();
                    }
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mActionMode = null;
            isMultiSelect = false;
            multiselect_list = new ArrayList<>();
            refreshAdapter();
            onBackPressed();
        }
    };

    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> implements SectionIndexer, Filterable {

        public List<Contact> contactList;
        List<Contact> filteredUsersList;
        CustomFilter filter;
        Context mContext;
        int itemResource;
        ArrayList<Contact> selected_usersList = new ArrayList<>();

        private ArrayList<Integer> mSectionPositions;

        ContactAdapter(Context mContext, int itemResource, List<Contact> contactList, ArrayList<Contact> selectedList) {
            this.contactList = contactList;
            this.mContext = mContext;
            this.itemResource = itemResource;
            this.selected_usersList = selectedList;
            this.filteredUsersList = contactList;
        }

        @Override
        public ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(itemResource, parent, false);
            return new ContactHolder(v);
        }

        @Override
        public void onBindViewHolder(final ContactHolder holder, int position) {
            final Contact contact = contactList.get(position);

            holder.colg.setText(contact.getColg());
            holder.name.setText(contact.getName());
            holder.job.setText(contact.getJob());
            //  holder.img.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
            holder.img.setBorderColor(R.color.bg_card);
            if (contact.getImage() != null) {
                holder.img.setImageBitmap(ThumbnailUtils.extractThumbnail(Utility.getPhoto(contact.getImage()), 64, 64));
                holder.img.setVisibility(View.VISIBLE);
            } else {
                holder.img.setImageBitmap(null);
                holder.img.setVisibility(View.INVISIBLE);
            }

            if (selected_usersList.contains(contactList.get(position))) {
                holder.rr_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.bg_card_selected));
                holder.selected.setVisibility(View.VISIBLE);

                // contact.setStatus("0");
                Log.e("tag", "setStatus" + contact.getStatus());

                // holder.mentor.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimary));
                //holder.participant.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimary));
                //holder.mentee.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorAccent));
                holder.mentee.setVisibility(View.VISIBLE);
                holder.mentor.setVisibility(View.VISIBLE);
                holder.participant.setVisibility(View.VISIBLE);

                if (TextUtils.isEmpty(contact.getStatus()) || "2".equalsIgnoreCase(contact.getStatus())) {
                    holder.mentor.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimary));
                    holder.mentee.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimary));
                    holder.participant.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorAccent));
                } else if ("0".equalsIgnoreCase(contact.getStatus())) {
                    holder.mentor.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimary));
                    holder.participant.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimary));
                    holder.mentee.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorAccent));
                } else if ("1".equalsIgnoreCase(contact.getStatus())) {
                    holder.mentor.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorAccent));
                    holder.participant.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimary));
                    holder.mentee.setBackgroundColor(ContextCompat.getColor(ContactsActivity.this, R.color.colorPrimary));
                }


                holder.mentor.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selected_usersList.remove(contact);

                        contact.setStatus("1");

                        selected_usersList.add(contact);

                        notifyDataSetChanged();
                    }
                });


                holder.mentee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selected_usersList.remove(contact);

                        contact.setStatus("0");

                        selected_usersList.add(contact);

                        notifyDataSetChanged();

                    }
                });


                holder.participant.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        selected_usersList.remove(contact);

                        contact.setStatus("2");

                        selected_usersList.add(contact);

                        notifyDataSetChanged();
                    }
                });


            } else {
                holder.rr_layout.setBackgroundColor(ContextCompat.getColor(mContext, R.color.cement));
                holder.selected.setVisibility(View.GONE);

                holder.mentee.setVisibility(View.GONE);
                holder.mentor.setVisibility(View.GONE);
                holder.participant.setVisibility(View.GONE);

                holder.mentor.setOnClickListener(null);
                holder.mentee.setOnClickListener(null);
                holder.participant.setOnClickListener(null);
            }


           /* holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selected_usersList.contains(contact))
                        selected_usersList.remove(contact);
                    else
                        selected_usersList.add(contact);
                    notifyDataSetChanged();
                }
            });*/

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    // default participant
                    contact.setStatus("2");

                    addOrRemoveInMultiSelect(contact);

                    notifyDataSetChanged();
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(ContactsActivity.this, OtherProfileActivity.class);
                    intent.putExtra("id", Integer.parseInt(contact.getId()));
                    startActivity(intent);
                    return true;
                }
            });


        }

        private void addOrRemoveInMultiSelect(Contact contact) {
            if (selected_usersList.contains(contact))
                selected_usersList.remove(contact);
            else
                selected_usersList.add(contact);

            multi_select(selected_usersList);
        }

        @Override
        public int getItemCount() {
            return this.contactList.size();
        }

        @Override
        public Object[] getSections() {
            List<String> sections = new ArrayList<>(26);
            mSectionPositions = new ArrayList<>(26);
            for (int i = 0, size = contactList.size(); i < size; i++) {
                Contact contact = contactList.get(i);
                String section = String.valueOf(contact.getName().charAt(0)).toUpperCase();
                if (!sections.contains(section)) {
                    sections.add(section);
                    mSectionPositions.add(i);
                }
            }
            return sections.toArray(new String[0]);
        }

        @Override
        public int getPositionForSection(int sectionIndex) {
            return mSectionPositions.get(sectionIndex);
        }

        @Override
        public int getSectionForPosition(int position) {
            return 0;
        }

        @Override
        public Filter getFilter() {
            if (filter == null) {
                filter = new CustomFilter(filteredUsersList, this);
            }
            return filter;
        }

        class ContactHolder extends RecyclerView.ViewHolder {

            private TextView name, colg, job, id, mentee, mentor, participant;
            private ImageView selected;
            // private PorterShapeImageView img;
            private HexagonMaskView img;
            private RelativeLayout rr_layout;
            ItemClickListener itemClickListener;

            ContactHolder(View itemView) {
                super(itemView);
                // Set up the UI widgets of the holder
                // img = (PorterShapeImageView) itemView.findViewById(R.id.contact_image);
                img = (HexagonMaskView) itemView.findViewById(R.id.contact_image);
                name = (TextView) itemView.findViewById(R.id.contact_name);
                colg = (TextView) itemView.findViewById(R.id.contact_colg);
                job = (TextView) itemView.findViewById(R.id.contact_job);
                mentee = (TextView) itemView.findViewById(R.id.mentee);
                mentor = (TextView) itemView.findViewById(R.id.mentor);
                participant = (TextView) itemView.findViewById(R.id.participant);
                rr_layout = (RelativeLayout) itemView.findViewById(R.id.rr_layout);
                selected = (ImageView) itemView.findViewById(R.id.tic_contact_selected);

            }
        }
    }
}

