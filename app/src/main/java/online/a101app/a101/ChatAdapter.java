package online.a101app.a101;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter{
    private Context mContext;
    private List<Message> mMessageList;

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_PHOTOMESSAGE_SENT = 3;
    private static final int VIEW_TYPE_PHOTOMESSAGE_RECEIVED = 4;

    public ChatAdapter(Context context, List<Message> messageList) {
        mContext = context;
        mMessageList = messageList;
    }

    @Override
    public int getItemCount() {

        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Message message = (Message) mMessageList.get(position);


        if (message.getSenderId().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            // If the current user is the sender of the message
            if (message.getPhotoUrl() == null) {
                return VIEW_TYPE_MESSAGE_SENT;
            }
            else {
                return VIEW_TYPE_PHOTOMESSAGE_SENT;
            }

        } else {
            // If some other user sent the message
            if (message.getPhotoUrl() == null) {
                return VIEW_TYPE_MESSAGE_RECEIVED;
            } else {
                return VIEW_TYPE_PHOTOMESSAGE_RECEIVED;
            }
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.message_received, parent, false);
            return new ReceivedMessageHolder(view);
        } else if (viewType == VIEW_TYPE_PHOTOMESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_received, parent, false);
            return new ReceivedMessageHolder(view);

        } else if (viewType == VIEW_TYPE_PHOTOMESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.photo_sent, parent, false);
            return new SentMessageHolder(view);
        }

        return null;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
            //case VIEW_TYPE_PHOTOMESSAGE_RECEIVED:
            //    ((ReceivedMessageHolder) holder).bind(message);
            //case VIEW_TYPE_PHOTOMESSAGE_SENT:
            //    ((SentMessageHolder) holder).bind(message);
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = itemView.findViewById(R.id.text_message_body);

        }

        void bind(Message message) {
            messageText.setText(message.getMessageText());
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView messageText, nameText;

        ReceivedMessageHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            nameText = itemView.findViewById(R.id.text_message_name);

        }

        void bind(Message message) {
            messageText.setText(message.getMessageText());
            nameText.setText(message.getSenderName());
        }
    }


}
