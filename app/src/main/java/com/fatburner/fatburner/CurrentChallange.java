package com.fatburner.fatburner;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

public class CurrentChallange extends Menu {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_current_challange, null, false);
        mDrawerLayout.addView(contentView, 0);

        EditText label = (EditText) findViewById(R.id.editText2);

        label.setText("Program selected:" + GlobalVariables.selectedProgram);

    }

}
