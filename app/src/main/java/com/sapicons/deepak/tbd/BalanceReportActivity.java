package com.sapicons.deepak.tbd;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sapicons.deepak.tbd.Adapters.AccountItemAdapter;
import com.sapicons.deepak.tbd.Objects.AccountItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BalanceReportActivity extends AppCompatActivity {

    TableLayout tl;
    private ArrayList<AccountItem>  new_list;
    private AccountItemAdapter adapter;
    ProgressDialog progressDialog;

    String TAG="TAG";
    int i=0,al_row=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_balance_report);

        initialiseViews();
        listenToChanges();
    }

    @SuppressLint("ResourceType")
    private void initialiseViews(){

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please Wait ...");
        tl = (TableLayout) findViewById(R.id.balance_report_table_layout);
        TableRow tr_head = new TableRow(this);
        tr_head.setId(100);
        tr_head.setBackgroundColor(Color.GRAY);
        tr_head.setLayoutParams(new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));

        TextView label_name = new TextView(this);
        label_name.setId(20);
        label_name.setText("Name");
        label_name.setTextColor(Color.WHITE);
        label_name.setPadding(5, 5, 5, 5);
        tr_head.addView(label_name);// add the column to the table row here

        TextView label_start_date = new TextView(this);
        label_start_date.setId(21);// define id that must be unique
        label_start_date.setText("Start Date"); // set the text for the header
        label_start_date.setTextColor(Color.WHITE); // set the color
        label_start_date.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(label_start_date); // add the column to the table row here

        TextView account_type = new TextView(this);
        account_type.setId(22);// define id that must be unique
        account_type.setText("Acc. Type"); // set the text for the header
        account_type.setTextColor(Color.WHITE); // set the color
        account_type.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(account_type); // add the column to the table row here

        TextView actual_amt = new TextView(this);
        actual_amt.setId(23);// define id that must be unique
        actual_amt.setText("Actual Amt"); // set the text for the header
        actual_amt.setTextColor(Color.WHITE); // set the color
        actual_amt.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(actual_amt); // add the column to the table row here

        TextView due_amt = new TextView(this);
        due_amt.setId(24);// define id that must be unique
        due_amt.setText("Due Amt"); // set the text for the header
        due_amt.setTextColor(Color.WHITE); // set the color
        due_amt.setPadding(5, 5, 5, 5); // set the padding (if required)
        tr_head.addView(due_amt); // add the column to the table row here

        tl.addView(tr_head, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));


        /*Integer count=0;

            String date = "9";// get the first variable
            Double weight_kg = 9.0;// get the second variable
// Create the table row
            TableRow tr = new TableRow(this);
            if(count%2!=0) tr.setBackgroundColor(Color.GRAY);
            tr.setId(100+count);
            tr.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));

//Create two columns to add as table data
            // Create a TextView to add date
            TextView labelDATE = new TextView(this);
            labelDATE.setId(200+count);
            labelDATE.setText(date);
            labelDATE.setPadding(2, 0, 5, 0);
            labelDATE.setTextColor(Color.WHITE);
            tr.addView(labelDATE);
            TextView labelWEIGHT = new TextView(this);
            labelWEIGHT.setId(200+count);
            labelWEIGHT.setText(weight_kg.toString());
            labelWEIGHT.setTextColor(Color.WHITE);
            tr.addView(labelWEIGHT);

// finally add this to the table row
            tl.addView(tr, new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.FILL_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));*/

    }


    //get realtime updates
    private void listenToChanges(){
        progressDialog.show();

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

                        new_list = new ArrayList<AccountItem>();
                        for (QueryDocumentSnapshot doc : value) {
                            AccountItem newItem = doc.toObject(AccountItem.class);
                            Log.d(TAG,"Name: "+newItem.getFirstName());
                            new_list.add(newItem);

                            //al_row++;
                            //addNewRow(newItem);

                        }
                        //sort accordingly
                        Collections.sort(new_list,AccountItem.AccountNameComparator);

                        for( al_row=0;al_row<new_list.size();al_row++)
                            addNewRow(new_list.get(al_row));
                        progressDialog.dismiss();

                    }
                });
    }

    @SuppressLint("ResourceType")
    private void addNewRow(AccountItem accountItem){

        String name= accountItem.getFirstName()+" "+accountItem.getLastName();
        String accType = accountItem.getAccoutType();
        String actualAmt= accountItem.getActualAmt();
        String dueAmt = accountItem.getDueAmt();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yy");
        Long sd = Long.parseLong(accountItem.getStartDate());
        String startDate= dateFormat.format(sd);

        int color= Color.BLACK;

        TableRow tr = new TableRow(this);
        if(al_row%2 != 0){
            tr.setBackgroundColor(Color.GRAY);
            color = Color.WHITE;
        }
        tr.setId(100+(i++));
        TableLayout.LayoutParams params = new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(8,8,0,0);
        tr.setLayoutParams(params);


        TextView label_name = new TextView(this);
        label_name.setId(200+ (i++));
        label_name.setText(name);
        label_name.setTextColor(color);
        label_name.setPadding(2, 5, 5, 5);
        tr.addView(label_name);// add the column to the table row here


        TextView label_start_date = new TextView(this);
        label_start_date.setId(200+ (i++));// define id that must be unique
        label_start_date.setText(startDate); // set the text for the header
        label_start_date.setTextColor(color); // set the color
        label_start_date.setPadding(2, 10, 5, 5); // set the padding (if required)
        tr.addView(label_start_date); // add the column to the table row here

        TextView account_type = new TextView(this);
        account_type.setId(200+ (i++));// define id that must be unique
        account_type.setText(accType); // set the text for the header
        account_type.setTextColor(color); // set the color
        account_type.setPadding(2, 5, 5, 5); // set the padding (if required)
        tr.addView(account_type); // add the column to the table row here

        TextView actual_amt = new TextView(this);
        actual_amt.setId(200+ (i++));// define id that must be unique
        actual_amt.setText(actualAmt); // set the text for the header
        actual_amt.setTextColor(color); // set the color
        actual_amt.setPadding(2, 5, 5, 5); // set the padding (if required)
        tr.addView(actual_amt); // add the column to the table row here

        TextView due_amt = new TextView(this);
        due_amt.setId(200+ (i++));// define id that must be unique
        due_amt.setText(dueAmt); // set the text for the header
        due_amt.setTextColor(color); // set the color
        due_amt.setPadding(2, 5, 5, 5); // set the padding (if required)
        tr.addView(due_amt); // add the column to the table row here


        tl.addView(tr, new TableLayout.LayoutParams(
                TableLayout.LayoutParams.FILL_PARENT,
                TableLayout.LayoutParams.WRAP_CONTENT));
    }
}
