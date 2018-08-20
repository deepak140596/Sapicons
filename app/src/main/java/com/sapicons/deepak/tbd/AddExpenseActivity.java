package com.sapicons.deepak.tbd;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
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
import com.sapicons.deepak.tbd.Objects.ExpenseItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddExpenseActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    Calendar myCalendar;
    EditText amtET,startDateEt,descriptionEt;
    DatePickerDialog.OnDateSetListener date;
    //FancyButton saveBtn;
    FloatingActionButton saveBtn;

    Spinner selectExpenseTypeSpinner;

    String selectedExpenseType;

    PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);


        updateUI();
        setDate();
    }

    private void updateUI(){
        myCalendar = Calendar.getInstance();

        startDateEt= (EditText) findViewById(R.id.add_expense_date_et);
        selectExpenseTypeSpinner = findViewById(R.id.add_expense_type_spinner);
        saveBtn = findViewById(R.id.add_expense_save_btn);
        amtET = findViewById(R.id.add_expense_amount_et);
        descriptionEt = findViewById(R.id.add_expense_description_et);

        //add text watcher
        amtET.addTextChangedListener(watcher);

        List<String> expenseTypes = new ArrayList<String>();
        expenseTypes.add("Salary");
        expenseTypes.add("Investment");
        expenseTypes.add("Travel");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, expenseTypes);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        selectExpenseTypeSpinner.setAdapter(dataAdapter);

        // Spinner click listener
        selectExpenseTypeSpinner.setOnItemSelectedListener(this);


        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateStartDate();
            }

        };


        startDateEt.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


                new DatePickerDialog(AddExpenseActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });



        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(AddExpenseActivity.this);

                builder.setTitle("Save Expense?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //save changes accordingly

                                addExpenseToFirestore();

                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
            }
        });

    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        selectedExpenseType = parent.getItemAtPosition(position).toString();

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
            if(amtET.getText().toString().length()==0 ){

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

    private void addExpenseToFirestore(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // create firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Calendar cal = Calendar.getInstance();

        long ts = cal.getTimeInMillis() ;

        String timestamp = ts+"";
        String type = selectedExpenseType;
        String amount = amtET.getText().toString();
        String description  = descriptionEt.getText().toString();
        String date = myCalendar.getTimeInMillis()+"";


        final DocumentReference newAccRef = db.collection("users").document(user.getEmail())
                .collection("expenses").document(timestamp);

        ExpenseItem expenseItem = new ExpenseItem(type,amount, timestamp,description,date);

        newAccRef.set(expenseItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TAG","Expense added ");


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","Error adding account: "+ e);

            }
        });
        Toast.makeText(AddExpenseActivity.this, "Expense Added!", Toast.LENGTH_SHORT).show();
        finish();

    }
}
