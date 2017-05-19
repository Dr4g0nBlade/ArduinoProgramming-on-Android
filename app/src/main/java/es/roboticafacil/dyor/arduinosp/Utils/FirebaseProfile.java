package es.roboticafacil.dyor.arduinosp.Utils;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import es.roboticafacil.dyor.arduinosp.Models.User;

/**
 * Created by Dragos Dunareanu on 24-Apr-17.
 */

public class FirebaseProfile {

    private Boolean loggedIn = false;
    private FirebaseDatabase db = FirebaseDatabase.getInstance();
    private DatabaseReference users = db.getReference("/users/");
    private DatabaseReference channels = db.getReference("/channels/");
    private DatabaseReference messages = db.getReference("/messages/");
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser currentFirebaseUser;
    private User currentUser;
    public FirebaseProfile() {

        currentFirebaseUser = firebaseAuth.getCurrentUser();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (currentFirebaseUser == null) {
                    setLoggedIn(false);
                } else {
                    setLoggedIn(true);
                    Log.e("wtf", "is being set up");
                    currentUser = new User(currentFirebaseUser.getUid(), currentFirebaseUser.getEmail(), currentFirebaseUser.getDisplayName(), currentFirebaseUser.getPhotoUrl().toString(), new ArrayList<String>());

                    Log.e("wtf", "is set up");
                }
            }
        };

        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    public Boolean getLoggedIn() {
        return loggedIn;
    }

    //final Map<String, User> usersMap = new HashMap<>();

    public void setLoggedIn(Boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

    public DatabaseReference getUsers() {
        return users;
    }

    public DatabaseReference getChannels() {
        return channels;
    }

    public DatabaseReference getMessages() {
        return messages;
    }

    public void createUser(FirebaseUser user) {
        String uid = user.getUid();
        String email = user.getEmail();
        assert email != null;
        int i = email.indexOf("@");
        String username = email.substring(0, i);
        String photoUri = user.getPhotoUrl().toString();
        Log.e("CreatingUser", "Setting no channels");
        List<String> noChannels = new ArrayList<>();
        User newUser = new User(uid, email, username, photoUri, noChannels);

        Log.e("CreatingUser", "no channel set, uploading");
        users.child(uid).setValue(newUser);

        Log.e("CreatingUser", "done uploadinng");
    }

    public FirebaseUser getCurrentFirebaseUser() {
        return currentFirebaseUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getCurrentUserUid() {
        return currentFirebaseUser.getUid();
    }

    public String getCurrentUserName() {
        return currentFirebaseUser.getDisplayName();
    }

    public String getCurrentUserEmail() {
        return currentFirebaseUser.getEmail();
    }

    public void setCurrentUserEmail(String email) {
        currentFirebaseUser.updateEmail(email);
    }

    public Uri getCurrentPhotoUrl() {
        return currentFirebaseUser.getPhotoUrl();
    }

    public User getUser(String uid) {
        final User[] u = {new User()};
        getUsers().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                u[0] = dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return u[0];
    }

    public void setCurrentUserPassword(String password) {
        currentFirebaseUser.updatePassword(password);
    }
}


//    private List<String> GetJoinedChannels() {
//        final List<String> channels = new ArrayList<>();
//        users.child("channels").addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()){
//                    String post = postSnapshot.getValue(String.class);
//
//                    if (!channels.contains(post)) channels.add(post);
//                }
//                if (channels.isEmpty()) channels.add("Welcome");
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
//        return channels;
//    }

//    public Map<String, User> getAllUsers(){
//        startingReading();
//        getUsers().addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()){
//                    User post = dataSnapshot.getValue(User.class);
//                    String postUid = post.getUid();
//                    usersMap.put(postUid, post);
//                    if (usersMap.size() == dataSnapshot.getChildrenCount()) endReading();
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//        return usersMap;
//    }