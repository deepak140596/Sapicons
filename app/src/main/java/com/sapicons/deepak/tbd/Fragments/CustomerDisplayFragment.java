package com.sapicons.deepak.tbd.Fragments;

import android.app.Activity;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sapicons.deepak.tbd.Adapters.CustomerItemAdapter;
import com.sapicons.deepak.tbd.AddAccountActivity;
import com.sapicons.deepak.tbd.CustomerDetailsActivity;
import com.sapicons.deepak.tbd.Objects.CustomerItem;
import com.sapicons.deepak.tbd.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Deepak Prasad on 01-08-2018.
 */

public class CustomerDisplayFragment extends ListFragment implements SearchView.OnQueryTextListener, MenuItem.OnActionExpandListener {

    private List<CustomerItem> list;
    private ListView listView;
    private CustomerItemAdapter adapter;
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
        getActivity().setTitle("Select Customer");

        View view = inflater.inflate(R.layout.fragment_customer_display, container, false);
        listView = view.findViewById(R.id.customer_display_list_view);
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

        adapter = new CustomerItemAdapter(mContext,R.layout.item_customer,list);

        listView.setAdapter(adapter);

        progressDialog = new ProgressDialog(mContext);
        progressDialog.setMessage("Please Wait ...");



        //set listview on click to target activity
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CustomerItem item = (CustomerItem)adapterView.getItemAtPosition(i);
                Bundle bundle = new Bundle();
                bundle.putSerializable("selected_customer",item);


                    Intent intent = new Intent(getActivity(),AddAccountActivity.class );
                    intent.putExtras(bundle);
                    startActivity(intent);


                onDetach();
            }
        });
    }

    //get all data at once , not realtime
    private  void getDataFromFirestore(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getEmail())
                .collection("customers")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot doc : task.getResult()){
                                Log.d("CustomerActivity",doc.getId() + " => " + doc.getData());
                                CustomerItem newItem = doc.toObject(CustomerItem.class);
                                list.add(newItem);
                                adapter.notifyDataSetChanged();
                            }
                            progressDialog.dismiss();
                        }else {
                            Log.d("CustomerActivity", "Error getting documents: ", task.getException());
                        }
                    }
                });


    }

    //get realtime updates
    private void listenToChanges(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //final CollectionReference docRef = db.collection("users").document(user.getEmail()).collection("");

        db.collection("users").document(user.getEmail()).collection("customers")

                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        List<CustomerItem> new_list = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            CustomerItem newItem = doc.toObject(CustomerItem.class);
                            Log.d(TAG,"Name: "+newItem.getFirstName());
                            new_list.add(newItem);

                        }
                        list = new_list;
                        adapter = new CustomerItemAdapter(mContext,R.layout.item_customer,new_list);
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
        searchView.setQueryHint("Search Customers");

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
        List<CustomerItem> filteredValues = new ArrayList<CustomerItem>(list);
        for (CustomerItem value : list) {
            String searchString = value.getFirstName()+" "+value.getLastName();
            searchString=searchString.toLowerCase();

            if (!searchString.contains(newText.toLowerCase())) {

                filteredValues.remove(value);
            }
        }
        adapter = new CustomerItemAdapter(mContext,R.layout.item_customer,filteredValues);
        //setListAdapter(adapter);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);

        return false;
    }
    public void resetSearch() {
        adapter = new CustomerItemAdapter(mContext,R.layout.item_customer,list);
        adapter.notifyDataSetChanged();
        listView.setAdapter(adapter);
        //listenToChanges();
    }
}
