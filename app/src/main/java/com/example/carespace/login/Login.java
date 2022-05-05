package com.example.carespace.login;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.developer.gbuttons.GoogleSignInButton;
import com.example.carespace.MainPage.MainPage;
import com.example.carespace.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    //widgets
    private AppCompatButton register_btn,login_btn;
    private GoogleSignInButton GoogleSignIn_btn;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private DatabaseReference reference;

    //vars
    private final static int RC_SIGN_IN = 123;
    private String username,usermail,uid,photoURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(1);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        setContentView(R.layout.activity_login);

        GoogleSignIn_btn =  findViewById(R.id.google_signInbtn);
        register_btn =  findViewById(R.id.register_wEmail_btn);
        login_btn =  findViewById(R.id.logInbtn);

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
            uid = account.getId();
            photoURL = account.getPhotoUrl().toString();

            HashMap<String,Object> hashMap = new HashMap<>();
            hashMap.put("uid",account.getId());
            hashMap.put("name",account.getDisplayName());
            hashMap.put("imgUrl",account.getPhotoUrl().toString());
            hashMap.put("password","");
            hashMap.put("email",account.getEmail());
            hashMap.put("contactNum","");
            hashMap.put("address","");

            firebaseAuthWithGoogle(account.getIdToken(),hashMap);


        } catch (ApiException e) {
            // Google Sign In failed, update UI appropriately
            Toast.makeText(Login.this, "sign in failed", Toast.LENGTH_SHORT).show();
        }

    }

    private void firebaseAuthWithGoogle(String idToken, HashMap<String, Object> hashMap) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                                hashMap.replace("uid",user.getUid());
                            }

                            //update new user to database
                            reference = FirebaseDatabase.getInstance().getReference("User").child(user.getUid());

                            reference.setValue(hashMap);

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