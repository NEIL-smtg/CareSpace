package com.example.carespace.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carespace.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class Register extends AppCompatActivity {

    //widgets
    private Button register_btn,back;
    private EditText email,username,password,Rpassword;
    private CircleImageView picture;

    //firebase
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        register_btn = (Button) findViewById(R.id.register_btn);
        back = (Button) findViewById(R.id.register_back_btn);
        email = (EditText) findViewById(R.id.email);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        Rpassword = (EditText) findViewById(R.id.comfirm_password);
        picture = (CircleImageView) findViewById(R.id.picture);

        firebaseAuth = FirebaseAuth.getInstance();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Register.this,Login.class));
                finish();
            }
        });

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkLengthValidation() && checkEmptyString() && isValidEmail() && checkpwSimilarity())
                {
                    //add progress dialog
                    ProgressDialog dialog = new ProgressDialog(Register.this);
                    dialog.setMessage("Creating account...Please wait....");
                    dialog.show();

                    //if input are all valid, register the user in firebase
                    firebaseAuth.createUserWithEmailAndPassword(email.getEditableText().toString(),password.getEditableText().toString())
                            .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult)
                                {
                                    dialog.dismiss();
                                    Toast.makeText(Register.this, "Account is created successfully", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(getApplicationContext(),LogintoExistingAcc.class));
                                    finish();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    dialog.dismiss();
                                    Toast.makeText(Register.this, "Error !! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });
    }


    private boolean checkLengthValidation()
    {
        if(password.getEditableText().toString().length()<6  )
        {
            password.setError("Password must be more than 6 characters !!");
            return false;
        }

        if(Rpassword.getEditableText().toString().length()<6 )
        {
            Rpassword.setError("Password must be more than 6 characters !!");
            return false;
        }

        return true;
    }

    private boolean checkEmptyString()
    {
        String em = email.getEditableText().toString();
        String usname = username.getEditableText().toString();

        if (em.trim().equals("") || usname.trim().equals(""))
        {
            email.setError("email and username should not be empty !!");
            return false;
        }

        if (usname.trim().equals(""))
        {
            username.setError("email and username should not be empty !!");
            return false;
        }

        return true;
    }

    private boolean isValidEmail()
    {
        CharSequence target = email.getEditableText().toString();

        if ((!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches()) == false)
        {
            email.setError("invalid email");
            return false;
        }

        return true;
    }

    //check similarity of 2 password
    private boolean checkpwSimilarity()
    {
        String pw = password.getEditableText().toString();
        String rpw = Rpassword.getEditableText().toString();

        if (!pw.equals(rpw))
        {
            password.setError("Password is not the same !!");
            Rpassword.setError("Password is not the same !!");
            return false;
        }
        return true;
    }
}