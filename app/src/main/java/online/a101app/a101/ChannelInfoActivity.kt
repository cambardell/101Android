package online.a101app.a101

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView

class ChannelInfoActivity: AppCompatActivity() {
    lateinit var channelId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.channel_info)

        val intent = intent
        channelId = intent.getStringExtra("channel")
        val view: TextView = findViewById(R.id.channelInfo)
        view.text = channelId
    }
}