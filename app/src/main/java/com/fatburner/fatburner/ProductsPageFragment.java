package com.fatburner.fatburner;

/**
 * Created by sete on 6/15/2017.
 */

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import static com.fatburner.fatburner.GlobalVariables.PRODUCTS;
import static com.fatburner.fatburner.GlobalVariables.dietListViewMode;


public class ProductsPageFragment extends Fragment{

    ArrayAdapter<CharSequence> adapter = null;

    ImageButton applyButton;
    TextView product1;
    TextView product2;
    TextView product3;
    TextView product4;
    TextView textView15;
    ListView lvMain;
    String[] names;

    LinkedList<Integer> queue = new LinkedList<Integer>();
    int keyToRemove;


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

        final int maxItemsAllowed = 4;
        lvMain.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String productsList[] = {" "," "," "," "};

                if (lvMain.isItemChecked(position)) {

                    if(queue.size() < 1)
                    {queue.add(position);}
                    else{
                        if(!queue.contains(position))
                        {
                            queue.add(position);
                        }
                    }

                    if (queue.size() > maxItemsAllowed) {

                        lvMain.setItemChecked(queue.get(0), false);

                        for (int i = 0; i < queue.size() - 1; i++) {
                            queue.set(i, queue.get(i + 1));
                        }

                        queue.remove(maxItemsAllowed);
                    }
                }

                LinkedList<String> products = new LinkedList<String>();
                SparseBooleanArray sbArray = lvMain.getCheckedItemPositions();
                for (int i = 0; i < sbArray.size(); i++)
                {
                    int key = sbArray.keyAt(i);
                    if (sbArray.get(key))
                    {
                        String itemValue = (String) lvMain.getItemAtPosition(key);
                        products.add(itemValue);
                    }
                }

                    for (int i = 0; i < products.size(); i++) {
                        productsList[i] = products.get(i);
                    }

                    product1.setText(productsList[0]);
                    product2.setText(productsList[1]);
                    product3.setText(productsList[2]);
                    product4.setText(productsList[3]);
                }
        });

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

    /*
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
    */

    private void loadLists(int pageNumber, View view){
        String list[] = null;
        // устанавливаем режим выбора пунктов списка
        lvMain.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        // Создаем адаптер, используя массив из файла ресурсов
        //"П", "К", "М", "Ф", "О", "Ж"
        switch (PRODUCTS[pageNumber]){
            case "П":
                // получаем массив из файла ресурсов
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.meat_fish_eggs, listLayout);
                list = getResources().getStringArray(R.array.meat_fish_eggs);
                break;
            case "К":
                // получаем массив из файла ресурсов
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.grain, listLayout);
                list = getResources().getStringArray(R.array.grain);
                break;
            case "М":
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.milk, listLayout);
                list = getResources().getStringArray(R.array.milk);
                break;
            case "Ф":
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.fruits, listLayout);
                list = getResources().getStringArray(R.array.fruits);
                break;
            case "О":
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.nuts, listLayout);
                list = getResources().getStringArray(R.array.nuts);
                break;
            case "Ж":
                adapter = ArrayAdapter.createFromResource(getActivity(), R.array.oils, listLayout);
                list = getResources().getStringArray(R.array.oils);
                break;
            // etc.
        }

        lvMain.setAdapter(adapter);
    }


    }

