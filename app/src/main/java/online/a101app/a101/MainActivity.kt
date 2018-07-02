package online.a101app.a101

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ListView
import com.firebase.ui.auth.AuthUI
import com.google.firebase.database.*
import java.util.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import android.R.attr.data
import com.firebase.ui.auth.IdpResponse
import android.R.attr.data
import android.app.Activity
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.Toolbar
import kotlin.collections.HashMap


class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 123

    lateinit var database: DatabaseReference

    private var channelList: MutableList<Channel>? = null
    lateinit var adapter: ChannelAdapter
    private var listViewItems: ListView? = null

    var userSchool = ""

    private var providers: List<AuthUI.IdpConfig> = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build()
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val auth = FirebaseAuth.getInstance()

        if (auth.currentUser != null) {

            Log.d("Login", "Success")
            listViewItems = findViewById<View>(R.id.items_list) as ListView
            channelList = mutableListOf<Channel>()
            adapter = ChannelAdapter(this, channelList!!)
            listViewItems!!.setAdapter(adapter)

            database = FirebaseDatabase.getInstance().reference
            database.addListenerForSingleValueEvent(itemListener)

        } else {
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(providers)
                            .build(), RC_SIGN_IN)
        }


        // When a row is clicked, move to the chat view
        listViewItems?.setOnItemClickListener { _, _, position, _ ->
            val selectedChannel = channelList!![position]
            var i = Intent(this, ChatActivity2::class.java)
            i.putExtra("channel", selectedChannel.channelId as String)
            i.putExtra("school", userSchool)
            startActivity(i)
        }


    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.add_channel -> {
            val intent = Intent(this, AddChannelActivity::class.java)
            intent.putExtra("school", userSchool)
            Log.d("school", userSchool)

            startActivity(intent)
            true
        }

        R.id.account -> {
            val intent = Intent(this, AccountActivity::class.java).apply {

            }
            startActivity(intent)
            true
        }

        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
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

        val users = dataSnapshot.child("users").children.iterator()

        while (users.hasNext()) {
            val currentUser = users.next()
            if (currentUser.key == FirebaseAuth.getInstance().currentUser!!.uid) {

                val userSchoolMap = currentUser.getValue() as HashMap<String, String>
                userSchool = userSchoolMap.get("school")!!
                Log.d("school", userSchool)
            }
        }

        if (userSchool == "") {
            // Move to the choose school activity
            Log.d("user", "move to choose school activity")
            val intent = Intent(this, ChooseSchoolActivity::class.java).apply {

            }
            startActivity(intent)
        } else {
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
                    channelItem.channelMembers = map.get("members") as HashMap<Any, Any>?
                    channelItem.channelName = map.get("name") as String?
                    channelItem.channelSchool = map.get("school") as String?
                    channelItem.channelId = currentItem.key
                    val userId = FirebaseAuth.getInstance().currentUser!!.uid
                    val members = channelItem.channelMembers as HashMap<Any, Any>?
                    if (members != null) {

                        when {
                            userId in members.values -> channelList!!.add(channelItem);

                        }
                    }
                }
            }
        }

        //alert adapter that has changed
        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode == Activity.RESULT_OK) {
                // Successfully signed in
                Log.d("Login", "Success")
                listViewItems = findViewById<View>(R.id.items_list) as ListView
                channelList = mutableListOf<Channel>()
                adapter = ChannelAdapter(this, channelList!!)
                listViewItems!!.setAdapter(adapter)

                database = FirebaseDatabase.getInstance().reference
                database.addListenerForSingleValueEvent(itemListener)

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                Log.d("Login", "Fail")
            }
        }
    }
}
