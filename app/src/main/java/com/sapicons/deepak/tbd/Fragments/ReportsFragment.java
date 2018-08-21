package com.sapicons.deepak.tbd.Fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sapicons.deepak.tbd.BalanceReportActivity;
import com.sapicons.deepak.tbd.ClosedAccountsActivity;
import com.sapicons.deepak.tbd.DisplayAccountsListActivity;
import com.sapicons.deepak.tbd.DisplayCustomerListActivity;
import com.sapicons.deepak.tbd.R;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Deepak Prasad on 04-08-2018.
 */

public class ReportsFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_reports,container,false);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Reports");

        initialiseUI(view);

    }

    public void initialiseUI(View view){
        FancyButton customerAccBtn = view.findViewById(R.id.frag_reports_customer_accounts_btn),
        balanceReportBtn = view.findViewById(R.id.frag_reports_balance_report_btn),
        profitStatementBtn = view.findViewById(R.id.frag_reports_profit_statement_btn),
        closedAccRepBtn = view.findViewById(R.id.frag_reports_closed_accounts_reports_btn);




        customerAccBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), DisplayAccountsListActivity.class);
                startActivity(intent);
            }
        });

        balanceReportBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), BalanceReportActivity.class));
            }
        });

        profitStatementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getActivity(), "Clicked!", Toast.LENGTH_SHORT).show();
            }
        });

        closedAccRepBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ClosedAccountsActivity.class));
            }
        });
    }
}
