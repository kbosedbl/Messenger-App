package com.example.messenger__;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    TextInputEditText email,username,password;
    FirebaseAuth auth;
    DatabaseReference firebaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        email= findViewById(R.id.email);
        username=findViewById(R.id.username);
        password= findViewById(R.id.password);
        auth= FirebaseAuth.getInstance();
        findViewById(R.id.reg).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txt_username=username.getText().toString();
                String txt_password=password.getText().toString();
                String txt_emailId=email.getText().toString();
                if(txt_emailId.isEmpty()||txt_username.isEmpty()||txt_password.isEmpty()){
                    Toast.makeText(MainActivity.this,"Please fill all the fields",Toast.LENGTH_SHORT).show();
                }
                else if(txt_password.length()<5)
                    Toast.makeText(MainActivity.this,"Password should be minimum of 5 letters",Toast.LENGTH_SHORT).show();
                else
                    request(txt_username,txt_emailId,txt_password);
            }
        });
    }
    private void request(final String username,String email,String password){
        auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    FirebaseUser firebaseUser=auth.getCurrentUser();
                    String id=firebaseUser.getUid();
                    String userId=firebaseUser.getUid();
                    String mail=firebaseUser.getEmail();
                    String ssp="";
                    for(int i=mail.length()-1;i>=0;i--){
                        char ch=mail.charAt(i);
                        if(ch=='.'||ch=='@'||ch=='#')
                            continue;
                        else
                            ssp=ssp+ch;
                    }
                    firebaseReference=FirebaseDatabase.getInstance().getReference("Users").child(ssp);
                    HashMap<String,String> hashMap=new HashMap<>();
                    hashMap.put("id",ssp);
                    hashMap.put("username",username);
                    hashMap.put("imageURL","default");
                    hashMap.put("email",firebaseUser.getEmail());
                    firebaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Intent intent=new Intent(MainActivity.this,Dashboard.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                else
                    Toast.makeText(MainActivity.this,"You cannot register with this email and password",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
