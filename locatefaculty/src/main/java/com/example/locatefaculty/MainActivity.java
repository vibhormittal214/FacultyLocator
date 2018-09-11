package com.example.locatefaculty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //defining view objects
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonSignup;
    private EditText name;
    private EditText eid;
    private TextView textViewSignin;
    String name1;
    String eid1,email,password;
    private ProgressDialog progressDialog;
    public DatabaseReference current_user_db2;

    //defining firebaseauth object
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing firebase auth object
        firebaseAuth = FirebaseAuth.getInstance();

        //if getCurrentUser does not returns null
        if(firebaseAuth.getCurrentUser() != null){
            //that means user is already logged in
            //so close this activity
            finish();

            //and open profile activity
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }

        //initializing views
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextPassword = (EditText) findViewById(R.id.editTextPassword);
        textViewSignin = (TextView) findViewById(R.id.textViewSignin);
        name = (EditText) findViewById(R.id.entername);
        eid = (EditText) findViewById(R.id.enterid);
        buttonSignup = (Button) findViewById(R.id.buttonSignup);
        progressDialog = new ProgressDialog(this);
        //attaching listener to button
        buttonSignup.setOnClickListener(this);
        textViewSignin.setOnClickListener(this);
    }

    private void registerUser(){

        //getting email and password from edit texts
        email = editTextEmail.getText().toString().trim();
        password  = editTextPassword.getText().toString().trim();
        name1 = name.getText().toString();
        eid1 = eid.getText().toString();
        //checking if email and passwords are empty
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter email",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter password",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(name1)){
            Toast.makeText(this,"Please enter name",Toast.LENGTH_LONG).show();
            return;
        }

        if(TextUtils.isEmpty(eid1)){
            Toast.makeText(this,"Please enter enrollment number",Toast.LENGTH_LONG).show();
            return;
        }
        //if the email and password,name,eid are not empty
        //displaying a progress dialog
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();
        current_user_db2 = FirebaseDatabase.getInstance().getReference().child("users").child("studentdata");
        current_user_db2.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(eid1)) {
                    Toast.makeText(MainActivity.this,"this enrollment id already exist",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
                else
                {
                    //creating a new user
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    //checking if success
                                    if(task.isSuccessful()){

                                        String user_id = firebaseAuth.getCurrentUser().getUid();
                                        DatabaseReference current_user_db = FirebaseDatabase.getInstance().getReference().child("users").child("student").child(user_id);
                                        User1 user = new User1(name1,eid1);
                                        current_user_db.setValue(user);
                                        DatabaseReference current_user_db1 = FirebaseDatabase.getInstance().getReference().child("users").child("studentdata").child(eid1);
                                        User2 user2=new User2(name1);
                                        current_user_db1.setValue(user2);
                                        finish();
                                        Toast.makeText(MainActivity.this,"Registration Successfull",Toast.LENGTH_LONG).show();
                                        startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
                                    }else{
                                        //display some message here
                                        Toast.makeText(MainActivity.this,"Registration Error",Toast.LENGTH_LONG).show();
                                    }
                                    progressDialog.dismiss();
                                }
                            });

                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onClick(View view) {

        if(view == buttonSignup){
            if(Function.isNetworkAvailable(getApplicationContext()))
            {
                registerUser();
            }else{
                Toast.makeText(getApplicationContext(), "No Internet Connection", Toast.LENGTH_SHORT).show();
            }
            registerUser();
        }
        if(view == textViewSignin){
            //open login activity when user taps on the already registered textview
            startActivity(new Intent(this, LoginActivity.class));
        }
    }
}
