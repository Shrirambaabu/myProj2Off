package com.conext.ui;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.conext.MainActivity;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.Contact;
import com.conext.model.Skill;
import com.conext.model.db.EVENT_TYPE_MASTER;
import com.conext.model.db.USER_EVENT;
import com.conext.ui.Fragments.DatePickerFragment;
import com.conext.ui.Fragments.TimePickerFragment;
import com.conext.ui.custom.HexagonMaskView;
import com.conext.utils.Prefs;
import com.conext.utils.Utility;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.conext.AppManager.getAppContext;
import static com.conext.utils.Utility.getDateTime;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener, CompoundButton.OnCheckedChangeListener {

    private static final int CONTACT_CODE = 1;
    private static final int GALLERY_KITKAT_INTENT_CALLED = 11;
    private static final int PLACE_PICKER_REQUEST = 111;

    private SQLiteDBHelper sqLiteDBHelper;

    private Uri uriPath;
    private TextView cancelText, saveText, startSessionText, endSessionText;
    private ImageView cameraImage, timePickStart, timePickEnd;
    private AppCompatEditText calledEdit, descriptionEdit, dateStartEdit, dateEndEdit, startHourEdit,
            startMinEdit, endHourEdit, end_min_edit, locateEdit;
    private RelativeLayout relativeLayoutBackground;
    private AppCompatSpinner eventName, eventLanguage;
    private AppCompatCheckBox checkDay, mentee, mentor, participant;
    private RadioButton openEvent, closeEvent;
    private RadioGroup openClose;
    private boolean checkBoxDayBoolean = false;
    private boolean checkBoxOpenBoolean = false;
    private boolean checkBoxCloseBoolean = false;
    private boolean checkBoxMenteeBoolean = false;
    private boolean checkBoxMentorBoolean = false;
    private boolean checkBoxParticipantBoolean = false;

    private RecyclerView contactsRecyclerView;
    // private PorterShapeImageView imageViewHexBtn;
    private HexagonMaskView imageViewHexBtn;
    private String eventKey;
    private String tagKey;
    private String eventTypeEvent;
    private String descriptionText;
    private String locationData;
    private String timeHourStart;
    private String timeHourEnd;
    private String timeStart;
    private String locationKey;
    private String dbStart;
    private String formatedDbEnd;
    private String formatedDb;
    private String dbEnd;
    private String dateFinalStart = null;
    private String dateFinalEnd = null;

    private Date d1;
    private GoogleApiClient mGoogleApiClient;
    private ContactAdapter contactAdapter = null;
    private Calendar datetime2;
    int defaultValue = 0;
    String dateCurFinalStart;
    private ArrayList<Contact> contactsList = new ArrayList<>();
    List<String> eventSkill = null, eventType = null, otherUserKey = null;
    List<Contact> contactsLists;
    long eventId = 0;

    public CreateEventActivity() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_new_event);

        Bundle bundle = getIntent().getExtras();

        if (bundle != null)
            defaultValue = Integer.parseInt(bundle.getString("value"));

        settingToolbar();

        settingGoogleClient();

        sqLiteDBHelper = new SQLiteDBHelper(CreateEventActivity.this);

        //mappingView
        mappingView();
        //attachingListener
        listener();
        //eventNameSpinner
        eventNameSpinner();
        //eventLanguageSpinner
        eventLanguageSpinner();
        //settingContactRecyclerView
        settingContactRecyclerView();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_KITKAT_INTENT_CALLED && resultCode == RESULT_OK && data != null && data.getData() != null) {

            uriPath = data.getData();

            getContentResolver().takePersistableUriPermission(uriPath, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriPath);
                bitmap = Bitmap.createScaledBitmap(bitmap, 1080, 512, false);

                relativeLayoutBackground.setBackground(new BitmapDrawable(getResources(), bitmap));

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (requestCode == PLACE_PICKER_REQUEST && resultCode == RESULT_OK && data != null) {
            Place place = PlacePicker.getPlace(this, data);
            locateEdit.setText(place.getAddress());
            String locateLatitude = String.valueOf(place.getLatLng().latitude);
            String locateLongitude = String.valueOf(place.getLatLng().longitude);
            locationKey = sqLiteDBHelper.setEventLocation(Prefs.getUserKey(), locateLatitude, locateLongitude);

        } else if (requestCode == CONTACT_CODE && resultCode == RESULT_OK) {
            contactsLists = (ArrayList<Contact>) data.getSerializableExtra("contacts");
            for (int i = 0; i < contactsLists.size(); i++) {
                Contact contact = contactsLists.get(i);

                if (!otherUserKey.contains(contact.getId())) {
                    otherUserKey.add(contact.getId());
                    contactsList.add(contact);
                } else {
                    Toast.makeText(CreateEventActivity.this, contact.getName() + " Already exits", Toast.LENGTH_LONG).show();
                }

            }

            // contactAdapter.setContactList(contactsList);

            // contactsList.addAll(contactsLists);
            contactAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void settingContactRecyclerView() {

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getAppContext(), LinearLayoutManager.HORIZONTAL, false);
        contactsRecyclerView.setLayoutManager(mLayoutManager);

        otherUserKey = new ArrayList<>();
        otherUserKey.add(Prefs.getUserKey());

        contactsList.add(sqLiteDBHelper.getUserContact(Prefs.getUserKey()));

        //create an ArrayAdapter from the String Array
        contactAdapter = new ContactAdapter(getAppContext(), R.layout.card__contact_image, contactsList);
        //For performance, tell OS RecyclerView won't change size
        contactsRecyclerView.setHasFixedSize(true);
        // Assign adapter to recyclerView
        contactsRecyclerView.setAdapter(contactAdapter);

    }

    private void updateContactList(List<Contact> contactList) {
        contactsList = (ArrayList<Contact>) contactList;
    }

    DatePickerDialog.OnDateSetListener onEndDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            dbEnd = myFormat(year, monthOfYear, dayOfMonth);
            dateFinalEnd = formatDate(year, monthOfYear, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("EEE,MMM d yyyy");
            try {
                if (dateFinalStart != null) {
                    if (dateFinalStart == null && dateFinalStart.isEmpty()) {
                        Toast.makeText(getAppContext(), "Enter Start Date First", Toast.LENGTH_LONG).show();
                        dateEndEdit.setEnabled(false);
                    } else {
                        dateEndEdit.setEnabled(true);
                        Date d2 = sdf.parse(dateFinalEnd);
                        if (d2.compareTo(d1) < 0) {
                            Toast.makeText(getAppContext(), "Enter valid date", Toast.LENGTH_LONG).show();
                            dateEndEdit.setText("");
                        } else {
                            dateEndEdit.setText(formatDate(year, monthOfYear, dayOfMonth));
                        }
                    }
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    };

    DatePickerDialog.OnDateSetListener onStartDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {

            //  dateStartEdit.setText(formatDate(year, monthOfYear, dayOfMonth));
            dbStart = myFormat(year, monthOfYear, dayOfMonth);
            dateFinalStart = formatDate(year, monthOfYear, dayOfMonth);
            // todayDate = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("EEE,MMM d yyyy");
            try {
                String d3 = sdf.format(new Date());
                Date d4 = sdf.parse(d3);
                d1 = sdf.parse(dateFinalStart);
                if (d1.compareTo(d4) < 0) {
                    Toast.makeText(getAppContext(), "Enter valid date", Toast.LENGTH_LONG).show();
                    dateStartEdit.setText("");
                } else {
                    dateStartEdit.setText(formatDate(year, monthOfYear, dayOfMonth));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (checkDay.isChecked()) {
                if (dateFinalStart == null) {
                    dateEndEdit.setText("");
                    startHourEdit.setText("");
                    startMinEdit.setText("");
                    startSessionText.setText("AM");
                    checkDay.setChecked(false);
                    endHourEdit.setText("");
                    end_min_edit.setText("");
                    endSessionText.setText("AM");
                    timePickStart.setEnabled(true);
                    timePickEnd.setEnabled(true);
                    dateEndEdit.setEnabled(true);

                } else {
                    dateEndEdit.setText(dateFinalStart);
                    startHourEdit.setText("12");
                    startMinEdit.setText("00");
                    startSessionText.setText("AM");
                    timePickStart.setEnabled(false);
                    timePickEnd.setEnabled(false);
                    dateEndEdit.setEnabled(false);
                    endHourEdit.setText("11");
                    end_min_edit.setText("59");
                    endSessionText.setText("PM");
                }
            }
            Log.e("tag", dbStart);
        }
    };


    private String myFormat(int year, int monthOfYear, int dayOfMonth) {

        SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DATE, dayOfMonth);
        Date date = calendar.getTime();
        //Date date = new Date(newYear, monthOfYear, dayOfMonth);
        return parseFormat.format(date);
    }

    private Bundle dateDialog() {

        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("year", calender.get(Calendar.YEAR));
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("day", calender.get(Calendar.DAY_OF_MONTH));
        return args;
    }


    private String formatDate(int year, int monthOfYear, int dayOfMonth) {

        SimpleDateFormat parseFormat = new SimpleDateFormat("EEE,MMM d yyyy");

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, monthOfYear);
        calendar.set(Calendar.DATE, dayOfMonth);
        Date date = calendar.getTime();
        //Date date = new Date(newYear, monthOfYear, dayOfMonth);
        return parseFormat.format(date);
    }

    private void listener() {

        cameraImage.setOnClickListener(this);
        dateEndEdit.setOnClickListener(this);
        dateStartEdit.setOnClickListener(this);
        cancelText.setOnClickListener(this);
        saveText.setOnClickListener(this);
        timePickStart.setOnClickListener(this);
        checkDay.setOnCheckedChangeListener(this);/*
        openEvent.setOnCheckedChangeListener(this);
        closeEvent.setOnCheckedChangeListener(this);*/
        mentor.setOnCheckedChangeListener(this);
        participant.setOnCheckedChangeListener(this);
        mentee.setOnCheckedChangeListener(this);
        timePickEnd.setOnClickListener(this);
//        limitEvent.setOnClickListener(this);
        locateEdit.setOnClickListener(this);
        calledEdit.setOnClickListener(this);

        imageViewHexBtn.setOnClickListener(this);

    }

    private void mappingView() {

        relativeLayoutBackground = (RelativeLayout) findViewById(R.id.head_event);
        //imageViewHexBtn = (PorterShapeImageView) findViewById(R.id.add_contact);
        imageViewHexBtn = (HexagonMaskView) findViewById(R.id.add_contact);
        imageViewHexBtn.setBorderColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
        contactsRecyclerView = (RecyclerView) findViewById(R.id.contact_recycler);
        cancelText = (TextView) findViewById(R.id.cancel);
        saveText = (TextView) findViewById(R.id.save);
        cameraImage = (ImageView) findViewById(R.id.take_pic);
        startSessionText = (TextView) findViewById(R.id.start_session);
        endSessionText = (TextView) findViewById(R.id.end_session);

        calledEdit = (AppCompatEditText) findViewById(R.id.what_called);
        descriptionEdit = (AppCompatEditText) findViewById(R.id.descripText);
        dateStartEdit = (AppCompatEditText) findViewById(R.id.date_picked_start);
        dateEndEdit = (AppCompatEditText) findViewById(R.id.date_picked_end);
        startHourEdit = (AppCompatEditText) findViewById(R.id.time_start_hours);
        startMinEdit = (AppCompatEditText) findViewById(R.id.time_start_min);
        endHourEdit = (AppCompatEditText) findViewById(R.id.time_end_hours);
        end_min_edit = (AppCompatEditText) findViewById(R.id.time_end_min);
        timePickStart = (ImageView) findViewById(R.id.time_pick_start);
        timePickEnd = (ImageView) findViewById(R.id.time_pick_end);
        cameraImage = (ImageView) findViewById(R.id.take_pic);
        eventName = (AppCompatSpinner) findViewById(R.id.what_event);
        eventLanguage = (AppCompatSpinner) findViewById(R.id.tag_event);
        checkDay = (AppCompatCheckBox) findViewById(R.id.day_check);
        // limitEvent = (AppCompatCheckBox) findViewById(R.id.limit_event);
        openClose = (RadioGroup) findViewById(R.id.open_close);
        openEvent = (RadioButton) findViewById(R.id.open);
        closeEvent = (RadioButton) findViewById(R.id.close);
        locateEdit = (AppCompatEditText) findViewById(R.id.locate_edit);
        mentee = (AppCompatCheckBox) findViewById(R.id.mentee);
        mentor = (AppCompatCheckBox) findViewById(R.id.mentor);
        participant = (AppCompatCheckBox) findViewById(R.id.participant);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.date_picked_start:

                DatePickerFragment dateStart = new DatePickerFragment();
                // Set Up Current Date Into dialog
                dateStart.setArguments(dateDialog());
                //Set Call back to capture selected date
                dateStart.setCallBack(onStartDateSetListener);
                dateStart.show(getSupportFragmentManager(), "Date Picker");

                break;
            case R.id.date_picked_end:

                DatePickerFragment dateEnd = new DatePickerFragment();
                //Set Up Current Date Into dialog
                dateEnd.setArguments(dateDialog());
                //Set Call back to capture selected date
                dateEnd.setCallBack(onEndDateSetListener);
                dateEnd.show(getSupportFragmentManager(), "Date Picker");

                break;
            case R.id.time_pick_start:

                TimePickerFragment timeStart = new TimePickerFragment();
                timeStart.setArguments(timePickerDialog());
                timeStart.setCallBack(ontime);
                timeStart.show(getSupportFragmentManager(), "Time Picker");

                break;
            case R.id.time_pick_end:

                TimePickerFragment timeEnd = new TimePickerFragment();
                timeEnd.setArguments(timePickerDialog());
                timeEnd.setCallBack(onTime);
                timeEnd.show(getSupportFragmentManager(), "Time Picker");

                break;
            case R.id.take_pic:

                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, GALLERY_KITKAT_INTENT_CALLED);

                break;
           /* case R.id.limit_event:

                if (limitEvent.isChecked()) {
                    checkBoxEventBoolean = limitEvent.isChecked();
                }
                break;
*/
            case R.id.locate_edit:

                if (mGoogleApiClient == null || !mGoogleApiClient.isConnected())
                    return;

                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

                try {
                    startActivityForResult(builder.build(CreateEventActivity.this), PLACE_PICKER_REQUEST);
                } catch (GooglePlayServicesRepairableException e) {
                    Log.d("PlacesAPI Demo", "GooglePlayServicesRepairableException thrown");
                } catch (GooglePlayServicesNotAvailableException e) {
                    Log.d("PlacesAPI Demo", "GooglePlayServicesNotAvailableException thrown");
                }

                break;
            case R.id.add_contact:

                Intent contactIntent = new Intent(getAppContext(), ContactsActivity.class);
                startActivityForResult(contactIntent, CONTACT_CODE);

                break;
            case R.id.save:
                eventTypeEvent = calledEdit.getText().toString();
                descriptionText = descriptionEdit.getText().toString();
                locationData = locateEdit.getText().toString();
                dateFinalEnd = dateEndEdit.getText().toString();
                timeHourStart = startHourEdit.getText().toString();
                timeHourEnd = endHourEdit.getText().toString();
                if (uriPath == null) {
                    Toast.makeText(getAppContext(), "Select the Image", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (eventTypeEvent == null || eventTypeEvent.isEmpty()) {
                    Toast.makeText(getAppContext(), "Enter the Title", Toast.LENGTH_SHORT).show();
                    return;
                } else if (descriptionText == null || descriptionText.isEmpty()) {
                    Toast.makeText(getAppContext(), "Enter the description", Toast.LENGTH_SHORT).show();
                    return;
                } else if (dateFinalStart == null) {
                    Toast.makeText(getAppContext(), "Select the Start Date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (timeHourStart == null || timeHourStart.isEmpty()) {
                    Toast.makeText(getAppContext(), "Select the Start Time", Toast.LENGTH_SHORT).show();
                    return;
                } else if (dateFinalEnd == null || dateFinalEnd.isEmpty()) {
                    Toast.makeText(getAppContext(), "Select the End Date", Toast.LENGTH_SHORT).show();
                    return;
                } else if (timeHourEnd == null || timeHourEnd.isEmpty()) {
                    Toast.makeText(getAppContext(), "Select the End time", Toast.LENGTH_SHORT).show();
                    return;
                } else if (locationData == null || locationData.isEmpty()) {
                    Toast.makeText(getAppContext(), "Select the Location", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!checkBoxMenteeBoolean && !checkBoxMentorBoolean && !checkBoxParticipantBoolean) {
                    Toast.makeText(getAppContext(), "Select Mentee/Mentor/Participant", Toast.LENGTH_SHORT).show();
                    return;
                } else if (checkBoxMenteeBoolean && checkBoxMentorBoolean && checkBoxParticipantBoolean) {
                    Toast.makeText(getAppContext(), "Select any one from Mentee/Mentor/Participant", Toast.LENGTH_SHORT).show();
                    return;
                } else if (!checkBoxMenteeBoolean && checkBoxMentorBoolean && checkBoxParticipantBoolean) {
                    Toast.makeText(getAppContext(), "Select any one Mentee/Mentor/Participant", Toast.LENGTH_SHORT).show();
                    return;
                } else if (checkBoxMenteeBoolean && !checkBoxMentorBoolean && checkBoxParticipantBoolean) {
                    Toast.makeText(getAppContext(), "Select any one Mentee/Mentor/Participant", Toast.LENGTH_SHORT).show();
                    return;
                } else if (checkBoxMenteeBoolean && checkBoxMentorBoolean && !checkBoxParticipantBoolean) {
                    Toast.makeText(getAppContext(), "Select any one Mentee/Mentor/Participant", Toast.LENGTH_SHORT).show();
                    return;
                } else if (otherUserKey.size() <= 1) {
                    Toast.makeText(getAppContext(), "Add People to the event", Toast.LENGTH_SHORT).show();
                    return;
                }
                saveData();
                onBackPressed();
                break;
            case R.id.cancel:
                onBackPressed();
                break;
        }

    }

    private void sendNotification() {
        if (!contactsLists.isEmpty() && eventId != 0) {
            for (int i = 0; i < contactsLists.size(); i++) {
                Contact contact = contactsLists.get(i);
                if (!Objects.equals(Prefs.getUserKey(), contact.getId()))
                    sqLiteDBHelper.updateEventParticipantsStatus(eventId, contact.getId(), contact.getStatus());
            }
        }
    }

    private void saveData() {

        /*eventTypeEvent = calledEdit.getText().toString();
        descriptionText = descriptionEdit.getText().toString();*/
        // timeHourStart = startHourEdit.getText().toString();
        String timeMinStart = startMinEdit.getText().toString();
        String timeSessionStart = startSessionText.getText().toString();
        // timeHourEnd = endHourEdit.getText().toString();
        String timeMinEnd = end_min_edit.getText().toString();
        String timeSessionEnd = endSessionText.getText().toString();
       /* locationData = locateEdit.getText().toString();*/

        byte[] inputImageData = null;
        if (uriPath != null) {

            InputStream iStream = null;
            try {
                iStream = getContentResolver().openInputStream(uriPath);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            try {
                inputImageData = Utility.getBytes(iStream);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (dbEnd == null) {
            dbEnd = dbStart;
        }

        boolean checkOpenCloseBoolean;
        if (openClose.getCheckedRadioButtonId() == R.id.open) {
            checkOpenCloseBoolean = true;
        } else {
            checkOpenCloseBoolean = false;
        }

        int checkMenteeMentorParticipant;
        if (checkBoxMenteeBoolean && !checkBoxMentorBoolean && !checkBoxParticipantBoolean) {
            checkMenteeMentorParticipant = 1;
        } else if (!checkBoxMenteeBoolean && checkBoxMentorBoolean && !checkBoxParticipantBoolean) {
            checkMenteeMentorParticipant = 2;
        } else {
            checkMenteeMentorParticipant = 3;
        }
        Log.e("ValueOfBox: ", "" + checkOpenCloseBoolean);
        if (!eventTypeEvent.isEmpty() && !descriptionText.isEmpty() && !timeHourStart.isEmpty() && !timeHourStart.isEmpty() && !timeSessionStart.isEmpty()
                && !timeHourEnd.isEmpty() && !timeMinEnd.isEmpty() && !timeSessionEnd.isEmpty() && !locationData.isEmpty() && inputImageData != null) {

            USER_EVENT user_event = new USER_EVENT();

            String userKey = null;
            if (Prefs.getUserKey().isEmpty() || Prefs.getUserKey() != null) {
                userKey = Prefs.getUserKey();
            }
            user_event.setUserKey(userKey);
            user_event.setEventTitle(eventTypeEvent);
            user_event.setEventTypeKey("" + eventKey);
            user_event.setTagKey("" + tagKey);
            user_event.setLocationKey(String.valueOf(locationKey));
            user_event.setDescription(descriptionText);

            String dbSaveTime = (dbStart + " " + timeHourStart + ":" + timeMinStart + " " + timeSessionStart);
            SimpleDateFormat readFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
            SimpleDateFormat writeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                Date dateDb = readFormat.parse(dbSaveTime);
                formatedDb = writeFormat.format(dateDb);
                Log.e("DBEventStartTime", formatedDb);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            user_event.setEventStartTs(formatedDb);

            String dbSaveEndTime = (dbEnd + " " + timeHourEnd + ":" + timeMinEnd + " " + timeSessionEnd);
            try {
                Date dateDb = readFormat.parse(dbSaveEndTime);
                formatedDbEnd = writeFormat.format(dateDb);
                Log.e("DBEventEndTime", formatedDbEnd);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            user_event.setEventEndTs(formatedDbEnd);

            user_event.setAddress(locationData);
            //   user_event.setIsOpen("" + checkBoxEventBoolean);
            user_event.setOpen(checkOpenCloseBoolean);
            user_event.setImageKey(inputImageData);
            user_event.setDeleteFlag("");
            user_event.setCreatedBy(Prefs.getUserKey());
            user_event.setCreatedTs(getDateTime());
            user_event.setModifiedBy(Prefs.getUserKey());
            user_event.setModifiedTs(getDateTime());

            eventId = sqLiteDBHelper.createEvent(user_event, userKey);

            Log.e("tag", "Event Type " + eventId);

            sqLiteDBHelper.updateEventParticipants(eventId, userKey, otherUserKey, checkMenteeMentorParticipant);

            sendNotification();


            Log.e("tag", "Event Type " + eventTypeEvent);
            Log.e("tag", "Event Name " + eventKey);
            Log.e("tag", "Event Language " + tagKey);
            Log.e("tag", "Event Description" + descriptionText);
            Log.e("tag", "Event Start Time:" + formatedDb);
            Log.e("tag", "Event End Time:" + formatedDbEnd);
            Log.e("tag", "Location:" + locationData);
            Log.e("tag", "Location Key : " + locationKey);
            Log.e("tag", "Image " + Arrays.toString(inputImageData));
            Log.e("tag", "All day box: " + checkBoxDayBoolean);
            Log.e("tag", "open Event:" + checkBoxOpenBoolean);
            Log.e("tag", "Close Event:" + checkBoxCloseBoolean);
            Log.e("tag", "checkMenteeMentorBoolean:" + checkMenteeMentorParticipant);
        }

        Toast.makeText(getAppContext(), "Event Created", Toast.LENGTH_LONG).show();
    }

    private void eventLanguageSpinner() {
        List<Skill> categories = sqLiteDBHelper.getUserTags(Prefs.getUserKey());
        eventSkill = new ArrayList<>();
        if (categories.size() > 0) {

            for (int i = 0; i < categories.size(); i++) {

                eventSkill.add(categories.get(i).getTitle());
            }
        }
        setSpinnerValue(eventSkill, eventLanguage);

        eventLanguage.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String tagSelected = eventSkill.get(position);
                Log.e("tag", "eventType selected--> " + tagSelected);
                tagKey = sqLiteDBHelper.getTagUserKey(tagSelected);
                Log.e("tag", "eventType Key--> " + tagKey);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void eventNameSpinner() {
        List<EVENT_TYPE_MASTER> categories;
        eventType = new ArrayList<>();
        categories = sqLiteDBHelper.getEventType();
        if (sqLiteDBHelper.getEventType().size() > 0 && sqLiteDBHelper.getEventType() != null) {

            for (int i = 0; i < categories.size(); i++) {
                eventType.add(categories.get(i).getName());
            }
        }

        setSpinnerValue(eventType, eventName);
        eventName.setSelection(defaultValue);
        eventName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String eventTypeSelected = eventType.get(position);
                Log.e("tag", "eventTypeselected--> " + eventTypeSelected);
                eventKey = sqLiteDBHelper.getEventTypeKey(eventTypeSelected);
                Log.e("tag", "eventType Key--> " + eventKey);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    private void setSpinnerValue(final List<String> list, AppCompatSpinner spinner) {

        // Creating adapter for spinner
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(CreateEventActivity.this, R.layout.spin_list_custom, list);

        // Drop down layout style - list view with radio button
        languageAdapter.setDropDownViewResource(R.layout.spin_list_custom);
        // attaching data adapter to spinner
        spinner.setAdapter(languageAdapter);

    }

    private Bundle timePickerDialog() {

        Calendar calender = Calendar.getInstance();
        Bundle args = new Bundle();
        args.putInt("hour", Calendar.HOUR_OF_DAY);
        args.putInt("month", calender.get(Calendar.MONTH));
        args.putInt("minute", calender.get(Calendar.MINUTE));
        return args;
    }


    private void settingGoogleClient() {
        mGoogleApiClient = new GoogleApiClient
                .Builder(CreateEventActivity.this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this, this)
                .build();
    }

    private void settingToolbar() {

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Events");
        }
    }

    TimePickerDialog.OnTimeSetListener onTime = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            ArrayList<String> timeArrayList = convertToHourMinuteTimeSet(hourOfDay, minute);
            String timeEndFinal = String.valueOf(timeArrayList.get(0) + timeArrayList.get(1) + timeArrayList.get(2));
            SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
            SimpleDateFormat dateFormat = new SimpleDateFormat("hhmmaa");
            Calendar c = Calendar.getInstance();
            Date cc = c.getTime();
            Calendar datetime = Calendar.getInstance();
            datetime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime.set(Calendar.MINUTE, minute);
            try {
                Date dateTimePickedStart = dateFormat.parse(timeEndFinal);
                String dateTimeFinalStart = df.format(dateTimePickedStart);
                Date selectNextFinal = df.parse(dateTimeFinalStart);

                if (dateFinalEnd == null || timeStart == null) {
                    Toast.makeText(getAppContext(), "Select the Start Time/End Date", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (dbEnd.equals(dateCurFinalStart)) {
                    if (datetime.getTimeInMillis() < datetime2.getTimeInMillis()) {

                        Toast.makeText(getAppContext(), "Enter Valid time", Toast.LENGTH_SHORT).show();
                        endHourEdit.setText("");
                        end_min_edit.setText("");
                        endSessionText.setText("AM");

                    } else {
                        endHourEdit.setText(String.valueOf(timeArrayList.get(0)));
                        end_min_edit.setText(String.valueOf(timeArrayList.get(1)));
                        endSessionText.setText(String.valueOf(timeArrayList.get(2)));
                    }
                } else {
                    endHourEdit.setText(String.valueOf(timeArrayList.get(0)));
                    end_min_edit.setText(String.valueOf(timeArrayList.get(1)));
                    endSessionText.setText(String.valueOf(timeArrayList.get(2)));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }

    };

    TimePickerDialog.OnTimeSetListener ontime = new TimePickerDialog.OnTimeSetListener() {

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

            ArrayList<String> timeArrayList = convertToHourMinuteTimeSet(hourOfDay, minute);
            Log.e("tag", "" + timeArrayList);
            timeStart = String.valueOf(timeArrayList.get(0) + timeArrayList.get(1) + timeArrayList.get(2));
            Log.e("time", timeStart);

            Calendar c = Calendar.getInstance();
            datetime2 = Calendar.getInstance();
            datetime2.set(Calendar.HOUR_OF_DAY, hourOfDay);
            datetime2.set(Calendar.MINUTE, minute);
            Date cc = c.getTime();
            Log.e("newTime", "" + cc);
            SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
            String formattedDate = df.format(c.getTime());
            Log.e("newTimeFormat", "" + formattedDate);

            SimpleDateFormat dateFormat = new SimpleDateFormat("hhmmaa");
            SimpleDateFormat dateFormat3 = new SimpleDateFormat("yyyy-MM-dd");
            dateCurFinalStart = dateFormat3.format(cc);


            try {
                Date dateTimePickedStart = dateFormat.parse(timeStart);
                String dateTimeFinalStart = df.format(dateTimePickedStart);
                Date slectedFinal = df.parse(dateTimeFinalStart);
                Date currentFinal = df.parse(formattedDate);

                if (dateFinalStart == null) {
                    Toast.makeText(getAppContext(), "Select the Start Date", Toast.LENGTH_SHORT).show();
                    dateEndEdit.setText("");
                    startHourEdit.setText("");
                    startMinEdit.setText("");
                    startSessionText.setText("AM");
                    endHourEdit.setText("");
                    end_min_edit.setText("");
                    endSessionText.setText("AM");
                    return;
                } else {
                    dateStartEdit.setText(dateFinalStart);
                    startSessionText.setText("AM");
                    endHourEdit.setText("");
                    end_min_edit.setText("");
                    endSessionText.setText("AM");
                }
                if (dbStart.equals(dateCurFinalStart)) {
                    if (datetime2.getTimeInMillis() < c.getTimeInMillis()) {
                        Toast.makeText(getAppContext(), "Enter Valid time", Toast.LENGTH_SHORT).show();
                        startHourEdit.setText("");
                        startMinEdit.setText("");
                        startSessionText.setText("AM");
                    } else {
                        startHourEdit.setText(timeArrayList.get(0));
                        startMinEdit.setText(timeArrayList.get(1));
                        startSessionText.setText(timeArrayList.get(2));
                    }
                } else {
                    startHourEdit.setText(timeArrayList.get(0));
                    startMinEdit.setText(timeArrayList.get(1));
                    startSessionText.setText(timeArrayList.get(2));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    };

    private ArrayList<String> convertToHourMinuteTimeSet(int hourOfDay, int minute) {

        ArrayList<String> integerArrayList = new ArrayList<>();

        int hour = hourOfDay;
        int minutes = minute;
        String timeSet = "";
        if (hour > 12) {
            hour -= 12;
            timeSet = "PM";
        } else if (hour == 0) {
            hour += 12;
            timeSet = "AM";
        } else if (hour == 12) {
            timeSet = "PM";
        } else {
            timeSet = "AM";
        }

        String min = "";
        if (minutes < 10)
            min = "0" + minutes;
        else
            min = String.valueOf(minutes);

        integerArrayList.add(String.valueOf(hour));
        integerArrayList.add(min);
        integerArrayList.add(timeSet);

        return integerArrayList;

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {

            case R.id.day_check:

                if (isChecked) {
                    Log.e("tag", " " + isChecked);
                    Log.e("tag", " " + dateFinalStart);
                    if (dateFinalStart == null) {
                        Toast.makeText(getAppContext(), "Select the Start Date Before Checking", Toast.LENGTH_SHORT).show();
                        checkDay.setChecked(false);
                        dateEndEdit.setText("");
                        startHourEdit.setText("");
                        startMinEdit.setText("");
                        startSessionText.setText("AM");
                        endHourEdit.setText("");
                        end_min_edit.setText("");
                        endSessionText.setText("AM");
                        return;
                    } else {
                        dateStartEdit.setText(dateFinalStart);
                        dateEndEdit.setText(dateFinalStart);
                        checkBoxDayBoolean = checkDay.isChecked();
                        SimpleDateFormat sdf = new SimpleDateFormat("EEE,MMM d yyyy");
                        String currentDateSet = sdf.format(new Date());

                        if (currentDateSet.equals(dateFinalStart)) {
                            Calendar c = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("hh:mm aa");
                            String formattedDate = df.format(c.getTime());
                            Log.e("newTimeFormat", "" + formattedDate);
                            String[] parts = formattedDate.split(" ");
                            String part1 = parts[0];
                            String part2 = parts[1];
                            String[] time = part1.split(":");
                            String hourText = time[0];
                            String minText = time[1];
                            startHourEdit.setText(hourText);
                            startMinEdit.setText(minText);
                            startSessionText.setText(part2);
                            timePickStart.setEnabled(false);
                            timePickEnd.setEnabled(false);
                            dateEndEdit.setEnabled(false);
                            endHourEdit.setText("11");
                            end_min_edit.setText("59");
                            endSessionText.setText("PM");
                        } else {
                            startHourEdit.setText("12");
                            startMinEdit.setText("00");
                            startSessionText.setText("AM");
                            timePickStart.setEnabled(false);
                            timePickEnd.setEnabled(false);
                            dateEndEdit.setEnabled(false);
                            endHourEdit.setText("11");
                            end_min_edit.setText("59");
                            endSessionText.setText("PM");
                        }
                    }
                } else {
                    checkBoxDayBoolean = checkDay.isChecked();
                    dateEndEdit.setText("");
                    dateFinalStart = null;
                    timePickStart.setEnabled(true);
                    timePickEnd.setEnabled(true);
                    dateEndEdit.setEnabled(true);
                    startHourEdit.setText("");
                    startMinEdit.setText("");
                    startSessionText.setText("AM");
                    endHourEdit.setText("");
                    end_min_edit.setText("");
                    endSessionText.setText("AM");
                    dateEndEdit.setText("");
                    dateStartEdit.setText("");
                    dateEndEdit.setText("");
                }

                break;
           /* case R.id.open:
                if (isChecked) {
                    checkBoxOpenBoolean = openEvent.isChecked();
                } else {
                    checkBoxOpenBoolean = false;
                }
                break;
            case R.id.close:
                if (isChecked) {
                    checkBoxCloseBoolean = closeEvent.isChecked();
                } else {
                    checkBoxCloseBoolean = false;
                }*/
            case R.id.mentor:
                if (isChecked) {
                    checkBoxMentorBoolean = mentor.isChecked();
                } else {
                    checkBoxMentorBoolean = false;
                }
                break;
            case R.id.mentee:
                if (isChecked) {
                    checkBoxMenteeBoolean = mentee.isChecked();
                } else {
                    checkBoxMenteeBoolean = false;
                }
                break;
            case R.id.participant:
                if (isChecked) {
                    checkBoxParticipantBoolean = participant.isChecked();
                } else {
                    checkBoxParticipantBoolean = false;
                }
                break;
        }
    }


    public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ContactHolder> {

        public List<Contact> contactList;
        private int mRowIndex = -1;

        private Context context;
        private int itemResource;

        public ContactAdapter(Context context, int itemResource, List<Contact> contactList) {
            this.contactList = contactList;
            this.context = context;
            this.itemResource = itemResource;
        }

        public void setContactList(List<Contact> dataContactList) {
            if (contactList != dataContactList) {
                contactList = dataContactList;
                notifyDataSetChanged();
            }
        }

        public void setmRowIndex(int index) {
            mRowIndex = index;
        }

        @Override
        public ContactAdapter.ContactHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(itemResource, parent, false);
            return new ContactAdapter.ContactHolder(v);
        }

        @Override
        public void onBindViewHolder(ContactAdapter.ContactHolder holder, final int position) {
            final Contact contact = contactList.get(position);
            holder.img.setImageBitmap(ThumbnailUtils.extractThumbnail(sqLiteDBHelper.getUserImage(contact.getId())/*Utility.getPhoto(contact.getImage())*/, 64, 64));
            holder.itemView.setTag(position);
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Context context = v.getContext();

                    if (!Objects.equals(contact.getId(), Prefs.getUserKey())) {

                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
                        } else {
                            builder = new AlertDialog.Builder(context);
                        }
                        builder.setMessage("Are you sure you want to delete this contact?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        contactList.remove(position);
                                        notifyItemRemoved(position);
                                        updateContactList(contactList);
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
                    return true;
                }
            });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!Objects.equals(contactList.get(position).getId(), Prefs.getUserKey())) {
                        Intent intent = new Intent(context, OtherProfileActivity.class);
                        intent.putExtra("id", contactList.get(position).getId());
                        context.startActivity(intent);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return this.contactList.size();
        }

        class ContactHolder extends RecyclerView.ViewHolder {

            //  private ImageView img;
            private HexagonMaskView img;

            ContactHolder(View itemView) {
                super(itemView);
                // Set up the UI widgets of the holder
                this.img = (HexagonMaskView) itemView.findViewById(R.id.add_contact_image);
            }

        }


    }

}

