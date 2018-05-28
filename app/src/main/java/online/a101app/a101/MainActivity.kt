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


class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 123

    lateinit var database: DatabaseReference

    var channelList: MutableList<Channel>? = null
    lateinit var adapter: ChannelAdapter
    private var listViewItems: ListView? = null

    private var providers: List<AuthUI.IdpConfig> = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build()
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
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
                channelItem.channelName = map.get("name") as String?
                channelItem.channelSchool = map.get("school") as String?
                channelList!!.add(channelItem);
            }
        }
        //alert adapter that has changed
        adapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode === RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)

            if (resultCode === Activity.RESULT_OK) {
                // Successfully signed in
                val user = FirebaseAuth.getInstance().currentUser
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
