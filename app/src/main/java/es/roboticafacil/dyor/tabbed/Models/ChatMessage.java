package es.roboticafacil.dyor.tabbed.Models;

import es.roboticafacil.dyor.tabbed.Utils.FirebaseProfile;

/**
 * Created by Dragos Dunareanu on 06-Apr-17.
 */

public class ChatMessage {

    private FirebaseProfile fbP = new FirebaseProfile();
    private String id;
    private String messageText;
    private String messageUserUid;
    private User messageUser;
    private long messageTime;

    public ChatMessage(String messageText, String messageUserUid, long messageTime) {
        this.messageText = messageText;
        this.messageUserUid = messageUserUid;
        this.messageUser =fbP.getUser(messageUserUid);
        this.messageTime = messageTime;
    }

    public ChatMessage(){
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


    public User getMessageUser() {
        return messageUser;
    }

    public String getMessageUserName(){
        return fbP.getUserName(messageUser);
    }

    public void setMessageUser(User messageUser) {
        this.messageUser = messageUser;
    }

    public long getMessageTime() {
        return messageTime;
    }

    public void setMessageTime(long messageTime) {
        this.messageTime = messageTime;
    }
}

