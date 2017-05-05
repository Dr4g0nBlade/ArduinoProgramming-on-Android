package es.roboticafacil.dyor.tabbed.Models;

import java.util.List;
import java.util.Map;

/**
 * Created by Dragos Dunareanu on 04-May-17.
 */

public class User {
    private String uid;
    private String email;
    private String username;
    private List<String> channels;

    public User(String uid, String email, String username, List<String> channels) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.channels = channels;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<String> getChannels(){
        return channels;
    }

}
