package es.roboticafacil.dyor.tabbed.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import es.roboticafacil.dyor.tabbed.Models.ChatMessage;
import es.roboticafacil.dyor.tabbed.Adaptors.MessageAdaptor;
import es.roboticafacil.dyor.tabbed.Models.User;
import es.roboticafacil.dyor.tabbed.R;
import es.roboticafacil.dyor.tabbed.Utils.FirebaseProfile;

public class ChatActivity extends AppCompatActivity {

    FirebaseProfile fbp = new FirebaseProfile();
    List<User> users = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        final String channelID = intent.getStringExtra("CHANNEL_ID");

        final DatabaseReference dbChannel = FirebaseDatabase.getInstance().getReference("/channels/" + channelID);

        final List<ChatMessage> messages;
        messages = new ArrayList<>();


        final ListView listView = (ListView) findViewById(R.id.lv_mess);
        final MessageAdaptor mess = new MessageAdaptor(this, messages);

        listView.setAdapter(mess);

        dbChannel.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap: dataSnapshot.getChildren()){
                    ChatMessage  post = postSnap.getValue(ChatMessage.class);

                    if(!messages.contains(post)) messages.add(post);
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

        fbp.getUsers().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnap: dataSnapshot.getChildren()){
                    User post = postSnap.getValue(User.class);

                    if(!users.contains(post)) users.add(post);
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

                dbChannel.push()
                        .setValue(new ChatMessage(etMassage.getText().toString(), FirebaseAuth.getInstance().getCurrentUser().getUid(), new Date().getTime()));
                etMassage.setText("");

            }
        });


    }

}
