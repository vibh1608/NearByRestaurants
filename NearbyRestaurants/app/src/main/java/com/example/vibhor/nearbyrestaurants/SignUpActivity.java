package com.example.vibhor.nearbyrestaurants;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity
{

    EditText email,password,confirmpassword;
    TextView signupBtn;
    Spinner usertype;

    private FirebaseAuth mAuth;
    DatabaseReference databaseReference;
    public static final String usertype1 = "Guest";
    public static final String usertype2 = "Owner";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usertype = findViewById(R.id.spinner_usertype);
        email = findViewById(R.id.emailRegister);
        password = findViewById(R.id.passwordRegister);
        confirmpassword = findViewById(R.id.confirmPassword);
        signupBtn = findViewById(R.id.SignupBtn);

        mAuth = FirebaseAuth.getInstance();

        signupBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(isIntenetConnected())
                {
                    registerUser();
                }
                else
                {
                    Toast.makeText(SignUpActivity.this,"No Internet Connection Found",Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void registerUser()
    {
        final String emailID = email.getText().toString();
        final String pass = password.getText().toString();
        String confirm = confirmpassword.getText().toString();

        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
        {
            email.setError("Wrong Email");
            email.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(pass))
        {
            password.setError("Please Enter Password");
            password.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(confirm))
        {
            confirmpassword.setError("Please Confirm Password");
            confirmpassword.requestFocus();
            return;
        }

        if(!TextUtils.equals(pass,confirm))
        {
            confirmpassword.setError("Password not matching");
            confirmpassword.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(emailID))
        {
            email.setError("Please Enter Email ID");
            email.requestFocus();
            return;
        }

        mAuth.createUserWithEmailAndPassword(emailID,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    //get current user
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    databaseReference = FirebaseDatabase.getInstance().getReference("users");

                    if(usertype.getSelectedItem().toString().equals(usertype1))
                    {
                        String id = databaseReference.push().getKey();
                        databaseReference.child("Guest").child(id).child("Password").setValue(pass);
                        databaseReference.child("Guest").child(id).child("EmailID").setValue(emailID);
                        databaseReference.child("Guest").child(id).child("Usertype").setValue(usertype1);
                    }
                    else if(usertype.getSelectedItem().toString().equals(usertype2))
                    {
                        String id = databaseReference.push().getKey();
                        databaseReference.child("Owner").child(id).child("Password").setValue(pass);
                        databaseReference.child("Owner").child(id).child("EmailID").setValue(emailID);
                        databaseReference.child("Owner").child(id).child("Usertype").setValue(usertype2);
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Name").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Address").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Lattitude").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Longitude").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Menu").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("Timings").setValue("");
                        databaseReference.child("Owner").child(id).child("RestaurantDetails").child("ContactInfo").setValue("");

                    }

                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>()
                    {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            Toast.makeText(SignUpActivity.this, "Verification link send to your registered gmail account" +
                                    ",First verify then login", Toast.LENGTH_LONG).show();
                        }
                    });

                    Intent intent = new Intent(SignUpActivity.this,MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                else
                {
                    if(task.getException() instanceof FirebaseAuthUserCollisionException)
                    {
                        Toast.makeText(getApplicationContext(),"Already registered with this email",Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(),task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //checking connection status of the device
    private boolean isIntenetConnected()
    {
        //initialising status of the device whether is connected to internet or not
        boolean isConnected = false;

        //creating connectivityManager object to check connection status
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        //getting network information
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

        //if connected to internet then changing its value
        if (networkInfo != null) {
            isConnected = true;
        }

        //returning connection status
        return isConnected;
    }
}