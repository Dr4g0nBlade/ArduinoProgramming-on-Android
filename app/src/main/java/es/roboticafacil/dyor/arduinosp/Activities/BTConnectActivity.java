package es.roboticafacil.dyor.arduinosp.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import es.roboticafacil.dyor.arduinosp.Adaptors.SearchBTAdaptor;
import es.roboticafacil.dyor.arduinosp.R;
import es.roboticafacil.dyor.arduinosp.Utils.UtilBluetoothService;

public class BTConnectActivity extends AppCompatActivity {

    private SharedPreferences bluetoothPrefs;
    private BluetoothAdapter mBluetoothAdaptor = BluetoothAdapter.getDefaultAdapter();
    private Gson gson = new Gson();

    private List<BluetoothDevice> mDevices = new ArrayList<>();
    private List<Boolean> inRange = new ArrayList<>();

    private SearchBTAdaptor adaptor;

    final BroadcastReceiver discover = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(UtilBluetoothService.SEND_DEVICE)){
                BluetoothDevice device = gson.fromJson(intent.getStringExtra("device"), BluetoothDevice.class);
                if (mDevices.contains(device)){
                    inRange.set(mDevices.lastIndexOf(device), true);
                } else {
                    mDevices.add(device);
                    inRange.add(mDevices.indexOf(device), true);
                }
            }
            adaptor.notifyDataSetChanged();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_to_arduino);

        bluetoothPrefs = getSharedPreferences("bluetooth", MODE_PRIVATE);

        IntentFilter intentFilter = new IntentFilter(UtilBluetoothService.CMD_SEARCH_DEVICES);
        registerReceiver(discover, intentFilter);

        ListView lvBTDevices = (ListView) findViewById(R.id.lv_bt_devices);
        lvBTDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = mDevices.get(position);
                if (mBluetoothAdaptor.getBondedDevices().contains(device)) {
                    Intent changeDefault = new Intent();
                    changeDefault.setAction(UtilBluetoothService.CMD_SET_DEFAULT_DEVICE);
                    changeDefault.putExtra("device", device);
                    Log.d("testingt", "Sending broadcast set default: "+device);
                    sendBroadcast(changeDefault);
                } else {
                    Intent pair = new Intent();
                    pair.setAction("pair_with");
                    sendBroadcast(pair);
                }
            }
        });

        mDevices.addAll(mBluetoothAdaptor.getBondedDevices());
        for (BluetoothDevice device : mDevices){
            inRange.add(mDevices.indexOf(device), false);
        }

        IntentFilter discoverFilter = new IntentFilter(UtilBluetoothService.SEND_DEVICE);
        registerReceiver(discover, discoverFilter);

        adaptor = new SearchBTAdaptor(getApplicationContext(), mDevices, bluetoothPrefs, inRange);
        lvBTDevices.setAdapter(adaptor);
    }


}
