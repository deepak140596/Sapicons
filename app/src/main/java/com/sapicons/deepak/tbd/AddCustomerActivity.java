package com.sapicons.deepak.tbd;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.sapicons.deepak.tbd.Objects.CustomerItem;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class AddCustomerActivity extends AppCompatActivity {

    EditText firstNameEt,lastNameEt, phoneEt, addLine1Et, addLine2Et,townCityEt, pinCodeEt;
    FloatingActionButton doneBtn;
    ImageView customerPicIv;
    Uri customerPicUri;

    StorageReference storageReference;
    ProgressDialog progressDialog;

    String TAG = "AddCustomerActivity";

    CustomerItem customerItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        setTitle("Add Customer");

        initialiseUI();
    }
    private void initialiseUI(){

        progressDialog = new ProgressDialog(this);

        firstNameEt = findViewById(R.id.add_customer_first_name);
        lastNameEt = findViewById(R.id.add_customer_last_name);
        phoneEt = findViewById(R.id.add_customer_phone);
        addLine1Et = findViewById(R.id.add_customer_add_line1);
        addLine2Et= findViewById(R.id.add_customer_add_line2);
        townCityEt = findViewById(R.id.add_customer_town_city);
        pinCodeEt = findViewById(R.id.add_customer_pincode);
        doneBtn = findViewById(R.id.add_customer_done_fab);
        customerPicIv=findViewById(R.id.add_customer_pic);

        firstNameEt.addTextChangedListener(watcher);
        lastNameEt.addTextChangedListener(watcher);
        phoneEt.addTextChangedListener(watcher);
        addLine2Et.addTextChangedListener(watcher);
        addLine1Et.addTextChangedListener(watcher);
        townCityEt.addTextChangedListener(watcher);

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressDialog.setMessage("Adding Customer ... ");
                progressDialog.show();

                if(customerPicUri!=null)
                    uploadToDBwithPic();
                else
                    addCustomerToDatabase("");
            }
        });

        customerPicIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(AddCustomerActivity.this);
            }
        });
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
            if(firstNameEt.getText().toString().length()==0 ||
                    lastNameEt.getText().toString().length()==0 ||
                    phoneEt.getText().toString().length()==0 ||
                    addLine1Et.getText().toString().length()==0 ||
                    addLine2Et.getText().toString().length()==0 ||
                    townCityEt.getText().toString().length()==0 ){

                doneBtn.setVisibility(View.GONE);

            }
            else
                doneBtn.setVisibility(View.VISIBLE);

        }
    };

    private void uploadToDBwithPic(){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        storageReference= FirebaseStorage.getInstance().getReference().child("users")
                .child(user.getEmail()).child("customers_pic");
        if(customerPicUri!=null) {
            storageReference = storageReference.child(customerPicUri.getLastPathSegment());
            UploadTask task = storageReference.putFile(customerPicUri);

            Task<Uri> urlTask = task.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        Log.d("PIC_URL","url: "+downloadUri);
                        addCustomerToDatabase(downloadUri.toString());
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }


    }

    private void askForNewAccount(){
        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(AddCustomerActivity.this);


        builder.setTitle("Add an Account?")
                .setMessage("Customer added successfully. Proceed to add an account?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("selected_customer",customerItem);
                        Intent intent = new Intent(AddCustomerActivity.this, AddAccountActivity.class);
                        intent.putExtras(bundle);
                        //Toast.makeText(mContext, item.getFirstName()+" selected", Toast.LENGTH_SHORT).show();
                        startActivity(intent);
                        finish();

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                finish();

            }
        }).show();

    }

    private void clearFields(){
        firstNameEt.setText("");
        lastNameEt.setText("");
        phoneEt.setText("");
        addLine1Et.setText("");
        addLine2Et.setText("");
        townCityEt.setText("");
        pinCodeEt.setText("");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                customerPicUri = result.getUri();
                Glide.with(this).load(customerPicUri).into(customerPicIv);
                Log.d("URI","Result URI: "+customerPicUri.toString());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void addCustomerToDatabase(String picUrl){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        // create firestore instance
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String firstName = firstNameEt.getText().toString();
        String lastName = lastNameEt.getText().toString();
        String phone = phoneEt.getText().toString();
        String addLine1 = addLine1Et.getText().toString();
        String addLine2 = addLine2Et.getText().toString();
        String townCity = townCityEt.getText().toString();
        String pincode = pinCodeEt.getText().toString();

        //create an auto-generated id
        final DocumentReference newCustomerRef = db.collection("users").document(user.getEmail())
                .collection("customers").document(phone);
        customerItem = new CustomerItem(newCustomerRef.getId(), firstName, lastName, phone, addLine1, addLine2, townCity, pincode, picUrl);


        //check if document already exists

        documentExists(newCustomerRef,customerItem);


    }

    public void uploadData(DocumentReference ref, CustomerItem customerItem){
        ref.set(customerItem)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Data successfully written.");
                        Toast.makeText(AddCustomerActivity.this, "Customer successfully added!", Toast.LENGTH_SHORT).show();

                        progressDialog.dismiss();
                        askForNewAccount();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error writing document. " + e);
            }
        });

    }

    public void documentExists(final  DocumentReference ref,final CustomerItem item){

        ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.aac_ll),"User with this phone number already exists.",Snackbar.LENGTH_LONG);
                    snackbar.show();
                    progressDialog.dismiss();
                }

                else {
                    uploadData(ref,item);
                }
            }
        });

    }

}
