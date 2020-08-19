package com.example.myapplication.views.account;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.myapplication.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class RegisterActivity extends AppCompatActivity {
    private static final String VERIFICATION_LINK_STATUS_LOG = "Verification email: ";
    private FirebaseAuth firebaseAuth;
    EditText email_editText, password_EditText, passwordRepEditText;
    Button confirmButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        email_editText = findViewById(R.id.email);
        password_EditText = findViewById(R.id.password);
        passwordRepEditText = findViewById(R.id.password_repeat);
        confirmButton = findViewById(R.id.register_button);
        firebaseAuth = FirebaseAuth.getInstance();

        confirmButton.setOnClickListener(v -> {
            String email = email_editText.getText().toString();
            String password = password_EditText.getText().toString();
            String passwordRepeat = passwordRepEditText.getText().toString();
            boolean areNotEmpty = Validation.areNotEmpty(email, password, passwordRepeat);
            boolean checkPassword = Validation.passwordCheck(password);
            if (areNotEmpty && checkPassword && password.equals(passwordRepeat)) {
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                assert user != null;
                                sendActivationLink(user);
                                FirebaseAuth.getInstance().signOut();
                                finish();
                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.registersuccess), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                Toast.makeText(RegisterActivity.this, getResources().getString(R.string.wrong_input), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendActivationLink(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(VERIFICATION_LINK_STATUS_LOG, "sent");
                    }
                });
    }
}