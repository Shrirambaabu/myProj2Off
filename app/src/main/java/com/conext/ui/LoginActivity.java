package com.conext.ui;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.conext.MainActivity;
import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.db.USER_PROFILE;
import com.conext.utils.Prefs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.conext.utils.Constants.BaseUri;
import static com.conext.utils.Utility.getDateTime;
import static com.conext.utils.Utility.showDialogue;

/**
 * Created by Ashith VL on 6/4/2017.
 */

public class LoginActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 123;
    private AppCompatEditText editName, editPassword;
    private Button login, register;
    private SQLiteDBHelper sqLiteDBHelper;
    private SwitchCompat devModeSwitchCompat;
    private long userKey;
    private String URL = BaseUri + "/loginService/login";

    private ProgressDialog pDialog;
    private String uName, password;
    private boolean serverError = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        sqLiteDBHelper = new SQLiteDBHelper(LoginActivity.this);
        //Hides the Action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        checkAndRequestPermissions();

        //sqLiteDBHelper.deleteEvents();
        if (Prefs.getFirstTime() == null) {
            sqLiteDBHelper.insertDataFromCSV();
            Prefs.setFirstTime("1");
        }

        addressingView();

        devModeSwitchCompat.setOnCheckedChangeListener(this);
        devModeSwitchCompat.setChecked(true);

        sqLiteDBHelper = new SQLiteDBHelper(LoginActivity.this);

        addingListener();



        /*try {
            writeToSD(LoginActivity.this);
            Log.e("WriteLog", "Success");
        } catch (IOException e) {
            Log.e("WriteLog", "" + e.getMessage());
            e.printStackTrace();
        }*/

    }

    private boolean checkAndRequestPermissions() {
        int permissionCAMERA = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionCAMERA != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }

        return true;
    }

    private void addressingView() {

        editName = (AppCompatEditText) findViewById(R.id.edt_username);
        editPassword = (AppCompatEditText) findViewById(R.id.edt_password);

        login = (Button) findViewById(R.id.btn_login);
        register = (Button) findViewById(R.id.btn_linkedin_reg);

        devModeSwitchCompat = (SwitchCompat) findViewById(R.id.dev_mode);
    }

    private void addingListener() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openLogin();
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openRegister();
            }
        });

    }

    private void openRegister() {

        Intent register = new Intent(this, CredentialRegisterActivity.class);
        startActivity(register);
        /*
        Intent intent = new Intent(getApplicationContext(), LinkedInRegActivity.class);
        //startActivity(intent);
        startActivity(intent);*/

    }

    private void openLogin() {


        uName = editName.getText().toString().trim();
        password = editPassword.getText().toString().trim();

        USER_PROFILE user_profile = new USER_PROFILE(uName, password, getDateTime());

        if (Prefs.getEditMode()) {
            userKey = sqLiteDBHelper.login(user_profile);
            serverError = false;
        } else {
            Log.e("tag", "not implemented");
            loginUsingWs();
        }

        if (userKey != 0) {
            Prefs.setUserKey(String.valueOf(userKey));
            Intent login = new Intent(this, MainActivity.class);
            startActivity(login);
        } else {
            if (!serverError) {
                //I am showing Alert Dialog Box here for alerting user about wrong credentials
                final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("Please enter valid credentials!!!");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            } else {
                showDialogue(LoginActivity.this, "Sorry! Server Error", "Oops!!!");
            }
        }
    }

    private void loginUsingWs() {

        pDialog = new ProgressDialog(LoginActivity.this, R.style.MyThemeProgress);
        pDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Large);
        pDialog.onBackPressed();
        pDialog.show();

        serverError = true;

        //Requests the data from webservice using volley
        StringRequest request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                pDialog.dismiss();
                if (!s.equals("false")) {
                    userKey = Long.parseLong(s);
                    serverError = false;
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                pDialog.dismiss();
                /**
                 *  Returns error message when,
                 *  server is down,
                 *  incorrect IP
                 *  Server not deployed
                 */
                serverError = true;
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> parameters = new HashMap<String, String>();
                parameters.put("email", uName);
                parameters.put("password", password);
                return parameters;
            }
        };


        int MY_SOCKET_TIMEOUT_MS = 3000;//3 seconds - change to what you want
        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_SOCKET_TIMEOUT_MS,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        // Adding request to request queue
        RequestQueue rQueue = Volley.newRequestQueue(LoginActivity.this);
        rQueue.add(request);
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.dev_mode:
                if (!isChecked) {
                    Toast.makeText(LoginActivity.this, "Developer mode is off!!", Toast.LENGTH_SHORT).show();

                    Prefs.setEditMode(false);

                } else {
                    Toast.makeText(LoginActivity.this, "Developer mode is on!!", Toast.LENGTH_SHORT).show();

                    Prefs.setEditMode(true);

                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {

            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                    Toast.makeText(LoginActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                if (grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //Permission Granted Successfully. Write working code here.
                } else {
                    //You did not accept the request can not use the functionality.
                    Toast.makeText(LoginActivity.this, "Permission denied to Camera Access", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}