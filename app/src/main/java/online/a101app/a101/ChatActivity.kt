package online.a101app.a101

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import com.google.firebase.database.*
import java.util.ArrayList

class ChatActivity2 : AppCompatActivity() {
    private var mMessageRecycler: RecyclerView? = null
    lateinit var mMessageAdapter: ChatAdapter
    lateinit var databaseReference: DatabaseReference
    lateinit var channelId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat)

        // Retrieve the channelId from the intent
        val intent = intent
        channelId = intent.getStringExtra("channel")
        databaseReference = FirebaseDatabase.getInstance().reference


        val messagesList = ArrayList<Message>()

        val query = databaseReference.child("channels").child(channelId).limitToLast(25)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val items = dataSnapshot.child("messages").children.iterator()
                messagesList.clear()
                while (items.hasNext()) {
                    val item = items.next() as DataSnapshot
                    val message = Message.create()
                    message.senderName = item.child("senderName").value?.toString()
                    message.senderId = item.child("senderId").value!!.toString()
                    message.messageText = item.child("text").value?.toString()
                    message.photoUrl = item.child("photoURL").value?.toString()

                    messagesList.add(message)
                }
                mMessageAdapter.notifyDataSetChanged()

                mMessageRecycler!!.scrollToPosition(mMessageAdapter.itemCount - 1)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })


        mMessageRecycler = findViewById<RecyclerView>(R.id.reyclerview_message_list)
        mMessageAdapter = ChatAdapter(this, messagesList)
        mMessageRecycler!!.layoutManager = LinearLayoutManager(this)
        mMessageRecycler!!.adapter = mMessageAdapter
    }
}
