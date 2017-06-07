package com.fatburner.fatburner;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Menu {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Implements menu visibility
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_main, null, false);
        mDrawerLayout.addView(contentView, 0);


        final Button startButton = (Button) findViewById(R.id.start_btn);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                /*
                if(startButton.getText().equals("Start"))
                {startButton.setText("Done");}
                else
                {startButton.setText("Start");}
                */

                    Intent intent = new Intent(MainActivity.this, MyPrograms.class);
                    startActivity(intent);

            }
        });


    }

}


