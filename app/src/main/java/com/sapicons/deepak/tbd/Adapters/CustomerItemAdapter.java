package com.sapicons.deepak.tbd.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sapicons.deepak.tbd.Objects.CustomerItem;
import com.sapicons.deepak.tbd.R;

import java.util.List;

/**
 * Created by Deepak Prasad on 28-07-2018.
 */

public class CustomerItemAdapter extends ArrayAdapter<CustomerItem>{

    public CustomerItemAdapter(@NonNull Context context, int resource, @NonNull List<CustomerItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_customer,parent, false);
        ImageView customerIV = convertView.findViewById(R.id.item_customer_image);
        TextView customerNameTV = convertView.findViewById(R.id.item_customer_name);

        CustomerItem item = getItem(position);
        customerNameTV.setText(item.getFirstName().toString()+" "+item.getLastName().toString());
        if(item.getPhotoUrl().length()>0)
            Glide.with(getContext()).load(item.getPhotoUrl()).into(customerIV);

        return  convertView;
    }
}
