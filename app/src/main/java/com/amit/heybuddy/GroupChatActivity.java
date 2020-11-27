package com.amit.heybuddy;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity
{
    private Toolbar mToolbar;
    private ImageButton sendMessageBtn;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessage ,displayTimeDate,displayName;

    private String currentGroupName, currentUserId,currentUserName,currentDate,currentTime;
    private FirebaseAuth mAuth;
    private DatabaseReference usersRef,groupNameRef,groupMsgKeyRef;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        currentGroupName= getIntent().getExtras().get("GroupName").toString();
        mToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(currentGroupName);



        //Toast.makeText(this, ""+currentGroupName, Toast.LENGTH_SHORT).show();
        InitializeFields();

        mAuth= FirebaseAuth.getInstance();
        currentUserId= mAuth.getCurrentUser().getUid();
        usersRef= FirebaseDatabase.getInstance().getReference().child("Users");
        groupNameRef= FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName);

        getUserInfo();

        sendMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveMsgInfoToDatabase();

                userMessageInput.setText(null);
                mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        groupNameRef .addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if (snapshot.exists())
                {
                    DisplayMessages(snapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName)
            {
                if (snapshot.exists())
                {
                    DisplayMessages(snapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void InitializeFields()
    {
        sendMessageBtn = findViewById(R.id.send_message_btn);
        userMessageInput = findViewById(R.id.input_group_message);
        //displayName = findViewById(R.id.group_chat_Name);
        displayTextMessage = findViewById(R.id.group_chat_msg);
        //displayTimeDate = findViewById(R.id.group_chat_datetime);
        mScrollView = findViewById(R.id.my_scroll_view);
    }

    private void getUserInfo()
    {
        usersRef.child(currentUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    currentUserName=dataSnapshot.child("name").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void SaveMsgInfoToDatabase()
    {
        String message= userMessageInput.getText().toString();
        String msgKey= groupNameRef.push().getKey();
        if(TextUtils.isEmpty(message))
        {
            Toast.makeText(this, "Enter Message", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat= new SimpleDateFormat("MMM dd,YYYY");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat= new SimpleDateFormat("hh:mm:ss a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String , Object> groupMsgKey=new HashMap<>();
            groupNameRef.updateChildren(groupMsgKey);

            groupMsgKeyRef = groupNameRef.child(msgKey);

            HashMap<String,Object> msgInfoMap = new HashMap<>();
                msgInfoMap.put("name",currentUserName);
                msgInfoMap.put("message",message);
                msgInfoMap.put("date",currentDate);
                msgInfoMap.put("time",currentTime);

             groupMsgKeyRef.updateChildren(msgInfoMap);
        }
    }


    private void DisplayMessages(DataSnapshot snapshot)
    {
        Iterator iterator= snapshot.getChildren().iterator();

        while (iterator.hasNext())
        {
            String chatDate=(String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage=(String)((DataSnapshot)iterator.next()).getValue();
            String chatName=(String)((DataSnapshot)iterator.next()).getValue();
            String chatTime=(String)((DataSnapshot)iterator.next()).getValue();

            //displayName.setText(chatName);
            //displayTextMessage.setText(chatMessage);
            displayTextMessage.append(chatName+ " :\n" + chatMessage  + " \n" +chatTime + " " +chatDate+" \n\n\n" );
           // displayTimeDate.setText(chatTime + " " +chatDate);
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

}