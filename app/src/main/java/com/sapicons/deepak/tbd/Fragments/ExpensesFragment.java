package com.sapicons.deepak.tbd.Fragments;

import android.app.Fragment;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sapicons.deepak.tbd.Adapters.CustomerItemAdapter;
import com.sapicons.deepak.tbd.Adapters.ExpenseItemAdapter;
import com.sapicons.deepak.tbd.AddExpenseActivity;
import com.sapicons.deepak.tbd.BalanceReportActivity;
import com.sapicons.deepak.tbd.DisplayAccountsListActivity;
import com.sapicons.deepak.tbd.Objects.CustomerItem;
import com.sapicons.deepak.tbd.Objects.ExpenseItem;
import com.sapicons.deepak.tbd.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Deepak Prasad on 16-08-2018.
 */

public class ExpensesFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {


    private List<ExpenseItem> list;
    private ListView listView;
    private ExpenseItemAdapter adapter;
    private Context mContext;

    ProgressDialog progressDialog;
    String TAG = "TAG";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Expenses");

        View view = inflater.inflate(R.layout.fragment_expenses, container, false);
        listView = view.findViewById(R.id.expenses_frag_list_view);
        return view;


    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initialiseViews(view);
        progressDialog.show();

        //get realtime updates
        listenToChanges();

    }

    public void initialiseViews(View view){

        list = new ArrayList<>();

        adapter = new ExpenseItemAdapter(mContext,R.layout.item_expense,list);

        listView.setAdapter(adapter);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait ...");


    }

    private void listenToChanges(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final CollectionReference docRef = db.collection("users").document(user.getEmail()).collection("");

        db.collection("users").document(user.getEmail()).collection("expenses")

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<ExpenseItem> new_list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            ExpenseItem newItem = doc.toObject(ExpenseItem.class);
                            Log.d(TAG,"Type: "+newItem.getType());
                            new_list.add(newItem);

                        }
                        list = new_list;
                        adapter = new ExpenseItemAdapter(mContext,R.layout.item_expense,new_list);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                        progressDialog.dismiss();

                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        inflater.inflate(R.menu.sort_expenses_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        MenuItem sortItem = menu.findItem(R.id.action_sort);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search By Type/Date");



        super.onCreateOptionsMenu(menu, inflater);

    }



    @Override
    public boolean onMenuItemActionExpand(MenuItem menuItem) {
        return true;
    }

    @Override
    public boolean onMenuItemActionCollapse(MenuItem menuItem) {
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText == null || newText.trim().isEmpty()) {
            resetSearch();
            return false;
        }
        List<ExpenseItem> filteredValues = new ArrayList<ExpenseItem>(list);
        for (ExpenseItem value : list) {

            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");
            long s = Long.parseLong(value.getDate());
            //calendar.setTimeInMillis(s);

            String d = dateFormatter.format(s);

            String searchString = d+" "+value.getType();
            searchString=searchString.toLowerCase();

            if (!searchString.contains(newText.toLowerCase())) {

                filteredValues.remove(value);
            }
        }
        adapter = new ExpenseItemAdapter(mContext,R.layout.item_expense,filteredValues);
        //setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        return false;
    }
    public void resetSearch() {
        adapter = new ExpenseItemAdapter(mContext,R.layout.item_expense,list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        //listenToChanges();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if(id==R.id.action_sort_by_travel) {
            onQueryTextChange("travel");

        }   else if(id == R.id.action_sort_by_investment){
            onQueryTextChange("investment");

        } else if(id == R.id.action_sort_by_salary){
            onQueryTextChange("salary");

        }


        return super.onOptionsItemSelected(item);
    }


}

