package com.example.carespace.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.carespace.MainPage.MainPage;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogintoExistingAcc extends AppCompatActivity {

    //widgets
    EditText email,password;
    Button login,back;
    TextView forgotpw;

    //firebase authentication
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginto_existing_acc);

        //widgets
        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_password);
        login = (Button) findViewById(R.id.login_btn);
        back = (Button) findViewById(R.id.login_back_btn);
        forgotpw = (TextView) findViewById(R.id.forgot_pw);

        firebaseAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LogintoExistingAcc.this,Login.class));
                finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkEmptyString())
                {
                    //add progress dialog
                    ProgressDialog dialog = new ProgressDialog(LogintoExistingAcc.this);
                    dialog.setMessage("Logging in....");
                    dialog.show();

                    firebaseAuth.signInWithEmailAndPassword
                            (email.getEditableText().toString(),password.getEditableText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult)
                                {
                                    dialog.dismiss();
                                    Toast.makeText(LogintoExistingAcc.this, "Login successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(), MainPage.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    dialog.dismiss();
                                    Toast.makeText(LogintoExistingAcc.this, "Login failed"+e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        forgotpw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                EditText resetEmail = new EditText(LogintoExistingAcc.this);
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(LogintoExistingAcc.this);
                passwordResetDialog.setTitle("Reset Password ?");
                passwordResetDialog.setMessage("Enter your email address to received reset link.");
                passwordResetDialog.setView(resetEmail);

                passwordResetDialog.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //extract the email and send reset link
                        String mail = resetEmail.getText().toString();
                        firebaseAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused)
                            {
                                Toast.makeText(LogintoExistingAcc.this, "Reset link has been sent to your Email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e)
                            {
                                Toast.makeText(LogintoExistingAcc.this, "Reset link failed to send to your Email "+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordResetDialog.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        //close the dialog
                    }
                });

                passwordResetDialog.create().show();
            }
        });
    }

    private boolean checkEmptyString()
    {
        String em = email.getEditableText().toString();
        String pw = password.getEditableText().toString();

        if (em.isEmpty())
        {
            email.setError("Email is required !!!");
            return false;
        }

        if (pw.isEmpty())
        {
            password.setError("Password is required !!!");
            return false;
        }

        return true;
    }
}