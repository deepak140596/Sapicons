package com.sapicons.deepak.tbd.Adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sapicons.deepak.tbd.Objects.AccountItem;
import com.sapicons.deepak.tbd.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Deepak Prasad on 21-08-2018.
 */

public class ClosedAccountAdapter extends ArrayAdapter<AccountItem> {

    public ClosedAccountAdapter(@NonNull Context context, int resource, @NonNull List<AccountItem> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null)
            convertView = ((Activity)getContext()).getLayoutInflater().inflate(R.layout.item_closed_account,parent, false);

        CircleImageView customerPic = convertView.findViewById(R.id.item_closed_account_customer_pic_iv);
        TextView customerNameTv = convertView.findViewById(R.id.item_closed_account_customer_name),
                accTypeTv = convertView.findViewById(R.id.item_closed_account_type_tv),
                closedDateTv = convertView.findViewById(R.id.item_closed_account_closed_date_tv),
                amountTv = convertView.findViewById(R.id.item_closed_account_amount_tv),
                accNoTv = convertView.findViewById(R.id.item_closed_account_acc_no_tv);

        AccountItem accountItem = getItem(position);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM d, yyyy");
        long e = Long.parseLong(accountItem.getEndDate());
        calendar.setTimeInMillis(e);


        closedDateTv.setText("Closed on: "+dateFormatter.format(calendar.getTime()));
        if(accountItem.getCustomerPicUrl().length()>0)
            Glide.with(getContext()).load(accountItem.getCustomerPicUrl()).into(customerPic);
        customerNameTv.setText(accountItem.getFirstName()+" "+accountItem.getLastName());
        accTypeTv.setText(accountItem.getAccoutType());
        amountTv.setText(accountItem.getDueAmt());
        accNoTv.setText("Acc/No: "+accountItem.getAccountNumber());

        return convertView;

    }
}
