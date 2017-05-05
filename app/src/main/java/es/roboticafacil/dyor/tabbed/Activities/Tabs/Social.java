package es.roboticafacil.dyor.tabbed.Activities.Tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.roboticafacil.dyor.tabbed.Activities.ChatActivity;
import es.roboticafacil.dyor.tabbed.Adaptors.ChannelAdaptor;
import es.roboticafacil.dyor.tabbed.Models.Channel;
import es.roboticafacil.dyor.tabbed.Models.ChatMessage;
import es.roboticafacil.dyor.tabbed.R;
import es.roboticafacil.dyor.tabbed.Utils.FirebaseProfile;

/**
 * Created by Dragos Dunareanu on 06-Apr-17.
 */

public class Social extends android.support.v4.app.Fragment {

    FirebaseProfile fbf = new FirebaseProfile();

    final List<Channel> channels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.social, container, false);

        ListView channelList = (ListView) v.findViewById(R.id.lv_channels);

        SetupChannels();

        JoinChannels();




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

        DatabaseReference c = fbf.getChannels();
        List<String> channelsList = fbf.getCurrentUser().getChannels();
        for (int i = 0; i < channelsList.size(); i++){
            c.child(channelsList.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    channels.add(dataSnapshot.getValue(Channel.class));
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        ChannelAdaptor adaptor = new ChannelAdaptor(this.getContext(), channels);
    }

    private void SetupChannels() {
        Map<String, String> members = new HashMap<>();
        members.put(fbf.getCurrentUserUid(), "rwe");

        List<ChatMessage> mess = new ArrayList<>();
        channels.add(new Channel("1", "Test Channel 1", members, mess));
    }
}
