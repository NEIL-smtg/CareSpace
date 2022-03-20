package com.example.carespace.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.carespace.MainPage.MainPage;
import com.example.carespace.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Login extends AppCompatActivity {

    //widgets
    private Button GoogleSignIn_btn,register_btn,login_btn;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    //vars
    private final static int RC_SIGN_IN = 123;
    private String username,usermail,id,photoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        GoogleSignIn_btn = (Button) findViewById(R.id.google_signInbtn);
        register_btn = (Button) findViewById(R.id.register_wEmail_btn);
        login_btn = (Button) findViewById(R.id.logInbtn);

        mAuth = FirebaseAuth.getInstance();
        createRequest();

        GoogleSignIn_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GsignIn(); //google sign
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,Register.class);
                startActivity(intent);
                finish();
            }
        });

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Login.this,LogintoExistingAcc.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void GsignIn()
    {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void createRequest()
    {
        //configure Google sign in
        GoogleSignInOptions gso =new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this,gso);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user =mAuth.getCurrentUser();

        if (user != null)
        {
            //jump to menu
            startActivity(new Intent(Login.this, MainPage.class));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
        try {
            // Google Sign In was successful, authenticate with Firebase
            GoogleSignInAccount account = task.getResult(ApiException.class);


            username = account.getDisplayName();
            usermail = account.getEmail();
            id = account.getId();
            photoURL = account.getPhotoUrl().toString();


            firebaseAuthWithGoogle(account.getIdToken());

            SharedPreferences.Editor editor = getApplicationContext()
                    .getSharedPreferences("MyPrefs",MODE_PRIVATE)
                    .edit();

            editor.putString("id",account.getId());
            editor.putString("username",account.getDisplayName());
            editor.putString("usermail", account.getEmail());
            editor.putString("photoURL",account.getPhotoUrl().toString());
            editor.apply();

        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Toast.makeText(Login.this, "sign in failed", Toast.LENGTH_SHORT).show();
        }

    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            //update new user to database
                            reference = FirebaseDatabase.getInstance().getReference("User/"+id);

                            reference.child("info").setValue(username);

                            // Sign in success, update UI with the signed-in user's information
                            Intent intent = new Intent(Login.this, MainPage.class);
                            startActivity(intent);

                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Login.this, "sign in failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}