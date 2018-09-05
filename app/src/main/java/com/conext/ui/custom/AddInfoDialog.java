package com.conext.ui.custom;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;

import com.conext.R;
import com.conext.db.SQLiteDBHelper;
import com.conext.model.db.EDUCATION_MASTER;

import java.util.ArrayList;

public class AddInfoDialog extends Activity implements View.OnClickListener {

    private EditText majorSplAppCompatEditText, eduAppCompatEditText, yearFromAppCompatEditText, yearTillAppCompatEditText;
    private Button addBtn, cancelBtn;
    private AutoCompleteTextView univAppCompatEditText;

    ArrayList<String> university;
    ArrayList<EDUCATION_MASTER> education_masterArrayList;
    ArrayAdapter<String> adapter;
    String selectedItemText;

    SQLiteDBHelper sqLiteDBHelper;
    private String index;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_info_dialog);

        sqLiteDBHelper = new SQLiteDBHelper(AddInfoDialog.this);
        university = new ArrayList<>();
        selectedItemText = null;
        education_masterArrayList = new ArrayList<>();
        //mapping to view object
        mapping();
        //settingListners
        settingListeners();

    }

    private void settingListeners() {
        addBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    private void mapping() {

        majorSplAppCompatEditText = (EditText) findViewById(R.id.major_spl);
        eduAppCompatEditText = (EditText) findViewById(R.id.edu);
        univAppCompatEditText = (AutoCompleteTextView) findViewById(R.id.univ);
        yearFromAppCompatEditText = (EditText) findViewById(R.id.start_year);
        yearTillAppCompatEditText = (EditText) findViewById(R.id.end_year);
        addBtn = (Button) findViewById(R.id.add);
        cancelBtn = (Button) findViewById(R.id.cancel);

        education_masterArrayList = sqLiteDBHelper.getEducation();

        if (education_masterArrayList.size() > 0 && education_masterArrayList != null) {
            for (int i = 0; i < education_masterArrayList.size(); i++) {
                EDUCATION_MASTER education_master = education_masterArrayList.get(i);
                university.add(education_master.getSchoolName());
                Log.e("tag", education_master.getSchoolName());
            }
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, university);
        univAppCompatEditText.setAdapter(adapter);
        univAppCompatEditText.setThreshold(1);

        univAppCompatEditText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItemText = adapter.getItem(position);
                index = String.valueOf(position + 1);
                Log.e("tag", "selected item" + selectedItemText);

            }
        });

        univAppCompatEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                selectedItemText = null;
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.add) {
            if (!majorSplAppCompatEditText.getText().toString().equals("") && !eduAppCompatEditText.getText().toString().equals("") &&
                    !univAppCompatEditText.getText().toString().equals("") && selectedItemText != null && !yearFromAppCompatEditText.getText().toString().equals("") &&
                    !yearTillAppCompatEditText.getText().toString().equals("") && yearFromAppCompatEditText.getText().length() == 4
                    && yearTillAppCompatEditText.getText().length() == 4) {

                Intent intent = new Intent();
                intent.putExtra("major", majorSplAppCompatEditText.getText().toString());
                intent.putExtra("edu", eduAppCompatEditText.getText().toString());
                intent.putExtra("uni", selectedItemText);
                intent.putExtra("univ", index);
                intent.putExtra("year_start", yearFromAppCompatEditText.getText().toString());
                intent.putExtra("year_end", yearTillAppCompatEditText.getText().toString());
                setResult(Activity.RESULT_OK, intent);
                finish();
            } else if (!eduAppCompatEditText.getText().toString().equals("")) {
                eduAppCompatEditText.requestFocus();
            } else if (!univAppCompatEditText.getText().toString().equals("")) {
                univAppCompatEditText.requestFocus();
            } else if (selectedItemText == null) {
                univAppCompatEditText.requestFocus();
            } else if (!yearFromAppCompatEditText.getText().toString().equals("") && yearFromAppCompatEditText.getText().length() == 4) {
                yearFromAppCompatEditText.requestFocus();
            } else if (!yearTillAppCompatEditText.getText().toString().equals("") && yearTillAppCompatEditText.getText().length() == 4) {
                yearTillAppCompatEditText.requestFocus();
            }
        } else if (v.getId() == R.id.cancel) {
            finish();
        }
    }
}