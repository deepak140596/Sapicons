package com.sapicons.deepak.tbd.Fragments;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.sapicons.deepak.tbd.AccountsDetailsActivity;
import com.sapicons.deepak.tbd.Adapters.AccountItemAdapter;
import com.sapicons.deepak.tbd.Adapters.CustomerItemAdapter;
import com.sapicons.deepak.tbd.AddAccountActivity;
import com.sapicons.deepak.tbd.Objects.AccountItem;
import com.sapicons.deepak.tbd.Objects.CustomerItem;
import com.sapicons.deepak.tbd.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak Prasad on 04-08-2018.
 */

public class AccountsDisplayFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private List<AccountItem> list;
    private ListView listView;
    private AccountItemAdapter adapter;
    private Context mContext;

    ProgressDialog progressDialog;
    String TAG = "TAG";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
        setHasOptionsMenu(true);
        //populateList();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getActivity().setTitle("Select Account");

        View view = inflater.inflate(R.layout.fragment_accounts_display, container, false);
        listView = view.findViewById(R.id.accounts_display_list_view);
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initialiseViews(view);
        progressDialog.show();
        //getDataFromFirestore();

        //get realtime updates
        listenToChanges();
    }

    private void initialiseViews(View view){
        list = new ArrayList<>();

        adapter = new AccountItemAdapter(mContext,R.layout.item_account,list,2);

        listView.setAdapter(adapter);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait ...");

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AccountItem item = (AccountItem)adapterView.getItemAtPosition(i);
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_account",item);


                Intent intent = new Intent(getActivity(),AccountsDetailsActivity.class );
                intent.putExtras(bundle);
                startActivity(intent);


                onDetach();
            }
        });


    }

    //get realtime updates
    private void listenToChanges(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final CollectionReference docRef = db.collection("users").document(user.getEmail()).collection("");

        db.collection("users").document(user.getEmail()).collection("accounts")

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<AccountItem> new_list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            AccountItem newItem = doc.toObject(AccountItem.class);
                            Log.d(TAG,"Name: "+newItem.getFirstName());
                            new_list.add(newItem);

                        }
                        list = new_list;
                        adapter = new AccountItemAdapter(mContext,R.layout.item_account,new_list,2);
                        adapter.notifyDataSetChanged();
                        listView.setAdapter(adapter);
                        progressDialog.dismiss();

                    }
                });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu,menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setOnQueryTextListener(this);
        searchView.setQueryHint("Search Accounts");

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
        List<AccountItem> filteredValues = new ArrayList<AccountItem>(list);
        for (AccountItem value : list) {

            String name = value.getFirstName()+" "+value.getLastName()+" "+value.getAccountNumber();
            name = name.toLowerCase();

            if ( !(name.contains(newText.toLowerCase())) ) {

                filteredValues.remove(value);
            }
        }
        adapter = new AccountItemAdapter(mContext,R.layout.item_account,filteredValues,2);
        //setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        return false;
    }
    public void resetSearch() {
        adapter = new AccountItemAdapter(mContext,R.layout.item_account,list,2);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        //listenToChanges();
    }
}
