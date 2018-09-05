package com.conext.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Environment;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by sunil on 24-03-2016.
 */
public class Utility {

    private static final String TAG = Utility.class.getSimpleName();

    /**
     * Method that generates the url to get the access token from the Service
     *
     * @return Url
     */
    public static String getAccessTokenUrl(String authorizationToken) {
        String accessTokenUrl = Constants.ACCESS_TOKEN_URL
                + Constants.QUESTION_MARK
                + Constants.GRANT_TYPE_PARAM + Constants.EQUALS + Constants.GRANT_TYPE
                + Constants.AMPERSAND
                + Constants.RESPONSE_TYPE_VALUE + Constants.EQUALS + authorizationToken
                + Constants.AMPERSAND
                + Constants.CLIENT_ID_PARAM + Constants.EQUALS + Constants.APP_CLIENT_ID
                + Constants.AMPERSAND
                + Constants.REDIRECT_URI_PARAM + Constants.EQUALS + Constants.REDIRECT_URI
                + Constants.AMPERSAND
                + Constants.SECRET_KEY_PARAM + Constants.EQUALS + Constants.APP_CLIENT_SECRET_KEY;

        Log.d(TAG, "AccessToken URL ==> " + accessTokenUrl);

        return accessTokenUrl;
    }

    /**
     * Method that generates the url for get the authorization token from the Service
     *
     * @return Url
     */
    public static String getAuthorizationUrl() {
        String authorizationUrl = Constants.AUTHORIZATION_URL
                + Constants.QUESTION_MARK + Constants.RESPONSE_TYPE_PARAM + Constants.EQUALS + Constants.RESPONSE_TYPE_VALUE
                + Constants.AMPERSAND + Constants.CLIENT_ID_PARAM + Constants.EQUALS + Constants.APP_CLIENT_ID
                + Constants.AMPERSAND + Constants.SCOPE_PARAM + Constants.EQUALS + Constants.SCOPES
                + Constants.AMPERSAND + Constants.STATE_PARAM + Constants.EQUALS + Constants.STATE
                + Constants.AMPERSAND + Constants.REDIRECT_URI_PARAM + Constants.EQUALS + Constants.REDIRECT_URI;

        Log.d(TAG, "Authorization URL ==> " + authorizationUrl);

        return authorizationUrl;
    }

    public static Retrofit getLinkedInAPIRetrofitInstance() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.LINKEDIN_API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit;
    }

    public static boolean isValidEmailAddress(String emailAddr) {
        return emailAddr != null && Patterns.EMAIL_ADDRESS.matcher(emailAddr).matches();
    }

    public static String getImageNameBasedOnCurrTime() {
        Calendar calendar = Calendar.getInstance();

        StringBuffer currDateTime = new StringBuffer();
        currDateTime.append(calendar.get(Calendar.DAY_OF_MONTH) + "-");
        currDateTime.append((calendar.get(Calendar.MONTH)) + 1 + "-");
        currDateTime.append(calendar.get(Calendar.YEAR) + "-");
        currDateTime.append(calendar.get(Calendar.HOUR) + "-");
        currDateTime.append(calendar.get(Calendar.MINUTE) + "-");
        currDateTime.append(calendar.get(Calendar.SECOND));

        return currDateTime.toString();
    }

    public static String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    // convert from bitmap to byte array
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getPhoto(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    public static String getAddress(Context context, Location location) {
        String address = null;
        String city = null;
        String state = null;
        if (location != null) {

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(context);

            try {
                addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1); // Here 1 represent max setLocation result to returned, by documents it recommended 1 to 5

                address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                city = addresses.get(0).getLocality();
                state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (city == null) {
            city = "Address Unknown";
            if (state != null) {
                city = state;
            }
        }
        // return longitude
        return city;
    }

    public static void showGPSSettingsAlert(final Context mContext) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        // Setting Dialog Title
        alertDialog.setTitle("GPS setting");

        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(intent);
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Toast.makeText(mContext, "GPS must be enabled to proceed", Toast.LENGTH_LONG).show();
                showGPSSettingsAlert(mContext);
                // Intent successIntent =  new Intent(mContext, SuccessPopUp.class);
                // startActivity(successIntent);
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public static void animateFab(final int position, final FloatingActionButton fab) {

        fab.clearAnimation();
        // Scale down animation
        ScaleAnimation shrink = new ScaleAnimation(1f, 0.2f, 1f, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        shrink.setDuration(150);     // animation duration in milliseconds
        shrink.setInterpolator(new DecelerateInterpolator());
        shrink.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // Scale up animation
                ScaleAnimation expand = new ScaleAnimation(0.2f, 1f, 0.2f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                expand.setDuration(100);     // animation duration in milliseconds
                expand.setInterpolator(new AccelerateInterpolator());
                fab.startAnimation(expand);
                fab.show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(shrink);
    }


    public static boolean isColorDark(int color) {
        double darkness = 1 - (0.299 * Color.red(color) + 0.587 * Color.green(color) + 0.114 * Color.blue(color)) / 255;
        if (darkness < 0.5) {
            return false; // It's a light color
        } else {
            return true; // It's a dark color
        }
    }

    public static void writeToSD(Context context) throws IOException {
        File sd = Environment.getExternalStorageDirectory();

        if (sd.canWrite()) {
            String currentDBPath = "conext.db";
            String backupDBPath = "backupname.db";
            File currentDB = new File(context.getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator, currentDBPath);
            File backupDB = new File(sd, backupDBPath);
            if (currentDB.exists()) {
                FileChannel src = new FileInputStream(currentDB).getChannel();
                FileChannel dst = new FileOutputStream(backupDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();
            }
        }
    }

    public static void showDialogue(final Activity activity, String message, String title) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setTitle(title);
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


}
