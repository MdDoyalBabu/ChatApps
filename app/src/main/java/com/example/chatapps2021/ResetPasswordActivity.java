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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.time.temporal.ValueRange;

public class ResetPasswordActivity extends AppCompatActivity {


    EditText input_email;
    Button reset_pwd;

    FirebaseAuth firebaseAuth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        Toolbar toolbar=findViewById(R.id.bar_layout_pwdReset);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Reset Password");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        input_email=findViewById(R.id.send_gmail);
        reset_pwd=findViewById(R.id.reset_password);
        progressDialog=new ProgressDialog(this);

        firebaseAuth=FirebaseAuth.getInstance();

        reset_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                progressDialog.setTitle("Loading......");
                progressDialog.setMessage("PLease wait a few seconds.");
                progressDialog.show();


                String email=input_email.getText().toString();




                if (email.isEmpty()){
                    Toast.makeText(ResetPasswordActivity.this, "please enter your right email", Toast.LENGTH_SHORT).show();
                }
                else {

                    firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "Please check your email", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(ResetPasswordActivity.this,LoginActivity.class));

                                progressDialog.dismiss();

                            }else {

                                String message=task.getException().getMessage();
                                Toast.makeText(ResetPasswordActivity.this, "Error"+message, Toast.LENGTH_SHORT).show();

                                progressDialog.dismiss();

                            }
                        }
                    });
                }




            }
        });



    }
}