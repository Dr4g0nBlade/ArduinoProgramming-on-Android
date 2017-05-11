package es.roboticafacil.dyor.arduinosp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.roboticafacil.dyor.arduinosp.Adaptors.MessageAdaptor;
import es.roboticafacil.dyor.arduinosp.Models.ChatMessage;
import es.roboticafacil.dyor.arduinosp.Models.User;
import es.roboticafacil.dyor.arduinosp.R;
import es.roboticafacil.dyor.arduinosp.Utils.FirebaseProfile;

public class ChatActivity extends AppCompatActivity {

    FirebaseProfile fb = new FirebaseProfile();
    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        final String channelID = intent.getStringExtra("CHANNEL_ID");

        final DatabaseReference dbChannel = FirebaseDatabase.getInstance().getReference("/channels/" + channelID);

        final List<String> listOfUsersUid = new ArrayList<>();
        final List<User> listOfUsers = new ArrayList<>();

        //usersInChannel.add(fb.getCurrentUser().getUsername());
        //fb.getJoinedChannels().child(channelID).child("members").setValue(usersInChannel);
        Log.e("Testing", "channel-id:" + channelID);
        fb.getChannels().child(channelID).child("members").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    String postUid = postSnapshot.getKey();
                    listOfUsers.add(fb.getUser(postUid));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        final List<ChatMessage> messages = new ArrayList<>();

        final ListView listView = (ListView) findViewById(R.id.lv_mess);
        final Map<ChatMessage, User> messagesMap = new HashMap<>();
        final MessageAdaptor mess = new MessageAdaptor(this, messages, listOfUsers);


        listView.setAdapter(mess);

        dbChannel.child("messages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap : dataSnapshot.getChildren()) {
                    ChatMessage post = postSnap.getValue(ChatMessage.class);

                    messagesMap.put(post, post.getMessageUser());

                    if (!messages.contains(post)) messages.add(post);
                    mess.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        //dbChannel.addChildEventListener(new ChildEventListener() {
        //    @Override
        //    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        //        ChatMessage post = dataSnapshot.getValue(ChatMessage.class);
        //        messages.add(post);
        //        mess.notifyDataSetChanged();
        //        mess.registerDataSetObserver(new DataSetObserver() {
        //            @Override
        //            public void onChanged() {
        //                super.onChanged();
        //            }
        //        });
        //    }
//
        //    @Override
        //    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//
        //    }
//
        //    @Override
        //    public void onChildRemoved(DataSnapshot dataSnapshot) {
//
        //    }
//
        //    @Override
        //    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
//
        //    }
//
        //    @Override
        //    public void onCancelled(DatabaseError databaseError) {
//
        //    }
        //});

        fb.getUsers().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap : dataSnapshot.getChildren()) {
                    User post = postSnap.getValue(User.class);

                    if (!users.contains(post)) users.add(post);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        ImageButton sendButton = (ImageButton) findViewById(R.id.ib_send_mess);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText etMassage = (EditText) findViewById(R.id.et_mess);

                fb.getMessages().child(channelID).push()
                        .setValue(new ChatMessage(etMassage.getText().toString(), fb.getCurrentUser().getUid(), new Date().getTime()));

                etMassage.setText("");

            }
        });


    }

}
