package com.personal.debrian.squareone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity {

    private EditText nameInput;
    private EditText passwordInput;
    private Button loginBtn;
    private TextView createBtn;

    private FirebaseAuth myAuth;
    private FirebaseAuth.AuthStateListener authListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        nameInput = (EditText) findViewById(R.id.nameInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        createBtn = (TextView) findViewById(R.id.createAccountBtn);

        myAuth = FirebaseAuth.getInstance();
        authListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if(user != null){
                    //User signed in
                    Log.d("State","signed in");
                }else{
                    //User signed out
                }
            }
        };

        //// Logging in ////
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = nameInput.getText().toString();
                String password = passwordInput.getText().toString();

                if(!username.isEmpty() && !password.isEmpty()) {
                    myAuth.signInWithEmailAndPassword(username, password)
                            .addOnCompleteListener(Login.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(Login.this, "Sign in Failed",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        startActivity(new Intent(Login.this, MainActivity.class));

                                    }
                                }
                            });
                }else{
                    Toast.makeText(getApplicationContext(),"Empty Username or Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
        // End of Logging in

        //Creating Account
        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this,CreateAccountActivity.class));
            }
        });
        /// End of creating account
    }

    @Override
    protected void onStart() {
        super.onStart();
        myAuth.addAuthStateListener(authListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authListener != null)
            myAuth.removeAuthStateListener(authListener);
    }
}
