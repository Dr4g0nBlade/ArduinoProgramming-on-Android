package es.roboticafacil.dyor.arduinosp.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import es.roboticafacil.dyor.arduinosp.R;

public class SearchChannel extends AppCompatActivity implements View.OnClickListener {

    private FirebaseProfile fb;
    private ToggleButton tbChannels, tbUsers;
    private Query query;
    private boolean toSearch;
    private String keyword;
    private String type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_channel);

        fb = new FirebaseProfile();

        ImageButton ibBack = (ImageButton) findViewById(R.id.ib_back);
        EditText etSearchBar = (EditText) findViewById(R.id.et_search_keyword);
        tbChannels = (ToggleButton) findViewById(R.id.tb_search_channel);
        tbUsers = (ToggleButton) findViewById(R.id.tb_search_users);
        ListView lvResults = (ListView) findViewById(R.id.lv_search_result);

        type = "channels";

        tbChannels.setChecked(true);
        tbUsers.setChecked(false);

        tbChannels.setOnClickListener(this);
        tbUsers.setOnClickListener(this);

        etSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                toSearch = count >= 3;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (toSearch) {
                    keyword = s.toString();
                    searchChsnnel(keyword);
                }
            }
        });
    }

    private void searchChsnnel(String s) {

        fb.getChannels().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Channel c = postSnapshot.getValue(Channel.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tb_search_channel:
                tbUsers.setChecked(false);
                type = "channels";
                break;
            case R.id.tb_search_users:
                tbChannels.setChecked(false);
                type = "users";
                break;
        }
    }
}
