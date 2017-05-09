package es.roboticafacil.dyor.tabbed.Activities.Tabs;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

import es.roboticafacil.dyor.tabbed.Models.Channel;
import es.roboticafacil.dyor.tabbed.R;
import es.roboticafacil.dyor.tabbed.Utils.FirebaseProfile;

/**
 * Created by Dragos Dunareanu on 06-Apr-17.
 */

public class SocialNo extends android.support.v4.app.Fragment {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    FirebaseProfile fbf;

    final List<Channel> channels = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.social, container, false);

        ListView channelList = (ListView) v.findViewById(R.id.lv_channels);
        TextView tvNoChannels = (TextView) v.findViewById(R.id.tv_no_channels);

        return v;
    }
}
