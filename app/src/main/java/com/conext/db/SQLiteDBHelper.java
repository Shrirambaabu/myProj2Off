package com.conext.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.location.Location;
import android.util.Log;

import com.conext.R;
import com.conext.model.Contact;
import com.conext.model.MyMentees;
import com.conext.model.MyMentor;
import com.conext.model.Network;
import com.conext.model.NetworkTag;
import com.conext.model.OtherNotification;
import com.conext.model.ProfileAlumni;
import com.conext.model.Skill;
import com.conext.model.db.EDUCATION_MASTER;
import com.conext.model.db.EVENT_TYPE_MASTER;
import com.conext.model.db.USER_EVENT;
import com.conext.model.db.USER_PROFILE;
import com.conext.utils.Prefs;
import com.conext.utils.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.conext.db.CsvToSQLiteHelper.insertInToEducationMasterTable;
import static com.conext.db.CsvToSQLiteHelper.insertInToEventTypeMasterTable;
import static com.conext.db.CsvToSQLiteHelper.insertInToEventUserParticipantsTable;
import static com.conext.db.CsvToSQLiteHelper.insertInToLocationTable;
import static com.conext.db.CsvToSQLiteHelper.insertInToTagMasterTable;
import static com.conext.db.CsvToSQLiteHelper.insertInToUserEducationTable;
import static com.conext.db.CsvToSQLiteHelper.insertInToUserEventTable;
import static com.conext.db.CsvToSQLiteHelper.insertInToUserProfileTable;
import static com.conext.db.CsvToSQLiteHelper.insertInToUserTagTable;
import static com.conext.db.CsvToSQLiteHelper.insertInToUserWorkrTable;
import static com.conext.db.CsvToSQLiteHelper.insertInToWorkMasterTable;
import static com.conext.db.DbConstants.ACTIVITIES;
import static com.conext.db.DbConstants.ADDRESS;
import static com.conext.db.DbConstants.ALUMNI_MEET;
import static com.conext.db.DbConstants.ANALYTICS;
import static com.conext.db.DbConstants.C;
import static com.conext.db.DbConstants.CITY;
import static com.conext.db.DbConstants.COFFEE_CONNECT;
import static com.conext.db.DbConstants.COMPANY_DISPLAY;
import static com.conext.db.DbConstants.COMPANY_SIZE;
import static com.conext.db.DbConstants.CREATED_BY;
import static com.conext.db.DbConstants.CREATED_TIMESTAMP;
import static com.conext.db.DbConstants.CREATE_TABLE_QUERY_EDUCATION_MASTER;
import static com.conext.db.DbConstants.CREATE_TABLE_QUERY_EVENT_TYPE_MASTER;
import static com.conext.db.DbConstants.CREATE_TABLE_QUERY_LOCATION;
import static com.conext.db.DbConstants.CREATE_TABLE_QUERY_TAG_MASTER;
import static com.conext.db.DbConstants.CREATE_TABLE_QUERY_USER_EDUCATION;
import static com.conext.db.DbConstants.CREATE_TABLE_QUERY_USER_EVENT;
import static com.conext.db.DbConstants.CREATE_TABLE_QUERY_USER_PARTICIPANTS;
import static com.conext.db.DbConstants.CREATE_TABLE_QUERY_USER_PROFILE;
import static com.conext.db.DbConstants.CREATE_TABLE_QUERY_USER_TAG;
import static com.conext.db.DbConstants.CREATE_TABLE_QUERY_USER_WORK;
import static com.conext.db.DbConstants.CREATE_TABLE_QUERY_WORK_MASTER;
import static com.conext.db.DbConstants.C_PLUS_PLUS;
import static com.conext.db.DbConstants.DEFAULT_IMAGE;
import static com.conext.db.DbConstants.DEFAULT_OC;
import static com.conext.db.DbConstants.DEGREE;
import static com.conext.db.DbConstants.DELETE_FLAG;
import static com.conext.db.DbConstants.DESCRIPTION;
import static com.conext.db.DbConstants.DOT_NET;
import static com.conext.db.DbConstants.EDUCATION_KEY;
import static com.conext.db.DbConstants.EMAIL_ID;
import static com.conext.db.DbConstants.EVENT_END_TIMESTAMP;
import static com.conext.db.DbConstants.EVENT_KEY;
import static com.conext.db.DbConstants.EVENT_START_TIMESTAMP;
import static com.conext.db.DbConstants.EVENT_TITLE;
import static com.conext.db.DbConstants.EVENT_TYPE_KEY;
import static com.conext.db.DbConstants.FIELD_OF_STUDY;
import static com.conext.db.DbConstants.FIRST_NAME;
import static com.conext.db.DbConstants.FITNESS;
import static com.conext.db.DbConstants.FOUNDED;
import static com.conext.db.DbConstants.FUN;
import static com.conext.db.DbConstants.GET_EVENT_TYPE;
import static com.conext.db.DbConstants.GHS_SCHOOL;
import static com.conext.db.DbConstants.GRADE;
import static com.conext.db.DbConstants.HIKING;
import static com.conext.db.DbConstants.IMAGE_KEY;
import static com.conext.db.DbConstants.INDUSTRY;
import static com.conext.db.DbConstants.INFO;
import static com.conext.db.DbConstants.IS_OPEN;
import static com.conext.db.DbConstants.JAVA;
import static com.conext.db.DbConstants.JAVA_SCRIPT;
import static com.conext.db.DbConstants.JSP;
import static com.conext.db.DbConstants.LAST_NAME;
import static com.conext.db.DbConstants.LATITUDE;
import static com.conext.db.DbConstants.LINKED_IN_ID;
import static com.conext.db.DbConstants.LOCATION_KEY;
import static com.conext.db.DbConstants.LONGITUDE;
import static com.conext.db.DbConstants.LUNCH_MEETUP;
import static com.conext.db.DbConstants.MENTEE;
import static com.conext.db.DbConstants.MENTEE_FLAG;
import static com.conext.db.DbConstants.MENTOR;
import static com.conext.db.DbConstants.MENTORSHIP;
import static com.conext.db.DbConstants.MENTOR_FLAG;
import static com.conext.db.DbConstants.MODIFIED_BY;
import static com.conext.db.DbConstants.MODIFIED_TIMESTAMP;
import static com.conext.db.DbConstants.NAME;
import static com.conext.db.DbConstants.PARTICIPANT;
import static com.conext.db.DbConstants.PARTICIPANT_STATUS;
import static com.conext.db.DbConstants.PASSWORD;
import static com.conext.db.DbConstants.PHOTOGRAPHY;
import static com.conext.db.DbConstants.PROFILE_PIC;
import static com.conext.db.DbConstants.PYTHON;
import static com.conext.db.DbConstants.SCHOOL_NAME;
import static com.conext.db.DbConstants.SKILING;
import static com.conext.db.DbConstants.SOCCER;
import static com.conext.db.DbConstants.SPECIALITIES;
import static com.conext.db.DbConstants.TABLE_NAME_EDUCATION_MASTER;
import static com.conext.db.DbConstants.TABLE_NAME_EVENT_TYPE_MASTER;
import static com.conext.db.DbConstants.TABLE_NAME_LOCATION;
import static com.conext.db.DbConstants.TABLE_NAME_TAG_MASTER;
import static com.conext.db.DbConstants.TABLE_NAME_USER_EDUCATION;
import static com.conext.db.DbConstants.TABLE_NAME_USER_EVENT;
import static com.conext.db.DbConstants.TABLE_NAME_USER_PROFILE;
import static com.conext.db.DbConstants.TABLE_NAME_USER_TAG;
import static com.conext.db.DbConstants.TABLE_NAME_USER_WORK;
import static com.conext.db.DbConstants.TABLE_NAME_WORK_MASTER;
import static com.conext.db.DbConstants.TABLE_QUERY_USER_PARTICIPANTS;
import static com.conext.db.DbConstants.TAG_DESCRIPTION;
import static com.conext.db.DbConstants.TAG_KEY;
import static com.conext.db.DbConstants.TAG_NAME;
import static com.conext.db.DbConstants.TITLE_DISPLAY;
import static com.conext.db.DbConstants.TYPE;
import static com.conext.db.DbConstants.USER_KEY;
import static com.conext.db.DbConstants.WEBSITE;
import static com.conext.db.DbConstants.YEAR_FINISH;
import static com.conext.db.DbConstants.YEAR_START;
import static com.conext.utils.Utility.getDateTime;

/**
 * Created by Ashith VL on 6/22/2017.
 */

public class SQLiteDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "conext.db";
    public static final int DATABASE_VERSION = 1;


    private Context mCtx;

    //modified constructor
    public SQLiteDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mCtx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY_USER_PROFILE);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY_TAG_MASTER);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY_EDUCATION_MASTER);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY_WORK_MASTER);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY_LOCATION);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY_EVENT_TYPE_MASTER);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY_USER_TAG);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY_USER_EDUCATION);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY_USER_PARTICIPANTS);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY_USER_WORK);
        sqLiteDatabase.execSQL(CREATE_TABLE_QUERY_USER_EVENT);
        dummyTags(sqLiteDatabase);

        //  insertInToUserProfileTable(mCtx, sqLiteDatabase);

        //  exportDb(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_PROFILE);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_TAG_MASTER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EDUCATION_MASTER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_WORK_MASTER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_LOCATION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_EVENT_TYPE_MASTER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_TAG);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_EDUCATION);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_WORK);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_QUERY_USER_PARTICIPANTS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME_USER_EVENT);
        onCreate(sqLiteDatabase);
    }


    private void dummyTags(SQLiteDatabase db) {

        ContentValues tagValues1 = new ContentValues();
        tagValues1.put(TAG_NAME, JAVA);
        tagValues1.put(TAG_DESCRIPTION, JAVA);
        tagValues1.put(CREATED_BY, JAVA);
        tagValues1.put(CREATED_TIMESTAMP, JAVA);
        tagValues1.put(MODIFIED_BY, JAVA);
        tagValues1.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues1);

        ContentValues tagValues2 = new ContentValues();
        tagValues2.put(TAG_NAME, C);
        tagValues2.put(TAG_DESCRIPTION, JAVA);
        tagValues2.put(CREATED_BY, JAVA);
        tagValues2.put(CREATED_TIMESTAMP, JAVA);
        tagValues2.put(MODIFIED_BY, JAVA);
        tagValues2.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues2);

        ContentValues tagValues3 = new ContentValues();
        tagValues3.put(TAG_NAME, C_PLUS_PLUS);
        tagValues3.put(TAG_DESCRIPTION, JAVA);
        tagValues3.put(CREATED_BY, JAVA);
        tagValues3.put(CREATED_TIMESTAMP, JAVA);
        tagValues3.put(MODIFIED_BY, JAVA);
        tagValues3.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues3);

        ContentValues tagValues4 = new ContentValues();
        tagValues4.put(TAG_NAME, PYTHON);
        tagValues4.put(TAG_DESCRIPTION, JAVA);
        tagValues4.put(CREATED_BY, JAVA);
        tagValues4.put(CREATED_TIMESTAMP, JAVA);
        tagValues4.put(MODIFIED_BY, JAVA);
        tagValues4.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues4);

        ContentValues tagValues5 = new ContentValues();
        tagValues5.put(TAG_NAME, DOT_NET);
        tagValues5.put(TAG_DESCRIPTION, JAVA);
        tagValues5.put(CREATED_BY, JAVA);
        tagValues5.put(CREATED_TIMESTAMP, JAVA);
        tagValues5.put(MODIFIED_BY, JAVA);
        tagValues5.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues5);

        ContentValues tagValues6 = new ContentValues();
        tagValues6.put(TAG_NAME, PHOTOGRAPHY);
        tagValues6.put(TAG_DESCRIPTION, JAVA);
        tagValues6.put(CREATED_BY, JAVA);
        tagValues6.put(CREATED_TIMESTAMP, JAVA);
        tagValues6.put(MODIFIED_BY, JAVA);
        tagValues6.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues6);

        ContentValues tagValues7 = new ContentValues();
        tagValues7.put(TAG_NAME, SKILING);
        tagValues7.put(TAG_DESCRIPTION, JAVA);
        tagValues7.put(CREATED_BY, JAVA);
        tagValues7.put(CREATED_TIMESTAMP, JAVA);
        tagValues7.put(MODIFIED_BY, JAVA);
        tagValues7.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues7);

        ContentValues tagValues8 = new ContentValues();
        tagValues8.put(TAG_NAME, FITNESS);
        tagValues8.put(TAG_DESCRIPTION, JAVA);
        tagValues8.put(CREATED_BY, JAVA);
        tagValues8.put(CREATED_TIMESTAMP, JAVA);
        tagValues8.put(MODIFIED_BY, JAVA);
        tagValues8.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues8);

        ContentValues tagValues9 = new ContentValues();
        tagValues9.put(TAG_NAME, HIKING);
        tagValues9.put(TAG_DESCRIPTION, JAVA);
        tagValues9.put(CREATED_BY, JAVA);
        tagValues9.put(CREATED_TIMESTAMP, JAVA);
        tagValues9.put(MODIFIED_BY, JAVA);
        tagValues9.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues9);

        ContentValues tagValues10 = new ContentValues();
        tagValues10.put(TAG_NAME, SOCCER);
        tagValues10.put(TAG_DESCRIPTION, JAVA);
        tagValues10.put(CREATED_BY, JAVA);
        tagValues10.put(CREATED_TIMESTAMP, JAVA);
        tagValues10.put(MODIFIED_BY, JAVA);
        tagValues10.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues10);

        ContentValues tagValues11 = new ContentValues();
        tagValues11.put(TAG_NAME, ANALYTICS);
        tagValues11.put(TAG_DESCRIPTION, JAVA);
        tagValues11.put(CREATED_BY, JAVA);
        tagValues11.put(CREATED_TIMESTAMP, JAVA);
        tagValues11.put(MODIFIED_BY, JAVA);
        tagValues11.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues11);

        ContentValues tagValues12 = new ContentValues();
        tagValues12.put(TAG_NAME, JSP);
        tagValues12.put(TAG_DESCRIPTION, JAVA);
        tagValues12.put(CREATED_BY, JAVA);
        tagValues12.put(CREATED_TIMESTAMP, JAVA);
        tagValues12.put(MODIFIED_BY, JAVA);
        tagValues12.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues12);

        ContentValues tagValues13 = new ContentValues();
        tagValues13.put(TAG_NAME, JAVA_SCRIPT);
        tagValues13.put(TAG_DESCRIPTION, JAVA);
        tagValues13.put(CREATED_BY, JAVA);
        tagValues13.put(CREATED_TIMESTAMP, JAVA);
        tagValues13.put(MODIFIED_BY, JAVA);
        tagValues13.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_TAG_MASTER, null, tagValues13);

        for (int i = 0; i < 5; i++) {

            ContentValues educationValues = new ContentValues();
            educationValues.put(SCHOOL_NAME, GHS_SCHOOL + i);
            educationValues.put(CITY, JAVA + i);
            educationValues.put(ADDRESS, JAVA + i);
            educationValues.put(TYPE, JAVA + i);
            educationValues.put(INFO, JAVA + i);
            educationValues.put(WEBSITE, JAVA + i);
            educationValues.put(DELETE_FLAG, "false");
            educationValues.put(CREATED_BY, JAVA + i);
            educationValues.put(CREATED_TIMESTAMP, JAVA + i);
            educationValues.put(MODIFIED_BY, JAVA + i);
            educationValues.put(MODIFIED_TIMESTAMP, JAVA + i);
            educationValues.put(LOCATION_KEY, "" + i);

            // Inserting Row
            db.insert(TABLE_NAME_EDUCATION_MASTER, null, educationValues);

            ContentValues workValues = new ContentValues();
            workValues.put(LINKED_IN_ID, JAVA + i);
            workValues.put(NAME, JAVA + i);
            workValues.put(INFO, JAVA + i);
            workValues.put(SPECIALITIES, JAVA + i);
            workValues.put(WEBSITE, JAVA + i);
            workValues.put(INDUSTRY, JAVA + i);
            workValues.put(TYPE, JAVA + i);
            workValues.put(COMPANY_SIZE, JAVA + i);
            workValues.put(FOUNDED, JAVA + i);
            workValues.put(DELETE_FLAG, "false");
            workValues.put(CREATED_BY, JAVA + i);
            workValues.put(CREATED_TIMESTAMP, JAVA + i);
            workValues.put(MODIFIED_TIMESTAMP, "false");
            workValues.put(MODIFIED_BY, JAVA + i);
            workValues.put(LOCATION_KEY, "" + i);

            // Inserting Row
            db.insert(TABLE_NAME_WORK_MASTER, null, workValues);
        }

        ContentValues eventValues1 = new ContentValues();
        eventValues1.put(NAME, MENTORSHIP);
        eventValues1.put(DEFAULT_OC, JAVA);
        eventValues1.put(DEFAULT_IMAGE, "false");
        eventValues1.put(DELETE_FLAG, "false");
        eventValues1.put(CREATED_BY, JAVA);
        eventValues1.put(CREATED_TIMESTAMP, JAVA);
        eventValues1.put(MODIFIED_BY, JAVA);
        eventValues1.put(MODIFIED_TIMESTAMP, JAVA);

        // Inserting Row
        db.insert(TABLE_NAME_EVENT_TYPE_MASTER, null, eventValues1);
        ContentValues eventValues2 = new ContentValues();
        eventValues2.put(NAME, ALUMNI_MEET);
        eventValues2.put(DEFAULT_OC, JAVA);
        eventValues2.put(DEFAULT_IMAGE, "false");
        eventValues2.put(DELETE_FLAG, "false");
        eventValues2.put(CREATED_BY, JAVA);
        eventValues2.put(CREATED_TIMESTAMP, JAVA);
        eventValues2.put(MODIFIED_BY, JAVA);
        eventValues2.put(MODIFIED_TIMESTAMP, JAVA);

        db.insert(TABLE_NAME_EVENT_TYPE_MASTER, null, eventValues2);

        ContentValues eventValues3 = new ContentValues();
        eventValues3.put(NAME, LUNCH_MEETUP);
        eventValues3.put(DEFAULT_OC, JAVA);
        eventValues3.put(DEFAULT_IMAGE, "false");
        eventValues3.put(DELETE_FLAG, "false");
        eventValues3.put(CREATED_BY, JAVA);
        eventValues3.put(CREATED_TIMESTAMP, JAVA);
        eventValues3.put(MODIFIED_BY, JAVA);
        eventValues3.put(MODIFIED_TIMESTAMP, JAVA);
        db.insert(TABLE_NAME_EVENT_TYPE_MASTER, null, eventValues3);

        ContentValues eventValues4 = new ContentValues();
        eventValues4.put(NAME, COFFEE_CONNECT);
        eventValues4.put(DEFAULT_OC, JAVA);
        eventValues4.put(DEFAULT_IMAGE, "false");
        eventValues4.put(DELETE_FLAG, "false");
        eventValues4.put(CREATED_BY, JAVA);
        eventValues4.put(CREATED_TIMESTAMP, JAVA);
        eventValues4.put(MODIFIED_BY, JAVA);
        eventValues4.put(MODIFIED_TIMESTAMP, JAVA);
        db.insert(TABLE_NAME_EVENT_TYPE_MASTER, null, eventValues4);

        // db.close();
    }

    public boolean checkUser(String email) {


        Log.e("tag", "checkUser called");

        // array of columns to fetch
        String[] columns = {"UserKey"};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = "EmailID" + " = ?";
        // selection argument
        String[] selectionArgs = {email};

        Cursor cursor = db.query(TABLE_NAME_USER_PROFILE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();

        return cursorCount > 0;

    }

    public long login(USER_PROFILE user_profile) {
        long key = -1;
        // array of columns to fetch
        String[] columns = {"UserKey"};
        SQLiteDatabase db = this.getReadableDatabase();

        //exportDb(db);

        // selection criteria
        String selection = "EmailID" + " = ?" + " AND " + "Password" + " = ?";
        // selection arguments
        String[] selectionArgs = {user_profile.getEmailID(), user_profile.getPassword()};
        // query user table with conditions]
        Cursor cursor = db.query(TABLE_NAME_USER_PROFILE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        //int cursorCount = cursor.getCount();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                key = cursor.getLong(cursor.getColumnIndex(USER_KEY));
                updateContact(key);
            }
            cursor.close();
        }

        db.close();

        if (key > 0) {
            return key;
        }
        return 0;
    }


    public long createEvent(USER_EVENT user_event, String key) {

        long id = 0;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues eventValues = new ContentValues();

        eventValues.put(USER_KEY, key);
        eventValues.put(EVENT_TYPE_KEY, user_event.getEventTypeKey());
        eventValues.put(TAG_KEY, user_event.getTagKey());
        eventValues.put(LOCATION_KEY, user_event.getLocationKey());
        eventValues.put(DESCRIPTION, user_event.getDescription());
        eventValues.put(EVENT_TITLE, user_event.getEventTitle());
        eventValues.put(EVENT_START_TIMESTAMP, user_event.getEventStartTs());
        eventValues.put(EVENT_END_TIMESTAMP, user_event.getEventEndTs());
        eventValues.put(ADDRESS, user_event.getAddress());
        eventValues.put(IS_OPEN, user_event.isOpen());
        eventValues.put(IMAGE_KEY, user_event.getImageKey());
        eventValues.put(DELETE_FLAG, user_event.getDeleteFlag());
        eventValues.put(CREATED_BY, user_event.getCreatedBy());
        eventValues.put(CREATED_TIMESTAMP, user_event.getCreatedTs());
        eventValues.put(MODIFIED_BY, user_event.getModifiedBy());
        eventValues.put(MODIFIED_TIMESTAMP, user_event.getModifiedTs());

        id = db.insert(TABLE_NAME_USER_EVENT, null, eventValues);
        db.close();

        return id;
    }

    public long createEvent(USER_EVENT user_event) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues eventValues = new ContentValues();
        long id;
        //eventValues.put("UserKey", key);
        eventValues.put(EVENT_TYPE_KEY, user_event.getEventTypeKey());
        eventValues.put(TAG_KEY, user_event.getTagKey());
        eventValues.put(LOCATION_KEY, user_event.getLocationKey());
        eventValues.put(DESCRIPTION, user_event.getDescription());
        eventValues.put(EVENT_TITLE, user_event.getDescription());
        eventValues.put(EVENT_START_TIMESTAMP, user_event.getEventStartTs());
        eventValues.put(EVENT_END_TIMESTAMP, user_event.getEventEndTs());
        eventValues.put(ADDRESS, user_event.getAddress());
        eventValues.put(IS_OPEN, user_event.isOpen());
        eventValues.put(IMAGE_KEY, user_event.getImageKey());
        eventValues.put(DELETE_FLAG, user_event.getDeleteFlag());
        eventValues.put(CREATED_BY, user_event.getCreatedBy());
        eventValues.put(CREATED_TIMESTAMP, user_event.getCreatedTs());
        eventValues.put(MODIFIED_BY, user_event.getModifiedBy());
        eventValues.put(MODIFIED_TIMESTAMP, user_event.getModifiedTs());

        id = db.insert(TABLE_NAME_USER_EVENT, null, eventValues);
        db.close();
        return id;

    }

    public void insertDataFromCSV() {

        SQLiteDatabase db = this.getWritableDatabase();

        insertInToUserProfileTable(mCtx, db);
        insertInToTagMasterTable(mCtx, db);
        insertInToEducationMasterTable(mCtx, db);
        insertInToWorkMasterTable(mCtx, db);
        insertInToLocationTable(mCtx, db);
        insertInToEventTypeMasterTable(mCtx, db);
        insertInToUserTagTable(mCtx, db);
        insertInToEventUserParticipantsTable(mCtx, db);
        insertInToUserWorkrTable(mCtx, db);
        insertInToUserEducationTable(mCtx, db);
        insertInToUserEventTable(mCtx, db);

        db.close();

    }

    public long register(USER_PROFILE user_profile) {

        SQLiteDatabase db = this.getWritableDatabase();

        Log.e("tag", "register called");

        ContentValues values = new ContentValues();
        values.put(EMAIL_ID, user_profile.getEmailID());
        values.put(PASSWORD, user_profile.getPassword());
        values.put(FIRST_NAME, user_profile.getFirstName());
        values.put(LAST_NAME, user_profile.getLastName());
        values.put(TITLE_DISPLAY, user_profile.getTitleDisplay());
        values.put(COMPANY_DISPLAY, user_profile.getCompanyDisplay());
        values.put(PROFILE_PIC, user_profile.getProfilePic());
        values.put(DELETE_FLAG, user_profile.getDeleteFlg());
        values.put(CREATED_BY, user_profile.getCreatedBy());
        values.put(CREATED_TIMESTAMP, user_profile.getCreatedTs());
        values.put(MODIFIED_BY, user_profile.getModifiedBy());
        values.put(MODIFIED_TIMESTAMP, user_profile.getModifiedTs());
        values.put(LOCATION_KEY, user_profile.getLocationKey());

        // Inserting Row
        long id = db.insert(TABLE_NAME_USER_PROFILE, null, values);
        db.close();
        if (id != 0) {
            Log.e("tag", "inserted");


            SQLiteDatabase dbUpdate = this.getWritableDatabase();

            ContentValues valueUpdate = new ContentValues();
            valueUpdate.put(CREATED_BY, id);

            dbUpdate.update(TABLE_NAME_USER_PROFILE, valueUpdate, "UserKey" + " = ?",
                    new String[]{String.valueOf(id)});
            dbUpdate.close();
            return id;
        }
        return 0;
    }

    // Updating single contact
    private int updateContact(long key) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(MODIFIED_BY, String.valueOf(key));
        values.put(MODIFIED_TIMESTAMP, getDateTime());

        // updating row
        return db.update(TABLE_NAME_USER_PROFILE, values, "UserKey" + " = ?",
                new String[]{String.valueOf(key)});
    }

    private long updateLocation(String key, long locationKey) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(LOCATION_KEY, String.valueOf(locationKey));
        values.put(MODIFIED_TIMESTAMP, getDateTime());
        values.put(MODIFIED_BY, key);
        long row = db.update(TABLE_NAME_USER_PROFILE, values, "UserKey" + " = ?",
                new String[]{String.valueOf(key)});
        Log.e("tag", "locKey" + locationKey + "userkey" + key + "row" + row);
        if (row > 0) {
            db.close();
            return row;
        }
        db.close();
        return 0;
    }

    public long settLocation(String key, String lat, String lon) {

        long locKey = getLocation(key);

        if (locKey != 0) {

            SQLiteDatabase dbUpdate = this.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(LATITUDE, lat);
            values.put(LONGITUDE, lon);
            values.put(MODIFIED_TIMESTAMP, getDateTime());
            values.put(MODIFIED_BY, key);
            dbUpdate.update(TABLE_NAME_LOCATION, values, LOCATION_KEY + " = ?",
                    new String[]{String.valueOf(locKey)});
            dbUpdate.close();
            return locKey;

        } else {
            SQLiteDatabase db = this.getWritableDatabase();

            long locationKey = checkLocationTable(lat, lon, db);
            if (locationKey != 0) {
                updateLocation(key, locationKey);
                db.close();
                return locationKey;
            } else {
                ContentValues values = new ContentValues();
                values.put(TYPE, FUN);
                values.put(LATITUDE, lat);
                values.put(LONGITUDE, lon);
                values.put(DELETE_FLAG, "0");
                values.put(CREATED_BY, "" + key);
                values.put(CREATED_TIMESTAMP, getDateTime());
                values.put(MODIFIED_TIMESTAMP, getDateTime());
                values.put(MODIFIED_BY, key);
                // Inserting Row
                long locationKeyInsert = db.insert(TABLE_NAME_LOCATION, null, values);
                Log.e("tag", "lockeyyyy" + locationKeyInsert);
                db.close();
                updateLocation(key, locationKey);
                return locationKeyInsert;
            }
        }
    }


    private long checkLocationTable(String lat, String lon, SQLiteDatabase db) {
        long key = -1;

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_LOCATION + " WHERE Latitude" + " = '" + lat + "' AND " + "Longitude" + " = '" + lon + "'";
        Log.e("tag", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                key = cursor.getInt(0);
                Log.e("tag", "new key " + key);
            } while (cursor.moveToNext());
        }
        if (key > 0) {
            cursor.close();
            return key;
        }
        if (cursor != null) {
            cursor.close();
        }
        return 0;
    }

    private long getLocation(String userKey) {

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE + " WHERE UserKey=" + userKey;
        Log.e("tag", selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        long key = 0;
        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                key = cursor.getInt(13);
                Log.e("tag", "key " + key);
            } while (cursor.moveToNext());
            cursor.close();
        }
        if (key > 0) {
            db.close();
            return key;
        }
        db.close();
        return 0;
    }

    public ArrayList<EVENT_TYPE_MASTER> getEventType() {

        ArrayList<EVENT_TYPE_MASTER> event_type_masterArrayList = new ArrayList<>();

        // Select All Query
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(GET_EVENT_TYPE, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                EVENT_TYPE_MASTER event_type_master = new EVENT_TYPE_MASTER();
                event_type_master.setEventTypeKey(cursor.getString(0));
                event_type_master.setName(cursor.getString(1));
                event_type_master.setDefaultOC(cursor.getString(2));
                event_type_master.setDefaultImage(cursor.getString(3));
                event_type_master.setDeleteFlag(cursor.getString(4));
                event_type_master.setCreatedBy(cursor.getString(5));
                event_type_master.setCreatedTs(cursor.getString(6));
                event_type_master.setModifiedBy(cursor.getString(7));
                event_type_master.setModifiedTs(cursor.getString(8));
                // Adding contact to list
                event_type_masterArrayList.add(event_type_master);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return event_type_masterArrayList;
    }


    public ArrayList<EDUCATION_MASTER> getEducation() {

        ArrayList<EDUCATION_MASTER> education_masterArrayList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_EDUCATION_MASTER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                EDUCATION_MASTER education_master = new EDUCATION_MASTER();
                education_master.setEducationKey(cursor.getString(0));
                education_master.setSchoolName(cursor.getString(1));
                // Adding  to list
                education_masterArrayList.add(education_master);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return education_masterArrayList;
    }

    public boolean addUserSKill(String userKey, String tagKey, String mentee, String mentor) {
        ContentValues valueUpdate = new ContentValues();
        valueUpdate.put(USER_KEY, userKey);
        valueUpdate.put(TAG_KEY, tagKey);
        valueUpdate.put(MENTOR_FLAG, mentor);
        valueUpdate.put(MENTEE_FLAG, mentee);
        valueUpdate.put(DELETE_FLAG, "false");
        valueUpdate.put(CREATED_BY, userKey);
        valueUpdate.put(CREATED_TIMESTAMP, getDateTime());
        valueUpdate.put(MODIFIED_BY, userKey);
        valueUpdate.put(MODIFIED_TIMESTAMP, getDateTime());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME_USER_TAG, null, valueUpdate);
        db.close();
        return true;

    }

    public boolean addUserInfo(String userKey, String eduKey, String yearStart, String yearEnd, String major, String degree) {
        ContentValues valueUpdate = new ContentValues();
        valueUpdate.put(USER_KEY, userKey);
        valueUpdate.put(EDUCATION_KEY, eduKey);
        valueUpdate.put(YEAR_START, yearStart);
        valueUpdate.put(YEAR_FINISH, yearEnd);
        valueUpdate.put(DEGREE, degree);
        valueUpdate.put(FIELD_OF_STUDY, major);
        valueUpdate.put(GRADE, "false");
        valueUpdate.put(ACTIVITIES, "false");
        valueUpdate.put(DESCRIPTION, "false");
        valueUpdate.put(DELETE_FLAG, "0");
        valueUpdate.put(CREATED_BY, userKey);
        valueUpdate.put(CREATED_TIMESTAMP, getDateTime());
        valueUpdate.put(MODIFIED_BY, userKey);
        valueUpdate.put(MODIFIED_TIMESTAMP, getDateTime());

        SQLiteDatabase db = this.getWritableDatabase();
        db.insert(TABLE_NAME_USER_EDUCATION, null, valueUpdate);
        db.close();
        return true;

    }

    public long getTagKey(String title) {

        long key = -1;
        // array of columns to fetch
        String[] columns = {"TagKey"};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = "TagName" + " = ?";

        // selection arguments
        String[] selectionArgs = {title};

        // query user table with conditions]
        Cursor cursor = db.query(TABLE_NAME_TAG_MASTER, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                       //filter by row groups
                null);                      //The sort order

        //int cursorCount = cursor.getCount();
        if (cursor != null) {
            cursor.moveToFirst();
            key = cursor.getLong(cursor.getColumnIndex(TAG_KEY));
            updateContact(key);
            cursor.close();
        }
        db.close();
        if (key > 0) {
            return key;
        }
        return 0;
    }

    public void updateProfilePicture(byte[] inputData, String key) {
        SQLiteDatabase dbUpdate = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PROFILE_PIC, inputData);
        values.put(MODIFIED_TIMESTAMP, getDateTime());
        values.put(MODIFIED_BY, key);
        dbUpdate.update(TABLE_NAME_USER_PROFILE, values, "UserKey" + " = ?",
                new String[]{key});
        dbUpdate.close();
    }

    public ArrayList<Contact> getContact() {

        ArrayList<Contact> contactArrayList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact();
                contact.setId(cursor.getString(0));
                contact.setName(cursor.getString(5));
                contact.setJob(cursor.getString(6));
                Log.e("tag", "id " + cursor.getString(0));
                Log.e("tag", "name " + cursor.getString(5));
                Log.e("tag", "job " + cursor.getString(6));
                if (cursor.getBlob(7).length > 0) {
                    contact.setImage(cursor.getBlob(7));
                    //          Log.e("tag", "image " + Arrays.toString(cursor.getBlob(7)));
                } else
                    contact.setImage(null);

                String innerSelectQuery = "SELECT * FROM " + TABLE_NAME_USER_EDUCATION + " WHERE UserKey = '" + cursor.getString(0) + "'";

                Cursor innerCursor = db.rawQuery(innerSelectQuery, null);
                String eduKey;
                // looping through all rows and adding to list
                if (innerCursor.moveToFirst() && innerCursor.getCount() > 0) {

                    eduKey = String.valueOf(innerCursor.getInt(0));
                    Log.e("tag", "du key " + eduKey);

                    String innerRSelectQuery = "SELECT * FROM " + TABLE_NAME_EDUCATION_MASTER + " WHERE EducationKey = '" + eduKey + "'";

                    Cursor innerRCursor = db.rawQuery(innerRSelectQuery, null);
                    if (innerRCursor.moveToFirst() && innerRCursor.getCount() > 0) {
                        contact.setColg(innerRCursor.getString(1));

                        Log.e("tag", "edu school " + innerRCursor.getString(1));
                    } else {

                        contact.setColg("");
                    }
                    innerCursor.close();
                    innerRCursor.close();

                } else {
                    innerCursor.close();
                    contact.setColg("");
                }
                // Adding  to list
                contactArrayList.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return contactArrayList;

    }


    public Contact getUserContact(String key) {

        Contact contact = new Contact();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE + " WHERE UserKey = '" + key + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                contact.setId(cursor.getString(0));
                contact.setName(cursor.getString(5));
                contact.setJob(cursor.getString(6));
                Log.e("tag", "id " + cursor.getString(0));
                Log.e("tag", "name " + cursor.getString(5));
                Log.e("tag", "job " + cursor.getString(6));
                if (cursor.getBlob(7).length > 0) {
                    contact.setImage(cursor.getBlob(7));
                    //      Log.e("tag", "image " + Arrays.toString(cursor.getBlob(7)));
                } else
                    contact.setImage(null);

                String innerSelectQuery = "SELECT * FROM " + TABLE_NAME_USER_EDUCATION + " WHERE UserKey = '" + cursor.getInt(0) + "'";

                Cursor innerCursor = db.rawQuery(innerSelectQuery, null);
                String eduKey;
                // looping through all rows and adding to list
                if (innerCursor.moveToFirst() && innerCursor.getCount() > 0) {

                    innerCursor.moveToLast();
                    eduKey = String.valueOf(innerCursor.getInt(0));
                    Log.e("tag", "du key " + eduKey);

                    String innerRSelectQuery = "SELECT * FROM " + TABLE_NAME_EDUCATION_MASTER + " WHERE EducationKey = '" + eduKey + "'";

                    Cursor innerRCursor = db.rawQuery(innerRSelectQuery, null);
                    if (innerRCursor.moveToFirst() && innerRCursor.getCount() > 0) {
                        contact.setColg(innerRCursor.getString(1));

                        Log.e("tag", "edu school " + innerRCursor.getString(1));
                    } else {

                        contact.setColg("");
                    }
                    innerCursor.close();
                    innerRCursor.close();

                } else {
                    innerCursor.close();
                    contact.setColg("");
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return contact;
    }

    public String setEventLocation(String key, String locateLatitude, String locateLongitude) {
        SQLiteDatabase db = this.getWritableDatabase();

        long locationKey = checkLocationTable(locateLatitude, locateLongitude, db);
        if (locationKey != 0) {
            db.close();
            Log.e("tag", "lockey" + locationKey);
            return String.valueOf(locationKey);
        } else {
            ContentValues values = new ContentValues();
            values.put(TYPE, FUN);
            values.put(LATITUDE, locateLatitude);
            values.put(LONGITUDE, locateLongitude);
            values.put(DELETE_FLAG, "0");
            values.put(CREATED_BY, "" + key);
            values.put(CREATED_TIMESTAMP, getDateTime());
            values.put(MODIFIED_TIMESTAMP, getDateTime());
            values.put(MODIFIED_BY, key);
            // Inserting Row
            long locationKeyInsert = db.insert(TABLE_NAME_LOCATION, null, values);
            Log.e("tag", "lockeyyyy" + locationKeyInsert);
            db.close();
            return String.valueOf(locationKeyInsert);
        }
    }

    public String getEventTypeKey(String eventTypeSelected) {
        String key = null;

        SQLiteDatabase db = this.getWritableDatabase();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_EVENT_TYPE_MASTER + " WHERE Name" + " = '" + eventTypeSelected + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                key = String.valueOf(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        if (key != null) {
            cursor.close();
            return key;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public String getTagUserKey(String tagSelected) {

        String key = null;

        SQLiteDatabase db = this.getWritableDatabase();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_TAG_MASTER + " WHERE TagName" + " = '" + tagSelected + "'";

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            do {
                key = String.valueOf(cursor.getInt(0));
            } while (cursor.moveToNext());
        }
        if (key != null) {
            cursor.close();
            return key;
        }
        if (cursor != null) {
            cursor.close();
        }
        return null;
    }

    public ArrayList<Skill> getUserTags(String userKey) {

        ArrayList<Skill> skillsList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_TAG + " WHERE UserKey = '" + userKey + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                int tagKey = cursor.getInt(1);
                String selectInnerQuery = "SELECT * FROM " + TABLE_NAME_TAG_MASTER + " WHERE TagKey = '" + tagKey + "'";

                Cursor InnerCursor = db.rawQuery(selectInnerQuery, null);
                if (InnerCursor != null && InnerCursor.moveToFirst()) {

                    Skill skill = new Skill();
                    skill.setTitle(InnerCursor.getString(1));
                    skill.setSkillId(tagKey);
                    // Adding contact to list
                    skillsList.add(skill);
                    InnerCursor.close();
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return skill list
        return skillsList;

    }

    public String getLocationOfUser(String userKey, Context context) {
        String address = null;
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE + " WHERE UserKey = '" + userKey + "'";
        Log.e("tag", selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                String selectInnerQuery = "SELECT * FROM " + TABLE_NAME_LOCATION + " WHERE LocationKey = '" + cursor.getInt(13) + "'";
                Log.e("tag", selectInnerQuery);

                Cursor InnerCursor = db.rawQuery(selectInnerQuery, null);
                if (InnerCursor != null && InnerCursor.moveToFirst()) {
                    Location location = new Location("");
                    location.setLatitude(InnerCursor.getInt(2));
                    location.setLongitude(InnerCursor.getInt(3));

                    address = Utility.getAddress(context, location);
                    Log.e("tag", address);
                    InnerCursor.close();
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return address;
    }


    public ArrayList<Skill> getUserMentorSkill(String id) {

        ArrayList<Skill> skillsList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_TAG + " WHERE UserKey = '" + id + "' AND MentorFlag = '1' AND MenteeFlag = '0'";
        Log.e("tag", "" + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                int tagKey = cursor.getInt(1);
                String selectInnerQuery = "SELECT * FROM " + TABLE_NAME_TAG_MASTER + " WHERE TagKey = '" + tagKey + "'";
                Log.e("tag", "" + tagKey);

                Cursor InnerCursor = db.rawQuery(selectInnerQuery, null);
                if (InnerCursor != null && InnerCursor.moveToFirst()) {

                    Skill skill = new Skill();
                    skill.setTitle(InnerCursor.getString(1));
                    Log.e("tag", InnerCursor.getString(1));
                    // Adding contact to list
                    skillsList.add(skill);
                    InnerCursor.close();
                }
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return skillsList;

    }

    public ArrayList<Skill> getUserMenteeSkill(String id) {

        ArrayList<Skill> skillsList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_TAG + " WHERE UserKey = '" + id + "' AND MentorFlag = '0' AND MenteeFlag = '1'";
        Log.e("tag", "" + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                int tagKey = cursor.getInt(1);
                String selectInnerQuery = "SELECT * FROM " + TABLE_NAME_TAG_MASTER + " WHERE TagKey = '" + tagKey + "'";
                Log.e("tag", "" + tagKey);

                Cursor InnerCursor = db.rawQuery(selectInnerQuery, null);
                if (InnerCursor != null && InnerCursor.moveToFirst()) {

                    Skill skill = new Skill();
                    Log.e("tag", InnerCursor.getString(1));
                    skill.setTitle(InnerCursor.getString(1));
                    // Adding contact to list
                    skillsList.add(skill);
                    InnerCursor.close();
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return skillsList;
    }

    public ArrayList<Skill> getUserOtherSkill(String id) {

        ArrayList<Skill> skillsList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_TAG + " WHERE UserKey = '" + id + "' AND MentorFlag = '0' AND MenteeFlag = '0'";
        Log.e("tag", "" + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                int tagKey = cursor.getInt(1);
                String selectInnerQuery = "SELECT * FROM " + TABLE_NAME_TAG_MASTER + " WHERE TagKey = '" + tagKey + "'";
                Log.e("tag", "" + tagKey);

                Cursor InnerCursor = db.rawQuery(selectInnerQuery, null);
                if (InnerCursor != null && InnerCursor.moveToFirst()) {

                    Skill skill = new Skill();
                    Log.e("tag", InnerCursor.getString(1));
                    skill.setTitle(InnerCursor.getString(1));
                    // Adding contact to list
                    skillsList.add(skill);
                    InnerCursor.close();
                }

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return skillsList;
    }

    public String getUserEmail(String userKey) {

        String email;
        // array of columns to fetch
        String[] columns = {"EmailID"};

        SQLiteDatabase db = this.getReadableDatabase();
        // selection criteria
        String selection = "UserKey" + " = ?";
        // selection argument
        String[] selectionArgs = {userKey};

        Cursor cursor = db.query(TABLE_NAME_USER_PROFILE, //Table to query
                columns,                    //columns to return
                selection,                  //columns for the WHERE clause
                selectionArgs,              //The values for the WHERE clause
                null,                       //group the rows
                null,                      //filter by row groups
                null);                      //The sort order
        cursor.moveToFirst();
        email = cursor.getString(cursor.getColumnIndex(EMAIL_ID));

        cursor.close();
        db.close();
        return email;
    }

    public ArrayList<ProfileAlumni> getAluminiDetails(String userKey) {

        ArrayList<ProfileAlumni> profileList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_EDUCATION + " WHERE UserKey = '" + userKey + "'";
        Log.e("tag", "" + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                ProfileAlumni profileAlumni = new ProfileAlumni();
                profileAlumni.setAlumniStudy(cursor.getString(4) + " , " + cursor.getString(5));
                profileAlumni.setAlumniYear(cursor.getString(2) + " - " + cursor.getString(3));

                int eduKey = cursor.getInt(0);
                String selectInnerQuery = "SELECT * FROM " + TABLE_NAME_EDUCATION_MASTER + " WHERE EducationKey = '" + eduKey + "'";
                Log.e("tag", "" + eduKey);

                Cursor InnerCursor = db.rawQuery(selectInnerQuery, null);
                if (InnerCursor != null && InnerCursor.moveToFirst()) {

                    profileAlumni.setAlumniUniversity(InnerCursor.getString(1));

                    InnerCursor.close();
                }
                profileList.add(profileAlumni);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return profileList;

    }

    public ArrayList<Network> getNetworkContacts() {

        ArrayList<Network> profileList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Network network = new Network();
                network.setDrawable(Utility.getPhoto(cursor.getBlob(7)));
                network.setId(cursor.getInt(0));

                int eduKey = cursor.getInt(0);
                String selectInnerQuery = "SELECT * FROM " + TABLE_NAME_USER_TAG + " WHERE UserKey = '" + cursor.getInt(0) + "'";
                Log.e("tag", "tag" + eduKey);

                Cursor InnerCursor = db.rawQuery(selectInnerQuery, null);
                if (InnerCursor != null && InnerCursor.moveToFirst()) {
                    do {

                        network.setNetworkTags(new NetworkTag(cursor.getInt(1), cursor.getInt(3), cursor.getInt(2)));

                    } while (InnerCursor.moveToNext());
                    InnerCursor.close();
                }

                profileList.add(network);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return profileList;
    }

    public Bitmap getUserImage(String id) {
        byte[] name = new byte[0];

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE + " WHERE UserKey = '" + id + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            name = cursor.getBlob(7);
        }

        cursor.close();
        db.close();

        return Utility.getPhoto(name);

    }


    public String getName(String userKey) {

        String name = null;
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE + " WHERE UserKey = '" + userKey + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            name = cursor.getString(5);
        }

        cursor.close();
        db.close();
        return name;
    }

    public String getCompanyName(String userKey) {

        String name = null;
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE + " WHERE UserKey = '" + userKey + "'";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            name = cursor.getString(6);
        }

        cursor.close();
        db.close();
        return name;
    }


    public ArrayList<Skill> getTags() {
        ArrayList<Skill> skillsList = new ArrayList<>();

        int[] resImg = {R.drawable.app_logo, R.drawable.ic_call_black_24dp, R.drawable.user_logo};
        Random rand = new Random();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_TAG_MASTER;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Skill skill = new Skill();
                skill.setIcon(resImg[rand.nextInt(resImg.length)]);
                skill.setTitle(cursor.getString(1));
                skill.setSkillId(cursor.getInt(0));
                // Adding contact to list
                skillsList.add(skill);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        // return contact list
        return skillsList;
    }

    public ArrayList<Long> getUserKeyFromTag(ArrayList<Long> skillFilter) {

        ArrayList<Long> userKeyList = new ArrayList<>();

        if (skillFilter.size() == 0)
            return null;

        // Select All Query
        String selectQuery = "SELECT DISTINCT * FROM " + TABLE_NAME_USER_TAG + " WHERE TagKey IN ( '" + skillFilter.get(0) + "'";

        for (int i = 1; i < skillFilter.size(); i++) {
            if (i != skillFilter.size() - 1) {
                selectQuery = selectQuery.concat(", '" + skillFilter.get(i) + "'");
            } else {
                selectQuery = selectQuery.concat(", '" + skillFilter.get(i) + "') GROUP BY UserKey");
            }
        }
        if (skillFilter.size() == 1)
            selectQuery = selectQuery.concat(") GROUP BY UserKey");

        Log.e("tag", "id " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            Log.e("getUserKeyFromTag", DatabaseUtils.dumpCursorToString(cursor));
            do {
                userKeyList.add((long) cursor.getInt(0));
                Log.e("tag", "cursor.getInt " + cursor.getInt(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return userKeyList;
    }

    public ArrayList<Contact> getContact(ArrayList<Long> userKey) {

        ArrayList<Contact> contactArrayList = new ArrayList<>();
        for (int i = 0; i < userKey.size(); i++) {
            // Select All Query
            String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE + " WHERE UserKey = '" + userKey.get(i) + "'";

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {

                    Contact contact = new Contact();

                    contact.setId(cursor.getString(0));
                    contact.setName(cursor.getString(5));
                    contact.setJob(cursor.getString(6));
                    Log.e("tag", "id " + cursor.getString(0));
                    Log.e("tag", "name " + cursor.getString(5));
                    Log.e("tag", "job " + cursor.getString(6));
                    if (cursor.getBlob(7).length > 0) {
                        contact.setImage(cursor.getBlob(7));
                        //      Log.e("tag", "image " + Arrays.toString(cursor.getBlob(7)));
                    } else
                        contact.setImage(null);

                    String innerSelectQuery = "SELECT * FROM " + TABLE_NAME_USER_EDUCATION + " WHERE UserKey = '" + cursor.getInt(0) + "'";

                    Cursor innerCursor = db.rawQuery(innerSelectQuery, null);
                    String eduKey;
                    // looping through all rows and adding to list
                    if (innerCursor.moveToFirst() && innerCursor.getCount() > 0) {

                        innerCursor.moveToLast();
                        eduKey = String.valueOf(innerCursor.getInt(0));
                        Log.e("tag", "du key " + eduKey);

                        String innerRSelectQuery = "SELECT * FROM " + TABLE_NAME_EDUCATION_MASTER + " WHERE EducationKey = '" + eduKey + "'";

                        Cursor innerRCursor = db.rawQuery(innerRSelectQuery, null);
                        if (innerRCursor.moveToFirst() && innerRCursor.getCount() > 0) {
                            contact.setColg(innerRCursor.getString(1));

                            Log.e("tag", "edu school " + innerRCursor.getString(1));
                        } else {

                            contact.setColg("");
                        }
                        innerCursor.close();
                        innerRCursor.close();

                    } else {
                        innerCursor.close();
                        contact.setColg("");
                    }
                    contactArrayList.add(contact);
                } while (cursor.moveToNext());
            }

            cursor.close();
            db.close();

            // array of columns to fetch
        }
        return contactArrayList;

    }

    public ArrayList<USER_EVENT> getMyEvents(String userKey) {

        ArrayList<USER_EVENT> userEventArrayList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_EVENT + " WHERE UserKey = '" + userKey +
                "' OR EventKey IN(SELECT EventKey FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE UserKey = '" + userKey +
                "' AND ParticipantStatus NOT IN ( '2', '3' ) ) GROUP BY EventStartTs";
        Log.e("tag", selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            Log.e("getMyecevts", DatabaseUtils.dumpCursorToString(cursor));

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {

                    USER_EVENT userEvent = new USER_EVENT();

                    userEvent.setUserKey(cursor.getString(16));
                    userEvent.setEventKey(cursor.getString(0));
                    userEvent.setEventStartTs(cursor.getString(5));
                    userEvent.setEventEndTs(cursor.getString(6));
                    userEvent.setEventTitle(cursor.getString(15));
                    userEvent.setAddress(cursor.getString(7));
                    userEvent.setLocationKey(String.valueOf(cursor.getInt(3)));
                    userEvent.setImageKey(cursor.getBlob(9));
                    userEvent.setOpen(cursor.getInt(8) >= 1);
                    userEvent.setDescription(cursor.getString(4));

                    Log.e("tag", "id " + cursor.getString(1));
                    Log.e("tag", "name " + cursor.getString(2));
                    Log.e("tag", "job " + cursor.getString(3));
                    Log.e("tag", "id " + cursor.getString(4));
                    Log.e("tag", "name " + cursor.getString(5));
                    Log.e("tag", "job " + cursor.getString(6));
                    Log.e("tag", "id " + cursor.getString(7));
                    Log.e("tag", "name " + cursor.getString(8));
                    // Log.e("tag", "job " + cursor.getString(9));
//                    Log.e("tag", "id " + Arrays.toString(cursor.getBlob(10)));
                    Log.e("tag", "name " + cursor.getString(11));
                    Log.e("tag", "job " + cursor.getString(12));

                    String innerSelectQuery = "SELECT * FROM " + TABLE_NAME_EVENT_TYPE_MASTER + " WHERE EventTypeKey = '" + cursor.getInt(1) + "'";

                    Cursor innerCursor = db.rawQuery(innerSelectQuery, null);
                    // looping through all rows and adding to list
                    if (innerCursor.moveToFirst() && innerCursor.getCount() > 0) {

                        userEvent.setEventTypeKey(innerCursor.getString(1));
                        Log.e("tag", "EventTypeKey " + innerCursor.getString(1));

                    } else {
                        userEvent.setEventTypeKey("");
                    }
                    innerCursor.close();
                    String innerSelectQuery2 = "SELECT * FROM " + TABLE_NAME_TAG_MASTER + " WHERE TagKey = '" + cursor.getInt(2) + "'";

                    Cursor innerCursor2 = db.rawQuery(innerSelectQuery2, null);
                    // looping through all rows and adding to list
                    if (innerCursor2.moveToFirst() && innerCursor2.getCount() > 0) {

                        userEvent.setTagKey(innerCursor2.getString(1));
                        Log.e("tag", "job " + innerCursor2.getString(1));

                    } else {
                        userEvent.setTagKey("");
                    }
                    innerCursor2.close();


                    Date eventStartDate = null, currentDate = null;
                    try {
                        eventStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(5));
                        currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utility.getDateTime());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (eventStartDate != null && eventStartDate.after(currentDate))
                        userEventArrayList.add(userEvent);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();

        return userEventArrayList;
    }


    public ArrayList<USER_EVENT> getMyUpCommingEvents(String userKey, ArrayList<Long> eventKeyArrayLists) {

        SQLiteDatabase db = this.getWritableDatabase();

        ArrayList<USER_EVENT> userEventArrayList = new ArrayList<>();

        ArrayList<Long> eventKeyArrayList = new ArrayList<>();

        ArrayList<Long> stringArrayList = new ArrayList<>();

        String sel = "SELECT DISTINCT * FROM " + TABLE_QUERY_USER_PARTICIPANTS +
                " WHERE UserKey = '" + userKey + "' AND ParticipantStatus = '3' GROUP BY EventKey";

        Cursor cur = db.rawQuery(sel, null);
        if (cur != null) {
            Log.e("Duplicate", DatabaseUtils.dumpCursorToString(cur));

            if (cur.moveToFirst()) {
                do {
                    stringArrayList.add((long) cur.getInt(1));
                } while (cur.moveToNext());
            } else
                stringArrayList = new ArrayList<>();

            cur.close();
        }
        if (!eventKeyArrayLists.isEmpty()) {
            for (Long userEvent : eventKeyArrayLists) {
                if (!stringArrayList.contains(userEvent)) {
                    eventKeyArrayList.add(userEvent);
                    Log.e("Duplicate", "" + userEvent);
                }
            }
        }

        if (eventKeyArrayList.size() == 0) {
            return new ArrayList<>();
        }
        // Select All Query
        String selectQuery = "SELECT DISTINCT * FROM " + TABLE_NAME_USER_EVENT + " WHERE isOpen" + " = '1' AND " +
                " EventKey IN ('" + eventKeyArrayList.get(0) + "'";

        for (int i = 1; i < eventKeyArrayList.size(); i++) {
            if (i != eventKeyArrayList.size() - 1) {
                selectQuery = selectQuery.concat(", '" + eventKeyArrayList.get(i) + "'");
            } else {
                selectQuery = selectQuery.concat(", '" + eventKeyArrayList.get(i) + "') GROUP BY EventEndTs");
            }
        }

        if (eventKeyArrayList.size() == 1)
            selectQuery = selectQuery.concat(") GROUP BY EventEndTs");

        Log.e("tag", "id " + selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            Log.e("getMyUpCommingEvents", DatabaseUtils.dumpCursorToString(cursor));

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {

                    USER_EVENT userEvent = new USER_EVENT();

                    userEvent.setUserKey(cursor.getString(16));
                    userEvent.setEventKey(cursor.getString(0));
                    userEvent.setEventStartTs(cursor.getString(5));
                    userEvent.setEventEndTs(cursor.getString(6));
                    userEvent.setEventTitle(cursor.getString(15));
                    userEvent.setAddress(cursor.getString(7));
                    userEvent.setLocationKey(String.valueOf(cursor.getInt(3)));
                    userEvent.setImageKey(cursor.getBlob(9));
                    userEvent.setOpen(cursor.getInt(8) >= 1);
                    userEvent.setDescription(cursor.getString(4));

                    Log.e("tag", "id " + cursor.getString(1));
                    Log.e("tag", "name " + cursor.getString(2));
                    Log.e("tag", "job " + cursor.getString(3));
                    Log.e("tag", "id " + cursor.getString(4));
                    Log.e("tag", "name " + cursor.getString(5));
                    Log.e("tag", "job " + cursor.getString(6));
                    Log.e("tag", "id " + cursor.getString(7));
                    Log.e("tag", "name " + cursor.getString(8));
                    // Log.e("tag", "job " + cursor.getString(9));
//                    Log.e("tag", "id " + Arrays.toString(cursor.getBlob(10)));
                    Log.e("tag", "name " + cursor.getString(11));
                    Log.e("tag", "job " + cursor.getString(12));

                    String innerSelectQuery = "SELECT * FROM " + TABLE_NAME_EVENT_TYPE_MASTER + " WHERE EventTypeKey = '" + cursor.getInt(1) + "'";

                    Cursor innerCursor = db.rawQuery(innerSelectQuery, null);
                    // looping through all rows and adding to list
                    if (innerCursor.moveToFirst() && innerCursor.getCount() > 0) {

                        userEvent.setEventTypeKey(innerCursor.getString(1));
                        Log.e("tag", "EventTypeKey " + innerCursor.getString(1));
                        innerCursor.close();

                    } else {
                        userEvent.setEventTypeKey("");
                        innerCursor.close();
                    }

                    innerCursor.close();
                    String innerSelectQuery2 = "SELECT * FROM " + TABLE_NAME_TAG_MASTER + " WHERE TagKey = '" + cursor.getInt(2) + "'";

                    Cursor innerCursor2 = db.rawQuery(innerSelectQuery2, null);
                    // looping through all rows and adding to list
                    if (innerCursor2.moveToFirst() && innerCursor2.getCount() > 0) {

                        userEvent.setTagKey(innerCursor2.getString(1));
                        Log.e("tag", "job " + innerCursor2.getString(1));
                        innerCursor2.close();

                    } else {
                        userEvent.setTagKey("");
                        innerCursor2.close();
                    }

                    innerCursor2.close();

                    Date eventStartDate = null, currentDate = null;
                    try {
                        eventStartDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(5));
                        currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utility.getDateTime());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (eventStartDate != null && eventStartDate.after(currentDate))
                        userEventArrayList.add(userEvent);

                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();

        return userEventArrayList;

    }

    public ArrayList<Network> getNetworkContacts(ArrayList<Long> userKey) {

        ArrayList<Network> profileList = new ArrayList<>();

        for (int i = 0; i < userKey.size(); i++) {

            // Select All Query
            String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE + " WHERE UserKey = '" + userKey.get(i) + "'";
            Log.e("tag", "selectQuery" + selectQuery);

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Network network = new Network();
                    network.setDrawable(Utility.getPhoto(cursor.getBlob(7)));
                    network.setId(cursor.getInt(0));

                    int eduKey = cursor.getInt(0);
                    String selectInnerQuery = "SELECT * FROM " + TABLE_NAME_USER_TAG + " WHERE UserKey = '" + cursor.getInt(0) + "'";

                    Cursor InnerCursor = db.rawQuery(selectInnerQuery, null);
                    Log.e("tag", "selectInnerQuery" + selectInnerQuery);
                    network.setMentor(0);
                    network.setMentee(0);

                    if (InnerCursor != null && InnerCursor.moveToFirst()) {
                        do {

                            Log.e("tag", "cursor.getInt(2)" + InnerCursor.getInt(2));
                            Log.e("tag", "cursor.getInt(3)" + InnerCursor.getInt(3));
                            if (InnerCursor.getInt(2) == 1)
                                network.setMentor(1);
                            if (InnerCursor.getInt(3) == 1)
                                network.setMentee(1);

                            network.setNetworkTags(new NetworkTag(cursor.getInt(1), cursor.getInt(3), cursor.getInt(2)));

                        } while (InnerCursor.moveToNext());
                        InnerCursor.close();
                    }

                    profileList.add(network);

                } while (cursor.moveToNext());
            }

            cursor.close();

            db.close();
            // return contact list
        }

        return profileList;

    }

    public USER_EVENT getEventDetails(String id) {

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_EVENT + " WHERE EventKey = '" + id + "'";
        Log.e("tag", "selectQuery" + selectQuery);

        USER_EVENT userEvent = new USER_EVENT();

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            Log.e("getEventDetails", DatabaseUtils.dumpCursorToString(cursor));
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {

                userEvent.setUserKey(cursor.getString(16));
                userEvent.setEventKey(cursor.getString(0));
                userEvent.setEventStartTs(cursor.getString(5));
                userEvent.setEventEndTs(cursor.getString(6));
                userEvent.setEventTitle(cursor.getString(15));
                userEvent.setAddress(cursor.getString(7));
                userEvent.setLocationKey(String.valueOf(cursor.getInt(3)));
                userEvent.setImageKey(cursor.getBlob(9));
                userEvent.setOpen(cursor.getInt(8) >= 1);
                userEvent.setDescription(cursor.getString(4));
                userEvent.setCreatedBy(cursor.getString(11));

                Log.e("tag", "id " + cursor.getString(1));
                Log.e("tag", "name " + cursor.getString(2));
                Log.e("tag", "job " + cursor.getString(3));
                Log.e("tag", "id " + cursor.getString(4));
                Log.e("tag", "name " + cursor.getString(5));
                Log.e("tag", "job " + cursor.getString(6));
                Log.e("tag", "id " + cursor.getString(7));
                Log.e("tag", "name " + cursor.getString(8));
//            Log.e("tag", "job " + cursor.getString(9));
//                Log.e("tag", "id " + Arrays.toString(cursor.getBlob(10)));
                Log.e("tag", "name " + cursor.getString(11));
                Log.e("tag", "job " + cursor.getString(12));

                String innerSelectQuery = "SELECT * FROM " + TABLE_NAME_EVENT_TYPE_MASTER + " WHERE EventTypeKey = '" + cursor.getInt(1) + "'";

                Cursor innerCursor = db.rawQuery(innerSelectQuery, null);
                // looping through all rows and adding to list
                if (innerCursor.moveToFirst() && innerCursor.getCount() > 0) {

                    userEvent.setEventTypeKey(innerCursor.getString(1));
                    Log.e("tag", "EventTypeKey " + innerCursor.getString(1));

                } else {
                    userEvent.setEventTypeKey("");
                }

                innerCursor.close();
                String innerSelectQuery2 = "SELECT * FROM " + TABLE_NAME_TAG_MASTER + " WHERE TagKey = '" + cursor.getInt(2) + "'";

                Cursor innerCursor2 = db.rawQuery(innerSelectQuery2, null);
                // looping through all rows and adding to list
                if (innerCursor2.moveToFirst() && innerCursor2.getCount() > 0) {

                    userEvent.setTagKey(innerCursor2.getString(1));
                    Log.e("tag", "job " + innerCursor2.getString(1));

                } else {
                    userEvent.setTagKey("");
                }

                innerCursor2.close();

            }
            cursor.close();
        }
        db.close();

        return userEvent;
    }

    public void updateEventParticipants(long eventId, String userKey, List<String> otherUserKey, int checkMenteeMentorBoolean) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues eventValues = new ContentValues();

        for (int i = 0; i < otherUserKey.size(); i++) {

            int mentor = 0;
            int mentee = 0;
            int ParticipantStatus = 3;

            eventValues.put(USER_KEY, otherUserKey.get(i));
            eventValues.put(EVENT_KEY, eventId);

            if (Objects.equals(userKey, otherUserKey.get(i))) {
                ParticipantStatus = 1;
                if (checkMenteeMentorBoolean == 1)
                    mentee = 1;
                else if (checkMenteeMentorBoolean == 2)
                    mentor = 1;
            }

            eventValues.put(MENTOR_FLAG, mentor);
            eventValues.put(MENTEE_FLAG, mentee);
            eventValues.put(PARTICIPANT_STATUS, ParticipantStatus);

            db.insert(TABLE_QUERY_USER_PARTICIPANTS, null, eventValues);
        }
        db.close();


    }

    public ArrayList<MyMentor> getMyMentors(String userKey) {

        ArrayList<MyMentor> myMentorsList = new ArrayList<>();
        ArrayList<Skill> skillOtherArrayList = new ArrayList<>();
        String otherSkill = null;

        SQLiteDatabase db = this.getWritableDatabase();
        // Select All Query
        String selectQuery = "SELECT DISTINCT * FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE MentorFlag" + " = '1' AND" +
                " EventKey IN (SELECT EventKey FROM " +
                TABLE_QUERY_USER_PARTICIPANTS + " WHERE UserKey = '" + userKey + "') GROUP BY UserKey";
        Log.e("tag", selectQuery);
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {

            Log.e("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));

            do {
                if (!userKey.equals(String.valueOf(cursor.getString(0)))) {

                    MyMentor myMentor = new MyMentor();
                    myMentor.setMentorID(String.valueOf(cursor.getString(0)));
                    myMentor.setMentorName(getName(String.valueOf(cursor.getString(0))));
                    myMentor.setMentorDescription(getCompanyName(String.valueOf(cursor.getString(0))));
                    myMentor.setMentorImage(getUserImage(String.valueOf(cursor.getString(0))));

                    Log.e("Cursor Object", String.valueOf(cursor.getString(0)));
                    Log.e("Cursor Object", getName(String.valueOf(cursor.getString(0))));
                    Log.e("Cursor Object", getCompanyName(String.valueOf(cursor.getString(0))));
                    Log.e("Cursor Object", String.valueOf(getUserImage(String.valueOf(cursor.getString(0)))));


                    skillOtherArrayList = getUserOtherSkill(String.valueOf(cursor.getString(0)));
                    for (int j = 0; j < skillOtherArrayList.size(); j++) {
                        Skill skill = skillOtherArrayList.get(j);
                        if (j == 0)
                            otherSkill = skill.getTitle();
                        else if (j == skillOtherArrayList.size() - 1)
                            otherSkill += "\t|\t" + skill.getTitle() + "\t";
                        else
                            otherSkill += "\t|\t" + skill.getTitle();
                    }
                    myMentor.setOtherMentoring(otherSkill);
                    Log.e("Cursor Object", otherSkill);

                    myMentorsList.add(myMentor);

                }
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

        }

        return myMentorsList;
    }

    public ArrayList<MyMentor> getMyMentors(ArrayList<Long> userKey, String userKey1) {

        ArrayList<MyMentor> myMentorsList = new ArrayList<>();

        ArrayList<Skill> skillOtherArrayList;
        String otherSkill = null;

        if (userKey.size() == 0) {
            return new ArrayList<>();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        // Select All Query
        String selectQuery = "SELECT DISTINCT * FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE MentorFlag" + " = '1'" +
                "AND EventKey IN ( '" + userKey.get(0) + "'";

        for (int i = 1; i < userKey.size(); i++) {
            if (i != userKey.size() - 1) {
                selectQuery = selectQuery.concat(", '" + userKey.get(i) + "'");
            } else {
                selectQuery = selectQuery.concat(", '" + userKey.get(i) + "') GROUP BY UserKey");
            }
        }

        if (userKey.size() == 1)
            selectQuery = "SELECT DISTINCT * FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE MentorFlag" + " = '1'" +
                    "AND  EventKey IN ('" + userKey.get(0) + "') GROUP BY UserKey";

        Log.e("tag", "id " + selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            Log.e("getMyMentors", DatabaseUtils.dumpCursorToString(cursor));

            if (cursor.moveToFirst()) {

                do {
                    if (!String.valueOf(userKey1).equals(String.valueOf(cursor.getString(0)))) {

                        MyMentor myMentor = new MyMentor();
                        myMentor.setMentorID(String.valueOf(cursor.getString(0)));
                        myMentor.setMentorName(getName(String.valueOf(cursor.getString(0))));
                        myMentor.setMentorDescription(getCompanyName(String.valueOf(cursor.getString(0))));
                        myMentor.setMentorImage(getUserImage(String.valueOf(cursor.getString(0))));

                        Log.e("Cursor Object", String.valueOf(cursor.getString(0)));
                        Log.e("Cursor Object", getName(String.valueOf(cursor.getString(0))));
                        Log.e("Cursor Object", getCompanyName(String.valueOf(cursor.getString(0))));
                        Log.e("Cursor Object", String.valueOf(getUserImage(String.valueOf(cursor.getString(0)))));


                        skillOtherArrayList = getUserOtherSkill(String.valueOf(cursor.getString(0)));
                        for (int j = 0; j < skillOtherArrayList.size(); j++) {
                            Skill skill = skillOtherArrayList.get(j);
                            if (j == 0)
                                otherSkill = skill.getTitle();
                            else if (j == skillOtherArrayList.size() - 1)
                                otherSkill += "\t|\t" + skill.getTitle() + "\t";
                            else
                                otherSkill += "\t|\t" + skill.getTitle();
                        }
                        myMentor.setOtherMentoring(otherSkill);
//                    Log.e("Cursor Object", otherSkill);

                        myMentorsList.add(myMentor);

                    }
                } while (cursor.moveToNext());

                cursor.close();
                db.close();
            }
        }

        return myMentorsList;
    }

    public ArrayList<Long> getEventKeyFromEvent(ArrayList<Long> skillFilter) {

        ArrayList<Long> eventKeyList = new ArrayList<>();

        if (skillFilter.size() == 0)
            return new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT DISTINCT * FROM " + TABLE_NAME_USER_EVENT + " WHERE TagKey IN ( '" + skillFilter.get(0) + "'";

        for (int i = 1; i < skillFilter.size(); i++) {
            if (i != skillFilter.size() - 1) {
                selectQuery = selectQuery.concat(", '" + skillFilter.get(i) + "'");
            } else {
                selectQuery = selectQuery.concat(", '" + skillFilter.get(i) + "') AND EventKey IN (SELECT EventKey FROM " +
                        TABLE_QUERY_USER_PARTICIPANTS + " WHERE UserKey  = '" + Prefs.getUserKey() + "')");
            }
        }
        if (skillFilter.size() == 1)
            selectQuery = "SELECT DISTINCT * FROM " + TABLE_NAME_USER_EVENT + " WHERE TagKey = '" + skillFilter.get(0) +
                    "' AND EventKey IN (SELECT EventKey FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE UserKey  = '" + Prefs.getUserKey() + "') GROUP BY EventKey";

        Log.e("tag", "id " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.getCount() == 0) {
            Log.e("getEventKeyFromEvent", DatabaseUtils.dumpCursorToString(cursor));
            cursor.close();
            db.close();
            return eventKeyList;
        }

        // looping through all rows and adding to list
        if (cursor != null && cursor.moveToFirst()) {
            Log.e("getEventKeyFromEvent", DatabaseUtils.dumpCursorToString(cursor));
            do {
                eventKeyList.add((long) cursor.getInt(0));
                Log.e("tag", "cursor.getInt 0" + cursor.getInt(0));
            } while (cursor.moveToNext());
            cursor.close();
        }


        db.close();
        // return contact list

        return eventKeyList;
    }

    public ArrayList<MyMentees> getMyMentee(ArrayList<Long> eventKey, String userKey) {

        ArrayList<MyMentees> myMenteeList = new ArrayList<>();

        ArrayList<Skill> skillOtherArrayList;
        String otherSkill = null;

        if (eventKey.size() == 0) {
            return new ArrayList<>();
        }

        SQLiteDatabase db = this.getWritableDatabase();
        // Select All Query
        String selectQuery = "SELECT DISTINCT * FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE MenteeFlag" + " = '1'" +
                "AND EventKey IN ( '" + eventKey.get(0) + "'";

        for (int i = 1; i < eventKey.size(); i++) {
            if (i != eventKey.size() - 1) {
                selectQuery = selectQuery.concat(", '" + eventKey.get(i) + "'");
            } else {
                selectQuery = selectQuery.concat(", '" + eventKey.get(i) + "') GROUP BY UserKey");
            }
        }

        if (eventKey.size() == 1)
            selectQuery = "SELECT DISTINCT * FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE MenteeFlag" + " = '1'" +
                    "AND  EventKey IN ('" + eventKey.get(0) + "') GROUP BY UserKey";

        Log.e("tag", "id " + selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {

            Log.e("Cursor Object", DatabaseUtils.dumpCursorToString(cursor));

            do {
                if (!String.valueOf(userKey).equals(String.valueOf(cursor.getString(0)))) {

                    MyMentees myMentees = new MyMentees();
                    myMentees.setMentorID(String.valueOf(cursor.getString(0)));
                    myMentees.setMenteeName(getName(String.valueOf(cursor.getString(0))));
                    myMentees.setMenteeDescription(getCompanyName(String.valueOf(cursor.getString(0))));
                    myMentees.setMenteeImage(getUserImage(String.valueOf(cursor.getString(0))));

                    Log.e("Cursor Object", String.valueOf(cursor.getString(0)));
                    Log.e("Cursor Object", getName(String.valueOf(cursor.getString(0))));
                    Log.e("Cursor Object", getCompanyName(String.valueOf(cursor.getString(0))));
                    Log.e("Cursor Object", String.valueOf(getUserImage(String.valueOf(cursor.getString(0)))));


                    skillOtherArrayList = getUserOtherSkill(String.valueOf(cursor.getString(0)));
                    for (int j = 0; j < skillOtherArrayList.size(); j++) {
                        Skill skill = skillOtherArrayList.get(j);

                        if (j == 0)
                            otherSkill = skill.getTitle();
                        else if (j == skillOtherArrayList.size() - 1)
                            otherSkill += "\t|\t" + skill.getTitle() + "\t";
                        else
                            otherSkill += "\t|\t" + skill.getTitle();

                    }
                    myMentees.setOtherMentoring(otherSkill);
                    //  Log.e("Cursor Object", otherSkill);
                    myMenteeList.add(myMentees);
                }
            } while (cursor.moveToNext());

            cursor.close();
            db.close();

        }


        return myMenteeList;
    }

    public ArrayList<Long> getMyUpComingEventsKey(ArrayList<Skill> skillArrayList, String userKey) {

        ArrayList<Long> eventKeyList = new ArrayList<>();

        if (skillArrayList.size() == 0)
            return new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT DISTINCT * FROM " + TABLE_NAME_USER_EVENT + " WHERE UserKey != '" + userKey +
                "' AND TagKey IN ( '" + skillArrayList.get(0).getSkillId() + "'";

        for (int i = 1; i < skillArrayList.size(); i++) {
            if (i != skillArrayList.size() - 1) {
                selectQuery = selectQuery.concat(", '" + skillArrayList.get(i).getSkillId() + "'");
            } else {
                selectQuery = selectQuery.concat(", '" + skillArrayList.get(i).getSkillId() + "') AND EventKey NOT IN " +
                        "(SELECT EventKey FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE UserKey = '" +
                        userKey + "' AND ParticipantStatus IN ('0', '1', '2')) GROUP BY EventKey");
            }
        }
        if (skillArrayList.size() == 1)
            selectQuery = "SELECT DISTINCT * FROM " + TABLE_NAME_USER_EVENT + " WHERE UserKey != '" + userKey +
                    "' AND TagKey = '" + skillArrayList.get(0).getSkillId() + "' AND EventKey NOT IN " +
                    "(SELECT EventKey FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE UserKey = '" +
                    userKey + "' AND ParticipantStatus IN ('0', '1', '2')) GROUP BY EventKey";

        Log.e("tag", "id " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null) {
            Log.e("getMyUpComingEventsKey", "id " + DatabaseUtils.dumpCursorToString(cursor));

            // looping through all rows and adding to list
            if (cursor.moveToFirst() && cursor.getCount() > 0) {
                do {
                    eventKeyList.add((long) cursor.getInt(0));
                    Log.e("tag", "cursor.getInt " + cursor.getInt(0));
                } while (cursor.moveToNext());
            }

            cursor.close();
        }
        db.close();
        // return contact list

        return eventKeyList;
    }

    public ArrayList<Network> getNetworkContacts(ArrayList<Long> userKey, String userKey1) {


        ArrayList<Network> profileList = new ArrayList<>();

        for (int i = 0; i < userKey.size(); i++) {

            // Select All Query
            String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE + " WHERE UserKey = '" + userKey.get(i) + "'";
            Log.e("tag", "selectQuery" + selectQuery);

            SQLiteDatabase db = this.getWritableDatabase();
            Cursor cursor = db.rawQuery(selectQuery, null);

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Network network = new Network();
                    network.setDrawable(Utility.getPhoto(cursor.getBlob(7)));
                    network.setId(cursor.getInt(0));

                    int eduKey = cursor.getInt(0);
                    String selectInnerQuery = "SELECT * FROM " + TABLE_NAME_USER_TAG + " WHERE UserKey = '" + cursor.getInt(0) + "'";

                    Cursor InnerCursor = db.rawQuery(selectInnerQuery, null);
                    Log.e("tag", "selectInnerQuery" + selectInnerQuery);
                    network.setMentor(0);
                    network.setMentee(0);
                    network.setAlumini(0);

                    if (InnerCursor != null && InnerCursor.moveToFirst()) {
                        do {

                            Log.e("tag", "cursor.getInt(2)" + InnerCursor.getInt(2));
                            Log.e("tag", "cursor.getInt(3)" + InnerCursor.getInt(3));
                            if (InnerCursor.getInt(2) == 1)
                                network.setMentor(1);
                            if (InnerCursor.getInt(3) == 1)
                                network.setMentee(1);

                            network.setNetworkTags(new NetworkTag(cursor.getInt(1), cursor.getInt(3), cursor.getInt(2)));

                        } while (InnerCursor.moveToNext());
                        InnerCursor.close();
                    }

                    profileList.add(network);

                } while (cursor.moveToNext());
            }

            cursor.close();

            db.close();
            // return contact list
        }

        ArrayList<Network> profileList2 = new ArrayList<>();

        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE + " up2 WHERE UserKey IN " +
                "( SELECT UserKey FROM " + TABLE_NAME_USER_EDUCATION + " ue2 WHERE EducationKey IN " +
                "( SELECT EducationKey FROM " + TABLE_NAME_USER_EDUCATION + " ue, " + TABLE_NAME_USER_PROFILE + " up" +
                " WHERE up.EmailID = '" + getUserEmail(userKey1) + "'  AND up.UserKey = ue.UserKey ))";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            Log.e("tag", "selectQuery" + DatabaseUtils.dumpCursorToString(cursor));

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Network network = new Network();
                    network.setDrawable(Utility.getPhoto(cursor.getBlob(7)));
                    network.setId(cursor.getInt(0));
                    network.setAlumini(1);

                    int eduKey = cursor.getInt(0);
                    String selectInnerQuery = "SELECT * FROM " + TABLE_NAME_USER_TAG + " WHERE UserKey = '" + cursor.getInt(0) + "'";

                    Cursor InnerCursor = db.rawQuery(selectInnerQuery, null);
                    Log.e("tag", "selectInnerQuery" + selectInnerQuery);
                    network.setMentor(0);
                    network.setMentee(0);

                    if (InnerCursor != null && InnerCursor.moveToFirst()) {
                        do {

                            Log.e("tag", "cursor.getInt(2)" + InnerCursor.getInt(2));
                            Log.e("tag", "cursor.getInt(3)" + InnerCursor.getInt(3));
                            if (InnerCursor.getInt(2) == 1)
                                network.setMentor(1);
                            if (InnerCursor.getInt(3) == 1)
                                network.setMentee(1);

                            network.setNetworkTags(new NetworkTag(cursor.getInt(1), cursor.getInt(3), cursor.getInt(2)));


                        } while (InnerCursor.moveToNext());
                        InnerCursor.close();
                    }

                    profileList2.add(network);

                } while (cursor.moveToNext());
            }
        }
        cursor.close();

        db.close();

        ArrayList<Network> finalProfileList = new ArrayList<>();

        for (Network emp : profileList) {
            boolean found = false;
            for (Network tgtEmp : profileList2) {
                if ((emp.getId() == tgtEmp.getId()) && (emp.getMentor() == tgtEmp.getMentor())) {
                    found = true;
                    finalProfileList.add(tgtEmp);
                    break;
                }
            }
            if (!found) {
                finalProfileList.add(emp);
            }
        }

        return finalProfileList;
    }

    public void updateUpcomingEventSelected(String eventKey, String userKey, int i) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(USER_KEY, userKey);
        values.put(EVENT_KEY, eventKey);
        values.put(MENTOR_FLAG, 0);
        values.put(MENTEE_FLAG, 1);
        values.put(PARTICIPANT_STATUS, i);

        db.insert(TABLE_QUERY_USER_PARTICIPANTS, null, values);

        db.close();
    }

    public ArrayList<USER_EVENT> getMyAttendedEvents(String userKey) {

        ArrayList<USER_EVENT> userEventArrayList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT * FROM " + TABLE_NAME_USER_EVENT + " WHERE EventKey IN (SELECT EventKey FROM "
                + TABLE_QUERY_USER_PARTICIPANTS + " WHERE UserKey = '" + userKey + "' AND ParticipantStatus IN ( '1', '0' )) GROUP BY EventKey";
        Log.e("tag", selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            Log.e("cursor", DatabaseUtils.dumpCursorToString(cursor));

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {

                    USER_EVENT userEvent = new USER_EVENT();

                    userEvent.setUserKey(cursor.getString(16));
                    userEvent.setEventKey(cursor.getString(0));
                    userEvent.setEventStartTs(cursor.getString(5));
                    userEvent.setEventEndTs(cursor.getString(6));
                    userEvent.setEventTitle(cursor.getString(15));
                    userEvent.setAddress(cursor.getString(7));
                    userEvent.setLocationKey(String.valueOf(cursor.getInt(3)));
                    userEvent.setImageKey(cursor.getBlob(9));
                    userEvent.setOpen(cursor.getInt(8) >= 1);
                    userEvent.setDescription(cursor.getString(4));

                    Log.e("tag", "id " + cursor.getString(1));
                    Log.e("tag", "name " + cursor.getString(2));
                    Log.e("tag", "job " + cursor.getString(3));
                    Log.e("tag", "id " + cursor.getString(4));
                    Log.e("tag", "name " + cursor.getString(5));
                    Log.e("tag", "job " + cursor.getString(6));
                    Log.e("tag", "id " + cursor.getString(7));
                    Log.e("tag", "name " + cursor.getString(8));
                    // Log.e("tag", "job " + cursor.getString(9));
                    //     Log.e("tag", "id " + Arrays.toString(cursor.getBlob(10)));
                    Log.e("tag", "name " + cursor.getString(11));
                    Log.e("tag", "job " + cursor.getString(12));

                    String innerSelectQuery = "SELECT * FROM " + TABLE_NAME_EVENT_TYPE_MASTER + " WHERE EventTypeKey = '" + cursor.getInt(1) + "'";

                    Cursor innerCursor = db.rawQuery(innerSelectQuery, null);
                    // looping through all rows and adding to list
                    if (innerCursor.moveToFirst() && innerCursor.getCount() > 0) {

                        userEvent.setEventTypeKey(innerCursor.getString(1));
                        Log.e("tag", "EventTypeKey " + innerCursor.getString(1));

                    } else {
                        userEvent.setEventTypeKey("");
                    }

                    innerCursor.close();
                    String innerSelectQuery2 = "SELECT * FROM " + TABLE_NAME_TAG_MASTER + " WHERE TagKey = '" + cursor.getInt(2) + "'";

                    Cursor innerCursor2 = db.rawQuery(innerSelectQuery2, null);
                    // looping through all rows and adding to list
                    if (innerCursor2.moveToFirst() && innerCursor2.getCount() > 0) {

                        userEvent.setTagKey(innerCursor2.getString(1));
                        Log.e("tag", "job " + innerCursor2.getString(1));

                    } else {
                        userEvent.setTagKey("");
                    }

                    innerCursor2.close();
                    Date eventEndDate = null, currentDate = null;
                    try {
                        eventEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(6));
                        currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utility.getDateTime());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (eventEndDate != null && eventEndDate.before(currentDate))
                        userEventArrayList.add(userEvent);

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        db.close();

        return userEventArrayList;

    }


    public ArrayList<USER_EVENT> getParticularMyAttendedEvents(String userKey, String eventTypeKey) {

        ArrayList<USER_EVENT> userEventArrayList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT DISTINCT * FROM " + TABLE_NAME_USER_EVENT + " WHERE EventKey IN (SELECT EventKey FROM "
                + TABLE_QUERY_USER_PARTICIPANTS + " WHERE UserKey = '" + userKey + "' AND ParticipantStatus IN ( '1', '0' )) " +
                "AND EventTypeKey = '" + eventTypeKey + "' GROUP BY EventKey";
        Log.e("tag", selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor != null) {
            Log.e("cursor", DatabaseUtils.dumpCursorToString(cursor));

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {

                    USER_EVENT userEvent = new USER_EVENT();

                    userEvent.setUserKey(cursor.getString(16));
                    userEvent.setEventKey(cursor.getString(0));
                    userEvent.setEventStartTs(cursor.getString(5));
                    userEvent.setEventEndTs(cursor.getString(6));
                    userEvent.setEventTitle(cursor.getString(15));
                    userEvent.setAddress(cursor.getString(7));
                    userEvent.setLocationKey(String.valueOf(cursor.getInt(3)));
                    userEvent.setImageKey(cursor.getBlob(9));
                    userEvent.setOpen(cursor.getInt(8) >= 1);
                    userEvent.setDescription(cursor.getString(4));

                    Log.e("tag", "id " + cursor.getString(1));
                    Log.e("tag", "name " + cursor.getString(2));
                    Log.e("tag", "job " + cursor.getString(3));
                    Log.e("tag", "id " + cursor.getString(4));
                    Log.e("tag", "name " + cursor.getString(5));
                    Log.e("tag", "job " + cursor.getString(6));
                    Log.e("tag", "id " + cursor.getString(7));
                    Log.e("tag", "name " + cursor.getString(8));
                    // Log.e("tag", "job " + cursor.getString(9));
                    //    Log.e("tag", "id " + Arrays.toString(cursor.getBlob(10)));
                    Log.e("tag", "name " + cursor.getString(11));
                    Log.e("tag", "job " + cursor.getString(12));

                    String innerSelectQuery = "SELECT * FROM " + TABLE_NAME_EVENT_TYPE_MASTER + " WHERE EventTypeKey = '" + cursor.getInt(1) + "'";

                    Cursor innerCursor = db.rawQuery(innerSelectQuery, null);
                    // looping through all rows and adding to list
                    if (innerCursor.moveToFirst() && innerCursor.getCount() > 0) {

                        userEvent.setEventTypeKey(innerCursor.getString(1));
                        Log.e("tag", "EventTypeKey " + innerCursor.getString(1));

                    } else {
                        userEvent.setEventTypeKey("");
                    }

                    innerCursor.close();
                    String innerSelectQuery2 = "SELECT * FROM " + TABLE_NAME_TAG_MASTER + " WHERE TagKey = '" + cursor.getInt(2) + "'";

                    Cursor innerCursor2 = db.rawQuery(innerSelectQuery2, null);
                    // looping through all rows and adding to list
                    if (innerCursor2.moveToFirst() && innerCursor2.getCount() > 0) {

                        userEvent.setTagKey(innerCursor2.getString(1));
                        Log.e("tag", "job " + innerCursor2.getString(1));

                    } else {
                        userEvent.setTagKey("");
                    }

                    innerCursor2.close();
                    Date eventEndDate = null, currentDate = null;
                    try {
                        eventEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(6));
                        currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utility.getDateTime());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    if (eventEndDate != null && eventEndDate.before(currentDate))
                        userEventArrayList.add(userEvent);

                } while (cursor.moveToNext());
            }
        }
        assert cursor != null;
        if (cursor != null) {
            cursor.close();
        }
        db.close();

        return userEventArrayList;

    }


    public void deleteEvents() {
        SQLiteDatabase dbUpdate = this.getWritableDatabase();

        dbUpdate.delete(TABLE_NAME_USER_EVENT, null, null);
        dbUpdate.delete(TABLE_QUERY_USER_PARTICIPANTS, null, null);
        dbUpdate.close();
    }

    public boolean getUserChoiceOnEventDetails(String eventKey) {

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE EventKey = '" + eventKey +
                "' AND ParticipantStatus IN ( '0' , '1' , '2' )";
        Log.e("tag", "selectQuery " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public boolean isOpen(String eventKey) {

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_EVENT + " WHERE EventKey = '" + eventKey +
                "' AND isOpen = '0' ";
        Log.e("tag", "selectQuery " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public ArrayList<OtherNotification> getMenteeMentorRequest() {

        ArrayList<OtherNotification> otherNotificationsList = new ArrayList<>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE UserKey = '" + Prefs.getUserKey() +
                "' AND ((MentorFlag = '0' AND MenteeFlag = '1') OR (MentorFlag = '1' AND MenteeFlag = '0') OR (MentorFlag = '0' AND MenteeFlag = '0'))" +
                " AND ParticipantStatus = '3'";
        Log.e("tag", "selectQuery " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null && cursor.moveToFirst()) {
            Log.e("getMenteeMentorRequest", DatabaseUtils.dumpCursorToString(cursor));

            do {

                OtherNotification otherNotification = new OtherNotification();

                otherNotification.setEventId(cursor.getString(1));
                //cursor.getString(2) -- > mentorflag
                if (cursor.getString(2).equals("0") && cursor.getString(3).equals("1"))
                    otherNotification.setMentorMentee(MENTEE);
                else if (cursor.getString(2).equals("1") && cursor.getString(3).equals("0"))
                    otherNotification.setMentorMentee(MENTOR);
                else
                    otherNotification.setMentorMentee(PARTICIPANT);

                String innerSelectQuery = "SELECT * FROM " + TABLE_NAME_USER_EVENT + " WHERE EventKey = '" + cursor.getInt(1) + "'";

                SQLiteDatabase innerDb = this.getWritableDatabase();
                Cursor innerCursor = innerDb.rawQuery(innerSelectQuery, null);

                if (innerCursor.moveToFirst() && innerCursor != null) {

                    innerCursor.moveToFirst();
                    Log.e("getMenteeMentorRequest", DatabaseUtils.dumpCursorToString(innerCursor));

                    otherNotification.setNotifyName(getName(innerCursor.getString(16)));

                    otherNotification.setImg(getUserImage(innerCursor.getString(16)));

                    Date createdDate = null, todaysDate = null;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
                    try {
                        createdDate = sdf.parse(innerCursor.getString(12));
                        todaysDate = sdf.parse(getDateTime());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (createdDate != null && todaysDate != null) {
                        if (sdf.format(createdDate).equals(sdf.format(todaysDate))) {

                            SimpleDateFormat showformat = new SimpleDateFormat("hh:mm a");
                            try {
                                otherNotification.setNotifyTime(String.valueOf(showformat.parse(innerCursor.getString(12))));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        } else {

                            SimpleDateFormat showformat = new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                otherNotification.setNotifyTime(String.valueOf(showformat.parse(innerCursor.getString(12))));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    String innerSelectQuery2 = "SELECT * FROM " + TABLE_NAME_TAG_MASTER + " WHERE TagKey = '" + innerCursor.getInt(2) + "'";

                    SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
                    Cursor innerCur = sqLiteDatabase.rawQuery(innerSelectQuery2, null);

                    if (innerCur.moveToFirst() && innerCur.getCount() > 0) {

                        Log.e("getMenteeMentorRequest", DatabaseUtils.dumpCursorToString(innerCur));

                        otherNotification.setEventType(innerCur.getString(1));

                        innerCur.close();
                    }
                    sqLiteDatabase.close();

                    innerCursor.close();

                }
                innerDb.close();

                otherNotificationsList.add(otherNotification);

            } while (cursor.moveToNext());

            cursor.close();
        }
        db.close();
        return otherNotificationsList;
    }

    public void updateEventParticipantsStatus(long eventId, String userKey, String status) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues eventValues = new ContentValues();

        int mentor = 0;
        int mentee = 0;

        if (status.equals("0"))
            mentee = 1;
        else if (status.equals("1"))
            mentor = 1;

        eventValues.put(MENTOR_FLAG, mentor);
        eventValues.put(MENTEE_FLAG, mentee);

        db.update(TABLE_QUERY_USER_PARTICIPANTS, eventValues, "EventKey = ? AND UserKey = ?",
                new String[]{String.valueOf(eventId), userKey});

        db.close();

    }

    public void updateParticipantStatus(String eventId, String userKey, String status) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues eventValues = new ContentValues();

        eventValues.put(PARTICIPANT_STATUS, status);

        db.update(TABLE_QUERY_USER_PARTICIPANTS, eventValues, "EventKey = ? AND UserKey = ?",
                new String[]{eventId, userKey});

        db.close();

    }

    public String getNumberOfPeopleGoing(String eventKey, String userKey) {

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE EventKey = '" + eventKey +
                "' AND UserKey != '" + userKey + "' AND ParticipantStatus IN ('0', '1')";
        Log.e("tag", "selectQuery " + selectQuery);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        String count = String.valueOf(cursor.getCount());
        if (cursor.getCount() > 0) {
            cursor.close();
            return count;
        }
        cursor.close();
        return "0";

    }

    public ArrayList<Contact> getAllContactsOfEvents(String eventKey) {
        ArrayList<Contact> contactArrayList = new ArrayList<>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_PROFILE + " WHERE UserKey IN ( SELECT UserKey " +
                "FROM " + TABLE_QUERY_USER_PARTICIPANTS + " WHERE EventKey ='" + eventKey + "' AND ParticipantStatus IN('0', '1'))";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Log.e("getAllContactsOfEvents", selectQuery);
        Log.e("getAllContactsOfEvents", DatabaseUtils.dumpCursorToString(cursor));

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {

                Contact contact = new Contact();
                contact.setId(cursor.getString(0));
                contact.setName(cursor.getString(5));
                contact.setJob(cursor.getString(6));
                Log.e("tag", "id " + cursor.getString(0));
                Log.e("tag", "name " + cursor.getString(5));
                Log.e("tag", "job " + cursor.getString(6));
                if (cursor.getBlob(7).length > 0) {
                    contact.setImage(cursor.getBlob(7));
                    //         Log.e("tag", "image " + Arrays.toString(cursor.getBlob(7)));
                } else
                    contact.setImage(null);

                String innerSelectQuery = "SELECT * FROM " + TABLE_NAME_USER_EDUCATION + " WHERE UserKey = '" + cursor.getInt(0) + "'";

                Cursor innerCursor = db.rawQuery(innerSelectQuery, null);
                String eduKey;
                // looping through all rows and adding to list
                if (innerCursor.moveToFirst() && innerCursor.getCount() > 0) {

                    innerCursor.moveToLast();
                    eduKey = String.valueOf(innerCursor.getInt(0));
                    Log.e("tag", "du key " + eduKey);

                    String innerRSelectQuery = "SELECT * FROM " + TABLE_NAME_EDUCATION_MASTER + " WHERE EducationKey = '" + eduKey + "'";

                    Cursor innerRCursor = db.rawQuery(innerRSelectQuery, null);
                    if (innerRCursor.moveToFirst() && innerRCursor.getCount() > 0) {
                        contact.setColg(innerRCursor.getString(1));

                        Log.e("tag", "edu school " + innerRCursor.getString(1));
                    } else {

                        contact.setColg("");
                    }
                    innerCursor.close();
                    innerRCursor.close();

                } else {
                    innerCursor.close();
                    contact.setColg("");
                }
                contactArrayList.add(contact);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return contactArrayList;

    }

    public boolean getEventCreator(String userKey, String eventKey) {

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_EVENT + " WHERE UserKey = '" + userKey + "' AND eventKey " +
                "= '" + eventKey + "'";
        Log.e("tag", "selectQuery " + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.e("tag", DatabaseUtils.dumpCursorToString(cursor));
        if (cursor.getCount() > 0) {
            cursor.close();
            return true;
        }
        cursor.close();
        return false;
    }

    public void removeUser(String userKey, String eventKey) {
        SQLiteDatabase dbUpdate = this.getWritableDatabase();

        dbUpdate.delete(TABLE_QUERY_USER_PARTICIPANTS, "UserKey ='" + userKey + "' AND EventKey = '" + eventKey + "'", null);
        dbUpdate.close();
    }

    public USER_PROFILE getUserDetails(String userKey) {
        return null;
    }

    public boolean getEventEndDate(String eventKey) {

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_NAME_USER_EVENT + " WHERE EventKey = '" + eventKey + "'";
        Log.e("tag", "selectQuery " + selectQuery);
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.e("tag", DatabaseUtils.dumpCursorToString(cursor));
        if (cursor.getCount() > 0) {
            cursor.moveToFirst();

            Date eventEndDate = null, currentDate = null;
            try {
                eventEndDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(cursor.getString(6));
                currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(Utility.getDateTime());

            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (eventEndDate != null && eventEndDate.after(currentDate)) {
                cursor.close();
                return true;
            }
        }
        cursor.close();
        return false;
    }

}