package com.sapicons.deepak.tbd.Fragments;

import android.app.ListFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sapicons.deepak.tbd.Adapters.AccountItemAdapter;
import com.sapicons.deepak.tbd.Objects.AccountItem;
import com.sapicons.deepak.tbd.Objects.CustomerItem;
import com.sapicons.deepak.tbd.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak Prasad on 09-08-2018.
 */

public class CollectActivityFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private List<AccountItem> list;
    private ListView listView;
    private AccountItemAdapter adapter;
    private Context mContext;
    LinearLayout ll;

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

        View view = inflater.inflate(R.layout.fragment_collect, container, false);
        listView = view.findViewById(R.id.collect_list_view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        initialiseViews(view);
        progressDialog.show();
        //get realtime updates
        listenToChanges();
    }

    private void initialiseViews(View view){
        list = new ArrayList<>();

        adapter = new AccountItemAdapter(mContext,R.layout.item_account,list,1);
        ll = view.findViewById(R.id.caf_ll);

        listView.setAdapter(adapter);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait ...");



        // collect amount from customer
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                AccountItem item = (AccountItem) adapterView.getItemAtPosition(i);
                setUpPopupWindow(item);
            }
        });


    }

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
                        adapter = new AccountItemAdapter(mContext,R.layout.item_account,new_list,1);
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
            name= name.toLowerCase();

            if ( !(name.contains(newText.toLowerCase())) ) {

                filteredValues.remove(value);
            }
        }
        adapter = new AccountItemAdapter(mContext,R.layout.item_account,filteredValues,1);
        //setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        return false;
    }
    public void resetSearch() {
        adapter = new AccountItemAdapter(mContext,R.layout.item_account,list,1);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        //listenToChanges();
    }


    private void setUpPopupWindow(final AccountItem accountItem){


        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View customView=inflater.inflate(R.layout.custom_collect_popup,null);
        final EditText amtEt = customView.findViewById(R.id.custom_collect_amt_et);

        alertDialog.setTitle("Collect Amount")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).setPositiveButton("Collect", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //Toast.makeText(mContext, "Amt collected!", Toast.LENGTH_SHORT).show();
                float actualAmt = Float.parseFloat(accountItem.getDueAmt());
                String eA = amtEt.getText().toString();
                if(!eA.isEmpty()) {
                    float enteredAmt = Float.parseFloat(eA);
                    if (enteredAmt > actualAmt) {
                        Snackbar snackbar = Snackbar.make(ll, "Collected Amount is more than Due.", Snackbar.LENGTH_LONG);
                        snackbar.show();
                    } else {
                        progressDialog.show();
                        deductAmountFromAccount(accountItem, amtEt.getText().toString());
                    }
                }


            }
        });

        alertDialog.setView(customView)
                .create().show();

    }

    private void deductAmountFromAccount(final AccountItem accountItem, String amount){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        String newAmt = (Float.parseFloat(accountItem.getDueAmt()) - Float.parseFloat(amount))+"";
        //accountItem.setDueAmt(newAmt);

        // set customer pic if pic is null
        if(accountItem.getCustomerPicUrl().length()<10){
            DocumentReference cusRefDOc = db.collection("users").document(user.getEmail())
                    .collection("customers").document(accountItem.getPhoneNumber());

            cusRefDOc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            AccountItem i = document.toObject(AccountItem.class);
                            accountItem.setCustomerPicUrl(i.getCustomerPicUrl());

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }

        //update the new info to db
        DocumentReference accRef = db.collection("users").document(user.getEmail())
                .collection("accounts").document(accountItem.getAccountNumber());

        accRef.update("dueAmt",newAmt)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(mContext, "Amount Updated!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","error updating amt: "+e);
            }
        });



    }

}
