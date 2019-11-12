package com.example.myapplication.account;

import com.example.myapplication.MainMenuActivity;
import com.example.myapplication.R;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;

import android.content.Intent;
import android.net.Uri;
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

public class LoginActivity extends AppCompatActivity {

    int RC_SIGN_IN = 0;
    private FirebaseAuth firebaseAuth;
    private TextView forgotPassword;
    private ImageButton customFbButton;
    private CallbackManager callbackManager;
    EditText et1,et2;
    Button btn;
    SignInButton google,twitter;
    GoogleSignInClient googleSignInClient;
    LoginButton loginButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        //connect code objects with their layout objects
        et1 = (EditText) findViewById(R.id.email);
        et2 = (EditText) findViewById(R.id.password);
        btn = (Button) findViewById(R.id.loginButton);
        forgotPassword = (TextView) findViewById(R.id.forgotPassword);
        google = findViewById(R.id.googleBtn);
        loginButton = (LoginButton) findViewById(R.id.facebook_login_button);
        customFbButton = (ImageButton) findViewById(R.id.customFbButton);
        firebaseAuth = FirebaseAuth.getInstance();

        //data which google will get
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        reDesignGoogleButton(google);

        //Logging in by email+password
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s1 = et1.getText().toString().trim();
                String s2 = et2.getText().toString().trim();

                if (s1.equals("") || s2.equals("")) {
                    Toast.makeText(getApplicationContext(), "Uzupełnij wszystkie pola", Toast.LENGTH_SHORT).show();
                } else {
                    firebaseAuth.signInWithEmailAndPassword(s1, s2)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = firebaseAuth.getCurrentUser();
                                        startActivity(new Intent(getApplicationContext(), MainMenuActivity.class));
                                        Toast.makeText(LoginActivity.this, "Pomyślnie zalogowano", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Użytkownik nie istnieje lub podane dane są niepoprawne", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });

        //when user pressed "forgot password textView"
        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class));
            }
        });

        //Facebook Logging IN
        //Facebook step results: success/canceled/error
        callbackManager = CallbackManager.Factory.create();

        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("success: ", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("Cancelled: ", "facebook:onCancel");
                // ...
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


//Google logging in
        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.googleBtn:
                        signIn();
                        break;
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser!=null) {
            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
            startActivity(intent);
            Toast.makeText(getApplicationContext(), "Zostałeś zalogowany" + currentUser.getEmail(), Toast.LENGTH_SHORT).show();
        }

    }

    // google start sign in
    private void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    // Google/facebook codes and data
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
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e);
                // ...
            }
        }
    }

    // Facebook behaviour when client logged success or failed
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("loggin", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            startActivity(new Intent(LoginActivity.this, MainMenuActivity.class));
                            finish();
                            Toast.makeText(getApplicationContext(), "Zostałeś zalogowany" + user.getEmail(), Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("error: ", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d("TAG", "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential: success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            Intent intent = new Intent(LoginActivity.this, MainMenuActivity.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                           Toast.makeText(LoginActivity.this, "Something went wrong while adding to firebase",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //go to the RegisterActivity
    public void goToTheRegisterIntent(View view){
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }

    //change appearance of google button
    public void  reDesignGoogleButton(SignInButton signInButton) {
        for (int i = 0; i<signInButton.getChildCount(); i++) {
            final View v = signInButton.getChildAt(i);
                v.setBackgroundResource(android.R.color.transparent); //setting transparent color that will hide google image and white backgroun
            return;
        }
    }
    //change appearance of facebook button
    public void reDesignFbButton(View view){
        if (view == customFbButton) {
            loginButton.performClick();
        }
    }

    boolean pressedOnceBackButton = false;
    @Override
    public void onBackPressed() {
        if (pressedOnceBackButton){
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
        },1500);
        Toast.makeText(this, "Press BACK twice to exit the application", Toast.LENGTH_SHORT).show();
    }
}