package com.fatburner.fatburner;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MyPrograms extends Menu {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_my_programs, null, false);
        mDrawerLayout.addView(contentView, 0);


        final ListView programmsList = (ListView) findViewById(R.id.programs_list);

        List<String> itemsArray = new ArrayList<String>();
        itemsArray.add("Home fat burner");
        itemsArray.add("Gym fat burner");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                itemsArray );

        programmsList.setAdapter(arrayAdapter);




        programmsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String stringText;

                stringText= ((TextView)view).getText().toString();

                Intent intent;
                switch(stringText){
                    case "Home fat burner":
                        GlobalVariables.selectedProgram = "Home fat burner";
                        intent = new Intent(MyPrograms.this, CurrentChallange.class);
                        startActivity(intent);
                        break;
                    case "Gym fat burner":
                        GlobalVariables.selectedProgram = "Gym fat burner";
                        intent = new Intent(MyPrograms.this, CurrentChallange.class);
                        startActivity(intent);
                        break;
                }
            }
        });


    }
}
