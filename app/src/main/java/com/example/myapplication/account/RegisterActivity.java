package com.example.myapplication.account;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.facebook.login.LoginManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    EditText et1, et2, et3;
    Button bt1;

    Validation val = new Validation();
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        et1 = (EditText) findViewById(R.id.email);
        et2 = (EditText) findViewById(R.id.pass);
        et3 = (EditText) findViewById(R.id.passRepeat);

        bt1 = (Button) findViewById(R.id.regBtn);

        firebaseAuth = FirebaseAuth.getInstance();

        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String s1 = et1.getText().toString();
                String s2 = et2.getText().toString();
                String s3 = et3.getText().toString();
                boolean areNotEmpty = val.areNotEmpty(s1, s2, s3);
                if (areNotEmpty) {
                    boolean checkPassword = val.passwordCheck(s2);
                    if (checkPassword) {
                        if (s2.equals(s3)) {
                            firebaseAuth.createUserWithEmailAndPassword(s1, s2)
                                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                                sendActivationLink(user);
                                                FirebaseAuth.getInstance().signOut();
                                                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registersuccess), Toast.LENGTH_SHORT).show();

                                            } else {
                                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.emailwrong), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            Toast.makeText(RegisterActivity.this, getResources().getString(R.string.passdiff) , Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(RegisterActivity.this,getResources().getString(R.string.passrules) , Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(RegisterActivity.this, getResources().getString(R.string.fillfields), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void sendActivationLink(FirebaseUser user){
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d("Verification email: ", "sent");
                        }
                    }
                });
    }
}