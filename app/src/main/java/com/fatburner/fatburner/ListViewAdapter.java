package com.fatburner.fatburner;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter
{
    Activity context;
    String productName[];
    String productWeight[];
    String productCal[];

    public ListViewAdapter(Activity context, String[] productName, String[] productWeight, String[] productCal) {
        super();
        this.context = context;
        this.productName = productName;
        this.productWeight = productWeight;
        this.productCal = productCal;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return productName.length;
    }

    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    private class ViewHolder {
        TextView txtViewName;
        TextView txtViewWeight;
        TextView txtViewCal;
    }

    public View getView(int position, View convertView, ViewGroup parent)
    {
        // TODO Auto-generated method stub
        ViewHolder holder;
        LayoutInflater inflater =  context.getLayoutInflater();

        if (convertView == null)
        {
            convertView = inflater.inflate(R.layout.products_list, null);
            holder = new ViewHolder();
            holder.txtViewName = (TextView) convertView.findViewById(R.id.productsName);
            holder.txtViewWeight = (TextView) convertView.findViewById(R.id.productWeight);
            holder.txtViewCal = (TextView) convertView.findViewById(R.id.productCaloricity);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtViewName.setText(productName[position]);
        holder.txtViewWeight.setText(productWeight[position]);
        holder.txtViewCal.setText(productCal[position]);

        return convertView;
    }

}
