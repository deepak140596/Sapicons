package com.sapicons.deepak.tbd.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.sapicons.deepak.tbd.AddCustomerActivity;
import com.sapicons.deepak.tbd.AddExpenseActivity;
import com.sapicons.deepak.tbd.CollectActivity;
import com.sapicons.deepak.tbd.DisplayCustomerListActivity;
import com.sapicons.deepak.tbd.R;
import com.sapicons.deepak.tbd.TodaysDueActivity;

import at.markushi.ui.CircleButton;
import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Deepak Prasad on 25-07-2018.
 */

public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.fragment_dashboard,container,false);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Dashboard");

        initialiseUI(view);

    }

    private void initialiseUI(View view){

       FancyButton addCustomerBtn = view.findViewById(R.id.frag_dash_add_customer_btn),
               addAccountBtn = view.findViewById(R.id.frag_dash_add_account_btn),
               collectBtn = view.findViewById(R.id.frag_dash_collect_btn),
               addExpenseBtn = view.findViewById(R.id.frag_dash_add_expense_btn),
                dueTodayBtn = view.findViewById(R.id.frag_dash_due_today_btn);

       addCustomerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddCustomerActivity.class));
           }
        });

       addAccountBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               Intent intent = new Intent(getActivity(), DisplayCustomerListActivity.class);
               startActivity(intent);
           }
       });

       collectBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(getActivity(), CollectActivity.class));
           }
       });

       addExpenseBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               startActivity(new Intent(getActivity(), AddExpenseActivity.class));
           }
       });

       dueTodayBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {

               startActivity(new Intent(getActivity(),TodaysDueActivity.class));
           }
       });


    }


}
