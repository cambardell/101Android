package online.a101app.a101;

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
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ChatActivity extends AppCompatActivity {



    private RecyclerView mMessageRecycler;
    static ChatAdapter mMessageAdapter;
    static DatabaseReference databaseReference;
    static List<Message> messageList;
    String channelId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        // Retrieve the channelId from the intent
        Intent intent = getIntent();
        channelId = intent.getStringExtra("channel");
        databaseReference = FirebaseDatabase.getInstance().getReference();

        messageList = getMessages(channelId);

        mMessageRecycler = (RecyclerView) findViewById(R.id.reyclerview_message_list);
        mMessageAdapter = new ChatAdapter(this, messageList);
        mMessageRecycler.setLayoutManager(new LinearLayoutManager(this));
        mMessageRecycler.setAdapter(mMessageAdapter);
    }

    private static List<Message> getMessages(final String channelId) {
        // Last 100 messages
        final List<Message> messagesList = new ArrayList<Message>();

        databaseReference.child("channels").child(channelId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterator items = dataSnapshot.child("messages").getChildren().iterator();
                while (items.hasNext()) {
                    DataSnapshot item = ((DataSnapshot) items.next());
                    Message message = Message.Factory.create();
                    message.setSenderName(item.child("senderName").getValue().toString());
                    message.setSenderId(item.child("senderId").getValue().toString());
                    message.setMessageText(item.child("text").getValue().toString());
                    Log.d("message", message.getSenderName());
                    messagesList.add(message);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return messagesList;
    }




}
