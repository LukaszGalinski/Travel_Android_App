package com.example.myapplication.account;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private Button sendLink;
    private EditText resetEmail;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password_layout);

        firebaseAuth = FirebaseAuth.getInstance();

        resetEmail = (EditText) findViewById(R.id.resetEmailText);
        sendLink = (Button) findViewById(R.id.ResetEmailBtn);

        resetEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userEmail  = resetEmail.getText().toString().trim();

                if (TextUtils.isEmpty(userEmail)){
                    Toast.makeText(ForgotPasswordActivity.this, "Please input your valid email adress", Toast.LENGTH_SHORT).show();
                }
                else{
                    firebaseAuth.sendPasswordResetEmail(userEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ForgotPasswordActivity.this, "Please check your email for password reset", Toast.LENGTH_SHORT).show();

                            }
                            else {
                                Toast.makeText(ForgotPasswordActivity.this, "Email doesnt exist", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }
}
