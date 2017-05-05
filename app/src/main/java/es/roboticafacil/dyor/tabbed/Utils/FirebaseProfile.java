package es.roboticafacil.dyor.tabbed.Utils;

import android.net.Uri;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.roboticafacil.dyor.tabbed.Models.User;

/**
 * Created by Dragos Dunareanu on 24-Apr-17.
 */

public class FirebaseProfile {
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference users = db.getReference("/users/");
    private DatabaseReference channels = db.getReference("/channels/");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentFirebaseUser = firebaseAuth.getCurrentUser();
    private User currentUser = new User(currentFirebaseUser.getUid(), currentFirebaseUser.getEmail(), currentFirebaseUser.getDisplayName(), GetJoinedChannels());

    private List<String> GetJoinedChannels() {
        final List<String> channels = new ArrayList<>();

        users.child("channels").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
                    String post = postSnapshot.getValue(String.class);

                    if (!channels.contains(post)) channels.add(post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return channels;
    }

    public DatabaseReference getUsers(){
        return users;
    }

    public DatabaseReference getChannels(){
        return channels;
    }

    public void createUser(FirebaseUser user){
        String uid = user.getUid();
        String email = user.getEmail();
        assert email != null;
        int i = email.indexOf("@");
        String username = email.substring(0, i);

        List<String> noChannels = new ArrayList<>();
        User newUser = new User(uid, email, username, noChannels);

        users.child(uid).setValue(newUser);
    }

    public FirebaseUser getCurrentFirebaseUser(){
        return currentFirebaseUser;
    }

    public User getCurrentUser(){
        return currentUser;
    }

    public String getCurrentUserUid(){
        return currentFirebaseUser.getUid();
    }

    public String getCurrentUserName(){
        return currentFirebaseUser.getDisplayName();
    }

    public String getCurrentUserEmail(){
        return currentFirebaseUser.getEmail();
    }

    public Uri getCurrentPhotoUrl(){
        return currentFirebaseUser.getPhotoUrl();
    }

    public User getUser(String uid) {
        return null;
    }

    public String getUserName(User user){
        return user.getUsername();
    }

    public void setCurrentUserEmail(String email){
        currentFirebaseUser.updateEmail(email);
    }

    public void setCurrentUserPassword(String password){
        currentFirebaseUser.updatePassword(password);
    }

}
