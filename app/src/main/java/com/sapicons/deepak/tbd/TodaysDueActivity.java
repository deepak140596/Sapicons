package com.sapicons.deepak.tbd;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sapicons.deepak.tbd.Fragments.AccountsDisplayFragment;
import com.sapicons.deepak.tbd.Fragments.CustomerDisplayFragment;

public class TodaysDueActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_due);

        startTodaysDueFragment();

    }

    private  void startTodaysDueFragment(){
        Fragment fragment =new AccountsDisplayFragment();
        if(fragment!=null) {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.activity_todays_due_content_frame, fragment, "").commit();
        }
    }
}
