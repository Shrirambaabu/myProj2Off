package com.conext.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.db.USER_PROFILE;
import com.conext.utils.Prefs;

import static com.conext.utils.Utility.getDateTime;

public class CredentialRegisterActivity extends AppCompatActivity {

    private AppCompatEditText edit_username, edit_password, edit_conform_password, edit_first, edit_last, edit_company;
    private Button btn_login_sign_in;
    private SQLiteDBHelper sqLiteDBHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Hides the Action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();

        setContentView(R.layout.activity_credential_register);

        sqLiteDBHelper = new SQLiteDBHelper(CredentialRegisterActivity.this);

        addressingView();

        addingListener();


    }

    private void addingListener() {

        btn_login_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        edit_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (sqLiteDBHelper.checkUser(s.toString())) {
                    //Alert dialog after clicking the Register Account
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CredentialRegisterActivity.this);
                    builder.setTitle("Information");
                    builder.setMessage("Email Id Already Exits");
                    builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //Finishing the dialog and removing Activity from stack.
                            dialogInterface.dismiss();
                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
    }

    private void login() {
        if (!sqLiteDBHelper.checkUser(edit_username.getText().toString())) {
            if (edit_password.getText().toString().equals(edit_conform_password.getText().toString())
                    && !edit_password.getText().toString().trim().isEmpty() && !edit_conform_password.getText().toString().trim().isEmpty()) {
                USER_PROFILE user = new USER_PROFILE();

                user.setEmailID(edit_username.getText().toString());
                user.setPassword(edit_conform_password.getText().toString());
                user.setFirstName(edit_first.getText().toString());
                user.setLastName(edit_last.getText().toString());
                user.setTitleDisplay(edit_first.getText().toString() + " " + edit_last.getText().toString());
                user.setCompanyDisplay(edit_company.getText().toString());
                user.setProfilePic("");
                user.setDeleteFlg("0");
                user.setCreatedBy("");
                user.setCreatedTs(getDateTime());
                user.setModifiedBy("");
                user.setModifiedTs(getDateTime());
                user.setLocationKey("");

                final long uId = sqLiteDBHelper.register(user);

                if (uId != 0) {
                    final Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {
                            Intent intent = new Intent(CredentialRegisterActivity.this, SuccessPopUp.class);
                            intent.putExtra("key", uId);
                            Log.e("tag", "" + uId);
                            Prefs.setUserKey(String.valueOf(uId));
                            startActivity(intent);
                        }
                    };

                    handler.postDelayed(r, 1000);
                } else {

                    //I am showing Alert Dialog Box here for alerting user about wrong credentials
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CredentialRegisterActivity.this);
                    builder.setTitle("Alert");
                    builder.setMessage("Something Went Wrong!!!.Please Try Again Later");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            dialogInterface.dismiss();

                        }
                    });

                    AlertDialog dialog = builder.create();
                    dialog.show();
                }

            } else {
                //I am showing Alert Dialog Box here for alerting user about wrong credentials
                final AlertDialog.Builder builder = new AlertDialog.Builder(CredentialRegisterActivity.this);
                builder.setTitle("Alert");
                builder.setMessage("Password and Confirm Password doesn't match.");
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.dismiss();

                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } else {
            //Alert dialog after clicking the Register Account
            final AlertDialog.Builder builder = new AlertDialog.Builder(CredentialRegisterActivity.this);
            builder.setTitle("Information");
            builder.setMessage("Email Id Already Exits");
            builder.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Finishing the dialog and removing Activity from stack.
                    dialogInterface.dismiss();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void addressingView() {
        edit_username = (AppCompatEditText) findViewById(R.id.edt_username);
        edit_password = (AppCompatEditText) findViewById(R.id.edt_password);
        edit_conform_password = (AppCompatEditText) findViewById(R.id.edt_conform_password);
        edit_first = (AppCompatEditText) findViewById(R.id.edt_first);
        edit_last = (AppCompatEditText) findViewById(R.id.edt_last_name);
        edit_company = (AppCompatEditText) findViewById(R.id.edt_Company_name);


        btn_login_sign_in = (Button) findViewById(R.id.btn_login_sign_in);

    }


}
