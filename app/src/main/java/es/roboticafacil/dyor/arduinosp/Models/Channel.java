package es.roboticafacil.dyor.arduinosp.Models;

import android.graphics.Bitmap;

import java.util.List;
import java.util.Map;

/**
 * Created by Dragos Dunareanu on 06-Apr-17.
 */

public class Channel {

    private static FirebaseProfile fb = new FirebaseProfile();

    private String uid;
    private String name;
    private Bitmap avatar;
    private Map<String, String> members;
    private List<ChatMessage> messages;
    private String lastMessage;

    public Channel() {
    }

    public Channel(String uid, String name, Map<String, String> members, List<ChatMessage> messages) {
        this.uid = uid;
        this.name = name;
        this.members = members;
        this.messages = messages;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getMembers() {
        return members;
    }

    public void setMembers(Map<String, String> members) {
        this.members = members;

        fb.getChannels().child(getUid()).setValue(members);

    }

    public void addMember(String uid, String perm) {
        this.members.put(uid, perm);

        fb.getChannels().child(getUid()).child(uid).setValue(perm);

    }

    public void removeMember(String uid) {
        this.members.remove(uid);
    }

    public void editPerms(String uid, String perms) {
        this.members.put(uid, perms);
    }


    public List<ChatMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }

    public String getLastMessage() {
        if (messages.isEmpty() || messages.size() == 0) {
            return "";
        }
        return this.messages.get(messages.size() - 1).getMessageText();
    }


}
