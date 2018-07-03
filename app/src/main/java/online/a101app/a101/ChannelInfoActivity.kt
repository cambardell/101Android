package online.a101app.a101

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ChannelInfoActivity: AppCompatActivity() {
    lateinit var channelId: String
    lateinit var channelSchool: String
    lateinit var leaveChannel: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_info)

        val intent = intent
        channelId = intent.getStringExtra("channel")
        channelSchool = intent.getStringExtra("school")
        val view: TextView = findViewById(R.id.channelInfo)
        view.text = channelId

        leaveChannel = findViewById(R.id.leave_class)

        val itemListener: ValueEventListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get Post object and use the values to update the UI
                removeMember(dataSnapshot)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Item failed, log a message

            }
        }

        leaveChannel.setOnClickListener {
            val channelRef = FirebaseDatabase.getInstance().reference.child("channels").child(channelSchool).child(channelId)
            val channelMembers = channelRef.child("members")
            channelMembers.addListenerForSingleValueEvent(itemListener)
        }
    }

    private fun removeMember(dataSnapshot: DataSnapshot) {
        val members = dataSnapshot.children.iterator()
        var newMembers: HashMap<String, String> = hashMapOf()
        while (members.hasNext()) {
            val member = members.next()
            if (member.value == FirebaseAuth.getInstance().currentUser!!.uid) {

            } else {
                newMembers.set(member.key.toString(), member.value.toString())
            }
        }

        FirebaseDatabase.getInstance().reference.child("channels").child(channelSchool).child(channelId).child("members").setValue(newMembers)
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
    }
}