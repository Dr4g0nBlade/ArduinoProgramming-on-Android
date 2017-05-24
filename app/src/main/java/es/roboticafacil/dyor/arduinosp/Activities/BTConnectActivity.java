package es.roboticafacil.dyor.arduinosp.Activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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


    Intent changeDefault = new Intent();
    Intent pair = new Intent();
    Intent search = new Intent();

    final BroadcastReceiver discover = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("UtilBluetoothService", "I got something");
            if (action.equals(UtilBluetoothService.SEND_DEVICE)){
                String deviceAddress = intent.getStringExtra("device_address");
                BluetoothDevice device = mBluetoothAdaptor.getRemoteDevice(deviceAddress);
                Log.d("UtilBluetoothService", "I found: " + deviceAddress);
                if (!mDevices.contains(device)){
                    mDevices.add(device);
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
        getApplicationContext().registerReceiver(discover, intentFilter);

        setupIntentCommands();

        ListView lvBTDevices = (ListView) findViewById(R.id.lv_bt_devices);
        lvBTDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = mDevices.get(position);
                if (mBluetoothAdaptor.getBondedDevices().contains(device)) {
                    changeDefault.putExtra("default_device_address", device.getAddress());
                    Log.d("UtilBluetoothService", "Sending broadcast set default: " + device);
                    getApplicationContext().sendBroadcast(changeDefault);
                } else {
                    pair.putExtra("device_address", device.getAddress());
                    getApplicationContext().sendBroadcast(pair);
                }
            }
        });

        mDevices.addAll(mBluetoothAdaptor.getBondedDevices());



        IntentFilter discoverFilter = new IntentFilter(UtilBluetoothService.SEND_DEVICE);
        getApplicationContext().registerReceiver(discover, discoverFilter);

        adaptor = new SearchBTAdaptor(getApplicationContext(), mDevices, bluetoothPrefs, inRange);
        lvBTDevices.setAdapter(adaptor);

        findViewById(R.id.bt_search_devices).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().sendBroadcast(search);
                Log.d("UtilBluetoothService", "Sending command to search devices");
            }
        });
    }

    private void setupIntentCommands() {
        changeDefault.setAction(UtilBluetoothService.CMD_SET_DEFAULT_DEVICE);
        pair.setAction(UtilBluetoothService.CMD_PAIR_WITH);
        search.setAction(UtilBluetoothService.CMD_SEARCH_DEVICES);
    }
}
