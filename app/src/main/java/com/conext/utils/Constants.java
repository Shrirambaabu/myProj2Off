package com.conext.utils;

/**
 * Created by sunil on 22-03-2016.
 */
public class Constants {
    //This is the public api key of our application
    //public static final String APP_CLIENT_ID = "7707xgx0frki24";
    public static final String APP_CLIENT_ID = "81fkih226tjoji";

    //This is the private api key of our application
    //public static final String APP_CLIENT_SECRET_KEY = "ACYmPeLLHfLBSlVR";
    public static final String APP_CLIENT_SECRET_KEY = "iW6V3lDBYsrVyjPt";

    //This is any string we want to use. This will be used for avoid CSRF attacks.
    //You can generate one here: http://strongpasswordgenerator.com/
    public static final String STATE = "E3ZYKC1T6H2yP4z";

    //This is the url that LinkedIn Auth process will redirect to. We can put whatever we want that starts with http:// or https:// .
    //We use a made up url that we will intercept when redirecting. Avoid Uppercases.
    public static final String REDIRECT_URI = "http://www.con.com";

    public static final String SCOPES = "r_basicprofile";

    //These are constants used for build the urls
    public static final String AUTHORIZATION_URL = "https://www.linkedin.com/uas/oauth2/authorization";
    public static final String ACCESS_TOKEN_URL = "https://www.linkedin.com";
    public static final String SECRET_KEY_PARAM = "client_secret";
    public static final String RESPONSE_TYPE_PARAM = "response_type";
    public static final String GRANT_TYPE_PARAM = "grant_type";
    public static final String GRANT_TYPE = "authorization_code";
    public static final String RESPONSE_TYPE_VALUE = "code";
    public static final String CLIENT_ID_PARAM = "client_id";
    public static final String SCOPE_PARAM = "scope";
    public static final String STATE_PARAM = "state";
    public static final String REDIRECT_URI_PARAM = "redirect_uri";
    /*---------------------------------------*/

    public static final String QUESTION_MARK = "?";
    public static final String AMPERSAND = "&";
    public static final String EQUALS = "=";

    //BASE URL for LinkedIn API
    public static final String LINKEDIN_API_BASE_URL = "https://api.linkedin.com";

    //Request code for Activity callback
    public static final int LINKEDIN_LOGIN_REQ_CODE = 1;

    //Max skills
    public static final int MAX_SKILLS_ALLOWED = 5;

    public static String Base = "http://192.168.43.225:8080";

    public static String BaseUri = Base + "/IgotplacedRestWebService/webapi";

}