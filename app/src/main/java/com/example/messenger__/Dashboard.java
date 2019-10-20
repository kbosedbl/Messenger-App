package com.example.messenger__;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.messenger__.Fragments.ChatFragment;
import com.example.messenger__.Fragments.ProfileFragment;
import com.example.messenger__.Fragments.UsersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Dashboard extends AppCompatActivity {
    CircleImageView circleImageView;
    TextView textView;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    AlertDialog.Builder builder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        circleImageView=findViewById(R.id.profile_image);
        textView=findViewById(R.id.username);
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        builder = new AlertDialog.Builder(this);
        String mail=firebaseUser.getEmail();
        String ssp="";
        for(int i=mail.length()-1;i>=0;i--){
            char ch=mail.charAt(i);
            if(ch=='.'||ch=='@'||ch=='#')
                continue;
            else
                ssp=ssp+ch;
        }
        databaseReference= FirebaseDatabase.getInstance().getReference("Users").child(ssp);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user=dataSnapshot.getValue(User.class);
                textView.setText(user.getUsername());
                if(user.getImageURL().equals("default")){
                    circleImageView.setImageResource(R.mipmap.ic_launcher);
                }
                else{
                    Glide.with(Dashboard.this).load(user.getImageURL()).into(circleImageView);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        TabLayout tabLayout=findViewById(R.id.tablayout);
        ViewPager viewPager=findViewById(R.id.view_pager);
        ViewPagerAdapter viewPagerAdapter=new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragments(new ChatFragment(),"Chats");
        viewPagerAdapter.addFragments(new UsersFragment(),"Users");
        viewPagerAdapter.addFragments(new ProfileFragment(),"Profile");
        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        findViewById(R.id.logout2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.setMessage("Wanna logout?").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        FirebaseAuth.getInstance().signOut();
                        startActivity(new Intent(Dashboard.this,StartActivity.class));
                        finish();
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();
                    }
                });
                AlertDialog alert = builder.create();
                alert.setTitle("LOGOUT");
                alert.show();
            }
        });
    }
    class ViewPagerAdapter extends FragmentPagerAdapter{
        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;
        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }
        @NonNull
        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragments(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
    private void status(String status){
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(hash(firebaseUser.getEmail()));
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("status",status);
        databaseReference.updateChildren(hashMap);
    }
    private String hash(String email) {
        String t=email;
        email="";
        for(int i=t.length()-1;i>=0;i--){
            char c=t.charAt(i);
            if(c!='@'&&c!='#'&&c!='.') {
                email += c;
            }
        }
        return email;
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("Offline");
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("Online");
    }
}
