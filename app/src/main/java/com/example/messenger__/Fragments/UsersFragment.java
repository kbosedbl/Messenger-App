package com.example.messenger__.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.messenger__.Adapter.user_adapter;
import com.example.messenger__.Dashboard;
import com.example.messenger__.R;
import com.example.messenger__.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class UsersFragment extends Fragment {
    private RecyclerView recyclerView;
    private user_adapter userAdapter;
    private List<User> mUsers;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView=view.findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers=new ArrayList<>();
        readUsers();
        return view;
    }

    private void readUsers() {
        final FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                String mail=firebaseUser.getEmail();
                String ssp1="";
                for(int i=mail.length()-1;i>=0;i--){
                    char ch=mail.charAt(i);
                    if(ch=='.'||ch=='@'||ch=='#')
                        continue;
                    else
                        ssp1=ssp1+ch;
                }
                for(DataSnapshot dataSnapshot1:dataSnapshot.getChildren()){
                    User user=dataSnapshot1.getValue(User.class);
                    assert user!=null;
                    assert firebaseUser!=null;
                    String ss=user.getEmail();
                    String ssp="";
                    for(int i=ss.length()-1;i>=0;i--){
                        char ch=ss.charAt(i);
                        if(ch=='.'||ch=='@'||ch=='#')
                            continue;
                        else
                            ssp=ssp+ch;
                    }
                    if(ss.length()==0){
                        Toast.makeText(getContext(),"Hello world!!!",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //ss = ss.substring(0, ss.length() - 2);
                        if (!ssp.equals(ssp1)) {
                            //String curusrname = firebaseUser.getDisplayName();
                            assert user.getUsername() != null;
                            //Toast.makeText(getContext(), user.getUserId(), Toast.LENGTH_SHORT).show();
                            mUsers.add(user);
                        }
                    }
                }
                userAdapter=new user_adapter(getContext(),mUsers);
                recyclerView.setAdapter(userAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
