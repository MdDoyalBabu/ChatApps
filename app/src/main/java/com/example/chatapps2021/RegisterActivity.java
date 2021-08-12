package com.example.chatapps2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {


    private EditText name,email,password;
    private Button registration_btn;
    String userId;

   private FirebaseAuth auth;
   private DatabaseReference reference;
   private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


       /* Toolbar toolbar=findViewById(R.id.bar_layout_reg);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registered");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        */
        auth = FirebaseAuth.getInstance();
        reference=FirebaseDatabase.getInstance().getReference().child("Users");
        progressDialog=new ProgressDialog(this);


        name=findViewById(R.id.name_id);
        email=findViewById(R.id.email_id);
        password=findViewById(R.id.password_id);
        registration_btn=findViewById(R.id.reg_btn);


        registration_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name_text=name.getText().toString();
                String email_text=email.getText().toString();
                String password_text=password.getText().toString();

                if (name_text.isEmpty()||email_text.isEmpty()||password_text.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Please fill up this from", Toast.LENGTH_SHORT).show();
                }
                else {

                    registered(name_text,email_text,password_text);
                }

            }
        });

    }

    private void registered(String name_text, String email_text, String password_text) {

        progressDialog.setTitle("Loading...........");
        progressDialog.setMessage("Please wait a few seconds..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();



        auth.createUserWithEmailAndPassword(email_text,password_text).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()){

                    FirebaseUser firebaseUser=auth.getCurrentUser();
                    userId=firebaseUser.getUid();

                    HashMap<String,Object> hashMap=new HashMap<>();

                    hashMap.put("id",userId);
                    hashMap.put("name",name_text);
                    hashMap.put("email",email_text);
                    hashMap.put("imageUrl","default");
                    hashMap.put("status","offline");
                    hashMap.put("search",name_text.toLowerCase());

                    reference.child(userId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful()){
                                Toast.makeText(RegisterActivity.this, "Created Your Account", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();

                                progressDialog.dismiss();


                            }
                            else {

                                String messsageError=task.getException().getMessage();

                                Toast.makeText(RegisterActivity.this, "Error Message"+messsageError, Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();

                            }

                        }
                    });

                }
                else {

                    String messsageError=task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this, "Some Error"+messsageError, Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                }

            }
        });

    }
}