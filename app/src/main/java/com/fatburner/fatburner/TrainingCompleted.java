package com.fatburner.fatburner;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;

import com.github.lzyzsd.circleprogress.DonutProgress;

import static android.R.color.holo_orange_dark;

/**
 * Created by sergeyteperchuk on 6/13/17.
 */

public class TrainingCompleted extends Menu {

    DonutProgress doneBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_training_completed, null, false);
        mDrawerLayout.addView(contentView, 0);

        doneBtn = (DonutProgress) findViewById(R.id.done_btn);
        doneBtn.setText("Got It");
        doneBtn.setFinishedStrokeWidth(15);
        doneBtn.setUnfinishedStrokeWidth(15);
        doneBtn.setTextColor(R.color.OrangeDark);
        doneBtn.setFinishedStrokeColor(R.color.OrangeDark);
        startAnimation ();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                    Intent intent = new Intent(TrainingCompleted.this, MainActivity.class);
                    startActivity(intent);
            }
        });
    }

    void startAnimation () {
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(TrainingCompleted.this, R.animator.progress_anim);
        set.setInterpolator(new DecelerateInterpolator());
        set.setTarget(doneBtn);
        set.start();
    }
}
