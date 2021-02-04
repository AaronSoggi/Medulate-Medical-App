package com.example.mobile_interactions_aaron_soggi;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity {

    public Button CreateAccount;
    public EditText editTextFullName, editTextEmail, editTextPassword;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        editTextFullName = (EditText) findViewById(R.id.signUpName);
        editTextEmail = (EditText) findViewById(R.id.signUpEmail);
        editTextPassword = (EditText) findViewById(R.id.signUpPassword);

        mAuth = FirebaseAuth.getInstance();



        CreateAccount = findViewById(R.id.signIn);
        CreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });

    }

    private void createAccount() {
        String fullName =editTextFullName.getText().toString().trim();
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();


        if(fullName.isEmpty())
        {
            editTextFullName.setError("Full Name is Required!");
            editTextFullName.requestFocus();
            return;
        }
        if(email.isEmpty())
        {
            editTextEmail.setError("Email is Required!");
            editTextEmail.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
        {
            editTextEmail.setError("Please provide a valid email!");
            editTextEmail.requestFocus();
            return;
        }
        if(password.isEmpty())
        {
            editTextPassword.setError("Password is required!");
            editTextPassword.requestFocus();
            return;
        }
        if(password.length() < 6)
        {
            editTextPassword.setError("Password must be at least 6 characters!");
            editTextPassword.requestFocus();
            return;
        }


        // register user in firebase
        
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    User user = new User(fullName,email);

                    FirebaseDatabase.getInstance().getReference("Users")
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(RegisterUser.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(RegisterUser.this,MainActivity.class));

                            }else{
                                Toast.makeText(RegisterUser.this, "An error has occurred, please try again", Toast.LENGTH_LONG).show();
                            }

                        }
                    });
                }else {
                    Toast.makeText(RegisterUser.this, "Error, this email address is already been registered with", Toast.LENGTH_SHORT).show();

                }
            }
        });

    }
}