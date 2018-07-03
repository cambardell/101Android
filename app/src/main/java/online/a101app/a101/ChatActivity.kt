package online.a101app.a101

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.ArrayList

class ChatActivity2 : AppCompatActivity() {
    private var mMessageRecycler: RecyclerView? = null
    lateinit var mMessageAdapter: ChatAdapter
    lateinit var databaseReference: DatabaseReference
    lateinit var channelId: String
    lateinit var channelSchool: String
    lateinit var sendButton: Button
    lateinit var textBox: EditText


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat)

        // Retrieve the channelId from the intent
        val intent = intent
        channelId = intent.getStringExtra("channel")
        databaseReference = FirebaseDatabase.getInstance().reference
        channelSchool = intent.getStringExtra("school")

        sendButton = findViewById(R.id.button_chatbox_send)
        textBox = findViewById(R.id.edittext_chatbox)

        sendButton.setOnClickListener {
            sendMessage()
        }

        val messagesList = ArrayList<Message>()

        val query = databaseReference.child("channels").child(channelSchool).child(channelId).limitToLast(50)
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
                    Log.d("message", message.senderName)
                    messagesList.add(message)
                }
                mMessageAdapter.notifyDataSetChanged()

                mMessageRecycler!!.scrollToPosition(mMessageAdapter.itemCount - 1)
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        })

        mMessageRecycler = findViewById<RecyclerView>(R.id.reyclerview_message_list)
        mMessageAdapter = ChatAdapter(messagesList)
        mMessageRecycler!!.layoutManager = LinearLayoutManager(this)
        mMessageRecycler!!.adapter = mMessageAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chat_menu_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.channelInfo -> {
            val intent = Intent(this, ChannelInfoActivity::class.java).apply {

            }
            intent.putExtra("channel", channelId)
            intent.putExtra("school", channelSchool)
            startActivity(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    private fun sendMessage() {
        val messageText = textBox.text.toString()
        val message = Message.create()
        message.senderName = FirebaseAuth.getInstance().currentUser!!.displayName
        message.senderId = FirebaseAuth.getInstance().currentUser!!.uid
        message.messageText = messageText

        val messageItem = hashMapOf("senderId" to message.senderId, "senderName" to message.senderName, "text" to messageText)

        val messageRef = FirebaseDatabase.getInstance().reference.child("channels").child(channelSchool).child(channelId).child("messages")
        messageRef.push().setValue(messageItem)
    }
}
