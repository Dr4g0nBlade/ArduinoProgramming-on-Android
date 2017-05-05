package es.roboticafacil.dyor.tabbed.Activities.Tabs;

//import android.app.Fragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
//import android.support.design.widget.FloatingActionButton;
//import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
//import java.util.HashMap;
import java.util.HashMap;
import java.util.List;

import es.roboticafacil.dyor.tabbed.Adaptors.adaptor;
import es.roboticafacil.dyor.tabbed.Models.Component;
import es.roboticafacil.dyor.tabbed.R;
//import java.util.Map;

/**
 * Created by Dragos Dunareanu on 22-Mar-17.
 */

public class ProfileSettings extends android.support.v4.app.Fragment{

    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    ExpandableListView elvBoards;
    HashMap<String, List<Component>> testList;
    List<String> groups;
    List<Component> comps;
    List<Component> comps2;
    List<Component> comps3;
    ExpandableListAdapter adaptor;

    public AlertDialog.Builder mChangeLoginDialog;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        final View v = inflater.inflate(R.layout.profile_settings, container, false);

        //FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        //fab.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                .setAction("Action", null).show();
        //    }
        //});

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                ImageView ivAvatar = (ImageView) v.findViewById(R.id.iv_avatar);
                EditText etNickname = (EditText)v.findViewById(R.id.et_nickname);
                TextView tvEmail = (TextView) v.findViewById(R.id.tv_email);
                TextView tvAccID = (TextView) v.findViewById(R.id.tv_id);
                Button btChangeEmail = (Button) v.findViewById(R.id.bt_change_login);
                if (user == null || user.isAnonymous()){
                    etNickname.setEnabled(false);
                    btChangeEmail.setEnabled(false);
                }
                else{
                    ivAvatar.setEnabled(true);
                    ivAvatar.setImageURI(user.getPhotoUrl());

                    etNickname.setEnabled(true);
                    etNickname.setText(user.getDisplayName());

                    tvEmail.setText(user.getEmail());
                    tvAccID.setText(user.getUid());

                    btChangeEmail.setEnabled(true);
                    btChangeEmail.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(ProfileSettings.this.getContext());
                            View mView = getActivity().getLayoutInflater().inflate(R.layout.dialog_change_login, null);

                            EditText etEmail = (EditText) mView.findViewById(R.id.et_change_email);
                            EditText etCurrPass = (EditText) mView.findViewById(R.id.et_change_pass_current);
                            EditText etNewPass1 = (EditText) mView.findViewById(R.id.et_change_pass_new1);
                            EditText etNewPass2 = (EditText) mView.findViewById(R.id.et_change_pass_new2);

                            mBuilder.setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            })
                                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    }).setView(mView);
                            AlertDialog dialog = mBuilder.create();
                            dialog.show();
                        }
                    });
                }
            }
        };


        initCompList(v);

        adaptor = new adaptor(this.getContext(), groups, testList);
        elvBoards.setAdapter(adaptor);

        return v;
    }



    private void initCompList(View v) {

        elvBoards = (ExpandableListView)v.findViewById(R.id.elv_boards);

        testList = new HashMap<>();
        groups = new ArrayList<>();
        comps = new ArrayList<>();
        comps2 = new ArrayList<>();
        comps3 = new ArrayList<>();

        Component comp1 = new Component();
        comp1.setName("Arduino Placa de Desarrollo M0");
        comp1.setType("Boards");

        Component comp2 = new Component();
        comp2.setName("Servo");
        comp2.setType("Interaction");

        Component comp3 = new Component();
        comp3.setName("WiFi Adaptor");
        comp3.setType("Communication");

        comps.add(comp1);
        comps2.add(comp2);
        comps3.add(comp3);

        groups.add("Boards");
        groups.add("Interaction");
        groups.add("Communication");

        testList.put(groups.get(0), comps);
        testList.put(groups.get(1), comps2);
        testList.put(groups.get(2), comps3);


    }

}

