package com.sapicons.deepak.tbd;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
//import android.support.v4.app.Fragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.SearchView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sapicons.deepak.tbd.Fragments.CustomerFragment;
import com.sapicons.deepak.tbd.Fragments.DashboardFragment;
import com.sapicons.deepak.tbd.Fragments.ExpensesFragment;
import com.sapicons.deepak.tbd.Fragments.ReportsFragment;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Fragment fragment = null;
    private FragmentManager fragmentManager;
    boolean doubleBackToExitPressedOnce = false;

    //private TextView mTextMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        startDashboardFragment();

    }



    private  void startDashboardFragment(){
        Fragment fragment =new DashboardFragment();
        if(fragment!=null) {
            fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main_activity_content_frame, fragment, "").commit();
        }
    }

    public void signOut(){
        AuthUI.getInstance()
                .signOut(getApplicationContext())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                        SharedPreferences preferences = getSharedPreferences("pin",0);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("existing_pin","");
                        editor.apply();
                        editor.commit();
                        preferences = getSharedPreferences("remember_me",0);
                        editor = preferences.edit();
                        editor.putBoolean("is_checked",false);
                        startActivity(new Intent(Main2Activity.this,SignInActivity.class));
                        finish();
                    }
                });
    }


    // create toolbar settings menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main2, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==R.id.menu_account){
            startActivity(new Intent(Main2Activity.this,UserProfile.class));
        }
        return super.onOptionsItemSelected(item);
    }



    //Navigation drawer menu
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_sign_out){
            signOut();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    //Bottom navigation view
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

             fragment = null;
            String fragmentTags = "";
            switch (item.getItemId()) {

                case R.id.navigation_dashboard:
                    fragment = new DashboardFragment();
                    //mTextMessage.setText(R.string.title_dashboard);
                    break;
                case R.id.navigation_reports:
                    fragment = new ReportsFragment();
                    //mTextMessage.setText("Reports");
                    break;
                case R.id.navigation_customers:
                    //mTextMessage.setText("Customers");
                    //startActivity(new Intent(Main2Activity.this, CustomerActivity.class));
                    fragment = new CustomerFragment();
                    break;
                case R.id.navigation_expenses:
                    //mTextMessage.setText("Settings");
                    fragment = new ExpensesFragment();
                    break;
                default:
                    fragment = new DashboardFragment();
                    break;


            }


            if (fragment != null) {
                fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main_activity_content_frame, fragment, fragmentTags).commit();
            }
            return true;
        }
    };

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }
}
