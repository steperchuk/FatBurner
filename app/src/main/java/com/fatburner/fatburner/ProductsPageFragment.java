package com.fatburner.fatburner;

/**
 * Created by sete on 6/15/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static com.fatburner.fatburner.GlobalVariables.PRODUCTS_PAGES_COUNT;
import static com.fatburner.fatburner.GlobalVariables.dietListViewMode;


public class ProductsPageFragment extends Fragment implements View.OnClickListener {

    ArrayAdapter<CharSequence> adapter = null;

    ImageButton applyButton;
    TextView product1;
    TextView product2;
    TextView product3;
    TextView product4;
    TextView textView15;
    ListView lvMain;
    String[] names;


    static final String ARGUMENT_PAGE_NUMBER = "arg_page_number";
    static final String SAVE_PAGE_NUMBER = "save_page_number";

    int pageNumber;
    int listLayout = android.R.layout.simple_list_item_multiple_choice;

    static ProductsPageFragment newInstance(int page) {
        ProductsPageFragment pageFragment = new ProductsPageFragment();
        Bundle arguments = new Bundle();
        arguments.putInt(ARGUMENT_PAGE_NUMBER, page);
        pageFragment.setArguments(arguments);
        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pageNumber = getArguments().getInt(ARGUMENT_PAGE_NUMBER);

        int savedPageNumber = -1;
        if (savedInstanceState != null) {
            savedPageNumber = savedInstanceState.getInt(SAVE_PAGE_NUMBER);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.products_fragment, null);

        lvMain = (ListView) view.findViewById(R.id.lvSimple);
        applyButton = (ImageButton) view.findViewById(R.id.applyBtn);
        product1 = (TextView) view.findViewById(R.id.product1);
        product2 = (TextView) view.findViewById(R.id.product2);
        product3 = (TextView) view.findViewById(R.id.product3);
        product4 = (TextView) view.findViewById(R.id.product4);
        textView15 = (TextView) view.findViewById(R.id.textView15);

        /// Code which determines which list mode is need to show depending on Shared Preferences
        //true - extended (with button)
        //false -  simple (w/o buttons)
        //.setVisibility(View.GONE);
        //.setVisibility(View.VISIBLE);
        if(dietListViewMode){
            textView15.setVisibility(View.GONE);
            applyButton.setVisibility(View.GONE);
            product1.setVisibility(View.GONE);
            product2.setVisibility(View.GONE);
            product3.setVisibility(View.GONE);
            product4.setVisibility(View.GONE);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    RelativeLayout.LayoutParams.MATCH_PARENT
            );
            params.setMargins(0, 0, 0, 0); //left,top,right,bottom
            listLayout = android.R.layout.simple_list_item_1;
            lvMain.setChoiceMode(ListView.CHOICE_MODE_NONE);
            lvMain.setLayoutParams(params);
        }


        //загружаем списки
        loadLists(pageNumber, view);

        return view;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(SAVE_PAGE_NUMBER, pageNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.lvSimple:
                //code to restrict selection

                SparseBooleanArray sbArray = lvMain.getCheckedItemPositions();
                for (int i = 0; i < sbArray.size(); i++) {
                    int key = sbArray.keyAt(i);
                    if (sbArray.get(key))
                        product1.setText(names[key]);
                }

                break;
        }
    }

    private void loadLists(int pageNumber, View view){
        String list[] = null;
        // устанавливаем режим выбора пунктов списка
        lvMain.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // Создаем адаптер, используя массив из файла ресурсов

        switch (pageNumber){
            case 0: //"П"
                // получаем массив из файла ресурсов
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.meat_fish_eggs, listLayout);
                list = getResources().getStringArray(R.array.meat_fish_eggs);
                break;
            case 1: //"К"
                // получаем массив из файла ресурсов
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.grain, listLayout);
                list = getResources().getStringArray(R.array.grain);
                break;
            case 2: //"М"
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.milk, listLayout);
                list = getResources().getStringArray(R.array.milk);
                break;
            case 3: //"Ф"
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.fruits, listLayout);
                list = getResources().getStringArray(R.array.fruits);
                break;
            case 4: //"О"
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.nuts, listLayout);
                list = getResources().getStringArray(R.array.nuts);
                break;
            case 5: //"Ж"
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.oils, listLayout);
                list = getResources().getStringArray(R.array.oils);
                break;
            // etc.
        }

        lvMain.setAdapter(adapter);
    }

    }

