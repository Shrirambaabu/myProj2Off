package com.conext.ui.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.conext.Adapter.ContactAdapter;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.Contact;
import com.conext.model.MyEvents;
import com.conext.model.db.USER_EVENT;
import com.conext.ui.ContactsActivity;
import com.conext.ui.CreateEventActivity;
import com.conext.ui.custom.HexagonMaskView;
import com.conext.utils.ItemClickListener;
import com.conext.utils.Prefs;
import com.conext.utils.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.conext.AppManager.getAppContext;


public class MyEventsFragment extends Fragment implements View.OnClickListener {
    private boolean fabStatus = false;
    private FrameLayout fraToolFloat;
    private FloatingActionButton fabSetting;
    private FloatingActionButton fabSub1;
    private FloatingActionButton fabSub2;
    private FloatingActionButton fabSub3;
    private FloatingActionButton fabSub4;

    private LinearLayout linFabSetting;
    private LinearLayout linFab1;
    private LinearLayout linFab2;
    private LinearLayout linFab3;
    private LinearLayout linFab4;
    MyEvents myEvents;
    boolean flag = true;
    ArrayList<USER_EVENT> myEventsArrayList;
    RecyclerAdapterMyEvents myEventsAdapter = null;
    RecyclerView recyclerView;
    String dateStartMyEvent;
    String dateEndMyEvent;
    String monthStartMyEvent;
    String monthEndMyEvent;
    String dateTimeMyEvent;
    String dateTimeEndMyEvent, dateTimeSameEndEvent;
    private static final int CONTACT_CODE = 12345;
    private ContactAdapter contactAdapterMyEvents = null;
    ArrayList<Contact> contactsList = new ArrayList<>();
    List<Contact> contactsLists;
    List<String> otherUserKey = null;

    SQLiteDBHelper sqLiteDBHelper;

    public MyEventsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_my_events, container, false);

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());
        otherUserKey = new ArrayList<>();
        otherUserKey.add(Prefs.getUserKey());


        fab(view);

        displayMyEvents(view);
        // Inflate the layout for this fragment
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        fabSetting.setImageResource(R.drawable.ic_add_white_24dp);
        hideFab();

    }

    private void fab(View view) {
        fraToolFloat = (FrameLayout) view.findViewById(R.id.fraToolFloat_my_events);
        fabSetting = (FloatingActionButton) view.findViewById(R.id.fabSetting_my_events);
        fabSub1 = (FloatingActionButton) view.findViewById(R.id.fabSub1_my_events);
        fabSub2 = (FloatingActionButton) view.findViewById(R.id.fabSub2_my_events);
        fabSub3 = (FloatingActionButton) view.findViewById(R.id.fabSub3_my_events);
        fabSub4 = (FloatingActionButton) view.findViewById(R.id.fabSub4_my_events);

        linFab1 = (LinearLayout) view.findViewById(R.id.linFab1_my_events);
        linFab2 = (LinearLayout) view.findViewById(R.id.linFab2_my_events);
        linFab3 = (LinearLayout) view.findViewById(R.id.linFab3_my_events);
        linFab4 = (LinearLayout) view.findViewById(R.id.linFab4_my_events);
        linFabSetting = (LinearLayout) view.findViewById(R.id.linFabSetting_my_events);

        fabSub1.setOnClickListener(this);
        fabSub2.setOnClickListener(this);
        fabSub3.setOnClickListener(this);
        fabSub4.setOnClickListener(this);
        fabSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fabStatus) {
                    if (flag) {
                        fabSetting.setImageResource(R.drawable.ic_add_white_24dp);
                        flag = false;
                        hideFab();
                    }
                } else {
                    fabSetting.setImageResource(R.drawable.ic_clear_white_24dp);
                    flag = true;
                    showFab();
                }
            }
        });
        hideFab();
    }

    private void showFab() {
        linFab1.setVisibility(View.VISIBLE);
        linFab2.setVisibility(View.VISIBLE);
        linFab3.setVisibility(View.VISIBLE);
        linFab3.setVisibility(View.VISIBLE);
        linFab4.setVisibility(View.VISIBLE);
        fraToolFloat.setBackgroundColor(Color.argb(200, 0, 0, 0));
        fabStatus = true;
    }

    private void hideFab() {
        linFab1.setVisibility(View.INVISIBLE);
        linFab2.setVisibility(View.INVISIBLE);
        linFab3.setVisibility(View.INVISIBLE);
        linFab4.setVisibility(View.INVISIBLE);
        fraToolFloat.setBackgroundColor(Color.argb(0, 255, 255, 255));
        fabStatus = false;

    }

    private void displayMyEvents(View view) {

        myEventsArrayList = new ArrayList<>();

        myEventsArrayList = sqLiteDBHelper.getMyEvents(Prefs.getUserKey());

        Log.e("tag", myEventsArrayList.toString());

        myEventsAdapter = new RecyclerAdapterMyEvents(getContext(), myEventsArrayList);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_my_event);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(myEventsAdapter);
    }

    @Override
    public void onClick(View v) {
        Bundle bundle = new Bundle();
        switch (v.getId()) {

            case R.id.fabSub1_my_events:
                Intent intent1 = new Intent(getAppContext(), CreateEventActivity.class);
                bundle.putString("value", "3");
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;
            case R.id.fabSub2_my_events:
                Intent intent2 = new Intent(getAppContext(), CreateEventActivity.class);
                bundle.putString("value", "2");
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            case R.id.fabSub3_my_events:
                Intent intent3 = new Intent(getAppContext(), CreateEventActivity.class);
                bundle.putString("value", "1");
                intent3.putExtras(bundle);
                startActivity(intent3);
                break;
            case R.id.fabSub4_my_events:
                Intent intent4 = new Intent(getAppContext(), CreateEventActivity.class);
                bundle.putString("value", "0");
                intent4.putExtras(bundle);
                startActivity(intent4);
                break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CONTACT_CODE && resultCode == RESULT_OK && data != null) {

            int result = data.getIntExtra("selectedPosition", -1);

            Log.e("result", "" + result);

            contactsLists = (ArrayList<Contact>) data.getSerializableExtra("contacts");
            for (int i = 0; i < contactsLists.size(); i++) {
                Contact contact = contactsLists.get(i);
                Toast.makeText(getActivity(), "a " + contact.getName(), Toast.LENGTH_LONG).show();
                if (!otherUserKey.contains(contact.getId())) {
                    otherUserKey.add(contact.getId());
                    contactsList.add(contact);
                } else {
                    Toast.makeText(getActivity(), "Already exits", Toast.LENGTH_LONG).show();
                }

            }

            contactAdapterMyEvents.setContactList(contactsList);

            contactAdapterMyEvents.notifyDataSetChanged();

            myEventsAdapter.notifyItemChanged(result);

        }
    }

    class RecyclerAdapterMyEvents extends RecyclerView.Adapter<RecyclerAdapterMyEvents.MyViewHolder> {

        private List<USER_EVENT> myEventsList;
        private Context context;
        private LayoutInflater inflater;
        private String str;

        RecyclerAdapterMyEvents(Context context, List<USER_EVENT> myEventsList) {

            this.context = context;
            this.myEventsList = myEventsList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = inflater.inflate(R.layout.card_view_my_events, parent, false);
            return new RecyclerAdapterMyEvents.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            USER_EVENT myEvents = myEventsList.get(holder.getAdapterPosition());

            // holder.eventMyName.setText(myEvents.getEventMyName());
            holder.eventMyType.setText(myEvents.getEventTypeKey() + " | " + myEvents.getTagKey());
            holder.eventMyName.setText(myEvents.getEventTitle());
            holder.eventMyAddPeople.setBorderColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            String dateStart = myEvents.getEventStartTs();
            String datEnd = myEvents.getEventEndTs();
            Log.e("dateStart", dateStart);
            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
            try {
                Date dateStartEvent = parseFormat.parse(dateStart);
                dateStartMyEvent = dateFormat.format(dateStartEvent);
                Log.e("startEvent", "" + dateStartEvent);
                Date dateEndEvent = parseFormat.parse(datEnd);
                dateEndMyEvent = dateFormat.format(dateEndEvent);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String monthStart = myEvents.getEventStartTs();
            String monthEnd = myEvents.getEventEndTs();

            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM");
            try {
                Date monthStartEvent = parseFormat.parse(monthStart);
                Log.e("monthDate", "" + monthStartEvent);
                monthStartMyEvent = dateFormat2.format(monthStartEvent);
                Date monthEndEvent = parseFormat.parse(monthEnd);
                monthEndMyEvent = dateFormat2.format(monthEndEvent);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (!dateStartMyEvent.equals(dateEndMyEvent)) {
                holder.eventMyDate.setText(dateStartMyEvent + " - " + dateEndMyEvent);
                holder.eventMyMonth.setText(monthEndMyEvent + "   " + monthEndMyEvent);
            } else {
                holder.eventMyDate.setText(dateStartMyEvent);
                holder.eventMyMonth.setText(monthEndMyEvent);
            }

            SimpleDateFormat dateFormatStart = new SimpleDateFormat("dd MMM hh:mm aa");
            SimpleDateFormat dateFormatStart2 = new SimpleDateFormat("hh:mm aa");
            try {
                Date dateTimeStart = parseFormat.parse(dateStart);
                dateTimeMyEvent = dateFormatStart.format(dateTimeStart);
                Date dateTimeEnd = parseFormat.parse(datEnd);
                dateTimeEndMyEvent = dateFormatStart.format(dateTimeEnd);
                dateTimeSameEndEvent = dateFormatStart2.format(dateTimeEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (dateStartMyEvent.equals(dateEndMyEvent)) {
                holder.eventMyAlarmNotify.setText(dateTimeMyEvent + " to " + dateTimeSameEndEvent);
            } else {
                holder.eventMyAlarmNotify.setText(dateTimeMyEvent + " to " + dateTimeEndMyEvent);
            }


            str = myEvents.getAddress();
            if (str.contains(",")) {
                holder.eventMyPlace.setText(str.substring(0, str.indexOf(",")));
                holder.eventMyPlace2.setText(str.substring(str.indexOf(",") + 1, str.length()));
                holder.eventMyPlace2.setVisibility(View.VISIBLE);
            } else {
                holder.eventMyPlace.setText(str);
            }

            holder.bac.setImageBitmap(Utility.getPhoto(myEvents.getImageKey()));

            holder.eventMyAddPeople.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(getAppContext(), ContactsActivity.class);
                    i.putExtra("selectedPosition", holder.getAdapterPosition());
                    startActivityForResult(i, CONTACT_CODE);
                }
            });

            holder.contactMyRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

            ArrayList<Contact> newContactArrayList = new ArrayList<>();
            //   newContactArrayList.add(sqLiteDBHelper.getUserContact(Prefs.getUserKey()));
            newContactArrayList.addAll(sqLiteDBHelper.getAllContactsOfEvents(myEvents.getEventKey()));

            contactAdapterMyEvents = new ContactAdapter(getAppContext(), R.layout.card__contact_image, newContactArrayList);
            holder.contactMyRecyclerView.setAdapter(contactAdapterMyEvents);


            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    Log.e("tag", "click" + myEventsList.get(holder.getAdapterPosition()).getEventKey());

                    EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", myEventsList.get(holder.getAdapterPosition()).getEventKey());
                    eventDetailsFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.child_fragment_container, eventDetailsFragment, "tag")
                            .addToBackStack("tag").commit();
                }
            });

        }

        @Override
        public int getItemCount() {
            return myEventsList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView eventMyName, eventMyType, eventMyDate, eventMyMonth, eventMyAlarmNotify, eventMyPlace, eventMyPlace2;
            private ImageView eventMymore, bac;
            private RecyclerView contactMyRecyclerView;
            // private PorterShapeImageView eventMyAddPeople;
            private HexagonMaskView eventMyAddPeople;
            private ItemClickListener itemClickListener;
            private RelativeLayout eventImage;

            MyViewHolder(View itemView) {
                super(itemView);
                eventMyName = (TextView) itemView.findViewById(R.id.event_my_name);
                eventMyType = (TextView) itemView.findViewById(R.id.event_my_type);
                eventMyDate = (TextView) itemView.findViewById(R.id.event_my_date);
                eventMyMonth = (TextView) itemView.findViewById(R.id.event_my_month);
                eventMyAlarmNotify = (TextView) itemView.findViewById(R.id.event_my_time_notify);
                eventMyPlace = (TextView) itemView.findViewById(R.id.event_my_place);
                eventMyPlace2 = (TextView) itemView.findViewById(R.id.event_my_place2);
                eventMymore = (ImageView) itemView.findViewById(R.id.event_my_more);
                bac = (ImageView) itemView.findViewById(R.id.bac);
                //  eventMyAddPeople = (PorterShapeImageView) itemView.findViewById(R.id.hex_one_event_my);
                eventMyAddPeople = (HexagonMaskView) itemView.findViewById(R.id.hex_one_event_my);
                contactMyRecyclerView = (RecyclerView) itemView.findViewById(R.id.my_events_recycler);
                eventImage = (RelativeLayout) itemView.findViewById(R.id.topRelativeLayout);
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
}