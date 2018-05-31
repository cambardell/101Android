package online.a101app.a101

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddChannelActivity : AppCompatActivity() {

    lateinit var database: DatabaseReference

    private var channelList: MutableList<Channel>? = null
    lateinit var adapter: ChannelAdapter
    private var listViewItems: ListView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_channel)

        listViewItems = findViewById<View>(R.id.items_list) as ListView
        channelList = mutableListOf<Channel>()
        adapter = ChannelAdapter(this, channelList!!)
        listViewItems!!.setAdapter(adapter)

        database = FirebaseDatabase.getInstance().reference
        database.addListenerForSingleValueEvent(itemListener)

    }

    var itemListener: ValueEventListener = object : ValueEventListener {
        override fun onDataChange(dataSnapshot: DataSnapshot) {
            // Get Post object and use the values to update the UI
            addDataToList(dataSnapshot)
        }
        override fun onCancelled(databaseError: DatabaseError) {
            // Getting Item failed, log a message
            Log.w("MainActivity", "loadItem:onCancelled", databaseError.toException())
        }
    }

    // Build the channel list
    private fun addDataToList(dataSnapshot: DataSnapshot) {
        val items = dataSnapshot.children.iterator()

        //Check if current database contains any collection
        if (items.hasNext()) {
            val channelListIndex = items.next()
            val itemsIterator = channelListIndex.children.iterator()

            //check if the collection has any to do items or not
            while (itemsIterator.hasNext()) {
                //get current item
                val currentItem = itemsIterator.next()

                val channelItem = Channel.create()
                //get current data in a map
                val map = currentItem.getValue() as HashMap<String, Any>
                //key will return Firebase ID
                channelItem.channelMembers = map.get("members") as HashMap<Any, Any>
                channelItem.channelName = map.get("name") as String?
                channelItem.channelSchool = map.get("school") as String?
                val userId = FirebaseAuth.getInstance().currentUser!!.uid.toString()
                val members = channelItem.channelMembers as HashMap<Any, Any>
                if (members != null) {
                    Log.d("members", members.toString())
                    when {
                        userId !in members.values -> channelList!!.add(channelItem);
                    }
                }



            }
        }

        //alert adapter that has changed
        adapter.notifyDataSetChanged()
    }


}
