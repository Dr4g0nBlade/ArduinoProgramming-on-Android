package es.roboticafacil.dyor.tabbed.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import es.roboticafacil.dyor.tabbed.Utils.MessageHolder;
import es.roboticafacil.dyor.tabbed.Models.ChatMessage;
import es.roboticafacil.dyor.tabbed.R;
import es.roboticafacil.dyor.tabbed.Utils.FirebaseProfile;

/**
 * Created by Dragos Dunareanu on 06-Apr-17.
 */

public class MessageAdaptor extends BaseAdapter {

    private static final int TYPE_COUNT = 2;
    private static final int TYPE_OUTCOME = 0;
    private static final int TYPE_INCOME = 1;

    FirebaseProfile fb = new FirebaseProfile();

    private Context mContext;
    private List<ChatMessage> messages;
    private ChatMessage message;
    private long date;
    private String formatedDate;
    private SimpleDateFormat sdf = new SimpleDateFormat("MMM MM dd, yyyy h:mm a", Locale.ENGLISH);

    public MessageAdaptor(Context mContext, List<ChatMessage> messages) {
        this.mContext = mContext;
        this.messages = messages;
    }

    @Override
    public int getCount() {
        return this.messages.size();
    }

    @Override
    public Object getItem(int position) {
        return this.messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        String userUid = messages.get(position).getMessageUser().getUid();
        if (userUid.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            return TYPE_OUTCOME;
        } else return TYPE_INCOME;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MessageHolder.SentMessage sentMessageHolder = null;
        MessageHolder.RecievedMessage recievedMessageHolder = null;

        ChatMessage entry =  messages.get(position);

        View v = convertView;
        LayoutInflater inflater;
        inflater = LayoutInflater.from(mContext);

        int type = getItemViewType(position);

        if (type == TYPE_INCOME){
            if (v == null){
                v = inflater.inflate(R.layout.message, parent, false);
                recievedMessageHolder = new MessageHolder().new RecievedMessage();
                recievedMessageHolder.userName = (TextView) v.findViewById(R.id.tv_mess_user);
                recievedMessageHolder.textMessage = (TextView) v.findViewById(R.id.tv_mess_content);
                recievedMessageHolder.dateText = (TextView) v.findViewById(R.id.tv_mess_date);

                v.setTag(recievedMessageHolder);

                sentMessageHolder = null;
            } else {
                recievedMessageHolder = (MessageHolder.RecievedMessage) convertView.getTag();
                sentMessageHolder = null;
            }

            recievedMessageHolder.userName.setText(entry.getMessageUserName());
            recievedMessageHolder.textMessage.setText(entry.getMessageText());
            recievedMessageHolder.dateText.setText(sdf.format(entry.getMessageTime()));
        } else if(type == TYPE_OUTCOME){
            if (v == null){
                v = inflater.inflate(R.layout.chat_send_message, parent, false);
                sentMessageHolder = new MessageHolder().new SentMessage();
                sentMessageHolder.textMessage = (TextView) v.findViewById(R.id.tv_mess_content);
                sentMessageHolder.dateText = (TextView) v.findViewById(R.id.tv_mess_date);

                v.setTag(sentMessageHolder);
            } else {
                sentMessageHolder = (MessageHolder.SentMessage) v.getTag();
                recievedMessageHolder = null;
            }
            sentMessageHolder.textMessage.setText(entry.getMessageText());
            sentMessageHolder.dateText.setText(sdf.format(entry.getMessageTime()));
        }

        return v;
    }


}
