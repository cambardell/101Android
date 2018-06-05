package online.a101app.a101;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {



    private RecyclerView mMessageRecycler;
    private ChatAdapter mMessageAdapter;
    static DatabaseReference databaseReference;
    String channelId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        // Retrieve the channelId from the intent
        Intent intent = getIntent();
        channelId = intent.getStringExtra("channel");

        databaseReference = FirebaseDatabase.getInstance().getReference();

        List<Message> messageList = new ArrayList<>();
        Message tempMessage = new Message();
        Message tempMessage2 = new Message();
        Message tempMessage3 = new Message();
        Message tempMessage4 = new Message();
        tempMessage.setMessageText("text");
        tempMessage.setSenderId("id1");
        tempMessage.setSenderName("name1");
        tempMessage2.setMessageText("Much longer testing text");
        tempMessage2.setSenderId("id1");
        tempMessage2.setSenderName("name1");
        tempMessage3.setMessageText("text");
        tempMessage3.setSenderId("id1");
        tempMessage3.setSenderName("name1");
        tempMessage4.setMessageText("Some longer text for testing the spacing of the bubbles");
        tempMessage4.setSenderId(FirebaseAuth.getInstance().getCurrentUser().getUid());
        tempMessage4.setSenderName("name1");
        messageList.add(tempMessage);
        messageList.add(tempMessage2);
        messageList.add(tempMessage3);
        messageList.add(tempMessage4);
        Log.d("messages", messageList.toString());

        getMessages(channelId);


        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new ChatAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);
    }

    private static void getMessages(final String channelId) {
        // Last 100 messages
        databaseReference.child("channels").child(channelId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("messages", dataSnapshot.child("messages").toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }




}
