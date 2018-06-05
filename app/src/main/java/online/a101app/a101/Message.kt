package online.a101app.a101

class Message {
    companion object Factory {
        fun create(): Message = Message()
    }

    var messageText: String? = null
    var senderId: String? = null
    var senderName: String? = null
}