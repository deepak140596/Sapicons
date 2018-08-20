package com.sapicons.deepak.tbd.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sapicons.deepak.tbd.Objects.AccountItem;
import com.sapicons.deepak.tbd.Objects.CustomerItem;
import com.sapicons.deepak.tbd.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Deepak Prasad on 04-08-2018.
 */

public class AccountItemAdapter extends ArrayAdapter<AccountItem> {
    int i ;
    public AccountItemAdapter(@NonNull Context context, int resource, @NonNull List<AccountItem> objects,int i) {

        super(context, resource, objects);
        this.i = i;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_account,parent, false);

        CircleImageView customerPic = convertView.findViewById(R.id.item_account_customer_pic_iv);
        TextView customerNameTv = convertView.findViewById(R.id.item_account_customer_name),
                accTypeTv = convertView.findViewById(R.id.item_account_type_tv),
                startDateTv = convertView.findViewById(R.id.item_account_start_date_tv),
                endDateTv = convertView.findViewById(R.id.item_account_end_date_tv),
                dueAmountTv = convertView.findViewById(R.id.item_account_due_amount_tv),
                accNoTv = convertView.findViewById(R.id.item_account_acc_no_tv);

        AccountItem accountItem = getItem(position);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM d, yyyy");
        long s = Long.parseLong(accountItem.getStartDate());
        long e = Long.parseLong(accountItem.getEndDate());
        calendar.setTimeInMillis(s);
        startDateTv.setText(dateFormatter.format(calendar.getTime()));
        calendar.setTimeInMillis(e);
        endDateTv.setText(dateFormatter.format(calendar.getTime()));


        if(accountItem.getCustomerPicUrl().length()>0)
            Glide.with(getContext()).load(accountItem.getCustomerPicUrl()).into(customerPic);
        customerNameTv.setText(accountItem.getFirstName()+" "+accountItem.getLastName());
        accTypeTv.setText(accountItem.getAccoutType());

        dueAmountTv.setText(accountItem.getDueAmt());

        Log.d("TAG","I: "+i);

        if(i==1) {
            accNoTv.setText("Acc/No: "+accountItem.getAccountNumber());
            accNoTv.setVisibility(View.VISIBLE);
        }
        else
            accNoTv.setVisibility(View.GONE);


        return convertView;

    }
}
