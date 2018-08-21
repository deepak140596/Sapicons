package com.sapicons.deepak.tbd.Adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sapicons.deepak.tbd.Objects.AccountItem;
import com.sapicons.deepak.tbd.Objects.CollectItem;
import com.sapicons.deepak.tbd.Objects.CustomerItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sapicons.deepak.tbd.R;

import mehdi.sakout.fancybuttons.FancyButton;

/**
 * Created by Deepak Prasad on 15-08-2018.
 */

public class ClubbedAccountsAdapter  extends ArrayAdapter<CustomerItem> {

    LinearLayout linearLayout;
    Context context;
    List<CustomerItem> custItem = new ArrayList<>();
    List<AccountItem> accItem = new ArrayList<>();
    int layoutResourceId;

    int isCollect = 0;

    ProgressDialog progressDialog;
    ViewHolder holder;
    CustomerItem customerItem;

    public ClubbedAccountsAdapter(@NonNull Context context, int resource, @NonNull List<CustomerItem> objects,int isCollect) {
        super(context, resource, objects);
        this.context = context;
        layoutResourceId = resource;
        custItem = objects;
        this.isCollect = isCollect;
    }

    static class ViewHolder{
        ImageView custImage;
        TextView custName;
        LinearLayout accLL;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        customerItem = getItem(position);
        if(convertView == null) {
            convertView = ((Activity) getContext()).getLayoutInflater().inflate(R.layout.item_clubbed_accounts, parent, false);

            holder = new ViewHolder();
            holder.custImage = convertView.findViewById(R.id.item_clubbed_account_customer_pic_iv);
            holder.custName = convertView.findViewById(R.id.item_clubbed_account_customer_name);
            holder.accLL = convertView.findViewById(R.id.item_clubbed_acc_ll);

            convertView.setTag(holder);
            findAccountsOfCustomer(customerItem,holder);


        }else{
            holder=(ViewHolder)convertView.getTag();
        }


        holder.custName.setText(customerItem.getFirstName().toString() + " " + customerItem.getLastName().toString());
        if (customerItem.getPhotoUrl().length() > 0)
            Glide.with(getContext()).load(customerItem.getPhotoUrl()).into(holder.custImage);



        return  convertView;
    }


    public void findAccountsOfCustomer(CustomerItem item,final ViewHolder holder){
        List<AccountItem> accounts = new ArrayList<>();

        getAccountsFromFirebase(item,accounts,holder);

    }

    public void getAccountsFromFirebase(CustomerItem item, final List<AccountItem> list,final ViewHolder holder){



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference ref = db.collection("users").document(user.getEmail())
                .collection("accounts");

        //create a query to search for user's accounts
        Query getNoOfAcc = ref.whereEqualTo("firstName",item.getFirstName())
                .whereEqualTo("lastName",item.getLastName())
                .whereEqualTo("phoneNumber",item.getPhone())
                .whereEqualTo("accountStatus","open");

        getNoOfAcc.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {


                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                AccountItem item = document.toObject(AccountItem.class);
                                Log.d("CAA",item.getFirstName());

                                list.add(item);
                            }
                        }else {
                            Log.d("TAG", "Error getting documents: ", task.getException());
                        }

                        //  club the accounts

                        clubAccounts(list,holder);
                    }
                });


    }

    public void clubAccounts(List<AccountItem> list,ViewHolder holder){
        for(final AccountItem item: list) {
            Log.d("TAG", "Name: " + item.getFirstName() + " NO: " + item.getAccountNumber());

            LayoutInflater layoutInflater =  (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view;

            view = layoutInflater.inflate(R.layout.item_single_account_in_club, linearLayout, false);
            final TextView accTypeTv = view.findViewById(R.id.single_account_type_tv),
                    dueAmtTv = view.findViewById(R.id.single_acc_due_amt),
                    startDateTv = view.findViewById(R.id.single_account_start_date_tv),
                    endDateTv =  view.findViewById(R.id.single_account_end_date_tv);
            FancyButton collectBtn = view.findViewById(R.id.single_acc_collect_btn);

            if(isCollect == 0)
                collectBtn.setVisibility(View.INVISIBLE);



            //set the textviews
            accTypeTv.setText(item.getAccoutType());
            dueAmtTv.setText(item.getDueAmt());

            //set the date in formatted way
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yy");
            long s = Long.parseLong(item.getStartDate());
            long e = Long.parseLong(item.getEndDate());
            calendar.setTimeInMillis(s);
            startDateTv.setText(dateFormatter.format(calendar.getTime()));
            calendar.setTimeInMillis(e);
            endDateTv.setText(dateFormatter.format(calendar.getTime()));


            holder.accLL.addView(view);
            collectBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("TAG","Amt: "+item.getDueAmt());
                    setUpPopupWindow(item,dueAmtTv);
                }
            });


        }

    }

    // collect functionality

    public void setUpPopupWindow(final AccountItem item, final TextView dueAmtTv){
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Please Wait ...");

        AlertDialog.Builder alertDialog=new AlertDialog.Builder(getContext());
        LayoutInflater inflater =(LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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
                float actualAmt = Float.parseFloat(item.getDueAmt());
                String eA = amtEt.getText().toString();
                if(!eA.isEmpty()) {
                    float enteredAmt = Float.parseFloat(eA);
                    if (enteredAmt > actualAmt) {
                        Toast.makeText(context, "Amount collected is more than Due.", Toast.LENGTH_LONG).show();
                    } else {
                        progressDialog.show();

                        deductAmountFromAccount(item, amtEt.getText().toString(),dueAmtTv);
                    }
                }


            }
        });

        alertDialog.setView(customView)
                .create().show();
    }

    private void deductAmountFromAccount(final AccountItem accountItem, String amount,TextView dueAmtTv){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();


        String newAmt = (Float.parseFloat(accountItem.getDueAmt()) - Float.parseFloat(amount))+"";

        if(accountItem.getAccoutType().contains("M"))
            newAmt = accountItem.getDueAmt();
        dueAmtTv.setText(newAmt);
        accountItem.setDueAmt(newAmt);


        //update the new info to db
        DocumentReference accRef = db.collection("users").document(user.getEmail())
                .collection("accounts").document(accountItem.getAccountNumber());

        accRef.update("dueAmt",newAmt)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(context, "Amount Updated!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","error updating amt: "+e);
            }
        });


        //close account id dueAmt = zero
        if(Float.parseFloat(newAmt)  < 1.0){
            Toast.makeText(context, "Account is Closed!", Toast.LENGTH_SHORT).show();
            accountItem.setAccountStatus("closed");

            accRef.update("accountStatus","closed")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            //Toast.makeText(context, "Amount Updated!", Toast.LENGTH_SHORT).show();
                            //progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d("TAG","error updating account status: "+e);
                }
            });
        }


        addCollectionToFirestore(accountItem,amount);

    }

    private void addCollectionToFirestore(AccountItem item, String collectedAmount){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collectRef = db.collection("users").document(user.getEmail())
                .collection("collections");

        Date date = new Date();
        String timestamp = date.getTime()+"";
        String profit = "0";

        // profit for D account is zero
        // profit for M account is (loanAmt * interestPct/100)
        if(item.getAccoutType().contains("M")){

            profit= (Float.parseFloat(item.getLoanAmt().trim())*Float.parseFloat(item.getInterestPct().trim())/100)+"";
        }

        CollectItem collectItem = new CollectItem(item.getAccountNumber(),timestamp,collectedAmount,profit,item.getAccoutType());



        collectRef.add(collectItem)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","Failed to write collection amount. "+e);
            }
        });



        //reset the user interface
        resetUI();
    }

    private void resetUI(){

        custItem = new ArrayList<>();
        accItem = new ArrayList<>();
        findAccountsOfCustomer(customerItem,holder);
    }
}
