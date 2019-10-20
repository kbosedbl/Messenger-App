package com.example.messenger__;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.messenger__.Adapter.MessageAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    CircleImageView profile_image;
    TextView username;
    FirebaseUser fuser;
    DatabaseReference databaseReference;
    ImageButton btn_send;
    TextInputEditText text_send;
    Bundle bundle;
    MessageAdapter messageAdapter;
    List<Chat> mChat;
    RecyclerView recyclerView;
    Intent intent;
    String email,email2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        recyclerView=findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getApplicationContext());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        profile_image=findViewById(R.id.profile_image);
        username=findViewById(R.id.username);
        btn_send=findViewById(R.id.btn_send);
        text_send=findViewById(R.id.txt_send);
        fuser= FirebaseAuth.getInstance().getCurrentUser();
        bundle=getIntent().getExtras();
        email=bundle.getString("emailid");
        email2=fuser.getEmail();
        String mail=email;
        String ssp="";
        for(int i=mail.length()-1;i>=0;i--){
            char ch=mail.charAt(i);
            if(ch=='.'||ch=='@'||ch=='#')
                continue;
            else
                ssp=ssp+ch;
        }
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(ssp);
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email=bundle.getString("emailid");
                String mail=email;
                String ssp="";
                for(int i=mail.length()-1;i>=0;i--){
                    char ch=mail.charAt(i);
                    if(ch=='.'||ch=='@'||ch=='#')
                        continue;
                    else
                        ssp=ssp+ch;
                }
                String email2=fuser.getEmail();
                String mail2=email2;
                String ssp2="";
                for(int i=mail2.length()-1;i>=0;i--){
                    char ch=mail2.charAt(i);
                    if(ch=='.'||ch=='@'||ch=='#')
                        continue;
                    else
                        ssp2=ssp2+ch;
                }
                String message=text_send.getText().toString();
                if(!message.equalsIgnoreCase("")){
                    sendMessage(email2,email,message);
                }
                text_send.setText("");
            }
        });
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                username.setText(user.getUsername());
                if(user.getImageURL().equalsIgnoreCase("default")){
                    profile_image.setImageResource(R.mipmap.ic_launcher);
                }
                else
                    Glide.with(MessageActivity.this).load(user.getImageURL()).into(profile_image);
                readMessages(email2,email,user.getImageURL());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    private void sendMessage(String sender,String receiver,String message){
        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("sender",sender);
        hashMap.put("receiver",receiver);
        hashMap.put("message",message);
        databaseReference.child("Chats").push().setValue(hashMap);
    }
    private void readMessages(final String myId, final String userID, final String imageURL){
        mChat=new ArrayList<>();
        databaseReference=FirebaseDatabase.getInstance().getReference("Chats");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mChat.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Chat chat=snapshot.getValue(Chat.class);
                    if((chat.getReceiver().equals(myId)&&chat.getSender().equals(userID))||(chat.getReceiver().equals(userID)&&chat.getSender().equals(myId))){
                        mChat.add(chat);
                    }
                }
                List<Chat> mChat2=mChat;
                mChat2.add(new Chat(myId,userID,""));
                messageAdapter=new MessageAdapter(MessageActivity.this,mChat,imageURL);
                recyclerView.setAdapter(messageAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
