package com.conext.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.conext.Adapter.ContactAdapter;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.Contact;
import com.conext.model.db.USER_EVENT;
import com.conext.ui.Fragments.EventDetailsFragment;
import com.conext.ui.custom.HexagonMaskView;
import com.conext.utils.Prefs;
import com.conext.utils.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.conext.AppManager.getAppContext;

public class EventDetailsActivity extends AppCompatActivity {

    private static final int CONTACT_CODE = 9;
    private RelativeLayout bgImage, recyclerAdd;
    private ImageView moreEvent, shareEvent, eventAlarm, eventLocate, eventPeople, bac;
    private TextView eventName, eventAddressMore, eventType, eventDate, eventMonth, eventDescription, eventYes, eventInterested, eventNo, eventTimeNotify, eventAddress;
    private RecyclerView contactRecycler;
    // private PorterShapeImageView addContact, contactImage;
    private HexagonMaskView addContact, contactImage;
    private EditText addComment;
    private String dateStartEventDetails, dateEndEventDetails, monthStartEventDetails, monthEndEventDetails, dateTimeEventDetails, dateTimeEndEventDetails, dateTimeSameEndEvent;
    private SQLiteDBHelper sqLiteDBHelper;
    private String id = null;
    private LinearLayout optionLinearLayout;
    String str;
    private ArrayList<Contact> contactsLists = new ArrayList<>();
    private ArrayList<String> otherUserKey;
    private ArrayList<Contact> contactsList = new ArrayList<>();
    private ContactAdapter contactAdapter = null;
    int posi;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            id = bundle.getString("id");
            Log.e("id", id);
        }

        sqLiteDBHelper = new SQLiteDBHelper(EventDetailsActivity.this);

        setupToolbar();

        addressingView();

        addingDataFromSqlite();

        contactRecycler.addOnItemTouchListener(new ContactRecyclerItemClickListener(EventDetailsActivity.this, contactRecycler, new ContactRecyclerItemClickListener.ContactOnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (!Objects.equals(contactsList.get(position).getId(), Prefs.getUserKey())) {
                    Intent intent = new Intent(EventDetailsActivity.this, OtherProfileActivity.class);
                    Log.e("ids", id + " " + contactsList.get(position).getId());
                    intent.putExtra("id", contactsList.get(position).getId());
                    startActivity(intent);
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                posi = position;

                Context context = view.getContext();

                if (!Objects.equals(contactsList.get(position).getId(), Prefs.getUserKey())) {

                    if (sqLiteDBHelper.getEventCreator(Prefs.getUserKey(), id)) {

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setMessage("Are you sure you want to delete this contact?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        sqLiteDBHelper.removeUser(contactsList.get(posi).getId(), id);
                                        contactsList.remove(posi);
                                        // notifyItemRemoved(position);
                                        contactAdapter.notifyDataSetChanged();
                                        dialog.dismiss();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    }
                }
            }
        }));

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONTACT_CODE && resultCode == RESULT_OK) {
            contactsLists = new ArrayList<>();

            contactsLists = sqLiteDBHelper.getAllContactsOfEvents(id);

            contactsLists.addAll((ArrayList<Contact>) data.getSerializableExtra("contacts"));

            for (int i = 0; i < contactsLists.size(); i++) {
                Contact contact = contactsLists.get(i);
                if (!otherUserKey.contains(contact.getId())) {
                    otherUserKey.add(contact.getId());
                    contactsList.add(contact);
                } else {
                    Toast.makeText(EventDetailsActivity.this, "Already exits", Toast.LENGTH_LONG).show();
                }

            }

            // contactsList.addAll(contactsLists);
            contactAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Event Details");
        }
    }

    private void addingDataFromSqlite() {

        final USER_EVENT userEvent = sqLiteDBHelper.getEventDetails(id);

        eventName.setText(userEvent.getEventTitle());
        eventType.setText(userEvent.getEventTypeKey() + " | " + userEvent.getTagKey());

        String dateStart = userEvent.getEventStartTs();
        String datEnd = userEvent.getEventEndTs();
        Log.e("dateStart", dateStart);
        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd");
        try {
            Date dateStartEvent = parseFormat.parse(dateStart);
            dateStartEventDetails = dateFormat.format(dateStartEvent);
            Log.e("startEvent", "" + dateStartEvent);
            Date dateEndEvent = parseFormat.parse(datEnd);
            dateEndEventDetails = dateFormat.format(dateEndEvent);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String monthStart = userEvent.getEventStartTs();
        String monthEnd = userEvent.getEventEndTs();

        SimpleDateFormat dateFormat2 = new SimpleDateFormat("MMM");
        try {
            Date monthStartEvent = parseFormat.parse(monthStart);
            Log.e("monthDate", "" + monthStartEvent);
            monthStartEventDetails = dateFormat2.format(monthStartEvent);
            Date monthEndEvent = parseFormat.parse(monthEnd);
            monthEndEventDetails = dateFormat2.format(monthEndEvent);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        if (!dateStartEventDetails.equals(dateEndEventDetails)) {
            eventDate.setText(dateStartEventDetails + " - " + dateEndEventDetails);
            eventMonth.setText(monthStartEventDetails + "    " + monthEndEventDetails);
        } else {
            eventDate.setText(dateStartEventDetails);
            eventMonth.setText(monthStartEventDetails);
        }

        eventDescription.setText(userEvent.getDescription());

        SimpleDateFormat dateFormatStart = new SimpleDateFormat("dd MMM hh:mm aa");
        SimpleDateFormat dateFormatStart2 = new SimpleDateFormat("hh:mm aa");
        try {
            Date dateTimeStart = parseFormat.parse(dateStart);
            dateTimeEventDetails = dateFormatStart.format(dateTimeStart);
            Date dateTimeEnd = parseFormat.parse(datEnd);
            dateTimeEndEventDetails = dateFormatStart.format(dateTimeEnd);
            dateTimeSameEndEvent = dateFormatStart2.format(dateTimeEnd);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (dateStartEventDetails.equals(dateEndEventDetails)) {
            eventTimeNotify.setText(dateTimeEventDetails + " to " + dateTimeSameEndEvent);
        } else {
            eventTimeNotify.setText(dateTimeEventDetails + " to " + dateTimeEndEventDetails);
        }

        str = userEvent.getAddress();
        Log.e("address", userEvent.getAddress());
        if (str.contains(",")) {
            eventAddress.setText(str.substring(0, str.indexOf(",")));
            eventAddressMore.setText(str.substring(str.indexOf(",") + 1, str.length()));
            eventAddressMore.setVisibility(View.VISIBLE);
        } else {
            eventAddress.setText(str);
        }

        if (Objects.equals(userEvent.getCreatedBy(), Prefs.getUserKey())) {
            Log.e(userEvent.getCreatedBy(), Prefs.getUserKey());
            optionLinearLayout.setVisibility(View.GONE);
        }

        if (sqLiteDBHelper.getUserChoiceOnEventDetails(userEvent.getEventKey())) {
            Log.e(userEvent.getCreatedBy(), "" + sqLiteDBHelper.getUserChoiceOnEventDetails(userEvent.getEventKey()));
            optionLinearLayout.setVisibility(View.GONE);
        }

        if (sqLiteDBHelper.isOpen(userEvent.getEventKey())) {
            optionLinearLayout.setVisibility(View.GONE);
        }

        eventYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDBHelper.updateUpcomingEventSelected(userEvent.getEventKey(), Prefs.getUserKey(), 0);
                eventYes.setTextColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.colorPrimary));
                optionLinearLayout.setVisibility(View.GONE);
            }
        });

        eventInterested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDBHelper.updateUpcomingEventSelected(userEvent.getEventKey(), Prefs.getUserKey(), 1);
                eventInterested.setTextColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.colorPrimary));
                optionLinearLayout.setVisibility(View.GONE);
            }
        });

        eventNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sqLiteDBHelper.updateUpcomingEventSelected(userEvent.getEventKey(), Prefs.getUserKey(), 2);
                eventNo.setTextColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.colorPrimary));
                optionLinearLayout.setVisibility(View.GONE);
            }
        });

        bac.setImageBitmap(Utility.getPhoto(userEvent.getImageKey()));

        if (userEvent.isOpen())
            addContact.setVisibility(View.VISIBLE);
        else if (Prefs.getUserKey().equals(userEvent.getUserKey()))
            addContact.setVisibility(View.VISIBLE);
        else
            addContact.setVisibility(View.GONE);

        if (sqLiteDBHelper.getEventEndDate(userEvent.getEventKey()))
            addContact.setVisibility(View.VISIBLE);
        else
            addContact.setVisibility(View.GONE);

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent contactIntent = new Intent(getAppContext(), ContactsActivity.class);
                startActivityForResult(contactIntent, CONTACT_CODE);

            }
        });

        otherUserKey = new ArrayList<>();

       /* otherUserKey.add(Prefs.getUserKey());

        contactsLists.add(sqLiteDBHelper.getUserContact(Prefs.getUserKey()));*/

        contactsLists.addAll(sqLiteDBHelper.getAllContactsOfEvents(userEvent.getEventKey()));

        for (int i = 0; i < contactsLists.size(); i++) {
            Contact contact = contactsLists.get(i);

            otherUserKey.add(contact.getId());
            contactsList.add(contact);
        }

        contactAdapter = new ContactAdapter(getAppContext(), R.layout.card__contact_image, contactsList);

        contactRecycler.setLayoutManager(new LinearLayoutManager(EventDetailsActivity.this, LinearLayoutManager.HORIZONTAL, false));
        //For performance, tell OS RecyclerView won't change size
        contactRecycler.setHasFixedSize(true);
        // Assign adapter to recyclerView
        contactRecycler.setAdapter(contactAdapter);

    }

    private void addressingView() {
        bgImage = (RelativeLayout) findViewById(R.id.top);
        optionLinearLayout = (LinearLayout) findViewById(R.id.optionLinearLayout);
        moreEvent = (ImageView) findViewById(R.id.my_event_other_more);
        bac = (ImageView) findViewById(R.id.bac);
        shareEvent = (ImageView) findViewById(R.id.my_share_more);
        eventName = (TextView) findViewById(R.id.event_name);
        eventType = (TextView) findViewById(R.id.event_type);
        eventDate = (TextView) findViewById(R.id.event_date);
        eventMonth = (TextView) findViewById(R.id.event_month);
        eventDescription = (TextView) findViewById(R.id.event_other_des);
        eventYes = (TextView) findViewById(R.id.interest_event_more_yes);
        eventInterested = (TextView) findViewById(R.id.interest_more_interest);
        eventNo = (TextView) findViewById(R.id.interest_event_more_no);
        eventAlarm = (ImageView) findViewById(R.id.alrm_img_event_more);
        eventTimeNotify = (TextView) findViewById(R.id.event_more_time_notify);
        eventLocate = (ImageView) findViewById(R.id.locate_more_image);
        eventAddress = (TextView) findViewById(R.id.event_more_place);
        eventAddressMore = (TextView) findViewById(R.id.event_more_place2);
        eventPeople = (ImageView) findViewById(R.id.hex_one_event_more);
        recyclerAdd = (RelativeLayout) findViewById(R.id.recyclerAdd);
        contactRecycler = (RecyclerView) findViewById(R.id.contact_recycler);
       /* addContact = (PorterShapeImageView) findViewById(R.id.add_contact);
        contactImage = (PorterShapeImageView) findViewById(R.id.contact_image);*/
        addContact = (HexagonMaskView) findViewById(R.id.add_contact);
        contactImage = (HexagonMaskView) findViewById(R.id.contact_image);
        //  addComment = (EditText) view.findViewById(R.id.user_comment_event_more);
//        contactImage.setImageBitmap(sqLiteDBHelper.getUserImage(Prefs.getUserKey()));
        //  contactImage.setBorderColor(ContextCompat.getColor(getContext(), R.color.black));
        addContact.setBorderColor(ContextCompat.getColor(EventDetailsActivity.this, R.color.colorPrimaryDark));
    }


    public static class ContactRecyclerItemClickListener implements RecyclerView.OnItemTouchListener {

        interface ContactOnItemClickListener {
            void onItemClick(View view, int position);

            void onItemLongClick(View view, int position);
        }

        private ContactRecyclerItemClickListener.ContactOnItemClickListener mListener;
        private GestureDetector mGestureDetector;

        public ContactRecyclerItemClickListener(Context context, final RecyclerView recyclerView, ContactRecyclerItemClickListener.ContactOnItemClickListener listener) {
            mListener = listener;

            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View childView = recyclerView.findChildViewUnder(e.getX(), e.getY());

                    if (childView != null && mListener != null) {
                        mListener.onItemLongClick(childView, recyclerView.getChildAdapterPosition(childView));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());

            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
            }

            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }


}

