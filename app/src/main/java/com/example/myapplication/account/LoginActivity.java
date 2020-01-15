package com.example.myapplication.account;

import com.example.myapplication.MainMenuActivity;
import com.example.myapplication.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    int RC_SIGN_IN = 0;
    private FirebaseAuth firebaseAuth;
    private ImageButton customFbButton;
    private CallbackManager callbackManager;
    EditText et1, et2;
    Button btn;
    SignInButton google, twitter;
    GoogleSignInClient googleSignInClient;
    LoginButton loginButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        et1 = (EditText) findViewById(R.id.email);
        et2 = (EditText) findViewById(R.id.password);
        btn = (Button) findViewById(R.id.loginButton);
        TextView forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        google = findViewById(R.id.googleBtn);
        loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        customFbButton = (ImageButton) findViewById(R.id.customFbButton);
        firebaseAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        reDesignGoogleButton(google);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = et1.getText().toString().trim();
                String s2 = et2.getText().toString().trim();

                if (s1.equals("") || s2.equals("")) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fillfields), Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(s1, s2)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        boolean emailVerified = user.isEmailVerified();
                                        if (emailVerified) {
                                            startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                                        } else {
                                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.activate_acc), Toast.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.wrongdetails), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        //Facebook Logging IN
        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v.getId() == R.id.googleBtn) {
                    signIn();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            boolean user = currentUser.isEmailVerified();
            if (user) {
                Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                startActivity(intent);
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.welcome) + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                Log.w("TAG", "Google sign in failed", e);
            }
        }
    }

    private void handleFacebookAccessToken(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.welcome) + user.getEmail(), Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.somethingwrong),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.somethingwrong), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void goToTheRegisterIntent(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    public void reDesignGoogleButton(SignInButton signInButton) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            final View v = signInButton.getChildAt(i);
            v.setBackgroundResource(android.R.color.transparent); //setting transparent color that will hide google image and white backgroun
            return;
        }
    }

    public void reDesignFbButton(View view) {
        if (view == customFbButton) {
            loginButton.performClick();
        }
    }

    boolean pressedOnceBackButton = false;

    @Override
    public void onBackPressed() {
        if (pressedOnceBackButton) {
            this.finishAffinity();
            System.exit(0);
            return;
        }

        this.pressedOnceBackButton = true;

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pressedOnceBackButton = false;
            }
        }, 1500);
        Toast.makeText(this, getResources().getString(R.string.exit), Toast.LENGTH_SHORT).show();
    }
}