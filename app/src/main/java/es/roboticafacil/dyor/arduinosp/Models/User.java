package es.roboticafacil.dyor.arduinosp.Models;

import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dragos Dunareanu on 04-May-17.
 */

public class User {
    private String uid;
    private String email;
    private String username;
    private String photoUri;
    private List<String> channels = new ArrayList<>();

    public User(String uid, String email, String username, String photoUri, List<String> channels) {
        this.uid = uid;
        this.email = email;
        this.username = username;
        this.photoUri = photoUri;
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

    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(String photoUri) {
        this.photoUri = photoUri;
    }

    public List<String> getJoinedChannels() {
        return channels;
    }

}
