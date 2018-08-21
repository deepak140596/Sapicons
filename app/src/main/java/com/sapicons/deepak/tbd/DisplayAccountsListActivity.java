package com.sapicons.deepak.tbd;

import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.sapicons.deepak.tbd.Fragments.ClubAccountsDisplayFragment;

public class DisplayAccountsListActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_accounts_list);

        startDisplayAccountsFragement();

    }
    public  void startDisplayAccountsFragement(){
        Bundle bundle = new Bundle();
        bundle.putInt("is_collect", 0);
// set Fragmentclass Arguments
        Fragment fragment =new ClubAccountsDisplayFragment();
        fragment.setArguments(bundle);
        if(fragment!=null) {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.activity_display_accounts_content_frame, fragment, "").commit();
        }
    }
}
