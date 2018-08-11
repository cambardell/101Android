package online.a101app.a101

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import android.R.attr.data
import android.media.browse.MediaBrowser
import android.renderscript.Sampler
import android.widget.ImageButton
import com.google.firebase.storage.StorageMetadata
import java.io.File
import java.time.LocalDateTime
import kotlin.collections.HashMap


class ChatActivity2 : AppCompatActivity() {
    private var mMessageRecycler: RecyclerView? = null
    private lateinit var mMessageAdapter: ChatAdapter
    private lateinit var databaseReference: DatabaseReference
    private lateinit var channelId: String
    private lateinit var channelSchool: String
    private lateinit var sendButton: Button
    private lateinit var textBox: EditText
    private lateinit var photoButton: ImageButton

    private val gallery = 1
    private val camera = 2

    private val imageURLNotSetKey = "NOTSET"
    var storage = FirebaseStorage.getInstance("gs://app-31003.appspot.com")
    private var photoMessageMap = HashMap<String, Any>()

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
        photoButton = findViewById(R.id.photo_button)

        sendButton.setOnClickListener {
            sendMessage()
        }

        photoButton.setOnClickListener {
            attachPhoto()
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

                    // If the message has text, it is a text message. Otherwise it is a photo
                    val possibleMessageText  = item.child("text").value?.toString()?.let {
                        message.senderName = item.child("senderName").value?.toString()
                        message.senderId = item.child("senderId").value!!.toString()
                        message.messageText = item.child("text").value?.toString()
                    } ?: run {
                        message.senderId = item.child("senderId").value!!.toString()
                        message.photoUrl = item.child("photoURL").value?.toString()
                    }


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

    // Present a dialog when the attachment button is tapped
    private fun attachPhoto() {
        val pictureDialog = AlertDialog.Builder(this)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    // Choose a photo from the gallery, send it to onActivityResult
    private fun choosePhotoFromGallary() {
        val galleryIntent = Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

        startActivityForResult(galleryIntent, gallery)
    }

    // Take a photo, send it to onActivityResult
    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, camera)
    }

    public override fun onActivityResult(requestCode:Int, resultCode:Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (requestCode == gallery)
        {
            if (data != null)
            {
                val selectedImage = data.data
                val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                val key = sendPhotoMessage()
                val imagePath = FirebaseAuth.getInstance().currentUser!!.uid + LocalDateTime.now()
                val metadata = StorageMetadata()
                storage.reference.child(imagePath).putFile(selectedImage)
                setImageURL(storage.getReference().child(metadata.path).toString(), key)

            }

        }
        else if (requestCode == camera)
        {

        }
    }

    private fun sendPhotoMessage(): String {
        val messageRef = FirebaseDatabase.getInstance().reference.child("channels").child(channelSchool).child(channelId).child("messages")
        val messageItem = hashMapOf<String, Any>("photoURL" to imageURLNotSetKey, "senderId" to FirebaseAuth.getInstance().currentUser!!.uid)
        val itemRef = messageRef.push()
        itemRef.setValue(messageItem)
        return itemRef.key!!

    }

    private fun setImageURL(url: String, key: String) {
        val messageRef = FirebaseDatabase.getInstance().reference.child("channels").child(channelSchool).child(channelId).child("messages")
        val itemRef = messageRef.child(key)
        itemRef.updateChildren(mutableMapOf<String, Any>("photoURL" to (key as Any)))
    }






}
