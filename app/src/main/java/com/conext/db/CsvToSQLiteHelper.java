package com.conext.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.util.Log;

import com.conext.utils.Utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

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

/**
 * Created by Ashith VL on 7/25/2017.
 */

class CsvToSQLiteHelper {

    private static InputStream inputStream = null;

    static void insertInToUserProfileTable(Context context, SQLiteDatabase db) {

        //String filepath = data.getData().getPath();

        //SQLiteDBHelper sqLiteDBHelper = new SQLiteDBHelper(context);
        //SQLiteDatabase db = sqLiteDBHelper.getWritableDatabase();

        boolean flag = false;
        byte[] ProfilePic;

        db.execSQL("delete from " + TABLE_NAME_USER_PROFILE);
        try {

            inputStream = context.getAssets().open("userProfile.csv");

            try {

                //FileReader file = new FileReader(filepath);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();

                while ((line = buffer.readLine()) != null) {

                    String[] str = line.split(",", 14);  // defining 3 columns with null or blank field //values acceptance
                    //Id, Company,Name,Price
                    String UserKey = str[0].toString();
                    String EmailID = str[1].toString();
                    String Password = str[2].toString();
                    String FirstName = str[3].toString();
                    String LastName = str[4].toString();
                    String TitleDisplay = str[5].toString();
                    String CompanyDisplay = str[6].toString();
                    // String ProfilePic = convertHexToString(str[7].toString());

                    if (flag) {
                        ProfilePic = Utility.getBytes(((BitmapDrawable) Drawable.createFromStream(context.getAssets().open("Pics/girl.png"), null)).getBitmap());
                        flag = false;
                    } else {
                        ProfilePic = Utility.getBytes(((BitmapDrawable) Drawable.createFromStream(context.getAssets().open("Pics/male.png"), null)).getBitmap());
                        flag = true;
                    }
                    String DeleteFlg = str[8].toString();
                    String CreatedBy = str[9].toString();
                    String CreatedTs = str[10].toString();
                    String ModifiedBy = str[11].toString();
                    String ModifiedTs = str[12].toString();
                    int LocationKey = Integer.parseInt(str[13].toString());

                    contentValues.put("UserKey", UserKey);
                    contentValues.put("EmailID", EmailID);
                    contentValues.put("Password", Password);
                    contentValues.put("FirstName", FirstName);
                    contentValues.put("LastName", LastName);
                    contentValues.put("TitleDisplay", TitleDisplay);
                    contentValues.put("CompanyDisplay", CompanyDisplay);
                    contentValues.put("ProfilePic", ProfilePic);
                    contentValues.put("DeleteFlag", DeleteFlg);
                    contentValues.put("CreatedBy", CreatedBy);
                    contentValues.put("CreatedTs", CreatedTs);
                    contentValues.put("ModifiedBy", ModifiedBy);
                    contentValues.put("ModifiedTs", ModifiedTs);
                    contentValues.put("LocationKey", LocationKey);

                    db.insert(TABLE_NAME_USER_PROFILE, null, contentValues);
                    Log.e("tag", "Successfully Updated Database.");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                if (db.inTransaction())
                    db.endTransaction();

                Log.e("tag", e.getMessage().toString() + "first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();

            Log.e("tag", ex.getMessage().toString() + " userProfile second");
        }

    }

    static void insertInToTagMasterTable(Context context, SQLiteDatabase db) {

        db.execSQL("delete from " + TABLE_NAME_TAG_MASTER);
        try {
            inputStream = context.getAssets().open("tagMaster.csv");
            try {

                //FileReader file = new FileReader(filepath);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();

                while ((line = buffer.readLine()) != null) {

                    String[] str = line.split(",", 7);  // defining 3 columns with null or blank field //values acceptance
                    //Id, Company,Name,Price
                    String TagKey = str[0].toString();
                    String TagName = str[1].toString();
                    String TagDescription = str[2].toString();
                    String CreatedBy = str[3].toString();
                    String CreatedTs = str[4].toString();
                    String ModifiedBy = str[5].toString();
                    String ModifiedTs = str[6].toString();

                    contentValues.put("TagKey", TagKey);
                    contentValues.put("TagName", TagName);
                    contentValues.put("TagDescription", TagDescription);
                    contentValues.put("CreatedBy", CreatedBy);
                    contentValues.put("CreatedTs", CreatedTs);
                    contentValues.put("ModifiedBy", ModifiedBy);
                    contentValues.put("ModifiedTs", ModifiedTs);

                    db.insert(TABLE_NAME_TAG_MASTER, null, contentValues);
                    Log.e("tag", "Successfully Updated Database.");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                if (db.inTransaction())
                    db.endTransaction();

                Log.e("tag", e.getMessage().toString() + " tagMaster first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();

            Log.e("tag", ex.getMessage().toString() + " tagMaster second");
        }

    }

    static void insertInToEducationMasterTable(Context context, SQLiteDatabase db) {

        db.execSQL("delete from " + TABLE_NAME_EDUCATION_MASTER);
        try {
            inputStream = context.getAssets().open("educationMaster.csv");
            try {

                //FileReader file = new FileReader(filepath);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();

                while ((line = buffer.readLine()) != null) {

                    String[] str = line.split(",", 13);  // defining 3 columns with null or blank field //values acceptance
                    //Id, Company,Name,Price
                    int EducationKey = Integer.parseInt(str[0].toString());
                    String SchoolName = str[1].toString();
                    String City = str[2].toString();
                    String Address = str[3].toString();
                    String Type = str[4].toString();
                    String Info = str[5].toString();
                    String Website = str[6].toString();
                    String DeleteFlg = str[7].toString();
                    String CreatedBy = str[8].toString();
                    String CreatedTs = str[9].toString();
                    String ModifiedBy = str[10].toString();
                    String ModifiedTs = str[11].toString();
                    String LocationKey = str[12].toString();

                    contentValues.put("EducationKey", EducationKey);
                    contentValues.put("SchoolName", SchoolName);
                    contentValues.put("City", City);
                    contentValues.put("Address", Address);
                    contentValues.put("Type", Type);
                    contentValues.put("Info", Info);
                    contentValues.put("Website", Website);
                    contentValues.put("DeleteFlag", DeleteFlg);
                    contentValues.put("CreatedBy", CreatedBy);
                    contentValues.put("CreatedTs", CreatedTs);
                    contentValues.put("ModifiedBy", ModifiedBy);
                    contentValues.put("ModifiedTs", ModifiedTs);
                    contentValues.put("LocationKey", LocationKey);

                    db.insert(TABLE_NAME_EDUCATION_MASTER, null, contentValues);
                    Log.e("tag", "Successfully Updated Database.");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                if (db.inTransaction())
                    db.endTransaction();

                Log.e("tag", e.getMessage().toString() + " educationMaster first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();

            Log.e("tag", ex.getMessage().toString() + " educationMaster second");
        }

    }

    static void insertInToWorkMasterTable(Context context, SQLiteDatabase db) {

        db.execSQL("delete from " + TABLE_NAME_WORK_MASTER);
        try {
            inputStream = context.getAssets().open("workMaster.csv");
            try {

                //FileReader file = new FileReader(filepath);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();

                while ((line = buffer.readLine()) != null) {

                    String[] str = line.split(",", 16);  // defining 3 columns with null or blank field //values acceptance
                    //Id, Company,Name,Price
                    int WorkKey = Integer.parseInt(str[0].toString());
                    int LinkedInId = Integer.parseInt(str[1].toString());
                    String Name = str[2].toString();
                    String Info = str[3].toString();
                    String Specialities = str[4].toString();
                    String Website = str[5].toString();
                    String Industry = str[6].toString();
                    String Type = str[7].toString();
                    String CompanySize = str[8].toString();
                    int Founded = Integer.parseInt(str[9].toString());
                    String DeleteFlg = str[10].toString();
                    String CreatedBy = str[11].toString();
                    String CreatedTs = str[12].toString();
                    String ModifiedBy = str[13].toString();
                    String ModifiedTs = str[14].toString();
                    int LocationKey = Integer.parseInt(str[15].toString());

                    contentValues.put("WorkKey", WorkKey);
                    contentValues.put("LinkedInId", LinkedInId);
                    contentValues.put("Name", Name);
                    contentValues.put("Info", Info);
                    contentValues.put("Specialities", Specialities);
                    contentValues.put("Website", Website);
                    contentValues.put("Industry", Industry);
                    contentValues.put("Type", Type);
                    contentValues.put("CompanySize", CompanySize);
                    contentValues.put("Founded", Founded);
                    contentValues.put("DeleteFlag", DeleteFlg);
                    contentValues.put("CreatedBy", CreatedBy);
                    contentValues.put("CreatedTs", CreatedTs);
                    contentValues.put("ModifiedBy", ModifiedBy);
                    contentValues.put("ModifiedTs", ModifiedTs);
                    contentValues.put("LocationKey", LocationKey);

                    db.insert(TABLE_NAME_WORK_MASTER, null, contentValues);
                    Log.e("tag", "Successfully Updated Database.");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                if (db.inTransaction())
                    db.endTransaction();

                Log.e("tag", e.getMessage().toString() + " workMaster first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();

            Log.e("tag", ex.getMessage().toString() + " workMaster second");
        }

    }

    static void insertInToLocationTable(Context context, SQLiteDatabase db) {

        db.execSQL("delete from " + TABLE_NAME_LOCATION);
        try {
            inputStream = context.getAssets().open("location.csv");
            try {

                //FileReader file = new FileReader(filepath);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();

                while ((line = buffer.readLine()) != null) {

                    String[] str = line.split(",", 9);  // defining 3 columns with null or blank field //values acceptance
                    //Id, Company,Name,Price
                    int LocationKey = Integer.parseInt(str[0].toString());
                    String Type = str[1].toString();
                    String Latitude = str[2].toString();
                    String Longitude = str[3].toString();
                    String DeleteFlg = str[4].toString();
                    String CreatedBy = str[5].toString();
                    String CreatedTs = str[6].toString();
                    String ModifiedBy = str[7].toString();
                    String ModifiedTs = str[8].toString();

                    contentValues.put("LocationKey", LocationKey);
                    contentValues.put("Type", Type);
                    contentValues.put("Latitude", Latitude);
                    contentValues.put("Longitude", Longitude);
                    contentValues.put("DeleteFlag", DeleteFlg);
                    contentValues.put("CreatedBy", CreatedBy);
                    contentValues.put("CreatedTs", CreatedTs);
                    contentValues.put("ModifiedBy", ModifiedBy);
                    contentValues.put("ModifiedTs", ModifiedTs);

                    db.insert(TABLE_NAME_LOCATION, null, contentValues);
                    Log.e("tag", "Successfully Updated Database.");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                if (db.inTransaction())
                    db.endTransaction();

                Log.e("tag", e.getMessage().toString() + " location first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();

            Log.e("tag", ex.getMessage().toString() + " location second");
        }

    }

    static void insertInToEventTypeMasterTable(Context context, SQLiteDatabase db) {

        db.execSQL("delete from " + TABLE_NAME_EVENT_TYPE_MASTER);
        try {
            inputStream = context.getAssets().open("eventTypeMaster.csv");
            try {

                //FileReader file = new FileReader(filepath);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();

                while ((line = buffer.readLine()) != null) {

                    String[] str = line.split(",", 9);  // defining 3 columns with null or blank field //values acceptance
                    //Id, Company,Name,Price
                    int EventTypeKey = Integer.parseInt(str[0].toString());
                    String Name = str[1].toString();
                    String DefaultOC = str[2].toString();
                    String DefaultImage = str[3].toString();
                    String DeleteFlg = str[4].toString();
                    String CreatedBy = str[5].toString();
                    String CreatedTs = str[6].toString();
                    String ModifiedBy = str[7].toString();
                    String ModifiedTs = str[8].toString();

                    contentValues.put("EventTypeKey", EventTypeKey);
                    contentValues.put("Name", Name);
                    contentValues.put("DefaultOC", DefaultOC);
                    contentValues.put("DefaultImage", DefaultImage);
                    contentValues.put("DeleteFlag", DeleteFlg);
                    contentValues.put("CreatedBy", CreatedBy);
                    contentValues.put("CreatedTs", CreatedTs);
                    contentValues.put("ModifiedBy", ModifiedBy);
                    contentValues.put("ModifiedTs", ModifiedTs);

                    db.insert(TABLE_NAME_EVENT_TYPE_MASTER, null, contentValues);
                    Log.e("tag", "Successfully Updated Database.");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                if (db.inTransaction())
                    db.endTransaction();

                Log.e("tag", e.getMessage().toString() + " eventTypeMaster first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();

            Log.e("tag", ex.getMessage().toString() + " eventTypeMaster second");
        }
    }

    static void insertInToUserTagTable(Context context, SQLiteDatabase db) {

        db.execSQL("delete from " + TABLE_NAME_USER_TAG);
        try {
            inputStream = context.getAssets().open("userTag.csv");
            try {

                //FileReader file = new FileReader(filepath);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();

                while ((line = buffer.readLine()) != null) {

                    String[] str = line.split(",", 9);  // defining 3 columns with null or blank field //values acceptance
                    //Id, Company,Name,Price
                    int UserKey = Integer.parseInt(str[0].toString());
                    int TagKey = Integer.parseInt(str[1].toString());
                    String MentorFlag = str[2].toString();
                    String MenteeFlag = str[3].toString();
                    String DeleteFlg = str[4].toString();
                    String CreatedBy = str[5].toString();
                    String CreatedTs = str[6].toString();
                    String ModifiedBy = str[7].toString();
                    String ModifiedTs = str[8].toString();

                    contentValues.put("UserKey", UserKey);
                    contentValues.put("TagKey", TagKey);
                    contentValues.put("MentorFlag", MentorFlag);
                    contentValues.put("MenteeFlag", MenteeFlag);
                    contentValues.put("DeleteFlag", DeleteFlg);
                    contentValues.put("CreatedBy", CreatedBy);
                    contentValues.put("CreatedTs", CreatedTs);
                    contentValues.put("ModifiedBy", ModifiedBy);
                    contentValues.put("ModifiedTs", ModifiedTs);

                    db.insert(TABLE_NAME_USER_TAG, null, contentValues);
                    Log.e("tag", "Successfully Updated Database.");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                if (db.inTransaction())
                    db.endTransaction();

                Log.e("tag", e.getMessage().toString() + " userTag first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();

            Log.e("tag", ex.getMessage().toString() + "userTag second");
        }

    }

    static void insertInToEventUserParticipantsTable(Context context, SQLiteDatabase db) {

        db.execSQL("delete from " + TABLE_QUERY_USER_PARTICIPANTS);
        try {
            inputStream = context.getAssets().open("userParticipants.csv");
            try {

                //FileReader file = new FileReader(filepath);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();

                while ((line = buffer.readLine()) != null) {

                    String[] str = line.split(",", 5);  // defining 3 columns with null or blank field //values acceptance
                    //Id, Company,Name,Price
                    int UserKey = Integer.parseInt(str[0].toString());
                    int EventKey = Integer.parseInt(str[1].toString());
                    String MentorFlag = str[2].toString();
                    String MenteeFlag = str[3].toString();
                    String ParticipantStatus = str[4].toString();

                    contentValues.put("UserKey", UserKey);
                    contentValues.put("EventKey", EventKey);
                    contentValues.put("MentorFlag", MentorFlag);
                    contentValues.put("MenteeFlag", MenteeFlag);
                    contentValues.put("ParticipantStatus", ParticipantStatus);

                    db.insert(TABLE_QUERY_USER_PARTICIPANTS, null, contentValues);
                    Log.e("tag", "Successfully Updated Database.");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                if (db.inTransaction())
                    db.endTransaction();

                Log.e("tag", e.getMessage().toString() + " userPArticipants first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();

            Log.e("tag", ex.getMessage().toString() + " userPArticipants second");
        }

    }

    static void insertInToUserWorkrTable(Context context, SQLiteDatabase db) {

        db.execSQL("delete from " + TABLE_NAME_USER_WORK);
        try {
            inputStream = context.getAssets().open("userWork.csv");
            try {

                //FileReader file = new FileReader(filepath);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();

                while ((line = buffer.readLine()) != null) {

                    String[] str = line.split(",", 12);  // defining 3 columns with null or blank field //values acceptance
                    //Id, Company,Name,Price
                    int UserKey = Integer.parseInt(str[0].toString());
                    int WorkKey = Integer.parseInt(str[1].toString());
                    String WorkTitle = str[2].toString();
                    int LocationKey = Integer.parseInt(str[3].toString());
                    int YearStart = Integer.parseInt(str[4].toString());
                    int YearFinish = Integer.parseInt(str[5].toString());
                    String Description = str[6].toString();
                    String DeleteFlg = str[7].toString();
                    String CreatedBy = str[8].toString();
                    String CreatedTs = str[9].toString();
                    String ModifiedBy = str[10].toString();
                    String ModifiedTs = str[11].toString();

                    contentValues.put("UserKey", UserKey);
                    contentValues.put("WorkKey", WorkKey);
                    contentValues.put("WorkTitle", WorkTitle);
                    contentValues.put("LocationKey", LocationKey);
                    contentValues.put("YearStart", YearStart);
                    contentValues.put("YearFinish", YearFinish);
                    contentValues.put("Description", Description);
                    contentValues.put("DeleteFlag", DeleteFlg);
                    contentValues.put("CreatedBy", CreatedBy);
                    contentValues.put("CreatedTs", CreatedTs);
                    contentValues.put("ModifiedBy", ModifiedBy);
                    contentValues.put("ModifiedTs", ModifiedTs);

                    db.insert(TABLE_NAME_USER_WORK, null, contentValues);
                    Log.e("tag", "Successfully Updated Database.");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                if (db.inTransaction())
                    db.endTransaction();

                Log.e("tag", e.getMessage().toString() + " userWork first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();

            Log.e("tag", ex.getMessage().toString() + " userWork second");
        }

    }

    static void insertInToUserEducationTable(Context context, SQLiteDatabase db) {

        db.execSQL("delete from " + TABLE_NAME_USER_EDUCATION);
        try {
            inputStream = context.getAssets().open("userEducation.csv");
            try {

                //FileReader file = new FileReader(filepath);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();

                while ((line = buffer.readLine()) != null) {

                    String[] str = line.split(",", 14);  // defining 3 columns with null or blank field //values acceptance

                    String EducationKey = str[0].toString();
                    String UserKey = str[1].toString();
                    String YearStart = str[2].toString();
                    String YearFinish = str[3].toString();
                    String Degree = str[4].toString();
                    String FieldOfStudy = str[5].toString();
                    String Grade = str[6].toString();
                    String Activities = str[7].toString();
                    String Description = str[8].toString();
                    String DeleteFlg = str[9].toString();
                    String CreatedBy = str[10].toString();
                    String CreatedTs = str[11].toString();
                    String ModifiedBy = str[12].toString();
                    String ModifiedTs = str[13].toString();

                    contentValues.put("EducationKey", EducationKey);
                    contentValues.put("UserKey", UserKey);
                    contentValues.put("YearStart", YearStart);
                    contentValues.put("YearFinish", YearFinish);
                    contentValues.put("Degree", Degree);
                    contentValues.put("FieldOfStudy", FieldOfStudy);
                    contentValues.put("Grade", Grade);
                    contentValues.put("Activities", Activities);
                    contentValues.put("Description", Description);
                    contentValues.put("DeleteFlag", DeleteFlg);
                    contentValues.put("CreatedBy", CreatedBy);
                    contentValues.put("CreatedTs", CreatedTs);
                    contentValues.put("ModifiedBy", ModifiedBy);
                    contentValues.put("ModifiedTs", ModifiedTs);

                    db.insert(TABLE_NAME_USER_EDUCATION, null, contentValues);
                    Log.e("tag", "Successfully Updated Database.");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                if (db.inTransaction())
                    db.endTransaction();

                Log.e("tag", e.getMessage().toString() + " userEvent first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();

            Log.e("tag", ex.getMessage().toString() + " userEvent second");
        }

    }

    static void insertInToUserEventTable(Context context, SQLiteDatabase db) {

        db.execSQL("delete from " + TABLE_NAME_USER_EVENT);

        boolean flag = false;

        try {
            inputStream = context.getAssets().open("userEvent.csv");
            try {

                //FileReader file = new FileReader(filepath);

                BufferedReader buffer = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                ContentValues contentValues = new ContentValues();
                String line = "";
                db.beginTransaction();

                while ((line = buffer.readLine()) != null) {

                    //Log.e("tag", line);

                    String[] str = line.split(",(?=([^\\\"]*\\\"[^\\\"]*\\\")*[^\\\"]*$)", 17);  // defining 3 columns with null or blank field //values acceptance
                    //Id, Company,Name,Price
                    int EventKey = Integer.parseInt(str[0].toString());
                    int EventTypeKey = Integer.parseInt(str[1].toString());
                    int TagKey = Integer.parseInt(str[2].toString());
                    int LocationKey = Integer.parseInt(str[3].toString());
                    String Description = str[4].toString();
                    String EventStartTs = str[5].toString();
                    String EventEndTs = str[6].toString();
                    String Address = str[7].toString();
                    String isOpen = str[8].toString();
                    //String ImageKey = convertHexToString(str[9].toString());

                    byte[] ImageKey = Utility.getBytes(((BitmapDrawable) Drawable.createFromStream(context.getAssets().open("Pics/coffee.png"), null)).getBitmap());

                    String DeleteFlg = str[10].toString();
                    String CreatedBy = str[11].toString();
                    String CreatedTs = str[12].toString();
                    String ModifiedBy = str[13].toString();
                    String ModifiedTs = str[14].toString();
                    String EventTitle = str[15].toString();
                    int UserKey = Integer.parseInt(str[16].toString());

                    contentValues.put("EventKey", EventKey);
                    contentValues.put("EventTypeKey", EventTypeKey);
                    contentValues.put("TagKey", TagKey);
                    contentValues.put("LocationKey", LocationKey);
                    contentValues.put("Description", Description);
                    contentValues.put("EventStartTs", EventStartTs);
                    contentValues.put("EventEndTs", EventEndTs);
                    contentValues.put("Address", Address);
                    contentValues.put("isOpen", isOpen);
                    contentValues.put("ImageKey", ImageKey);
                    contentValues.put("DeleteFlag", DeleteFlg);
                    contentValues.put("CreatedBy", CreatedBy);
                    contentValues.put("CreatedTs", CreatedTs);
                    contentValues.put("ModifiedBy", ModifiedBy);
                    contentValues.put("ModifiedTs", ModifiedTs);
                    contentValues.put("EventTitle", EventTitle);
                    contentValues.put("UserKey", UserKey);

                    db.insert(TABLE_NAME_USER_EVENT, null, contentValues);
                    Log.e("tag", "Successfully Updated Database.");
                }
                db.setTransactionSuccessful();
                db.endTransaction();
            } catch (IOException e) {
                if (db.inTransaction())
                    db.endTransaction();

                Log.e("tag", e.getMessage().toString() + " userEvent first");
            }
        } catch (Exception ex) {
            if (db.inTransaction())
                db.endTransaction();

            Log.e("tag", ex.getMessage().toString() + " userEvent second");
        }
    }

    static void exportDb(SQLiteDatabase db) {

        Cursor c = null;

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_NAME_LOCATION, null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "location.csv";

            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                Log.e("tag", "Exported Successfully.");
                Log.e("tag", "Exported Successfully to" + sdCardDir);
            }
            c.close();


        } catch (Exception ex) {
            Log.e("tag", ex.getMessage().toString());
        }

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_NAME_USER_PROFILE, null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "userProfile.csv";

            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != 7) {
                            if (j != colcount - 1)
                                bw.write(c.getString(j) + ",");
                            else
                                bw.write(c.getString(j));
                        } else
                            bw.write(convertStringToHex(String.valueOf((c.getBlob(7)))) + ",");
                    }
                    bw.newLine();
                }
                bw.flush();
                Log.e("tag", "Exported Successfully.");
                Log.e("tag", "Exported Successfully to" + sdCardDir);
            }
            c.close();


        } catch (Exception ex) {
            Log.e("catch", ex.getMessage().toString());
        }

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_NAME_TAG_MASTER, null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "tagMaster.csv";

            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                Log.e("tag", "Exported Successfully.");
                Log.e("tag", "Exported Successfully to" + sdCardDir);
            }
            c.close();


        } catch (Exception ex) {
            Log.e("tag", ex.getMessage().toString());
        }

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_NAME_EDUCATION_MASTER, null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "educationMaster.csv";

            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                Log.e("tag", "Exported Successfully.");
                Log.e("tag", "Exported Successfully to" + sdCardDir);
            }
            c.close();


        } catch (Exception ex) {
            Log.e("tag", ex.getMessage().toString());
        }

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_NAME_WORK_MASTER, null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "workMaster.csv";

            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                Log.e("tag", "Exported Successfully.");
                Log.e("tag", "Exported Successfully to" + sdCardDir);
            }
            c.close();


        } catch (Exception ex) {
            Log.e("tag", ex.getMessage().toString());
        }

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_NAME_EVENT_TYPE_MASTER, null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "eventTypeMaster.csv";

            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                Log.e("tag", "Exported Successfully.");
                Log.e("tag", "Exported Successfully to" + sdCardDir);
            }
            c.close();


        } catch (Exception ex) {
            Log.e("tag", ex.getMessage().toString());
        }

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_NAME_USER_TAG, null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "userTag.csv";

            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                Log.e("tag", "Exported Successfully.");
                Log.e("tag", "Exported Successfully to" + sdCardDir);
            }
            c.close();


        } catch (Exception ex) {
            Log.e("tag", ex.getMessage().toString());
        }

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_NAME_USER_EDUCATION, null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "userEducation.csv";

            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                Log.e("tag", "Exported Successfully.");
                Log.e("tag", "Exported Successfully to" + sdCardDir);
            }
            c.close();


        } catch (Exception ex) {
            Log.e("tag", ex.getMessage().toString());
        }

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_QUERY_USER_PARTICIPANTS, null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "userParticipants.csv";

            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                Log.e("tag", "Exported Successfully.");
                Log.e("tag", "Exported Successfully to" + sdCardDir);
            }
            c.close();


        } catch (Exception ex) {
            Log.e("tag", ex.getMessage().toString());
        }

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_NAME_USER_WORK, null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "userWork.csv";

            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != colcount - 1)
                            bw.write(c.getString(j) + ",");
                        else
                            bw.write(c.getString(j));
                    }
                    bw.newLine();
                }
                bw.flush();
                Log.e("tag", "Exported Successfully.");
                Log.e("tag", "Exported Successfully to" + sdCardDir);
            }
            c.close();


        } catch (Exception ex) {
            Log.e("tag", ex.getMessage().toString());
        }

        try {
            c = db.rawQuery("SELECT * FROM " + TABLE_NAME_USER_EVENT, null);
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            String filename = "userEvent.csv";

            // the name of the file to export with
            File saveFile = new File(sdCardDir, filename);
            FileWriter fw = new FileWriter(saveFile);

            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();

                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {

                        bw.write(c.getColumnName(i) + ",");

                    } else {

                        bw.write(c.getColumnName(i));

                    }
                }
                bw.newLine();

                for (int i = 0; i < rowcount; i++) {
                    c.moveToPosition(i);

                    for (int j = 0; j < colcount; j++) {
                        if (j != 9) {
                            if (j != colcount - 1)
                                bw.write(c.getString(j) + ",");
                            else
                                bw.write(c.getString(j));
                        } else
                            bw.write(convertStringToHex(String.valueOf((c.getBlob(9)))) + ",");
                    }
                    bw.newLine();
                }
                bw.flush();
                Log.e("tag", "Exported Successfully.");
                Log.e("tag", "Exported Successfully to" + sdCardDir);
            }
            c.close();


        } catch (Exception ex) {
            Log.e("catch", ex.getMessage().toString());
        }
    }


    // Convert Hex String to Byte Array

    public static byte[] hex2Byte(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer
                    .parseInt(str.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    // Convert Byte Arrary to Hex String
    public static String byte2hex(byte[] b) {

        // String Buffer can be used instead

        String hs = "";
        String stmp = "";

        for (int n = 0; n < b.length; n++) {
            stmp = (java.lang.Integer.toHexString(b[n] & 0XFF));

            if (stmp.length() == 1) {
                hs = hs + "0" + stmp;
            } else {
                hs = hs + stmp;
            }

            if (n < b.length - 1) {
                hs = hs + "";
            }
        }

        return hs;
    }

    public static String convertStringToHex(String str) {

        char[] chars = str.toCharArray();

        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }

        return hex.toString();
    }

    public static String convertHexToString(String hex) {

        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();

        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for (int i = 0; i < hex.length() - 1; i += 2) {

            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char) decimal);

            temp.append(decimal);
        }
        System.out.println("Decimal : " + temp.toString());

        return sb.toString();
    }

}
