package com.fatburner.fatburner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.util.Log;

import static com.fatburner.fatburner.GlobalVariables.PRODUCTS_PAGES_COUNT;
import static com.fatburner.fatburner.GlobalVariables.PRODUCTS;

public class Products extends FragmentActivity {

    static final String TAG = "myLogs";
    //static final int PAGES_COUNT = 5;

    ViewPager pager;
    PagerAdapter pagerAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_list);

        pager = (ViewPager) findViewById(R.id.pager);
        pagerAdapter = new ProductsListPagerAdapter(getSupportFragmentManager());
        pager.setAdapter(pagerAdapter);

        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                Log.d(TAG, "onPageSelected, position = " + position);
            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}

class ProductsListPagerAdapter extends FragmentPagerAdapter {

    //private static final int PAGES_COUNT = 6;

    public ProductsListPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        String product = "";
        switch (position){
            case 0:
                product = PRODUCTS[0];
                break;
            case 1:
                product = PRODUCTS[1];
                break;
            case 2:
                product = PRODUCTS[2];
                break;
            case 3:
                product = PRODUCTS[3];
                break;
            case 4:
                product = PRODUCTS[4];
                break;
            case 5:
                product = PRODUCTS[5];
                break;
        }
        return product;
    }

    @Override
    public Fragment getItem(int position) {
        return ProductsPageFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return PRODUCTS_PAGES_COUNT;
    }

}

