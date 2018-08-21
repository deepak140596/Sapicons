package com.sapicons.deepak.tbd;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.sapicons.deepak.tbd.Fragments.ClosedAccountsDisplayFragment;
import com.sapicons.deepak.tbd.Fragments.CustomerDisplayFragment;

public class ClosedAccountsActivity extends AppCompatActivity {
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closed_accounts);
        setTitle("Closed Accounts");

        startClosedAccountsFragment();
    }

    private  void startClosedAccountsFragment(){
        Fragment fragment =new ClosedAccountsDisplayFragment();
        if(fragment!=null) {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.activity_closed_accounts_content_frame, fragment, "").commit();
        }
    }
}
