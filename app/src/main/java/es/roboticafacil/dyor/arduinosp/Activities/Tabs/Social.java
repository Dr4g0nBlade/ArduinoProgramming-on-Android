package es.roboticafacil.dyor.arduinosp.Activities.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.roboticafacil.dyor.arduinosp.Activities.ChatActivity;
import es.roboticafacil.dyor.arduinosp.Activities.SearchChannel;
import es.roboticafacil.dyor.arduinosp.Adaptors.ChannelAdaptor;
import es.roboticafacil.dyor.arduinosp.Models.Channel;
import es.roboticafacil.dyor.arduinosp.Models.ChatMessage;
import es.roboticafacil.dyor.arduinosp.R;
import es.roboticafacil.dyor.arduinosp.Utils.FirebaseProfile;

/**
 * Created by Dragos Dunareanu on 06-Apr-17.
 */

public class Social extends android.support.v4.app.Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseProfile fbf;

    final List<Channel> channels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        mAuth = FirebaseAuth.getInstance();

        Log.e("WOOOOW", "listening for user");
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {

                    Log.e("WOOOOW", "no user");
                } else {

                    Log.e("WOOOOW", "has user");
                    fbf = new FirebaseProfile();
                    fbf.setLoggedIn(true);

                    Log.e("WOOOOW", "calling setup");
                    SetupChannels();

                    JoinChannels();

                }
            }
        };

        mAuth.addAuthStateListener(mAuthListener);

        fbf = new FirebaseProfile();
        List<String> joinedChann;

        View v = inflater.inflate(R.layout.social, container, false);

        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Social.this.getContext(), SearchChannel.class);
                startActivity(intent);
            }
        });

        ListView channelList = (ListView) v.findViewById(R.id.lv_channels);
        TextView tvNoChannels = (TextView) v.findViewById(R.id.tv_no_channels);

        ChannelAdaptor adaptor = new ChannelAdaptor(this.getContext(), channels);

        channelList.setAdapter(adaptor);

        channelList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(Social.this.getContext(), ChatActivity.class);

                intent.putExtra("CHANNEL_ID", channels.get(position).getUid());
                startActivity(intent);

            }
        });

        return v;
    }

    private void JoinChannels() {
        fbf = new FirebaseProfile();
        DatabaseReference c = fbf.getChannels();
        if (fbf.getLoggedIn()) {
            List<String> channelsList = fbf.getCurrentUser().getJoinedChannels();
            for (int i = 0; i < channelsList.size(); i++) {
                c.child(channelsList.get(i)).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        channels.add(dataSnapshot.getValue(Channel.class));
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        }


    }

    private void SetupChannels() {
        Map<String, String> members = new HashMap<>();
        members.put(fbf.getCurrentUserUid(), "rwe");

        List<ChatMessage> mess = new ArrayList<>();
        channels.add(new Channel("0", "Test Channel 1", members, mess));
        channels.add(new Channel("1", "Test Channel 2", members, mess));

        Log.e("WOOOOW", "updating channels");
        fbf.getChannels().setValue(channels);
    }
}
