package com.example.myapplication.views.account;

import com.example.myapplication.views.MainMenuActivity;
import com.example.myapplication.R;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private static final int BACK_BUTTON_EXIT_DELAY = 1500;
    private FirebaseAuth firebaseAuth;
    boolean pressedOnceBackButton = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
        firebaseAuth = FirebaseAuth.getInstance();
        EditText et1 = findViewById(R.id.email);
        EditText et2 = findViewById(R.id.password);
        Button btn = findViewById(R.id.loginButton);
        TextView forgotPassword = findViewById(R.id.forgotPassword);

        buildLogin(btn, et1, et2);
        forgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        checkLoginStatus(currentUser);
    }

    private void checkLoginStatus(FirebaseUser user) {
        if (user != null) {
            if (user.isEmailVerified()) {
                finish();
                startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.welcome) + user.getEmail(), Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(LoginActivity.this, getResources().getString(R.string.activate_acc), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void goToTheRegisterIntent(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    private void buildLogin(Button btn, final EditText et1, final EditText et2) {
        btn.setOnClickListener(v -> {
            String s1 = et1.getText().toString().trim();
            String s2 = et2.getText().toString().trim();

            if (s1.isEmpty() || s2.isEmpty()) {
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.fillfields), Toast.LENGTH_SHORT).show();
            } else {
                firebaseAuth.signInWithEmailAndPassword(s1, s2)
                        .addOnCompleteListener(LoginActivity.this, task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                checkLoginStatus(user);
                            } else {
                                Toast.makeText(LoginActivity.this, getResources().getString(R.string.wrongdetails), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }


    @Override
    public void onBackPressed() {
        if (pressedOnceBackButton) {
            finishAffinity();
            System.exit(0);
            return;
        }
        this.pressedOnceBackButton = true;
        new Handler().postDelayed(() -> pressedOnceBackButton = false, BACK_BUTTON_EXIT_DELAY);
        Toast.makeText(this, getResources().getString(R.string.exit), Toast.LENGTH_SHORT).show();
    }
}