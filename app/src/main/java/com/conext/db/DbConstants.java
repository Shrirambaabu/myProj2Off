package com.conext.db;

/**
 * Created by Ashith VL on 7/23/2017.
 */

class DbConstants {

    public static final String TABLE_NAME_USER_PROFILE = "USER_PROFILE";
    public static final String TABLE_NAME_TAG_MASTER = "TAG_MASTER";
    public static final String TABLE_NAME_EDUCATION_MASTER = "EDUCATION_MASTER";
    public static final String TABLE_NAME_WORK_MASTER = "WORK_MASTER";
    public static final String TABLE_NAME_LOCATION = "LOCATION";
    public static final String TABLE_NAME_EVENT_TYPE_MASTER = "EVENT_TYPE_MASTER";
    public static final String TABLE_NAME_USER_TAG = "USER_TAG";
    public static final String TABLE_NAME_USER_EDUCATION = "USER_EDUCATION";
    public static final String TABLE_QUERY_USER_PARTICIPANTS = "USER_PARTICIPANTS";
    public static final String TABLE_NAME_USER_WORK = "USER_WORK";
    public static final String TABLE_NAME_USER_EVENT = "USER_EVENT";

    public static final String CREATE_TABLE_QUERY_USER_PROFILE =
            "CREATE TABLE USER_PROFILE (\n" +
                    "\tUserKey INTEGER PRIMARY KEY NOT NULL DEFAULT '10000000' ,\n" +
                    "\tEmailID varchar(256) NOT NULL ,\n" +
                    "\tPassword varchar(256) NOT NULL ,\n" +
                    "\tFirstName varchar(256) NOT NULL ,\n" +
                    "\tLastName varchar(256) NOT NULL ,\n" +
                    "\tTitleDisplay varchar(256) NOT NULL,\n" +
                    "\tCompanyDisplay varchar(256) NOT NULL,\n" +
                    "\tProfilePic BLOB NOT NULL,\n" +
                    "\tDeleteFlag BOOLEAN NOT NULL,\n" +
                    "\tCreatedBy varchar(256) NOT NULL,\n" +
                    "\tCreatedTs TIMESTAMP NOT NULL,\n" +
                    "\tModifiedBy varchar(256) NOT NULL,\n" +
                    "\tModifiedTs TIMESTAMP NOT NULL,\n" +
                    "\tLocationKey INT(10) NOT NULL,\n" +
                    "\tFOREIGN KEY(LocationKey) REFERENCES LOCATION(LocationKey)\n" +
                    ")";

    public static final String CREATE_TABLE_QUERY_TAG_MASTER =
            "CREATE TABLE TAG_MASTER (\n" +
                    "\tTagKey INTEGER PRIMARY KEY NOT NULL DEFAULT '10000000',\n" +
                    "\tTagName varchar(256) NOT NULL,\n" +
                    "\tTagDescription varchar(256),\n" +
                    "\tCreatedBy varchar(256) NOT NULL,\n" +
                    "\tCreatedTs TIMESTAMP NOT NULL,\n" +
                    "\tModifiedBy varchar(256) NOT NULL,\n" +
                    "\tModifiedTs TIMESTAMP NOT NULL\n" +
                    ")";

    public static final String CREATE_TABLE_QUERY_EDUCATION_MASTER =
            "CREATE TABLE EDUCATION_MASTER (\n" +
                    "\tEducationKey INTEGER PRIMARY KEY NOT NULL DEFAULT '100000',\n" +
                    "\tSchoolName varchar(256) NOT NULL,\n" +
                    "\tCity varchar(256) NOT NULL,\n" +
                    "\tAddress varchar(256) NOT NULL,\n" +
                    "\tType varchar(256) NOT NULL,\n" +
                    "\tInfo varchar(256) NOT NULL,\n" +
                    "\tWebsite varchar(256) NOT NULL,\n" +
                    "\tDeleteFlag BOOLEAN NOT NULL,\n" +
                    "\tCreatedBy varchar(256) NOT NULL,\n" +
                    "\tCreatedTs TIMESTAMP NOT NULL,\n" +
                    "\tModifiedBy varchar(256) NOT NULL,\n" +
                    "\tModifiedTs TIMESTAMP NOT NULL,\n" +
                    "\tLocationKey INT(10) NOT NULL,\n" +
                    "\tFOREIGN KEY(LocationKey) REFERENCES LOCATION(LocationKey)\n" +
                    ")";

    public static final String CREATE_TABLE_QUERY_WORK_MASTER =
            "CREATE TABLE WORK_MASTER (\n" +
                    "\tWorkKey INTEGER PRIMARY KEY NOT NULL DEFAULT '1000000',\n" +
                    "\tLinkedInId INT(8) NOT NULL DEFAULT '1000000' ,\n" +
                    "\tName varchar(256) NOT NULL,\n" +
                    "\tInfo varchar(256) NOT NULL,\n" +
                    "\tSpecialities varchar(256) NOT NULL,\n" +
                    "\tWebsite varchar(256) NOT NULL,\n" +
                    "\tIndustry varchar(256) NOT NULL,\n" +
                    "\tType varchar(256) NOT NULL,\n" +
                    "\tCompanySize INT(10),\n" +
                    "\tFounded INT(4),\n" +
                    "\tDeleteFlag BOOLEAN NOT NULL,\n" +
                    "\tCreatedBy varchar(256) NOT NULL,\n" +
                    "\tCreatedTs TIMESTAMP NOT NULL,\n" +
                    "\tModifiedTs TIMESTAMP NOT NULL,\n" +
                    "\tModifiedBy varchar(256) NOT NULL,\n" +
                    "\tLocationKey INT(10) NOT NULL,\n" +
                    "\tFOREIGN KEY(LocationKey) REFERENCES LOCATION(LocationKey)\n" +
                    ")";

    public static final String CREATE_TABLE_QUERY_LOCATION =
            "CREATE TABLE LOCATION (\n" +
                    "\tLocationKey INTEGER PRIMARY KEY NOT NULL DEFAULT '1000000000',\n" +
                    "\tType varchar(10) NOT NULL,\n" +
                    "\tLatitude varchar(20) NOT NULL,\n" +
                    "\tLongitude varchar(20) NOT NULL,\n" +
                    "\tDeleteFlag BOOLEAN NOT NULL,\n" +
                    "\tCreatedBy varchar(256) NOT NULL,\n" +
                    "\tCreatedTs TIMESTAMP NOT NULL,\n" +
                    "\tModifiedBy varchar(256) NOT NULL,\n" +
                    "\tModifiedTs TIMESTAMP NOT NULL\n" +
                    ")";

    public static final String CREATE_TABLE_QUERY_EVENT_TYPE_MASTER =
            "CREATE TABLE EVENT_TYPE_MASTER (\n" +
                    "\tEventTypeKey INTEGER PRIMARY KEY NOT NULL DEFAULT '1000000000',\n" +
                    "\tName varchar(256) NOT NULL,\n" +
                    "\tDefaultOC varchar(256) NOT NULL,\n" +
                    "\tDefaultImage varchar(256) NOT NULL,\n" +
                    "\tDeleteFlag BOOLEAN NOT NULL,\n" +
                    "\tCreatedBy varchar(256) NOT NULL,\n" +
                    "\tCreatedTs TIMESTAMP NOT NULL,\n" +
                    "\tModifiedBy varchar(256) NOT NULL,\n" +
                    "\tModifiedTs TIMESTAMP NOT NULL\n" +
                    ")";

    public static final String CREATE_TABLE_QUERY_USER_TAG =
            "CREATE TABLE USER_TAG (\n" +
                    "\tUserKey INTEGER NOT NULL,\n" +
                    "\tTagKey INTEGER NOT NULL,\n" +
                    "\tMentorFlag BOOLEAN NOT NULL,\n" +
                    "\tMenteeFlag BOOLEAN NOT NULL,\n" +
                    "\tDeleteFlag BOOLEAN NOT NULL,\n" +
                    "\tCreatedBy varchar(256) NOT NULL,\n" +
                    "\tCreatedTs TIMESTAMP NOT NULL,\n" +
                    "\tModifiedBy varchar(256) NOT NULL,\n" +
                    "\tModifiedTs TIMESTAMP NOT NULL,\n" +
                    "\tPRIMARY KEY (UserKey, TagKey),\n" +
                    "\tFOREIGN KEY(TagKey) REFERENCES TAG_MASTER(TagKey),\n" +
                    "\tFOREIGN KEY(UserKey) REFERENCES USER_PROFILE(UserKey)\n" +
                    ")";

    public static final String CREATE_TABLE_QUERY_USER_PARTICIPANTS =
            "CREATE TABLE USER_PARTICIPANTS (\n" +
                    "\tUserKey INTEGER NOT NULL DEFAULT 0,\n" +
                    "\tEventKey INTEGER NOT NULL DEFAULT 0,\n" +
                    "\tMentorFlag BOOLEAN NOT NULL DEFAULT 0,\n" +
                    "\tMenteeFlag BOOLEAN NOT NULL DEFAULT 0,\n" +
                    "\tParticipantStatus varchar(256) NOT NULL DEFAULT 3,\n" +
                    "\tFOREIGN KEY(EventKey) REFERENCES USER_EVENT(EventKey),\n" +
                    "\tFOREIGN KEY(UserKey) REFERENCES USER_PROFILE(UserKey)\n" +
                    ")";

/*    ParticipantStatus : 0 --> yes ,
                        1 --> intrested ,
                        2 --> no,
                        3 --> informed*/

    public static final String CREATE_TABLE_QUERY_USER_EDUCATION =
            "CREATE TABLE USER_EDUCATION (\n" +
                    "\tEducationKey INTEGER NOT NULL,\n" +
                    "\tUserKey INT NOT NULL,\n" +
                    "\tYearStart INT NOT NULL,\n" +
                    "\tYearFinish INT NOT NULL,\n" +
                    "\tDegree varchar(10) NOT NULL,\n" +
                    "\tFieldOfStudy varchar(256) NOT NULL,\n" +
                    "\tGrade varchar(10) NOT NULL,\n" +
                    "\tActivities varchar(256) NOT NULL,\n" +
                    "\tDescription varchar(256) NOT NULL,\n" +
                    "\tDeleteFlag BOOLEAN NOT NULL DEFAULT 0,\n" +
                    "\tCreatedBy varchar(256) NOT NULL,\n" +
                    "\tCreatedTs TIMESTAMP NOT NULL,\n" +
                    "\tModifiedBy varchar(256) NOT NULL,\n" +
                    "\tModifiedTs TIMESTAMP NOT NULL,\n" +
                    "\tPRIMARY KEY (UserKey, EducationKey),\n" +
                    "\tFOREIGN KEY(EducationKey) REFERENCES EDUCATION_MASTER(EducationKey),\n" +
                    "\tFOREIGN KEY(UserKey) REFERENCES USER_PROFILE(UserKey)\n" +
                    ")";

    public static final String CREATE_TABLE_QUERY_USER_WORK =
            "CREATE TABLE USER_WORK (\n" +
                    "\tUserKey INTEGER NOT NULL,\n" +
                    "\tWorkKey INTEGER NOT NULL,\n" +
                    "\tWorkTitle varchar(25) NOT NULL,\n" +
                    "\tLocationKey INT NOT NULL,\n" +
                    "\tYearStart INT NOT NULL,\n" +
                    "\tYearFinish INT NOT NULL,\n" +
                    "\tDescription varchar(256) NOT NULL,\n" +
                    "\tDeleteFlag BOOLEAN NOT NULL DEFAULT 0,\n" +
                    "\tCreatedBy varchar(256) NOT NULL,\n" +
                    "\tCreatedTs TIMESTAMP NOT NULL,\n" +
                    "\tModifiedBy varchar(256) NOT NULL,\n" +
                    "\tModifiedTs TIMESTAMP NOT NULL,\n" +
                    "\tPRIMARY KEY (UserKey, WorkKey),\n" +
                    "\tFOREIGN KEY(LocationKey) REFERENCES LOCATION(LocationKey),\n" +
                    "\tFOREIGN KEY(WorkKey) REFERENCES WORK_MASTER(WorkKey),\n" +
                    "\tFOREIGN KEY(UserKey) REFERENCES USER_PROFILE(UserKey)\n" +
                    ")";

    public static final String CREATE_TABLE_QUERY_USER_EVENT =
            "CREATE TABLE USER_EVENT (\n" +
                    "\tEventKey INTEGER PRIMARY KEY NOT NULL DEFAULT 'null',\n" +
                    "\tEventTypeKey INT NOT NULL,\n" +
                    "\tTagKey INT(8) NOT NULL,\n" +
                    "\tLocationKey INT(8) NOT NULL,\n" +
                    "\tDescription varchar(256) NOT NULL,\n" +
                    "\tEventStartTs TIMESTAMP NOT NULL,\n" +
                    "\tEventEndTs TIMESTAMP NOT NULL,\n" +
                    "\tAddress varchar(256) NOT NULL,\n" +
                    "\tisOpen BOOLEAN NOT NULL DEFAULT 0,\n" +
                    "\tImageKey BLOB NOT NULL,\n" +
                    "\tDeleteFlag BOOLEAN NOT NULL DEFAULT 0,\n" +
                    "\tCreatedBy varchar(256) NOT NULL,\n" +
                    "\tCreatedTs TIMESTAMP NOT NULL,\n" +
                    "\tModifiedBy varchar(256) NOT NULL,\n" +
                    "\tModifiedTs TIMESTAMP NOT NULL,\n" +
                    "\tEventTitle varchar(256) NOT NULL,\n" +
                    "\tUserKey INTEGER NOT NULL,\n" +
                    "\tFOREIGN KEY(UserKey) REFERENCES USER_PROFILE(UserKey),\n" +
                    "\tFOREIGN KEY(EventTypeKey) REFERENCES EVENT_TYPE_MASTER(EventTypeKey),\n" +
                    "\tFOREIGN KEY(TagKey) REFERENCES TAG_MASTER(TagKey),\n" +
                    "\tFOREIGN KEY(LocationKey) REFERENCES LOCATION(LocationKey )\n" +
                    ")";


    static final String USER_KEY = "UserKey";
    static final String EVENT_TYPE_KEY = "EventTypeKey";
    static final String TAG_KEY = "TagKey";
    static final String LOCATION_KEY = "LocationKey";
    static final String DESCRIPTION = "Description";
    static final String EVENT_TITLE = "EventTitle";
    static final String EVENT_START_TIMESTAMP = "EventStartTs";
    static final String EVENT_END_TIMESTAMP = "EventEndTs";
    static final String TAG_NAME = "TagName";
    static final String TAG_DESCRIPTION = "TagDescription";
    static final String CREATED_BY = "CreatedBy";
    static final String CREATED_TIMESTAMP = "CreatedTs";
    static final String MODIFIED_BY = "ModifiedBy";
    static final String MODIFIED_TIMESTAMP = "ModifiedTs";
    static final String SCHOOL_NAME = "SchoolName";
    static final String CITY = "City";
    static final String ADDRESS = "Address";
    static final String TYPE = "Type";
    static final String INFO = "Info";
    static final String WEBSITE = "Website";
    static final String DELETE_FLAG = "DeleteFlag";
    static final String LINKED_IN_ID = "LinkedInId";
    static final String NAME = "Name";
    static final String SPECIALITIES = "Specialities";
    static final String INDUSTRY = "Industry";
    static final String COMPANY_SIZE = "CompanySize";
    static final String FOUNDED = "Founded";
    static final String DEFAULT_OC = "DefaultOC";
    static final String DEFAULT_IMAGE = "DefaultImage";
    static final String EMAIL_ID = "EmailID";
    static final String PASSWORD = "Password";
    static final String IS_OPEN = "isOpen";
    static final String IMAGE_KEY = "ImageKey";
    static final String FIRST_NAME = "FirstName";
    static final String LAST_NAME = "LastName";
    static final String TITLE_DISPLAY = "TitleDisplay";
    static final String COMPANY_DISPLAY = "CompanyDisplay";
    static final String PROFILE_PIC = "ProfilePic";
    static final String LATITUDE = "Latitude";
    static final String LONGITUDE = "Longitude";
    static final String MENTOR_FLAG = "MentorFlag";
    static final String MENTEE_FLAG = "MenteeFlag";
    static final String EDUCATION_KEY = "EducationKey";
    static final String YEAR_START = "YearStart";
    static final String YEAR_FINISH = "YearFinish";
    static final String DEGREE = "Degree";
    static final String FIELD_OF_STUDY = "FieldOfStudy";
    static final String GRADE = "Grade";
    static final String ACTIVITIES = "Activities";
    static final String EVENT_KEY = "EventKey";
    static final String PARTICIPANT_STATUS = "ParticipantStatus";
    static final String MENTEE = "Mentee";
    static final String MENTOR = "Mentor";
    static final String PARTICIPANT = "Participant";
    static final String JAVA = "Java";
    static final String C = "C";
    static final String C_PLUS_PLUS = "C++";
    static final String PYTHON = "Python";
    static final String DOT_NET = ".NET";
    static final String PHOTOGRAPHY = "Photography";
    static final String SKILING = "Skiling";
    static final String FITNESS = "Fitness";
    static final String HIKING = "Hiking";
    static final String SOCCER = "Soccer";
    static final String ANALYTICS = "Analytics";
    static final String JSP = "Jsp";
    static final String JAVA_SCRIPT = "JavaScript";
    static final String GHS_SCHOOL = "GHS School";
    static final String MENTORSHIP = "Mentorship";
    static final String ALUMNI_MEET = "Alumni Meet";
    static final String LUNCH_MEETUP = "Lunch Meetup";
    static final String COFFEE_CONNECT = "Coffee Connect";
    static final String FUN = "Fun";


    static final String GET_EVENT_TYPE = "SELECT * FROM " + TABLE_NAME_EVENT_TYPE_MASTER;

}
