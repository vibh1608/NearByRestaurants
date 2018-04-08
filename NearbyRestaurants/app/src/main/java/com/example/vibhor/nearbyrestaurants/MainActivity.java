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
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
{
    private FirebaseAuth mAuth;

    TextView signupBtn,LoginBtn;
    EditText email,Password;
    DatabaseReference customerReference = FirebaseDatabase.getInstance().getReference("users").child("Guest");
    DatabaseReference ownerReference = FirebaseDatabase.getInstance().getReference("users").child("Owner");
    ArrayList<UserDetails> userList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        setContentView(R.layout.activity_main);

        signupBtn = findViewById(R.id.signBtn);
        LoginBtn = findViewById(R.id.loginBtn);
        email = findViewById(R.id.emailId);
        Password = findViewById(R.id.password);

        if(isIntenetConnected())
        {
            customerReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        UserDetails userDetails = new UserDetails();
                        userDetails.setUserEmail(String.valueOf(snapshot.child("EmailID").getValue()));
                        userDetails.setUserKey(snapshot.getKey());
                        userDetails.setUsertype(String.valueOf(snapshot.child("Usertype").getValue()));

                        userList.add(userDetails);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }

        if(isIntenetConnected())
        {
            ownerReference.addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren())
                    {
                        UserDetails userDetails = new UserDetails();
                        userDetails.setUserEmail(String.valueOf(snapshot.child("EmailID").getValue()));
                        userDetails.setUserKey(snapshot.getKey());
                        userDetails.setUsertype(String.valueOf(snapshot.child("Usertype").getValue()));

                        userList.add(userDetails);
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError)
                {

                }
            });
        }

        signupBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(intent);
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if(isIntenetConnected())
                {
                    userLogin();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"No Internet Connection Found",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void userLogin()
    {
        final String emailID = email.getText().toString();
        String password = Password.getText().toString();

        if(TextUtils.isEmpty(emailID))
        {
            email.setError("Please Enter Email ID");
            email.requestFocus();
            return;
        }

        if(TextUtils.isEmpty(password))
        {
            Password.setError("Please Enter Password");
            Password.requestFocus();
            return;
        }

        if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
        {
            email.setError("Wrong Email");
            email.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailID, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task)
            {
                if (task.isSuccessful())
                {
                    int flag=0;
                    FirebaseUser user = mAuth.getCurrentUser();

                    if(user.isEmailVerified())
                    {
                        for(int i=0;i<userList.size();i++)
                        {
                            if(userList.get(i).getUserEmail().equals(emailID))
                            {
                                Intent intent = new Intent(MainActivity.this,HomeScreen.class);
                                intent.putExtra("Key",userList.get(i).getUserKey());
                                intent.putExtra("Usertype",userList.get(i).getUsertype());
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                Toast.makeText(MainActivity.this, userList.get(i).getUsertype(), Toast.LENGTH_SHORT).show();
                                flag=1;
                                break;
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Email Not Verified", Toast.LENGTH_SHORT).show();
                    }

                    if(flag==0)
                    {
                        Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Toast.makeText(MainActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
