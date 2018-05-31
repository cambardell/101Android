package online.a101app.a101

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import kotlinx.android.synthetic.main.chat.*

class ChatActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_NAME = "Name"

        fun newIntent(context: Context, channel: Channel): Intent {
            val detailIntent = Intent(context, ChatActivity::class.java)

            detailIntent.putExtra(EXTRA_NAME, channel.channelName)

            return detailIntent
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chat)
        val nameLabel: TextView = findViewById<TextView>(R.id.channel_name) as TextView
        nameLabel.text = intent.extras.getString(EXTRA_NAME)
    }

}
