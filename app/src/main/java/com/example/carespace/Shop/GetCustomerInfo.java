package com.example.carespace.Shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.carespace.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class GetCustomerInfo extends AppCompatActivity {

    //widgets
    private TextView itemName,itemPrice,orderID,orderItemPrice,deliveryCharges,totalPrice,placeOrder;
    private EditText name,contactNum,email,address;
    private Spinner shippingCompany;
    private CircleImageView itemImg;
    private ImageButton back;

    private String[] deliveryChargesList;

    //vars
    private float deliveryfees;
    private float total;
    private String orderid, shipping_company;

    //incoming vars
    private ItemModel model;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_customer_info);

        init();
        setupView();
    }

    private void setupView()
    {
        itemName.setText(model.getItemName());
        Glide.with(this).load(model.getItemImg()).into(itemImg);

        String price = "RM " + model.getItemPrice();
        itemPrice.setText(price);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        orderid = ref.push().getKey();
        orderID.setText(orderid);

        orderItemPrice.setText(price);


        shippingCompany.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                deliveryfees = Float.parseFloat(deliveryChargesList[i]);
                shipping_company = adapterView.getSelectedItem().toString();
                String fees = "RM " + deliveryChargesList[i];
                deliveryCharges.setText(fees);

                total = deliveryfees + Float.parseFloat(model.getItemPrice());
                NumberFormat formatter = NumberFormat.getNumberInstance();
                formatter.setMinimumFractionDigits(2);
                formatter.setMaximumFractionDigits(2);
                String t = "RM " + formatter.format(total);
                totalPrice.setText(t);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });


        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean valid = checkEditTextValidation();

                AlertDialog alertDialog = new AlertDialog.Builder(GetCustomerInfo.this)
                        .setMessage("Please confirm all the detail are correct before place an order.")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                uploadTodb();
                            }
                        })
                        .setNegativeButton("Cancel",null)
                        .show();
            }
        });
    }

    private void uploadTodb()
    {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userUID", user.getUid());
        hashMap.put("itemName",model.getItemName());
        hashMap.put("itemImg",model.getItemImg());
        hashMap.put("itemID",model.getItemID());
        hashMap.put("itemPrice",model.getItemPrice());
        hashMap.put("orderID",orderid);
        hashMap.put("deliveryCharges", deliveryfees+"");
        hashMap.put("totalPrice",total+"");
        hashMap.put("buyerName",name.getText().toString());
        hashMap.put("buyerContactNum",contactNum.getText().toString());
        hashMap.put("buyerAddress",address.getText().toString());
        hashMap.put("shippingCompany",shipping_company);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Purchased Item");
        ref.child(orderid).setValue(hashMap).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(GetCustomerInfo.this, "You have purchased this item !", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(GetCustomerInfo.this, "Failed to purchased this item", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }
        });

    }

    private boolean checkEditTextValidation() {
        boolean validName,validEmail,validContact, validAddress;

        if (name.getText().toString().isEmpty() || name.getText().toString().equals(" "))
        {
            name.setError("Name should not be empty");
            validName = false;
        }
        else
        {
            validName = true;
        }

        if (contactNum.getText().toString().isEmpty() || contactNum.getText().toString().equals(" "))
        {
            contactNum.setError("Contact should not be empty");
            validContact = false;
        }
        else
        {
            validContact = true;
        }

        if (email.getText().toString().isEmpty() || email.getText().toString().equals(" ") || !Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())
        {
            email.setError("Enter a valid e-mail");
            validEmail = false;
        }
        else
        {
            validEmail = true;
        }

        if (address.getText().toString().isEmpty() || address.getText().toString().equals(" "))
        {
            address.setError("Please describe your problem");
            validAddress = false;
        }
        else
        {
            validAddress = true;
        }


        return validName && validAddress && validContact && validEmail ;
    }

    private void init()
    {
        deliveryChargesList = getResources().getStringArray(R.array.shipping_cost);
        model = (ItemModel) getIntent().getSerializableExtra("model");
        itemName = findViewById(R.id.itemName);
        itemImg = findViewById(R.id.img);
        itemPrice = findViewById(R.id.itemPrice);
        orderID = findViewById(R.id.orderID);
        orderItemPrice = findViewById(R.id.price);
        deliveryCharges = findViewById(R.id.deliveryCharges);
        totalPrice = findViewById(R.id.total);
        placeOrder = findViewById(R.id.placeOrderBtn);
        name = findViewById(R.id.name);
        contactNum = findViewById(R.id.contactNum);
        email = findViewById(R.id.email);
        address = findViewById(R.id.address);
        shippingCompany = findViewById(R.id.spinner);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}