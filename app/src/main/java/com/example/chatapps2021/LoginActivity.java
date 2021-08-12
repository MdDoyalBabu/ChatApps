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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

public class LoginActivity extends AppCompatActivity {


    private EditText email,password;
    private Button login_btn;
    private TextView forget_pwd;
    private FirebaseAuth auth;

    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

       /* Toolbar toolbar=findViewById(R.id.bar_layout_login);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registered");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        */
        auth = FirebaseAuth.getInstance();

        progressDialog=new ProgressDialog(this);

        password=findViewById(R.id.password_id);
        email=findViewById(R.id.email_id);
        forget_pwd=findViewById(R.id.forget_password);

        login_btn=findViewById(R.id.login_btn);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email_l=email.getText().toString();
                String password_l=password.getText().toString();

                login(email_l,password_l);

            }
        });

        forget_pwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ResetPasswordActivity.class);
                startActivity(intent);
            }
        });


    }

    private void login(String email_l, String password_l) {

        progressDialog.setTitle("Loading...........");
        progressDialog.setMessage("Please wait a few seconds..");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        auth.signInWithEmailAndPassword(email_l,password_l).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                }
                else {
                    String message=task.getException().getMessage();
                    Toast.makeText(LoginActivity.this, "Error Message "+message, Toast.LENGTH_SHORT).show();

                    progressDialog.dismiss();
                }
            }
        });

    }
}