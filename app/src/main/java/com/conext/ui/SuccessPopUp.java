package com.conext.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.conext.R;

/**
 * Created by Ashith VL on 6/4/2017.
 */

public class SuccessPopUp extends Activity implements View.OnClickListener {

    public ImageView nextBtn;
    long key;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.success);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Intent intent = getIntent();
        if (intent != null) {
            key = intent.getLongExtra("key", 0);
        }

        nextBtn = (ImageView) findViewById(R.id.nextBtn);
        nextBtn.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        Intent regPerIntent = new Intent(SuccessPopUp.this, RegistrationPersonaliseActivity.class);
        regPerIntent.putExtra("key", key);
        startActivity(regPerIntent);
    }
}
