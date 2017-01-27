package com.personal.debrian.squareone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.auth.UserProfileChangeRequest;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private Button signUpBtn;

    private FirebaseAuth myAuth;
    private FirebaseAuth.AuthStateListener myAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        myAuth = FirebaseAuth.getInstance();

        nameInput = (EditText) findViewById(R.id.new_nameInput);
        emailInput = (EditText) findViewById(R.id.new_emailInput);
        passwordInput = (EditText) findViewById(R.id.new_passwordInput);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);

        myAuthListener = new FirebaseAuth.AuthStateListener(){

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                String name = nameInput.getText().toString();
                if(user != null && !name.isEmpty()){
                    UserProfileChangeRequest userProfile = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                    user.updateProfile(userProfile);
                }
            }
        };
        /// Button Listener
        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = emailInput.getText().toString();
                String password = passwordInput.getText().toString();
                //make sure user entered something
                if(!name.isEmpty() && !password.isEmpty()) {
                    myAuth.createUserWithEmailAndPassword(name, password)
                            .addOnCompleteListener(CreateAccountActivity.this,
                                    new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (!task.isSuccessful()) {
                                                Toast.makeText(CreateAccountActivity.this, "Sign in Failed",
                                                        Toast.LENGTH_SHORT).show();
                                            } else {
                                                myAuth.addAuthStateListener(myAuthListener);
                                                Toast.makeText(getApplicationContext(),"Account Created",Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(CreateAccountActivity.this, Login.class));
                                                finish();
                                            }
                                        }
                                    });
                }
            }
        });

        /// end of sign up listener

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(myAuthListener != null)
            myAuth.removeAuthStateListener(myAuthListener);
    }
}
