package com.conext.ui.Fragments;

import android.app.Activity;
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

import com.conext.Adapter.ContactAdapter;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.Contact;
import com.conext.model.Skill;
import com.conext.model.UpcomingEvents;
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

import static com.conext.AppManager.getAppContext;

//import com.conext.utils.DataReceivedListener;

public class UpcomingEventFragment extends Fragment implements /*DataReceivedListener,*/ View.OnClickListener {

    private static final int CONTACT_CODE = 123;
    UpcomingEvents upcomingEvents;
    ArrayList<UpcomingEvents> upcomingEventsArrayList;
    RecyclerAdapterUpcomingEvents upcomingEventsAdapter = null;
    ArrayList<USER_EVENT> myEventsArrayList;

    private ContactAdapter contactAdapter = null;
    ArrayList<Contact> contactsList = new ArrayList<>();

    private String dateStartUpcomeEvent, dateEndUpcomeEvent, monthStartUpcomeEvent, monthEndUpcomeEvent;
    private String dateTimeUpcomeEvent, dateTimeEndUpcomeEvent, dateTimeSameEndEvent;
    RecyclerView recyclerView;
    boolean flag = true;
    ArrayList<Skill> skillArrayList = null;
    ArrayList<Long> eventKeyArrayList = null;
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


    SQLiteDBHelper sqLiteDBHelper;

    public UpcomingEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_upcoming_event, container, false);

        sqLiteDBHelper = new SQLiteDBHelper(getActivity());

        fab(view);
        displayUpcomingEvents(view);
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

        fraToolFloat = (FrameLayout) view.findViewById(R.id.fraToolFloat_upcome_event);
        fabSetting = (FloatingActionButton) view.findViewById(R.id.fabSetting_upcome_events);
        fabSub1 = (FloatingActionButton) view.findViewById(R.id.fabSub1_upcome_event);
        fabSub2 = (FloatingActionButton) view.findViewById(R.id.fabSub2_upcome_events);
        fabSub3 = (FloatingActionButton) view.findViewById(R.id.fabSub3_upcome_events);
        fabSub4 = (FloatingActionButton) view.findViewById(R.id.fabSub4_upcome_events);

        linFab1 = (LinearLayout) view.findViewById(R.id.linFab1_upcome_events);
        linFab2 = (LinearLayout) view.findViewById(R.id.linFab2_upcome_events);
        linFab3 = (LinearLayout) view.findViewById(R.id.linFab3_upcome_events);
        linFab4 = (LinearLayout) view.findViewById(R.id.linFab4_upcome_events);
        linFabSetting = (LinearLayout) view.findViewById(R.id.linFabSetting_upcome_events);

        fabSub1.setOnClickListener(this);
        fabSub2.setOnClickListener(this);
        fabSub3.setOnClickListener(this);
        fabSub4.setOnClickListener(this);

        /* when fab Setting (fab main) clicked */
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

    private void hideFab() {
        linFab1.setVisibility(View.INVISIBLE);
        linFab2.setVisibility(View.INVISIBLE);
        linFab3.setVisibility(View.INVISIBLE);
        linFab4.setVisibility(View.INVISIBLE);
        fraToolFloat.setBackgroundColor(Color.argb(0, 255, 255, 255));
        fabStatus = false;
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


    private void displayUpcomingEvents(View view) {
        myEventsArrayList = new ArrayList<>();
        eventKeyArrayList = new ArrayList<>();

        skillArrayList = sqLiteDBHelper.getUserTags(Prefs.getUserKey());

        eventKeyArrayList = sqLiteDBHelper.getMyUpComingEventsKey(skillArrayList, Prefs.getUserKey());

        myEventsArrayList = sqLiteDBHelper.getMyUpCommingEvents(Prefs.getUserKey(), eventKeyArrayList);

        Log.e("tag", myEventsArrayList.toString());

        upcomingEventsAdapter = new RecyclerAdapterUpcomingEvents(getContext(), myEventsArrayList);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_upcoming_event);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(upcomingEventsAdapter);

    }

   /* @Override
    public void onReceived(int requestCode, int resultCode, Intent data) {

        if (requestCode == CONTACT_CODE && resultCode == RESULT_OK && data != null) {

            int result = data.getIntExtra("selectedPosition", -1);
            List<Contact> contactsLists = (ArrayList<Contact>) data.getSerializableExtra("contacts");
            contactsList.clear();
            contactsList.addAll(contactsLists);
            Log.e("tag", "res==> " + result);

            contactAdapter.setContactList(contactsLists);
            upcomingEventsAdapter.notifyItemChanged(result);

            upcomingEventsAdapter = new RecyclerAdapterUpcomingEvents(getContext(), upcomingEventsArrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(upcomingEventsAdapter);
        }
    }*/

    @Override
    public void onClick(View v) {

        Bundle bundle = new Bundle();
        switch (v.getId()) {

            case R.id.fabSub1_upcome_event:
                Intent intent1 = new Intent(getAppContext(), CreateEventActivity.class);
                bundle.putString("value", "3");
                intent1.putExtras(bundle);
                startActivity(intent1);
                break;
            case R.id.fabSub2_upcome_events:
                Intent intent2 = new Intent(getAppContext(), CreateEventActivity.class);
                bundle.putString("value", "2");
                intent2.putExtras(bundle);
                startActivity(intent2);
                break;
            case R.id.fabSub3_upcome_events:
                Intent intent3 = new Intent(getAppContext(), CreateEventActivity.class);
                bundle.putString("value", "1");
                intent3.putExtras(bundle);
                startActivity(intent3);
                break;
            case R.id.fabSub4_upcome_events:
                Intent intent4 = new Intent(getAppContext(), CreateEventActivity.class);
                bundle.putString("value", "0");
                intent4.putExtras(bundle);
                startActivity(intent4);
                break;
        }

    }

    class RecyclerAdapterUpcomingEvents extends RecyclerView.Adapter<RecyclerAdapterUpcomingEvents.MyViewHolder> {

        private List<USER_EVENT> upcomingEventsList;
        private Context context;
        private LayoutInflater inflater;
        private String str;
        int posi;


        RecyclerAdapterUpcomingEvents(Context context, List<USER_EVENT> upcomingEventsList) {
            this.context = context;
            this.upcomingEventsList = upcomingEventsList;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View rootView = inflater.inflate(R.layout.card_view_upcoming_event, parent, false);
            return new RecyclerAdapterUpcomingEvents.MyViewHolder(rootView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {
            posi = position;

            final USER_EVENT upcomingEvents = upcomingEventsList.get(position);

            holder.eventUpcomingName.setText(upcomingEvents.getEventTitle());
            holder.eventUpcomingType.setText(upcomingEvents.getEventTypeKey() + " | " + upcomingEvents.getTagKey());
            holder.eventUpcomingAddPeople.setBorderColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
            holder.bac.setImageBitmap(Utility.getPhoto(upcomingEvents.getImageKey()));

            String dateStart = upcomingEvents.getEventStartTs();
            String datEnd = upcomingEvents.getEventEndTs();
            Log.e("dateStart", dateStart);

            SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
            try {
                Date dateStartEvent = parseFormat.parse(dateStart);
                dateStartUpcomeEvent = dateFormat.format(dateStartEvent);
                Log.e("startEvent", "" + dateStartEvent);
                Date dateEndEvent = parseFormat.parse(datEnd);
                dateEndUpcomeEvent = dateFormat.format(dateEndEvent);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            String monthStart = upcomingEvents.getEventStartTs();
            String monthEnd = upcomingEvents.getEventEndTs();

            SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM");
            try {
                Date monthStartEvent = parseFormat.parse(monthStart);
                Log.e("monthDate", "" + monthStartEvent);
                monthStartUpcomeEvent = dateFormat2.format(monthStartEvent);
                Date monthEndEvent = parseFormat.parse(monthEnd);
                monthEndUpcomeEvent = dateFormat2.format(monthEndEvent);
            } catch (ParseException e) {
                e.printStackTrace();
            }


            if (!dateStartUpcomeEvent.equals(dateEndUpcomeEvent)) {
                holder.eventUpcomingDate.setText(dateStartUpcomeEvent + " - " + dateEndUpcomeEvent);
                holder.eventUpcomingMonth.setText(monthStartUpcomeEvent + "   " + monthEndUpcomeEvent);
            } else {
                holder.eventUpcomingDate.setText(dateStartUpcomeEvent);
                holder.eventUpcomingMonth.setText(monthStartUpcomeEvent);
            }

            SimpleDateFormat dateFormatStart = new SimpleDateFormat("dd MMM hh:mm aa");
            SimpleDateFormat dateFormatStart2 = new SimpleDateFormat("hh:mm aa");
            try {
                Date dateTimeStart = parseFormat.parse(dateStart);
                dateTimeUpcomeEvent = dateFormatStart.format(dateTimeStart);
                Date dateTimeEnd = parseFormat.parse(datEnd);
                dateTimeEndUpcomeEvent = dateFormatStart.format(dateTimeEnd);
                dateTimeSameEndEvent = dateFormatStart2.format(dateTimeEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (dateStartUpcomeEvent.equals(dateEndUpcomeEvent)) {
                holder.eventUpcomingAlarmNotify.setText(dateTimeUpcomeEvent + " to " + dateTimeSameEndEvent);
            } else {
                holder.eventUpcomingAlarmNotify.setText(dateTimeUpcomeEvent + " to " + dateTimeEndUpcomeEvent);
            }


            str = upcomingEvents.getAddress();
            if (str.contains(",")) {
                holder.eventUpcomingPlace.setText(str.substring(0, str.indexOf(",")));
                holder.eventUpcomingPlace2.setText(str.substring(str.indexOf(",") + 1, str.length()));
                holder.eventUpcomingPlace2.setVisibility(View.VISIBLE);
            } else {
                holder.eventUpcomingPlace.setText(str);
            }
/*

            holder.eventUpcomingAddPeople.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Activity origin = (Activity) context;
                    Intent i = new Intent(getAppContext(), ContactsActivity.class);
                    i.putExtra("selectedPosition", holder.getAdapterPosition());
                    Log.e("tag", "position==> " + holder.getAdapterPosition());
                    origin.startActivityForResult(i, CONTACT_CODE);
                }
            });
*/

            if (contactsList != null && contactsList.size() > 0)
                Log.e("tag", contactsList.get(position).getName());

            contactsList = sqLiteDBHelper.getAllContactsOfEvents(upcomingEvents.getEventKey());

            holder.contactRecyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            contactAdapter = new ContactAdapter(getAppContext(), R.layout.card__contact_image, contactsList);
            holder.contactRecyclerView.setAdapter(contactAdapter);


            holder.setItemClickListener(new ItemClickListener() {
                @Override
                public void onItemClick(View v, int pos) {
                    Log.e("tag", "click" + upcomingEventsList.get(position).getEventKey());

                    EventDetailsFragment eventDetailsFragment = new EventDetailsFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("id", upcomingEventsList.get(position).getEventKey());
                    eventDetailsFragment.setArguments(bundle);
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.child_fragment_container, eventDetailsFragment, "tag")
                            .addToBackStack("tag").commit();

                }
            });

            holder.eventUpcomingYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sqLiteDBHelper.updateUpcomingEventSelected(upcomingEvents.getEventKey(), Prefs.getUserKey(), 0);
                    holder.intrest.setVisibility(View.GONE);
                    upcomingEventsList.remove(position);
                    notifyDataSetChanged();

                }
            });

            holder.eventUpcomingInterested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sqLiteDBHelper.updateUpcomingEventSelected(upcomingEvents.getEventKey(), Prefs.getUserKey(), 1);
                    holder.intrest.setVisibility(View.GONE);
                    upcomingEventsList.remove(position);
                    notifyDataSetChanged();
                }
            });

            holder.eventUpcomingNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    sqLiteDBHelper.updateUpcomingEventSelected(upcomingEvents.getEventKey(), Prefs.getUserKey(), 2);
                    holder.intrest.setVisibility(View.GONE);
                    upcomingEventsList.remove(position);
                    notifyDataSetChanged();
                }
            });


        }


        @Override
        public int getItemCount() {
            return upcomingEventsList.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            private TextView eventUpcomingName, eventUpcomingType, eventUpcomingDate, eventUpcomingMonth, eventUpcomingAlarmNotify, eventUpcomingPlace, eventUpcomingPlace2, eventUpcomingYes, eventUpcomingInterested, eventUpcomingNo;
            private ImageView eventUpcomingmore, bac;
            private RecyclerView contactRecyclerView;
            private RelativeLayout topRelativeLayout;
            private LinearLayout intrest;
            // private PorterShapeImageView eventUpcomingAddPeople;
            private HexagonMaskView eventUpcomingAddPeople;
            private ItemClickListener itemClickListener;

            MyViewHolder(View itemView) {
                super(itemView);
                eventUpcomingName = (TextView) itemView.findViewById(R.id.event_name);
                eventUpcomingType = (TextView) itemView.findViewById(R.id.event_type);
                eventUpcomingDate = (TextView) itemView.findViewById(R.id.event_date);
                eventUpcomingMonth = (TextView) itemView.findViewById(R.id.event_month);

                eventUpcomingAlarmNotify = (TextView) itemView.findViewById(R.id.event_time_notify);
                eventUpcomingPlace = (TextView) itemView.findViewById(R.id.event_place);
                eventUpcomingPlace2 = (TextView) itemView.findViewById(R.id.event_place2);

                eventUpcomingYes = (TextView) itemView.findViewById(R.id.event_yes);
                eventUpcomingInterested = (TextView) itemView.findViewById(R.id.event_interested);
                eventUpcomingNo = (TextView) itemView.findViewById(R.id.event_no);

                eventUpcomingmore = (ImageView) itemView.findViewById(R.id.event_more);
                bac = (ImageView) itemView.findViewById(R.id.bac);
                topRelativeLayout = (RelativeLayout) itemView.findViewById(R.id.topRelativeLayout);
                intrest = (LinearLayout) itemView.findViewById(R.id.intrest);
                // eventUpcomingAddPeople = (PorterShapeImageView) itemView.findViewById(R.id.hex_one_event);
                eventUpcomingAddPeople = (HexagonMaskView) itemView.findViewById(R.id.hex_one_event);

                contactRecyclerView = (RecyclerView) itemView.findViewById(R.id.upcome_events_recycler);
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
