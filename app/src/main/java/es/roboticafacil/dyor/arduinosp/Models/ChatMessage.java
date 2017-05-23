package es.roboticafacil.dyor.arduinosp.Models;

import android.util.Log;

/**
 * Created by Dragos Dunareanu on 06-Apr-17.
 */

public class ChatMessage {

    private FirebaseProfile fbP;
    private String id;
    private String messageText;

    private String messageUserUid;
    private User messageUser;
    private long messageTime;

    public ChatMessage(String messageText, String messageUserUid, long messageTime) {
        this.messageText = messageText;
        this.messageUserUid = messageUserUid;
        this.messageTime = messageTime;
        this.fbP = new FirebaseProfile();
    }

    public ChatMessage() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public String getMessageUserUid() {
        return messageUserUid;
    }

    public User getMessageUser() {
        this.fbP = new FirebaseProfile();
        Log.e("wooow", "getting user;" + getMessageUserUid());
        return fbP.getUser(getMessageUserUid());
    }

    public long getMessageTime() {
        return messageTime;
    }
}

