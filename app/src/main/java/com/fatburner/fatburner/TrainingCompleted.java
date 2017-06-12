package com.fatburner.fatburner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

/**
 * Created by sergeyteperchuk on 6/13/17.
 */

public class TrainingCompleted extends Menu {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_training_completed, null, false);
        mDrawerLayout.addView(contentView, 0);

        Button okBtn = (Button) findViewById(R.id.ok_btn);


        okBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    Intent intent = new Intent(TrainingCompleted.this, MainActivity.class);
                    startActivity(intent);
            }
        });
    }
}
