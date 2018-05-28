package online.a101app.a101

class Channel {
    companion object Factory {
        fun create(): Channel = Channel()
    }

    var channelName: String? = null
    var channelSchool: String? = null
}