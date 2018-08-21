package com.sapicons.deepak.tbd;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sapicons.deepak.tbd.Objects.AccountItem;
import com.sapicons.deepak.tbd.Objects.CustomerItem;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.time.MonthDay;
import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Date;

import mehdi.sakout.fancybuttons.FancyButton;

public class AddAccountActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    CustomerItem selectedCustomer;
    Calendar myCalendar,endCalender;
    EditText amtET,interestPctEt,startDateEt,endDateEt;
    DatePickerDialog.OnDateSetListener date,endDate;
    //FancyButton saveBtn;
    FloatingActionButton saveBtn;

    TextView customerNameTV;
    Spinner selectAccTypeSpinner;

    String selectedAccType;

    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_account);
        setTitle("Add Account");

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        selectedCustomer = (CustomerItem)bundle.getSerializable("selected_customer");

        updateUI();
        setDate();
    }



    private void updateUI(){
        myCalendar = Calendar.getInstance();
        endCalender = Calendar.getInstance();

        startDateEt= (EditText) findViewById(R.id.add_acc_start_date_et);
        customerNameTV = findViewById(R.id.add_acc_customer_name_et);
        selectAccTypeSpinner = findViewById(R.id.add_acc_choose_ac_type_spinner);
        saveBtn = findViewById(R.id.add_acc_save_btn);
        amtET = findViewById(R.id.add_acc_amount_et);
        interestPctEt = findViewById(R.id.add_acc_interest_pct_et);
        endDateEt = findViewById(R.id.add_acc_end_date_et);


        //add text watcher
        interestPctEt.addTextChangedListener(watcher);
        amtET.addTextChangedListener(watcher);

        List<String> accTypes = new ArrayList<String>();
        accTypes.add("D Account");
        accTypes.add("M Account");
        accTypes.add("C Account");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, accTypes);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        selectAccTypeSpinner.setAdapter(dataAdapter);

        // Spinner click listener
        selectAccTypeSpinner.setOnItemSelectedListener(this);

        customerNameTV.setText(selectedCustomer.getFirstName()+" "+selectedCustomer.getLastName());

        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartDate();
                setEndDate(0);
            }

        };

        endDate = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int monthOfYear, int dayOfMonth) {
                endCalender.set(Calendar.YEAR, year);
                endCalender.set(Calendar.MONTH, monthOfYear);
                endCalender.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                setEndDate(1);
            }
        };



        startDateEt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                setEndDate(0);

                new DatePickerDialog(AddAccountActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        endDateEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddAccountActivity.this,endDate,
                        endCalender.get(Calendar.YEAR),endCalender.get(Calendar.MONTH),
                        endCalender.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(AddAccountActivity.this);

                builder.setTitle("Save Account?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //add account
                                addAccountToFirestore();


                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });

        interestPctEt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                PopupWindow popUp = popupWindowsort();
                popUp.showAsDropDown(view, 0, 0); // show popup like dropdown list
            }
        });
        interestPctEt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PopupWindow popUp = popupWindowsort();
                popUp.showAsDropDown(view, 0, 0); // show popup like dropdown list
            }
        });
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        selectedAccType = parent.getItemAtPosition(position).toString();

        setEndDate(0); //automatically set end date and interest rates
    }
    public void onNothingSelected(AdapterView<?> arg0) {


    }
    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(amtET.getText().toString().length()==0 ||
                    interestPctEt.getText().toString().length()==0 ){

                saveBtn.setVisibility(View.GONE);


            }
            else
                saveBtn.setVisibility(View.VISIBLE);

        }
    };


    //update date when the activity starts for the first time
    private void setDate(){
        Date now = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM d, yyyy");
        startDateEt.setText(dateFormatter.format(now));
    }


    private void updateStartDate() {
        String myFormat = "MMMM d, yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        startDateEt.setText(sdf.format(myCalendar.getTime()));
    }

    private void setEndDate(int manual){
        SimpleDateFormat dateFormatter = new SimpleDateFormat("MMMM d, yyyy");



        // if date is entered manually
        if(manual==1){
            endDateEt.setText(dateFormatter.format(endCalender.getTime()));
            Log.d("DATE","START DATE: "+myCalendar);
            Log.d("DATE","END DATE: "+endCalender);
            return;
        }
        //automatic date set
        if(selectedAccType.contains("D")) {
            long time = myCalendar.getTimeInMillis();
            time = time+100*24*60*60*1000l;

            endCalender.setTimeInMillis(time);

            Date endDate = new Date(time);
            endDateEt.setText(dateFormatter.format(endDate));
            interestPctEt.setText("3.0");
        }else if(selectedAccType.contains("M")){


            Date endDate =  new Date(4102338600000l);
            endCalender.setTimeInMillis(4102338600000l);
            endDateEt.setText(dateFormatter.format(endDate));

            interestPctEt.getText().clear();
        }
        else if(selectedAccType.contains("C")){
            interestPctEt.getText().clear();
        }


        Log.d("DATE","START DATE: "+myCalendar);
        Log.d("DATE","END DATE: "+endCalender);


    }

    private void addAccountToFirestore(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // create firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Calendar cal = Calendar.getInstance();

        long timestamp = cal.getTimeInMillis() ;


        final String accNumber = timestamp+"";
        //String startDate = startDateEt.getText().toString();
        String startDate = myCalendar.getTimeInMillis()+"";
        String endDate = endCalender.getTimeInMillis()+"";
        String phoneNumber = selectedCustomer.getPhone();
        String firstName = selectedCustomer.getFirstName();
        String lastName = selectedCustomer.getLastName();
        String accountType = selectedAccType;
        String interestPct = interestPctEt.getText().toString();
        String amount = amtET.getText().toString();
        String actualAmt = amount;
        String dueAmt = amount;
        String accoutStatus = "open";
        String customerPicUrl = selectedCustomer.getPhotoUrl();

        String loanAmt = dueAmt;
        String actualLoanAmt;
        if(accountType.contains("D")){
            actualLoanAmt = (Float.parseFloat(loanAmt)- (0.1* Float.parseFloat(loanAmt)))+"";
        }else
            actualLoanAmt = dueAmt;

        final DocumentReference newAccRef = db.collection("users").document(user.getEmail())
                .collection("accounts").document(accNumber);

        AccountItem accountItem = new AccountItem(accNumber,startDate,endDate,accountType,firstName,
                lastName,phoneNumber,amount,actualAmt,dueAmt,interestPct,accoutStatus,customerPicUrl,loanAmt,actualLoanAmt);

        newAccRef.set(accountItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG","Customer added. AccNo: "+ accNumber);


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","Error adding account: "+ e);

            }
        });
        Toast.makeText(AddAccountActivity.this, "Account Added!", Toast.LENGTH_SHORT).show();
        finish();

    }


    /**
     * show popup window method reuturn PopupWindow
     */
    private PopupWindow popupWindowsort() {

        // initialize a pop up window type
        popupWindow = new PopupWindow(this);

        ArrayList<String> sortList = new ArrayList<String>();
        for(double i=0.5;i<=9.0;i+=0.5){
            sortList.add(i+"");
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,
                sortList);
        // the drop down list is a list view

        ListView listViewSort = new ListView(this);

        // set our adapter and pass our pop up window contents
        listViewSort.setAdapter(adapter);
        listViewSort.setBackgroundColor(Color.parseColor("#ffffff"));

        // set on item selected
        listViewSort.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                interestPctEt.setText((String)adapter.getItem(i)+"");
                popupWindow.dismiss();
            }
        });

        // some other visual settings for popup window
        popupWindow.setFocusable(true);
        popupWindow.setWidth(250);
        // popupWindow.setBackgroundDrawable(getResources().getDrawable(R.drawable.white));
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // set the listview as popup content
        popupWindow.setContentView(listViewSort);


        return popupWindow;
    }
}
