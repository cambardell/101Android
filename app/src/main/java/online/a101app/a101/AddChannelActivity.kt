package online.a101app.a101

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class AddChannelActivity : AppCompatActivity() {

    lateinit var database: DatabaseReference

    var channelList: MutableList<Channel>? = null
    lateinit var userSchool: String
    lateinit var adapter: ChannelAdapter
    lateinit var filterAdapter: ChannelAdapter

    private var listViewItems: ListView? = null
    private var checkmark: ImageView? = null
    lateinit var filterText: EditText
    private var filteredChannelList: MutableList<Channel>? = null
    // Store a permanent copy of the original classes.
    private var permanentList: MutableList<Channel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_channel)

        filterText = findViewById(R.id.searchChannel)
        val intent = intent
        userSchool = intent.getStringExtra("school")

        listViewItems = findViewById<View>(R.id.items_list) as ListView
        channelList = mutableListOf<Channel>()
        adapter = ChannelAdapter(this, channelList!!)
        filteredChannelList = mutableListOf<Channel>()
        filterAdapter = ChannelAdapter(this, filteredChannelList!!)
        listViewItems!!.setAdapter(adapter)

        database = FirebaseDatabase.getInstance().reference
        database.addListenerForSingleValueEvent(itemListener)

        checkmark = findViewById(R.id.checkmark)


        // When a row is clicked, add the user id to members.
        listViewItems!!.setOnItemClickListener { _, _, position, _ ->
            val selectedChannel = channelList!![position]
            addToMembers(selectedChannel)
            channelList!!.removeAt(position)
            adapter.notifyDataSetChanged()
        }

        filterText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // required

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // required
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // TODO: Fix backspace
                var tempList = channelList!!.filter { it.channelName!!.contains(s!!, ignoreCase = true) } as MutableList<Channel>
                channelList!!.clear()
                for (i in tempList) {
                    channelList!!.add(i)
                }
                adapter.notifyDataSetChanged()

            }
        })
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
        Log.d("Updating data", "data")

        //Check if current database contains any collection
        if (items.hasNext()) {
            val channelListIndex = items.next()
            val itemsIterator = channelListIndex.child(userSchool).children.iterator()

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
                channelItem.channelId = currentItem.key
                val userId = FirebaseAuth.getInstance().currentUser!!.uid.toString()
                val members = channelItem.channelMembers as HashMap<Any, Any>
                if (members != null) {

                    when {
                        userId !in members.values -> channelList!!.add(channelItem)
                    }
                }
            }
        }

        Log.d("permanent", permanentList.toString())
        //alert adapter that has changed
        adapter.notifyDataSetChanged()
    }

    fun addToMembers(channel: Channel) {
        val userId = FirebaseAuth.getInstance().currentUser!!.uid
        val userDisplayName = FirebaseAuth.getInstance().currentUser!!.displayName as String
        val database = FirebaseDatabase.getInstance().getReference("channels").child(userSchool)

        var members = database.child(channel.channelId.toString()).child("members")
        members.push().setValue(userId)
        permanentList!!.remove(channel)


        var names = database.child(channel.channelId.toString()).child("names")
        names.push().setValue(userDisplayName)



    }

    fun isFiltering(): Boolean {
        return filterText.isActivated && !searchBarIsEmpty()
    }

    fun searchBarIsEmpty(): Boolean {
       return filterText.text.isEmpty()
    }



}
