package com.sapicons.deepak.tbd;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.sapicons.deepak.tbd.Objects.AccountItem;
import com.sapicons.deepak.tbd.Objects.CustomerItem;

import java.text.SimpleDateFormat;

public class AccountsDetailsActivity extends AppCompatActivity {

    AccountItem accountItem;
    TextView displayDueAmtLargeTv, nameTv, amountTv, actualAmtTv;
    EditText accountNoEt, interestEt,startDateEt, endDateEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accounts_details);
        setTitle("Report");

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        accountItem = (AccountItem) bundle.getSerializable("selected_account");

        initialiseViews();


    }
    public void initialiseViews(){

        displayDueAmtLargeTv = findViewById(R.id.aad_display_due_amt_big_tv);
        nameTv = findViewById(R.id.aad_display_full_name);
        amountTv= findViewById(R.id.aad_amt_tv);
        actualAmtTv= findViewById(R.id.aad_actual_amt_tv);
        accountNoEt= findViewById(R.id.aad_acnt_no_et);
        interestEt = findViewById(R.id.aad_interest_et);
        startDateEt = findViewById(R.id.aad_start_date_et);
        endDateEt = findViewById(R.id.aad_end_date_et);

        displayDueAmtLargeTv.setText(accountItem.getDueAmt());
        nameTv.setText(accountItem.getFirstName()+" "+accountItem.getLastName());
        amountTv.setText(accountItem.getAmount());
        actualAmtTv.setText(accountItem.getActualAmt());
        accountNoEt.setText(accountItem.getAccountNumber());
        interestEt.setText(accountItem.getInterestPct());

        long date = Long.parseLong(accountItem.getStartDate());
        SimpleDateFormat format = new SimpleDateFormat("MMMM d, yyyy");
        startDateEt.setText(format.format(date));

        date = Long.parseLong(accountItem.getEndDate());
        endDateEt.setText(format.format(date));

    }
}
